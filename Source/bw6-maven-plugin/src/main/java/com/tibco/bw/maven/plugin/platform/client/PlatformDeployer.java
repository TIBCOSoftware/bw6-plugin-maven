package com.tibco.bw.maven.plugin.platform.client;

import java.io.File;
import java.io.IOException;
import java.net.ConnectException;
import java.net.URI;
import java.util.Map;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.StatusType;
import javax.ws.rs.core.Response.Status.Family;

import org.apache.maven.plugin.logging.Log;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.media.multipart.file.FileDataBodyPart;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tibco.bw.maven.plugin.admin.client.ClientException;

public class PlatformDeployer {
	
	private final int connectTimeout;
	private final int readTimeout;
	private final int retryCount;
	private Log log;
	private WebTarget webTarget;
	
	public PlatformDeployer(int connectTimeout, int readTimeout, int retryCount, Log log) {
		this.connectTimeout = connectTimeout;
		this.readTimeout = readTimeout;
		this.retryCount = retryCount;
		this.log = log;
	}
	
	public void buildApp(String application, String earPath, String buildName, String appName, String profile, int replicas, boolean enableAutoScaling, boolean eula, String platformConfigFile, String dpUrl, String authToken, String baseVersion, String baseImageTag, String namespace) throws ClientException, IOException, InterruptedException {
		try {
			this.log.info("Deployment to Platform started...");
			if(dpUrl == null){
				this.log.debug("Unable to build the application. Please provide the data plane URL.");
				return;
			}
			if(authToken == null){
				this.log.debug("Unable to build the application. Please provide authorization token.");
				return;
			}
			if(baseVersion == null){
				this.log.debug("Unable to build the application. Please provide base version.");
				return;
			}
			if(baseImageTag == null){
				this.log.debug("Unable to build the application. Please provide base image tag.");
				return;
			}
			if(namespace == null){
				this.log.debug("Unable to build the application. Please provide namespace.");
				return;
			}

			File ear = new File(earPath);
			this.log.debug("EarLocation : " + earPath + ", EarFile : "+ ear.getName());

			Client client = ClientBuilder.newClient();
			webTarget = client.target(new URI(dpUrl));
			webTarget.register(MultiPartFeature.class);
			FormDataMultiPart multipart = new FormDataMultiPart();
			multipart.bodyPart(new FileDataBodyPart("artifact", ear));
			multipart.bodyPart(new FormDataBodyPart("request", "{\"buildName\": \"" + buildName + "\"}"));
			Response response = webTarget
					.queryParam("baseversion", baseVersion)
					.queryParam("baseimagetag", baseImageTag)
					.path("public/v1/dp/builds")
					.request(MediaType.MULTIPART_FORM_DATA)
					.accept(MediaType.APPLICATION_JSON)
					.header("Authorization", "Bearer " + authToken)
					.post(Entity.entity(multipart, MediaType.MULTIPART_FORM_DATA));
			StatusType statusInfo = response.getStatusInfo();
			if(statusInfo.getFamily().equals(Family.SUCCESSFUL)) {
				String readEntity = response.readEntity(String.class);
				ObjectMapper mapper = new ObjectMapper();
				mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
				Map<?, ?> responseMap;
				responseMap = mapper.readValue(readEntity, Map.class);
				String buildId = (String) responseMap.get("buildId");
				deployApp(buildId, namespace, authToken, eula, replicas, appName);
			}else {
				processErrorResponse(response, statusInfo);
			}
		}catch (ProcessingException pe) {
			pe.printStackTrace();
			throw getConnectionException(pe);
		}catch (Exception ex) {
			ex.printStackTrace();
			throw new ClientException(500, ex.getMessage(), ex);
		}
	}
	
	public void deployApp(String buildId, String namespace, String authToken, boolean eula, int replicas, String appName) throws ClientException, IOException, InterruptedException {
		if(buildId == null || buildId.isEmpty()){
			this.log.debug("Unable to deploy the application. Please provide a valid build ID.");
			return;
		}
		if(namespace == null || namespace.isEmpty()){
			this.log.debug("Unable to deploy the application. Please provide a valid namespace.");
			return;
		}
		if(!eula) {
			this.log.debug("Unable to deploy the application. Please accept the EULA.");
			return;
		}
		
		Response response = webTarget
				.queryParam("namespace", namespace)
				.path("public/v1/dp/deploy")
				.request(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + authToken)
				.header("Content-Type", "application/json")
				.post(Entity.entity("{\"buildId\": \"" + buildId + "\"," + "\"eula\": " + eula + "," + "\"appName\": \"" + appName + "\"" + "}", MediaType.APPLICATION_JSON));
		StatusType statusInfo = response.getStatusInfo();
		if(statusInfo.getFamily().equals(Family.SUCCESSFUL)) {
			String readEntity = response.readEntity(String.class);
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
			Map<?, ?> responseMap;
			responseMap = mapper.readValue(readEntity, Map.class);
			String appId = (String) responseMap.get("appId");
			scaleApp(appId, replicas, authToken);
		}else {
			processErrorResponse(response, statusInfo);
		}
	}
	
	public void scaleApp(String appId, int replicas, String authToken) throws ClientException, IOException, InterruptedException {
		if(appId == null || appId.isEmpty()){
			this.log.debug("Unable to scale the application. Please provide app ID.");
		}
		if(replicas <= 0) {
			this.log.debug("Unable to scale the application. Please provide a valid number of replicas.");
		}
		Response response = webTarget
				.queryParam("count", Integer.toString(replicas))
				.path("public/v1/dp/apps")
				.path(appId)
				.path("scale")
				.request(MediaType.TEXT_PLAIN)
				.header("Authorization", "Bearer " + authToken)
				.put(Entity.entity("", MediaType.TEXT_PLAIN));
		StatusType statusInfo = response.getStatusInfo();
		if(!statusInfo.getFamily().equals(Family.SUCCESSFUL)) {
			processErrorResponse(response, statusInfo);
		}
	}
	
	private void processErrorResponse(Response response, StatusType statusInfo) throws ClientException {
		if (statusInfo.getStatusCode() == 401) {
			throw new ClientException(response.getStatus(), statusInfo.getStatusCode() + ": " + statusInfo.getReasonPhrase(), null);
		}
		String error = response.readEntity(String.class);
		if (error != null) {
			String errCode = null;
			String errMsg = null;
			String errDetail = null;
			try {
				ObjectMapper mapper = new ObjectMapper();
				mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
				Map<?, ?> responseMap;
				responseMap = mapper.readValue(error, Map.class);
				errCode = (String) responseMap.get("errCode");
				errMsg = (String) responseMap.get("errMsg");
				errDetail = (String) responseMap.get("errDetail");
			} catch (JsonMappingException e) {
				e.printStackTrace();
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
			throw new ClientException(response.getStatus(), "Error code: " + errCode + "\nError message: " + errMsg + "\nError details: " + errDetail, null);
		} else {
			throw new ClientException(response.getStatus(), statusInfo.getReasonPhrase(), null);
		}
	}
	
	private static ClientException getConnectionException(ProcessingException pe) {
		if(pe.getCause() instanceof ConnectException) {
			return new ClientException(503, pe.getCause().getMessage(), pe.getCause());
		}
		if(pe.getCause() instanceof IllegalStateException) {
			return new ClientException(503, pe.getCause().getMessage(), pe.getCause());
		}
		return new ClientException(500, pe.getMessage(), pe);
	}

}

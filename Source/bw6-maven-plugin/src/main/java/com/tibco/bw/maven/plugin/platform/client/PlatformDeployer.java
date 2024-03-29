package com.tibco.bw.maven.plugin.platform.client;

import java.io.File;
import java.io.IOException;
import java.net.ConnectException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
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
import org.json.JSONArray;
import org.json.JSONObject;

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
			if(dpUrl == null) {
				throw new ClientException("Unable to build the application. Please provide the data plane URL.");
			}
			if(authToken == null) {
				throw new ClientException("Unable to build the application. Please provide authorization token.");
			}
			if(baseVersion == null) {
				throw new ClientException("Unable to build the application. Please provide base version.");
			}
			if(baseImageTag == null) {
				throw new ClientException("Unable to build the application. Please provide base image tag.");
			}
			if(namespace == null) {
				throw new ClientException("Unable to build the application. Please provide namespace.");
			}

			File ear = new File(earPath);
			this.log.debug("EarLocation : " + earPath + ", EarFile : "+ ear.getName());

			Client client = ClientBuilder.newClient();
			webTarget = client.target(new URI(dpUrl));
			webTarget.register(MultiPartFeature.class);
			
			String platformConfigFileContent = new String(Files.readAllBytes(Paths.get(platformConfigFile)));
			JSONArray dependenciesArray = null;
			if(platformConfigFileContent != null && !platformConfigFileContent.isEmpty()) {
				JSONObject rootObject = new JSONObject(platformConfigFileContent);
				if(rootObject != null && rootObject.has("platformConfig")) {
					JSONArray platformConfigObject = (JSONArray) rootObject.get("platformConfig");
					if(platformConfigObject != null) {
						if(platformConfigObject.length() > 2) {
							JSONObject dependencies = (JSONObject) platformConfigObject.get(2);
							if(dependencies != null) {
								dependenciesArray = (JSONArray) dependencies.get("dependencies");
							}
						}
					}
				}
			}
			
			FormDataMultiPart multipart = new FormDataMultiPart();
			multipart.bodyPart(new FileDataBodyPart("artifact", ear));
			multipart.bodyPart(new FormDataBodyPart("request", "{\"buildName\": \"" + buildName + "\", " + "\"dependencies\": " + dependenciesArray.toString() + "}"));
			
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
				deployApp(buildId, namespace, authToken, eula, replicas, appName, profile, platformConfigFile);
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
	
	public void deployApp(String buildId, String namespace, String authToken, boolean eula, int replicas, String appName, String profile, String platformConfigFile) throws ClientException, IOException, InterruptedException {
		if(buildId == null || buildId.isEmpty()) {
			throw new ClientException("Unable to deploy the application. Please provide a valid build ID.");
		}
		if(namespace == null || namespace.isEmpty()) {
			throw new ClientException("Unable to deploy the application. Please provide a valid namespace.");
		}
		if(!eula) {
			throw new ClientException("Unable to deploy the application. Please accept the EULA.");
		}
		
		JSONObject appJsonObject = new JSONObject();
		String platformConfigFileContent = new String(Files.readAllBytes(Paths.get(platformConfigFile)));
		if(platformConfigFileContent != null && !platformConfigFileContent.isEmpty()) {
			JSONObject rootObject = new JSONObject(platformConfigFileContent);
			if(rootObject != null && rootObject.has("platformConfig")) {
				JSONArray platformConfigObject = (JSONArray) rootObject.get("platformConfig");
				if(platformConfigObject != null) {
					JSONArray appPropertiesArray = null;
					JSONArray systemPropertiesArray = null;
					JSONObject resourceLimitsArray = null;
					if(platformConfigObject.length() > 0) {
						JSONObject appProperties = (JSONObject) platformConfigObject.get(0);
						if(appProperties != null) {
							appPropertiesArray = (JSONArray) appProperties.get("appProperties");
						}
					}
					if(platformConfigObject.length() > 1) {
						JSONObject systemProperties = (JSONObject) platformConfigObject.get(1);
						if(systemProperties != null) {
							systemPropertiesArray = (JSONArray) systemProperties.get("systemProperties");
						}
					}
					if(platformConfigObject.length() > 3) {
						JSONObject resourceLimits = (JSONObject) platformConfigObject.get(3);
						if(resourceLimits != null) {
							resourceLimitsArray = (JSONObject) resourceLimits.get("resourceLimits");
						}
					}
					appJsonObject.put("buildId", buildId);
					appJsonObject.put("eula", eula);
					appJsonObject.put("appName", appName);
					appJsonObject.put("profile", profile);
					if(appPropertiesArray != null) {
						appJsonObject.put("appProperties", appPropertiesArray);
					}
					if(systemPropertiesArray != null) {
						appJsonObject.put("systemProperties", systemPropertiesArray);
					}
					if(resourceLimitsArray != null) {
						appJsonObject.put("resourceLimits", resourceLimitsArray);
					}
				}
			}
		}
		
		Response response = webTarget
				.queryParam("namespace", namespace)
				.path("public/v1/dp/deploy")
				.request(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + authToken)
				.header("Content-Type", "application/json")
				.post(Entity.entity(appJsonObject.toString(), MediaType.APPLICATION_JSON));
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
		if(appId == null || appId.isEmpty()) {
			throw new ClientException("Unable to scale the application. Please provide app ID.");
		}
		if(replicas <= 0) {
			throw new ClientException("Unable to scale the application. Please provide a valid number of replicas.");
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

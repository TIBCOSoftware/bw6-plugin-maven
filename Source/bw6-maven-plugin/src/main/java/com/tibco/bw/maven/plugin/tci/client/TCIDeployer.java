package com.tibco.bw.maven.plugin.tci.client;

import java.io.File;
import java.io.IOException;
import java.net.ConnectException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Feature;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status.Family;
import javax.ws.rs.core.UriBuilder;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.logging.Log;
import org.glassfish.jersey.SslConfigurator;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.client.oauth2.OAuth2ClientSupport;
import org.glassfish.jersey.filter.LoggingFilter;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.media.multipart.file.FileDataBodyPart;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tibco.bw.maven.plugin.admin.client.ClientException;
import com.tibco.bw.maven.plugin.tci.dto.TCIAppId;
import com.tibco.bw.maven.plugin.tci.dto.TCIAppStatus;
import com.tibco.bw.maven.plugin.tci.dto.TCIError;
import com.tibco.bw.maven.plugin.tci.dto.TCIOrganization;
import com.tibco.bw.maven.plugin.tci.dto.TCIProperty;
import com.tibco.bw.maven.plugin.tci.dto.TCIUserInfo;
import com.tibco.bw.maven.plugin.tci.dto.TCIVariables;
import com.tibco.bw.maven.plugin.utils.Constants;

public class TCIDeployer {

	private String serverEndpoint = Constants.TCI_SERVER;
	private String oAuthAccessToken;
	private String contextRoot = Constants.TCI_CONTEXT_ROOT;
	private Client jerseyClient;
	private WebTarget r;
	private final int connectTimeout;
	private final int readTimeout;
	private final int retryCount;
	private Log log;
	private int SLEEP_INTERVAL = 1000;

	/*
	 * Initialize TCI deployer, prepare config using ENV variables.
	 * TCI_PLATFORM_API_ENDPOINT = The Endpoint to the TCI Server Platform
	 * API's. TCI_PLATFORM_API_ACCESS_TOKEN = The OAuth Access token for
	 * accessing TCI Pltform API. TCI_PLATFORM_SUBSCRIPTION_LOCATOR = The TCI
	 * Subscription Locator, defaults to current org if not specified.
	 */

	public TCIDeployer(int connectTimeout, int readTimeout, int retryCount,
			Log log) throws Exception {
		this.connectTimeout = connectTimeout;
		this.readTimeout = readTimeout;
		this.retryCount = retryCount;
		this.log = log;

		if (System.getenv(Constants.TCI_SERVER_ENDPOINT_ENV) != null
				&& System.getenv(Constants.TCI_SERVER_ENDPOINT_ENV).trim()
						.length() > 0)
			this.serverEndpoint = System
					.getenv(Constants.TCI_SERVER_ENDPOINT_ENV);

		if (System.getenv(Constants.TCI_ACCESS_TOKEN_ENV) != null
				&& System.getenv(Constants.TCI_ACCESS_TOKEN_ENV).trim()
						.length() > 0) {
			this.oAuthAccessToken = System
					.getenv(Constants.TCI_ACCESS_TOKEN_ENV);
		} else {
			throw new Exception(
					"TCI Access token is missing. Please set environment variable TCI_PLATFORM_API_ACCESS_TOKEN");
		}

		//init();
		
		if (System.getenv(Constants.TCI_SUBSCRIPTION_LOCATOR_ENV) != null
				&& System.getenv(Constants.TCI_SUBSCRIPTION_LOCATOR_ENV).trim()
						.length() > 0) {
			this.contextRoot = this.contextRoot + "subscriptions/"
					+ System.getenv(Constants.TCI_SUBSCRIPTION_LOCATOR_ENV)
					+ "/apps";
			this.log.info("Using Subscription Locator -> "
					+ System.getenv(Constants.TCI_SUBSCRIPTION_LOCATOR_ENV));
		} else {
			//String subscriptionLocator = getCurrentOrgSubscriptionLocator();
			String subscriptionLocator = "0";
			this.contextRoot = this.contextRoot + "subscriptions/" + subscriptionLocator + "/apps";
			this.log.info("The environment variable TCI_PLATFORM_SUBSCRIPTION_LOCATOR is not set. Using Subscription Locator -> "+ subscriptionLocator);
		}
		
		init();
	}

	/*
	 * Initialize Jersey Client
	 */

	private void init() {
		ClientConfig clientConfig = new ClientConfig();
		clientConfig = clientConfig.property(ClientProperties.CONNECT_TIMEOUT,
				connectTimeout);
		clientConfig = clientConfig.property(ClientProperties.READ_TIMEOUT,
				readTimeout);
		clientConfig.register(JacksonFeature.class).register(
				MultiPartFeature.class);

		if (log.isDebugEnabled()) {
			Logger logger = Logger.getLogger(getClass().getName());
			clientConfig.register(new LoggingFilter(logger, true));
		}

		SslConfigurator sslConfig = SslConfigurator.newInstance();
		this.jerseyClient = ClientBuilder.newBuilder().withConfig(clientConfig)
				.sslContext(sslConfig.createSSLContext())
				.hostnameVerifier(new HostnameVerifier() {
					@Override
					public boolean verify(String hostname, SSLSession session) {
						return true;
					}
				}).build();
		Feature feature = OAuth2ClientSupport.feature(this.oAuthAccessToken);
		this.jerseyClient.register(feature);
		this.r = this.jerseyClient.target(UriBuilder.fromPath(this.contextRoot)
				.scheme("https").host(this.serverEndpoint).port(443).build());
	}

	/*
	 * Close Client and cleanup
	 */

	public void close() {
		if (this.jerseyClient != null) {
			this.jerseyClient.close();
			this.jerseyClient = null;
		}
	}
	
	/*
	 * Get User info to find current org subscription locator
	 */
	
	private String getCurrentOrgSubscriptionLocator() throws ClientException 
	{
		String subscriptionLocator = null;
		
		//get user info
		Response response = r
				.path("userinfo")
				.request(MediaType.APPLICATION_JSON_TYPE)
				.get();
		processErrorResponse(response);
		TCIUserInfo tciUserInfo = response.readEntity(TCIUserInfo.class);
		for (TCIOrganization org : tciUserInfo.getOrganizations()) {
			if(org.isCurrentOrg()){
				subscriptionLocator = org.getSubscriptionLocator();
				break;
			}
		}
		log.info("Current User org subscription locator : "+ subscriptionLocator);
		
		return subscriptionLocator;
	}

	/*
	 * Deploy App on TCI
	 */
	public void deployApp(String appName, String earPath, int instances,
			String appVariablesFile, String engineVariablesFile,
			boolean forceOverwrite, boolean retainAppProps)
			throws ClientException, IOException, InterruptedException {

		// if set variables
		if (((appVariablesFile != null && appVariablesFile.trim().length() > 0)
				|| (engineVariablesFile != null && engineVariablesFile.trim()
						.length() > 0)) && !retainAppProps) {
			// push app
			this.log.info("Pushing app to TCI, AppName -> " + appName);
			String appId = pushApp(appName, earPath, 0, forceOverwrite,
					retainAppProps);
			this.log.info("App Push Successful, AppId -> " + appId);

			checkAppStatus(appId);

			if (appVariablesFile != null
					&& appVariablesFile.trim().length() > 0) {
				// set app variables
				this.log.info("Setting App Variables from file -> "
						+ appVariablesFile);
				setAppVariables(appId, appVariablesFile);
			}

			if (engineVariablesFile != null
					&& engineVariablesFile.trim().length() > 0) {
				// set engine variables
				this.log.info("Setting Engine Variables from file -> "
						+ engineVariablesFile);
				setEngineVariables(appId, engineVariablesFile);
			}

			// scale app
			this.log.info("Scaling app, instance count -> " + instances);
			scaleApp(appId, instances);
		} else {
			// push app
			this.log.info("Pushing app to TCI, AppName -> " + appName);
			String appId = pushApp(appName, earPath, instances, forceOverwrite,
					retainAppProps);
			this.log.info("App Push Successful, AppId -> " + appId);
		}

	}

	/*
	 * Check app status after pushing an App on TCI. the app remains locked till
	 * it finishes its deployment
	 */

	private void checkAppStatus(String appId) throws ClientException,
			InterruptedException {
		String appStatus = Constants.TCI_APP_STATUS_UPDATING;
		int count = 0;
		while ((Constants.TCI_APP_STATUS_UPDATING.equalsIgnoreCase(appStatus) || Constants.TCI_APP_STATUS_BUILDING
				.equalsIgnoreCase(appStatus) || Constants.TCI_APP_STATUS_SCALING.equalsIgnoreCase(appStatus)) 
				&& count < this.retryCount) {
			Thread.sleep(SLEEP_INTERVAL);
			count++;
			Response response = r.path(appId).path("status")
					.request(MediaType.APPLICATION_JSON_TYPE).get();
			processErrorResponse(response);
			TCIAppStatus tciAppStatus = response.readEntity(TCIAppStatus.class);
			log.info("App Status -> AppId : " + appId + ", Status : "
					+ tciAppStatus.getStatus());
			appStatus = tciAppStatus.getStatus();
		}

	}

	private boolean isValidURL(String url) {
		try {
			new URL(url).toURI();
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/*
	 * Set app variables from app var json.
	 */

	private void setAppVariables(String appId, String variablesFile)
			throws IOException, ClientException {
		// /v1/apps/{id}/env/variables
		if (variablesFile.contains("http") && isValidURL(variablesFile)) {
			String localFileName = variablesFile.substring(variablesFile
					.lastIndexOf("/") + 1);
			log.info("App variable file is from external URL, creating temporary local file - "
					+ localFileName);
			File file = new File(localFileName);
			file.delete();
			file.createNewFile();
			FileUtils.copyURLToFile(new URL(variablesFile), file);
			variablesFile = file.getAbsolutePath();
		}

		String variablesFileContent = new String(Files.readAllBytes(Paths
				.get(variablesFile)));
		if(null != variablesFileContent && !variablesFileContent.isEmpty() && !variablesFileContent.equals("\n[\n]")  ) { 
		// addQueryParam("type", type);
		Response response = r
				.queryParam("variableType", "app")
				.path(appId)
				.path("env")
				.path("variables")
				.request(MediaType.APPLICATION_JSON_TYPE)
				.put(Entity.entity(variablesFileContent,
						MediaType.APPLICATION_JSON));
		processErrorResponse(response);
		log.info("Successfully updated application variables for app : "
				+ appId);
		}
		else {
			log.info("Application Variable not found in" + variablesFile +  "for app : "
					+ appId);
			}
		}

	

	/*
	 * Set engine variables from engine var json.
	 */

	private void setEngineVariables(String appId, String variablesFile)
			throws IOException, ClientException 
	{
		// /v1/apps/{id}/env/variables
		if (variablesFile.contains("http") && isValidURL(variablesFile)) {
			String localFileName = variablesFile.substring(variablesFile
					.lastIndexOf("/") + 1);
			log.info("Engine variable file is from external URL, creating temporary local file - "
					+ localFileName);
			File file = new File(localFileName);
			file.delete();
			file.createNewFile();
			FileUtils.copyURLToFile(new URL(variablesFile), file);
			variablesFile = file.getAbsolutePath();
		}

		String variablesFileContent = new String(Files.readAllBytes(Paths
				.get(variablesFile)));

		// extract user variables.
		ObjectMapper objectMapper = new ObjectMapper();
		List<TCIProperty> engineProps = objectMapper.readValue(
				variablesFileContent, new TypeReference<List<TCIProperty>>() {
				});
		
		
		//fetch existing user engine vars
		Response resp = r
				.queryParam("variableType", "user")
				.path(appId)
				.path("env")
				.path("variables")
				.request(MediaType.APPLICATION_JSON_TYPE)
				.get();
		processErrorResponse(resp);
		TCIVariables existingUserEngineProps = resp.readEntity(TCIVariables.class);
		log.info("Fetched existing user engine variables for app : " + appId);
		

		List<TCIProperty> updateEnginePropsList = new ArrayList<TCIProperty>();
		List<TCIProperty> newUserEnginePropsList = new ArrayList<TCIProperty>();
		List<TCIProperty> updateUserEnginePropsList = new ArrayList<TCIProperty>();
		
		for (TCIProperty tciProperty : engineProps) {
			if(Constants.TCI_DEFAULT_ENGINE_PROPS.contains(tciProperty.getName())){
				updateEnginePropsList.add(tciProperty);
			} else {
				boolean found = false;
				if(existingUserEngineProps.getUserVariables() != null){
					for (TCIProperty tciProp : existingUserEngineProps.getUserVariables()) {
						if(tciProp.getName().equalsIgnoreCase(tciProperty.getName())){
							found = true;
							break;
						}
					}
				}
				if(found){
					updateUserEnginePropsList.add(tciProperty);
				} else {
					newUserEnginePropsList.add(tciProperty);
				}
			}
		}
		
		if(!updateEnginePropsList.isEmpty())
		{
			String updateEnginePropsListStr = objectMapper.writeValueAsString(updateEnginePropsList);
			
			log.debug("Updating engine variables -> "+ updateEnginePropsListStr);
			// update values for existing engine variables.
	
			Response response = r
					.queryParam("variableType", "engine")
					.path(appId)
					.path("env")
					.path("variables")
					.request(MediaType.APPLICATION_JSON_TYPE)
					.put(Entity.entity(updateEnginePropsListStr,
							MediaType.APPLICATION_JSON));
			processErrorResponse(response);
			log.info("Successfully updated engine variables for app : " + appId);
		}
		
		if(!newUserEnginePropsList.isEmpty())
		{
			String newEnginePropsListsStr = objectMapper.writeValueAsString(newUserEnginePropsList);
			
			log.debug("Adding user engine variables -> "+ newEnginePropsListsStr);
			// add user engine variables.
	
			Response response = r
					.path(appId)
					.path("env")
					.path("variables")
					.request(MediaType.APPLICATION_JSON_TYPE)
					.post(Entity.entity(newEnginePropsListsStr,
							MediaType.APPLICATION_JSON));
			processErrorResponse(response);
			log.info("Successfully added user engine variables for app : " + appId);
		}
		
		if(!updateUserEnginePropsList.isEmpty())
		{
			String updateUserEnginePropsListStr = objectMapper.writeValueAsString(updateUserEnginePropsList);
			
			log.debug("Updating user engine variables -> "+ updateUserEnginePropsListStr);
			// update values for existing engine variables.
	
			Response response = r
					.queryParam("variableType", "user")
					.path(appId)
					.path("env")
					.path("variables")
					.request(MediaType.APPLICATION_JSON_TYPE)
					.put(Entity.entity(updateUserEnginePropsListStr,
							MediaType.APPLICATION_JSON));
			processErrorResponse(response);
			log.info("Successfully updated user engine variables for app : " + appId);
		}
	}

	/*
	 * Push an app to TCI
	 */

	private String pushApp(String appName, String earPath, int instanceCount,
			boolean forceOverwrite, boolean retainAppProps)
			throws ClientException {
		log.info("Pushing app on TCI : " + appName);

		try {
			FormDataMultiPart multipart = new FormDataMultiPart();
			File earFileEntity = new File(earPath);
			multipart.bodyPart(new FileDataBodyPart("artifact", earFileEntity));

			// addQueryParam("appName", appName);
			// addQueryParam("instanceCount", "0");
			Response response = r
					.queryParam("appName", appName)
					.queryParam("instanceCount", instanceCount)
					.queryParam("forceOverwrite", forceOverwrite)
					.queryParam("retainAppProps", retainAppProps)
					.request(MediaType.MULTIPART_FORM_DATA)
					.accept(MediaType.APPLICATION_JSON)
					.post(Entity.entity(multipart,
							MediaType.MULTIPART_FORM_DATA));
			if (response.getStatusInfo().getFamily().equals(Family.SUCCESSFUL)) {
				TCIAppId tciAppId = response.readEntity(TCIAppId.class);
				return tciAppId.getAppId();
			} else {
				processErrorResponse(response);
			}
			return null;
		} catch (ProcessingException pe) {
			pe.printStackTrace();
			throw getConnectionException(pe);
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new ClientException(500, ex.getMessage(), ex);
		}
	}

	/*
	 * Scale the app with specified number of instances.
	 */

	public void scaleApp(String appId, int instances) throws ClientException {
		if (instances > 0) {
			log.info("Scaling app : " + appId);
			// addQueryParam("instanceCount", Integer.toString(instances));
			Response response = r
					.queryParam("instanceCount", Integer.toString(instances))
					.path(appId).path("scale")
					.request(MediaType.APPLICATION_JSON_TYPE).post(null);
			processErrorResponse(response);
			log.info("Successfully submitted scale app request : " + appId + " to "
					+ instances + " instances.");
		}
	}

	/*
	 * Add query parameters to the Jersey client request
	 */

	private void addQueryParam(final String name, final String value) {
		if (value != null) {
			r = r.queryParam(name, value);
		}
	}

	/*
	 * Process Jersey client error response.
	 */

	private void processErrorResponse(Response response) throws ClientException {
		if (!Family.SUCCESSFUL.equals(response.getStatusInfo().getFamily())) {
			if (response.getStatusInfo().getStatusCode() == 401) {
				throw new ClientException(response.getStatus(), response
						.getStatusInfo().getStatusCode()
						+ ": "
						+ response.getStatusInfo().getReasonPhrase(), null);
			}
			TCIError error = response.readEntity(TCIError.class);
			if (error != null) {
				throw new ClientException(response.getStatus(),
						error.getError() + ": " + error.getErrorDetail(), null);
			} else {
				throw new ClientException(response.getStatus(), response
						.getStatusInfo().getReasonPhrase(), null);
			}
		}
	}

	/*
	 * Handle Connection exception.
	 */

	private static ClientException getConnectionException(ProcessingException pe) {
		if (pe.getCause() instanceof ConnectException) {
			return new ClientException(503, pe.getCause().getMessage(),
					pe.getCause());
		}
		// https://java.net/jira/browse/JERSEY-2728
		if (pe.getCause() instanceof IllegalStateException) {
			return new ClientException(503, pe.getCause().getMessage(),
					pe.getCause());
		}
		return new ClientException(500, pe.getMessage(), pe);
	}

}

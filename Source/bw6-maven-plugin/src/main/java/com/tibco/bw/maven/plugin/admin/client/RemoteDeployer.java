package com.tibco.bw.maven.plugin.admin.client;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.URI;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status.Family;
import javax.ws.rs.core.UriBuilder;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.logging.Log;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.media.multipart.file.FileDataBodyPart;

import com.tibco.bw.maven.plugin.admin.dto.Agent;
import com.tibco.bw.maven.plugin.admin.dto.AppNode;
import com.tibco.bw.maven.plugin.admin.dto.AppSpace;
import com.tibco.bw.maven.plugin.admin.dto.Application;
import com.tibco.bw.maven.plugin.admin.dto.Archive;
import com.tibco.bw.maven.plugin.admin.dto.Domain;

public class RemoteDeployer {
	private static final String DATE_TIME = new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date());
	private static final String CONTEXT_ROOT = "/bw/v1";
	private Client jerseyClient;
	private final String host;
	private final int port;
	private Log log;
	private String user;
	private String pass;

	private void init() {
		if (this.jerseyClient == null) {
			ClientConfig clientConfig = new ClientConfig();
			clientConfig.register(JacksonFeature.class).register(MultiPartFeature.class);
			this.jerseyClient = ClientBuilder.newClient(clientConfig);
		}

		if (user.length()>0)
		{
			 HttpAuthenticationFeature feature = HttpAuthenticationFeature.universalBuilder()
					  .credentialsForBasic(user, pass)
				      .credentials(user, pass)
				      .build();
			 this.jerseyClient.register(feature);
		}
	}

	public RemoteDeployer(final String host, final String port) {
		if (host == null) {
			throw new IllegalArgumentException("Host must not be null");
		}
		int p = Integer.parseInt(port);
		if (p <= 0 | p > 65535) {
			throw new IllegalArgumentException("Invalid port number");
		}
		this.host = host;
		this.port = p;
	}

	public void setLog(Log log) {
		this.log = log;
	}

	public void close() {
		if (this.jerseyClient != null) {
			this.jerseyClient.close();
			this.jerseyClient = null;
		}
	}

	
	public RemoteDeployer(final String host, final String port,String user, String pass) {
		if (host == null) {
			throw new IllegalArgumentException("Host must not be null");
		}
		int p = Integer.parseInt(port);
		if (p <= 0 | p > 65535) {
			throw new IllegalArgumentException("Invalid port number");
		}
		this.host = host;
		this.port = p;
		this.user = user;
		this.pass = pass;
	}
	
	public List<Agent> getAgentInfo() throws ClientException {
		init();
		URI u = UriBuilder.fromPath(CONTEXT_ROOT).scheme("http").host(this.host).port(this.port).build();
		WebTarget r = this.jerseyClient.target(u);

		try {
			Response response = r.path("/agents").path("info").request(MediaType.APPLICATION_JSON_TYPE).get();
			processErrorResponse(response);
			List<Agent> info = response.readEntity(new GenericType<List<Agent>>() {});
			return info;
		} catch (ProcessingException pe) {
			throw getConnectionException(pe);
		} catch (Exception ex) {
			throw new ClientException(500, ex.getMessage(), ex);
		}
	}

	public Domain getOrCreateDomain(final String name, final String desc) throws ClientException {
		List <Domain> domains = getDomains(null, false, true);
		for(Domain domain : domains) {
			if(domain.getName().equals(name)) {
				log.info("Domain exists with Name -> " + name);
				return domain;
			}
		}
		log.info("Creating Domain with name -> " + name);
		return createDomain(name, desc, "owner", null, null);
	}

	private Domain createDomain(final String name, final String description, final String owner, final String agent, final String home) throws ClientException {
		init();
		URI u = UriBuilder.fromPath(CONTEXT_ROOT).scheme("http").host(this.host).port(this.port).build();
		WebTarget r = this.jerseyClient.target(u);
		try {
			if (description != null) {
				r = r.queryParam("desc", description);
			}
			if (agent != null) {
				r = r.queryParam("agent", agent);
			}
			if (owner != null) {
				r = r.queryParam("owner", owner);
			}
			if (home != null) {
				r = r.queryParam("home", home);
			}

			Response response = r.path("/domains").path(name).request(MediaType.APPLICATION_JSON_TYPE).post(null);
			processErrorResponse(response);
			Domain domain = response.readEntity(Domain.class);
			return domain;
		} catch (ProcessingException pe) {
			throw getConnectionException(pe);
		} catch (Exception ex) {
			throw new ClientException(500, ex.getMessage(), ex);
		}
	}

	private List<Domain> getDomains(final String filter, final boolean full, final boolean status) throws ClientException {
		init();
		URI u = UriBuilder.fromPath(CONTEXT_ROOT).scheme("http").host(this.host).port(this.port).build();
		WebTarget r = this.jerseyClient.target(u);
		try {
			r = r.queryParam("full", full).queryParam("status", status);
			if (filter != null) {
				r = r.queryParam("filter", filter);
			}
			Response response = r.path("/browse").path("domains").request(MediaType.APPLICATION_JSON_TYPE).get();
			processErrorResponse(response);
			List<Domain> domains = response.readEntity(new GenericType<List<Domain>>() {});
			return domains;
		} catch (ProcessingException pe) {
			throw getConnectionException(pe);
		} catch (Exception ex) {
			throw new ClientException(500, ex.getMessage(), ex);
		}
	}

	public AppSpace getOrCreateAppSpace(final String domainName, final String appSpaceName, final String desc) throws ClientException {
		List<AppSpace> appSpaces = getAppSpaces(domainName, null, false, true);
		for(AppSpace appSpace : appSpaces) {
			if(appSpace.getName().equals(appSpaceName)) {
				log.info("AppSpace exists with Name -> " +  appSpaceName + " in Domain -> " + domainName);
				return appSpace;
			}
		}
		log.info("Creating AppSpace with Name -> " +  appSpaceName + " in Domain -> "  + domainName);
		return createAppSpace(domainName, appSpaceName, true, 0, null, desc, "owner");
	}

	public AppNode getOrCreateAppNode(final String domainName, final String appSpaceName, final String appNodeName, final int httpPort, final int osgiPort, final String description) throws ClientException {		
		List<AppNode> nodes = getAppNodes(domainName, appSpaceName, null, true);
		for(AppNode node : nodes) {
			if(node.getName().equals(appNodeName)) {
				log.info("AppNode exists with Name -> " +  appNodeName + " in Domain -> " + domainName  + " and in AppSpace -> " + appSpaceName);
				log.info("AppNode HTTP Port  -> " +  httpPort + ". AppNode OSGi Port -> " + osgiPort);
				return node;
			}
		}
		log.info("Creating AppNode with Name -> " +  appNodeName + " in Domain -> " + domainName  + " and in AppSpace -> " + appSpaceName);
		return createAppNode(domainName, appSpaceName, appNodeName, null, httpPort, osgiPort, description);
	}

	public void addAndDeployApplication(final String domainName, final String appSpaceName, final String appName, final String earName, final String file, final boolean replace, final String profile, final boolean backupEar, final String backupLocation) throws ClientException {
		List<Application> applications = getApplications(domainName, appSpaceName, null, true);
		for(Application application : applications) {
			if(application.getName().equals(appName)) {
				if(replace) {
					// Backup ear and profile
					if(backupEar) {
						log.info("Generating backup ear file for application -> " + appName);
						downloadArchive(domainName, backupLocation, application.getArchiveName().toString());
						log.info("Generating backup substvar file for profile -> " + application.getProfileName());
						downloadProfileAplication(domainName, backupLocation, application.getArchiveName().toString(), application.getProfileName());
					}
					log.info("Application exists with name -> " + appName + ". Undeploying the Application as Redeploy flag is true.");
					undeployApplication(domainName, appSpaceName, appName, application.getVersion());	
				} else {
					log.info("Application exists with name -> " + appName + ". Not Re-deploying the Application as Redeploy flag is false.");
					return;
				}
			}
		}
		log.info("Uploading the Archive file -> " + earName);
		uploadArchive(domainName, null, file, true);
		log.info("Deploying the Application with name -> " + appName + " with Profile -> " + profile);
		deployApplication(domainName, appSpaceName, earName, null, true, replace, profile);
	}

	private List<AppSpace> getAppSpaces(final String domainName, final String filter, final boolean full, final boolean status) throws ClientException {
		init();
		URI u = UriBuilder.fromPath(CONTEXT_ROOT).scheme("http").host(this.host).port(this.port).build();
		WebTarget r = this.jerseyClient.target(u);
		try {
			r = r.queryParam("domain", domainName).queryParam("full", full).queryParam("status", status);
			if (filter != null) {
				r = r.queryParam("filter", filter);
			}
			Response response = r.path("/browse").path("appspaces").request(MediaType.APPLICATION_JSON_TYPE).get();
			processErrorResponse(response);
			List<AppSpace> appSpaces = response.readEntity(new GenericType<List<AppSpace>>() {});
			return appSpaces;
		} catch (ProcessingException pe) {
			throw getConnectionException(pe);
		} catch (Exception ex) {
			throw new ClientException(500, ex.getMessage(), ex);
		}
	}

	private AppSpace createAppSpace(final String domainName, final String appSpaceName, final boolean elastic, final int minNodes, final String version, final String description, final String owner) throws ClientException {
		init();
		URI u = UriBuilder.fromPath(CONTEXT_ROOT).scheme("http").host(this.host).port(this.port).build();
		WebTarget r = this.jerseyClient.target(u);
		try {
			r = r.queryParam("elastic", String.valueOf(elastic)).queryParam("minNodes", String.valueOf(minNodes));
			if (version != null) {
				r = r.queryParam("version", version);
			}
			if (description != null) {
				r = r.queryParam("desc", description);
			}
			if (owner != null) {
				r = r.queryParam("owner", owner);
			}
			Response response = r.path("/domains").path(domainName).path("appspaces").path(appSpaceName).request(MediaType.APPLICATION_JSON_TYPE).post(null);
			processErrorResponse(response);
			AppSpace appSpace = response.readEntity(AppSpace.class);
			return appSpace;
		} catch (ProcessingException pe) {
			throw getConnectionException(pe);
		} catch (Exception ex) {
			throw new ClientException(500, ex.getMessage(), ex);
		}
	}

	public void startAppSpace(final String domainName, final String appSpaceName) throws ClientException {
		init();
		log.info("Starting AppSpace with name -> " + appSpaceName + " in Domain -> " + domainName);
		URI u = UriBuilder.fromPath(CONTEXT_ROOT).scheme("http").host(this.host).port(this.port).build();
		WebTarget r = this.jerseyClient.target(u);
		try {
			Response response = r.path("/domains").path(domainName).path("appspaces").path(appSpaceName).path("start").request(MediaType.APPLICATION_JSON_TYPE).post(null);
			processErrorResponse(response);
		} catch (ProcessingException pe) {
			throw getConnectionException(pe);
		} catch (Exception ex) {
			throw new ClientException(500, ex.getMessage(), ex);
		}
	}

	private AppNode createAppNode(final String domainName, final String appSpaceName, final String appNodeName, final String agentName, final int httpPort, final int osgiPort, final String description) throws ClientException {
		init();
		URI u = UriBuilder.fromPath(CONTEXT_ROOT).scheme("http").host(this.host).port(this.port).build();
		WebTarget r = this.jerseyClient.target(u);
		try {
			if (agentName != null) {
				r = r.queryParam("agent", String.valueOf(agentName));
			}
			if (httpPort > 0) {
				r = r.queryParam("httpport", String.valueOf(httpPort));
			}
			if (osgiPort > 0) {
				r = r.queryParam("osgiport", String.valueOf(osgiPort));
			}
			if (description != null) {
				r = r.queryParam("description", description);
			}
			Response response = r.path("/domains").path(domainName).path("appspaces").path(appSpaceName).path("appnodes").path(appNodeName).request(MediaType.APPLICATION_JSON_TYPE).post(null);
			processErrorResponse(response);
			AppNode appNode = response.readEntity(AppNode.class);
			return appNode;
		} catch (ProcessingException pe) {
			throw getConnectionException(pe);
		} catch (Exception ex) {
			throw new ClientException(500, ex.getMessage(), ex);
		}
	}

	@SuppressWarnings("unused")
	private void startAppNode(final String domainName, final String appSpaceName, final String appNodeName) throws ClientException {
		init();
		URI u = UriBuilder.fromPath(CONTEXT_ROOT).scheme("http").host(this.host).port(this.port).build();
		WebTarget r = this.jerseyClient.target(u);
		try {
			Response response = r.path("/domains").path(domainName).path("appspaces").path(appSpaceName).path("appnodes").path(appNodeName).path("start").request(MediaType.APPLICATION_JSON_TYPE).post(null);
			processErrorResponse(response);
		} catch (ProcessingException pe) {
			throw getConnectionException(pe);
		} catch (Exception ex) {
			throw new ClientException(500, ex.getMessage(), ex);
		}
	}

	private void uploadArchive(final String domainName, final String path, final String file, final boolean replace) throws ClientException {
		init();
		URI u = UriBuilder.fromPath(CONTEXT_ROOT).scheme("http").host(this.host).port(this.port).build();
		WebTarget r = this.jerseyClient.target(u);

		try (MultiPart multipart = new FormDataMultiPart()) {
			r = r.path("/domains").path(domainName).path("archives");
			r = r.queryParam("replace", replace);
			if (path != null) {
				r = r.queryParam("path", path);
			}

			File fileEntity = new File(file);
			final FileDataBodyPart filePart = new FileDataBodyPart("file", fileEntity, MediaType.APPLICATION_OCTET_STREAM_TYPE);

			FormDataContentDisposition.FormDataContentDispositionBuilder builder = FormDataContentDisposition.name("file");
			builder.fileName(URLEncoder.encode(file, "UTF-8"));
			builder.size(fileEntity.length());
			builder.modificationDate(new Date(fileEntity.lastModified()));
			filePart.setFormDataContentDisposition(builder.build());
			multipart.bodyPart(filePart);

			Response response = r.request(MediaType.APPLICATION_JSON_TYPE).post(Entity.entity(multipart, multipart.getMediaType()));

			if (!response.getStatusInfo().getFamily().equals(Family.SUCCESSFUL)) {
				if (response.getMediaType().getType().equals(MediaType.TEXT_HTML_TYPE.getType()) && response.getMediaType().getSubtype().equals(MediaType.TEXT_HTML_TYPE.getSubtype())) {
					throw new ClientException(response.getStatus(), response.readEntity(String.class), null);
				} else {
					processErrorResponse(response);
				}
			}
		} catch (ProcessingException pe) {
			throw getConnectionException(pe);
		} catch (Exception ex) {
			throw new ClientException(500, ex.getMessage(), ex);
		}
	}

	private Application deployApplication(final String domainName, final String appSpaceName, final String archiveName, final String path, final boolean startOnDeploy, final boolean replace, final String profile) throws ClientException {
		init();
		URI u = UriBuilder.fromPath(CONTEXT_ROOT).scheme("http").host(this.host).port(this.port).build();
		WebTarget r = this.jerseyClient.target(u);
		try {
			r = r.queryParam("archivename", archiveName);
			if (path != null) {
				r = r.queryParam("path", path);
			}
			r = r.queryParam("startondeploy", String.valueOf(startOnDeploy)).queryParam("replace", String.valueOf(replace));
			if (profile != null) {
				r = r.queryParam("profile", profile);
			}
			Response response = r.path("/domains").path(domainName).path("appspaces").path(appSpaceName).path("applications").request(MediaType.APPLICATION_JSON_TYPE).post(null);
			processErrorResponse(response);
			Application application = response.readEntity(Application.class);
			if(!application.getCode().isEmpty()) {
				throw new ClientException(500, application.getCode() + ": " + application.getMessage(), null);
			}
			return application;
		} catch (ProcessingException pe) {
			throw getConnectionException(pe);
		} catch (Exception ex) {
			throw new ClientException(500, ex.getMessage(), ex);
		}
	}

	private void undeployApplication(final String domainName, final String appSpaceName, final String appName, final String version) throws ClientException {
		init();
		URI u = UriBuilder.fromPath(CONTEXT_ROOT).scheme("http").host(this.host).port(this.port).build();
		WebTarget r = this.jerseyClient.target(u);
		try {
			Response response = r.path("/domains").path(domainName).path("appspaces").path(appSpaceName).path("applications").path(appName).path(version).request(MediaType.APPLICATION_JSON_TYPE).delete();
			processErrorResponse(response);
		} catch (ProcessingException pe) {
			throw getConnectionException(pe);
		} catch (Exception ex) {
			throw new ClientException(500, ex.getMessage(), ex);
		}
	}

	@SuppressWarnings("unused")
	private void startApplication(final String domainName, final String appSpaceName, final String appName, final String version, final String appNodeName) throws ClientException {
		init();
		URI u = UriBuilder.fromPath(CONTEXT_ROOT).scheme("http").host(this.host).port(this.port).build();
		WebTarget r = this.jerseyClient.target(u);
		try {
			if (appNodeName != null) {
				r = r.queryParam("appnode", appNodeName);
			}
			Response response = r.path("/domains").path(domainName).path("appspaces").path(appSpaceName).path("applications").path(appName).path(version).path("start").request(MediaType.APPLICATION_JSON_TYPE).post(null);
			processErrorResponse(response);
		} catch (ProcessingException pe) {
			throw getConnectionException(pe);
		} catch (Exception ex) {
			throw new ClientException(500, ex.getMessage(), ex);
		}
	}

	private List<AppNode> getAppNodes(final String domainName, final String appSpaceName, final String filter, final boolean status) throws ClientException {
		init();
		URI u = UriBuilder.fromPath(CONTEXT_ROOT).scheme("http").host(this.host).port(this.port).build();
		WebTarget r = this.jerseyClient.target(u);
		try {
			r = r.queryParam("domain", domainName).queryParam("appspace", appSpaceName).queryParam("status", status);
			if (filter != null) {
				r = r.queryParam("filter", filter);
			}
			Response response = r.path("/browse").path("appnodes").request(MediaType.APPLICATION_JSON_TYPE).get();
			processErrorResponse(response);
			List<AppNode> appSpaces = response.readEntity(new GenericType<List<AppNode>>() {});
			return appSpaces;
		} catch (ProcessingException pe) {
			throw getConnectionException(pe);
		} catch (Exception ex) {
			throw new ClientException(500, ex.getMessage(), ex);
		}
	}

	@SuppressWarnings("unused")
	private List<Archive> getArchives(final String domainName, final String path, final String filter) throws ClientException {
		init();
		URI u = UriBuilder.fromPath(CONTEXT_ROOT).scheme("http").host(this.host).port(this.port).build();
		WebTarget r = this.jerseyClient.target(u);
		try {
			r = r.queryParam("domain", domainName);
			if (path != null) {
				r = r.queryParam("path", path);
			}
			if (filter != null) {
				r = r.queryParam("filter", filter);
			}
			Response response = r.path("/browse").path("archives").request(MediaType.APPLICATION_JSON_TYPE).get();
			processErrorResponse(response);
			List<Archive> archives = response.readEntity(new GenericType<List<Archive>>() {});
			return archives;
		} catch (ProcessingException pe) {
			throw getConnectionException(pe);
		} catch (Exception ex) {
			throw new ClientException(500, ex.getMessage(), ex);
		}
	}

	private List<Application> getApplications(final String domainName, final String appSpace, final String filter, final boolean status) throws ClientException {
		init();
		URI u = UriBuilder.fromPath(CONTEXT_ROOT).scheme("http").host(this.host).port(this.port).build();
		WebTarget r = this.jerseyClient.target(u);
		try {
			r = r.queryParam("domain", domainName);
			if (appSpace != null) {
				r = r.queryParam("appspace", appSpace);
			}
			if (filter != null) {
				r = r.queryParam("filter", filter);
			}
			r = r.queryParam("status", status);
			Response response = r.path("/browse").path("apps").request(MediaType.APPLICATION_JSON_TYPE).get();
			processErrorResponse(response);
			List<Application> apps = response.readEntity(new GenericType<List<Application>>() {});
			return apps;
		} catch (ProcessingException pe) {
			throw getConnectionException(pe);
		} catch (Exception ex) {
			throw new ClientException(500, ex.getMessage(), ex);
		}
	}

	public void downloadArchive(final String domainName, final String path, final String name) throws ClientException {
		init();
		URI u = UriBuilder.fromPath(CONTEXT_ROOT).scheme("http").host(this.host).port(this.port).build();
		WebTarget r = this.jerseyClient.target(u);
		try {
			Response response = r.path("/domains").path(domainName).path("archives").path(name).path("content").request().get();
			processErrorResponse(response);
			saveArchive(response, path, name);
		} catch (ProcessingException pe) {
			throw getConnectionException(pe);
		} catch (Exception ex) {
			throw new ClientException(500, ex.getMessage(), ex);
		}
	}

	public void downloadProfileAplication(final String domainName, final String path, final String name, final String profileName) throws ClientException {
		init();
		URI u = UriBuilder.fromPath(CONTEXT_ROOT).scheme("http").host(this.host).port(this.port).build();
		WebTarget r = this.jerseyClient.target(u);
		try {
			Response response = r.path("/domains").path(domainName).path("archives").path(name).path(profileName).request().get();
			processErrorResponse(response);
			saveArchive(response, path, profileName);
		} catch (ProcessingException pe) {
			throw getConnectionException(pe);
		} catch (Exception ex) {
			throw new ClientException(500, ex.getMessage(), ex);
		}
	}

	private void saveArchive(Response response, final String path, final String name) throws IOException {
		FileOutputStream outputStream = null;
		try {
			// Read response string
			InputStream inputStream = response.readEntity(InputStream.class);
			String fullPath = path + File.separator + DATE_TIME;
			File file = new File(fullPath);
			FileUtils.forceMkdir(file);
			//parentPath = file.getAbsoluteFile().getAbsolutePath();
			fullPath += File.separator + name;
			outputStream = new FileOutputStream(fullPath);
			byte[] buffer = new byte[1024];
			int bytesRead;
			while ((bytesRead = inputStream.read(buffer)) != -1) {
				outputStream.write(buffer, 0, bytesRead);
			}
			log.info("Downloaded successfully at: " + fullPath);
		} catch (IOException e) {
			throw e;
		} finally {
			if(outputStream != null) {
				try {
					outputStream.close();
				} catch (IOException e) {
					log.error(e);
				}
			}
		}
	}

	private void processErrorResponse(Response response) throws ClientException {
		if (!Family.SUCCESSFUL.equals(response.getStatusInfo().getFamily())) {
			com.tibco.bw.maven.plugin.admin.dto.Error error = response.readEntity(com.tibco.bw.maven.plugin.admin.dto.Error.class);
			if (error != null) {
				throw new ClientException(response.getStatus(), error.getCode() + ": " + error.getMessage(), null);
			} else {
				throw new ClientException(response.getStatus(), response.getStatusInfo().getReasonPhrase(), null);
			}
		}
	}

	private static ClientException getConnectionException(ProcessingException pe) {
		if (pe.getCause() instanceof ConnectException) {
			return new ClientException(503, pe.getCause().getMessage(), pe.getCause());
		}
		//https://java.net/jira/browse/JERSEY-2728
		if (pe.getCause() instanceof IllegalStateException) {
			return new ClientException(503, pe.getCause().getMessage(), pe.getCause());
		}
		return new ClientException(500, pe.getMessage(), pe);
	}
}
package com.tibco.bw.maven.plugin.admin.client;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status.Family;
import javax.ws.rs.core.UriBuilder;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.apache.maven.plugin.logging.Log;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.media.multipart.BodyPart;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.media.multipart.MultiPartMediaTypes;
import org.glassfish.jersey.media.multipart.file.FileDataBodyPart;

import com.tibco.bw.maven.plugin.admin.dto.Agent;
import com.tibco.bw.maven.plugin.admin.dto.AppNode;
import com.tibco.bw.maven.plugin.admin.dto.AppSpace;
import com.tibco.bw.maven.plugin.admin.dto.Application;
import com.tibco.bw.maven.plugin.admin.dto.Archive;
import com.tibco.bw.maven.plugin.admin.dto.BWEngineInfo;
import com.tibco.bw.maven.plugin.admin.dto.Domain;
import com.tibco.bw.maven.plugin.admin.dto.DomainConfig;
import com.tibco.bw.maven.plugin.admin.dto.Installation;
import com.tibco.bw.maven.plugin.admin.dto.Machine;
import com.tibco.bw.maven.plugin.admin.dto.Property;
import com.tibco.bw.maven.plugin.admin.dto.SystemProcessInfo;

public class RemoteDeployer {
	private javax.ws.rs.client.Client jerseyClient;

	private static final String CONTEXT_ROOT = "/bw/v1";

	private static final String REPLACE_EXISTING = null;

	private final String host;
	private final int port;

	static Logger log = Logger.getLogger(RemoteDeployer.class);

	private void init() {
		if (this.jerseyClient == null) {
			ClientConfig clientConfig = new ClientConfig();
			clientConfig.register(JacksonFeature.class).register(MultiPartFeature.class);
			this.jerseyClient = ClientBuilder.newClient(clientConfig);
		}
	}

	public RemoteDeployer(final String host, final String port) {
		if (host == null) {
			throw new IllegalArgumentException("host must not be null");
		}

		int p = Integer.parseInt(port);

		if (p <= 0 | p > 65535) {
			throw new IllegalArgumentException("invalid port number");
		}

		this.host = host;
		this.port = p;
	}

	public void setLog(Log log) {
		RemoteDeployer.log = (Logger) log;
	}

	public void close() {
		if (this.jerseyClient != null) {
			this.jerseyClient.close();
			this.jerseyClient = null;
		}
	}

	public List<Agent> getAgentInfo() throws ClientException {
		init();
		URI u = UriBuilder.fromPath(CONTEXT_ROOT).scheme("http").host(this.host).port(this.port).build();
		WebTarget r = this.jerseyClient.target(u);

		try {
			Response response = r.path("/agents").path("info").request(MediaType.APPLICATION_JSON_TYPE).get();
			if (!response.getStatusInfo().getFamily().equals(Family.SUCCESSFUL)) {
				com.tibco.bw.maven.plugin.admin.dto.Error error = response
						.readEntity(com.tibco.bw.maven.plugin.admin.dto.Error.class);
				if (error != null) {
					throw new ClientException(response.getStatus(), error.getCode() + ": " + error.getMessage(), null);
				} else {
					throw new ClientException(response.getStatus(), response.getStatusInfo().getReasonPhrase(), null);
				}
			}

			List<Agent> info = response.readEntity(new GenericType<List<Agent>>() {
			});
			return info;
		} catch (ClientException che) {
			throw che;
		} catch (ProcessingException pe) {
			if (pe.getCause() instanceof ConnectException) {
				throw new ClientException(503, pe.getCause().getMessage(), pe.getCause());
			}
			throw new ClientException(500, pe.getMessage(), pe);
		} catch (Exception ex) {
			throw new ClientException(500, ex.getMessage(), ex);
		}
	}

	public Domain getOrCreateDomain(final String name, final String desc) throws ClientException {

		List<Domain> domains = getDomains(null, false, true);
		for (Domain domain : domains) {
			if (domain.getName().equals(name)) {
				log.info("Domain exists with Name -> " + name + " ");
				return domain;
			}
		}

		log.info("Creating Domain with name -> " + name);

		return createDomain(name, desc, "owner", null, null);

	}

	private Domain createDomain(final String name, final String description, final String owner, final String agent,
			final String home) throws ClientException {
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
			if (!response.getStatusInfo().getFamily().equals(Family.SUCCESSFUL)) {
				com.tibco.bw.maven.plugin.admin.dto.Error error = response
						.readEntity(com.tibco.bw.maven.plugin.admin.dto.Error.class);
				if (error != null) {
					throw new ClientException(response.getStatus(), error.getCode() + ": " + error.getMessage(), null);
				} else {
					throw new ClientException(response.getStatus(), response.getStatusInfo().getReasonPhrase(), null);
				}
			}

			Domain domain = response.readEntity(Domain.class);
			return domain;
		} catch (ClientException che) {
			throw che;
		} catch (ProcessingException pe) {
			throw getConnectionException(pe);
		} catch (Exception ex) {
			throw new ClientException(500, ex.getMessage(), ex);
		}
	}

	private List<Domain> getDomains(final String filter, final boolean full, final boolean status)
			throws ClientException {
		init();
		URI u = UriBuilder.fromPath(CONTEXT_ROOT).scheme("http").host(this.host).port(this.port).build();
		WebTarget r = this.jerseyClient.target(u);

		try {
			r = r.queryParam("full", full).queryParam("status", status);
			if (filter != null) {
				r = r.queryParam("filter", filter);
			}

			Response response = r.path("/browse").path("domains").request(MediaType.APPLICATION_JSON_TYPE).get();
			if (!response.getStatusInfo().getFamily().equals(Family.SUCCESSFUL)) {
				com.tibco.bw.maven.plugin.admin.dto.Error error = response
						.readEntity(com.tibco.bw.maven.plugin.admin.dto.Error.class);
				if (error != null) {
					throw new ClientException(response.getStatus(), error.getCode() + ": " + error.getMessage(), null);
				} else {
					throw new ClientException(response.getStatus(), response.getStatusInfo().getReasonPhrase(), null);
				}
			}

			List<Domain> domains = response.readEntity(new GenericType<List<Domain>>() {
			});
			return domains;
		} catch (ClientException che) {
			throw che;
		} catch (ProcessingException pe) {
			throw getConnectionException(pe);
		} catch (Exception ex) {
			throw new ClientException(500, ex.getMessage(), ex);
		}
	}

	public AppSpace getOrCreateAppSpace(final String domainName, final String appSpaceName, final String desc)
			throws ClientException {

		List<AppSpace> appSpaces = getAppSpaces(domainName, null, false, true);
		for (AppSpace appSpace : appSpaces) {
			if (appSpace.getName().equals(appSpaceName)) {
				log.info("AppSpace exists with Name -> " + appSpaceName + " in Domain -> " + domainName);
				return appSpace;
			}
		}

		log.info("Creating AppSpace with Name -> " + appSpaceName + " in Domain -> " + domainName);
		return createAppSpace(domainName, appSpaceName, true, 0, null, desc, "owner");

	}

	public AppNode getOrCreateAppNode(final String domainName, final String appSpaceName, final String appNodeName,
			final int httpPort, final int osgiPort, final String description) throws ClientException {

		List<AppNode> nodes = getAppNodes(domainName, appSpaceName, null, true);
		for (AppNode node : nodes) {
			if (node.getName().equals(appNodeName)) {
				log.info("AppNode exists with Name -> " + appNodeName + " in Domain -> " + domainName
						+ " and in AppSpace -> " + appSpaceName);
				log.info("AppNode HTTP Port  -> " + httpPort + ". AppNode OSGi Port -> " + osgiPort);
				return node;
			}

		}

		log.info("Creating AppNode with Name -> " + appNodeName + " in Domain -> " + domainName + " and in AppSpace -> "
				+ appSpaceName);
		return createAppNode(domainName, appSpaceName, appNodeName, null, httpPort, osgiPort, description);

	}

	public void addAndDeployApplication(final String domainName, final String appSpaceName, final String appName,
			final String earName, final String file, final boolean replace, final String profile)
					throws ClientException {
		List<Application> applications = getApplications(domainName, appSpaceName, null, true);

		for (Application application : applications) {
			if (application.getName().equals(appName)) {
				if (replace) {
					log.info("Application exists with name -> " + appName
							+ ". Undeploying the Application as Redeploy flag is true.");
					undeployApplication(domainName, appSpaceName, appName, application.getVersion());
				} else {
					log.info("Application exists with name -> " + appName
							+ ". Not Re-deploying the Application as Redeploy flag is false.");
					return;
				}

			}
		}

		log.info("Uploading the Archive file -> " + earName);
		uploadArchive(domainName, null, file, true);

		log.info("Deploying the Application with name -> " + appName + " with Profile -> " + profile);
		deployApplication(domainName, appSpaceName, earName, null, true, replace, profile);

	}

	public void backupApplication(final String domainName, final String appSpaceName, final String appName,
			final String earName, final String file, final boolean replace, final String profile)
					throws ClientException {
		List<Application> applications = getApplications(domainName, appSpaceName, null, true);

		for (Application application : applications) {

			if (application.getName().equals(appName)) {

				
				log.info("Application exists with name -> " + appName + ". Generating BackupFile ");

				downloadArchive(domainName, "target", application.getArchiveName().toString());

				log.info("Application exists with name  download Profile -> " + application.getProfileName() + ". Generating BackupFile ");

				downloadProfileAplication(domainName, "target",application.getArchiveName().toString(), application.getProfileName());

				
				
				
				

			}
		}

	}

	
	private void saveArchive(Response response, final String path, final String name)
			throws ClientException {
		try {
		// read response string
		InputStream inputStream = response.readEntity(InputStream.class);
		SimpleDateFormat formatter = new SimpleDateFormat("ddMMyyyy_HHmm");
		String parentPath = path + "/" + formatter.format(new Date());
		File file = new File(parentPath);

			FileUtils.forceMkdir(file);

		parentPath = file.getAbsoluteFile().getAbsolutePath();
		String qualifiedDownloadFilePath = parentPath +"/" + name;
		FileOutputStream outputStream = new FileOutputStream(qualifiedDownloadFilePath);
		byte[] buffer = new byte[1024];
		int bytesRead;
		while ((bytesRead = inputStream.read(buffer)) != -1) {
			outputStream.write(buffer, 0, bytesRead);
		}

		// set download SUCCES message to return
		String responseString = "downloaded successfully at " + qualifiedDownloadFilePath;
		log.info(responseString);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private void downloadArchive(final String domainName, final String path, final String name)
			throws ClientException {
		init();
		URI u = UriBuilder.fromPath(CONTEXT_ROOT).scheme("http").host(this.host).port(this.port).build();
		WebTarget r = this.jerseyClient.target(u);

		try {

			Response response = r.path("/domains").path(domainName).path("archives").path(name).path("content")
					.request().get();
			//System.out.println(response.toString());

			// get response code
			int responseCode = response.getStatus();
			//System.out.println("Response code: " + responseCode);

			if (response.getStatus() != 200) {
				throw new RuntimeException("Failed with HTTP error code : " + responseCode);
			}

			saveArchive(response, path, name);
			

		} catch (ProcessingException pe) {
			throw getConnectionException(pe);
		} catch (Exception ex) {
			throw new ClientException(500, ex.getMessage(), ex);
		}
	}
	
	private void downloadProfileAplication(final String domainName, final String path, final String name,final String profileName)
			throws ClientException {
		init();
		URI u = UriBuilder.fromPath(CONTEXT_ROOT).scheme("http").host(this.host).port(this.port).build();
		WebTarget r = this.jerseyClient.target(u);

		try {

			Response response = r.path("/domains").path(domainName).path("archives").path(name).path(profileName)
					.request().get();
			//System.out.println(response.toString());

			// get response code
			int responseCode = response.getStatus();
			//System.out.println("Response code: " + responseCode);

			if (response.getStatus() != 200) {
				throw new RuntimeException("Failed with HTTP error code : " + responseCode);
			}

			saveArchive(response, path, profileName);
			

		} catch (ProcessingException pe) {
			throw getConnectionException(pe);
		} catch (Exception ex) {
			throw new ClientException(500, ex.getMessage(), ex);
		}
	}

	private List<AppSpace> getAppSpaces(final String domainName, final String filter, final boolean full,
			final boolean status) throws ClientException {
		init();
		URI u = UriBuilder.fromPath(CONTEXT_ROOT).scheme("http").host(this.host).port(this.port).build();
		WebTarget r = this.jerseyClient.target(u);

		try {
			r = r.queryParam("domain", domainName).queryParam("full", full).queryParam("status", status);
			if (filter != null) {
				r = r.queryParam("filter", filter);
			}

			Response response = r.path("/browse").path("appspaces").request(MediaType.APPLICATION_JSON_TYPE).get();
			if (!response.getStatusInfo().getFamily().equals(Family.SUCCESSFUL)) {
				com.tibco.bw.maven.plugin.admin.dto.Error error = response
						.readEntity(com.tibco.bw.maven.plugin.admin.dto.Error.class);
				if (error != null) {
					throw new ClientException(response.getStatus(), error.getCode() + ": " + error.getMessage(), null);
				} else {
					throw new ClientException(response.getStatus(), response.getStatusInfo().getReasonPhrase(), null);
				}
			}

			List<AppSpace> appSpaces = response.readEntity(new GenericType<List<AppSpace>>() {
			});
			return appSpaces;
		} catch (ClientException che) {
			throw che;
		} catch (ProcessingException pe) {
			throw getConnectionException(pe);
		} catch (Exception ex) {
			throw new ClientException(500, ex.getMessage(), ex);
		}
	}

	private AppSpace createAppSpace(final String domainName, final String appSpaceName, final boolean elastic,
			final int minNodes, final String version, final String description, final String owner)
					throws ClientException {
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

			Response response = r.path("/domains").path(domainName).path("appspaces").path(appSpaceName)
					.request(MediaType.APPLICATION_JSON_TYPE).post(null);
			if (!response.getStatusInfo().getFamily().equals(Family.SUCCESSFUL)) {
				com.tibco.bw.maven.plugin.admin.dto.Error error = response
						.readEntity(com.tibco.bw.maven.plugin.admin.dto.Error.class);
				if (error != null) {
					throw new ClientException(response.getStatus(), error.getCode() + ": " + error.getMessage(), null);
				} else {
					throw new ClientException(response.getStatus(), response.getStatusInfo().getReasonPhrase(), null);
				}
			}

			AppSpace appSpace = response.readEntity(AppSpace.class);
			return appSpace;
		} catch (ClientException che) {
			throw che;
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
			Response response = r.path("/domains").path(domainName).path("appspaces").path(appSpaceName).path("start")
					.request(MediaType.APPLICATION_JSON_TYPE).post(null);
			if (!response.getStatusInfo().getFamily().equals(Family.SUCCESSFUL)) {
				com.tibco.bw.maven.plugin.admin.dto.Error error = response
						.readEntity(com.tibco.bw.maven.plugin.admin.dto.Error.class);
				if (error != null) {
					throw new ClientException(response.getStatus(), error.getCode() + ": " + error.getMessage(), null);
				} else {
					throw new ClientException(response.getStatus(), response.getStatusInfo().getReasonPhrase(), null);
				}
			}
		} catch (ClientException che) {
			throw che;
		} catch (ProcessingException pe) {
			throw getConnectionException(pe);
		} catch (Exception ex) {
			throw new ClientException(500, ex.getMessage(), ex);
		}
	}

	private AppNode createAppNode(final String domainName, final String appSpaceName, final String appNodeName,
			final String agentName, final int httpPort, final int osgiPort, final String description)
					throws ClientException {
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

			Response response = r.path("/domains").path(domainName).path("appspaces").path(appSpaceName)
					.path("appnodes").path(appNodeName).request(MediaType.APPLICATION_JSON_TYPE).post(null);
			if (!response.getStatusInfo().getFamily().equals(Family.SUCCESSFUL)) {
				com.tibco.bw.maven.plugin.admin.dto.Error error = response
						.readEntity(com.tibco.bw.maven.plugin.admin.dto.Error.class);
				if (error != null) {
					throw new ClientException(response.getStatus(), error.getCode() + ": " + error.getMessage(), null);
				} else {
					throw new ClientException(response.getStatus(), response.getStatusInfo().getReasonPhrase(), null);
				}
			}

			AppNode appNode = response.readEntity(AppNode.class);
			return appNode;
		} catch (ClientException che) {
			throw che;
		} catch (ProcessingException pe) {
			throw getConnectionException(pe);
		} catch (Exception ex) {
			throw new ClientException(500, ex.getMessage(), ex);
		}
	}

	private void startAppNode(final String domainName, final String appSpaceName, final String appNodeName)
			throws ClientException {
		init();
		URI u = UriBuilder.fromPath(CONTEXT_ROOT).scheme("http").host(this.host).port(this.port).build();
		WebTarget r = this.jerseyClient.target(u);

		try {
			Response response = r.path("/domains").path(domainName).path("appspaces").path(appSpaceName)
					.path("appnodes").path(appNodeName).path("start").request(MediaType.APPLICATION_JSON_TYPE)
					.post(null);
			if (!response.getStatusInfo().getFamily().equals(Family.SUCCESSFUL)) {
				com.tibco.bw.maven.plugin.admin.dto.Error error = response
						.readEntity(com.tibco.bw.maven.plugin.admin.dto.Error.class);
				if (error != null) {
					throw new ClientException(response.getStatus(), error.getCode() + ": " + error.getMessage(), null);
				} else {
					throw new ClientException(response.getStatus(), response.getStatusInfo().getReasonPhrase(), null);
				}
			}
		} catch (ClientException che) {
			throw che;
		} catch (ProcessingException pe) {
			throw getConnectionException(pe);
		} catch (Exception ex) {
			throw new ClientException(500, ex.getMessage(), ex);
		}
	}

	private void uploadArchive(final String domainName, final String path, final String file, final boolean replace)
			throws ClientException {
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
			final FileDataBodyPart filePart = new FileDataBodyPart("file", fileEntity,
					MediaType.APPLICATION_OCTET_STREAM_TYPE);

			FormDataContentDisposition.FormDataContentDispositionBuilder builder = FormDataContentDisposition
					.name("file");
			builder.fileName(URLEncoder.encode(file, "UTF-8"));
			builder.size(fileEntity.length());
			builder.modificationDate(new Date(fileEntity.lastModified()));
			filePart.setFormDataContentDisposition(builder.build());
			multipart.bodyPart(filePart);

			Response response = r.request(MediaType.APPLICATION_JSON_TYPE)
					.post(Entity.entity(multipart, multipart.getMediaType()));

			if (!response.getStatusInfo().getFamily().equals(Family.SUCCESSFUL)) {
				if (response.getMediaType().getType().equals(MediaType.TEXT_HTML_TYPE.getType())
						&& response.getMediaType().getSubtype().equals(MediaType.TEXT_HTML_TYPE.getSubtype())) {
					throw new ClientException(response.getStatus(), response.readEntity(String.class), null);
				} else {
					com.tibco.bw.maven.plugin.admin.dto.Error error = response
							.readEntity(com.tibco.bw.maven.plugin.admin.dto.Error.class);
					if (error != null) {
						throw new ClientException(response.getStatus(), error.getCode() + ": " + error.getMessage(),
								null);
					} else {
						throw new ClientException(response.getStatus(), response.getStatusInfo().getReasonPhrase(),
								null);
					}
				}
			}
		} catch (ClientException che) {
			throw che;
		} catch (ProcessingException pe) {
			throw getConnectionException(pe);
		} catch (Exception ex) {
			throw new ClientException(500, ex.getMessage(), ex);
		}
	}

	private Application deployApplication(final String domainName, final String appSpaceName, final String archiveName,
			final String path, final boolean startOnDeploy, final boolean replace, final String profile)
					throws ClientException {
		init();
		URI u = UriBuilder.fromPath(CONTEXT_ROOT).scheme("http").host(this.host).port(this.port).build();
		WebTarget r = this.jerseyClient.target(u);

		try {
			r = r.queryParam("archivename", archiveName);

			if (path != null) {
				r = r.queryParam("path", path);
			}

			r = r.queryParam("startondeploy", String.valueOf(startOnDeploy)).queryParam("replace",
					String.valueOf(replace));

			if (profile != null) {
				r = r.queryParam("profile", profile);
			}

			Response response = r.path("/domains").path(domainName).path("appspaces").path(appSpaceName)
					.path("applications").request(MediaType.APPLICATION_JSON_TYPE).post(null);
			if (!response.getStatusInfo().getFamily().equals(Family.SUCCESSFUL)) {
				com.tibco.bw.maven.plugin.admin.dto.Error error = response
						.readEntity(com.tibco.bw.maven.plugin.admin.dto.Error.class);
				if (error != null) {
					throw new ClientException(response.getStatus(), error.getCode() + ": " + error.getMessage(), null);
				} else {
					throw new ClientException(response.getStatus(), response.getStatusInfo().getReasonPhrase(), null);
				}
			}

			Application application = response.readEntity(Application.class);
			return application;
		} catch (ClientException che) {
			throw che;
		} catch (ProcessingException pe) {
			throw getConnectionException(pe);
		} catch (Exception ex) {
			throw new ClientException(500, ex.getMessage(), ex);
		}
	}

	private void undeployApplication(final String domainName, final String appSpaceName, final String appName,
			final String version) throws ClientException {
		init();
		URI u = UriBuilder.fromPath(CONTEXT_ROOT).scheme("http").host(this.host).port(this.port).build();
		WebTarget r = this.jerseyClient.target(u);

		try {
			Response response = r.path("/domains").path(domainName).path("appspaces").path(appSpaceName)
					.path("applications").path(appName).path(version).request(MediaType.APPLICATION_JSON_TYPE).delete();

			if (!response.getStatusInfo().getFamily().equals(Family.SUCCESSFUL)) {
				com.tibco.bw.maven.plugin.admin.dto.Error error = response
						.readEntity(com.tibco.bw.maven.plugin.admin.dto.Error.class);
				if (error != null) {
					throw new ClientException(response.getStatus(), error.getCode() + ": " + error.getMessage(), null);
				} else {
					throw new ClientException(response.getStatus(), response.getStatusInfo().getReasonPhrase(), null);
				}
			}

		} catch (ClientException che) {
			throw che;
		} catch (ProcessingException pe) {
			throw getConnectionException(pe);
		} catch (Exception ex) {
			throw new ClientException(500, ex.getMessage(), ex);
		}
	}

	private void startApplication(final String domainName, final String appSpaceName, final String appName,
			final String version, final String appNodeName) throws ClientException {
		init();
		URI u = UriBuilder.fromPath(CONTEXT_ROOT).scheme("http").host(this.host).port(this.port).build();
		WebTarget r = this.jerseyClient.target(u);

		try {
			if (appNodeName != null) {
				r = r.queryParam("appnode", appNodeName);
			}

			Response response = r.path("/domains").path(domainName).path("appspaces").path(appSpaceName)
					.path("applications").path(appName).path(version).path("start")
					.request(MediaType.APPLICATION_JSON_TYPE).post(null);
			if (!response.getStatusInfo().getFamily().equals(Family.SUCCESSFUL)) {
				com.tibco.bw.maven.plugin.admin.dto.Error error = response
						.readEntity(com.tibco.bw.maven.plugin.admin.dto.Error.class);
				if (error != null) {
					throw new ClientException(response.getStatus(), error.getCode() + ": " + error.getMessage(), null);
				} else {
					throw new ClientException(response.getStatus(), response.getStatusInfo().getReasonPhrase(), null);
				}
			}
		} catch (ClientException che) {
			throw che;
		} catch (ProcessingException pe) {
			throw getConnectionException(pe);
		} catch (Exception ex) {
			throw new ClientException(500, ex.getMessage(), ex);
		}
	}

	private List<AppNode> getAppNodes(final String domainName, final String appSpaceName, final String filter,
			final boolean status) throws ClientException {
		init();
		URI u = UriBuilder.fromPath(CONTEXT_ROOT).scheme("http").host(this.host).port(this.port).build();
		WebTarget r = this.jerseyClient.target(u);

		try {
			r = r.queryParam("domain", domainName).queryParam("appspace", appSpaceName).queryParam("status", status);
			if (filter != null) {
				r = r.queryParam("filter", filter);
			}

			Response response = r.path("/browse").path("appnodes").request(MediaType.APPLICATION_JSON_TYPE).get();
			if (!response.getStatusInfo().getFamily().equals(Family.SUCCESSFUL)) {
				com.tibco.bw.maven.plugin.admin.dto.Error error = response
						.readEntity(com.tibco.bw.maven.plugin.admin.dto.Error.class);
				if (error != null) {
					throw new ClientException(response.getStatus(), error.getCode() + ": " + error.getMessage(), null);
				} else {
					throw new ClientException(response.getStatus(), response.getStatusInfo().getReasonPhrase(), null);
				}
			}

			List<AppNode> appSpaces = response.readEntity(new GenericType<List<AppNode>>() {
			});
			return appSpaces;
		} catch (ClientException che) {
			throw che;
		} catch (ProcessingException pe) {
			throw getConnectionException(pe);
		} catch (Exception ex) {
			throw new ClientException(500, ex.getMessage(), ex);
		}
	}

	private List<Archive> getArchives(final String domainName, final String path, final String filter)
			throws ClientException {
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
			if (!response.getStatusInfo().getFamily().equals(Family.SUCCESSFUL)) {
				com.tibco.bw.maven.plugin.admin.dto.Error error = response
						.readEntity(com.tibco.bw.maven.plugin.admin.dto.Error.class);
				if (error != null) {
					throw new ClientException(response.getStatus(), error.getCode() + ": " + error.getMessage(), null);
				} else {
					throw new ClientException(response.getStatus(), response.getStatusInfo().getReasonPhrase(), null);
				}
			}

			List<Archive> archives = response.readEntity(new GenericType<List<Archive>>() {
			});
			return archives;
		} catch (ClientException che) {
			throw che;
		} catch (ProcessingException pe) {
			throw getConnectionException(pe);
		} catch (Exception ex) {
			throw new ClientException(500, ex.getMessage(), ex);
		}
	}

	private List<Application> getApplications(final String domainName, final String appSpace, final String filter,
			final boolean status) throws ClientException {
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
			if (!response.getStatusInfo().getFamily().equals(Family.SUCCESSFUL)) {
				com.tibco.bw.maven.plugin.admin.dto.Error error = response
						.readEntity(com.tibco.bw.maven.plugin.admin.dto.Error.class);
				if (error != null) {
					throw new ClientException(response.getStatus(), error.getCode() + ": " + error.getMessage(), null);
				} else {
					throw new ClientException(response.getStatus(), response.getStatusInfo().getReasonPhrase(), null);
				}
			}

			List<Application> apps = response.readEntity(new GenericType<List<Application>>() {
			});
			return apps;
		} catch (ClientException che) {
			throw che;
		} catch (ProcessingException pe) {
			throw getConnectionException(pe);
		} catch (Exception ex) {
			throw new ClientException(500, ex.getMessage(), ex);
		}
	}

	private static ClientException getConnectionException(ProcessingException pe) {
		if (pe.getCause() instanceof ConnectException) {
			return new ClientException(503, pe.getCause().getMessage(), pe.getCause());
		}
		// https://java.net/jira/browse/JERSEY-2728
		if (pe.getCause() instanceof IllegalStateException) {
			return new ClientException(503, pe.getCause().getMessage(), pe.getCause());
		}
		return new ClientException(500, pe.getMessage(), pe);
	}

	public List<Installation> getInstallations() {
		// TODO Auto-generated method stub
		return null;
	}

}

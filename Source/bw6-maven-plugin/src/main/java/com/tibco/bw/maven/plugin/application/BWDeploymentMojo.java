package com.tibco.bw.maven.plugin.application;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.jar.Manifest;

import org.apache.commons.io.FileUtils;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import com.tibco.bw.maven.plugin.admin.client.RemoteDeployer;
import com.tibco.bw.maven.plugin.admin.dto.Agent;
import com.tibco.bw.maven.plugin.admin.dto.AppSpace;
import com.tibco.bw.maven.plugin.admin.dto.AppSpace.AppSpaceRuntimeStatus;
import com.tibco.bw.maven.plugin.osgi.helpers.ManifestParser;
import com.tibco.bw.maven.plugin.platform.client.PlatformDeployer;
import com.tibco.bw.maven.plugin.tci.client.TCIDeployer;
import com.tibco.bw.maven.plugin.utils.BWFileUtils;
import com.tibco.bw.maven.plugin.utils.Constants;

@Mojo(name = "bwdeploy", defaultPhase = LifecyclePhase.DEPLOY)
public class BWDeploymentMojo extends AbstractMojo {
	@Parameter(defaultValue="${session}", readonly=true)
	private MavenSession session;

	@Parameter(defaultValue="${project}", readonly=true)
	private MavenProject project;

	@Parameter(property="project.build.directory")
	private File outputDirectory;

	@Parameter(property="project.basedir")
	private File projectBasedir;

	@Parameter(property="project.type")
	private String projectType;

	@Parameter(property="deployToAdmin")
	private boolean deployToAdmin; 

	@Parameter(property="agentHost")
	private String agentHost;

	@Parameter(property="agentPort")
	private String agentPort;

	@Parameter(property="agentAuth")
	private String agentAuth;

	@Parameter(property="agentUsername")
	private String agentUsername;

	@Parameter(property="agentPassword")
	private String agentPassword;

	@Parameter(property="agentSSL")
	private boolean agentSSL;

	@Parameter(property="truststorePath")
	private String trustPath;

	@Parameter(property="truststorePassword")
	private String trustPassword;

	@Parameter(property="keystorePath")
	private String keyPath;

	@Parameter(property="keystorePassword")
	private String keyPassword;

	@Parameter(property="domain")
	private String domain;

	@Parameter(property="domainDesc")
	private String domainDesc;

	@Parameter(property="appSpace")
	private String appSpace;

	@Parameter(property="appSpaceDesc")
	private String appSpaceDesc;

	@Parameter(property="appNode")
	private String appNode;

	@Parameter(property="appNodeDesc")
	private String appNodeDesc;

	@Parameter(property="httpPort")
	private String httpPort;

	@Parameter(property="osgiPort")
	private String osgiPort;

	@Parameter(property="profile")
	private String profile;

	@Parameter(property="redeploy")
	private boolean redeploy;

	@Parameter(property="backup")
	private boolean backup;

	@Parameter(property="externalProfile")
	private boolean externalProfile;

	@Parameter(property="externalProfileLoc")
	private String externalProfileLoc;

	@Parameter(property="backupLocation")
	private String backupLocation;

	@Parameter(property="deploymentConfigfile")
	private String deploymentConfigfile;

	@Parameter(property = "externalEarLoc")
	private String externalEarLoc;

	@Parameter(property="skipUploadArchive")
	private boolean skipUploadArchive;

	@Parameter(property = "createAdminCompo" , defaultValue = "true" )
	private boolean createAdminCompo;

	@Parameter(property = "appNodeConfig")
	protected Map appNodeConfig;

	@Parameter(property="restartAppNode")
	private boolean restartAppNode;

	@Parameter(property = "earUploadPath")
	private String earUploadPath;

	@Parameter(property="retryCount", defaultValue = "50")
	private int retryCount;

	@Parameter(property="connectTimeout", defaultValue = "120000")
	private int connectTimeout;

	@Parameter(property="readTimeout", defaultValue = "120000")
	private int readTimeout;

	@Parameter(property="instanceCount", defaultValue = "0")
	private int instanceCount;

	@Parameter(property = "appVariablesFile")
	private String appVariablesFile;

	@Parameter(property = "engineVariablesFile")
	private String engineVariablesFile;

	@Parameter(property = "forceOverwrite", defaultValue = "false")
	private boolean forceOverwrite;

	@Parameter(property = "retainAppProps", defaultValue = "false")
	private boolean retainAppProps;

	@Parameter(property = "startOnDeploy", defaultValue = "true")
	private boolean startOnDeploy;

	@Parameter(property="startOnly", defaultValue ="false")
	private boolean startOnly;

	@Parameter(property="stopOnly", defaultValue ="false")
	private boolean stopOnly;

	@Parameter(property = "buildName")
	private String buildName;

	@Parameter(property = "appName")
	private String appName;

	@Parameter(property="replicas", defaultValue = "0")
	private int replicas;

	@Parameter(property="enableAutoScaling", defaultValue ="false")
	private boolean enableAutoScaling;

	@Parameter(property="enableServiceMesh", defaultValue ="false")
	private boolean enableServiceMesh;

	@Parameter(property="eula", defaultValue ="false")
	private boolean eula;

	@Parameter(property = "platformConfigFile")
	private String platformConfigFile;

	@Parameter(property = "dpUrl")
	private String dpUrl;

	@Parameter(property = "authToken")
	private String authToken;

	@Parameter(property = "baseVersion")
	private String baseVersion;

	@Parameter(property = "baseImageTag")
	private String baseImageTag;

	@Parameter(property = "namespace")
	private String namespace;

	@Parameter(property="platformBuild", defaultValue ="false")
	private boolean platformBuild;

	@Parameter(property="platformDeploy", defaultValue ="false")
	private boolean platformDeploy;

	@Parameter(property="platformScale", defaultValue ="false")
	private boolean platformScale;

	@Parameter(property="platformUpgrade", defaultValue ="false")
	private boolean platformUpgrade;
	
	@Parameter(property="platformDeployViaHelm", defaultValue ="false")
	private boolean platformDeployViaHelm;
	
	@Parameter(property = "valuesYamlPath")
	private String valuesYamlPath;

	@Parameter(property = "appId")
	private String appId;

	@Parameter(property = "buildId")
	private String buildId;

	private String earLoc;
	private String earName;
	private String applicationName;

	public void execute() throws MojoExecutionException {
		try {    		
			getLog().info("BW Deployment Mojo started ...");
			Manifest manifest = ManifestParser.parseManifest(projectBasedir);
			String bwEdition = manifest.getMainAttributes().getValue(Constants.TIBCO_BW_EDITION);
			//TCI deployment
			if(projectType != null && projectType.equalsIgnoreCase(Constants.TCI)){
				if(!deployToAdmin) {
					getLog().info("Deploy To Admin/TCI is set to False. Skipping EAR Deployment.");
					return;
				}
				File [] files = BWFileUtils.getFilesForType(outputDirectory, ".ear");
				if(files.length == 0) {
					throw new Exception("EAR file not found for the Application");
				}
				String appName = manifest.getMainAttributes().getValue(Constants.BUNDLE_SYMBOLIC_NAME);

				TCIDeployer deployer = new TCIDeployer(connectTimeout, readTimeout, retryCount, getLog());

				deployer.deployApp(appName, files[0].getPath(), instanceCount, appVariablesFile, engineVariablesFile, forceOverwrite, retainAppProps);

				deployer.close();
			}else if(projectType != null && projectType.equalsIgnoreCase(Constants.Platform)) {
				//Platform deployment
				PlatformDeployer deployer = new PlatformDeployer(connectTimeout, readTimeout, retryCount, getLog());
				if(platformBuild || platformDeploy || platformScale || platformUpgrade) {
					if(platformBuild) {
						File [] files = BWFileUtils.getFilesForType(outputDirectory, ".ear");
						if(files.length == 0) {
							throw new Exception("EAR file not found for the Application");
						}
						deployer.buildApp(files[0].getPath(), buildName, appName, profile, replicas, enableAutoScaling, enableServiceMesh, eula, platformConfigFile, dpUrl, authToken, baseVersion, baseImageTag, namespace, false);
					}else if(platformDeploy) {
						deployer.deployApp(dpUrl, buildId, namespace, authToken, eula, appName, profile, platformConfigFile, enableAutoScaling, enableServiceMesh);
					}else if(platformScale) {
						deployer.scaleApp(dpUrl, appId, replicas, authToken, namespace);
					}else if(platformUpgrade) {
						deployer.upgradeApp(dpUrl, appId, buildId, namespace, authToken, eula, appName, profile, platformConfigFile, enableAutoScaling, enableServiceMesh);
					}
				}else if(platformDeployViaHelm) {
					if(valuesYamlPath != null && !valuesYamlPath.isEmpty()) {
						File valuesYaml = new File(valuesYamlPath);
						if(valuesYaml.exists()) {
							deployer.deployAppUsingHelmCharts(dpUrl, authToken, namespace, valuesYaml);
						}else {
							throw new Exception("values.yaml file does not exist at the specified path.");
						}
					}else {
						throw new Exception("Please specify path for values.yaml file.");
					}
					
				}else {
					File [] files = BWFileUtils.getFilesForType(outputDirectory, ".ear");
					if(files.length == 0) {
						throw new Exception("EAR file not found for the Application");
					}
					deployer.buildApp(files[0].getPath(), buildName, appName, profile, replicas, enableAutoScaling, enableServiceMesh, eula, platformConfigFile, dpUrl, authToken, baseVersion, baseImageTag, namespace, true);
			}
			}else {
				//enterprise deployment
				boolean configFileExists = deploymentConfigExists();
				if(configFileExists) {
					loadFromDeploymentProperties();
				}
				if(!validateFields()) {
					getLog().error("Validation failed. Skipping EAR Deployment.");
					return;
				}
				if(!deployToAdmin) {
					getLog().info("Deploy To Admin/TCI is set to False. Skipping EAR Deployment.");
					return;
				}

				File [] files = BWFileUtils.getFilesForType(outputDirectory, ".ear");
				if(files.length == 0) {
					throw new Exception("EAR file not found for the Application");
				}


				applicationName = manifest.getMainAttributes().getValue(Constants.BUNDLE_SYMBOLIC_NAME);

				RemoteDeployer deployer = new RemoteDeployer(agentHost, Integer.parseInt(agentPort), agentAuth, agentUsername, agentPassword, agentSSL, trustPath, trustPassword, keyPath, keyPassword, createAdminCompo, connectTimeout, readTimeout, retryCount,startOnDeploy);
				deployer.setLog(getLog());

				List<Agent> agents = deployer.getAgentInfo();
				if(agents.size() > 0) {
					getLog().info("Connected to BWAgent. Agents found.");
				} else {
					return;
				}
				String agentName = null;
				for(Agent agent : agents) {
					agentName = agent.getName();
					getLog().info("Agent Name -> " + agentName);
				}
				String[] versionNum = manifest.getMainAttributes().getValue(Constants.BUNDLE_VERSION).split("\\.");
				String version = null;
				if(versionNum.length > 2)
					version =  versionNum[0]+"."+versionNum[1];
				else 
					throw new Exception("Invalid Bundle Version -"+ manifest.getMainAttributes().getValue("Bundle-Version"));

				File selectedFile = files[0];
				if (files.length > 1)
					selectedFile = selectEARversion(files, version);
				if(externalEarLocExists()){
					File f = new File(externalEarLoc);
					Path p = Paths.get(externalEarLoc + "/" +selectedFile.getName());

					Files.deleteIfExists(p);
					FileUtils.copyFileToDirectory(files[0], f);
					deriveEARInformation(p.toFile());
				} else {
					deriveEARInformation(selectedFile);
				}

				deployer.getOrCreateDomain(domain, domainDesc);
				AppSpace appSpaceDto = deployer.getOrCreateAppSpace(domain, appSpace, appSpaceDesc);
				deployer.getOrCreateAppNode(domain, appSpace, appNode, Integer.parseInt(httpPort), osgiPort == null || osgiPort.isEmpty() ? -1 : Integer.parseInt(osgiPort), appNodeDesc, agentName);
				if(!appNodeConfig.isEmpty())
				{
					//Set AppNode config
					getLog().debug("Input AppNode Config : "+ appNodeConfig);
					deployer.setAppNodeConfig(domain,appSpace,appNode,appNodeConfig, restartAppNode);
				}

				if(appSpaceDto.getStatus() != AppSpaceRuntimeStatus.Running) {
					deployer.startAppSpace(domain, appSpace);
				} else {
					getLog().info("AppSpace is Running.");
				}
				getLog().info("domain -> " + domain + " earName -> " + earName + " Ear file to be uploaded -> " + selectedFile.getAbsolutePath());
				deployer.addAndDeployApplication(domain, appSpace, applicationName, earName, selectedFile.getAbsolutePath(), redeploy, profile, 
						backup, backupLocation,version,externalProfile,externalProfileLoc, appNode, earUploadPath, skipUploadArchive, startOnly, stopOnly);
				deployer.close();
			}
		} catch(Exception e) {
			getLog().error(e);
			throw new MojoExecutionException("Failed to deploy BW Application ", e);
		}
	}

	private File selectEARversion(File[] files, String version) {
		for (File f: files) {
			String name = f.getName();
			if (name.indexOf(version) >= 0)
				return f;
		}

		// can't find it, use first one like before
		return files[0];

	}

	private void deriveEARInformation(File file) {
		earLoc = file.getAbsolutePath();
		earLoc = earLoc.replace("\\", "/");
		earName = file.getName();
	}

	private boolean deploymentConfigExists() {
		if(deploymentConfigfile == null || deploymentConfigfile.isEmpty()) {
			getLog().info("No Deployment Config File set. Reading the deployment Properties from POM File.");
			return false;
		}
		String deploymentFile = deploymentConfigfile;
		if(deploymentFile.contains("http") && isValidURL(deploymentFile)){
			getLog().info("External Deployment Config file provided. Loading configuration from the same.");	
			return true;
		} else {
			File file = new File(deploymentFile);
			if(!file.exists()) {
				getLog().info("Deployment Config File not found. Reading the deployment Properties from POM File.");
				return false;
			}
			getLog().info("Deployment Config File found. Loading configuration from the same.");	
			return true;
		}
	}

	private boolean isValidURL(String url){
		try { 
			new URL(url).toURI(); 
			return true; 
		} catch (Exception e) { 
			return false; 
		} 
	}

	private boolean externalEarLocExists() {
		if(externalEarLoc == null || externalEarLoc.isEmpty()){
			return false;
		}
		getLog().info("Deploying the Ear from external Ear location: " + externalEarLoc);
		return true;
	}

	private void loadFromDeploymentProperties() throws MalformedURLException, IOException {
		File file = null;
		if(deploymentConfigfile.contains("http") && isValidURL(deploymentConfigfile)){
			String localFileName = deploymentConfigfile.substring(deploymentConfigfile.lastIndexOf("/")+1);
			getLog().info("Deployment config file is from external URL, creating temporary local file - "+ localFileName);
			file = new File(localFileName);
			file.delete();
			file.createNewFile();
			FileUtils.copyURLToFile(new URL(deploymentConfigfile), file);
		} else {
			file = new File(deploymentConfigfile);
		}
		Properties deployment = new Properties();
		FileInputStream stream = null;
		try {
			stream = new FileInputStream(file);
			deployment.load(stream);	
		} catch(Exception e) {
			e.printStackTrace();
			getLog().info("Failed to load Propeties from Deployment Config File");
		} finally {
			if(stream != null) {
				try {
					stream.close();
				} catch(IOException e) {
					e.printStackTrace();
				}
			}
		}
		try {
			agentHost = deployment.getProperty("agentHost");
			agentPort = deployment.getProperty("agentPort");
			agentAuth = deployment.getProperty("agentAuth");
			agentUsername = deployment.getProperty("agentUsername");
			agentPassword = deployment.getProperty("agentPassword");
			agentSSL = Boolean.parseBoolean(deployment.getProperty("agentSSL"));
			trustPath = deployment.getProperty("truststorePath");
			trustPassword = deployment.getProperty("truststorePassword");
			keyPath = deployment.getProperty("keystorePath");
			keyPassword = deployment.getProperty("keystorePassword");
			domain = deployment.getProperty("domain");
			domainDesc = deployment.getProperty("domainDesc");
			appSpace = deployment.getProperty("appSpace");
			appSpaceDesc = deployment.getProperty("appSpaceDesc");
			appNode = deployment.getProperty("appNode");
			appNodeDesc = deployment.getProperty("appNodeDesc");
			httpPort = deployment.getProperty("httpPort");
			osgiPort = deployment.getProperty("osgiPort");
			profile = deployment.getProperty("profile");
			deployToAdmin = Boolean.parseBoolean(deployment.getProperty("deployToAdmin"));
			redeploy = Boolean.parseBoolean(deployment.getProperty("redeploy"));
			backup = Boolean.parseBoolean(deployment.getProperty("backup"));
			backupLocation = deployment.getProperty("backupLocation");
			externalProfile=Boolean.parseBoolean(deployment.getProperty("externalProfile"));
			externalProfileLoc=deployment.getProperty("externalProfileLoc");
			externalEarLoc=deployment.getProperty("externalEarLoc");
			earUploadPath = deployment.getProperty("earUploadPath");
			skipUploadArchive = Boolean.parseBoolean(deployment.getProperty("skipUploadArchive"));
			startOnly = Boolean.parseBoolean(deployment.getProperty("startOnly"));
			stopOnly = Boolean.parseBoolean(deployment.getProperty("stopOnly"));
			getAppNodeConfigProps(deployment);
		} catch(Exception e) {
			deployToAdmin = false;
			getLog().error(e);
			getLog().info("Error in Loading Deployment Properties. Skipping EAR Deployment.");
		}
	}

	@SuppressWarnings("unchecked")
	private void getAppNodeConfigProps(Properties deployment){
		for(Object propKey : deployment.keySet()){
			if(((String)propKey).startsWith("appNodeConfig_")){
				String key = ((String)propKey).split("_")[1];
				if(appNodeConfig == null)
					appNodeConfig = new HashMap<String, String>();
				if(key != null){
					appNodeConfig.put(key, deployment.getProperty((String)propKey));
					getLog().info("AppNodeConfig -> "+ key + " : "+ deployment.getProperty((String)propKey));
				}
			}
		}
	}

	private boolean validateFields() {
		StringBuffer errorMessage = new StringBuffer();
		boolean isValidHost = agentHost != null && !agentHost.isEmpty();
		if(!isValidHost) {
			errorMessage.append("[Agent Host value is required]");
		}

		boolean isValidPort = false;
		try {
			if(agentPort == null || agentPort.isEmpty()) {
				errorMessage.append("[Agent Port value is required]");
			} else if(Integer.parseInt(agentPort) <= 0) {
				errorMessage.append("[Agent Port value must be an Integer]");
			} else if(Integer.parseInt(agentPort) > 65535) {
				errorMessage.append("[Agent Port value is invalid]");
			} else {
				isValidPort = true;
			}
		} catch(Exception e) {
			errorMessage.append("[Agent Port value must be an Integer]");
		}

		boolean isValidDomain = domain != null && !domain.isEmpty();
		if(!isValidDomain) {
			errorMessage.append("[Domain Value is required]");
		}

		boolean isValidAppSpace = appSpace != null && !appSpace.isEmpty(); 
		if(!isValidAppSpace) {
			errorMessage.append("[AppSpace Value is required]");
		}

		boolean isValidAppNode = appNode != null && !appNode.isEmpty();
		if(!isValidAppNode) {
			errorMessage.append("[AppNode Value is required]");
		}

		boolean isValidHTTPPort = false;
		try {
			if(httpPort == null || httpPort.isEmpty())	{
				errorMessage.append("[HTTP Port value is required]");
			} else if(Integer.parseInt(httpPort) < 0) {
				errorMessage.append("[HTTP Port value must be an Integer]");
			} else {
				isValidHTTPPort = true;
			}
		} catch(Exception e) {
			errorMessage.append("[HTTP Port value must be an Integer]");
		}

		boolean isValidOSGi = false;
		try	{
			if(osgiPort == null || osgiPort.isEmpty()) {
				isValidOSGi = true;
			} else if(Integer.parseInt(osgiPort) < 0) {
				isValidOSGi = false;
				errorMessage.append("[OSGi Port value must be an Integer]");
			} else {
				isValidOSGi = true;
			}
		} catch(Exception e) {
			errorMessage.append("[OSGi Port value must be an Integer]");
		}

		boolean isValidBackupLoc = true;
		if(backup && (backupLocation == null || backupLocation.isEmpty())) {
			isValidBackupLoc = false;
			errorMessage.append("[Backup Location value is required]");
		}

		boolean isValidexternalProfileLoc = true;
		// fix from runtime side only
		// UI doesn't seem to fix the externalProfile flag when profile is set to an application Profile 
		// We should give error only if profile is set to "other" and not an interal application profile
		if(externalProfile && externalProfileLoc.isEmpty() && (profile != null && profile.equals("other")) ) {
			isValidexternalProfileLoc = false;
			errorMessage.append("[external Profile Location value is required]");
		}

		boolean isValidCredential = true;
		if(agentAuth != null && (Constants.BASIC_AUTH.equalsIgnoreCase(agentAuth) || Constants.DIGEST_AUTH.equalsIgnoreCase(agentAuth))) {
			if(agentUsername == null || agentUsername.isEmpty()) {
				isValidCredential = false;
				errorMessage.append("[Agent Username value is required]");
			}
			if(agentPassword == null || agentPassword.isEmpty()) {
				isValidCredential = false;
				errorMessage.append("[Agent Password value is required]");
			}
		}

		boolean isValidSSL = true;
		if(agentSSL) {
			if(trustPath == null || trustPath.isEmpty()) {
				isValidSSL = false;
				errorMessage.append("[Truststore File Path value is required]");
			}
			if(trustPassword == null || trustPassword.isEmpty()) {
				isValidSSL = false;
				errorMessage.append("[Truststore Password value is required]");
			}
		}

		if(!errorMessage.toString().isEmpty()) {
			getLog().error(errorMessage.toString());
			return false;
		}

		if(isValidHost && isValidPort && isValidDomain && isValidAppSpace && isValidAppNode && isValidHTTPPort && isValidOSGi && isValidBackupLoc && isValidCredential && isValidSSL && isValidexternalProfileLoc) {
			return true;
		}
		return false;
	}
}
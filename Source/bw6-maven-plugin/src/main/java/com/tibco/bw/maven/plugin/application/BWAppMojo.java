package com.tibco.bw.maven.plugin.application;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.jar.Manifest;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import com.tibco.bw.maven.plugin.admin.client.RemoteDeployer;
import com.tibco.bw.maven.plugin.admin.dto.Agent;
import com.tibco.bw.maven.plugin.osgi.helpers.ManifestParser;
import com.tibco.bw.maven.plugin.utils.BWProjectUtils;
import com.tibco.bw.maven.plugin.utils.Constants;

@Mojo(name = "bwapp")
public class BWAppMojo extends AbstractMojo {
	
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
	
	@Parameter(property="appspaceDeployment", defaultValue ="false")
	private boolean appspaceDeployment;
	
	@Parameter(property="platformDeployment", defaultValue ="false")
	private boolean platformDeployment;
	
	@Parameter(property="start", defaultValue ="false")
	private boolean start;

	@Parameter(property="stop", defaultValue ="false")
	private boolean stop;
	
	private String applicationName;
	
	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		try {
			getLog().info("BW App Mojo started ...");
			if(project == null || !BWProjectUtils.isAplicationProject(project)) {
				throw new Exception("Please select a BW application project to run this goal.");
			}
			if(start && stop) {
				throw new Exception("Please provide only one of the start and stop flags while running this goal.");
			}
			if(!start && !stop) {
				throw new Exception("Please provide one of the start and stop flags with the value as true while running this goal.");
			}
			Manifest manifest = ManifestParser.parseManifest(projectBasedir);
			String bwEdition = manifest.getMainAttributes().getValue(Constants.TIBCO_BW_EDITION);
			if(appspaceDeployment) {
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
				if(versionNum.length > 2) {
					version =  versionNum[0]+"."+versionNum[1];
				}else {
					throw new Exception("Invalid Bundle Version -"+ manifest.getMainAttributes().getValue("Bundle-Version"));
				}
				if(start) {
					getLog().info("Starting the application -> " + applicationName);
					deployer.startApplication(domain, appSpace, applicationName, version, appNode, true);
					getLog().info("Application \"" + applicationName + "\" started.");
				}else if(stop) {
					getLog().info("Stopping the application -> " + applicationName);
					deployer.stopApplication(domain, appSpace, applicationName, version, appNode, true);
					getLog().info("Application \"" + applicationName + "\" stopped.");
				}
				deployer.close();
			}else if(platformDeployment) {
				
			}
		}catch(Exception e) {
			getLog().error(e);
			throw new MojoExecutionException("Failed to perform operations on the deployed app: ", e);
		}
	}

}

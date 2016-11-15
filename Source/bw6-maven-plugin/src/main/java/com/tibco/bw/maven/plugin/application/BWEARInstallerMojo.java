package com.tibco.bw.maven.plugin.application;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.jar.Manifest;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import com.tibco.bw.maven.plugin.admin.client.RemoteDeployer;
import com.tibco.bw.maven.plugin.admin.dto.Agent;
import com.tibco.bw.maven.plugin.admin.dto.AppSpace;
import com.tibco.bw.maven.plugin.admin.dto.AppSpace.AppSpaceRuntimeStatus;
import com.tibco.bw.maven.plugin.osgi.helpers.ManifestParser;
import com.tibco.bw.maven.plugin.utils.BWFileUtils;
import com.tibco.bw.maven.plugin.utils.Constants;

@Mojo(name = "bwinstall", defaultPhase = LifecyclePhase.INSTALL)
public class BWEARInstallerMojo extends AbstractMojo {
    @Component
    private MavenSession session;

    @Component
    private MavenProject project;

	@Parameter(property="project.build.directory")
    private File outputDirectory;

	@Parameter(property="project.basedir")
	private File projectBasedir;

	@Parameter(property="deployToAdmin")
	private boolean deployToAdmin; 

	@Parameter(property="agentHost")
	private String agentHost;

	@Parameter(property="agentPort")
	private String agentPort;
	
	@Parameter(property="agentUser")
	private String agentUser;
	
	@Parameter(property="agentPass")
	private String agentPass;

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

	@Parameter(property="backupLocation")
	private String backupLocation;

	@Parameter(property="deploymentConfigfile")
	private String deploymentConfigfile;

	private String earLoc;
	private String earName;
	private String applicationName;
	//private String applicationVersion;

    public void execute() throws MojoExecutionException {
    	try {    		
    		getLog().info("BWEAR Installer Mojo started ...");
    		Manifest manifest = ManifestParser.parseManifest(projectBasedir);
    		String bwEdition = manifest.getMainAttributes().getValue(Constants.TIBCO_BW_EDITION);
            if(bwEdition != null && bwEdition.equals(Constants.BWCF)) {
            	getLog().debug("BWCF edition. Returning..");
            	return;
            }
    		boolean configFileExists = deploymentConfigExists();
    		if(configFileExists) {
    			loadFromDeploymentProperties();
    		}
    		if(!validateFields()) {
    			getLog().error("Validation failed. Skipping EAR Deployment.");
    			return;
    		}
    		if(!deployToAdmin) {
    			getLog().info("Deploy To Admin is set to False. Skipping EAR Deployment.");
    			return;
    		}

    		File [] files = BWFileUtils.getFilesForType(outputDirectory, ".ear");
    		if(files.length == 0) {
    			throw new Exception("EAR file not found for the Application");
    		}

    		deriveEARInformation(files[0]);
    		applicationName = manifest.getMainAttributes().getValue(Constants.BUNDLE_SYMBOLIC_NAME);
<<<<<<< HEAD
    		RemoteDeployer deployer=null;
    		if (agentUser.length()>0)
    		{
    			deployer = new RemoteDeployer(agentHost, agentPort,agentUser,agentPass);
    		}
    		else
    		{
    			deployer = new RemoteDeployer(agentHost, agentPort);	
    		}
=======

    		RemoteDeployer deployer = new RemoteDeployer(agentHost, agentPort);
>>>>>>> upstream2/master
    		deployer.setLog(getLog());

    		List<Agent> agents = deployer.getAgentInfo();
    		if(agents.size() > 0) {
    			getLog().info("Connected to BWAgent. Agents found.");
    		} else {
    			return;
    		}

        	for(Agent agent : agents) {
        		getLog().info("Agent Name -> " + agent.getName());
        	}

    		deployer.getOrCreateDomain(domain, domainDesc);
    		AppSpace appSpaceDto = deployer.getOrCreateAppSpace(domain, appSpace, appSpaceDesc);
    		deployer.getOrCreateAppNode(domain, appSpace, appNode, Integer.parseInt(httpPort), osgiPort == null || osgiPort.isEmpty() ? -1 : Integer.parseInt(osgiPort), appNodeDesc);
    		if(appSpaceDto.getStatus() != AppSpaceRuntimeStatus.Running) {
    			deployer.startAppSpace(domain, appSpace);
    		} else {
    			getLog().info("AppSpace is Running.");
    		}
    		deployer.addAndDeployApplication(domain, appSpace, applicationName, earName, files[0].getAbsolutePath(), redeploy, profile, backup, backupLocation);    		
    	} catch(Exception e) {
    		getLog().error(e);
    	}
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
		File file = new File(deploymentFile);
		if(!file.exists()) {
			getLog().info("Deployment Config File not found. Reading the deployment Properties from POM File.");
			return false;
		}
		getLog().info("Deployment Config File found. Loading configuration from the same.");
		return true;
	}

	private void loadFromDeploymentProperties() {
		File file = new File(deploymentConfigfile);
		Properties deployment = new Properties();
		FileInputStream stream = null;
		try {
			stream = new FileInputStream(file);
			deployment.load(stream);	
		} catch(Exception e) {
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
		} catch(Exception e) {
			deployToAdmin = false;
			getLog().error(e);
			getLog().info("Error in Loading Deployment Properties. Skipping EAR Deployment.");
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
			} else if(Integer.parseInt(agentPort) < 0) {
				errorMessage.append("[Agent Port value must be an Integer]");
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
		if(backup && backupLocation.isEmpty()) {
			isValidBackupLoc = false;
			errorMessage.append("[Backup Location value is required]");
		}

		if(!errorMessage.toString().isEmpty()) {
			getLog().error(errorMessage.toString());
			return false;
		}

		if(isValidHost && isValidPort && isValidDomain && isValidAppSpace && isValidAppNode && isValidHTTPPort && isValidOSGi && isValidBackupLoc) {
			return true;
		}
		return false;
	}
}

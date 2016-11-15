package com.tibco.bw.maven.plugin.application;

import java.io.File;

import java.util.List;



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
import com.tibco.bw.maven.plugin.utils.BWFileUtils;

@Mojo(name = "bwbackupEAR", defaultPhase = LifecyclePhase.VERIFY)
public class BWEARBackupMojo
extends AbstractMojo

{

	@Parameter( property="project.build.directory")
	    private File outputDirectory;

	@Parameter( property="project.basedir")
		private File projectBasedir;

	@Component
	    private MavenSession session;

	@Component
	    private MavenProject project;

	@Parameter( property="deployToAdmin")
		private boolean deployToAdmin;

	@Parameter( property="agentHost")
		private String agentHost;

	@Parameter( property="agentPort")
		private String agentPort;
	
	@Parameter(property="agentUser")
	private String agentUser;
	
	@Parameter(property="agentPass")
	private String agentPass;

	@Parameter( property="domain")
		private String domain;

	@Parameter( property="domainDesc")
		private String domainDesc;

	@Parameter( property="appSpace")
		private String appSpace;

	@Parameter( property="appSpaceDesc")
		private String appSpaceDesc;

	@Parameter( property="appNode")
		private String appNode;

	@Parameter( property="appNodeDesc")
		private String appNodeDesc;

	@Parameter( property="httpPort")
		private String httpPort;

	@Parameter( property="osgiPort")
		private String osgiPort;

	@Parameter( property="profile")
		private String profile;

	@Parameter( property="redeploy")
		private boolean redeploy;

	@Parameter(property = "deploymentConfigfile")
	private String deploymentConfigfile;
	
	@Parameter(property="backupLocation")
	private String backupLocation;

	private String earLoc;

	private String earName;
	
	@Parameter(property = "app.name")
	private String applicationName;

	private String applicationVersion;

	public void execute() throws MojoExecutionException
	    {
	    	try 
	    	{    		
	    		getLog().info("BWEAR Backup Mojo started ...");
	    		
	    		RemoteDeployer deployer = null;
	    		if (agentUser.length()>0)
	    		{
	    			deployer = new RemoteDeployer(agentHost, agentPort,agentUser,agentPass);
	    		}
	    		else
	    		{
	    			deployer = new RemoteDeployer(agentHost, agentPort);	
	    		}
	    		deployer.setLog(getLog());
	    		
	    		List<Agent> agents = deployer.getAgentInfo();
	    		if( agents.size() > 0 )
	    		{
	    			getLog().info( "Connected to BWAgent. Agents found ");
	    		}
	    		else
	    		{
	    			return;
	    		}
	    		
	        	for(Agent agent : agents )
	        	{
	        		getLog().info( "Agent Name -> " + agent.getName() );
	        	}
	    		
	        	//deployer.backupApplication(domain, appSpace, applicationName, earName, true , profile);
	        	getLog().info("Generating backup ear file for application -> " + applicationName);
	        	deployer.downloadArchive(domain, backupLocation, applicationName);
				getLog().info("Generating backup substvar file for profile -> " +profile);
				deployer.downloadProfileAplication(domain, backupLocation, applicationName, profile);
	        		    		
	    	}
	    	catch(Exception e )
	    	{
	    		getLog().error( e );
	    	}
	    }



}

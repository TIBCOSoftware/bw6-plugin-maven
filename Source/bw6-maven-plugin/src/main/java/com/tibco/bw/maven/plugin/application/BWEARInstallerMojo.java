package com.tibco.bw.maven.plugin.application;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.jar.Manifest;

import javax.enterprise.inject.Stereotype;

import org.apache.commons.io.FilenameUtils;
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
import com.tibco.bw.maven.plugin.admin.dto.AppNode;
import com.tibco.bw.maven.plugin.admin.dto.AppSpace;
import com.tibco.bw.maven.plugin.admin.dto.AppSpace.AppSpaceRuntimeStatus;
import com.tibco.bw.maven.plugin.admin.dto.Domain;
import com.tibco.bw.maven.plugin.osgi.helpers.ManifestParser;
import com.tibco.bw.maven.plugin.utils.BWFileUtils;

@Mojo( name = "bwinstall", defaultPhase = LifecyclePhase.INSTALL )
public class BWEARInstallerMojo extends AbstractMojo
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

	@Parameter( property="deploymentConfigfile")
	private String deploymentConfigfile;

	private String earLoc;

	private String earName;
    
	private String applicationName;
    
	private String applicationVersion;

	

    public void execute() throws MojoExecutionException
    {
    	try 
    	{    		
    		getLog().info("BWEAR Installer Mojo started ...");
    		
    		boolean configFileExists = deploymentConfigExists();
    		if( configFileExists )
    		{
    			loadFromDeploymentProperties();
    		}
    		
    		if ( !validateFields() )
    		{
    			getLog().error( "Validation failed. Skipping EAR Deployment.");
    			return;
    		}
    		
    		if( !deployToAdmin )
    		{
    			getLog().info( "Deploy To Admin is set to False. Skipping EAR Deployment"  );
    			return;
    		}
    		
    		File [] files = BWFileUtils.getFilesForType(outputDirectory, ".ear");
    		if( files.length == 0 )
    		{
    			throw new Exception( "EAR file not found for the Application" );
    		}

    		deriveEARInformation(files[0]);
    	
    		Manifest manifest = ManifestParser.parseManifest(projectBasedir) ;
    		applicationName = manifest.getMainAttributes().getValue("Bundle-SymbolicName");
    		 
    		RemoteDeployer deployer = new RemoteDeployer(agentHost, agentPort);
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
    		
    		Domain domainDto = deployer.getOrCreateDomain(domain, domainDesc);
    		AppSpace appSpaceDto = deployer.getOrCreateAppSpace(domain, appSpace, appSpaceDesc);
    		AppNode nodeDto = deployer.getOrCreateAppNode(domain, appSpace, appNode , Integer.parseInt(httpPort), osgiPort == null || osgiPort.isEmpty() ?  -1 : Integer.parseInt(osgiPort), appNodeDesc );
    		if( appSpaceDto.getStatus() != AppSpaceRuntimeStatus.Running )
    		{
    			deployer.startAppSpace(domain, appSpace);	
    		}
    		else
    		{
    			getLog().info("AppSpace is Running.");
    			
    		}
    		
    		deployer.addAndDeployApplication(domain, appSpace, applicationName, earName, files[0].getAbsolutePath(), true , profile);
    		
    	}
    	catch(Exception e )
    	{
    		getLog().error( e );
    	}
    }

    
    
	private void deriveEARInformation(File file) 
	{
		earLoc = file.getAbsolutePath();
		earLoc = earLoc.replace("\\", "/");
		
		earName = file.getName();
		
	}

	
	private boolean deploymentConfigExists()
	{

		if ( deploymentConfigfile == null || deploymentConfigfile.isEmpty() ) 
		{
			getLog().info( "No Deployment Config File set. Reading the deployment Properties from POM File.");
			
			return false;
			
		}
		
		String deploymentFile = deploymentConfigfile;
		
		File file = new File ( deploymentFile );
		
		if( !file.exists() )
		{
			getLog().info( "Deployment Config File not found. Reading the deployment Properties from POM File.");
			return false;
		}

		getLog().info( "Deployment Config File found. Loading configuration from the same" );
		
		return true;

	}
	
	private void loadFromDeploymentProperties()
	{
	
		
		
		File file = new File ( deploymentConfigfile );
		
		Properties deployment = new Properties();
		
		FileInputStream stream = null;
		try
		{
			stream = new FileInputStream(file);
			deployment.load( stream );	
		}
		catch(Exception e )
		{
			getLog().info( "Failed to load Propeties from Deployment Config File" );
		}
		 finally
		 {
			 if ( stream != null )
			 {
				 try {
					stream.close();
				} catch (IOException e) 
				 {

					e.printStackTrace();
				}
			 }
		 }
		
		
		try
		{
			
			
			agentHost = deployment.getProperty( "agentHost" );
			agentPort = deployment.getProperty( "agentPort");
			
			domain = deployment.getProperty( "domain" );
			domainDesc = deployment.getProperty( "domainDesc" );
			
			appSpace = deployment.getProperty( "appSpace" );
			appSpaceDesc = deployment.getProperty( "appSpaceDesc" );
			
			appNode = deployment.getProperty( "appNode");
			appNodeDesc = deployment.getProperty( "appNodeDesc" );
			
			httpPort = deployment.getProperty( "httpPort" );
			osgiPort = deployment.getProperty( "osgiPort" );
			
			profile = deployment.getProperty( "profile" );
			
			deployToAdmin = Boolean.parseBoolean(deployment.getProperty( "deployToAdmin" ));

			redeploy = Boolean.parseBoolean(deployment.getProperty( "redeploy" ));

		}
		catch( Exception e)
		{
			deployToAdmin = false;
			getLog().error( e );
			getLog().info( "Error in Loading Deployment Properties. Skipping EAR Deployment.");
		}
		
		
	}
	
	private boolean validateFields()
	{
		String errorMessage = "";
		boolean isValidHost = agentHost != null && !agentHost.isEmpty();
		if( !isValidHost )
		{
			errorMessage = errorMessage + "[Agent Host value is required]";
		}
		
		boolean isValidPort = false;
		try
		{
			if( agentPort == null || agentPort.isEmpty())
			{
				errorMessage = errorMessage + "[Agent Port value is required]";
			}
			else if( Integer.parseInt( agentPort ) < 0 )
			{
				errorMessage = errorMessage + "[Agent Port value must be an Integer Value]";
			}
			else
			{
				isValidPort = true;
			}
		}
		catch( Exception e )
		{
			errorMessage = errorMessage + "[Agent Port value must be an Integer Value]";
		}
		
		boolean isValidDomain = domain != null && !domain.isEmpty();
		if( !isValidDomain )
		{
			errorMessage = errorMessage + "[Domain Value is required]";
		}
		
		boolean isValidAppSpace = appSpace != null && !appSpace.isEmpty(); 
		if(!isValidAppSpace )
		{
			errorMessage = errorMessage + "[AppSpace Value is required]";
		}
		
		boolean isValidAppNode = appNode != null && !appNode.isEmpty();
		if( !isValidAppNode )
		{
			errorMessage = errorMessage + "[AppNode Value is required]";
		}
		
		
		boolean isValidHTTPPort = false;
		try
		{
			if( httpPort == null || httpPort.isEmpty())
			{
				errorMessage = errorMessage + "[HTTP Port value is required]";
			}
			else if( Integer.parseInt( httpPort ) < 0 )
			{
				errorMessage = errorMessage + "[HTTP Port value must be an Integer Value]";
			}
			else
			{
				isValidHTTPPort = true;
			}
		}
		
		
		
		catch( Exception e )
		{
			errorMessage = errorMessage + "[HTTP Port value must be an Integer Value]";
		}
		
		boolean isValidOSGi = false;
		
		try
		{
			if( osgiPort == null || osgiPort.isEmpty())
			{
				isValidOSGi = true;
			}
			else if( Integer.parseInt( osgiPort ) < 0 )
			{
				isValidOSGi = false;
				errorMessage = errorMessage + "[OSGi Port value must be an Integer Value]";
			}
			else
			{
				isValidOSGi = true;
			}
		}
		catch( Exception e )
		{
			errorMessage = errorMessage + "[OSGi Port value must be an Integer Value]";
		}
		
		
		if( !errorMessage.isEmpty() )
		{
			getLog().error( errorMessage );
			return false;
		}
		
		if( isValidHost && isValidPort && isValidDomain && isValidAppSpace && isValidAppNode && isValidHTTPPort && isValidOSGi )
		{
			return true;	
		}
		
		return false;

	}
	
}

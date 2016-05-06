package com.tibco.bw.studio.maven.pom.builders;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.maven.model.Build;
import org.apache.maven.model.Plugin;
import org.codehaus.plexus.util.xml.Xpp3Dom;

import com.tibco.bw.studio.maven.helpers.ManifestParser;
import com.tibco.bw.studio.maven.helpers.ModuleHelper;
import com.tibco.bw.studio.maven.modules.BWApplication;
import com.tibco.bw.studio.maven.modules.BWDeploymentInfo;
import com.tibco.bw.studio.maven.modules.BWModule;
import com.tibco.bw.studio.maven.modules.BWProject;
import com.tibco.zion.project.core.ContainerPreferenceProject;

public class ApplicationPOMBuilder extends AbstractPOMBuilder implements IPOMBuilder 
{
	
	private String bwEdition;
	
	@Override
	public void build(BWProject project, BWModule module) throws Exception
	{
		if( !module.isOverridePOM() )
		{
			return;
		}
		
		this.project = project;
		this.module = module;
		
		Map<String,String> manifest = ManifestParser.parseManifest(module.getProject());
		if(manifest.containsKey("TIBCO-BW-Edition") && manifest.get("TIBCO-BW-Edition").equals("bwcf")){
			String targetPlatform = ContainerPreferenceProject.getCurrentContainer().getLabel();
			if(targetPlatform.equals("Cloud Foundry")){
				  bwEdition="cf";
			  }else{
				  bwEdition="docker";
			  }
		}
		else{
			bwEdition="bw6";
		}
		
		initializeModel();
		
		addPrimaryTags();
		addParent( ModuleHelper.getParentModule( project.getModules() ));
		
		if(bwEdition.equals("cf")){
			addBWCloudFoundryProperties();
		}
		else if(bwEdition.equals("docker")){
			String platform = module.getBwDockerModule().getPlatform();
			addBWDockerProperties(platform);
		}
		addBuild();
		
		generatePOMFile();
		
	}
	
	
	
	@Override
	protected void addDeploymentDetails(Plugin plugin) 
	{
		if( !bwEdition.equals("bw6"))
		{
			return;
		}
	
		Properties properties = new Properties();

		plugin.setConfiguration(null);
		
		BWDeploymentInfo info = ((BWApplication)module).getDeploymentInfo();
		
		if( info == null || ! info.isDeployToAdmin() )
		{
			Xpp3Dom config = new Xpp3Dom("configuration");
			plugin.setConfiguration( config );
			model.getProperties().remove("deployToAdmin");
			model.getProperties().remove("agentHost");
			model.getProperties().remove("agentPort");
			model.getProperties().remove("domain");
			model.getProperties().remove("domainDesc");
			model.getProperties().remove("appSpace");
			model.getProperties().remove("appSpaceDesc");
			model.getProperties().remove("appNode");
			model.getProperties().remove("appNodeDesc");
			model.getProperties().remove("httpPort");
			model.getProperties().remove("osgiPort");
			model.getProperties().remove("redeploy");
			model.getProperties().remove("profile");
			return;
		}
		
		Xpp3Dom config = new Xpp3Dom("configuration");

		properties.put("deploymentConfig.file", "" );

		Xpp3Dom deployToAdmin  = new Xpp3Dom("deployToAdmin");
		deployToAdmin.setValue( "${deployToAdmin}" );
		model.addProperty("deployToAdmin", Boolean.toString(info.isDeployToAdmin()) );
		properties.put("deployToAdmin", Boolean.toString(info.isDeployToAdmin()) );

		Xpp3Dom agentHost  = new Xpp3Dom("agentHost");
		agentHost.setValue( "${agentHost}"  );
		model.addProperty("agentHost", info.getAgentHost() );		
		properties.put("agentHost", info.getAgentHost() );
				
		Xpp3Dom agentPort = new Xpp3Dom("agentPort");
		agentPort.setValue( "${agentPort}" );
		model.addProperty("agentPort", info.getAgentPort() );
		properties.put("agentPort", info.getAgentPort() );
		
		Xpp3Dom domain  = new Xpp3Dom("domain");
		domain.setValue( "${domain}" );
		model.addProperty("domain", info.getDomain() );
		properties.put("domain", info.getDomain() );
		
		Xpp3Dom domainDesc  = new Xpp3Dom("domainDesc");
		domainDesc.setValue( "${domainDesc}" );
		model.addProperty("domainDesc", info.getDomainDesc() );
		properties.put("domainDesc", info.getDomainDesc() );
		
		Xpp3Dom appspace  = new Xpp3Dom("appSpace");
		appspace.setValue( "${appSpace}" );
		model.addProperty("appSpace", info.getAppspace() );
		properties.put("appSpace", info.getAppspace() );
		
		Xpp3Dom appspaceDesc  = new Xpp3Dom("appSpaceDesc");
		appspaceDesc.setValue( "${appSpaceDesc}" );
		model.addProperty("appSpaceDesc", info.getAppspaceDesc() );
		properties.put("appSpaceDesc", info.getAppspaceDesc() );

		Xpp3Dom appnode  = new Xpp3Dom("appNode");
		appnode.setValue( "${appNode}" );
		model.addProperty("appNode", info.getAppNode() );
		properties.put("appNode", info.getAppNode() );
		
		Xpp3Dom appnodeDesc  = new Xpp3Dom("appNodeDesc");
		appnodeDesc.setValue( "${appNodeDesc}" );
		model.addProperty("appNodeDesc", info.getAppNodeDesc() );
		properties.put("appNodeDesc", info.getAppNodeDesc() );

		Xpp3Dom osgiport  = new Xpp3Dom("osgiPort");
		osgiport.setValue( "${osgiPort}" );
		model.addProperty("osgiPort", info.getOsgiPort() );
		properties.put("osgiPort", info.getOsgiPort() );

		Xpp3Dom httpPort  = new Xpp3Dom("httpPort");
		httpPort.setValue( "${httpPort}" );
		model.addProperty("httpPort", info.getHttpPort() );
		properties.put("httpPort", info.getHttpPort() );

		Xpp3Dom profile  = new Xpp3Dom("profile");
		profile.setValue( "${profile}" );
		model.addProperty("profile", info.getProfile() );
		properties.put("profile", info.getProfile() );

		Xpp3Dom redeploy  = new Xpp3Dom("redeploy");
		redeploy.setValue( "${redeploy}" );
		model.addProperty("redeploy", Boolean.toString(info.isRedeploy()) );
		properties.put("redeploy", Boolean.toString(info.isRedeploy()) );

		config.addChild(deployToAdmin);
		
		config.addChild(agentHost);
		config.addChild(agentPort);
		
		config.addChild(domain);
		config.addChild(domainDesc);
		
		config.addChild(appspace);
		config.addChild(appspaceDesc);
		
		config.addChild(appnode);
		config.addChild(appnodeDesc);
		
		config.addChild(osgiport );
		config.addChild(httpPort );
		
		config.addChild(redeploy);		
		config.addChild(profile );
		
		plugin.setConfiguration(config);

		File deploymentProperties = new File( ModuleHelper.getParentModule( project.getModules() ).getProject().getLocationURI().getRawPath() + File.separator + "deployment.properties");

		if(deploymentProperties.exists()) 
		{
			deploymentProperties.delete();
		}

		boolean fileCreated = false;
		FileOutputStream fileOut = null;
		try {
			
			fileCreated = deploymentProperties.createNewFile();
			fileOut = new FileOutputStream(deploymentProperties);
			String msg = "EAR Deployment Properties. Pass -DdeploymentConfig.file=<File Location/File Name> if you are running from Command Line. Otherwise add 'deploymentConfig.file' property to the POM File ";
			properties.store(fileOut, msg);
			fileOut.close();


		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		

		
	}



	protected void addBuild()
	{
		Build build = model.getBuild();
		if(build == null){
			build = new Build();			
			
		}
		addBW6MavenPlugin( build );
		
		String platform = "";
		if(bwEdition.equals("docker"))
		{
			platform=module.getBwDockerModule().getPlatform();
		}
		//Add this plugin
		if(bwEdition.equals("cf") || bwEdition.equals("docker"))
    	{
			addBWCEPropertiesPlugin(build, bwEdition, platform);
    	}
		
		if(bwEdition.equals("cf"))
    	{
    		//Delete existing plugin
			List<Plugin> plugins=build.getPlugins();
    		for(int i=0;i<plugins.size();i++)
    		{
    			Plugin plg=plugins.get(i);
    			if(plg.getArtifactId().equals("cf-maven-plugin"))
    			{
    				build.removePlugin(plg);
    			}
    		}
    		
    		//Add Plugin
    		addPCFMavenPlugin(build);
    	}
    	else if(bwEdition.equals("docker"))
    	{
    		List<Plugin> plugins=build.getPlugins();
    		for(int i=0;i<plugins.size();i++)
    		{
    			Plugin plg=plugins.get(i);
    			if(plg.getArtifactId().equals("docker-maven-plugin"))
    			{
    				build.removePlugin(plg);
    			}
    		}
    		
    		addDockerMavenPlugin(build);
    		
    		if(platform.equals("K8S"))
    		{
    			for(int i=0;i<plugins.size();i++)
        		{
        			Plugin plg=plugins.get(i);
        			if(plg.getArtifactId().equals("fabric8-maven-plugin"))
        			{
        				build.removePlugin(plg);
        			}
        		}
    			addDockerK8SMavenPlugin(build);
    		}
    	}
    	
    	model.setBuild(build);
	}


	@Override
	protected String getPackaging() 
	{
		return "bwear";
	}

}

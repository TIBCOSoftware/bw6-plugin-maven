package com.tibco.bw.studio.maven.pom.builders;

import java.util.List;
import java.util.Map;

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
	
		plugin.setConfiguration(null);
		
		BWDeploymentInfo info = ((BWApplication)module).getDeploymentInfo();
		
		if( info == null || ! info.isDeployToAdmin() )
		{
			return;
		}
		
		Xpp3Dom config = new Xpp3Dom("configuration");

		Xpp3Dom deployToAdmin  = new Xpp3Dom("deployToAdmin");
		deployToAdmin.setValue( Boolean.toString(info.isDeployToAdmin()) );


		Xpp3Dom agentHost  = new Xpp3Dom("agentHost");
		agentHost.setValue( info.getAgentHost() );
		
		Xpp3Dom agentPort = new Xpp3Dom("agentPort");
		agentPort.setValue( info.getAgentPort() );
		
		Xpp3Dom domain  = new Xpp3Dom("domain");
		domain.setValue( info.getDomain() );

		Xpp3Dom domainDesc  = new Xpp3Dom("domainDesc");
		domainDesc.setValue( info.getDomainDesc() );
		
		Xpp3Dom appspace  = new Xpp3Dom("appSpace");
		appspace.setValue( info.getAppspace() );

		Xpp3Dom appspaceDesc  = new Xpp3Dom("appSpaceDesc");
		appspaceDesc.setValue( info.getAppspaceDesc() );


		
		Xpp3Dom appnode  = new Xpp3Dom("appNode");
		appnode.setValue( info.getAppNode() );
		
		
		Xpp3Dom appnodeDesc  = new Xpp3Dom("appNodeDesc");
		appnodeDesc.setValue( info.getAppNodeDesc() );

		Xpp3Dom osgiport  = new Xpp3Dom("osgiPort");
		osgiport.setValue( info.getOsgiPort() );

		Xpp3Dom httpPort  = new Xpp3Dom("httpPort");
		httpPort.setValue( info.getHttpPort() );

		Xpp3Dom profile  = new Xpp3Dom("profile");
		profile.setValue( info.getProfile() );

		Xpp3Dom redeploy  = new Xpp3Dom("redeploy");
		redeploy.setValue( Boolean.toString(info.isRedeploy()) );


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

package com.tibco.bw.studio.maven.pom.builders;

import java.util.List;
import java.util.Map;

import org.apache.maven.model.Build;
import org.apache.maven.model.Plugin;

import com.tibco.bw.studio.maven.helpers.ManifestParser;
import com.tibco.bw.studio.maven.helpers.ModuleHelper;
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
	
	protected void addBuild()
	{
		Build build = model.getBuild();
		if(build == null){
			build = new Build();
			//BW6 maven plugin only needs to be created/added if its a new POM
			addBW6MavenPlugin( build );
			
			if(bwEdition.equals("cf") || bwEdition.equals("docker"))
	    	{
				addBWCEPropertiesPlugin(build);
	    	}
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
    		String platform = module.getBwDockerModule().getPlatform();
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

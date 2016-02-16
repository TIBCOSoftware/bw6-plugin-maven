package com.tibco.bw.studio.maven.pom.builders;

import java.util.Map;

import org.apache.maven.model.Build;

import com.tibco.bw.studio.maven.helpers.ManifestParser;
import com.tibco.bw.studio.maven.helpers.ModuleHelper;
import com.tibco.bw.studio.maven.modules.BWModule;
import com.tibco.bw.studio.maven.modules.BWProject;

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
			bwEdition="bwcf";
		}else bwEdition="bw6";
		
		initializeModel();
		
		addPrimaryTags();
		addParent( ModuleHelper.getParentModule( project.getModules() ));
		addBuild();
		addProperties();
		
		generatePOMFile();
		
	}
	
	protected void addBuild()
	{
    	Build build = new Build();

    	addBW6MavenPlugin( build );
    	if(bwEdition.equals("bwcf")){
    		addPCFMavenPlugin(build);
    	}
    	model.setBuild(build);
	}


	@Override
	protected String getPackaging() 
	{
		return "bwear";
	}

}

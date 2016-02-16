package com.tibco.bw.studio.maven.pom.builders;

import java.util.List;
import java.util.Map;

import org.apache.maven.model.Build;
import org.apache.maven.model.Model;

import com.tibco.bw.studio.maven.helpers.ManifestParser;
import com.tibco.bw.studio.maven.helpers.ModuleHelper;
import com.tibco.bw.studio.maven.helpers.ModuleOrderBuilder;
import com.tibco.bw.studio.maven.modules.BWModule;
import com.tibco.bw.studio.maven.modules.BWModuleType;
import com.tibco.bw.studio.maven.modules.BWProject;

public class ParentPOMBuilder extends AbstractPOMBuilder implements IPOMBuilder 
{

	private String bwEdition;
	
	@Override
	public void build(BWProject project, BWModule module) throws Exception
	{
		
		this.project = project;
		this.module = module;
		this.model = new Model();
		
		Map<String,String> manifest = ManifestParser.parseManifest(project.getModules().get(0).getProject());
		if(manifest.containsKey("TIBCO-BW-Edition") && manifest.get("TIBCO-BW-Edition").equals("bwcf")){
			bwEdition="bwcf";
		}else bwEdition="bw6";
		
		addPrimaryTags();
		model.setGroupId( module.getGroupId());
		model.setVersion( module.getVersion() );
		addProperties();
		addModules();
		if(bwEdition.equals("bwcf")){
			addBuild();
		}
		generatePOMFile();

	}
	
	protected void addBuild()
	{
    	Build build = new Build();
    	addPCFWithSkipMavenPlugin( build );
    	model.setBuild(build);
	}
	
	@Override
	protected String getPackaging() 
	{
		return "pom";
	}
	
	protected void addModules()
	{
		
		for( BWModule module : project.getModules() )
		{
			if( module.getType() == BWModuleType.PluginProject )
			{
				model.getModules().add( module.getToPath());
			}
		}
		
		ModuleOrderBuilder builder = new ModuleOrderBuilder();
		List<String> list = builder.getDependencyOrder(project);
		for( String str : list )
		{
			BWModule module = ModuleHelper.getModule(project.getModules() , str );
			model.getModules().add( module.getToPath() );
		}
	}

}

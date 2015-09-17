package com.tibco.bw.studio.maven.pom.builders;

import org.apache.maven.model.Build;
import org.apache.maven.model.Model;

import com.tibco.bw.studio.maven.helpers.ModuleHelper;
import com.tibco.bw.studio.maven.modules.BWModule;
import com.tibco.bw.studio.maven.modules.BWProject;

public class ModulePOMBuilder extends AbstractPOMBuilder implements IPOMBuilder 
{

	@Override
	public void build(BWProject project, BWModule module) throws Exception
	{
		this.project = project;
		this.module = module;
		this.model = new Model();
		
		addPrimaryTags();
		addParent( ModuleHelper.getParentModule( project.getModules() ));
		addBuild();
		addProperties();
		
		generatePOMFile();

		
	}
	
	protected void addSourceTarget( Build build )
	{
		build.setSourceDirectory("src");
		build.setOutputDirectory("target/classes");
	}
	
	protected void addBuild()
	{
    	Build build = new Build();

    	addSourceTarget( build );
    	addBW6MavenPlugin( build );
    	
    	model.setBuild(build);
	}

	

	@Override
	protected String getPackaging() 
	{
		return "bwmodule";
	}

	
}

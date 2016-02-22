package com.tibco.bw.studio.maven.pom.builders;

import java.util.Map;

import org.apache.maven.model.Build;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;

import com.tibco.bw.studio.maven.helpers.ManifestParser;
import com.tibco.bw.studio.maven.helpers.ModuleHelper;
import com.tibco.bw.studio.maven.modules.BWAppModule;
import com.tibco.bw.studio.maven.modules.BWModule;
import com.tibco.bw.studio.maven.modules.BWPluginModule;
import com.tibco.bw.studio.maven.modules.BWProject;

public class ModulePOMBuilder extends AbstractPOMBuilder implements IPOMBuilder 
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
		if( module instanceof BWAppModule )
		{
			addPaletteSharedDependency();	
		}
		
		//addModuleDependencies();
		
		if( module instanceof BWPluginModule && ((BWPluginModule)module).isCustomXpath() )
		{
			addCustomXPathDependency();
		}
		
		//addProperties();
		
		generatePOMFile();

		
		
	}
	
	protected void addModuleDependencies()
	{
		if( module.getDepModules() == null || module.getDepModules().size() == 0 )
		{
			return;
		}
		for( String moduleName : module.getDepModules() )
		{
			BWModule module = ModuleHelper.getModule( project.getModules(), moduleName );
			if( module != null )
			{
				addModuleDependency(module);
			}
		}
	}
	
	protected void addPaletteSharedDependency()
	{
		
		Dependency dep = new Dependency();
		dep.setGroupId("com.tibco.plugins");
		dep.setArtifactId("com.tibco.bw.palette.shared");
		dep.setVersion("6.1.100");
		if(!dependencyExists(dep))
		{
			model.getDependencies().add(dep);	
		}
		
		
	}
	
	protected void addModuleDependency( BWModule module )
	{
		
		Dependency dep = new Dependency();
		dep.setGroupId(module.getGroupId());
		dep.setArtifactId(module.getArtifactId());
		dep.setVersion(module.getVersion());
		if(!dependencyExists(dep))
		{
			model.getDependencies().add(dep);	
		}
		
		
	}

	protected void addCustomXPathDependency()
	{
		
		Dependency dep = new Dependency();
		dep.setGroupId("com.tibco.plugins");
		dep.setArtifactId("com.tibco.xml.cxf.common");
		dep.setVersion("1.3.200");
		if(!dependencyExists(dep))
		{
			model.getDependencies().add(dep);	
		}


		
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
    	if(bwEdition.equals("bwcf")){
    		addPCFWithSkipMavenPlugin( build );
    	}
    	model.setBuild(build);
	}

	

	@Override
	protected String getPackaging() 
	{
		return "bwmodule";
	}

	
}

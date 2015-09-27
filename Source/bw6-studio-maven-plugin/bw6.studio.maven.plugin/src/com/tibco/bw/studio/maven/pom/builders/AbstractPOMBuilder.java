package com.tibco.bw.studio.maven.pom.builders;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Reader;
import java.util.List;

import org.apache.maven.model.Build;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.Parent;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;

import com.tibco.bw.studio.maven.modules.BWModule;
import com.tibco.bw.studio.maven.modules.BWParent;
import com.tibco.bw.studio.maven.modules.BWProject;

public abstract class AbstractPOMBuilder 
{
	protected BWProject project; 
	protected BWModule module;

	protected Model model;

	protected void addParent(BWParent parentModule )
	{
		Parent parent = new Parent();
		parent.setGroupId( parentModule.getGroupId() );
		parent.setArtifactId( parentModule.getArtifactId() );
		parent.setVersion(parentModule.getVersion() );
		parent.setRelativePath( module.getFromPath() );
		model.setParent(parent);
		
	}
	
	protected void addProperties()
	{
		
	}
	
	
	protected void addPrimaryTags( )
	{
		model.setModelVersion( "4.0.0" );
    	model.setArtifactId(  module.getArtifactId() );
    	model.setPackaging( getPackaging() );
	}

	protected void addBW6MavenPlugin( Build build )
	{
			Plugin plugin = new Plugin();
			plugin.setGroupId("com.tibco.plugins");
			plugin.setArtifactId("bw6-maven-plugin");
			plugin.setVersion("1.0.0");
			plugin.setExtensions("true");
			
			build.addPlugin(plugin);
	}
	
	protected boolean dependencyExists( Dependency check )
	{
		List<Dependency> list =   model.getDependencies();
		for ( Dependency dep : list )
		{
			return dep.getArtifactId().equals( check.getArtifactId() ) && dep.getGroupId().equals( check.getGroupId() ) && dep.getVersion().equals( check.getVersion() ); 
		}
		
		return false;
		
	}
	
	protected void pluginExists()
	{
		
	}
	
	
	protected void generatePOMFile() throws Exception
	{
		FileWriter writer = new FileWriter( module.getPomfileLocation());
		new MavenXpp3Writer().write(writer, model);
	}

	protected abstract String getPackaging();
	
	protected void initializeModel()
	{
		File pomFile = module.getPomfileLocation();
		model = readModel(pomFile);
		if( model == null )
		{
			model = new Model();
		}
		
	}
	
	protected Model readModel( File pomXmlFile)
	{
		Model model = null;
		try
		{
			Reader reader = new FileReader(pomXmlFile);
			try {
			    MavenXpp3Reader xpp3Reader = new MavenXpp3Reader();
			    model = xpp3Reader.read(reader);
			} finally {
			    reader.close();
			}
			
		}
		catch( Exception e)
		{
			e.printStackTrace();
		}
		
		return model;
	}
	
}

package com.tibco.bw.studio.maven.pom.builders;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Reader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.maven.model.Build;
import org.apache.maven.model.ConfigurationContainer;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.Parent;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.codehaus.plexus.util.xml.Xpp3Dom;

import com.tibco.bw.studio.maven.modules.BWModule;
import com.tibco.bw.studio.maven.modules.BWPCFServicesModule;
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
	
	
	protected void addPCFWithSkipMavenPlugin( Build build )
	{
			Plugin plugin = new Plugin();
			plugin.setGroupId("org.cloudfoundry");
			plugin.setArtifactId("cf-maven-plugin");
			plugin.setVersion("1.1.3");
			
			Xpp3Dom config=new Xpp3Dom("configuration");
			
			Xpp3Dom child = new Xpp3Dom( "skip" );
	        child.setValue("true");
	        config.addChild( child );
			
	        plugin.setConfiguration(config);
			build.addPlugin(plugin);
	}
	
	
	protected void addPCFMavenPlugin( Build build )
	{
			Plugin plugin = new Plugin();
			plugin.setGroupId("org.cloudfoundry");
			plugin.setArtifactId("cf-maven-plugin");
			plugin.setVersion("1.1.3");
			
			Xpp3Dom config=new Xpp3Dom("configuration");
			
			Xpp3Dom child = new Xpp3Dom( "server" );
	        child.setValue( module.getBwpcfModule().getCredString() );
	        config.addChild( child );
			
	        child = new Xpp3Dom( "target" );
	        child.setValue( module.getBwpcfModule().getTarget());
	        config.addChild( child );
	        
	        child = new Xpp3Dom( "trustSelfSignedCerts" );
	        child.setValue("true");
	        config.addChild( child );
	        
	        child = new Xpp3Dom( "org" );
	        child.setValue( module.getBwpcfModule().getOrg());
	        config.addChild( child );
	        
	        child = new Xpp3Dom( "space" );
	        child.setValue( module.getBwpcfModule().getSpace());
	        config.addChild( child );
	        
	        child = new Xpp3Dom( "url" );
	        child.setValue(getPCFAppDefaultURL());
	        config.addChild( child );
	        
	        child = new Xpp3Dom( "instances" );
	        child.setValue(module.getBwpcfModule().getInstances());
	        config.addChild( child );
	        
	        child = new Xpp3Dom( "skip" );
	        child.setValue("false");
	        config.addChild( child );
	        
	        if(module.getBwpcfModule().getMemory()!=null && !module.getBwpcfModule().getMemory().isEmpty()){
		        child = new Xpp3Dom( "memory" );
		        child.setValue(module.getBwpcfModule().getMemory());
		        config.addChild( child );
			}
	        
	        if(module.getBwpcfModule().getBuildpack()!=null && !module.getBwpcfModule().getBuildpack().isEmpty()){
		        child = new Xpp3Dom( "buildpack" );
		        child.setValue(module.getBwpcfModule().getBuildpack());
		        config.addChild( child );
			}
	        
	        List<BWPCFServicesModule> services=module.getBwpcfModule().getServices();
	        if(services!=null && services.size()>0){
	        	child = new Xpp3Dom( "services" );

	        	for(BWPCFServicesModule service: services){
	        		Xpp3Dom serviceChild = new Xpp3Dom( "service" );
	        		Xpp3Dom child1 = new Xpp3Dom( "name" );
	        		child1.setValue(service.getServiceName());
	        		serviceChild.addChild( child1 );
	        		
	        		child1 = new Xpp3Dom( "label" );
	        		child1.setValue(service.getServiceLabel());
	        		serviceChild.addChild( child1 );
	        		
	        		child1 = new Xpp3Dom( "version" );
	        		child1.setValue(service.getServiceVersion());
	        		serviceChild.addChild( child1 );
	        		
	        		child1 = new Xpp3Dom( "plan" );
	        		child1.setValue(service.getServicePlan());
	        		serviceChild.addChild( child1 );
	        		
	        		child.addChild( serviceChild );
	        	}
	        	
	        	config.addChild( child );
	        }
			plugin.setConfiguration(config);
			
			build.addPlugin(plugin);
	}
	
	private String getPCFAppDefaultURL(){
		
		String url=module.getArtifactId().replace(".", "-");
		String domainStr=module.getBwpcfModule().getTarget();
		String protoDom=domainStr.substring(0,domainStr.indexOf("."));
		String domain=domainStr.replace(protoDom, "");
		return url+domain;
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

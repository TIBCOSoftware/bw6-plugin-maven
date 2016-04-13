package com.tibco.bw.studio.maven.pom.builders;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.maven.model.Build;
import org.apache.maven.model.ConfigurationContainer;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.Parent;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.PluginExecution;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.codehaus.plexus.util.xml.Xpp3Dom;

import com.tibco.bw.studio.maven.modules.BWModule;
import com.tibco.bw.studio.maven.modules.BWModuleType;
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
	
	protected void addBWCEProperties()
	{
		Properties properties=new Properties();
		properties.put("property.file", "../pcfdev.properties");
		model.setProperties(properties);
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
	
	protected void addBWCEPropertiesPlugin( Build build )
	{
		Plugin plugin = new Plugin();
		plugin.setGroupId("org.codehaus.mojo");
		plugin.setArtifactId("properties-maven-plugin");
		plugin.setVersion("1.0.0");
		List<PluginExecution> executions=new ArrayList<PluginExecution>();
		PluginExecution pe=new PluginExecution();
		pe.setPhase("initialize");
		pe.setGoals(Arrays.asList("read-project-properties"));
		executions.add(pe);
		plugin.setExecutions(executions);
		
		Xpp3Dom config=new Xpp3Dom("configuration");
		Xpp3Dom child = new Xpp3Dom( "files" );
		Xpp3Dom fileChild = new Xpp3Dom( "file" );
		fileChild.setValue( "${property.file}" );
		child.addChild( fileChild );
        
		config.addChild( child );
		plugin.setConfiguration(config);
		
		build.addPlugin(plugin);
	}
	
	private void createPCFPropertiesFiles(){
		try {
			Properties properties = new Properties();
			properties.setProperty("bwpcf.server", module.getBwpcfModule().getCredString());
			properties.setProperty("bwpcf.target", module.getBwpcfModule().getTarget());
			properties.setProperty("bwpcf.trustSelfSignedCerts", "true");
			properties.setProperty("bwpcf.org", module.getBwpcfModule().getOrg());
			properties.setProperty("bwpcf.appName", module.getBwpcfModule().getAppName());
			properties.setProperty("bwpcf.space", module.getBwpcfModule().getSpace());
			if(module.getBwpcfModule().getAppName()!=null && !module.getBwpcfModule().getAppName().isEmpty()){
				properties.setProperty("bwpcf.url", getPCFAppURL(module.getBwpcfModule().getAppName()));
			}else{
				properties.setProperty("bwpcf.url", getPCFAppDefaultURL());
			}
			properties.setProperty("bwpcf.instances", module.getBwpcfModule().getInstances());
			properties.setProperty("bwpcf.memory", module.getBwpcfModule().getMemory());
			properties.setProperty("bwpcf.buildpack", module.getBwpcfModule().getBuildpack());
			
			File devfile = new File(getWorkspacepath() + File.separator + "pcfdev.properties");
			if (!devfile.exists()) {
				boolean done=devfile.createNewFile();
				if(done){
					FileOutputStream fileOut = new FileOutputStream(devfile);
					properties.store(fileOut, "PCF Properties");
					fileOut.close();
					
					File prodfile = new File(getWorkspacepath()+ File.separator + "pcfprod.properties");
					boolean proddone=prodfile.createNewFile();
					if(proddone){
						FileUtils.copyFile(devfile, prodfile);
					}
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private String getWorkspacepath(){
		
		for (BWModule module : project.getModules() )
		{
			if(module.getType() == BWModuleType.Parent){
				String pomloc=module.getPomfileLocation().toString();
				String workspace=pomloc.substring(0,pomloc.indexOf("pom.xml"));
				return workspace;
			}
		}
		return null;
	}
	
	protected void addPCFMavenPlugin( Build build )
	{
			//Create properties file for Dev and Prod environment
			createPCFPropertiesFiles();
		
			//Now just add PCF Maven plugin
			Plugin plugin = new Plugin();
			plugin.setGroupId("org.cloudfoundry");
			plugin.setArtifactId("cf-maven-plugin");
			plugin.setVersion("1.1.3");
			
			Xpp3Dom config=new Xpp3Dom("configuration");
			
			Xpp3Dom child = new Xpp3Dom( "server" );
	        child.setValue("${bwpcf.server}");
	        config.addChild( child );
			
	        child = new Xpp3Dom( "target" );
	        child.setValue("${bwpcf.target}");
	        config.addChild( child );
	        
	        child = new Xpp3Dom( "trustSelfSignedCerts" );
	        child.setValue("${bwpcf.trustSelfSignedCerts}");
	        config.addChild( child );
	        
	        child = new Xpp3Dom( "org" );
	        child.setValue("${bwpcf.org}");
	        config.addChild( child );
	        
	        child = new Xpp3Dom( "space" );
	        child.setValue("${bwpcf.space}");
	        config.addChild( child );
	        
	        child = new Xpp3Dom( "appname" );
	        child.setValue("${bwpcf.appName}");
	        config.addChild( child );
	        
	        child = new Xpp3Dom( "url" );
	        child.setValue("${bwpcf.url}");
	        config.addChild( child );
	        
	        child = new Xpp3Dom( "instances" );
	        child.setValue("${bwpcf.instances}");
	        config.addChild( child );
	        
	        child = new Xpp3Dom( "skip" );
	        child.setValue("false");
	        config.addChild( child );
	        
	        child = new Xpp3Dom( "memory" );
	        child.setValue("${bwpcf.memory}");
	        config.addChild( child );
	        
	        child = new Xpp3Dom( "buildpack" );
	        child.setValue("${bwpcf.buildpack}");
	        config.addChild( child );
	        
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
	
	private String getPCFAppURL(String appName){
		
		appName=appName.replace(".", "-");
		String domainStr=module.getBwpcfModule().getTarget();
		String protoDom=domainStr.substring(0,domainStr.indexOf("."));
		String domain=domainStr.replace(protoDom, "");
		return appName+domain;
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
			if(dep.getArtifactId().equals( check.getArtifactId() ) && dep.getGroupId().equals( check.getGroupId() ) && dep.getVersion().equals( check.getVersion() ) )
			{
				return true;	
			}
			 
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

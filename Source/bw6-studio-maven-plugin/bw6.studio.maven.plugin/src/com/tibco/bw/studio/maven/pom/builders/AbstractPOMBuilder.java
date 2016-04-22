package com.tibco.bw.studio.maven.pom.builders;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.maven.model.Build;
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
	
	protected void addBWCloudFoundryProperties()
	{
		Properties properties=new Properties();
		properties.put("property.file", "pcfdev.properties");
		model.setProperties(properties);
	}
	
	protected void addBWDockerProperties(String platform)
	{
		Properties properties=new Properties();
		if(platform.equals("K8S")){
			properties.put("property.file", "docker-k8s-dev.properties");
		}
		else if(platform.equals("Mesos")){
			properties.put("property.file", "docker-mesos-dev.properties");
		}
		else if(platform.equals("Swarm")){
			properties.put("property.file", "docker-swarm-dev.properties");
		}
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
	
	protected void addDockerK8SWithSkipMavenPlugin( Build build )
	{
			Plugin plugin = new Plugin();
			plugin.setGroupId("io.fabric8");
			plugin.setArtifactId("fabric8-maven-plugin");
			plugin.setVersion("2.2.102");
			
			Xpp3Dom config=new Xpp3Dom("configuration");
			
			Xpp3Dom child = new Xpp3Dom( "skip" );
	        child.setValue("true");
	        config.addChild( child );
			
	        plugin.setConfiguration(config);
			build.addPlugin(plugin);
	}
	
	protected void addDockerK8SMavenPlugin( Build build)
	{
		Plugin plugin = new Plugin();
		plugin.setGroupId("io.fabric8");
		plugin.setArtifactId("fabric8-maven-plugin");
		plugin.setVersion("2.2.102");
		
		Xpp3Dom config=new Xpp3Dom("configuration");
		
		Xpp3Dom child = new Xpp3Dom( "skip" );
        child.setValue("false");
        config.addChild( child );
		
        plugin.setConfiguration(config);
        
		build.addPlugin(plugin);
	}
	
	
	protected void addDockerWithSkipMavenPlugin( Build build )
	{
			Plugin plugin = new Plugin();
			plugin.setGroupId("io.fabric8");
			plugin.setArtifactId("docker-maven-plugin");
			plugin.setVersion("0.14.2");
			
			Xpp3Dom config=new Xpp3Dom("configuration");
			
			Xpp3Dom child = new Xpp3Dom( "skip" );
	        child.setValue("true");
	        config.addChild( child );
			
	        plugin.setConfiguration(config);
			build.addPlugin(plugin);
	}
	
	protected void addDockerMavenPlugin( Build build )
	{
		//Create properties file for Dev and Prod environment
		createDockerPropertiesFiles();
		
		//Now just add Fabric8 Docker Maven plugin
		Plugin plugin = new Plugin();
		plugin.setGroupId("io.fabric8");
		plugin.setArtifactId("docker-maven-plugin");
		plugin.setVersion("0.14.2");
		
		Xpp3Dom config=new Xpp3Dom("configuration");
		
		Xpp3Dom child = new Xpp3Dom( "skip" );
        child.setValue("false");
        config.addChild( child );
		
        child = new Xpp3Dom( "dockerHost" );
        child.setValue("${bwdocker.host}");
        config.addChild( child );
        
        child = new Xpp3Dom( "certPath" );
        child.setValue("${bwdocker.certPath}");
        config.addChild( child );
        
        child = new Xpp3Dom( "images" );
	        Xpp3Dom imageChild = new Xpp3Dom( "image" );
			Xpp3Dom child1 = new Xpp3Dom( "alias" );
			child1.setValue("${bwdocker.containername}");
			imageChild.addChild( child1 );
			
			child1 = new Xpp3Dom( "name" );
			child1.setValue("${docker.image}");
			imageChild.addChild( child1 );
			
			Xpp3Dom buildchild = new Xpp3Dom( "build" );
				Xpp3Dom child2 = new Xpp3Dom( "from" );
				child2.setValue("${bwdocker.from}");
				buildchild.addChild( child2 );
				
				child2 = new Xpp3Dom( "maintainer" );
				child2.setValue("${bwdocker.maintainer}");
				buildchild.addChild( child2 );
				
				Xpp3Dom assemblychild = new Xpp3Dom( "assembly" );
					Xpp3Dom child22 = new Xpp3Dom( "basedir" );
					child22.setValue("/");
					assemblychild.addChild( child22 );
					
					child22 = new Xpp3Dom( "descriptorRef" );
					child22.setValue("artifact");
					assemblychild.addChild( child22 );
				buildchild.addChild( assemblychild );
				
				Xpp3Dom tagchild = new Xpp3Dom( "tags" );
					Xpp3Dom child23 = new Xpp3Dom( "tag" );
					child23.setValue("latest");
					tagchild.addChild( child23 );
					
				buildchild.addChild( tagchild );
				
				Xpp3Dom portchild = new Xpp3Dom( "ports" );
					Xpp3Dom child24 = new Xpp3Dom( "port" );
					child24.setValue("8080");
					portchild.addChild( child24 );
				
				buildchild.addChild( portchild );
				
				// IF Volume exist
				if(module.getBwDockerModule().getDockerVolume()!=null 
						&& !module.getBwDockerModule().getDockerVolume().isEmpty())
				{
					Xpp3Dom volchild = new Xpp3Dom( "volumes" );
						Xpp3Dom child25 = new Xpp3Dom( "volume" );
						child25.setValue("${bwdocker.volume.v1}");
						volchild.addChild( child25 );
				
					buildchild.addChild( volchild );
				}
				
			imageChild.addChild( buildchild );
			
			Xpp3Dom runchild = new Xpp3Dom( "run" );
				Xpp3Dom child3 = new Xpp3Dom( "namingStrategy" );
				child3.setValue("alias");
				runchild.addChild( child3 );
				
				// IF Ports exist
				if(module.getBwDockerModule().getDockerPorts()!=null 
						&& module.getBwDockerModule().getDockerPorts().size()>0)
				{
					Xpp3Dom runportchild = new Xpp3Dom( "ports" );
						Xpp3Dom child31 = new Xpp3Dom( "port" );
						child31.setValue("${bwdocker.port.p1}");
						runportchild.addChild( child31 );
						
						if(module.getBwDockerModule().getDockerPorts().size()==2)
						{
						child31 = new Xpp3Dom( "port" );
						child31.setValue("${bwdocker.port.p2}");
						runportchild.addChild( child31 );
						}
						
					runchild.addChild( runportchild );
				}
				
				// IF Links exist
				if(module.getBwDockerModule().getDockerLink()!=null 
						&& !module.getBwDockerModule().getDockerLink().isEmpty())
				{
					Xpp3Dom linkchild = new Xpp3Dom( "links" );
						Xpp3Dom child32 = new Xpp3Dom( "link" );
						child32.setValue("${bwdocker.link.l1}");
						linkchild.addChild( child32 );
					runchild.addChild( linkchild );
				}
			imageChild.addChild( runchild );
			
		 child.addChild( imageChild );
		
		config.addChild( child );
		
		
		plugin.setConfiguration(config);
		build.addPlugin(plugin);
	}
	
	
	private void createDockerPropertiesFiles(){
		try {
			Properties properties = new Properties();

			// Add Docker properties
			properties.setProperty("bwdocker.host", module.getBwDockerModule().getDockerHost());
			properties.setProperty("bwdocker.certPath", module.getBwDockerModule().getDockerHostCertPath());
			properties.setProperty("docker.image", module.getBwDockerModule().getDockerImageName());
			properties.setProperty("bwdocker.containername", module.getBwDockerModule().getDockerAppName());
			properties.setProperty("bwdocker.from", module.getBwDockerModule().getDockerImageFrom());
			properties.setProperty("bwdocker.maintainer", module.getBwDockerModule().getDockerImageMaintainer());
			if(module.getBwDockerModule().getDockerVolume()!=null && !module.getBwDockerModule().getDockerVolume().isEmpty())
			{
				properties.setProperty("bwdocker.volume.v1", module.getBwDockerModule().getDockerVolume());
			}

			if(module.getBwDockerModule().getDockerLink()!=null && !module.getBwDockerModule().getDockerLink().isEmpty())
			{
				properties.setProperty("bwdocker.link.l1", module.getBwDockerModule().getDockerLink());
			}

			if(module.getBwDockerModule().getDockerPorts().size()>0){
				properties.setProperty("bwdocker.port.p1", module.getBwDockerModule().getDockerPorts().get(0));
			}
			if(module.getBwDockerModule().getDockerPorts().size()==2){
				properties.setProperty("bwdocker.port.p2", module.getBwDockerModule().getDockerPorts().get(1));
			}
			//Add platform properties

			String platform=module.getBwDockerModule().getPlatform();
			String devFileName="";
			if(platform.equals("K8S")){
				devFileName="docker-k8s-dev.properties";

				properties.setProperty("fabric8.template", module.getBwDockerModule().getRcName());
				properties.setProperty("fabric8.replicationController.name", module.getBwDockerModule().getRcName());
				properties.setProperty("fabric8.replicas", module.getBwDockerModule().getNumOfReplicas());
				properties.setProperty("fabric8.label.project", module.getBwDockerModule().getRcName());
				properties.setProperty("fabric8.label.group", module.getBwDockerModule().getRcName());
				properties.setProperty("fabric8.label.container", module.getBwDockerModule().getRcName());
				properties.setProperty("fabric8.container.name", module.getBwDockerModule().getRcName());
				properties.setProperty("fabric8.service.name", module.getBwDockerModule().getServiceName());
				properties.setProperty("fabric8.service.type", "LoadBalancer");
				properties.setProperty("fabric8.service.port", "80");
				properties.setProperty("fabric8.service.containerPort", module.getBwDockerModule().getContainerPort());
				properties.setProperty("fabric8.namespace", module.getBwDockerModule().getK8sNamespace());
				properties.setProperty("fabric8.apply.namespace", module.getBwDockerModule().getK8sNamespace());

				//Add k8s env variables
				Map<String, String> k8sEnvVars=module.getBwDockerModule().getK8sEnvVariables();
				for (String key : k8sEnvVars.keySet()) {
					String fabric8Key="fabric8.env."+key;
					properties.setProperty(fabric8Key, k8sEnvVars.get(key));
				}

			}
			else if(platform.equals("Mesos")){
				devFileName="docker-mesos-dev.properties";
			}
			else if(platform.equals("Swarm")){
				devFileName="docker-swarm-dev.properties";
			}

			File devfile = new File(getWorkspacepath() + File.separator + devFileName);
			if(devfile.exists()) 
			{
				devfile.delete();
			}
			boolean done=devfile.createNewFile();
			if(done)
			{
				FileOutputStream fileOut = new FileOutputStream(devfile);
				String msg = "Docker and "+platform+" platform properties";
				properties.store(fileOut, msg);
				fileOut.close();

				String prodFileName=devFileName.replace("dev", "prod");
				File prodfile = new File(getWorkspacepath()+ File.separator + prodFileName);
				if(prodfile.exists()) 
				{
					prodfile.delete();
				}
				Files.copy(devfile.toPath(), prodfile.toPath());
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
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
			
			if(module.getBwpcfModule().getAppName()!=null && !module.getBwpcfModule().getAppName().isEmpty())
			{
				properties.setProperty("bwpcf.url", getPCFAppURL(module.getBwpcfModule().getAppName()));
			}
			else
			{
				properties.setProperty("bwpcf.url", getPCFAppDefaultURL());
			}
			properties.setProperty("bwpcf.instances", module.getBwpcfModule().getInstances());
			properties.setProperty("bwpcf.memory", module.getBwpcfModule().getMemory());
			properties.setProperty("bwpcf.buildpack", module.getBwpcfModule().getBuildpack());

			File devfile = new File(getWorkspacepath() + File.separator + "pcfdev.properties");
			if(devfile.exists()) 
			{
				devfile.delete();
			}
		
			boolean done=devfile.createNewFile();
			if(done)
			{
				FileOutputStream fileOut = new FileOutputStream(devfile);
				properties.store(fileOut, "PCF Properties");
				
				File prodfile = new File(getWorkspacepath()+ File.separator + "pcfprod.properties");
				if(prodfile.exists()) 
				{
					prodfile.delete();
				}
				Files.copy(devfile.toPath(), prodfile.toPath());
				
				fileOut.close();
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
			if(module.getType() == BWModuleType.Application){
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
		try
		{
			new MavenXpp3Writer().write(writer, model);	
		}
		finally
		{
			writer.close();
		}
		
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

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

import org.apache.maven.model.Activation;
import org.apache.maven.model.Build;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.Parent;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.PluginExecution;
import org.apache.maven.model.Profile;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.codehaus.plexus.util.xml.Xpp3Dom;

import com.tibco.bw.studio.maven.modules.model.BWApplication;
import com.tibco.bw.studio.maven.modules.model.BWDeploymentInfo;
import com.tibco.bw.studio.maven.modules.model.BWModule;
import com.tibco.bw.studio.maven.modules.model.BWModuleType;
import com.tibco.bw.studio.maven.modules.model.BWPCFServicesModule;
import com.tibco.bw.studio.maven.modules.model.BWParent;
import com.tibco.bw.studio.maven.modules.model.BWProject;

public abstract class AbstractPOMBuilder {
	protected BWProject project; 
	protected BWModule module;
	protected Model model;

	protected void addParent(BWParent parentModule) {
		Parent parent = new Parent();
		parent.setGroupId(parentModule.getGroupId());
		parent.setArtifactId(parentModule.getArtifactId());
		parent.setVersion(parentModule.getVersion());
		parent.setRelativePath(module.getFromPath());
		model.setParent(parent);
	}

	protected void addBWCloudFoundryProperties() {
		Properties properties = model.getProperties();
		if(properties == null) {
			properties = new Properties();
		}
		properties.put("pcf.property.file", "pcfdev.properties");
		model.setProperties(properties);
	}

	protected void addBWDockerProperties(String platform) {
		Properties properties = model.getProperties();
		if(properties == null) {
			properties = new Properties();
		}
		properties.put("docker.property.file", "docker-dev.properties");
		if(module.getBwDockerModule().getDockerEnvs() != null && module.getBwDockerModule().getDockerEnvs().size() > 0) {
			properties.put("docker.env.property.file", "docker-host-env-dev.properties");
		} else {
			if(properties.containsKey("docker.env.property.file")) {
				properties.remove("docker.env.property.file");
			}
		}
		if(platform.equals("K8S")) {
			properties.put("k8s.property.file", "k8s-dev.properties");
		} else {
			if(properties.containsKey("k8s.property.file")) {
				properties.remove("k8s.property.file");
			}
		}
		model.setProperties(properties);
	}

	protected void addPrimaryTags() {
		model.setModelVersion("4.0.0");
		model.setArtifactId( module.getArtifactId());
		model.setPackaging(getPackaging());
	}

	protected void addBW6MavenPlugin(Build build) {
		Plugin plugin = null;
		for(Plugin p : build.getPlugins()) {
			if(p.getArtifactId().equals("bw6-maven-plugin") && p.getGroupId().equals("com.tibco.plugins")) {
				plugin = p;
			}
		}
		if(plugin == null) {
			plugin = new Plugin();	
			build.addPlugin(plugin);
		}
		plugin.setGroupId("com.tibco.plugins");
		plugin.setArtifactId("bw6-maven-plugin");
		plugin.setVersion("2.0.1");
		plugin.setExtensions("true");
		addDeploymentDetails(plugin);
	}

	protected void addDeploymentDetails(Plugin plugin) {
	}

	protected void addPCFWithSkipMavenPlugin(Build build) {
		Plugin plugin = new Plugin();
		plugin.setGroupId("org.cloudfoundry");
		plugin.setArtifactId("cf-maven-plugin");
		plugin.setVersion("1.1.3");

		Xpp3Dom config = new Xpp3Dom("configuration");

		Xpp3Dom child = new Xpp3Dom("skip");
		child.setValue("true");
		config.addChild(child);

		plugin.setConfiguration(config);
		build.addPlugin(plugin);
	}

	protected void addBWCEPropertiesPlugin(Build build, String bwEdition, String platfrom) {
		Plugin plugin = null;
		List<Plugin> plugins = build.getPlugins();
		for(int i = 0; i < plugins.size(); i++) {
			Plugin plg=plugins.get(i);
			if(plg.getArtifactId().equals("properties-maven-plugin")) {
				plugin = plg;
				break;
			}
		}

		if(plugin == null) {
			plugin = new Plugin();
			plugin.setGroupId("org.codehaus.mojo");
			plugin.setArtifactId("properties-maven-plugin");
			plugin.setVersion("1.0.0");
			List<PluginExecution> executions = new ArrayList<PluginExecution>();
			PluginExecution pe = new PluginExecution();
			pe.setPhase("initialize");
			pe.setGoals(Arrays.asList("read-project-properties"));
			executions.add(pe);
			plugin.setExecutions(executions);

			Xpp3Dom config = new Xpp3Dom("configuration");
			Xpp3Dom child = new Xpp3Dom("files");
			Xpp3Dom fileChild = new Xpp3Dom("file");
			if(bwEdition.equals("cf")) {
				fileChild.setValue("${pcf.property.file}");
				child.addChild(fileChild);
			} else if(bwEdition.equals("docker")) {
				fileChild.setValue("${docker.property.file}");
				child.addChild(fileChild);
				if(module.getBwDockerModule().getDockerEnvs() != null && module.getBwDockerModule().getDockerEnvs().size() > 0) {
					Xpp3Dom fileChild1 = new Xpp3Dom("file");
					fileChild1.setValue("${docker.env.property.file}");
					child.addChild(fileChild1);
				}
				if(platfrom.equals("K8S")) {
					Xpp3Dom fileChild2 = new Xpp3Dom("file");
					fileChild2.setValue("${k8s.property.file}");
					child.addChild(fileChild2);
				}
			}
			config.addChild(child);
			plugin.setConfiguration(config);
			build.addPlugin(plugin);
		} else {
			Xpp3Dom config = (Xpp3Dom) plugin.getConfiguration();
			if (config != null && config.getChild("files") != null) {
				Xpp3Dom files=(Xpp3Dom) config.getChild("files");
				Xpp3Dom[] childs = files.getChildren();
				if (files != null && files.getChild("file") != null) {
					if(bwEdition.equals("cf")) {
						boolean found = false;
						for(int i = 0; i < childs.length; i++) {
							Xpp3Dom child = childs[i];
							if(child.getValue().equals("${pcf.property.file}")) {
								found = true;
								break;
							}
						}
						if(!found) {
							Xpp3Dom fileChild = new Xpp3Dom("file");
							fileChild.setValue("${pcf.property.file}");
							files.addChild(fileChild);
						}
					} else if(bwEdition.equals("docker")) {
						boolean dfound = false;
						boolean defound = false;
						boolean k8sfound = false;
						int envIndex = 0;
						int k8sIndex = 0;
						for(int i = 0; i < childs.length; i++) {
							Xpp3Dom child = childs[i];
							if(child.getValue().equals("${docker.property.file}")) {
								dfound = true;
							}
							if(child.getValue().equals("${docker.env.property.file}")) {
								defound = true;
								envIndex = i;
							}
							if(child.getValue().equals("${k8s.property.file}")) {
								k8sfound = true;
								k8sIndex = i;
							}
						}
						if(!dfound) {
							Xpp3Dom fileChild = new Xpp3Dom("file");
							fileChild.setValue("${docker.property.file}");
							files.addChild(fileChild);
						}
						if(module.getBwDockerModule().getDockerEnvs() != null && module.getBwDockerModule().getDockerEnvs().size() > 0)	{
							if(!defound) {
								Xpp3Dom fileChild = new Xpp3Dom("file");
								fileChild.setValue("${docker.env.property.file}");
								files.addChild(fileChild);
							}
						} else {
							if(defound) {
								files.removeChild(envIndex);
								//Delete existing properties files for docker-env
								if(k8sIndex > envIndex) { 
									k8sIndex--;  //If K8s index is > the removed index of env, then decrement
								}
							}
						}

						if(platfrom.equals("K8S")) {
							if(!k8sfound) {
								Xpp3Dom fileChild = new Xpp3Dom("file");
								fileChild.setValue("${k8s.property.file}");
								files.addChild(fileChild);
							}
						} else {
							if(k8sfound) {
								files.removeChild(k8sIndex);
								//Delete existing properties files for k8s
							}
						}
					}
				}
			}
		}
	}

	protected void addDockerK8SMavenPlugin(Build build, boolean skip) {
		if(!skip) {
			createK8SPropertiesFiles();
		}
		Plugin plugin = new Plugin();
		plugin.setGroupId("io.fabric8");
		plugin.setArtifactId("fabric8-maven-plugin");
		plugin.setVersion("3.5.41");
		
		Xpp3Dom config = new Xpp3Dom("configuration");
		Xpp3Dom child = new Xpp3Dom("skip");
		child.setValue(String.valueOf(skip));
		config.addChild(child);
	
		plugin.setConfiguration(config);
		build.addPlugin(plugin);
	}

	protected void addDockerWithSkipMavenPlugin(Build build) {
		Plugin plugin = new Plugin();
		plugin.setGroupId("io.fabric8");
		plugin.setArtifactId("docker-maven-plugin");
		plugin.setVersion("0.26.1");
		Xpp3Dom config = new Xpp3Dom("configuration");
		Xpp3Dom child = new Xpp3Dom("skip");
		child.setValue("true");
		config.addChild(child);
		plugin.setConfiguration(config);
		build.addPlugin(plugin);
	}

	protected void addDockerMavenPlugin(Build build) {
		//Create properties file for Dev and Prod environment
		createDockerPropertiesFiles();

		//Now just add Fabric8 Docker Maven plugin
		Plugin plugin = new Plugin();
		plugin.setGroupId("io.fabric8");
		plugin.setArtifactId("docker-maven-plugin");
		plugin.setVersion("0.26.1");

		Xpp3Dom config = new Xpp3Dom("configuration");

		Xpp3Dom child = new Xpp3Dom("skip");
		child.setValue("false");
		config.addChild(child);
		
		
		child = new Xpp3Dom("autoPull");
		child.setValue("${bwdocker.autoPullImage}");
		config.addChild(child);

		child = new Xpp3Dom("dockerHost");
		child.setValue("${bwdocker.host}");
		config.addChild(child);

		child = new Xpp3Dom("certPath");
		child.setValue("${bwdocker.certPath}");
		config.addChild(child);

		child = new Xpp3Dom("images");
		Xpp3Dom imageChild = new Xpp3Dom("image");
		Xpp3Dom child1 = new Xpp3Dom("alias");
		child1.setValue("${bwdocker.containername}");
		imageChild.addChild(child1);

		child1 = new Xpp3Dom("name");
		child1.setValue("${docker.image}");
		imageChild.addChild(child1);

		Xpp3Dom buildchild = new Xpp3Dom("build");
		Xpp3Dom child2 = new Xpp3Dom("from");
		child2.setValue("${bwdocker.from}");
		buildchild.addChild(child2);

		child2 = new Xpp3Dom("maintainer");
		child2.setValue("${bwdocker.maintainer}");
		buildchild.addChild(child2);

		Xpp3Dom assemblychild = new Xpp3Dom("assembly");
		Xpp3Dom child22 = new Xpp3Dom("basedir");
		child22.setValue("/");
		assemblychild.addChild(child22);

		child22 = new Xpp3Dom("descriptorRef");
		child22.setValue("artifact");
		assemblychild.addChild(child22);
		buildchild.addChild(assemblychild);

		Xpp3Dom tagchild = new Xpp3Dom("tags");
		Xpp3Dom child23 = new Xpp3Dom("tag");
		child23.setValue("latest");
		tagchild.addChild(child23);

		buildchild.addChild(tagchild);

		Xpp3Dom portchild = new Xpp3Dom("ports");
		Xpp3Dom child24 = new Xpp3Dom("port");
		child24.setValue("8080");
		portchild.addChild(child24);

		buildchild.addChild(portchild);

		// IF Volume exist
		List<String> volumes = module.getBwDockerModule().getDockerVolumes();
		if(volumes != null && volumes.size() > 0) {
			Xpp3Dom volchild = new Xpp3Dom("volumes");
			for(int i = 0; i < volumes.size(); i++) {
				Xpp3Dom child25 = new Xpp3Dom("volume");
				child25.setValue("${bwdocker.volume.v"+i+"}");
				volchild.addChild(child25);
			}
			buildchild.addChild(volchild);
		}
		imageChild.addChild(buildchild);

		Xpp3Dom runchild = new Xpp3Dom("run");
		Xpp3Dom child3 = new Xpp3Dom("namingStrategy");
		child3.setValue("alias");
		runchild.addChild(child3);

		// IF Ports exist
		List<String> ports = module.getBwDockerModule().getDockerPorts();
		if(ports != null && ports.size() > 0) {
			Xpp3Dom runportchild = new Xpp3Dom("ports");
			for(int i = 0; i < ports.size(); i++) {
				Xpp3Dom child31 = new Xpp3Dom("port");
				child31.setValue("${bwdocker.port.p"+i+"}");
				runportchild.addChild(child31);
			}
			runchild.addChild(runportchild);
		}

		// IF Links exist
		List<String> links = module.getBwDockerModule().getDockerLinks();
		if(links != null && links.size() > 0) {
			Xpp3Dom linkchild = new Xpp3Dom("links");
			for(int i = 0; i < links.size(); i++) {
				Xpp3Dom child32 = new Xpp3Dom("link");
				child32.setValue("${bwdocker.link.l"+i+"}");
				linkchild.addChild(child32);
			}
			runchild.addChild(linkchild);
		}

		//IF env variable exist
		if(module.getBwDockerModule().getDockerEnvs() != null && module.getBwDockerModule().getDockerEnvs().size() > 0) {
			createDockerEnvVarPropertiesFiles();
			Xpp3Dom envVarChild = new Xpp3Dom("envPropertyFile");
			envVarChild.setValue("${docker.env.property.file}");
			runchild.addChild(envVarChild);
		}
		imageChild.addChild(runchild);
		child.addChild(imageChild);
		config.addChild(child);
		plugin.setConfiguration(config);
		build.addPlugin(plugin);
	}

	private void createDockerEnvVarPropertiesFiles() {
		try {
			Properties properties = new Properties();
			// Add k8s env variables
			Map<String, String> dockEnvVars = module.getBwDockerModule().getDockerEnvs();
			if(!dockEnvVars.isEmpty()) {
				for (String key : dockEnvVars.keySet()) {
					properties.setProperty(key, dockEnvVars.get(key));
				}
			}

			File devfile = new File(getWorkspacepath() + File.separator + "docker-host-env-dev.properties");
			if(devfile.exists()) {
				devfile.delete();
			}
			boolean done = devfile.createNewFile();
			if(done) {
				FileOutputStream fileOut = new FileOutputStream(devfile);
				properties.store(fileOut, "Your Docker Host Environment Variables properties");
				fileOut.close();

				File prodfile = new File(getWorkspacepath()+ File.separator + "docker-host-env-prod.properties");
				if(prodfile.exists()) {
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
	
	private void createK8SPropertiesFiles() {
		try {
			Properties properties = new Properties();
			//Add platform properties

			String platform = module.getBwDockerModule().getPlatform();
			properties.setProperty("fabric8.template", module.getBwk8sModule().getRcName());
			properties.setProperty("fabric8.replicationController.name", module.getBwk8sModule().getRcName());
			properties.setProperty("fabric8.replicas", module.getBwk8sModule().getNumOfReplicas());
			properties.setProperty("fabric8.label.project", module.getBwk8sModule().getRcName());
			properties.setProperty("fabric8.label.group", module.getBwk8sModule().getRcName());
			properties.setProperty("fabric8.label.container", module.getBwk8sModule().getRcName());
			properties.setProperty("fabric8.container.name", module.getBwk8sModule().getRcName());
			properties.setProperty("fabric8.service.name", module.getBwk8sModule().getServiceName());
			if(module.getBwk8sModule().getServiceType()==null || module.getBwk8sModule().getServiceType().isEmpty()){
				properties.setProperty("fabric8.service.type", "LoadBalancer");
			}
			else{
			properties.setProperty("fabric8.service.type", module.getBwk8sModule().getServiceType());
			}
			properties.setProperty("fabric8.service.port", "80");
			properties.setProperty("fabric8.provider", "Tibco");
			properties.setProperty("fabric8.service.containerPort", module.getBwk8sModule().getContainerPort());
			properties.setProperty("fabric8.namespace", module.getBwk8sModule().getK8sNamespace());
			properties.setProperty("fabric8.apply.namespace", module.getBwk8sModule().getK8sNamespace());
			if(module.getBwk8sModule().getResourcesLocation()!=null){
			properties.setProperty("fabric8.resources.location", module.getBwk8sModule().getResourcesLocation());
			}

			//Add k8s env variables
			Map<String, String> k8sEnvVars = module.getBwk8sModule().getK8sEnvVariables();
			if(!k8sEnvVars.isEmpty()) {
				for (String key : k8sEnvVars.keySet()) {
					String fabric8Key = "fabric8.env." + key;
					properties.setProperty(fabric8Key, k8sEnvVars.get(key));
				}
			}

			File devfile = new File(getWorkspacepath() + File.separator + "k8s-dev.properties");
			if(devfile.exists()) {
				devfile.delete();
			}
			boolean done = devfile.createNewFile();
			if(done) {
				FileOutputStream fileOut = new FileOutputStream(devfile);
				String msg = "Your " + platform + " platform properties";
				properties.store(fileOut, msg);
				fileOut.close();
				File prodfile = new File(getWorkspacepath() + File.separator + "k8s-prod.properties");
				if(prodfile.exists()) {
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

	private void createDockerPropertiesFiles() {
		try {
			Properties properties = new Properties();
			// Add Docker properties
			properties.setProperty("bwdocker.host", module.getBwDockerModule().getDockerHost());
			properties.setProperty("bwdocker.certPath", module.getBwDockerModule().getDockerHostCertPath());
			properties.setProperty("docker.image", module.getBwDockerModule().getDockerImageName());
			properties.setProperty("bwdocker.containername", module.getBwDockerModule().getDockerAppName());
			properties.setProperty("bwdocker.from", module.getBwDockerModule().getDockerImageFrom());
			properties.setProperty("bwdocker.autoPullImage", (module.getBwDockerModule().isAutoPullImage()?"true":"false"));
			properties.setProperty("bwdocker.maintainer", module.getBwDockerModule().getDockerImageMaintainer());
			

			List<String> volumes = module.getBwDockerModule().getDockerVolumes();
			if(volumes != null && volumes.size() > 0) {
				for(int i = 0; i < volumes.size(); i++) {
					properties.setProperty("bwdocker.volume.v"+i, volumes.get(i));
				}
			}

			List<String> links = module.getBwDockerModule().getDockerLinks();
			if(links != null && links.size() > 0) {
				for(int i = 0; i < links.size(); i++) {
					properties.setProperty("bwdocker.link.l"+i, links.get(i));
				}
			}

			List<String> ports = module.getBwDockerModule().getDockerPorts();
			if(ports != null && ports.size() > 0) {
				for(int i = 0; i < ports.size(); i++) {
					properties.setProperty("bwdocker.port.p"+i, ports.get(i));
				}
			}

			//Create docker properties file
			File dkrdevfile = new File(getWorkspacepath() + File.separator + "docker-dev.properties");
			if(dkrdevfile.exists()) {
				dkrdevfile.delete();
			}
			boolean dkrdone=dkrdevfile.createNewFile();
			if(dkrdone) {
				FileOutputStream fileOut = new FileOutputStream(dkrdevfile);
				String msg = "Docker host properties";
				properties.store(fileOut, msg);
				fileOut.close();

				File dkrprodfile = new File(getWorkspacepath() + File.separator + "docker-prod.properties");
				if(dkrprodfile.exists()) {
					dkrprodfile.delete();
				}
				Files.copy(dkrdevfile.toPath(), dkrprodfile.toPath());
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void createPCFPropertiesFiles() {
		try {
			Properties properties = new Properties();
			properties.setProperty("bwpcf.server", module.getBwpcfModule().getCredString());
			properties.setProperty("bwpcf.target", module.getBwpcfModule().getTarget());
			properties.setProperty("bwpcf.trustSelfSignedCerts", "true");
			properties.setProperty("bwpcf.org", module.getBwpcfModule().getOrg());
			properties.setProperty("bwpcf.appName", module.getBwpcfModule().getAppName());
			properties.setProperty("bwpcf.space", module.getBwpcfModule().getSpace());

			if(module.getBwpcfModule().getAppName() != null && !module.getBwpcfModule().getAppName().isEmpty()) {
				properties.setProperty("bwpcf.url", getPCFAppURL(module.getBwpcfModule().getAppName()));
			} else {
				properties.setProperty("bwpcf.url", getPCFAppDefaultURL());
			}
			properties.setProperty("bwpcf.instances", module.getBwpcfModule().getInstances());
			properties.setProperty("bwpcf.memory", module.getBwpcfModule().getMemory());
			properties.setProperty("bwpcf.diskQuota", module.getBwpcfModule().getDiskQuota());
			properties.setProperty("bwpcf.buildpack", module.getBwpcfModule().getBuildpack());

			//Add cf env variables
			Map<String, String> cfEnvVars = module.getBwpcfModule().getCfEnvVariables();
			if(!cfEnvVars.isEmpty()) {
				int i = 0;
				for (String key : cfEnvVars.keySet()) {
					String cfKey = "bwpcf.env." + i;
					properties.setProperty(cfKey, cfEnvVars.get(key));
					i++;
				}
			}

			File devfile = new File(getWorkspacepath() + File.separator + "pcfdev.properties");
			if(devfile.exists()) {
				devfile.delete();
			}
			boolean done=devfile.createNewFile();
			if(done) {
				FileOutputStream fileOut = new FileOutputStream(devfile);
				properties.store(fileOut, "PCF Properties");

				File prodfile = new File(getWorkspacepath()+ File.separator + "pcfprod.properties");
				if(prodfile.exists()) {
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

	private String getWorkspacepath() {
		for (BWModule module : project.getModules()) {
			if(module.getType() == BWModuleType.Application) {
				String pomloc=module.getPomfileLocation().toString();
				String workspace=pomloc.substring(0,pomloc.indexOf("pom.xml"));
				return workspace;
			}
		}
		return null;
	}

	protected void addPCFMavenPlugin(Build build) {
		//Create properties file for Dev and Prod environment
		createPCFPropertiesFiles();

		//Now just add PCF Maven plugin
		Plugin plugin = new Plugin();
		plugin.setGroupId("org.cloudfoundry");
		plugin.setArtifactId("cf-maven-plugin");
		plugin.setVersion("1.1.3");

		Xpp3Dom config=new Xpp3Dom("configuration");

		Xpp3Dom child = new Xpp3Dom("server");
		child.setValue("${bwpcf.server}");
		config.addChild(child);

		child = new Xpp3Dom("target");
		child.setValue("${bwpcf.target}");
		config.addChild(child);

		child = new Xpp3Dom("trustSelfSignedCerts");
		child.setValue("${bwpcf.trustSelfSignedCerts}");
		config.addChild(child);

		child = new Xpp3Dom("org");
		child.setValue("${bwpcf.org}");
		config.addChild(child);

		child = new Xpp3Dom("space");
		child.setValue("${bwpcf.space}");
		config.addChild(child);

		child = new Xpp3Dom("appname");
		child.setValue("${bwpcf.appName}");
		config.addChild(child);

		child = new Xpp3Dom("url");
		child.setValue("${bwpcf.url}");
		config.addChild(child);

		child = new Xpp3Dom("instances");
		child.setValue("${bwpcf.instances}");
		config.addChild(child);

		child = new Xpp3Dom("skip");
		child.setValue("false");
		config.addChild(child);

		child = new Xpp3Dom("memory");
		child.setValue("${bwpcf.memory}");
		config.addChild(child);
		
		child = new Xpp3Dom("diskQuota");
		child.setValue("${bwpcf.diskQuota}");
		config.addChild(child);

		child = new Xpp3Dom("buildpack");
		child.setValue("${bwpcf.buildpack}");
		config.addChild(child);

		Map<String, String> cfEnvVars=module.getBwpcfModule().getCfEnvVariables();
		if(!cfEnvVars.isEmpty()) {
			child = new Xpp3Dom("env");
			int i = 0;
			for (String key : cfEnvVars.keySet()) {
				Xpp3Dom child1 = new Xpp3Dom(key);
				String cfVal = "${bwpcf.env." + i + "}";
				child1.setValue(cfVal);
				child.addChild(child1);
				i++;
			}
			config.addChild(child);
		}

		List<BWPCFServicesModule> services=module.getBwpcfModule().getServices();
		if(services != null && services.size() > 0) {
			child = new Xpp3Dom("services");
			for(BWPCFServicesModule service: services) {
				Xpp3Dom serviceChild = new Xpp3Dom("service");
				Xpp3Dom child1 = new Xpp3Dom("name");
				child1.setValue(service.getServiceName());
				serviceChild.addChild(child1);

				child1 = new Xpp3Dom("label");
				child1.setValue(service.getServiceLabel());
				serviceChild.addChild(child1);

				child1 = new Xpp3Dom("version");
				child1.setValue(service.getServiceVersion());
				serviceChild.addChild(child1);

				child1 = new Xpp3Dom("plan");
				child1.setValue(service.getServicePlan());
				serviceChild.addChild(child1);

				child.addChild(serviceChild);
			}
			config.addChild(child);
		}
		plugin.setConfiguration(config);	
		build.addPlugin(plugin);
	}

	private String getPCFAppURL(String appName) {
		appName = appName.replace(".", "-");
		String domainStr = module.getBwpcfModule().getTarget();
		String protoDom = domainStr.substring(0, domainStr.indexOf("."));
		String domain = domainStr.replace(protoDom, "");
		return appName + domain;
	}

	private String getPCFAppDefaultURL() {
		String url = module.getArtifactId().replace(".", "-");
		String domainStr = module.getBwpcfModule().getTarget();
		String protoDom = domainStr.substring(0, domainStr.indexOf("."));
		String domain = domainStr.replace(protoDom, "");
		return url + domain;
	}

	protected boolean dependencyExists(Dependency check) {
		List<Dependency> list =   model.getDependencies();
		for (Dependency dep : list) {
			if(dep.getArtifactId().equals(check.getArtifactId()) && dep.getGroupId().equals(check.getGroupId()) && dep.getVersion().equals(check.getVersion())) {
				return true;	
			}			 
		}
		return false;
	}

	protected void pluginExists() {
	}

	protected void generatePOMFile() throws Exception {
		FileWriter writer = new FileWriter(module.getPomfileLocation());
		try {
			new MavenXpp3Writer().write(writer, model);	
		} finally {
			writer.close();
		}
	}

	protected abstract String getPackaging();

	protected void initializeModel() {
		File pomFile = module.getPomfileLocation();
		model = readModel(pomFile);
		if(model == null) {
			model = new Model();
		}
	}

	protected Model readModel(File pomXmlFile) {
		Model model = null;
		try {
			Reader reader = new FileReader(pomXmlFile);
			try {
				MavenXpp3Reader xpp3Reader = new MavenXpp3Reader();
				model = xpp3Reader.read(reader);
			} finally {
				reader.close();
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return model;
	}

	protected void addBW6MavenProfile(Model model) {
		List<Profile> profiles = new ArrayList<Profile>();
		BWDeploymentInfo info = ((BWApplication) module).getDeploymentInfo();	
		for(String nameProfile : info.getProfiles()) {
			Profile profile = new Profile();
			if (nameProfile.replace(".substvar", "").equals("default")) {
				profile.setId("DEFAULT");
			} else {
				profile.setId(nameProfile.replace(".substvar", ""));	
			}
			if (nameProfile.equals(info.getProfile())) {
				Activation activation = new Activation();
				activation.setActiveByDefault(true);
				profile.setActivation(activation);
			}
			Properties properties = new Properties();
			properties.setProperty("profile", nameProfile);
			profile.setProperties(properties);
			profiles.add(profile);
		}
		model.setProfiles(profiles);
	}
}

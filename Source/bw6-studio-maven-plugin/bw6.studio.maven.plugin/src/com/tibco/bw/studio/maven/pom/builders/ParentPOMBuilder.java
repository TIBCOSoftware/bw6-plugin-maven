package com.tibco.bw.studio.maven.pom.builders;

import java.util.List;
import java.util.Map;

import org.apache.maven.model.Build;
import org.apache.maven.model.Plugin;

import com.tibco.bw.studio.maven.helpers.ManifestParser;
import com.tibco.bw.studio.maven.helpers.ModuleHelper;
import com.tibco.bw.studio.maven.helpers.ModuleOrderBuilder;
import com.tibco.bw.studio.maven.modules.model.BWModule;
import com.tibco.bw.studio.maven.modules.model.BWModuleType;
import com.tibco.bw.studio.maven.modules.model.BWProject;
import com.tibco.bw.studio.maven.wizard.BWProjectTypes;
import com.tibco.bw.studio.maven.wizard.MavenWizardContext;

public class ParentPOMBuilder extends AbstractPOMBuilder implements IPOMBuilder {

	@Override
	public void build(BWProject project, BWModule module) throws Exception {
		this.project = project;
		this.module = module;
		initializeModel();

		
		addPrimaryTags();
		model.setGroupId(module.getGroupId());
		model.setVersion(module.getVersion());
		// addProperties();
		addModules();
		addBuild();

		generatePOMFile();
	}

	protected void addBuild() {
		Build build = model.getBuild();
		if (build == null) {
			build = new Build();
		}
		
		
		List<Plugin> plugins = build.getPlugins();
		
		boolean reportPlugin = false;
		
		for (Plugin plugin : plugins) 
		{
			if (plugin.getArtifactId().equals("maven-site-plugin")) {
				reportPlugin = true;
				break;
			}
		}
		
		if( !reportPlugin )
		{
			Plugin repPlugin = new Plugin();
			repPlugin.setArtifactId("maven-site-plugin");
			repPlugin.setVersion("3.7.1");
			repPlugin.setGroupId("org.apache.maven.plugins");
			plugins.add(repPlugin);
		}

		if ( MavenWizardContext.INSTANCE.getSelectedType() == BWProjectTypes.PCF )
		{
			boolean cfplugin = false;
			
			for (Plugin plg : plugins) {
				if (plg.getArtifactId().equals("cf-maven-plugin")) {
					cfplugin = true;
				}
			}

			// Add only if doesn't exist
			if (!cfplugin) {
				addPCFWithSkipMavenPlugin(build);
			}
		} else if (MavenWizardContext.INSTANCE.getSelectedType() == BWProjectTypes.Docker) {
			//|| MavenWizardContext.INSTANCE.getSelectedType() == BWProjectTypes.K8S
			boolean dockerPlugin = false;
			
			for (Plugin plg : plugins) {
				if (plg.getArtifactId().equals("docker-maven-plugin")) {
					dockerPlugin = true;
				}
			}

			if (!dockerPlugin) {
				// Add docker and platform plugins if doesn't exist
				addDockerWithSkipMavenPlugin(build);

				String platform = "";
				for (BWModule module : project.getModules()) {
					if (module.getType() == BWModuleType.Application) {
						platform = module.getBwDockerModule().getPlatform();
					}
				}

				if (platform.equals("K8S")) {
					addDockerK8SMavenPlugin(build, true);
				} else if (platform.equals("Mesos")) {

				} else if (platform.equals("Swarm")) {

				}
			}
		}
		model.setBuild(build);
	}

	@Override
	protected String getPackaging() {
		return "pom";
	}

	protected void addModules() {
		while(model.getModules().size()>0){
			String module= model.getModules().get(0);
			model.removeModule(module);
		}			
		for (BWModule module : project.getModules()) {
			if (module.getType() == BWModuleType.PluginProject) {
				model.getModules().add(module.getToPath());
			}
		}

		ModuleOrderBuilder builder = new ModuleOrderBuilder();
		List<String> list = builder.getDependencyOrder(project);
		for (String str : list) {
			BWModule module = ModuleHelper.getModule(project.getModules(), str);
			model.getModules().add(module.getToPath());
		}
	}
}

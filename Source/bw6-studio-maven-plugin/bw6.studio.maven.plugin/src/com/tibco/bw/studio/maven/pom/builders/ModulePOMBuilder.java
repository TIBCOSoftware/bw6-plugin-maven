package com.tibco.bw.studio.maven.pom.builders;

import java.util.List;
import java.util.Map;

import org.apache.maven.model.Build;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Plugin;

import com.tibco.bw.studio.maven.helpers.ManifestParser;
import com.tibco.bw.studio.maven.helpers.ModuleHelper;
import com.tibco.bw.studio.maven.modules.model.BWAppModule;
import com.tibco.bw.studio.maven.modules.model.BWModule;
import com.tibco.bw.studio.maven.modules.model.BWModuleType;
import com.tibco.bw.studio.maven.modules.model.BWPluginModule;
import com.tibco.bw.studio.maven.modules.model.BWProject;
import com.tibco.bw.studio.maven.wizard.MavenWizardContext;

public class ModulePOMBuilder extends AbstractPOMBuilder implements IPOMBuilder {
	protected String bwEdition;

	@Override
	public void build(BWProject project, BWModule module) throws Exception {
		if (!module.isOverridePOM()) {
			return;
		}
		this.project = project;
		this.module = module;
		bwEdition=setBwEdition(module);
		initializeModel();
		addPrimaryTags();
		addParent(ModuleHelper.getParentModule(project.getModules()));
		addBuild();
		if (module instanceof BWAppModule) {
			addPaletteSharedDependency();
		}
		// addModuleDependencies();
		if (module instanceof BWPluginModule && ((BWPluginModule) module).isCustomXpath()) {
			addCustomXPathDependency();
		}
		// addProperties();
		generatePOMFile();
	}

	protected void addModuleDependencies() {
		if (module.getDepModules() == null || module.getDepModules().size() == 0) {
			return;
		}
		for (String moduleName : module.getDepModules()) {
			BWModule module = ModuleHelper.getModule(project.getModules(), moduleName);
			if (module != null) {
				addModuleDependency(module);
			}
		}
	}

	protected void addPaletteSharedDependency() {
		Dependency dep = new Dependency();
		dep.setGroupId("com.tibco.plugins");
		dep.setArtifactId("com.tibco.bw.palette.shared");
		dep.setVersion("6.1.100");
		dep.setScope("provided");
		if (!dependencyExists(dep)) {
			model.getDependencies().add(dep);
		}
	}

	protected void addModuleDependency(BWModule module) {
		Dependency dep = new Dependency();
		dep.setGroupId(module.getGroupId());
		dep.setArtifactId(module.getArtifactId());
		dep.setVersion(module.getVersion());
		if (!dependencyExists(dep)) {
			model.getDependencies().add(dep);
		}
	}

	protected void addCustomXPathDependency() {
		Dependency dep = new Dependency();
		dep.setGroupId("com.tibco.plugins");
		dep.setArtifactId("com.tibco.xml.cxf.common");
		dep.setVersion("1.3.200");
		dep.setScope("provided");
		if (!dependencyExists(dep)) {
			model.getDependencies().add(dep);
		}
	}

	protected void addSourceTarget(Build build) {
		build.setSourceDirectory("src");
		build.setOutputDirectory("target/classes");
	}

	protected void addBuild() {
		Build build = model.getBuild();
		if (build == null) {
			build = new Build();
			
		}
			addSourceTarget(build);
			addBW6MavenPlugin(build);
		if (bwEdition.equals("cf")) {
			boolean cfplugin = false;
			List<Plugin> plugins = build.getPlugins();
			for (Plugin plg : plugins) {
				if (plg.getArtifactId().equals("cf-maven-plugin")) {
					cfplugin = true;
				}
			}

			// Add only if doesn't exist
			if (!cfplugin) {
				addPCFWithSkipMavenPlugin(build);
			}
		} else if (bwEdition.equals("docker")) {
			boolean dockerPlugin = false;
			List<Plugin> plugins = build.getPlugins();
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
		return "bwmodule";
	}
}

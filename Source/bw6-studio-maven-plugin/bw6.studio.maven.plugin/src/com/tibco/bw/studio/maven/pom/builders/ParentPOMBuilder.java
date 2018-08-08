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
import com.tibco.zion.project.core.ContainerPreferenceProject;

public class ParentPOMBuilder extends AbstractPOMBuilder implements IPOMBuilder {
	private String bwEdition;

	@Override
	public void build(BWProject project, BWModule module) throws Exception {
		this.project = project;
		this.module = module;
		initializeModel();

		Map<String, String> manifest = ManifestParser.parseManifest(project.getModules().get(0).getProject());
		if (manifest.containsKey("TIBCO-BW-Edition") && manifest.get("TIBCO-BW-Edition").equals("bwcf")) {
			String targetPlatform = ContainerPreferenceProject.getCurrentContainer().getLabel();
			if (targetPlatform.equals("Cloud Foundry")) {
				bwEdition = "cf";
			} else {
				bwEdition = "docker";
		}
		} else
			bwEdition = "bw6";

		addPrimaryTags();
		model.setGroupId(module.getGroupId());
		model.setVersion(module.getVersion());
		// addProperties();
		addModules();
		if (bwEdition.equals("cf") || bwEdition.equals("docker")) {
			addBuild();
		}
		generatePOMFile();
	}

	protected void addBuild()  {
		Build build = model.getBuild();
		if (build == null) {
			build = new Build();
		}

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
		return "pom";
	}

	protected void addModules() {
		if (model.getModules().size() > 0) {
			return;
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

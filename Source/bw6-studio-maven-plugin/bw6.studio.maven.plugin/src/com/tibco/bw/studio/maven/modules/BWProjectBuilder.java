package com.tibco.bw.studio.maven.modules;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.maven.model.Model;
import org.apache.maven.model.Plugin;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.pde.internal.core.project.PDEProject;

import com.tibco.bw.design.external.dependencies.BWExternalDependenciesHelper;
import com.tibco.bw.studio.maven.helpers.FileHelper;
import com.tibco.bw.studio.maven.helpers.ManifestParser;
import com.tibco.bw.studio.maven.helpers.POMHelper;
import com.tibco.bw.studio.maven.helpers.VersionHelper;
import com.tibco.bw.studio.maven.modules.model.BWAppModule;
import com.tibco.bw.studio.maven.modules.model.BWApplication;
import com.tibco.bw.studio.maven.modules.model.BWDeploymentInfo;
import com.tibco.bw.studio.maven.modules.model.BWModule;
import com.tibco.bw.studio.maven.modules.model.BWParent;
import com.tibco.bw.studio.maven.modules.model.BWPluginModule;
import com.tibco.bw.studio.maven.modules.model.BWProject;
import com.tibco.bw.studio.maven.modules.model.BWProjectType;
import com.tibco.bw.studio.maven.modules.model.BWSharedModule;
import com.tibco.bw.studio.maven.modules.model.BWTestInfo;
import com.tibco.bw.studio.maven.preferences.MavenDefaultsPreferencePage;
import com.tibco.bw.studio.maven.preferences.MavenProjectPreferenceHelper;
import com.tibco.bw.studio.maven.preferences.MavenPropertiesFileDefaults;
import com.tibco.bw.studio.maven.wizard.BWProjectTypes;

public class BWProjectBuilder {
	List<BWModuleParser.BWModuleData> moduleData;
	List<BWModule> moduleList = new ArrayList<BWModule>();
	private Map<String, List<String>> dependencies = new HashMap<String, List<String>>();
	
	public BWProject buildSMProject(IProject smProject) throws Exception{
		BWSharedModule module = new BWSharedModule();
		
		Map<String,String> headers = ManifestParser.parseManifest(smProject);
		buildCommonInfo(smProject, module, headers, null);
		if(headers.get("Require-Capability") != null) {
			computeDependencies(headers.get("Require-Capability"), module);	
		}
		module.setDepModules(dependencies.get(module.getArtifactId()));		
		moduleList.add(module);
		
		BWProject project = new BWProject(BWProjectType.SharedModule);
		project.setDependencies(dependencies);
		project.setModules(moduleList);

		return project;
	}
	
	public BWProject build(IProject applicationProject) throws Exception {
		buildModuleData(applicationProject);
		BWApplication application = buildApplication(applicationProject);
		buildModules(application);		
		buildParent(application);
		BWProject project = new BWProject();
		project.setDependencies(dependencies);
		project.setModules(moduleList);
		return project;
	}

	private void buildModuleData(IProject project) {
		IFile file = project.getFile("META-INF/TIBCO.xml");
		moduleData = BWModuleParser.INSTANCE.parseBWModules(file.getRawLocation().toFile());
	}

	private BWParent buildParent(BWApplication application) throws Exception {
		BWParent module = new BWParent();
		module.setGroupId(MavenProjectPreferenceHelper.INSTANCE.getDefaultGroupID(MavenPropertiesFileDefaults.INSTANCE.getDefaultGroupID("com.tibco.bw")));
		module.setArtifactId(application.getArtifactId() + ".parent");
		module.setVersion(application.getVersion());
		File parent = new File(application.getPomfileLocation().getParentFile().getParent() + File.separator + application.getArtifactId() + ".parent");
		if(application.isPomExists()) {
			try {
				String pom = application.getMavenModel().getParent().getRelativePath();
				if(pom.indexOf("\\") > 0) {
					pom = pom.replace("\\", "/");
				}
				File pomFile = new File(application.getProject().getLocation().toFile().toString() + File.separator + pom, "pom.xml");
				if(pomFile.getCanonicalFile().exists()) {
					parent = pomFile.getCanonicalFile().getParentFile();
					IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(parent.getName());
					module.setProject(project);
				}
			} catch(Exception e) {
			}
		}
		if(!parent.exists()) {
			parent.mkdirs();	
		}

		File pomFileAbs = new File (parent, "pom.xml");
		if(!pomFileAbs.exists()) {
			pomFileAbs.createNewFile();
		} else {
			module.setPomExists(true);
			Model model = POMHelper.readModelFromPOM(pomFileAbs);
			if(model != null) {
				module.setMavenModel(model);
				module.setArtifactId(model.getArtifactId());
				module.setGroupId(MavenProjectPreferenceHelper.INSTANCE.getDefaultGroupID(MavenPropertiesFileDefaults.INSTANCE.getDefaultGroupID("com.tibco.bw")));
			}
		}
		module.setPomfileLocation(pomFileAbs);
		moduleList.add(module);
		return module;
	}

	private BWApplication buildApplication(IProject applicationProject) throws Exception {
		BWApplication application = new BWApplication();
		Map<String,String> headers = ManifestParser.parseManifest(applicationProject);
		buildCommonInfo(applicationProject, application, headers, application);
		loadDeploymentInfo(application);
		moduleList.add(application);
		return application;
	}

	private void loadDeploymentInfo(BWApplication application) {
		
		Model model = application.getMavenModel();
		
BWTestInfo testInfo = application.getTestInfo();
		
		if( model != null && model.getProperties() != null )
		{
			testInfo.setSkipTests( (String) model.getProperties().get( "skipTests" ) );
			testInfo.setFailIfNoTests((String) model.getProperties().get( "failIfNoTests" ) );		
			testInfo.setTibcoHome((String) model.getProperties().get( "tibco.Home" ) );		
			testInfo.setBwHome( (String) model.getProperties().get( "bw.Home" ) );
		
			application.setProjectType( (String) model.getProperties().get( "project.type" ) != null && ! ((String) model.getProperties().get( "project.type" )).isEmpty() ?  BWProjectTypes.valueOf((String) model.getProperties().get( "project.type" )) : null);
		}
		
		BWDeploymentInfo info = application.getDeploymentInfo();
		
		if(model == null) {
			return;
		}
		Plugin plugin = null;
		for(Plugin p : model.getBuild().getPlugins()) {
			if(p.getArtifactId().equals("bw6-maven-plugin") && p.getGroupId().equals("com.tibco.plugins")) {
				plugin = p;
			}
		}
		if(plugin == null) {
			return;
		}

		Xpp3Dom dom = (Xpp3Dom) plugin.getConfiguration();
		if(dom == null) {
			return;
		}

		info.setDeployToAdmin(getBooleanValuefromDom("deployToAdmin", dom, model));
		info.setAgentHost(getStringValuefromDom("agentHost", dom, model));
		info.setAgentPort(getStringValuefromDom("agentPort", dom, model));
		info.setAgentAuth(getStringValuefromDom("agentAuth", dom, model));
		info.setAgentUsername(getStringValuefromDom("agentUsername", dom, model));
		info.setAgentPassword(getStringValuefromDom("agentPassword", dom, model));
		info.setAgentSSL(getBooleanValuefromDom("agentSSL", dom, model));
		info.setTrustPath(getStringValuefromDom("truststorePath", dom, model));
		info.setTrustPassword(getStringValuefromDom("truststorePassword", dom, model));
		info.setKeyPath(getStringValuefromDom("keystorePath", dom, model));
		info.setKeyPassword(getStringValuefromDom("keystorePassword", dom, model));
		info.setDomain(getStringValuefromDom("domain", dom, model));
		info.setDomainDesc(getStringValuefromDom("domainDesc", dom, model));
		info.setAppspace(getStringValuefromDom("appSpace", dom, model));
		info.setAppspaceDesc(getStringValuefromDom("appSpaceDesc", dom, model));
		info.setAppNode(getStringValuefromDom("appNode", dom, model));
		info.setAppNodeDesc(getStringValuefromDom("appNodeDesc", dom, model));
		info.setHttpPort(getStringValuefromDom("httpPort", dom, model));
		info.setOsgiPort(getStringValuefromDom("osgiPort", dom, model));
		info.setProfile(getStringValuefromDom("profile", dom, model));
		info.setRedeploy(getBooleanValuefromDom("redeploy", dom, model));
		info.setBackup(getBooleanValuefromDom("backup", dom, model));
		info.setBackupLocation(getStringValuefromDom("backupLocation", dom, model));
	}

	private String getStringValuefromDom(String name, Xpp3Dom dom, Model model) {
		Xpp3Dom child = dom.getChild(name);
		if(child == null) {
			return "";
		}
		if(child.getValue().startsWith("$")) {
			String value = model.getProperties().getProperty(child.getName());
			if(value != null) {
				return value;
			}
		} else {
			return child.getValue();
		}
		return "";
	}

	private boolean getBooleanValuefromDom(String name, Xpp3Dom dom, Model model) {
		String value = getStringValuefromDom(name, dom, model);
		try {
			return Boolean.parseBoolean(value);
		} catch(Exception e) {
			return false;
		}
	}

	private void buildModules(BWApplication application) throws Exception {
		for(BWModuleParser.BWModuleData data : moduleData) {
			
			IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(data.getModuleName());
			
			//When a module is an external Shared Module, it should be ignored.
			if(BWExternalDependenciesHelper.INSTANCE.isExternalProject(project)){
				continue;
			}
			
			BWModule module = null;
			switch(data.getModuleType()) {
			case AppModule:
				module = new BWAppModule();
				break;
			case SharedModule:
				module = new BWSharedModule();
				break;
			case PluginProject:
				module = new BWPluginModule();
				break;
			default:
				module = new BWAppModule();
				break;
			}

			Map<String,String> headers = ManifestParser.parseManifest(project);
			buildCommonInfo(project, module, headers, application);
			if(headers.get("Require-Capability") != null) {
				computeDependencies(headers.get("Require-Capability"), module);	
			}
			module.setDepModules(dependencies.get(module.getArtifactId()));		

			if(module instanceof BWPluginModule) {
				boolean isCustomXpath = checkForCustomXPath(project);
				((BWPluginModule)module).setCustomXpath(isCustomXpath);
			}
			moduleList.add(module);
		}
	}

	private BWModule buildCommonInfo(IProject project, BWModule module, Map<String,String> headers, BWApplication application) throws IOException {
		module.setProject(project); 
		module.setProjectName(project.getName());
	
		String artifactId = headers.get("Bundle-SymbolicName");
		if(artifactId.indexOf(";") != -1) {
			artifactId = artifactId.substring(0, (artifactId.indexOf(";")));
		}
		module.setArtifactId(artifactId);
		module.setName((headers.get("Bundle-Name")));
		module.setVersion(VersionHelper.getOSGi2MavenVersion((headers.get("Bundle-Version"))));
		module.setGroupId(MavenProjectPreferenceHelper.INSTANCE.getDefaultGroupID(MavenPropertiesFileDefaults.INSTANCE.getDefaultGroupID("com.tibco.bw")));
		
		IFile pomFile = project.getFile("/pom.xml");
		File pomFileAbs = pomFile.getRawLocation().toFile();
		if(!pomFileAbs.exists()) {
			pomFileAbs.createNewFile();
		} else {
			module.setPomExists(true);
			module.setMavenModel(POMHelper.readModelFromPOM(pomFileAbs));
		}
		module.setPomfileLocation(pomFileAbs);
		if(application != null){
			setRelativePaths(project, module, application);
		}
		return module;
	}

	private void setRelativePaths(IProject project, BWModule module, BWApplication application) {
		String projectLocation = project.getLocation().toFile().toString();
		String parentLocation = application.getPomfileLocation().getParentFile().getParent() + File.separator + application.getArtifactId() + ".parent";
		if(application.isPomExists()) {
			try {
				String pom = application.getMavenModel().getParent().getRelativePath();
				File pomFile = new File(application.getProject().getLocation().toFile().toString() + File.separator + pom, "pom.xml");
				if(pomFile.getCanonicalFile().exists()) {
					parentLocation = pomFile.getCanonicalFile().getParent();
				}
			} catch(Exception e) {
			}
		}
		String relativePathFrom = FileHelper.getRelativePath(parentLocation, projectLocation);
		String relativePathTo = FileHelper.getRelativePath(projectLocation, parentLocation);
		module.setFromPath(relativePathFrom);
		module.setToPath(relativePathTo);  
	}

	private boolean checkForCustomXPath(IProject project) {
		IFile pluginXml = PDEProject.getPluginXml(project);
		if(!pluginXml.exists()) {
			return false;
		}
		try {
			InputStream stream = pluginXml.getContents();
			String fileContents = IOUtils.toString(pluginXml.getContents());
			stream.close();	
			if(fileContents.indexOf("com.tibco.xml.cxf.common.customXPathFunction") != -1) {
				return true;
			}
		} catch(Exception e) {
		}
		return false;
	}

	private void computeDependencies(String capabiltiies, BWModule module) {
		String[] capArray = capabiltiies.split(",");
		for(String capability : capArray) {
			capability = capability.trim();

			if(capability.contains("com.tibco.bw.module")) {
				if (dependencies.containsKey(module.getArtifactId())) {
					dependencies.get(module.getArtifactId());
				} else {
					dependencies.put(module.getArtifactId(), new ArrayList<String>());
				}
				int nameIndex = capability.indexOf("name=");
				int endIndex = capability.indexOf(")", nameIndex);
				String pluginName = capability.substring(nameIndex + 5, endIndex);
				dependencies.get(module.getArtifactId()).add(pluginName);
			}
		}		
	}
}

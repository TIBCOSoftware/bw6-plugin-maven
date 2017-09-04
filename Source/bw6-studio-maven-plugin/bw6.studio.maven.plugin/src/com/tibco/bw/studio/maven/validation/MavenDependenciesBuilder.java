package com.tibco.bw.studio.maven.validation;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.m2e.core.embedder.IMaven;
import org.eclipse.m2e.core.embedder.IMavenConfiguration;
import org.eclipse.m2e.core.internal.embedder.MavenImpl;
import org.eclipse.m2e.core.internal.preferences.MavenConfigurationImpl;
import org.eclipse.pde.internal.core.PDECore;

import com.tibco.bw.design.api.BWAbstractBuilder;
import com.tibco.bw.design.util.ModelHelper;
import com.tibco.bw.studio.maven.helpers.POMHelper;
import com.tibco.xpd.resources.XpdProjectResourceFactory;
import com.tibco.xpd.resources.XpdResourcesPlugin;

public class MavenDependenciesBuilder extends BWAbstractBuilder{
	
	protected IMaven maven;
	
	protected static Map<String, String> dependenciesMap;
	
	public static final String MAVEN_NATURE_ID = "org.eclipse.m2e.core.maven2Nature";
	
	@Override
	public void doBuild(int kind, IProject project, IProgressMonitor monitor) {
				
		if(project == null){
			return;
		}
		
		if(!isMavenProject(project)){
			return;
		}
			
		if(!isLocalProject(project)){
			return;
		}
		
		File pomFileAbs = getPOMFile(project);

		if(pomFileAbs == null || !pomFileAbs.exists()){
			return;
		}
		
		Model mavenModel = POMHelper.readModelFromPOM(pomFileAbs);
		List<Dependency> dependencyList = mavenModel.getDependencies();
		List<File> jarDependencies = new ArrayList<File>();
		
		for(Dependency dependency : dependencyList){
			
//			String dependencyId = dependency.getGroupId() + "." + dependency.getArtifactId();
//			String version = dependency.getVersion();
//			if(isDependencyCreated(dependencyId, version)){
//				continue;
//			}
			
			File jarFile = getDependencyFile(dependency);
			if(isSharedModule(jarFile)){
				jarDependencies.add(jarFile);
			}
		}
		
		List<IProject> projects = new ArrayList<IProject>();
		for(File file : jarDependencies){
			IProject p = createProjectFromJar(file);
			projects.add(p);
		}
		
		addModulesToProjectDependencies(projects, project);
		
	}
	
	protected boolean isLocalProject(IProject project){
		boolean isLocal = true;
		
		try {
			String value = project.getPersistentProperty(PDECore.EXTERNAL_PROJECT_PROPERTY);
			if(value != null){
				isLocal = false;
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}
		
		return isLocal;
	}
	
	protected boolean isMavenProject(IProject project){
		boolean isMavenProject = false;
		try {
			IProjectNature nature = project.getNature(MAVEN_NATURE_ID);
			if(nature != null){
				isMavenProject = true;
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}
		
		return isMavenProject;
	}
	
	protected File getPOMFile(IProject project){
		IFile pomFile = project.getFile("/pom.xml");
		
		if(!pomFile.exists()){
			return null;
		}
		
		return pomFile.getRawLocation().toFile();
	}
	
	protected File getDependencyFile(Dependency dependency){
		if(maven == null){
			IMavenConfiguration config = new MavenConfigurationImpl();
			maven = new MavenImpl(config);
		}
		try {
			String groupId = dependency.getGroupId();
			String artifactId = dependency.getArtifactId();
			String version = dependency.getVersion();
			String type = dependency.getType();
			String classifier = dependency.getClassifier();
			
			Artifact artifact = maven.resolve(groupId, artifactId, version, type, classifier, maven.getArtifactRepositories(), null);
			if(artifact != null){
				File f = artifact.getFile();
				if(f.exists() && f.isFile()){
					return f;
				}
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	protected boolean isSharedModule(File file){
		if(file != null){
			try {
				JarFile jarFile = new JarFile(file);
				Manifest manifest = jarFile.getManifest();
				if(manifest != null){	
					Attributes attr = manifest.getMainAttributes();
					String value = attr.getValue("TIBCO-BW-SharedModule");
					jarFile.close();
					if(value != null && value.equals("META-INF/module.bwm")){
						return true;
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}
		return false;
	}

	protected boolean isDependencyCreated(String dependencyID, String version){
		if(dependenciesMap == null){
			dependenciesMap = new HashMap<String, String>();
			return false;
		}else{
			String key = dependencyID + "-" + version;
			if(dependenciesMap.containsKey(key)){
				String projectName = dependenciesMap.get(key);
				IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
				if(project.exists()){
					return true;
				}
			}
		}
		
		return false;
	}

	protected IProject createProjectFromJar(File jarFile){
		if(jarFile == null){
			return null;
		}
		//For TEST ONLY
		String p1 = "C:/BW/extSM-1.0.0.jar";
		jarFile = new File(p1);
    	
		String projectName = getProjectName(jarFile);
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IProject jarProject = root.getProject(projectName);
		
		if(jarProject.exists()){
			return null;
		}
		
		String location = jarFile.toPath().toString();
		IPath jarPath = new Path(location);
		
	    if("jar".equalsIgnoreCase(jarPath.getFileExtension())){
	    	try {
                String path = jarFile.getAbsolutePath();
                path = path.replace("\\", "/");
                URI zipURI = new URI("zip:/?file:/" + path);
                
                IProjectDescription desc = ResourcesPlugin.getWorkspace().newProjectDescription(projectName);
                desc.setLocationURI(zipURI);

				IProgressMonitor progressMonitor = new NullProgressMonitor();
				jarProject.create(desc , progressMonitor);
				jarProject.open(progressMonitor);
				jarProject.setPersistentProperty(PDECore.EXTERNAL_PROJECT_PROPERTY, PDECore.BINARY_PROJECT_VALUE);
									
				XpdProjectResourceFactory factory = XpdResourcesPlugin.getDefault().getXpdProjectResourceFactory(jarProject);
				
				return jarProject;
	    	}catch(Exception e){
	    		e.printStackTrace();
	    	}
	    }
	    
	    return null;
	}

	protected String getProjectName(File file){
		String projectName = "";
		if(file != null){
			try {
				JarFile jarFile = new JarFile(file);
				Manifest manifest = jarFile.getManifest();
				if(manifest != null){	
					Attributes attr = manifest.getMainAttributes();
					String value = attr.getValue("Bundle-SymbolicName"); //$NON-NLS-1$
					jarFile.close();
					if(value != null ){
						 projectName = value;
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return projectName;
	}

	
	protected void addModulesToProjectDependencies(List<IProject> modules, IProject sourceProject){
		for(IProject module : modules){
			ModelHelper.INSTANCE.addModuleProvideCapabilities(module, sourceProject);
		}
	}
}

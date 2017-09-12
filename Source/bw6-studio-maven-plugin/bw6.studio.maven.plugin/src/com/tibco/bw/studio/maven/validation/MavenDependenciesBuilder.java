package com.tibco.bw.studio.maven.validation;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
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
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.m2e.core.embedder.IMaven;
import org.eclipse.m2e.core.embedder.IMavenConfiguration;
import org.eclipse.m2e.core.internal.embedder.MavenImpl;
import org.eclipse.m2e.core.internal.preferences.MavenConfigurationImpl;
import org.eclipse.pde.internal.core.PDECore;

import com.tibco.bw.design.api.BWAbstractBuilder;
import com.tibco.bw.design.util.ModelHelper;
import com.tibco.bw.studio.maven.helpers.POMHelper;
import com.tibco.zion.common.util.EditingDomainUtil;

@SuppressWarnings("restriction")
public class MavenDependenciesBuilder extends BWAbstractBuilder{
	
	protected IMaven maven;
		
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
		
		Model mavenModel = getMavenModel(project);//POMHelper.readModelFromPOM(pomFileAbs);
		
		if(mavenModel == null){
			return;
		}
		
		List<Dependency> dependencyList = mavenModel.getDependencies();
		List<IProject> projects = new ArrayList<IProject>();

		for(Dependency dependency : dependencyList){
			
			File jarFile = getDependencyFile(dependency);
			if(isSharedModule(jarFile)){
				IProject p = createProjectFromDependency(dependency, jarFile);
				if(p != null){
					projects.add(p);
				}
			}
		}
		
		addModulesToProjectDependencies(projects, project);
		addModulesToApplication(projects, project);
		
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
	
	protected Model getMavenModel(IProject project){
		IFile pomFile = project.getFile("/pom.xml");
		
		if(!pomFile.exists()){
			return null;
		}
		Model mavenModel = null;
		try{
			 File f = pomFile.getRawLocation().toFile();
			 mavenModel = POMHelper.readModelFromPOM(f);
		}catch(Exception e){
			//File is not in the workspace, it may come from a zip package
			URI uri = pomFile.getLocationURI();
			try {
				IFileStore store = EFS.getStore(uri);
				InputStream is = store.openInputStream(EFS.NONE, null);
				mavenModel = POMHelper.readModelFromPOM(is);
				is.close();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
		return mavenModel;
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

	protected IProject createProjectFromDependency(Dependency dependency, File jarFile){
		if(jarFile == null || dependency == null){
			return null;
		}
		
		String projectName = getProjectName(jarFile);
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IProject jarProject = root.getProject(projectName);
		
		if(jarProject.exists()){
			//Validates if the existing project is the same version as the dependency
			
			Model mavenModel = getMavenModel(jarProject);
			String projectVersion = mavenModel.getVersion();
			String dependencyVersion = dependency.getVersion();
			
			if(projectVersion.equals(dependencyVersion)){
				return null;
			}else{
				//Deletes old project. So the project for the new version can be created
				try {
					jarProject.delete(false, true, null);
					jarProject = root.getProject(projectName);
				} catch (CoreException e) {
					e.printStackTrace();
				}
			}
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
				jarProject.setPersistentProperty(new QualifiedName("PLUGIN_ID", "SHARED_MODULE_TYPE"), "EXT_SM"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
									
//				XpdProjectResourceFactory factory = XpdResourcesPlugin.getDefault().getXpdProjectResourceFactory(jarProject);
				
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

	protected void addModulesToProjectDependencies(final List<IProject> modules, final IProject sourceProject){
		for(IProject module : modules){
			ModelHelper.INSTANCE.addModuleRequireCapabilities(module, sourceProject);
		}					
	}
	
	protected void addModulesToApplication(List<IProject> modules, IProject sourceProject){
		TransactionalEditingDomain editingDomain = EditingDomainUtil.INSTANCE.getEditingDomain(); 
		if(editingDomain != null){
			
			RecordingCommand command = new RecordingCommand(editingDomain) {
				@Override
				protected void doExecute() {
					ModelHelper.INSTANCE.addModulesToApplication(modules, sourceProject);
				}
			};

			editingDomain.getCommandStack().execute(command);
		}
	}
}

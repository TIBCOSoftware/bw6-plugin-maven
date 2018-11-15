package com.tibco.bw.studio.maven.validation;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
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
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.m2e.core.embedder.IMaven;
import org.eclipse.m2e.core.embedder.IMavenConfiguration;
import org.eclipse.m2e.core.internal.embedder.MavenImpl;
import org.eclipse.m2e.core.internal.preferences.MavenConfigurationImpl;
import org.eclipse.pde.internal.core.PDECore;

import com.tibco.bw.design.api.BWAbstractBuilder;
import com.tibco.bw.design.componenttype.util.AddProjectDependenciesOp;
import com.tibco.bw.design.componenttype.util.RemoveProjectDependenciesOp;
import com.tibco.bw.design.external.dependencies.BWExternalDependencyRecord;
import com.tibco.bw.design.external.dependencies.BWExternalDependenciesHelper;
import com.tibco.bw.design.external.dependencies.BWExternalDependenciesRegistry;
import com.tibco.bw.design.util.ModelHelper;
import com.tibco.bw.studio.maven.helpers.POMHelper;
import com.tibco.bw.studio.maven.plugin.Activator;
import com.tibco.bw.studio.maven.util.BWMavenConstants;
import com.tibco.xpd.resources.util.ProjectUtil;
import com.tibco.zion.common.util.EditingDomainUtil;

@SuppressWarnings("restriction")
public class BWMavenDependenciesBuilder extends BWAbstractBuilder{

	protected IMaven maven;
	public static final String PLUGIN_PROPERTY_VALUE_MAVEN_ESM = "MAVEN_ESM"; //$NON-NLS-1$

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

		Model mavenModel = getMavenModel(project);

		if(mavenModel == null){
			return;
		}

		List<Dependency> dependencyList = mavenModel.getDependencies();
		List<BWExternalDependencyRecord> handlers = new ArrayList<BWExternalDependencyRecord>();

		//1. Remove dependencies
		Set<IProject> projectsToValidate = new HashSet<IProject>(); 
		removeDependencies(project, dependencyList, projectsToValidate);

		//2. Create the new dependencies
		for(Dependency dependency : dependencyList){

			File jarFile = getDependencyFile(dependency);
			if(isSharedModule(jarFile)){
				BWExternalDependencyRecord handler = createProjectFromDependency(dependency, jarFile);
				if(handler != null && handler.isCreated()){
					handlers.add(handler);
					if(handler.getProject() != null){
						if(!projectsToValidate.contains(handler.getProject())){
							projectsToValidate.add(handler.getProject());
						}
					}
				}
			}
		}

		//3. Add new modules to project dependencies section
		addModulesToProjectDependencies(handlers, project);

		//4. Add new modules to application dependencies section
		addModulesToApplication(handlers, project);

		//5. Register new dependencies
		registerDependencies(handlers, project);

		//6. Register project dependencies in hostProject
		addProjectDependencies(project, handlers);

		//7. Add project itself to validations if there is other projects to validate
		if(!projectsToValidate.isEmpty() && !projectsToValidate.contains(project)){
			projectsToValidate.add(project);
		}

		//8. Run builder on projects that depend from newly created ESM
		if(!projectsToValidate.isEmpty()){
			validateProjects(projectsToValidate);
		}
	}

	protected boolean isMavenProject(IProject project){
		boolean isMavenProject = false;
		try {
			IProjectNature nature = project.getNature(BWMavenConstants.MAVEN_NATURE_ID);
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
		IFile pomFile = project.getFile(BWMavenConstants.POM_XML_LOCATION);

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
					String value = attr.getValue(BWMavenConstants.HEADER_BW_SHARED_MODULE);
					jarFile.close();
					if(value != null && value.equals(BWMavenConstants.HEADER_BW_SHARED_MODULE_VALUE)){
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

	protected BWExternalDependencyRecord createProjectFromDependency(Dependency dependency, File jarFile){
		if(jarFile == null || dependency == null){
			return null;
		}

		String projectName = getProjectName(jarFile);
		String dependencyId = dependency.getGroupId() + "." + dependency.getArtifactId();
		String dependencyVersion = dependency.getVersion();

		String pathStr = jarFile.getAbsolutePath();
		Path jarPath = new Path(pathStr);

		if("jar".equalsIgnoreCase(jarPath.getFileExtension())){	
			pathStr = pathStr.replace("\\", "/"); //$NON-NLS-1$ //$NON-NLS-2$
			try {
				URI zipURI = new URI(BWMavenConstants.EXTERNAL_SM_URI_SCHEME + pathStr);
				BWExternalDependencyRecord handler = new BWExternalDependencyRecord(projectName, dependencyId, dependencyVersion, zipURI);
				handler.setESMPropertValue(PLUGIN_PROPERTY_VALUE_MAVEN_ESM);
				handler = BWExternalDependenciesHelper.INSTANCE.createExternalProjectDependency(handler);
				return handler;
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
		}

		return null;
	}

	protected String getProjectName(File file){
		String projectName = ""; //$NON-NLS-1$
		if(file != null){
			try {
				JarFile jarFile = new JarFile(file);
				Manifest manifest = jarFile.getManifest();
				if(manifest != null){	
					Attributes attr = manifest.getMainAttributes();
					String value = attr.getValue(BWMavenConstants.HEADER_BUNDLE_NAME);
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

	protected void addModulesToProjectDependencies(final List<BWExternalDependencyRecord> modules, IProject sourceProject){
		for(BWExternalDependencyRecord handler : modules){
			IProject project = handler.getProject();
			ModelHelper.INSTANCE.addModuleRequireCapabilities(project, sourceProject);
		}					
	}

	protected void addModulesToApplication(List<BWExternalDependencyRecord> modules, final IProject sourceProject){

		final List<IProject> projects = new ArrayList<>();
		for(BWExternalDependencyRecord handler : modules){
			projects.add(handler.getProject());
		}


		ModelHelper.INSTANCE.addModulesToApplication(projects, sourceProject);

	}

	protected void registerDependencies(List<BWExternalDependencyRecord>records, IProject hostProject){
		BWExternalDependenciesRegistry registry = BWExternalDependenciesRegistry.INSTANCE;
		for(BWExternalDependencyRecord record : records){
			registry.addDependencyRecord(hostProject, record);
		}
	}

	protected void removeDependencies(final IProject hostProject, List<Dependency> dependencies, Set<IProject> projectsToValidate){
		Iterator<BWExternalDependencyRecord> records = BWExternalDependenciesRegistry.INSTANCE.getDependencyRecordsForProject(hostProject);
		List<BWExternalDependencyRecord> toRemove = new ArrayList<>();
		while(records.hasNext()){
			BWExternalDependencyRecord record = records.next();
			boolean remove = true;
			for(Dependency dep : dependencies){
				String depId = dep.getGroupId() + "." + dep.getArtifactId();
				String depVersion = dep.getVersion();

				if(record.getDependencyId().equals(depId) && record.getDependencyVersion().equals(depVersion)){
					dependencies.remove(dep);
					remove = false;
					break;
				}
			}
			if(remove){
				toRemove.add(record);
			}

		}

		//Remove referenced projects from hostProject
		List<IProject> projectsToRemove = new ArrayList<IProject>();
		Set<IProject> referencedProjects = ProjectUtil.getReferencedProjectsHierarchy(hostProject, null);
		for(BWExternalDependencyRecord record : toRemove){
			if(referencedProjects != null){
				if(referencedProjects.contains(record.getProject())){
					projectsToRemove.add(record.getProject());
				}
			}
		}

		if(projectsToRemove.size() > 0){
			IProject[] referencedProjectsToRemove = projectsToRemove.toArray(new IProject[projectsToRemove.size()]);
			RemoveProjectDependenciesOp op = new RemoveProjectDependenciesOp(hostProject, referencedProjectsToRemove);
			try {
				op.run(null);
			} catch (InvocationTargetException e) {
				Activator.getDefault().getLog().log(new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage()));
			} catch (InterruptedException e) {
				Activator.getDefault().getLog().log(new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage()));
			}

		}


		for(BWExternalDependencyRecord record : toRemove){

			final IProject moduleToRemove = record.getProject();

			//Add projects interested in the module to remove to the list of projects to validate
			List<IProject> interestedProjects = ModelHelper.INSTANCE.getInterestedInProject(moduleToRemove, true, true, false);
			for(IProject interestedProject : interestedProjects){
				//Do not add itself
				if(interestedProject.equals(moduleToRemove)){
					continue;
				}
				if(!projectsToValidate.contains(interestedProject)){
					projectsToValidate.add(interestedProject);
				}
			}

			TransactionalEditingDomain editingDomain = EditingDomainUtil.INSTANCE.getEditingDomain(); 
			if(editingDomain != null){

				RecordingCommand command = new RecordingCommand(editingDomain) {
					@Override
					protected void doExecute() {
						ModelHelper.INSTANCE.removeModuleRequireCapabilities(moduleToRemove, hostProject);
						ModelHelper.INSTANCE.removeModuleFromApplications(moduleToRemove, hostProject);
					}
				};

				editingDomain.getCommandStack().execute(command);
			}

			BWExternalDependenciesRegistry.INSTANCE.removeDependencyRecord(hostProject, record);
		}

		return;
	}

	protected void validateProjects(Set<IProject> projectsToValidate){
		BWMavenValidationJob.schedule(projectsToValidate, 1000);
	}

	protected void addProjectDependencies(IProject hostProject, List<BWExternalDependencyRecord >records){
		List<IProject> projectsToAdd = new ArrayList<IProject>();
		Set<IProject> referencedProjects = ProjectUtil.getReferencedProjectsHierarchy(hostProject, null);
		for(BWExternalDependencyRecord record : records){
			if(referencedProjects != null){
				if(!referencedProjects.contains(record.getProject())){
					projectsToAdd.add(record.getProject());
				}
			}
		}

		//Add projects to the reference
		if(projectsToAdd.size() > 0){
			IProject[] referencedProjectsToAdd = projectsToAdd.toArray(new IProject[projectsToAdd.size()]);
			AddProjectDependenciesOp addDependenciesOp = new AddProjectDependenciesOp(hostProject, referencedProjectsToAdd);
			try {
				addDependenciesOp.run(null);
			} catch (InvocationTargetException e) {
				Activator.getDefault().getLog().log(new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage()));
			} catch (InterruptedException e) {
				Activator.getDefault().getLog().log(new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage()));
			}
		}

	}
}

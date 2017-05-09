package com.tibco.bw.studio.maven.action;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.internal.events.BuildCommand;
import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.embedder.MavenModelManager;
import org.eclipse.m2e.core.project.AbstractProjectScanner;
import org.eclipse.m2e.core.project.LocalProjectScanner;
import org.eclipse.m2e.core.project.MavenProjectInfo;
import org.eclipse.m2e.core.project.ProjectImportConfiguration;
import org.eclipse.m2e.core.ui.internal.wizards.ImportMavenProjectsJob;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.PlatformUI;

import com.tibco.bw.studio.maven.helpers.FileHelper;
import com.tibco.bw.studio.maven.helpers.ModuleHelper;
import com.tibco.bw.studio.maven.modules.BWProjectBuilder;
import com.tibco.bw.studio.maven.modules.model.BWApplication;
import com.tibco.bw.studio.maven.modules.model.BWModule;
import com.tibco.bw.studio.maven.modules.model.BWParent;
import com.tibco.bw.studio.maven.modules.model.BWProject;
import com.tibco.bw.studio.maven.plugin.Activator;
import com.tibco.bw.studio.maven.wizard.MavenWizard;
import com.tibco.bw.studio.maven.wizard.MavenWizardDialog;

public class MavenPOMProcessor implements IObjectActionDelegate {
	private Shell shell;
	private IProject selectedProject;
	private BWProject project;
	
	public MavenPOMProcessor() {
		super();
	}

	/**
	 * @see IObjectActionDelegate#setActivePart(IAction, IWorkbenchPart)
	 */
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		shell = targetPart.getSite().getShell();
	}

	/**
	 * @see IActionDelegate#run(IAction)
	 */
	public void run(IAction action) {
		try {
			selectedProject = getCurrentSelectedProject();
			BWProjectBuilder builder = new BWProjectBuilder();
			project = builder.build(selectedProject);
			MavenWizard wizard = new MavenWizard(project);
			MavenWizardDialog wizardDialog = new MavenWizardDialog(shell, wizard);
			if (wizardDialog.open() == Window.OK) {
				project = wizard.getProject();
				parentProjectExistsCreate();
				generatePOMs();
				addMavenNature();
				refreshProjects();
				addParentProjectToWS();
			} else {  //No need to generate the POM files. Return.
				return;
			}
		} catch(Exception e) {
			 Activator.logException("Failed to generate the POM file ", IStatus.ERROR, e);		
		}
	}

	/**
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
	}

	private void generatePOMs() {
		for(BWModule module : project.getModules()) {
			try	{
				module.getPOMBuilder().build(project, module);	
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void addMavenNature() {
		for(BWModule module : project.getModules()) {
			if(module.getProject() != null) {
				addMavenNature(module.getProject());
			}
		}
	}

	private void addParentProjectToWS() {
		final AbstractProjectScanner<MavenProjectInfo> projectScanner = getProjectScanner();
		try {
			projectScanner.run(new NullProgressMonitor());
		} catch(InterruptedException e) {
			e.printStackTrace();
		}
		List<MavenProjectInfo> projects = projectScanner.getProjects();
		ProjectImportConfiguration importConfiguration = new ProjectImportConfiguration();
	    List<IWorkingSet> workingSets = new ArrayList<IWorkingSet>(); // ignore any preselected working set
	    ImportMavenProjectsJob job = new ImportMavenProjectsJob(projects, workingSets, importConfiguration);
	    job.setRule(MavenPlugin.getProjectConfigurationManager().getRule());
	    job.schedule();
	}

	private boolean parentProjectExistsCreate() {
		BWParent parent = ModuleHelper.getParentModule(project.getModules()); 
		BWModule application = ModuleHelper.getApplication(project.getModules());
		IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		for(IProject project : projects) {
			if(project.getName().equals(parent.getArtifactId())) {
				return true;
			}
		}

		if(parent.isValueChanged()) {
			try {
				File pomFile = parent.getPomfileLocation();
				File pomDir = pomFile.getParentFile();
				FileUtils.deleteDirectory(pomDir);

				File parentFile = new File(application.getPomfileLocation().getParentFile().getParent() + File.separator + parent.getArtifactId());

				parentFile.mkdirs();	
				File pomFileAbs = new File(parentFile, "pom.xml");
				pomFileAbs.createNewFile();
				parent.setPomfileLocation(pomFileAbs);

				resetRelativePaths(selectedProject, application, (BWApplication) application);

				for(BWModule module : project.getModules()) {
					switch(module.getType()) {
						case AppModule:
						case SharedModule:
						case PluginProject:
						case CustomXPathProject:
							resetRelativePaths(selectedProject, module, (BWApplication)application);
						default:
							break;
					}
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	private void resetRelativePaths(IProject project, BWModule module, BWApplication application) {
		String projectLocation = module.getPomfileLocation().getParent();
		String parentLocation = ModuleHelper.getParentModule(this.project.getModules()).getPomfileLocation().getParent();

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

	
	protected AbstractProjectScanner<MavenProjectInfo> getProjectScanner() {
		IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
		File root = workspaceRoot.getLocation().toFile();
		BWModule application = ModuleHelper.getApplication(project.getModules());
		BWParent parent = ModuleHelper.getParentModule(project.getModules());
		String parentPath = parent.getPomfileLocation().getParent();
		MavenModelManager modelManager = MavenPlugin.getMavenModelManager();
		return new LocalProjectScanner(root, parentPath, false, modelManager);
	}

	/**
	 * Refresh each of the selected Project. This is required as Maven nature is added to the Project.
	 * Hence the Projects needs to be refreshed.
	 * @throws Exception
	 */
	private void refreshProjects() throws Exception {
		for(BWModule module : project.getModules()) {
			if(module.getProject() != null) {
				module.getProject().refreshLocal(IResource.DEPTH_INFINITE, null); 
			}
		}
	}
	
	/**
	 * Returns the selected Project. The Project will be always of type Application project.
	 * 
	 * @return the Selected Project.
	 */
	public static IProject getCurrentSelectedProject() {
		//Standard code. Taken from Eclipse help.
		IProject project = null;
		ISelectionService selectionService = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getSelectionService();
		ISelection selection = selectionService.getSelection();
	    if(selection instanceof IStructuredSelection) {
	        Object element = ((IStructuredSelection)selection).getFirstElement();
	        if (element instanceof IResource) {
	            project= ((IResource)element).getProject();
	        }
	    }
	    return project;
	}

	/**
	 * Add Maven Nature to the existing natures.
	 * 
	 * @param project the Eclipse IProject.
	 */
	private void addMavenNature(IProject project) {
		try {
			IProjectDescription desc = project.getDescription();

			String[] prevNatures = desc.getNatureIds();
			String[] newNatures = new String[prevNatures.length + 1];

			System.arraycopy(prevNatures, 0, newNatures, 0, prevNatures.length);

			newNatures[prevNatures.length] = "org.eclipse.m2e.core.maven2Nature";
			desc.setNatureIds(newNatures);

			project.setDescription(desc, new NullProgressMonitor());

			ICommand[] commands = desc.getBuildSpec();
			List<ICommand> commandList = Arrays.asList(commands);
			ICommand build = new BuildCommand();
			build.setBuilderName("org.eclipse.m2e.core.maven2Builder");
			List<ICommand> modList = new ArrayList<>(commandList);
			modList.add(build);
			desc.setBuildSpec(modList.toArray(new ICommand[]{}));
		} catch(Exception e) {
			 Activator.logException("Failed to add Maven nature to the Project : " + project.getName(), IStatus.ERROR, e);		
		}
	}
}

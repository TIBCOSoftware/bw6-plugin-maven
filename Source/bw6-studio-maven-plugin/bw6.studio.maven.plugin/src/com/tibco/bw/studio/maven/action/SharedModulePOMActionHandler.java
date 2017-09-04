package com.tibco.bw.studio.maven.action;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.internal.events.BuildCommand;
import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.window.Window;

import com.tibco.bw.studio.maven.helpers.ProjectHelper;
import com.tibco.bw.studio.maven.modules.BWProjectBuilder;
import com.tibco.bw.studio.maven.modules.model.BWModule;
import com.tibco.bw.studio.maven.modules.model.BWProject;
import com.tibco.bw.studio.maven.plugin.Activator;
import com.tibco.bw.studio.maven.wizard.MavenWizard;
import com.tibco.bw.studio.maven.wizard.MavenWizardDialog;

public class SharedModulePOMActionHandler extends AbstractHandler{
	private IProject selectedProject;
	private BWProject project;
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		try{
			selectedProject = ProjectHelper.getCurrentSelectedProject();
			BWProjectBuilder builder = new BWProjectBuilder();
			project = builder.buildSMProject(selectedProject);
			MavenWizard wizard = new MavenWizard(project);
			MavenWizardDialog dialog = new MavenWizardDialog(ProjectHelper.getActiveShell(), wizard);
			if(dialog.open() == Window.OK){
				project = wizard.getProject();
				generatePOMs();
				addMavenNature();
				refreshProjects();

			}else{
				return null;
			}
		} catch(Exception e) {
			 Activator.logException("Failed to generate the POM file ", IStatus.ERROR, e);		
		}
		
		return null;
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
			if(module != null){
				addMavenNature(module.getProject());
			}
		}
	}
	
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
}

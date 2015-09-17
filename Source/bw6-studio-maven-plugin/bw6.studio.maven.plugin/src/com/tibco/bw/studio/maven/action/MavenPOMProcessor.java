/*
 * Copyright (c) 2013-2014 TIBCO Software Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package com.tibco.bw.studio.maven.action;

import java.util.Arrays;
import java.util.List;

import org.eclipse.core.internal.events.BuildCommand;
import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;

import com.tibco.bw.studio.maven.modules.BWModule;
import com.tibco.bw.studio.maven.modules.BWProject;
import com.tibco.bw.studio.maven.modules.BWProjectBuilder;
import com.tibco.bw.studio.maven.plugin.Activator;
import com.tibco.bw.studio.maven.wizard.MavenWizard;

public class MavenPOMProcessor implements IObjectActionDelegate 
{

	private Shell shell;
	
	private IProject selectedProject;
	
	private BWProject project;
	
	public MavenPOMProcessor()
	{
		super();
	}

	/**
	 * @see IObjectActionDelegate#setActivePart(IAction, IWorkbenchPart)
	 */
	public void setActivePart(IAction action, IWorkbenchPart targetPart) 
	{
		shell = targetPart.getSite().getShell();
	}

	/**
	 * @see IActionDelegate#run(IAction)
	 */
	public void run(IAction action) 
	{
		try 
		{

			selectedProject = getCurrentSelectedProject();
			BWProjectBuilder builder = new BWProjectBuilder();
			project = builder.build(selectedProject);
			
			MavenWizard wizard = new MavenWizard( project );

			WizardDialog wizardDialog = new WizardDialog(shell, wizard);
			if (wizardDialog.open() == Window.OK) 
			{
				project = wizard.getProject();
				generatePOMs();
				addMavenNature();
				refreshProjects();

			}
			else 
			{
				//No need to generate the POM files. Return.
				return;
			}

		} catch (Exception e) 
		{
			 Activator.logException("Failed to generate the POM file ", IStatus.ERROR , e );		
		}
	}

	/**
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) 
	{
	}
	
	
	private void generatePOMs()
	{
		for( BWModule module : project.getModules())
		{
			try
			{
				module.getPOMBuilder().build(project, module);	
			}
			catch(Exception e )
			{
				e.printStackTrace();
			}
			
		}
		
	}
	
	private void addMavenNature()
	{
		for( BWModule module : project.getModules() )
		{
			if( module.getProject() != null )
			{
				addMavenNature(module.getProject() );
			}
		}
	}
	
	/**
	 * Refresh each of the selected Project. This is required as Maven nature is added to the Project.
	 * Hence the Projects needs to be refreshed.
	 * @throws Exception
	 */
	private void refreshProjects() throws Exception
	{
		for( BWModule module : project.getModules() )
		{
			if( module.getProject() != null )
			{
				module.getProject().refreshLocal(IResource.DEPTH_INFINITE, null); 
			}
		}

	}
	
	/**
	 * Returns the selected Project. The Project will be always of type Application project.
	 * 
	 * @return the Selected Project.
	 */
	public static IProject getCurrentSelectedProject() 
	{
	
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
	private void addMavenNature( IProject project)
	{
		try
		{
			
			IProjectDescription desc = project.getDescription();
			
			String[] prevNatures = desc.getNatureIds();
			String[] newNatures = new String[prevNatures.length + 1];
			
			System.arraycopy(prevNatures, 0, newNatures, 0, prevNatures.length);
			
			newNatures[prevNatures.length] = "org.eclipse.m2e.core.maven2Nature";
			desc.setNatureIds(newNatures);
			
			project.setDescription(desc, new NullProgressMonitor());
		
			ICommand[] commands = desc.getBuildSpec();
			List<ICommand> commandList = Arrays.asList( commands );
			ICommand build = new BuildCommand();
			build.setBuilderName("org.eclipse.m2e.core.maven2Builder");
			commandList.add( build );
			desc.setBuildSpec( commandList.toArray(new ICommand[]{}));
		}
		catch(Exception e)
		{
			 Activator.logException("Failed to add Maven nature to the Project : " + project.getName() , IStatus.ERROR , e );		

		}
	}

}

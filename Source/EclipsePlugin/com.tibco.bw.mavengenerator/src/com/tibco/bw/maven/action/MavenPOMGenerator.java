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

package com.tibco.bw.maven.action;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
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

import com.tibco.bw.maven.Activator;
import com.tibco.bw.maven.gatherers.BWProjectInfoGatherer;
import com.tibco.bw.maven.gatherers.IBWProjectInfoGatherer;
import com.tibco.bw.maven.generators.BWPOMProcessor;
import com.tibco.bw.maven.generators.IBWPOMProcessor;
import com.tibco.bw.maven.utils.BWAppModuleInfo;
import com.tibco.bw.maven.utils.BWOSGiModuleInfo;
import com.tibco.bw.maven.utils.BWProjectInfo;
import com.tibco.bw.maven.utils.BWSharedModuleInfo;
import com.tibco.bw.maven.wizard.MavenWizard;

public class MavenPOMGenerator implements IObjectActionDelegate {

	private Shell shell;
	
	private IProject selectedProject;
	
	private BWProjectInfo bwProjectInfo;
	
	public MavenPOMGenerator()
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

			IExtensionRegistry registry = Platform.getExtensionRegistry();
			IExtensionPoint point = registry.getExtensionPoint("com.tibco.bw.design.Palette");
			selectedProject = getCurrentSelectedProject();
			bwProjectInfo = readBWProjectInfo();

			MavenWizard wizard = new MavenWizard();
			wizard.setBwProjectInfo(bwProjectInfo);

			WizardDialog wizardDialog = new WizardDialog(shell, wizard);
			if (wizardDialog.open() == Window.OK) 
			{
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
	
	private BWProjectInfo readBWProjectInfo() throws Exception
	{
		IBWProjectInfoGatherer gatherer = new BWProjectInfoGatherer(selectedProject);
		BWProjectInfo info = gatherer.gather();
		return info;
	}
	

	
	private void generatePOMs( )
	{
		try
		{
			IBWPOMProcessor pomGen = new BWPOMProcessor(bwProjectInfo);
			pomGen.process();
		}
		catch(Exception e )
		{
			e.printStackTrace();
		}
	}
	
	private void addMavenNature()
	{
		addMavenNature(bwProjectInfo.getAppInfo().getProject());
		
		for( BWAppModuleInfo module : bwProjectInfo.getAppInfo().getAppModules())
		{
			addMavenNature( module.getProject() );
		}
		
		for( BWSharedModuleInfo module : bwProjectInfo.getAppInfo().getSharedModules())
		{
			addMavenNature( module.getProject() );
		}

		for( BWOSGiModuleInfo module : bwProjectInfo.getAppInfo().getOsgiModules())
		{
			addMavenNature( module.getProject() );
		}

	}
	
	/**
	 * Refresh each of the selected Project. This is required as Maven nature is added to the Project.
	 * Hence the Projects needs to be refreshed.
	 * @throws Exception
	 */
	private void refreshProjects() throws Exception
	{
		bwProjectInfo.getAppInfo().getProject().refreshLocal(IResource.DEPTH_INFINITE, null);
		
		for( BWAppModuleInfo module : bwProjectInfo.getAppInfo().getAppModules())
		{
			module.getProject().refreshLocal(IResource.DEPTH_INFINITE, null); 
		}

		for( BWSharedModuleInfo module : bwProjectInfo.getAppInfo().getSharedModules())
		{
			module.getProject().refreshLocal(IResource.DEPTH_INFINITE, null); 
		}

		for( BWOSGiModuleInfo module : bwProjectInfo.getAppInfo().getOsgiModules())
		{
			module.getProject().refreshLocal(IResource.DEPTH_INFINITE, null); 
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
			
		}
		catch(Exception e)
		{
			 Activator.logException("Failed to add Maven nature to the Project : " + project.getName() , IStatus.ERROR , e );		

		}
	}

}

package com.tibco.bw.studio.maven.helpers;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.project.IMavenProjectFacade;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.PlatformUI;

public class ProjectHelper 
{

	public static IMavenProjectFacade getMavenProject( IProject project )
	{
		IMavenProjectFacade [] projects = MavenPlugin.getMavenProjectRegistry().getProjects();
		for( IMavenProjectFacade mavenProject : projects )
		{
			if(project.getName().equals( mavenProject.getArtifactKey().getArtifactId() ) )
			{
				return mavenProject;
			}
		}
		
		return null;
	}
	
	
	public static IProject getEclipseProject( String projectName)
	{
		IProject project  = ResourcesPlugin.getWorkspace().getRoot().getProject( projectName );
		return project;
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
	
	public static Shell getActiveShell(){
		Display display = Display.getDefault();
		Shell result = display.getActiveShell();
		return result;
	}

}

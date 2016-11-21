package com.tibco.bw.studio.maven.helpers;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.project.IMavenProjectFacade;

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
	
	

}

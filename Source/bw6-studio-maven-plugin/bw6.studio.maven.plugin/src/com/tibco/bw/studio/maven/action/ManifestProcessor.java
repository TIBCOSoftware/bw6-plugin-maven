package com.tibco.bw.studio.maven.action;

import java.util.ArrayList;
import java.util.List;
import java.util.jar.Manifest;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.internal.core.JarPackageFragmentRoot;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.project.IMavenProjectFacade;
import org.eclipse.pde.internal.core.project.PDEProject;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;

import com.tibco.bw.studio.maven.helpers.ManifestParser;
import com.tibco.bw.studio.maven.helpers.ManifestWriter;
import com.tibco.bw.studio.maven.helpers.ProjectHelper;
import com.tibco.bw.studio.maven.modules.BWModuleParser;
import com.tibco.bw.studio.maven.plugin.Activator;

@SuppressWarnings("restriction")
public class ManifestProcessor implements IObjectActionDelegate 
{

	private Shell shell;
	
	private IProject selectedProject;

	@Override
	public void run(IAction action) 
	{
		selectedProject = getCurrentSelectedProject();
		IFile file = selectedProject.getFile("META-INF/TIBCO.xml");
		List<BWModuleParser.BWModuleData> moduleData =  BWModuleParser.INSTANCE.parseBWModules(file.getRawLocation().toFile());

		for(BWModuleParser.BWModuleData module : moduleData )
		{
			updatePaths(module.getModuleName() );
		}		
	
		
	}
	
	public void run( List<String> applications )
	{
		List<IProject> list = getApplicationProjects( applications );
		
		for( IProject project : list )
		{
			selectedProject = project;
			IFile file = selectedProject.getFile("META-INF/TIBCO.xml");
			List<BWModuleParser.BWModuleData> moduleData =  BWModuleParser.INSTANCE.parseBWModules(file.getRawLocation().toFile());

			for(BWModuleParser.BWModuleData module : moduleData )
			{
				updatePaths(module.getModuleName() );
			}

		}
		
	}
	
	private List<IProject> getApplicationProjects( List<String> applications )
	{
		IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		
		List<IProject> projectList = new ArrayList<IProject>();
		for( IProject project : projects )
		{
			IFile file = project.getFile("META-INF/TIBCO.xml");
			if( file.isAccessible() )
			{
				if(applications.contains(project.getName()))
				{
					projectList.add(project);	
				}
				
			}
		}
		
		return projectList;
	}
	

	private void updatePaths( String module )
	{
		IProject project  = ProjectHelper.getEclipseProject(module);
		IJavaProject javaProject = JavaCore.create(project);
		
		if( javaProject == null || !javaProject.isOpen() )
		{
			return;
		}
		IFile manifest = PDEProject.getManifest(project);
		Manifest mf = ManifestParser.parseManifest( manifest.getRawLocation().toFile() );

		try
		{
			String externalPath = getExternalPath(project);
			
			if( externalPath == null || externalPath.isEmpty() )
			{
				return;
			}
			String bundleClassPath =  mf.getMainAttributes().getValue("Bundle-ClassPath");
			
			if(bundleClassPath == null )
			{
				bundleClassPath = " .";
			}
			
			if( !bundleClassPath.isEmpty() )
			{
			
				
				String[] bundleentries = bundleClassPath.split(",");
				bundleClassPath = "";
				int start = 0; 
				for(String entry : bundleentries)
				{
					if( entry != null && !entry.startsWith( " external:"))
					{
						if( start > 0)
						{
							bundleClassPath = bundleClassPath + ",";
						}
						bundleClassPath = bundleClassPath + entry;
					}
					
					start++;
					
				}
				
				bundleClassPath = bundleClassPath + ",";
			}
			
			bundleClassPath = bundleClassPath + externalPath;
			mf.getMainAttributes().putValue("Bundle-ClassPath", bundleClassPath);
			ManifestWriter.updateManifest(manifest.getRawLocation().toFile(), mf);
			
			project.refreshLocal(IResource.DEPTH_INFINITE, null); 
		
		}
		catch(Exception e )
		{
			Activator.getDefault().log("Failed to update External path for Project " + project.getName() , IStatus.ERROR );	
		}
		
	}
	
	
	private String getExternalPath( IProject project ) throws Exception
	{			
		IJavaProject javaProject = JavaCore.create(project);

		StringBuffer buffer = new StringBuffer();
		String mavenRepo = MavenPlugin.getRepositoryRegistry().getLocalRepository().getBasedir().toString();	
				
		@SuppressWarnings("unused")
		IMavenProjectFacade mavenProject = ProjectHelper.getMavenProject(project);

		IJavaElement [] elements = javaProject.getChildren();
		int start = 0;
		for( IJavaElement element : elements )
		{
			if( element instanceof JarPackageFragmentRoot )
			{
				JarPackageFragmentRoot jarelement = ((JarPackageFragmentRoot) element);
				if( jarelement.getJar().getName().indexOf( mavenRepo ) != -1 )
				{
					if( jarelement.getJar().getName().indexOf("com.tibco.bw.palette.shared") != -1 ||  jarelement.getJar().getName().indexOf("com.tibco.xml.cxf.common") != -1 )
					{
						continue;
					}
					if( start != 0 )
					{
						buffer.append(",");
					}
					buffer.append(" external:" + jarelement.getJar().getName());
					start++;
				}
				
			}
		}
		
		return buffer.toString();
	}
		
	
	
	

	@Override
	public void selectionChanged(IAction action, ISelection selection) 
	{
	
		
	}

	@Override
	public void setActivePart(IAction action, IWorkbenchPart targetPart) 
	{
		shell = targetPart.getSite().getShell();

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

}

package com.tibco.bw.studio.maven.plugin;

import java.lang.reflect.Field;

import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.m2e.core.embedder.IMavenConfiguration;
import org.eclipse.m2e.core.embedder.MavenModelManager;
import org.eclipse.m2e.core.internal.MavenPluginActivator;
import org.eclipse.m2e.core.internal.embedder.MavenImpl;
import org.eclipse.m2e.core.internal.markers.IMavenMarkerManager;
import org.eclipse.m2e.core.internal.project.registry.ProjectRegistryManager;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.tibco.bw.studio.maven.core.BWProjectConfigurationManager;

/**
 * The activator class controls the plug-in life cycle
 */
@SuppressWarnings("restriction")
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "com.tibco.bw.studio.maven.plugin"; 

	private static BundleContext bundleContext = null;
	
	// The shared instance
	private static Activator plugin;
	
	 {
		try{
			/**
			 * Override org.eclipse.m2e.core.internal.project.ProjectConfigurationManager to avoid build external projects.
			 * org.eclipse.m2e.core.internal.builder.MavenBuilder fails to try to build external projects as the pom file comes from a jar or a zip file
			 */
			Object mavenPlugin = MavenPluginActivator.getDefault();
			Field projectConf  = mavenPlugin.getClass().getDeclaredField("configurationManager");
			projectConf.setAccessible(true);
			MavenImpl maven = MavenPluginActivator.getDefault().getMaven();
			ProjectRegistryManager projectManager = MavenPluginActivator.getDefault().getMavenProjectManagerImpl();
			IMavenMarkerManager  mavenMarkerManager = MavenPluginActivator.getDefault().getMavenMarkerManager();
			IMavenConfiguration mavenConfiguration = MavenPluginActivator.getDefault().getMavenConfiguration();
			MavenModelManager mavenModelManager = MavenPluginActivator.getDefault().getMavenModelManager();
			projectConf.set(MavenPluginActivator.getDefault(), new BWProjectConfigurationManager(maven, projectManager, mavenModelManager, mavenMarkerManager, mavenConfiguration));
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	 
	/**
	 * The constructor
	 */
	public Activator() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		bundleContext = context;
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}
	
	public static void log( String message , int level )
	{
		getDefault().getLog().log(new Status( level , Activator.PLUGIN_ID, message ));
	}
	
	public static void logException( String message , int level , Throwable t)
	{
		getDefault().getLog().log(new Status( level , Activator.PLUGIN_ID, message , t ));
	}
	
	public static BundleContext getBundleContext() {
		return bundleContext;
	}


}

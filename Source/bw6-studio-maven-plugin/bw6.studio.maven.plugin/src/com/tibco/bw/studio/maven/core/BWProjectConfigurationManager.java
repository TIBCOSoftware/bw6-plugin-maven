package com.tibco.bw.studio.maven.core;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.m2e.core.embedder.IMaven;
import org.eclipse.m2e.core.embedder.IMavenConfiguration;
import org.eclipse.m2e.core.embedder.MavenModelManager;
import org.eclipse.m2e.core.internal.markers.IMavenMarkerManager;
import org.eclipse.m2e.core.internal.project.ProjectConfigurationManager;
import org.eclipse.m2e.core.internal.project.registry.ProjectRegistryManager;
import org.eclipse.m2e.core.project.ResolverConfiguration;

@SuppressWarnings("restriction")
public class BWProjectConfigurationManager extends ProjectConfigurationManager {

	public BWProjectConfigurationManager(IMaven maven,
			ProjectRegistryManager projectManager,
			MavenModelManager mavenModelManager,
			IMavenMarkerManager mavenMarkerManager,
			IMavenConfiguration mavenConfiguration) {
		super(maven, projectManager, mavenModelManager, mavenMarkerManager,
				mavenConfiguration);
	}

	@Override
	public ResolverConfiguration getResolverConfiguration(IProject project) {

		try {
			//If is an external project avoid the maven builder 
			String	value = project.getPersistentProperty(new QualifiedName("PLUGIN_ID", "SHARED_MODULE_TYPE"));
			if(value != null && !value.isEmpty()) {
				return null;
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}

		return super.getResolverConfiguration(project);
	}

	
}

package com.tibco.bw.maven.plugin.test.stub;


import java.util.ArrayList;
import java.util.List;

import org.apache.maven.MavenExecutionException;
import org.apache.maven.artifact.repository.MavenArtifactRepository;
import org.apache.maven.execution.DefaultMavenExecutionRequest;
import org.apache.maven.execution.DefaultMavenExecutionResult;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.DefaultPlexusContainer;
import org.codehaus.plexus.PlexusContainerException;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.internal.impl.SimpleLocalRepositoryManagerFactory;
import org.eclipse.aether.repository.LocalRepository;
import org.eclipse.aether.repository.NoLocalRepositoryManagerException;

public class MavenSessionStub extends MavenSession {

	@SuppressWarnings("deprecation")
	public MavenSessionStub(MavenProject project) throws PlexusContainerException {
		super(new DefaultPlexusContainer(), new DefaultRepositorySystemSession(), new DefaultMavenExecutionRequest(), new DefaultMavenExecutionResult());
					
		try {
			getRequest().setSystemProperties(System.getProperties());	
			//TODO replace hardcoded url with variable local repo path
			getRequest().setLocalRepository(new MavenArtifactRepository("My Maven Repo", "file:///"+System.getProperty("user.home")+"/.m2/repository", null, null, null));
			DefaultRepositorySystemSession repoSession = (DefaultRepositorySystemSession)getRepositorySession();
			repoSession.setLocalRepositoryManager(new SimpleLocalRepositoryManagerFactory().newInstance(getRepositorySession(),
					new LocalRepository(getRequest().getLocalRepository().getBasedir())));
			//For ear list of projects is required
			List<MavenProject> projects = new ArrayList<>();
			projects.add(project);
			setProjects(projects);
			
		} catch (NoLocalRepositoryManagerException e) {
			e.printStackTrace();
		}


	}

}

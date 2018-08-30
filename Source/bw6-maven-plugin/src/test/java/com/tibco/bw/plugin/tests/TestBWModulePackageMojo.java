package com.tibco.bw.plugin.tests;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.maven.DefaultMaven;
import org.apache.maven.Maven;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.repository.ArtifactRepositoryPolicy;
import org.apache.maven.artifact.repository.DefaultArtifactRepository;
import org.apache.maven.artifact.repository.MavenArtifactRepository;
import org.apache.maven.artifact.repository.layout.DefaultRepositoryLayout;
import org.apache.maven.execution.DefaultMavenExecutionRequest;
import org.apache.maven.execution.DefaultMavenExecutionResult;
import org.apache.maven.execution.MavenExecutionRequest;
import org.apache.maven.execution.MavenExecutionRequestPopulator;
import org.apache.maven.execution.MavenExecutionResult;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.Mojo;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.apache.maven.plugin.testing.MojoRule;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectBuilder;
import org.apache.maven.project.ProjectBuildingException;
import org.apache.maven.project.ProjectBuildingRequest;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.internal.impl.SimpleLocalRepositoryManagerFactory;
import org.eclipse.aether.repository.LocalRepository;
import org.eclipse.aether.repository.LocalRepositoryManager;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

import com.tibco.bw.maven.plugin.module.BWModulePackageMojo;


public class TestBWModulePackageMojo extends AbstractMojoTestCase {
	
	
	@Component
	private MavenSession session;

	@Component
	private MavenProject project;
	private MojoExecution execution;
	private File pom;
	@Rule public MojoRule mojoRule = new MojoRule();
	
	protected void setUp() throws Exception {		
		super.setUp();
		pom = new File( "C:/workspace/Test/pom.xml" );
		//project = readMavenProject(pom);
		//session = newMavenSession();
		
	}
	

	 private MavenProject readMavenProject(File pom) throws InitializationException, ProjectBuildingException, ComponentLookupException {
		 // New
       /* DefaultRepositorySystemSession repositorySession = new DefaultRepositorySystemSession();
        RepositorySystem repositorySystem;
        try {
            repositorySystem = getContainer().lookup( RepositorySystem.class );

        } catch (ComponentLookupException e) {
            throw new InitializationException("Failed to lookup RepositorySystem", e);
        }

        LocalRepository localRepo = new LocalRepository("C:/Users/ankpande/.m2/repository");
        
        LocalRepositoryManager localRepositoryManager = repositorySystem.newLocalRepositoryManager( repositorySession, localRepo );
        // New

        
        MavenExecutionRequest request = new DefaultMavenExecutionRequest();
        request.setBaseDirectory( new File("C:/Users/ankpande/.m2/repository") );
        ProjectBuildingRequest configuration = request.getProjectBuildingRequest();

        // Fix
       // repositorySession.setLocalRepositoryManager( localRepositoryManager );
        configuration.setRepositorySession( repositorySession );
        // Fix

        MavenProject project = lookup( ProjectBuilder.class ).build( pom, configuration ).getProject();
        Assert.assertNotNull( project );
        return project;*/
		 
		 
		 ProjectBuildingRequest buildingRequest = newMavenSession().getProjectBuildingRequest();
	        ProjectBuilder projectBuilder = lookup(ProjectBuilder.class);
	        MavenProject project = projectBuilder.build(pom, buildingRequest).getProject();
	        return project;
	}
	@Test
	public void testexecute() throws Exception {
		try {

			/*assertNotNull( pom );
	        assertTrue( pom.exists() );      
	        */
	        BWModulePackageMojo myMojo = (BWModulePackageMojo)lookupConfiguredMojo(pom, "bwmodule") ;// lookupMojo("bwmodule", pom);
	        assertNotNull( myMojo );
	        
	        myMojo.execute();
			
			System.out.println("BW Module Packager Mojo finished execution.");
		} catch (IOException e) {
			e.printStackTrace();
			throw new MojoExecutionException("Error assembling JAR", e);
		} 
	}
	  protected Mojo lookupConfiguredMojo(File pom, String goal) throws Exception {
	        assertNotNull( pom );
	        assertTrue( pom.exists() );

	        ProjectBuildingRequest buildingRequest = newMavenSession().getProjectBuildingRequest();
	        ProjectBuilder projectBuilder = lookup(ProjectBuilder.class);
	        MavenProject project = projectBuilder.build(pom, buildingRequest).getProject();
	        execution = newMojoExecution("bwmodule");
	        return lookupConfiguredMojo(session, execution);
	    }
	  protected MavenSession newMavenSession(MavenProject project) {
	        MavenSession session = newMavenSession();
	        session.setCurrentProject( project );
	        session.setProjects( Arrays.asList( project ) );
	        return session;        
	    }
	  
	  protected MavenSession newMavenSession() {
	        try {
	            MavenExecutionRequest request = new DefaultMavenExecutionRequest();
	            MavenExecutionResult result = new DefaultMavenExecutionResult();

	            // populate sensible defaults, including repository basedir and remote repos
	            MavenExecutionRequestPopulator populator;
	            populator = getContainer().lookup( MavenExecutionRequestPopulator.class );
	            populator.populateDefaults( request );

	            // this is needed to allow java profiles to get resolved; i.e. avoid during project builds:
	            // [ERROR] Failed to determine Java version for profile java-1.5-detected @ org.apache.commons:commons-parent:22, /Users/alex/.m2/repository/org/apache/commons/commons-parent/22/commons-parent-22.pom, line 909, column 14
	            request.setSystemProperties( System.getProperties() );
	            
	            // and this is needed so that the repo session in the maven session 
	            // has a repo manager, and it points at the local repo
	            // (cf MavenRepositorySystemUtils.newSession() which is what is otherwise done)
	            DefaultMaven maven = (DefaultMaven) getContainer().lookup( Maven.class );
	            DefaultRepositorySystemSession repoSession =
	                (DefaultRepositorySystemSession) maven.newRepositorySession( request );
	            repoSession.setLocalRepositoryManager(
	                new SimpleLocalRepositoryManagerFactory().newInstance(repoSession, 
	                    new LocalRepository( request.getLocalRepository().getBasedir() ) ));

	            
	             session = new MavenSession( getContainer(), 
	                repoSession,
	                request, result );
	            return session;
	        } catch (Exception e) {
	            throw new RuntimeException(e);
	        }
	    }

	protected void tearDown() throws Exception {
			super.tearDown();
	}
}

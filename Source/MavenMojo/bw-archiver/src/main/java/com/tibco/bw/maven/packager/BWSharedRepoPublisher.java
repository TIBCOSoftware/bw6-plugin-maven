package com.tibco.bw.maven.packager;

import java.io.File;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Execute;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectBuilder;
import org.apache.maven.settings.Settings;
import org.codehaus.plexus.component.annotations.Requirement;
import org.codehaus.plexus.logging.Logger;

import com.tibco.bw.maven.packager.utils.BWFileUtils;
import com.tibco.bw.maven.packager.utils.BWP2Publisher;


/**
 * 
 * The Tycho only refers to the p2 Repository for the Build. 
 *  If the AppModule depends upon the Shared Module then the SharedModule has to be in the P2 Repository.
 *  So after the Shared Module is packaged into the JAR it needs to be moved into the Shared Module p2 repository.
 *  
 *  This Mojo moves the packaged Shared Module to the P2 Repository at the packaging phase.
 * 
 * @author Ashutosh
 * 
 * @version 1.0
 *
 */

@Mojo( name="bw-sharedrepopublisher", defaultPhase=LifecyclePhase.PACKAGE  )
@Execute(goal="bw-sharedrepopublisher", phase= LifecyclePhase.PACKAGE)
public class BWSharedRepoPublisher extends AbstractMojo
{

	@Parameter( property="project.build.directory")
    private File outputDirectory;
    
	@Parameter( property="project.basedir")
	private File projectBasedir;
	
    @Component
    private MavenSession session;

    @Component
    private MavenProject project;

    @Component
    private MojoExecution mojo;

    @Component
    private ProjectBuilder builder;

    @Component
    private Settings settings;
    
    @Requirement
    private Logger logger;

    private String bwVersion;

    private String tibcoHome;
    
    private String tibcoSharedP2Home;
    
    private String tibcoSharedTempHome;
    
    /**
     * The Execute method. 
     * 
     * @throws MojoExecutionException
     * @throws MojoFailureException
     */
    public void execute() throws MojoExecutionException, MojoFailureException 
	{
		
    	//TibcoHome and the BW Version is set in the Properties in the POM file.
    	tibcoHome = project.getProperties().getProperty( "tibco.home" );		
    	bwVersion = project.getProperties().getProperty( "bw.version" );
    	
    	tibcoHome = tibcoHome.toString().replace("\\", "/");
    	
    	tibcoSharedTempHome = tibcoHome + "/bw/" + bwVersion + "/maven/temp" ;
    	
    	tibcoSharedP2Home =  tibcoHome + "/bw/" + bwVersion + "/maven/sharedmodulerepo" ;   	

    	try 
    	{
    		File jar = getModuleJar();
			publishToP2( jar );
		} 
    	catch (Exception e) 
    	{
    		throw new MojoExecutionException("Failed to add the Shared Module to the P2 Repository.", e );
		}
	}
    
    
    /**
     * Returns the packaged JAR file for the Module (Shared/OSGi)
     *  
     * @return the JAR file for the Module 
     * 
     * @throws Exception
     */
	private File getModuleJar() throws Exception
	{
		
        File[] files = BWFileUtils.getFilesForType( outputDirectory , ".jar" );
        
        files = BWFileUtils.sortFilesByDateDesc(files);
        
        if( files.length == 0 )
        {
        	throw new Exception("Module is not built yet. Please check your Application PO for the Module entry.");
        }

        return files[0];
	}

    /**
     * Publish the Module JAR to the Shared Module P2 Repository.
     * 
     * @param jar the JAR file for the Module.
     * 
     * @throws Exception
     */
	private void publishToP2( File jar ) throws Exception
	{

		BWP2Publisher publisher = new BWP2Publisher( tibcoHome , tibcoSharedP2Home, tibcoSharedTempHome, getLog() );
		publisher.publishToP2( jar );
	}
    
	
}
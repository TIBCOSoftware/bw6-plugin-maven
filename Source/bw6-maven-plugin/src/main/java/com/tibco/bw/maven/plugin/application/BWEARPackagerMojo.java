package com.tibco.bw.maven.plugin.application;

import com.tibco.bw.maven.plugin.osgi.helpers.ManifestParser;
import com.tibco.bw.maven.plugin.utils.BWModulesParser;
import com.tibco.bw.maven.plugin.utils.BWProjectUtils;
import org.apache.commons.io.FileUtils;
import org.apache.maven.archiver.MavenArchiveConfiguration;
import org.apache.maven.archiver.MavenArchiver;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.archiver.jar.JarArchiver;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.Manifest;


@Mojo( name = "bwear", defaultPhase = LifecyclePhase.PACKAGE )
public class BWEARPackagerMojo extends AbstractBWApplicationMojo {

	@Parameter( property="project.build.directory")
    private File outputDirectory;
    
	@Parameter( property="project.basedir")
	private File projectBasedir;
	
    @Component
    private MavenSession session;

    @Component
    private MavenProject project;

//    @Component
//    private MojoExecution mojo;
//
//    @Component
//    private ProjectBuilder builder;

//    @Component
//    private Settings settings;

    private Manifest manifest;
    




    //This is the actual JAR file which will be created in the EAR file.
    JarArchiver jarchiver;

    //This will create the EAR file
    MavenArchiver archiver;

    //Archive Configuration. This will set the Configuration for the Archive.
    protected MavenArchiveConfiguration archiveConfiguration;

    //The version to be updated in the Application Manifest. 
    String version;

    /**
     * Execute Method.
     * 
     */
    public void execute() throws MojoExecutionException
    {
    	try 
    	{
    		getLog().info("BWEARPackager Mojo started ...");
			initialize();

    	    jarchiver = new JarArchiver();

    	    archiver = new MavenArchiver();

    	    archiveConfiguration = new MavenArchiveConfiguration();
    	    
    	    moduleVersionMap = new HashMap<String, String>();
    	    
            manifest = ManifestParser.parseManifest(projectBasedir) ;

    	    getLog().info( "Adding Modules to the EAR file ");
    	    
    		addModules();
    		
    		getLog().info("Adding EAR Information to the EAR File.");
    		addApplication();

    		getLog().info( "BWEARPackager Mojo finished execution.");
    		
		}
    	
    	catch (Exception e1) 
		{
			throw new MojoExecutionException( "Failed to create BW EAR Archive ", e1);
		}
		finally {
			cleanup();
		}

	}
    
    
	/**
	 * Add the Application related files to the EAR.
	 * 
	 * @throws Exception
	 */
	private void addApplication() throws Exception 
	{
		
		getLog().debug("Adding Application specific files...");
		// Get the META-INF Folder for the Application Project
		File metainfFolder = getApplicationMetaInf();

		//Add the files from the META-INF to the EAR File.
		File appManifest = addFiletoEAR(metainfFolder);

		File earFile = getArchiveFileName();
		archiver.setArchiver(jarchiver);

		archiver.setOutputFile( earFile );

		// Set the MANIFEST.MF to the JAR Archiver
		jarchiver.setManifest(appManifest);

		// Set the MANIFEST.MF to the Archive Configuration
		archiveConfiguration.setManifestFile(appManifest);

		archiveConfiguration.setAddMavenDescriptor(true);

		//Create the Archive.
		archiver.createArchive(session, project, archiveConfiguration);

		project.getArtifact().setFile( earFile);

		//Move Archive
		//TODO: Need to move the Archive to the User defined location.
	}


    

    /**
     * Adds the Modules included in the Application to the EAR file. 
     * It will also maintain a Module vs mvn-version map which will be used later by the Application
     * to populate the TIBCO.xml
     *  
     * @throws Exception
     */
    private void addModules() throws Exception
    {
    	try
    	{
        	getLog().debug("Adding Modules to the Application EAR");

        	BWModulesParser parser = new BWModulesParser(session, project);
        	String bwEdition = manifest.getMainAttributes().getValue("TIBCO-BW-Edition");
        	parser.bwEdtion=bwEdition;
        	List<Artifact> artifacts = parser.getModulesSet();
        	
            for( Artifact artifact : artifacts )
            {
                
                //Find the Module JAR file
                File moduleJar = artifact.getFile();
                
                
                //Add the JAR file to the EAR file
                jarchiver.addFile( moduleJar ,  moduleJar.getName() );
                
                String version = BWProjectUtils.getModuleVersion(moduleJar);
                
                getLog().debug( "Adding Module JAR with name " + moduleJar.getName() + "  with version " + version );
                
                //Save the module version in the mvn-version Map.
                moduleVersionMap.put(  artifact.getArtifactId() , version );
                
                this.version = version; 

            }        
    		
    	}
    	catch(Exception e )
    	{
    		getLog().error("Failed to add modules to the Application");
    		throw e;
    	}
    }
    

    
	/**
	 * Returns the Archive file name and location. The Archive file is created in the Target directory 
	 * with the name same as application project which is also the artifactId for the Application project. 
	 * @return
	 */
	private File getArchiveFileName()
	{
		String fullVersion = BWProjectUtils.convertMvnVersionToOSGI(project.getVersion());
		
        String archiveName = project.getArtifactId() + "_" + fullVersion + ".ear";
        File archiveFile = new File( outputDirectory , archiveName );

        getLog().debug("The EAR file name for Application is " + archiveFile.toString() );
        
        return archiveFile;
	}

    
    /**
     * Adds the from the META-INF folder to the EAR file.
     * The META-INF folder needs to be copied as it is.
     * 
     * @param metainf the META-INF folder location for the Application project.
     * 
     * @return the NANIFEST.MF file for the Application project.
     * 
     * @throws Exception
     */
	private File addFiletoEAR( File metainf ) throws Exception
	{
		File manifestFile = null;

	    File [] fileList = metainf.listFiles();

	    getLog().debug("Adding files to META-INF folder of EAR. ");
		
	       for( int i = 0 ; i < fileList.length; i++ )
	       {

	    	   // If the File is MANIFEST.MF then the mvn-version needs to be updated in the File
	    	   // and added to the Archiver
	    	   if(fileList[i].getName().indexOf("MANIFEST") != -1 )
	    	   {
	    		   manifestFile = getUpdatedManifest ( fileList[i]);
	    		   jarchiver.addFile(manifestFile , "META-INF/" + fileList[i].getName());
	    	   }
	    	   
	    	   // If the File is TIBCO.xml then the each Module mvn-version needs to be updated in the File.
	    	   else if( fileList[i].getName().indexOf("TIBCO.xml") != -1 )
	    	   {
	    		   File tibcoXML = getUpdatedTibcoXML( fileList[i]);
	    		   jarchiver.addFile(tibcoXML , "META-INF/" + fileList[i].getName());
	    	   }
	    	   
	    	   // The substvar files needs to be added as it is.
	    	   else if( fileList[i].getName().indexOf(".substvar") != -1 )
	    	   {
	    		   jarchiver.addFile(fileList[i] , "META-INF/" + fileList[i].getName());
	    	   }
	    	   
	    	   // The rest of the files can be ignored.
	    	   else
	    	   {
	    		   continue;   
	    	   }	    	   
	       }


	       return manifestFile;
	}
	

	/**
	 * Updates the MANIFEST.MF with the Module mvn-version.
	 * 
	 * @param manifest the MANIFEST.MF file
	 * 
	 * @return the updated MANIFEST.MF file
	 * 
	 * @throws Exception
	 */
	private File getUpdatedManifest( File manifest ) throws Exception
	{
		//Copy the MANIFEST.MF to a temporary location.
		File tempManifest = File.createTempFile("bwear", "mf");
		FileUtils.copyFile(manifest, tempManifest);

		FileInputStream is = new FileInputStream(tempManifest);
		Manifest mf = new Manifest( new FileInputStream(tempManifest));
		is.close();
		
		// Update the Bundle with mvn-version
		Attributes attr = mf.getMainAttributes();
		attr.putValue("Bundle-Version", version);
		
		getLog().debug( "Manifest updated with Version " + version );
		
		//Write the updated file and return the same.
		FileOutputStream os = new FileOutputStream( tempManifest );
		mf.write( os );
		os.close();
		
		tempFiles.add(tempManifest);
		
		getLog().debug("manifest added to temp location at " + tempManifest.toString() );
		
		return tempManifest;
	}
	


    /**
     * Finds the folder name META-INF inside the Application Project.
     * 
     * @return the META-INF folder
     * 
     * @throws Exception
     */
	private File getApplicationMetaInf() throws Exception
	{
        File [] fileList = projectBasedir.listFiles( new FileFilter() {
			
			public boolean accept(File pathname) {
				if (pathname.getName().indexOf("META-INF") != -1 )
				{
        			return true;
				}
				return false;
			}
		});
        
       
       return fileList[0];

	}


    
//    /**
//     * Finds the JAR file for the Module.
//     * 
//     * @param target the Module Output directory which is the target directory.
//     * 
//     * @return the Module JAR.
//     * 
//     * @throws Exception
//     */
//	private File getModuleJar( File target ) throws Exception
//	{
//		
//        File[] files = BWFileUtils.getFilesForType( target , ".jar" );
//        
//        files = BWFileUtils.sortFilesByDateDesc(files);
//        
//        if( files.length == 0 )
//        {
//        	throw new Exception("Module is not built yet. Please check your Application PO for the Module entry.");
//        }
//
//        return files[0];
//	}
}

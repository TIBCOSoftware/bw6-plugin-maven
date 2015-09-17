package com.tibco.bw.maven.plugin.module;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.Manifest;

import org.apache.maven.archiver.MavenArchiveConfiguration;
import org.apache.maven.archiver.MavenArchiver;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectBuilder;
import org.apache.maven.settings.Settings;
import org.codehaus.plexus.archiver.Archiver;
import org.codehaus.plexus.archiver.ArchiverException;
import org.codehaus.plexus.archiver.FileSet;
import org.codehaus.plexus.archiver.jar.JarArchiver;
import org.codehaus.plexus.archiver.jar.ManifestException;
import org.codehaus.plexus.archiver.util.DefaultFileSet;

import com.tibco.bw.maven.plugin.build.BuildProperties;
import com.tibco.bw.maven.plugin.build.BuildPropertiesParser;
import com.tibco.bw.maven.plugin.osgi.helpers.ManifestParser;
import com.tibco.bw.maven.plugin.osgi.helpers.ManifestWriter;
import com.tibco.bw.maven.plugin.osgi.helpers.VersionParser;


@Mojo( name = "bwmodule", defaultPhase = LifecyclePhase.PACKAGE )
public class BWModulePackageMojo  extends AbstractMojo
{
    /**
     * Location of the file.
     */
    @Parameter( defaultValue = "${project.build.directory}", property = "outputDir", required = true )
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

    @Parameter( defaultValue = "${project.build.outputDirectory}", required = true )
    private File classesDirectory;

    
    @Component
    private Settings settings;
    
    private Manifest manifest;
    

    /**
     * The Jar archiver.
     */
    @Component(role = Archiver.class, hint = "jar")
    private JarArchiver jarArchiver;

   

    MavenArchiver archiver;

    @Parameter
    protected MavenArchiveConfiguration archiveConfiguration;

    
    public void execute()
        throws MojoExecutionException
    {
    	try
    	{
            MavenArchiver archiver = new MavenArchiver();
            
    	    archiveConfiguration = new MavenArchiveConfiguration();

            archiver.setArchiver(jarArchiver);

            manifest = ManifestParser.parseManifest(projectBasedir) ;
            
            updateManifestVersion();
            
            removeExternals();
            
            File pluginFile = getPluginJAR();                       
                        
            FileSet set = getFileSet();
            
            
            addDependencies();
            
            if(classesDirectory != null && classesDirectory.exists() )
            {
            	archiver.getArchiver().addDirectory( classesDirectory );	
            }
            
            		
            archiver.getArchiver().addFileSet( set );
            
            archiver.setOutputFile(pluginFile);

            File manifestFile = ManifestWriter.updateManifest(project, manifest);

            jarArchiver.setManifest(manifestFile);
            
            archiver.createArchive(session, project, archiveConfiguration);
            
            project.getArtifact().setFile( pluginFile);

    	}
    	catch (IOException e) 
    	{
            throw new MojoExecutionException("Error assembling JAR", e);
        }
    	catch (ArchiverException e)
    	{
            throw new MojoExecutionException("Error assembling JAR", e);
        }
    	catch (ManifestException e)
    	{
            throw new MojoExecutionException("Error assembling JAR", e);
        }
    	catch (DependencyResolutionRequiredException e) 
    	{
            throw new MojoExecutionException("Error assembling JAR", e);
        }
    	


    }



	private void addDependencies() {
		Set<Artifact> artifacts = project.getDependencyArtifacts();
		
		StringBuffer buffer = new StringBuffer();
		
		for( Artifact artifact : artifacts )
		{
			jarArchiver.addFile( artifact.getFile(), "lib/" + artifact.getFile().getName() );
			buffer.append(",lib/" + artifact.getFile().getName());
		}
		
		String bundleClasspath = manifest.getMainAttributes().getValue( "Bundle-ClassPath");
		if( bundleClasspath == null || bundleClasspath.isEmpty() )
		{
			bundleClasspath = ".";
		}
		bundleClasspath = bundleClasspath + buffer.toString();
		
		manifest.getMainAttributes().putValue("Bundle-ClassPath",  bundleClasspath );
	}



	private FileSet getFileSet() {
		BuildProperties buildProperties = BuildPropertiesParser.parse(projectBasedir); 
		
		List<String> binIncludesList = buildProperties.getBinIncludes();
		List<String> binExcludeList = buildProperties.getBinExcludes();

		FileSet set = getFileSet(projectBasedir, binIncludesList, binExcludeList);
		return set;
	}



	private File getPluginJAR() 
	{
		String qualifierVersion  = manifest.getMainAttributes().getValue("Bundle-Version" );
		File pluginFile = new File(outputDirectory,  manifest.getMainAttributes().getValue("Bundle-SymbolicName") +  "_" + qualifierVersion + ".jar");
		if (pluginFile.exists()) 
		{
		    pluginFile.delete();
		}
		return pluginFile;
	}
    
    
    
    protected FileSet getFileSet(File basedir, List<String> includes, List<String> excludes) {
        DefaultFileSet fileSet = new DefaultFileSet();
        fileSet.setDirectory(basedir);

        if(includes.contains("target/"))
        {
        	includes.remove("target/");
        }
        
        if (includes.isEmpty()) 
        {
            fileSet.setIncludes(new String[] { "" });
        } 
        else 
        {
            fileSet.setIncludes(includes.toArray(new String[includes.size()]));
        }

        Set<String> allExcludes = new LinkedHashSet<String>();
        
        if (excludes != null) 
        {
            allExcludes.addAll(excludes);
        }
        
        fileSet.setExcludes(allExcludes.toArray(new String[allExcludes.size()]));

        return fileSet;
    }
    
    private void updateManifestVersion()
    {
    	
    	String version = manifest.getMainAttributes().getValue("Bundle-Version");
    	String qualifierVersion = VersionParser.getcalculatedOSGiVersion(version);
    	manifest.getMainAttributes().putValue("Bundle-Version", qualifierVersion );
    	
    }
    
    private void removeExternals()
    {
    	String bundlePath = manifest.getMainAttributes().getValue("Bundle-ClassPath");
    	if( bundlePath != null )
    	{
        	String [] entries = bundlePath.split(",");
        	
        	StringBuffer buffer = new StringBuffer();
        	int start = 0;
        	for( String entry : entries )
        	{
        		if( entry.indexOf( "external") == -1 )
        		{
            		if ( start != 0 )
            		{
            			buffer.append( "," );
            		}

        			buffer.append( entry );	
        		}
    			start++;	
        		
        	}
        	
        	manifest.getMainAttributes().putValue("Bundle-ClassPath", buffer.toString() );
    		
    	}
    }

}

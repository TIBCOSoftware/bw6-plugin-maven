package com.tibco.bw.maven.plugin.module;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.Manifest;
import java.util.jar.Attributes.Name;

import org.apache.maven.archiver.MavenArchiveConfiguration;
import org.apache.maven.archiver.MavenArchiver;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.artifact.resolver.filter.TypeArtifactFilter;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.DefaultDependencyResolutionRequest;
import org.apache.maven.project.DependencyResolutionException;
import org.apache.maven.project.DependencyResolutionResult;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectDependenciesResolver;
import org.apache.maven.shared.dependency.graph.DependencyGraphBuilder;
import org.apache.maven.shared.dependency.graph.DependencyGraphBuilderException;
import org.apache.maven.shared.dependency.graph.DependencyNode;
import org.apache.maven.shared.dependency.graph.traversal.DependencyNodeVisitor;
import org.codehaus.plexus.archiver.Archiver;
import org.codehaus.plexus.archiver.ArchiverException;
import org.codehaus.plexus.archiver.FileSet;
import org.codehaus.plexus.archiver.jar.JarArchiver;
import org.codehaus.plexus.archiver.jar.ManifestException;
import org.codehaus.plexus.archiver.util.DefaultFileSet;
import org.eclipse.aether.graph.Dependency;

import com.tibco.bw.maven.plugin.build.BuildProperties;
import com.tibco.bw.maven.plugin.build.BuildPropertiesParser;
//import com.tibco.bw.maven.plugin.classpath.ClassPathFile;
//import com.tibco.bw.maven.plugin.classpath.ClassPathFileParser;
import com.tibco.bw.maven.plugin.osgi.helpers.ManifestParser;
import com.tibco.bw.maven.plugin.osgi.helpers.ManifestWriter;
import com.tibco.bw.maven.plugin.osgi.helpers.VersionParser;
import com.tibco.bw.maven.plugin.utils.Constants;

@Mojo(name = "bwmodule", defaultPhase = LifecyclePhase.PACKAGE)
public class BWModulePackageMojo extends AbstractMojo {
	// Location of the file.
    @Parameter(defaultValue = "${project.build.directory}", property = "outputDir", required = true)
    private File outputDirectory;

	@Parameter(property="project.basedir")
	private File projectBasedir;

	@Parameter(defaultValue = "${session}", readonly = true )
    private MavenSession session;

    @Parameter(defaultValue="${project}", readonly = true)
    private MavenProject project;

    @Parameter(defaultValue = "${project.build.outputDirectory}", required = true)
    private File classesDirectory;
    
    @Parameter(defaultValue = Constants.TIMESTAMP)
    private String qualifierReplacement;

    private Manifest manifest;

    // The Jar archiver.
    @Component(role = Archiver.class, hint = "jar")
    private JarArchiver jarArchiver;

    @Component
    DependencyGraphBuilder builder;

    @Component
    ProjectDependenciesResolver resolver;

    MavenArchiver archiver;

    @Parameter
    protected MavenArchiveConfiguration archiveConfiguration;

    public void execute() throws MojoExecutionException {
    	try {
    		getLog().info("Module Packager Mojo started for Module " + project.getName() + " ...");
            MavenArchiver archiver = new MavenArchiver();

    	    archiveConfiguration = new MavenArchiveConfiguration();

            archiver.setArchiver(jarArchiver);

            manifest = ManifestParser.parseManifest(projectBasedir);
            if(manifest == null){
            	throw new Exception("Failed to parse MANIFEST.MF for project -> "+ projectBasedir);
            }
            getLog().info("Updated the Manifest version ");
            ManifestWriter.udpateManifestAttributes(project, manifest, qualifierReplacement);
            
            getLog().info("Removing the externals entries if any. ");
            removeExternals();
            
            ManifestParser.updateWriteManifest(projectBasedir, manifest);

            File pluginFile = getPluginJAR();
            getLog().info("Created Plugin JAR with name " + pluginFile.toString());
            FileSet set = getFileSet();

            getLog().info("Adding Maven Dependencies to the Plugin JAR file");

            addDependencies();

            if(classesDirectory != null && classesDirectory.exists()) {
            	archiver.getArchiver().addDirectory(classesDirectory);
            }

            archiver.getArchiver().addFileSet(set);
            archiver.setOutputFile(pluginFile);

            File manifestFile = ManifestWriter.updateManifest(project, manifest);
            
            jarArchiver.setManifest(manifestFile);

            getLog().info("Creating the Plugin JAR file");
            archiver.createArchive(session, project, archiveConfiguration);

            project.getArtifact().setFile(pluginFile);

         // Code for BWCE
            String bwEdition = manifest.getMainAttributes().getValue(Constants.TIBCO_BW_EDITION);
            if(bwEdition != null && bwEdition.equals(Constants.BWCF)) {
            	List<MavenProject> amendedProjects = new ArrayList<>();
            	for(MavenProject proj: session.getAllProjects())
				{
					if(proj.getArtifactId().equals(project.getArtifactId())) {
						amendedProjects.add(project);
					}
					else {
						amendedProjects.add(proj);
					}
				}
            	session.setAllProjects(amendedProjects);
            }

            getLog().info("BW Module Packager Mojo finished execution.");
    	} catch (IOException e) {
            throw new MojoExecutionException("Error assembling JAR", e);
        } catch (ArchiverException e) {
            throw new MojoExecutionException("Error assembling JAR", e);
        } catch (ManifestException e) {
            throw new MojoExecutionException("Error assembling JAR", e);
        } catch (DependencyResolutionRequiredException e) {
            throw new MojoExecutionException("Error assembling JAR", e);
        } catch (Exception e) {
        	throw new MojoExecutionException("Error assembling JAR", e);
		}
    }

	private void addDependencies() throws Exception {
		getLog().debug("Adding Maven dependencies to the JAR file");
		Set<Artifact> artifacts = project.getDependencyArtifacts();
		HashMap<File,String> artifactFiles = new HashMap<File,String>(); 

		for(Artifact artifact : artifacts) {
			if(!artifact.getVersion().equals("0.0.0")) {
				artifactFiles.put(artifact.getFile(),artifact.getScope());
			}
		}

        DependencyResolutionResult resolutionResult = getDependencies();
        getLog().debug(resolutionResult.toString());
        getLog().debug(resolutionResult.getDependencies().toString());

        if (resolutionResult != null) {
        	for(Dependency dependency : resolutionResult.getDependencies()) {
                getLog().debug("Adding artifact for dependency => " + dependency + ". The file for Dependency is => "  + dependency.getArtifact().getFile());
    			if(!dependency.getArtifact().getVersion().equals("0.0.0")) {
            		artifactFiles.put(dependency.getArtifact().getFile(),dependency.getScope());
    			}
        	}
        }

		StringBuffer buffer = new StringBuffer();
		for(File file : artifactFiles.keySet()) {
			if(artifactFiles.get(file).equalsIgnoreCase("provided")|| artifactFiles.get(file).equalsIgnoreCase("test") || artifactFiles.get(file).equalsIgnoreCase("system") || file.getName().indexOf("com.tibco.bw.palette.shared") != -1 || file.getName().indexOf("com.tibco.xml.cxf.common") != -1 || file.getName().indexOf("tempbw") != -1){
				continue;
			}
			
			boolean isSharedModule = false;
			Manifest mf = ManifestParser.parseManifestFromJAR( file);
			if(mf == null){
				throw new Exception("Failed to get Manifest for - "+ file.getName() +". To run in a BW environment, the dependency must be an OSGI plugin. Please verify if jar file is valid, the MANIFEST.MF should be first or second entry in the jar file. Use Command - jar tf <Jar_File_Path> to verify.");
			}
			for( Object str : mf.getMainAttributes().keySet())
			{
				getLog().debug( str.toString() );
				if( Constants.TIBCO_SHARED_MODULE.equals(str.toString() ))
				{
					isSharedModule = true;
					break;
				}
			}
			if(!isSharedModule) {
				getLog().debug("Dependency added with name " + file.toString());
				jarArchiver.addFile(file, "lib/" + file.getName());
				buffer.append(",lib/" + file.getName());
			}
		}

		String bundleClasspath = manifest.getMainAttributes().getValue(Constants.BUNDLE_CLASSPATH);
		if(bundleClasspath == null || bundleClasspath.isEmpty()) {
			bundleClasspath = ".";
		}
		bundleClasspath = bundleClasspath + buffer.toString();
		getLog().debug("Final Bundle-Classpath is " + bundleClasspath);
		manifest.getMainAttributes().putValue(Constants.BUNDLE_CLASSPATH, bundleClasspath);
	}

	private DependencyResolutionResult getDependencies() {
		DependencyResolutionResult resolutionResult = null;
        try {
        	getLog().debug("Looking up dependency tree for the current project => " +  project + " and the current session => " + session);
            DefaultDependencyResolutionRequest resolution = new DefaultDependencyResolutionRequest(project, session.getRepositorySession());
            resolutionResult = resolver.resolve(resolution);
        } catch (DependencyResolutionException e) {
        	getLog().debug("Caught DependencyResolutionException for the project => " + e.getMessage() + " with cause => " + e.getCause());
        	e.printStackTrace();
            resolutionResult = e.getResult();
        }
		return resolutionResult;
	}

	private FileSet getFileSet() {
		BuildProperties buildProperties = BuildPropertiesParser.parse(projectBasedir); 
		List<String> binIncludesList = buildProperties.getBinIncludes();
		List<String> binExcludeList = buildProperties.getBinExcludes();
		getLog().debug("BinInclude list is " + binIncludesList.toString());
		getLog().debug("BinExclude list is " + binExcludeList.toString());
		FileSet set = getFileSet(projectBasedir, binIncludesList, binExcludeList);
		return set;
	}

	@SuppressWarnings("unused")
	private void calculateDependencies(Artifact artifact) {
		TypeArtifactFilter filter = new TypeArtifactFilter("jar");
		filter.include(artifact);
		try {
			DependencyNode node = builder.buildDependencyGraph(project, filter);
			node.getArtifact();
			node.accept(new DependencyNodeVisitor() {
				public boolean visit(DependencyNode node) {
					node.getArtifact();
					return true;
				}

				public boolean endVisit(DependencyNode node) {
					return true;
				}
			});
		} catch (DependencyGraphBuilderException e) {
			e.printStackTrace();
		}
	}

	private File getPluginJAR() {
		String qualifierVersion = manifest.getMainAttributes().getValue(Constants.ARCHIVE_FILE_VERSION);
		if(qualifierVersion != null && qualifierVersion.endsWith(".")) {
			qualifierVersion = qualifierVersion.substring(0, qualifierVersion.lastIndexOf("."));
		}
		String name = manifest.getMainAttributes().getValue(Constants.BUNDLE_SYMBOLIC_NAME);

		if(name.indexOf(";") != -1) {
			name = name.substring(0, (name.indexOf(";") -1));
		}

		getLog().debug("Creating Plugin JAR from name  " + name);		
		File pluginFile = new File(outputDirectory, name +  "_" + qualifierVersion + ".jar");
		if (pluginFile.exists()) {
		    pluginFile.delete();
		}
		return pluginFile;
	}

    protected FileSet getFileSet(File basedir, List<String> includes, List<String> excludes) {
        DefaultFileSet fileSet = new DefaultFileSet();
        fileSet.setDirectory(basedir);
        if(includes.contains("target/")) {
        	includes.remove("target/");
        }
        
        if(isSharedModule() ){
        	//Ensure .project and .config are added
        	if(!includes.contains(".config")){
        		includes.add(".config");
        	}
        	if(!includes.contains(".project")){
        		includes.add(".project");
        	}
        	
//        	ClassPathFile cf = ClassPathFileParser.parse(projectBasedir);
//        	if(!cf.getSourceEntries().isEmpty()){
//        		includes.add(".classpath");
//        		for(String srcFolder : cf.getSourceEntries()){
//        			if(!includes.contains(srcFolder)){
//        				includes.add(srcFolder);
//        			}
//        		}
//        	}
        }
        if(isCXFProject()) {
    		if(!includes.contains(".project")){
        		includes.add(".project");
        	}
			
    		if(!includes.contains("pom.xml"))
    		{ 
    			includes.add("pom.xml"); 
    		}

    		if(!includes.contains("build.properties"))
    		{
    			includes.add("build.properties");
    		}
			 
    	}
    	
        if (includes.isEmpty()) {
            fileSet.setIncludes(new String[] { "" });
        } else {
            fileSet.setIncludes(includes.toArray(new String[includes.size()]));
        }

        Set<String> allExcludes = new LinkedHashSet<String>();
        if (excludes != null) {
            allExcludes.addAll(excludes);
        }
        fileSet.setExcludes(allExcludes.toArray(new String[allExcludes.size()]));
        return fileSet;
    }
    
    protected boolean isSharedModule(){
    	return manifest.getMainAttributes().getValue(Constants.TIBCO_SHARED_MODULE) == null ? false : true;
    }

    protected boolean isCXFProject(){
    	if(null != manifest.getMainAttributes().getValue("Import-Package")) {
    		return manifest.getMainAttributes().getValue("Import-Package").equals("com.tibco.xml.cxf.common.annotations") ;
    	}
    	else {
    		return false;
    	}
    }

    private void removeExternals() {
    	String bundlePath = manifest.getMainAttributes().getValue(Constants.BUNDLE_CLASSPATH);
    	getLog().debug("Bundle Classpath before removing externals is " + bundlePath);
    	if(bundlePath != null) {
        	String[] entries = bundlePath.split(",");
        	StringBuffer buffer = new StringBuffer();
        	for(String entry : entries) {
        		if(entry.indexOf("external") == -1) {
            		if (buffer.length()!= 0) {
            			buffer.append(",");
            		}
        			buffer.append(entry);
        		}
        	}
        	getLog().debug("Bundle Classpath after removing externals is " + buffer.toString());
        	manifest.getMainAttributes().putValue(Constants.BUNDLE_CLASSPATH, buffer.toString());
    	}
    }
}

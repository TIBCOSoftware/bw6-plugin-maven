package com.tibco.bw.maven.plugin.module;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.Manifest;

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

    @Component
    private MavenSession session;

    @Component
    private MavenProject project;

    @Parameter(defaultValue = "${project.build.outputDirectory}", required = true)
    private File classesDirectory;

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

            getLog().info("Updated the Manifest version ");
            File manifestFile = ManifestWriter.updateManifest(project, manifest);
            updateManifestVersion();

            getLog().info("Removing the externals entries if any. ");
            removeExternals();

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

           // File manifestFile = ManifestWriter.updateManifest(project, manifest);

            jarArchiver.setManifest(manifestFile);

            getLog().info("Creating the Plugin JAR file");
            archiver.createArchive(session, project, archiveConfiguration);

            project.getArtifact().setFile(pluginFile);

            // Code for BWCE
            String bwEdition = manifest.getMainAttributes().getValue(Constants.TIBCO_BW_EDITION);
            if(bwEdition != null && bwEdition.equals(Constants.BWCF)) {
            	List<MavenProject> projs = session.getAllProjects();
            	for(int i = 0; i < projs.size(); i++) {
            		MavenProject proj = projs.get(i);
            		if(proj.getArtifactId().equals(project.getArtifactId())) {
        				session.getAllProjects().set(i, project);
        			}
            	}
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
        }
    }

	private void addDependencies() {
		getLog().debug("Adding Maven dependencies to the JAR file");
		Set<Artifact> artifacts = project.getDependencyArtifacts();
		Set<File> artifactFiles = new HashSet<File>(); 

		for(Artifact artifact : artifacts) {
			if(!artifact.getVersion().equals("0.0.0")) {
				artifactFiles.add(artifact.getFile());
			}
		}

        DependencyResolutionResult resolutionResult = getDependencies();
        getLog().debug(resolutionResult.toString());
        getLog().debug(resolutionResult.getDependencies().toString());

        if (resolutionResult != null) {
        	for(Dependency dependency : resolutionResult.getDependencies()) {
                getLog().debug("Adding artifact for dependency => " + dependency + ". The file for Dependency is => "  + dependency.getArtifact().getFile());
    			if(!dependency.getArtifact().getVersion().equals("0.0.0")) {
            		artifactFiles.add(dependency.getArtifact().getFile());
    			}
        	}
        }

		StringBuffer buffer = new StringBuffer();
		for(File file : artifactFiles) {
			if(file.getName().indexOf("com.tibco.bw.palette.shared") != -1 || file.getName().indexOf("com.tibco.xml.cxf.common") != -1 || file.getName().indexOf("tempbw") != -1) {
				continue;
			}
			getLog().debug("Dependency added with name " + file.toString());
			jarArchiver.addFile(file, "lib/" + file.getName());
			buffer.append(",lib/" + file.getName());
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
		String qualifierVersion = manifest.getMainAttributes().getValue(Constants.BUNDLE_VERSION);
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

    private void updateManifestVersion() {
    	String version = manifest.getMainAttributes().getValue(Constants.BUNDLE_VERSION);
    	String qualifierVersion = VersionParser.getcalculatedOSGiVersion(version);
    	getLog().info("The OSGi verion is " + qualifierVersion + " for Maven version of " + version);
    	manifest.getMainAttributes().putValue(Constants.BUNDLE_VERSION, qualifierVersion);
    }

    private void removeExternals() {
    	String bundlePath = manifest.getMainAttributes().getValue(Constants.BUNDLE_CLASSPATH);
    	getLog().debug("Bundle Classpath before removing externals is " + bundlePath);
    	if(bundlePath != null) {
        	String[] entries = bundlePath.split(",");
        	StringBuffer buffer = new StringBuffer();
        	int start = 0;
        	for(String entry : entries) {
        		if(entry.indexOf("external") == -1) {
            		if (start != 0) {
            			buffer.append(",");
            		}
        			buffer.append(entry);
        		}
    			start++;
        	}
        	getLog().debug("Bundle Classpath after removing externals is " + buffer.toString());
        	manifest.getMainAttributes().putValue(Constants.BUNDLE_CLASSPATH, buffer.toString());
    	}
    }
}

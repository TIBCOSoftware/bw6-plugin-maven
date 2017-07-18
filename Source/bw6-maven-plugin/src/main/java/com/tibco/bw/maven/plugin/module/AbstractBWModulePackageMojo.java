package com.tibco.bw.maven.plugin.module;

import com.tibco.bw.maven.plugin.build.BuildProperties;
import com.tibco.bw.maven.plugin.build.BuildPropertiesParser;
import com.tibco.bw.maven.plugin.osgi.helpers.ManifestParser;
import com.tibco.bw.maven.plugin.osgi.helpers.ManifestWriter;
import com.tibco.bw.maven.plugin.utils.Constants;
import com.tibco.bw.maven.plugin.utils.BWProjectUtils;
import org.apache.maven.archiver.MavenArchiveConfiguration;
import org.apache.maven.archiver.MavenArchiver;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.*;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;
import org.codehaus.plexus.archiver.Archiver;
import org.codehaus.plexus.archiver.FileSet;
import org.codehaus.plexus.archiver.jar.JarArchiver;
import org.codehaus.plexus.archiver.util.DefaultFileSet;

import java.io.File;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.Manifest;

public abstract class AbstractBWModulePackageMojo extends AbstractMojo {
    @Parameter
    private MavenArchiveConfiguration archiveConfiguration;
    
    protected String classifier;

    /**
     * Location of the file.
     */
    @Parameter(defaultValue = "${project.build.directory}", property = "outputDir", required = true)
    private File outputDirectory;
    @Parameter(property = "project.basedir")
    private File projectBasedir;
    @Component
    private MavenSession session;
    @Component
    private MavenProject project;
    @Parameter(defaultValue = "${project.build.outputDirectory}", required = true)
    private File classesDirectory;
    private Manifest manifest;
    @Component
    private MavenProjectHelper projectHelper;

    /**
     * The Jar archiver.
     */
    @Component(role = Archiver.class, hint = "jar")
    private JarArchiver jarArchiver;

    protected void executeInternal()
            throws MojoExecutionException {
        try {
            getLog().info("Module Packager Mojo started for Module " + project.getName() + " ...");

            if (classifier == null || !(classifier.equals(Constants.BW_SHAREDMODULE) ||
                    classifier.equals(Constants.BW_APPMODULE) ||
					classifier.equals(Constants.OSGI_BUNDLE))) {
                throw new MojoFailureException(
                        "packaging must be set to 'bw-sharedmodule', 'bw-appmodule' or 'osgi-bundle'..");
            }

            MavenArchiver archiver = new MavenArchiver();

            archiveConfiguration = new MavenArchiveConfiguration();

            archiver.setArchiver(jarArchiver);

            manifest = ManifestParser.parseManifest(projectBasedir);

            getLog().info("Removing the externals entries if any. ");
            removeExternals();

            File pluginFile = getPluginJAR();

            getLog().info("Created Plugin JAR with name " + pluginFile.toString());
            FileSet set = getFileSet();

            getLog().info("Adding Maven Dependencies to the Plugin JAR file");

            addDependencies();

            if (classesDirectory != null && classesDirectory.exists()) {
                archiver.getArchiver().addDirectory(classesDirectory);
            }

            archiver.getArchiver().addFileSet(set);

            archiver.setOutputFile(pluginFile);

            getLog().info("Updated the Manifest version ");
            manifest.getMainAttributes().putValue("Bundle-Version", BWProjectUtils.convertMvnVersionToOSGI(getLog(),
                    project.getVersion(), true, false));

            File manifestFile = ManifestWriter.updateManifest(project.getBuild().getDirectory(), manifest);

            jarArchiver.setManifest(manifestFile);

            getLog().info("Creating the Plugin JAR file ");
            archiver.createArchive(session, project, archiveConfiguration);

            projectHelper.attachArtifact(project, "jar", classifier, pluginFile);

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

            getLog().info("BW Module Packager Mojo finished execution. ");
        } catch (Exception e) {
            throw new MojoExecutionException("Error assembling JAR", e);
        }
    }


    private void addDependencies() {
        getLog().debug("Adding Maven dependencies to the JAR file");

        String origBundlePath = manifest.getMainAttributes().getValue("Bundle-ClassPath");

        StringBuffer buffer = new StringBuffer(".");

        if (origBundlePath.contains("annoxfiles")){
            getLog().debug("Adding .annoxfiles back into classpath for generated JAXB configs..");
            buffer.append(",.annoxfiles/");
        }

        for (Artifact artifact : project.getArtifacts()) {

            // don't add bw-sharedmodule dependencies to bw-appmodules..
            // add jars to either type..

            String nextArtifactClassifier = BWProjectUtils.getClassifierOrEmptyString(artifact);

            if (!nextArtifactClassifier.equals(Constants.BW_APPMODULE) &&
                    !nextArtifactClassifier.equals(Constants.BW_SHAREDMODULE) &&
					!nextArtifactClassifier.equals(Constants.OSGI_BUNDLE) &&
					!artifact.getScope().equals("provided")) {
                String fileName = artifact.getFile().getName();
                getLog().debug("Adding dependency: " + fileName + " with scope: " + artifact.getScope());

                jarArchiver.addFile(artifact.getFile(), "lib/" + fileName);
                buffer.append(",lib/")
                        .append(fileName);
            }
            else {
                getLog().debug("Skipping dependency: " + artifact.getFile().getName() + " with scope: " + artifact.getScope());
            }
        }

        String bundleClasspath = buffer.toString();

        getLog().debug("Final Bundle-Classpath  is " + bundleClasspath);

        manifest.getMainAttributes().putValue("Bundle-ClassPath", bundleClasspath);
    }

    private FileSet getFileSet() throws Exception {
        BuildProperties buildProperties = BuildPropertiesParser.parse(projectBasedir);

        List<String> binIncludesList = buildProperties.getBinIncludes();
        List<String> binExcludeList = buildProperties.getBinExcludes();

        getLog().debug("Bininclude list is " + binIncludesList.toString());

        getLog().debug("Binexclude list is " + binExcludeList.toString());

        return getFileSet(projectBasedir, binIncludesList, binExcludeList);
    }

    private File getPluginJAR() {
        String qualifierVersion = manifest.getMainAttributes().getValue("Bundle-Version");
        if (qualifierVersion != null && qualifierVersion.endsWith(".")) {
            qualifierVersion = qualifierVersion.substring(0, qualifierVersion.lastIndexOf('.'));
        }
        getLog().info("Determined Qualifier Version:: " + qualifierVersion);
        String name = manifest.getMainAttributes().getValue("Bundle-SymbolicName");


        if (name.indexOf(";") != -1) {
            name = name.substring(0, (name.indexOf(";") - 1));
        }

        getLog().debug("Creating Plugin JAR from name  " + name);

        File pluginFile = new File(outputDirectory, name + "-" + classifier + "_" + qualifierVersion + ".jar");
        if (pluginFile.exists()) {
            pluginFile.delete();
        }
        return pluginFile;
    }


    private FileSet getFileSet(File basedir, List<String> includes, List<String> excludes) {
        DefaultFileSet fileSet = new DefaultFileSet();
        fileSet.setDirectory(basedir);

        // cleanup some common items that should not be in the jar..
        if (includes.contains("target/")) {
            includes.remove("target/");
        }
        if (includes.contains("bin/")) {
            includes.remove("bin/");
        }
        if (includes.contains("lib/")) { // this one is tricky.. already adding these in based on scope etc..
            includes.remove("lib/");     // don't want to do it twice
        }
        if (includes.contains("pom.xml")) {
            includes.remove("pom.xml");
        }
        if (includes.contains(".settings/")) {
            includes.remove(".settings/");
        }
        if (includes.contains(".WebResources/")) {
            includes.remove(".WebResources/");
        }
        if (includes.contains("README.md")) {
            includes.remove("README.md");
        }

        if (includes.isEmpty()) {
            fileSet.setIncludes(new String[]{""});
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

    private void removeExternals() {
        String bundlePath = manifest.getMainAttributes().getValue("Bundle-ClassPath");

        getLog().debug("Bundle Classpath before removing externals is " + bundlePath);

        if (bundlePath != null) {
            String[] entries = bundlePath.split(",");

            StringBuffer buffer = new StringBuffer();
            int start = 0;
            for (String entry : entries) {
                if (entry.indexOf("external") == -1) {
                    if (start != 0) {
                        buffer.append(",");
                    }

                    buffer.append(entry);
                }
                start++;

            }

            getLog().debug("Bundle Classpath after removing externals is " + buffer.toString());
            manifest.getMainAttributes().putValue("Bundle-ClassPath", buffer.toString());

        }
    }

}

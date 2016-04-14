package com.tibco.bw.maven.plugin.compile;

import org.apache.commons.io.FileUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;


@Mojo(name = "resolve-studio-project-lib", requiresDependencyResolution = ResolutionScope.COMPILE)
public class TibcoStudioMojo extends AbstractMojo {

    private static final String STUDIO_PROJECT_LIB = "lib";

    @Parameter(defaultValue = "${project.build.sourceDirectory}", required = true, readonly = true)
    private String projectSourceDirectory;

    @Parameter(defaultValue = "${project}", required = true)
    private MavenProject project;

    public void execute() throws MojoExecutionException, MojoFailureException {
        if (!project.getPackaging().equals("bwmodule")) return;

        String moduleLibPath = project.getBasedir() + File.separator + STUDIO_PROJECT_LIB;
        File moduleLib = new File(moduleLibPath);
        cleanModuleLib(moduleLib);

        try {
            getLog().info("TibcoStudioMojo: --- checking for bwmodule and jar dependency types in this project ---");
            Set<Artifact> projectDependencies = new HashSet<Artifact>();
            for (Artifact artifact : project.getArtifacts()) {
                getLog().info("Resolving artifact: " + artifact.getArtifactId());
                getLog().info("  Type: " + artifact.getType());
                getLog().info("  File: " + artifact.getFile());
                if (artifact.getFile() != null && ((artifact.getType().equals("bwmodule") || artifact.getType().equals("jar")))
                        && !artifact.getArtifactId().startsWith("com.tibco.bw.palette")) {
                    getLog().info("TibcoStudioMojo:    Attempting to sync artifact: " + artifact.getArtifactId());
                    updateModuleLib(moduleLib, artifact.getFile());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new MojoFailureException(e.getMessage(), e);
        }

    }

    private void updateFileAliases(Set<Artifact> artifacts) throws IOException {
        int currentPosition = 0;
    }

    /**
     * @param moduleLib
     */
    private void cleanModuleLib(File moduleLib) {
        try {
            if (moduleLib.exists() && moduleLib.isDirectory()) {
                FileUtils.cleanDirectory(moduleLib);
                getLog().info("TibcoStudioMojo:  Cleaned project lib folder for incoming maven dependencies");
            }
        } catch (Exception e) {
            getLog().error("TibcoStudioMojo:   Failed to clean project/lib folder to prep for copy of jars from .m2 repository to enable Studio..");
        }
    }

    /**
     * @param moduleLib
     * @param file
     */
    private void updateModuleLib(File moduleLib, File file) {

        try {
            if (!moduleLib.exists()) {
                moduleLib.mkdir();
                getLog().info("TibcoStudioMojo:       Created project lib folder for incoming maven dependencies");
            }
            FileUtils.copyFile(file, new File(moduleLib + File.separator + file.getName()));
            getLog().info("TibcoStudioMojo:       Copied " + file.getCanonicalPath() + " from local maven repo into project lib folder..");
        } catch (Exception e) {
            try {
                getLog().error("TibcoStudioMojo:       Failed to copy " + file.getCanonicalPath() + " to " + moduleLib.getCanonicalPath());
            } catch (Exception e1) {
                getLog().error("TibcoStudioMojo:       updateModuleLib failed to log error about copy a jar from .m2 to project/lib folder");
            }
        }
    }
}

package com.tibco.bw.maven.plugin.compile;

import com.tibco.bw.maven.plugin.application.AbstractBWApplicationMojo;
import com.tibco.bw.maven.plugin.osgi.helpers.ManifestParser;
import com.tibco.bw.maven.plugin.osgi.helpers.ManifestWriter;
import com.tibco.bw.maven.plugin.utils.BWProjectUtils;
import org.apache.commons.io.FileUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.jar.Manifest;

/**
 * tcosley on 4/13/2016
 */
@Mojo(name = "resolve-studio-project-lib", requiresDependencyResolution = ResolutionScope.COMPILE)
public class TibcoStudioMojo extends AbstractBWApplicationMojo {

    private static final String STUDIO_PROJECT_LIB = "lib";
    //This map is required for maintaining the module name vs is version which needs
    //to be updated in the TibcoXML at the later stage.
    Map<String, String> moduleVersionMap;
    @Parameter(defaultValue = "${project.build.sourceDirectory}", required = true, readonly = true)
    private String projectSourceDirectory;
    @Parameter(defaultValue = "${project}", required = true)
    private MavenProject project;
    @Parameter(property = "project.basedir")
    private File projectBasedir;
    private Manifest manifest;


    public void execute() throws MojoExecutionException, MojoFailureException {
        if (!(project.getPackaging().equals("bwmodule")) || project.getPackaging().equals("bwear")) return;

        String moduleLibPath = project.getBasedir() + File.separator + STUDIO_PROJECT_LIB;
        File moduleLib = new File(moduleLibPath);

        moduleVersionMap = new HashMap<String, String>();
        initialize();

        manifest = ManifestParser.parseManifest(projectBasedir);
        String manifestFilePath = project.getBasedir() + File.separator + "META-INF" + File.separator + "MANIFEST.MF";
        String tibcoXMLFilePath = project.getBasedir() + File.separator + "META-INF" + File.separator + "TIBCO.xml";

        try {
            //
            // update META-INF/MANIFEST.MF to have local .m2 repository paths for jar dependencies..
            //
            getLog().info("Updated the Manifest version ");

            cleanModuleLib(moduleLib);


            manifest.getMainAttributes().putValue("Bundle-ClassPath", getModuleClasspathBuildLib(moduleLib));

            String qualifierVersion = BWProjectUtils.convertMvnVersionToOSGI(project.getVersion());
            manifest.getMainAttributes().putValue("Bundle-Version", qualifierVersion);

            File tmpManifestFile = ManifestWriter.updateManifest(project.getBasedir() + File.separator + "META-INF", manifest);

            // Copy manifestFile to project/META-INF to update for Studio debugging etc
            FileUtils.copyFile(tmpManifestFile, new File(manifestFilePath), true);

        } catch (Exception e) {
            getLog().error("Failed to update " + manifestFilePath);
        } finally {
            cleanup();
        }

        try {
            //
            // update META-INF/TIBCO.xml
            //
            if (project.getPackaging().equals("bwear")) {
                // update project to propagate mvn version to TIBCO.xml
                File tibcoXMLFile = new File(tibcoXMLFilePath);
                if (tibcoXMLFile.exists()) {
                    File tmpTibcoXML = getUpdatedTibcoXML(new File(tibcoXMLFilePath));

                    // copy from temp location to project/META-INF/TIBCO.xml
                    FileUtils.copyFile(tmpTibcoXML, new File(tibcoXMLFilePath));
                }
            }

        } catch (Exception e) {
            getLog().error("Failed to update " + tibcoXMLFilePath);
        } finally {
            cleanup();
        }
    }

    public String getModuleClasspathBuildLib(File moduleLib) throws MojoFailureException {
        StringBuffer moduleClasspath = new StringBuffer(".");
        try {
            getLog().info("TibcoStudioMojo: --- checking for bwmodule and jar dependency types in this project ---");
            Set<Artifact> projectDependencies = new HashSet<Artifact>();

            for (Artifact artifact : project.getArtifacts()) {
                getLog().info("Resolving artifact: " + artifact.getArtifactId());
                getLog().debug("  Type: " + artifact.getType());
                getLog().debug("  File: " + artifact.getFile());
                if (artifact.getFile() != null && ((artifact.getType().equals("bwmodule") || artifact.getType().equals("jar")))
                        && !artifact.getArtifactId().startsWith("com.tibco.bw.palette")) {
                    String modulePath = artifact.getFile().getName();
                    moduleClasspath.append(", lib/" + modulePath);
                    getLog().debug("TibcoStudioMojo:    Attempting to sync artifact: " + artifact.getArtifactId());
                    updateModuleLib(moduleLib, artifact.getFile());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new MojoFailureException(e.getMessage(), e);
        }
        return moduleClasspath.toString();
    }

    /**
     * @param moduleLib
     */
    private void cleanModuleLib(File moduleLib) {
        try {
            if (moduleLib.exists() && moduleLib.isDirectory()) {
                FileUtils.cleanDirectory(moduleLib);
                getLog().debug("TibcoStudioMojo:  Cleaned project lib folder for incoming maven dependencies");
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
                getLog().debug("TibcoStudioMojo:       Created project lib folder for incoming maven dependencies");
            }
            FileUtils.copyFile(file, new File(moduleLib + File.separator + file.getName()), true);
            getLog().info("TibcoStudioMojo:       Copied " + file.getCanonicalPath());
            getLog().info("                          .. from local maven repo into project lib folder..");
        } catch (Exception e) {
            try {
                getLog().error("TibcoStudioMojo:       Failed to copy " + file.getCanonicalPath() + " to " +
                        moduleLib.getCanonicalPath());

            } catch (Exception e1) {
                getLog().error("TibcoStudioMojo:       updateModuleLib failed to log error about copy a jar from .m2" +
                        " to project/lib folder");
            }
        }
    }
}

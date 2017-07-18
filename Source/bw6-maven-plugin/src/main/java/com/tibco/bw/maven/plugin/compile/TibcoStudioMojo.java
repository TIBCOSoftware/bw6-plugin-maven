package com.tibco.bw.maven.plugin.compile;

import com.tibco.bw.maven.plugin.osgi.helpers.ManifestParser;
import com.tibco.bw.maven.plugin.osgi.helpers.ManifestWriter;
import com.tibco.bw.maven.plugin.utils.Constants;
import com.tibco.bw.maven.plugin.utils.BWProjectUtils;
import org.apache.commons.io.FileUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.utils.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.jar.Manifest;

/**
 * Put stuff where BW Studio expects to see it..
 * <p>
 * <p>
 * For all bwmodule packages:
 * - update Bundle-Version
 * - create lib folder, populate with java jar dependencies from local .m2 repo.
 * - Update Bundle-Classpath in META-INF/MANIFEST.MF to reference jars in lib
 * <p>
 * For bwmodule with classifier of bw-sharedmodule:
 * - update Provide-Capability in META-INF/MANIFEST.MF
 * <p>
 * For bwmodule with classifier of bw-appmodule:
 * - update Require-Bundle, Require-Capability in META-INF/MANIFEST.MF
 * <p>
 * <p>
 * tcosley on 4/13/2016
 */
@Mojo(name = "resolve-studio-project-lib", requiresDependencyResolution = ResolutionScope.COMPILE)
public class TibcoStudioMojo extends AbstractMojo {

    private static final String STUDIO_PROJECT_JAR_LIB = "lib";

    @Parameter(defaultValue = "${project}", required = true)
    private MavenProject project;
    @Parameter(property = "project.basedir")
    private File projectBasedir;

    private String classifier="";

    @Parameter(property = "studio.jar.excludelist")
    private String studioJarExcludeList="";

    public void execute() throws MojoExecutionException, MojoFailureException {
        if (!project.getPackaging().equals(Constants.BW_APPMODULE) && !project.getPackaging().equals(Constants.BW_SHAREDMODULE) &&
                !project.getPackaging().equals(Constants.OSGI_BUNDLE)) return;

        classifier = project.getPackaging();

        String jarLibPath = project.getBasedir() + File.separator + STUDIO_PROJECT_JAR_LIB;
        File jarLib = new File(jarLibPath);

        Manifest manifest = null;
        try {
            manifest = ManifestParser.parseManifest(projectBasedir);
        } catch (Exception e) {
            throw new MojoFailureException("Failed to read project manifest.. " + project.getArtifact().getArtifactId(), e);
        }
        String manifestFilePath = project.getBasedir() + File.separator + "META-INF" + File.separator + "MANIFEST.MF";

        try {

            getLog().info("Updated the Manifest version for project/version: " + project.getName() + "/" +
                    project.getVersion());

            // Set Bundle-Version based on Maven version for bwmodule
            String qualifierVersion = BWProjectUtils.convertMvnVersionToOSGI(getLog(), project.getVersion(), true);
            manifest.getMainAttributes().putValue("Bundle-Version", qualifierVersion);


            // clear out module lib if it exists..
            if (classifier == null) {
                throw new MojoFailureException("packaging type must be set" +
                        " (to bw-sharedmodule, bw-appmodule or osgi-bundle");
            }

            // Build Bundle-Classpath (also copies .m2 jars into local project 'lib' folder for Studio..
            cleanLib(jarLib);
            manifest.getMainAttributes().putValue("Bundle-ClassPath", getModuleClasspathBuildLib(manifest.getMainAttributes().getValue("Bundle-Classpath"), jarLib));

            if (classifier.equals(Constants.BW_SHAREDMODULE)) {

                // If this is a bwmodule with classifier=bw-sharedmodule; update Provide-Capability
                manifest.getMainAttributes().putValue("Provide-Capability",
                        getProvideCapabilityList(manifest.getMainAttributes().getValue("Provide-Capability")));


            } else if (classifier.equals(Constants.BW_APPMODULE)) {

                // If this is a bwmodule with classifier=bw-appmodule dependency setup; update Require-Bundle
                manifest.getMainAttributes().putValue("Require-Bundle", getRequireBundleList());

                // If this is a bwmodule with classifier=bw-appmodule; update Require-Capability
                manifest.getMainAttributes().putValue("Require-Capability",
                        getRequireCapabilityList(manifest.getMainAttributes().getValue("Require-Capability")));
            }

            // Write updated manifest file
            ManifestWriter.updateManifest(project.getBasedir() + File.separator + "META-INF",
                    manifest);

        } catch (Exception e) {
            getLog().error("Failed to update " + manifestFilePath, e);
        }
    }

    /**
     * build Require-Bundle list.. basically list dependencies with bw-sharedmodule classification.
     *
     * @return requiredBundleList
     * @throws MojoFailureException
     */
    private String getRequireBundleList() throws MojoFailureException {
        StringBuilder requireBundle = new StringBuilder();

        try {
            getLog().info("TibcoStudioMojo: --- checking for inclusion of bw-sharedmodule (classifier) jar dependency types in this project ---");

            Iterator<Artifact> artifacts = project.getArtifacts().iterator();
            int indexBundlesRequired = 0;

            while (artifacts.hasNext()) {
                Artifact artifact = artifacts.next();

                getLog().info("TibcoStudioMojo:    Adding to Require-Bundle list - artifact id/version: " +
                        artifact.getArtifactId() + "/" + artifact.getVersion());

                if (indexBundlesRequired > 0) {
                    requireBundle.append(" ");
                }
                if ((artifact.getFile() != null) && 
					(BWProjectUtils.getClassifierOrEmptyString(artifact).equals(Constants.BW_SHAREDMODULE)||
					BWProjectUtils.getClassifierOrEmptyString(artifact).equals(Constants.OSGI_BUNDLE))) {
                    requireBundle.append(artifact.getArtifactId())
                            .append(";bundle-version=\"")
                            .append(BWProjectUtils.convertMvnVersionToOSGI(getLog(), artifact.getVersion(), false))
                            .append("\",");
                    indexBundlesRequired++;
                }
            }

        } catch (Exception e) {
            throw new MojoFailureException(e.getMessage(), e);
        }

        String requireBundleList = requireBundle.toString();
        requireBundleList = requireBundleList.substring(0, requireBundleList.lastIndexOf(','));

        // remove final comma char
        getLog().debug("Modified Require-Bundle list: " + requireBundleList);
        return requireBundleList;
    }


    /**
     * build Provide-Capability list.. basically list dependencies provided by module with bw-sharedmodule classifier..
     *
     * @param originalProvideCapabilityList
     * @return modifiedCapabilityList
     * @throws MojoFailureException
     */
    private String getProvideCapabilityList(String originalProvideCapabilityList) throws MojoFailureException {

        getLog().debug("Original Provide-Capability list: " + originalProvideCapabilityList);

        StringBuilder provideCapabilityList = new StringBuilder();
        List<String[]> capabilityList = new ArrayList<String[]>();

        try {
            // parse incoming capabilities and map by filter, type.. skip com.tibco.bw.module entries..
            String[] capabilityListParts = originalProvideCapabilityList.split(",\\s*");
            for (String capability : capabilityListParts) {
                String[] capabilityParts = capability.split(";\\s*");
                String packageName = capabilityParts[0];
                if (!packageName.equals("com.tibco.bw.module")) {
                    // store keyed by filter with version removed..
                    getLog().debug("  -- stored capability to list: " + capability);
                    capabilityList.add(capabilityParts);
                }
            }

            getLog().info("TibcoStudioMojo:    Adding to Provide-Capability list - artifact id/version: " +
                    project.getArtifactId() + "/" + project.getVersion());

            provideCapabilityList.append("com.tibco.bw.module; name=\"")
                    .append(project.getArtifactId())
                    .append("\"; version:Version=\"")
                    .append(BWProjectUtils.convertMvnVersionToOSGI(getLog(), project.getVersion(), false))
                    .append("\",");

            // now append remaining non-module entries back into buffer
            for (String[] entry : capabilityList) {
                provideCapabilityList.append(StringUtils.join(entry, "; "))
                        .append(",");
            }

        } catch (Exception e) {
            throw new MojoFailureException(e.getMessage(), e);
        }

        String modifiedCapabilityList = provideCapabilityList.toString();
        modifiedCapabilityList = modifiedCapabilityList.substring(0, modifiedCapabilityList.lastIndexOf(','));

        // remove final comma char
        getLog().debug("Modified Provide-Capability list: " + modifiedCapabilityList);
        return modifiedCapabilityList;
    }

    /**
     * build Require-Capability list.. basically list dependencies with bw-sharedmodule classifier..
     *
     * @param originalRequireCapabilityList
     * @return updatedRequireCapabilityList
     * @throws MojoFailureException
     */
    private String getRequireCapabilityList(String originalRequireCapabilityList) throws MojoFailureException {

        getLog().debug("Original Require-Capability list: " + originalRequireCapabilityList);

        StringBuilder requireCapabilityList = new StringBuilder();

        List<String[]> capabilityList = new ArrayList<String[]>();

        try {
            // parse incoming capabilities and map by filter, type.. skip com.tibco.bw.module entries..
            String[] capabilityListParts = originalRequireCapabilityList.split(",\\s*");
            for (String capability : capabilityListParts) {
                String[] capabilityParts = capability.split(";\\s*");
                String filterText = capabilityParts[1];
                String packageName = capabilityParts[0];
                if (!packageName.equals("com.tibco.bw.module")) {
                    // store keyed by filter with version removed..
                    getLog().debug("  -- stored capability to map: key/value" + filterText + "/" + packageName);
                    capabilityList.add(capabilityParts);
                }
            }

            getLog().info("TibcoStudioMojo: --- checking for inclusion of bw-sharedmodule (classifier) jar dependency types in this project ---");

            for (Artifact artifact : project.getArtifacts()) {
                getLog().info("TibcoStudioMojo:    Adding to Require-Capability list - artifact id/version: " +
                        artifact.getArtifactId() + "/" + artifact.getVersion());

                if ((artifact.getFile() != null) && BWProjectUtils.getClassifierOrEmptyString(artifact).equals(
                        Constants.BW_SHAREDMODULE)) {
                    requireCapabilityList.append("com.tibco.bw.module; filter:=\"(&(name=")
                            .append(artifact.getArtifactId())
                            .append(")(version=")
                            .append(BWProjectUtils.convertMvnVersionToOSGI(getLog(), artifact.getVersion(), false))
                            .append("))\",");
                }
            }

            // now append remaining non-module entries back into buffer
            for (String[] entry : capabilityList) {
                requireCapabilityList.append(StringUtils.join(entry, "; "))
                        .append(",");
            }

        } catch (Exception e) {
            throw new MojoFailureException(e.getMessage(), e);
        }

        String modifiedCapabilityList = requireCapabilityList.toString();
        modifiedCapabilityList = modifiedCapabilityList.substring(0, modifiedCapabilityList.lastIndexOf(','));

        // remove final comma char
        getLog().debug("Modified Require-Capability list: " + modifiedCapabilityList);
        return modifiedCapabilityList;
    }


    /**
     * @param jarLib
     * @return modifiedClasspath
     * @throws MojoFailureException
     */
    private String getModuleClasspathBuildLib(String origBundlePath, File jarLib) throws MojoFailureException {
        StringBuilder moduleClasspath = new StringBuilder(".");

        if (origBundlePath.contains("annoxfiles")){
            getLog().debug("Adding .annoxfiles back into classpath for generated JAXB configs..");
            moduleClasspath.append(",.annoxfiles/");
        }
        List<String> dependencyPaths = new ArrayList<>();

        try {
            getLog().info("TibcoStudioMojo: --- checking for bwmodule and jar dependency types in this project ---");

            for (Artifact artifact : project.getArtifacts()) {

                getLog().info("TibcoStudioMojo:    Attempting to sync artifact type/id: " + artifact.getType() + "/" + artifact.getArtifactId());

                String artifactName = artifact.getFile().getName();

                if ((artifact.getFile() != null) && artifact.getType().equals("jar")
                        && !artifact.getArtifactId().startsWith("com.tibco.bw.palette") &&
                        !BWProjectUtils.getClassifierOrEmptyString(artifact).equals(Constants.BW_SHAREDMODULE)) {
                    // check studio.jar.excludelist property..
                    if (!studioJarExcludeList.contains(artifactName) && !artifact.getScope().equals("provided")) {
                        moduleClasspath.append(",lib/")
                                .append(artifactName);
                        updateLib(jarLib, artifact.getFile());
                        dependencyPaths.add("lib/" + artifactName);
                    }
                }
            }
            File dotClasspathFile = new File(projectBasedir + File.separator + ".classpath");
            if (dotClasspathFile.exists()) {
                // update the .classpath file for the project if it exists..
                File tmpDotClasspath = getUpdatedDotClasspathXML(dependencyPaths, dotClasspathFile);
                FileUtils.copyFile(tmpDotClasspath, new File(projectBasedir + File.separator + ".classpath"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new MojoFailureException(e.getMessage(), e);
        }
        return moduleClasspath.toString();
    }

    /**
     * @param libLocation
     */
    private void cleanLib(File libLocation) {
        try {
            if (libLocation.exists() && libLocation.isDirectory()) {
                FileUtils.cleanDirectory(libLocation);
                getLog().debug("TibcoStudioMojo:  Cleaned project lib folder for incoming maven dependencies: " + libLocation.getAbsolutePath());
            }
        } catch (Exception e) {
            getLog().error("TibcoStudioMojo:   Failed to clean project/lib folder to prep for copy of jars from .m2 repository to enable Studio..");
        }
    }

    /**
     * @param libLocation
     * @param file
     */
    private void updateLib(File libLocation, File file) {

        try {
            if (!libLocation.exists()) {
                //noinspection ResultOfMethodCallIgnored
                libLocation.mkdir();
                getLog().debug("TibcoStudioMojo:       Created project lib folder for incoming maven dependencies");
            }
            FileUtils.copyFile(file, new File(libLocation + File.separator + file.getName()), true);
            getLog().info("TibcoStudioMojo:       Copied " + file.getCanonicalPath());
            getLog().info("                          .. from local maven repo into project lib folder..");
        } catch (Exception e) {
            try {
                getLog().error("TibcoStudioMojo:       Failed to copy " + file.getCanonicalPath() + " to " +
                        libLocation.getCanonicalPath());

            } catch (Exception e1) {
                getLog().error("TibcoStudioMojo:       updateModuleLib failed to log error about copy a jar from .m2" +
                        " to project/lib folder");
            }
        }
    }

    /**
     * Gets the Tibco XML file with the updated Module versions.
     *
     * @param dotClasspathFile the Application Project .classpath file
     * @return the updated .classpath file.
     * @throws Exception
     */
    private File getUpdatedDotClasspathXML(List<String> libPaths, File dotClasspathFile) throws Exception {
        getLog().debug("Updating the .classpath file with jars derived from maven dependency management.. ");
        Document doc = BWProjectUtils.loadXML(getLog(), dotClasspathFile);
        Element classpathRoot = doc.getDocumentElement();

        // remove any existing classpathentry elements with kind=lib
        XPathFactory xPathfactory = XPathFactory.newInstance();
        XPath xpath = xPathfactory.newXPath();
        XPathExpression expr = xpath.compile("//classpathentry[@kind=\"lib\"]");
        NodeList nodeList = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
        if (nodeList != null && nodeList.getLength() > 0) {
            for (int j = 0; j < nodeList.getLength(); j++) {
                Element el = (org.w3c.dom.Element) nodeList.item(j);
                el.getParentNode().removeChild(el).normalize();
                getLog().debug("Removed element: " + el.toString());
            }
        }
        // add libs in based on resolved maven dependencies
        for (String nextPath : libPaths) {
            Element nextEntry = doc.createElement("classpathentry");
            nextEntry.setAttribute("exported", "true");
            nextEntry.setAttribute("kind", "lib");
            nextEntry.setAttribute("path", nextPath);
            classpathRoot.appendChild(nextEntry);
        }

        return BWProjectUtils.saveXML(getLog(), "dotClasspathFile", doc, 7, true);
    }

}

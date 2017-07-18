package com.tibco.bw.maven.plugin.application;

import com.tibco.bw.maven.plugin.osgi.helpers.ManifestParser;
import com.tibco.bw.maven.plugin.osgi.helpers.ManifestWriter;
import com.tibco.bw.maven.plugin.utils.Constants;
import com.tibco.bw.maven.plugin.utils.BWProjectUtils;
import com.tibco.bw.maven.plugin.utils.JarUtil;
import org.apache.commons.io.FileUtils;
import org.apache.maven.archiver.MavenArchiveConfiguration;
import org.apache.maven.archiver.MavenArchiver;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.*;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.archiver.jar.JarArchiver;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.*;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

/**
 * For bwear
 * - update Bundle-Version in META-INF/MANIFEST.MF
 * - update modules/module elements to list bwmodule packages with classifiers bw-sharedmodule and bw-appmodule as
 * dependencies in META-INF/TIBCO.xml
 * <p>
 * - Gather up all of the needed modules and packaging constructs and build the ear..
 */
@Mojo(name = "bwear", defaultPhase = LifecyclePhase.PACKAGE, requiresDependencyResolution = ResolutionScope.COMPILE)
public class BWEARPackagerMojo extends AbstractMojo {

    public static final String BWEAR = "bwear";

    //Archive Configuration. This will set the Configuration for the Archive.
    private MavenArchiveConfiguration archiveConfiguration;
    //This is the actual JAR file which will be created in the EAR file.
    private JarArchiver jarchiver;
    //This will create the EAR file
    private MavenArchiver archiver;
    private List<File> tempFiles;
    @Parameter(property = "project.build.directory")
    private File outputDirectory;
    @Parameter(property = "project.basedir")
    private File projectBasedir;
    @Component
    private MavenSession session;
    @Parameter(defaultValue = "${project}", required = true)
    private MavenProject project;
    //This map is required for maintaining the module name vs is version which needs
    //to be updated in the TibcoXML at the later stage.
    private Map<String, String> sharedModuleVersionMap;
    private Map<String, String> appModuleVersionMap;
	private Map<String, String> osgiBundleModuleVersionMap;
    private ArrayList<File> filesToCleanup = new ArrayList<File>();

    /**
     * Execute Method.
     */
    public void execute() throws MojoExecutionException {
        try {
            getLog().info("BWEARPackager Mojo started ...");
            initialize();

            jarchiver = new JarArchiver();

            archiver = new MavenArchiver();

            archiveConfiguration = new MavenArchiveConfiguration();

            Manifest manifest = ManifestParser.parseManifest(projectBasedir);

            getLog().info("Adding Module Dependencies to the EAR file ");
            addDependentModules();

            getLog().info("Adding EAR Information to the EAR File.");
            addAdditionalEARSupportingFiles();

            getLog().info("BWEARPackager Mojo finished execution.");

        } catch (Exception e1) {
            throw new MojoExecutionException("Failed to create BW EAR Archive ", e1);
        } finally {
            cleanup();
        }

    }

    /**
     * Add the Application related files to the EAR.
     *
     * @throws Exception
     */
    private void addAdditionalEARSupportingFiles() throws Exception {

        getLog().debug("Adding Application specific files...");
        // Get the META-INF Folder for the Application Project
        File metainfFolder = getApplicationMetaInf();

        //Add the files from the META-INF to the EAR File.
        addFiletoEAR(metainfFolder);

        File earFile = getArchiveFileName();
        archiver.setArchiver(jarchiver);
        archiver.setOutputFile(earFile);
        archiveConfiguration.setAddMavenDescriptor(true);

        //Create the Archive.
        archiver.createArchive(session, project, archiveConfiguration);

        //Cleanup
        for (File nextFile : filesToCleanup) {
            if (nextFile.isDirectory()){
                try {
                    FileUtils.deleteDirectory(nextFile);
                }
                catch(Exception e){
                    Thread.sleep(100);
                    FileUtils.deleteDirectory(nextFile);
                }
            } else {
                FileUtils.forceDelete(nextFile);
            }

        }

        project.getArtifact().setFile(earFile);
    }

    /**
     * Adds the Modules included in the Application to the EAR file.
     * It will also maintain a Module vs Version map which will be used later by the Application
     * to populate the TIBCO.xml
     *
     * @throws Exception
     */
    private void addDependentModules() throws Exception {
        try {
            // store off unique version+timestamp for each artifact..
            initModuleVersionMap(project, true);

            getLog().debug("Adding Modules to the Application EAR");

            for (Artifact artifact : project.getArtifacts()) {
                //Find the Module JAR file
                File moduleJar = artifact.getFile();

                getLog().info("\nAdding Module to EAR: " + moduleJar.getName());

                String classifier = artifact.getClassifier();

                if ((classifier != null) && (classifier.equals(Constants.BW_SHAREDMODULE) || 
											classifier.equals(Constants.BW_APPMODULE) || 
											classifier.equals(Constants.OSGI_BUNDLE))) {
                    // update META-INF for each module to tag timestamp onto the Bundle-Version
                    String versionPlusTime = artifact.getVersion();
					if (classifier.equals(Constants.OSGI_BUNDLE)) {
                        versionPlusTime = osgiBundleModuleVersionMap.get(artifact.getArtifactId());
                    }else if (classifier.equals(Constants.BW_SHAREDMODULE)) {
                        versionPlusTime = sharedModuleVersionMap.get(artifact.getArtifactId());
                    } else {
                        versionPlusTime = appModuleVersionMap.get(artifact.getArtifactId());
                    }
                    moduleJar = updateBundleVersion(moduleJar, versionPlusTime);
                    filesToCleanup.add(moduleJar);
                }

                // Add the updated JAR file to the EAR file
                jarchiver.addFile(moduleJar, moduleJar.getName());
            }
        } catch (Exception e) {
            getLog().error("Failed to add modules to the Application");
            throw e;
        }
    }

    /**
     * open up the jar, update the Bundle-Version in the META-INF/MANIFEST.MF and re-write the jar returning the new File
     *
     * @param moduleJar
     * @param versionPlusTime
     * @return
     */
    private File updateBundleVersion(File moduleJar, String versionPlusTime) throws Exception {
        getLog().info("updateBundleVersion: started adding timestamp to Bundle-Version in jar manifest: " + versionPlusTime);
        String[] nameParts = moduleJar.getName().split("\\.jar");
        String newJarName = nameParts[0] + "." + versionPlusTime + ".jar";

        File extractedJarFolder = null;
        File newModule = null;

        try {
            // Extract the jar to dedicated folder target/[jar name]..
            extractedJarFolder = JarUtil.extractJarToTargetFolder(getLog(), projectBasedir.getAbsolutePath(),
                    moduleJar, newJarName);
        } catch (Exception e) {
            getLog().error("Failed to explode jar to folder for manifest update: " + moduleJar.getName(), e);
            throw new MojoFailureException("Failed to update manifest file for folder: " + moduleJar.getName(), e);
        }

        try {
            // update the manifest file in place..
            Manifest currentJarManifest = ManifestParser.parseManifest(extractedJarFolder);
            currentJarManifest.getMainAttributes().putValue("Bundle-Version", versionPlusTime);

            ManifestWriter.updateManifest(extractedJarFolder.getAbsolutePath() + File.separator + "META-INF", currentJarManifest);
        } catch (Exception e) {
            getLog().error("Failed to update manifest file for folder: " + extractedJarFolder.getAbsolutePath(), e);
            throw new MojoFailureException("Failed to update manifest file for folder: " + extractedJarFolder.getAbsolutePath(), e);
        }

        try {
            // repackage the jar; use JarUtil so that we're writing everything as-is to the zip....
            newModule = JarUtil.jarUpFolderContents(getLog(), extractedJarFolder.getAbsolutePath(), projectBasedir.getAbsolutePath() +
                    File.separator + "target" + File.separator + newJarName);
        } catch (Exception e) {
            getLog().error("Failed to re-jar after manifest update: " + extractedJarFolder.getAbsolutePath(), e);
            throw new MojoFailureException("Failed to re-zip after manifest update:" + extractedJarFolder.getAbsolutePath(), e);
        }

        filesToCleanup.add(extractedJarFolder);

        getLog().info("updateBundleVersion: finished adding timestamp to Bundle-Version in jar manifest: " + versionPlusTime);
        return newModule;
    }

    /**
     * Returns the Archive file name and location. The Archive file is created in the Target directory
     * with the name same as application project which is also the artifactId for the Application project.
     *
     * @return
     */

    private File getArchiveFileName() throws Exception {

        String archiveName = project.getArtifactId() + "_" + project.getVersion() + ".ear";
        File archiveFile = new File(outputDirectory, archiveName);

        getLog().debug("The EAR file name for Application is " + archiveFile.toString());

        return archiveFile;
    }

    /**
     * Adds the from the META-INF folder to the EAR file.
     * The META-INF folder needs to be copied as it is.
     *
     * @param metainf the META-INF folder location for the Application project.
     * @return the NANIFEST.MF file for the Application project.
     * @throws Exception
     */
    private void addFiletoEAR(File metainf) throws Exception {
        File tmpManifestFile = null;

        File[] fileList = metainf.listFiles();

        getLog().debug("Adding files to META-INF folder of EAR. ");

        assert fileList != null;
        for (int i = 0; i < fileList.length; i++) {
            String nextFileName = fileList[i].getName();

            // If the File is MANIFEST.MF then the mvn-version needs to be updated in the File
            // and added to the Archiver
            if (nextFileName.indexOf("MANIFEST") != -1) {
                // update for EAR, use timestamps instead of 'qualifier'
                tmpManifestFile = getUpdatedManifest(fileList[i], true);
                jarchiver.addFile(tmpManifestFile, "META-INF" + File.separator + nextFileName);
                // Set the MANIFEST.MF to the JAR Archiver
                jarchiver.setManifest(tmpManifestFile);
                // Set the MANIFEST.MF to the Archive Configuration
                archiveConfiguration.setManifestFile(tmpManifestFile);

                // update on disk, use 'qualifier'
                File manifestFile = getUpdatedManifest(fileList[i], false);
                getLog().info("Updating the META-INF/MANIFEST.MF on disk.. ");
                FileUtils.copyFile(manifestFile, new File(projectBasedir + File.separator + "META-INF" + File.separator + "MANIFEST.MF"));
            }

            // If the File is TIBCO.xml then each Module mvn-version needs to be updated in the File.
            else if (nextFileName.indexOf("TIBCO.xml") != -1) {
                // Update for EAR, use timestamps instead of 'qualifier'
                File tmpTibcoXML = getUpdatedTibcoXML(fileList[i]);
                jarchiver.addFile(tmpTibcoXML, "META-INF" + File.separator + nextFileName);

                // Update for project/src.. use 'qualifier'
                initModuleVersionMap(project, false);
                File tibcoXML = getUpdatedTibcoXML(fileList[i]);
                getLog().info("Updating the META-INF/TIBCO.xml on disk.. ");
                FileUtils.copyFile(tibcoXML, new File(projectBasedir + File.separator + "META-INF" + File.separator + "TIBCO.xml"));
            }

            // The substvar files needs to be added as it is.
            else if (nextFileName.indexOf(".substvar") != -1) {
                jarchiver.addFile(fileList[i], "META-INF" + File.separator + nextFileName);
            }

        }
    }

    /**
     * Updates the MANIFEST.MF with the Module mvn-version.
     *
     * @param manifest the MANIFEST.MF file
     * @return the updated MANIFEST.MF file
     * @throws Exception
     */
    private File getUpdatedManifest(File manifest, boolean useTimestamps) throws Exception {
        //Copy the MANIFEST.MF to a temporary location.
        File tempManifest = File.createTempFile(BWEAR, "mf");
        FileUtils.copyFile(manifest, tempManifest);

        FileInputStream is = new FileInputStream(tempManifest);
        Manifest mf = new Manifest(new FileInputStream(tempManifest));
        is.close();

        // Update the Bundle with mvn-version
        Attributes attr = mf.getMainAttributes();
        String version = BWProjectUtils.convertMvnVersionToOSGI(getLog(), project.getVersion(), true, useTimestamps);
        attr.putValue("Bundle-Version", version);

        getLog().debug("Manifest updated with Version " + version);

        //Write the updated file and return the same.
        FileOutputStream os = new FileOutputStream(tempManifest);
        mf.write(os);
        os.close();

        tempFiles.add(tempManifest);

        getLog().debug("manifest added to temp location at " + tempManifest.toString());

        return tempManifest;
    }

    /**
     * Finds the folder name META-INF inside the Application Project.
     *
     * @return the META-INF folder
     * @throws Exception
     */
    private File getApplicationMetaInf() {
        File[] fileList = projectBasedir.listFiles(new FileFilter() {

            public boolean accept(File pathname) {
                return pathname.getName().contains("META-INF");
            }
        });

        return fileList[0];

    }

    private void initialize() {
        tempFiles = new ArrayList<File>();
    }

    /**
     * Gets the Tibco XML file with the updated Module versions.
     *
     * @param tibcoxML the Application Project TIBCO.xml file
     * @return the updated TIBCO.xml file.
     * @throws Exception
     */
    private File getUpdatedTibcoXML(File tibcoxML) throws Exception {
        getLog().debug("Updating the TibcoXML file with the module versions ");
        Document doc = BWProjectUtils.loadXML(getLog(), tibcoxML);
        doc = updateTibcoXMLVersion(doc);
        File tmpFile = BWProjectUtils.saveXML(getLog(), BWEAR, doc, 2, false);
        tempFiles.add(tmpFile);
        return tmpFile;
    }

    /**
     * @throws Exception
     */
    private void initModuleVersionMap(MavenProject project, boolean useTimestamps) throws Exception {

        appModuleVersionMap = new HashMap<String, String>();
        sharedModuleVersionMap = new HashMap<String, String>();
		osgiBundleModuleVersionMap = new HashMap<String, String>();

        Iterator<Artifact> artifacts = project.getArtifacts().iterator();

        while (artifacts.hasNext()) {
            Artifact artifact = artifacts.next();
            String classifier = BWProjectUtils.getClassifierOrEmptyString(artifact);

            String moduleVersion = artifact.getVersion();
            getLog().debug("BWEARPackager:    moduleVersion pre-osgi-conversion - artifact id/version: " +
                    artifact.getArtifactId() + "/" + moduleVersion);

            if (Constants.BW_APPMODULE.equals(classifier) || 
					Constants.BW_SHAREDMODULE.equals(classifier) ||
					Constants.OSGI_BUNDLE.equals(classifier)) {
                moduleVersion = BWProjectUtils.convertMvnVersionToOSGI(getLog(), artifact.getVersion(), true, useTimestamps);
            }

            getLog().debug("BWEARPackager:    Adding to moduleVersionMap - artifact id/version: " +
                    artifact.getArtifactId() + "/" + moduleVersion);

            String artifactName = artifact.getFile().getName();

            if (artifact.getFile() != null) {
                if (classifier.equals(Constants.BW_SHAREDMODULE)) {
                    getLog().debug("BWEARPackager:    Adding to sharedModuleVersionMap - artifact id/version: " +
                            artifact.getArtifactId() + "/" + moduleVersion);
                    sharedModuleVersionMap.put(artifact.getArtifactId(), moduleVersion);
                } else if (classifier.equals(Constants.BW_APPMODULE)) {
                    getLog().debug("BWEARPackager:    Adding to appModuleVersionMap - artifact id/version: " +
                            artifact.getArtifactId() + "/" + moduleVersion);
                    appModuleVersionMap.put(artifact.getArtifactId(), moduleVersion);
                } else if (classifier.equals(Constants.OSGI_BUNDLE)) {
                    getLog().debug("BWEARPackager:    Adding to osgiBundleModuleVersionMap - artifact id/version: " +
                            artifact.getArtifactId() + "/" + moduleVersion);
                    osgiBundleModuleVersionMap.put(artifact.getArtifactId(), moduleVersion);
                }
            }
        }
        if (appModuleVersionMap.entrySet().size() > 1) {
            throw new MojoFailureException("bwear only allows 1 dependency with classifier=bw-appmodule");
        }
    }

    /**
     * Updates the document with the Module mvn-version for the Modules.
     *
     * @param doc the Root document object.
     * @return the Document updated with the Module mvn-versions.
     * @throws Exception
     */
    private Document updateTibcoXMLVersion(Document doc) {
        // The modules are listed under the Modules tag with name as "module"
        NodeList modulesList = doc.getElementsByTagNameNS("http://schemas.tibco.com/tra/model/core/PackagingModel", "modules");
        NodeList moduleList = doc.getElementsByTagNameNS("http://schemas.tibco.com/tra/model/core/PackagingModel", "module");

        Element modulesElement = (Element) modulesList.item(0);

        // clear out existing modules
        while (modulesElement.hasChildNodes())
            modulesElement.removeChild(modulesElement.getFirstChild());

        // add in bw-appmodule entry
        addModule(doc, modulesElement, appModuleVersionMap, Constants.BW_APPMODULE);

        // add in bw-sharedmodule entries..
        addModule(doc, modulesElement, sharedModuleVersionMap, Constants.BW_SHAREDMODULE);

		// add in osgi-bundle entries..
        addModule(doc, modulesElement, osgiBundleModuleVersionMap, Constants.OSGI_BUNDLE);
		
        getLog().debug("Updated Module versions in the Tibcoxml file");

        return doc;

    }

    private void addModule(Document doc, Element modulesElement, Map<String, String> moduleMap, String moduleType) {
        // add an entry for each specified by maven..
        for (Map.Entry<String, String> nextEntry : moduleMap.entrySet()) {
            Element newModule = doc.createElement("packaging:module");

            Element symbolicName = doc.createElement("packaging:symbolicName");
            symbolicName.setTextContent(nextEntry.getKey());
            newModule.appendChild(symbolicName);

            Element technologyType = doc.createElement("packaging:technologyType");
			if (moduleType.equals(Constants.OSGI_BUNDLE)) {
				technologyType.setTextContent("osgi-bundle");
			} else {
				technologyType.setTextContent("osgi-bundle," + moduleType);
			}
            newModule.appendChild(technologyType);

            Element technologyVersion = doc.createElement("packaging:technologyVersion");
            technologyVersion.setTextContent(nextEntry.getValue());
            newModule.appendChild(technologyVersion);

            modulesElement.appendChild(newModule);
            getLog().debug("Added 'module' element: " + moduleType + " " + nextEntry.getKey() + " " + nextEntry.getValue());
        }
    }


    /**
     * Clean the updated MANIFEST.MF and TIBCO.xml files
     */
    private void cleanup() {
        for (File file : tempFiles) {
            file.delete();
        }

        getLog().debug("cleaned up the temporary files. ");
    }
}

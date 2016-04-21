package com.tibco.bw.maven.plugin.application;

import com.tibco.bw.maven.plugin.osgi.helpers.ManifestParser;
import com.tibco.bw.maven.plugin.utils.BWProjectUtils;
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

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
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

            initModuleVersionMap(project);

            getLog().info("Adding Modules to the EAR file ");
            addModules();

            getLog().info("Adding EAR Information to the EAR File.");
            addApplication();

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
    private void addApplication() throws Exception {

        getLog().debug("Adding Application specific files...");
        // Get the META-INF Folder for the Application Project
        File metainfFolder = getApplicationMetaInf();

        //Add the files from the META-INF to the EAR File.
        File appManifest = addFiletoEAR(metainfFolder);

        File earFile = getArchiveFileName();
        archiver.setArchiver(jarchiver);

        archiver.setOutputFile(earFile);

        // Set the MANIFEST.MF to the JAR Archiver
        jarchiver.setManifest(appManifest);

        // Set the MANIFEST.MF to the Archive Configuration
        archiveConfiguration.setManifestFile(appManifest);

        archiveConfiguration.setAddMavenDescriptor(true);

        //Create the Archive.
        archiver.createArchive(session, project, archiveConfiguration);

        project.getArtifact().setFile(earFile);
    }

    /**
     * Adds the Modules included in the Application to the EAR file.
     * It will also maintain a Module vs Version map which will be used later by the Application
     * to populate the TIBCO.xml
     *
     * @throws Exception
     */
    private void addModules() throws Exception {
        try {
            getLog().debug("Adding Modules to the Application EAR");

            for (Artifact artifact : project.getArtifacts()) {
                //Find the Module JAR file
                File moduleJar = artifact.getFile();

                getLog().info("Adding Module to EAR: " + moduleJar.getName());

                //Add the JAR file to the EAR file
                jarchiver.addFile(moduleJar, moduleJar.getName());
            }
        } catch (Exception e) {
            getLog().error("Failed to add modules to the Application");
            throw e;
        }
    }

    /**
     * Returns the Archive file name and location. The Archive file is created in the Target directory
     * with the name same as application project which is also the artifactId for the Application project.
     *
     * @return
     */
    private File getArchiveFileName() throws Exception {
        String fullVersion = BWProjectUtils.convertMvnVersionToOSGI(project.getVersion(), true);

        String archiveName = project.getArtifactId() + "_" + fullVersion + ".ear";
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
    private File addFiletoEAR(File metainf) throws Exception {
        File manifestFile = null;

        File[] fileList = metainf.listFiles();

        getLog().debug("Adding files to META-INF folder of EAR. ");

        assert fileList != null;
        for (int i = 0; i < fileList.length; i++) {
            String nextFileName = fileList[i].getName();

            // If the File is MANIFEST.MF then the mvn-version needs to be updated in the File
            // and added to the Archiver
            if (nextFileName.indexOf("MANIFEST") != -1) {
                manifestFile = getUpdatedManifest(fileList[i]);
                jarchiver.addFile(manifestFile, "META-INF" + File.separator + nextFileName);
                getLog().info("Updating the META-INF/MANIFEST.MF on disk.. ");
                FileUtils.copyFile(manifestFile, new File(projectBasedir + File.separator + "META-INF" + File.separator + "MANIFEST.MF"));
            }

            // If the File is TIBCO.xml then the each Module mvn-version needs to be updated in the File.
            else if (nextFileName.indexOf("TIBCO.xml") != -1) {
                File tibcoXML = getUpdatedTibcoXML(fileList[i]);
                jarchiver.addFile(tibcoXML, "META-INF" + File.separator + nextFileName);
                getLog().info("Updating the META-INF/TIBCO.xml on disk.. ");
                FileUtils.copyFile(tibcoXML, new File(projectBasedir + File.separator + "META-INF" + File.separator + "TIBCO.xml"));
            }

            // The substvar files needs to be added as it is.
            else if (nextFileName.indexOf(".substvar") != -1) {
                jarchiver.addFile(fileList[i], "META-INF" + File.separator + nextFileName);
            }

        }


        return manifestFile;
    }

    /**
     * Updates the MANIFEST.MF with the Module mvn-version.
     *
     * @param manifest the MANIFEST.MF file
     * @return the updated MANIFEST.MF file
     * @throws Exception
     */
    private File getUpdatedManifest(File manifest) throws Exception {
        //Copy the MANIFEST.MF to a temporary location.
        File tempManifest = File.createTempFile(BWEAR, "mf");
        FileUtils.copyFile(manifest, tempManifest);

        FileInputStream is = new FileInputStream(tempManifest);
        Manifest mf = new Manifest(new FileInputStream(tempManifest));
        is.close();

        // Update the Bundle with mvn-version
        Attributes attr = mf.getMainAttributes();
        String version = BWProjectUtils.convertMvnVersionToOSGI(project.getVersion(), true);
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
        Document doc = loadTibcoXML(tibcoxML);
        doc = updateTibcoXMLVersion(doc);
        return saveTibcoXML(doc);
    }

    /**
     * Loads the TibcoXMLfile in a Document object (DOM)
     *
     * @param file the TIBCO.xml file
     * @return the root Document object for the TIBCO.xml file
     * @throws Exception
     */
    private Document loadTibcoXML(File file) throws Exception {

        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        docFactory.setNamespaceAware(true);

        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        Document doc = docBuilder.parse(file);

        getLog().debug("Loaded Tibco.xml file");
        return doc;

    }

    /**
     * @throws Exception
     */
    private void initModuleVersionMap(MavenProject project) throws Exception {

        appModuleVersionMap = new HashMap<String, String>();
        sharedModuleVersionMap = new HashMap<String, String>();

        Iterator<Artifact> artifacts = project.getArtifacts().iterator();

        while (artifacts.hasNext()) {
            Artifact artifact = artifacts.next();

            String convertedVersion = BWProjectUtils.convertMvnVersionToOSGI(artifact.getVersion(), true);

            getLog().debug("TibcoStudioMojo:    Adding to moduleVersionMap - artifact id/version: " +
                    artifact.getArtifactId() + "/" + convertedVersion);

            String artifactName = artifact.getFile().getName();
            String classifier = BWProjectUtils.getClassifierOrEmptyString(artifact);
            if (artifact.getFile() != null) {
                if (classifier.equals(BWProjectUtils.BW_SHAREDMODULE)) {
                    getLog().debug("TibcoStudioMojo:    Adding to sharedModuleVersionMap - artifact id/version: " +
                            artifact.getArtifactId() + "/" + convertedVersion);
                    sharedModuleVersionMap.put(artifact.getArtifactId(), convertedVersion);
                } else if (classifier.equals(BWProjectUtils.BW_APPMODULE)) {
                    getLog().debug("TibcoStudioMojo:    Adding to appModuleVersionMap - artifact id/version: " +
                            artifact.getArtifactId() + "/" + convertedVersion);
                    appModuleVersionMap.put(artifact.getArtifactId(), convertedVersion);
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
        addModule(doc, modulesElement, appModuleVersionMap, BWProjectUtils.BW_APPMODULE);

        // add in bw-sharedmodule entries..
        addModule(doc, modulesElement, sharedModuleVersionMap, BWProjectUtils.BW_SHAREDMODULE);

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
            technologyType.setTextContent("osgi-bundle," + moduleType);
            newModule.appendChild(technologyType);

            Element technologyVersion = doc.createElement("packaging:technologyVersion");
            technologyVersion.setTextContent(nextEntry.getValue());
            newModule.appendChild(technologyVersion);

            modulesElement.appendChild(newModule);
            getLog().debug("Added 'module' element: " + moduleType + " " + nextEntry.getKey() + " " + nextEntry.getValue());
        }
    }

    /**
     * Save the TibcoXML file to a temporary file with the new changes.
     *
     * @param doc the root Document
     * @return the updated TIBCO.xml file location
     * @throws Exception
     */
    private File saveTibcoXML(Document doc) throws Exception {
        File tempXml = File.createTempFile(BWEAR, "xml");
        doc.getDocumentElement().normalize();

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();

        DOMSource source = new DOMSource(doc);

        StreamResult result = new StreamResult(tempXml);
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.transform(source, result);
        tempFiles.add(tempXml);

        getLog().debug("Updated TibcoXML file to temp location " + tempXml.toString());

        return tempXml;
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

package com.tibco.bw.maven.plugin.application;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.io.FileUtils;
import org.apache.maven.archiver.MavenArchiveConfiguration;
import org.apache.maven.archiver.MavenArchiver;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.archiver.jar.JarArchiver;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.tibco.bw.maven.plugin.osgi.helpers.ManifestParser;
import com.tibco.bw.maven.plugin.osgi.helpers.Version;
import com.tibco.bw.maven.plugin.osgi.helpers.VersionParser;
import com.tibco.bw.maven.plugin.utils.BWModulesParser;
import com.tibco.bw.maven.plugin.utils.BWProjectUtils;
import com.tibco.bw.maven.plugin.utils.Constants;

@Mojo(name = "bwear", defaultPhase = LifecyclePhase.PACKAGE)
public class BWEARPackagerMojo extends AbstractMojo {
	@Parameter(property="project.build.directory")
    private File outputDirectory;

	@Parameter(property="project.basedir")
	private File projectBasedir;

    @Component
    private MavenSession session;

    @Component
    private MavenProject project;

    private List<File> tempFiles;

    private Manifest manifest;

    //This is the actual JAR file which will be created in the EAR file.
    JarArchiver jarchiver;

    //This will create the EAR file
    MavenArchiver archiver;

    //Archive Configuration. This will set the Configuration for the Archive.
    protected MavenArchiveConfiguration archiveConfiguration;

    //This map is required for maintaining the module name vs module version which needs to be updated in the TibcoXML at a later stage.    
    Map<String, String> moduleVersionMap;

    //The version to be updated in the Application Manifest. 
    String version;

    /**
     * Execute Method.
     * 
     */
    public void execute() throws MojoExecutionException {
    	try {
    		getLog().info("BWEARPackager Mojo started ...");
    	    tempFiles = new ArrayList<File>();
    	    jarchiver = new JarArchiver();
    	    archiver = new MavenArchiver();
    	    archiveConfiguration = new MavenArchiveConfiguration();
    	    moduleVersionMap = new HashMap<String, String>();
            manifest = ManifestParser.parseManifest(projectBasedir);
    	    getLog().info("Adding Modules to the EAR file");
    		addModules();
    		getLog().info("Adding EAR Information to the EAR File");
    		addApplication();
    		cleanup();
    		getLog().info("BWEARPackager Mojo finished execution");
		} catch (Exception e1) {
			throw new MojoExecutionException("Failed to create BW EAR Archive ", e1);
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

		//Move Archive
		//TODO: Need to move the Archive to the User defined location.
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
        	// The first artifact is an Application Module
        	boolean isAppModuleArtifact = true;
        	BWModulesParser parser = new BWModulesParser(session, project);
        	String bwEdition = manifest.getMainAttributes().getValue(Constants.TIBCO_BW_EDITION);
        	parser.bwEdition = bwEdition;
        	List<Artifact> artifacts = parser.getModulesSet();

            for(Artifact artifact : artifacts) {
                //Find the Module JAR file
                File moduleJar = artifact.getFile();

                //Add the JAR file to the EAR file
                jarchiver.addFile(moduleJar, moduleJar.getName());
                String version = BWProjectUtils.getModuleVersion(moduleJar);
                getLog().debug("Adding Module JAR with name " + moduleJar.getName() + " with version " + version);

                //Save the module version in the Version Map.
                moduleVersionMap.put(artifact.getArtifactId(), version);
                if(isAppModuleArtifact) {
                	this.version = version;
                	isAppModuleArtifact = false;
                }
            }
    	} catch(Exception e) {
    		getLog().error("Failed to add modules to the Application");
    		throw e;
    	}
    }

	/**
	 * Returns the Archive file name and location. The Archive file is created in the Target directory 
	 * with the name same as application project which is also the artifactId for the Application project. 
	 * @return
	 */
	private File getArchiveFileName() {
		Version version = VersionParser.parseVersion(manifest.getMainAttributes().getValue(Constants.BUNDLE_VERSION));
		String fullVersion = version.getMajor() + "." + version.getMinor() + "." + version.getMicro();

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
     * 
     * @return the NANIFEST.MF file for the Application project.
     * 
     * @throws Exception
     */
	private File addFiletoEAR(File metainf) throws Exception {
		File manifestFile = null;
	    File [] fileList = metainf.listFiles();
	    getLog().debug("Adding files to META-INF folder of EAR. ");
	    for(int i = 0; i < fileList.length; i++) {
	    	if(fileList[i].getName().indexOf("MANIFEST") != -1) { // If the File is MANIFEST.MF then the Version needs to be updated in the File and added to the Archiver
    		   manifestFile = getUpdatedManifest (fileList[i]);
    		   jarchiver.addFile(manifestFile, "META-INF/" + fileList[i].getName());
    	    } else if(fileList[i].getName().indexOf("TIBCO.xml") != -1) { // If the File is TIBCO.xml then the each Module Version needs to be updated in the File.
    	    	File tibcoXML = getUpdatedTibcoXML(fileList[i]);
    	    	jarchiver.addFile(tibcoXML, "META-INF/" + fileList[i].getName());
    	    } else if(fileList[i].getName().indexOf(".substvar") != -1) { // The substvar files need to be added as it is.
    	    	jarchiver.addFile(fileList[i], "META-INF/" + fileList[i].getName());
    	    } else { // The rest of the files can be ignored.
    	    	continue;
    	    }
	    }
	    return manifestFile;
	}

	/**
	 * Updates the MANIFEST.MF with the Module Version number.
	 * 
	 * @param manifest the MANIFEST.MF file
	 * 
	 * @return the updated MANIFEST.MF file
	 * 
	 * @throws Exception
	 */
	private File getUpdatedManifest(File manifest) throws Exception {
		//Copy the MANIFEST.MF to a temporary location.
		File tempManifest = File.createTempFile("bwear", "mf");
		FileUtils.copyFile(manifest, tempManifest);

		FileInputStream is = new FileInputStream(tempManifest);
		Manifest mf = new Manifest(new FileInputStream(tempManifest));
		is.close();

		// Update the Bundle Version
		Attributes attr = mf.getMainAttributes();
		attr.putValue(Constants.BUNDLE_VERSION, version);
		getLog().debug("Manifest updated with Version " + version);

		//Write the updated file and return the same.
		FileOutputStream os = new FileOutputStream(tempManifest);
		mf.write(os);
		os.close();

		tempFiles.add(tempManifest);
		getLog().debug("Manifest added to temp location at " + tempManifest.toString());
		return tempManifest;
	}

    /**
     * Finds the folder name META-INF inside the Application Project.
     * 
     * @return the META-INF folder
     * 
     * @throws Exception
     */
	private File getApplicationMetaInf() throws Exception {
		File[] fileList = projectBasedir.listFiles(new FileFilter() {
			public boolean accept(File pathname) {
				if(pathname.getName().indexOf("META-INF") != -1) {
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
//	private File getModuleJar(File target) throws Exception {
//      File[] files = BWFileUtils.getFilesForType(target, ".jar");
//      files = BWFileUtils.sortFilesByDateDesc(files);
//      if(files.length == 0) {
//       	throw new Exception("Module is not built yet. Please check your Application PO for the Module entry.");
//      }
//      return files[0];
//	}

	/**
	 * Gets the Tibco XML file with the updated Module versions.
	 * 
	 * @param tibcoxML the Application Project TIBCO.xml file
	 *  
	 * @return the updated TIBCO.xml file.
	 * 
	 * @throws Exception
	 */
	private File getUpdatedTibcoXML(File tibcoxML) throws Exception {
		getLog().debug("Updating the TibcoXML file with the module versions ");
		Document doc = loadTibcoXML(tibcoxML);
		doc = updateTibcoXMLVersion(doc);
		File file = saveTibcoXML(doc);
		return file;
	}

	/**
	 * Loads the TibcoXMLfile in a Document object (DOM)
	 * 
	 * @param file the TIBCO.xml file
	 * 
	 * @return the root Document object for the TIBCO.xml file
	 * 
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
	 * Updates the document with the Module Version for the Modules.
	 * 
	 * @param doc the Root document object.
	 * 
	 * @return the Document updated with the Module versions.
	 * 
	 * @throws Exception
	 */
	private Document updateTibcoXMLVersion(Document doc) throws Exception	{
		// The modules are listed under the Modules tag with name as "module"
		NodeList nList = doc.getElementsByTagNameNS(Constants.PACKAGING_MODEL_NAMESPACE_URI, Constants.MODULE);
		for(int i = 0; i < nList.getLength(); i++) {
			Element node = (Element)nList.item(i);
			// The Symbolic name is the Module name. The version for this needs to be updated under the tag technologyVersion 
			NodeList childList = node.getElementsByTagNameNS(Constants.PACKAGING_MODEL_NAMESPACE_URI, Constants.SYMBOLIC_NAME);
			String module = childList.item(0).getTextContent();		
			NodeList technologyVersionList = node.getElementsByTagNameNS(Constants.PACKAGING_MODEL_NAMESPACE_URI, Constants.TECHNOLOGY_VERSION);
			Node technologyVersion = technologyVersionList.item(0);
			//Get the version from the Module from the Map and set it in the Document. 
			technologyVersion.setTextContent(moduleVersionMap.get(module));
		}
		getLog().debug("Updated Module versions in the Tibcoxml file");
		return doc;
	}
	
	/**
	 * Save the TibcoXML file to a temporary file with the new changes.
	 * 
	 * @param doc the root Document
	 * 
	 * @return the updated TIBCO.xml file location
	 * 
	 * @throws Exception
	 */
	private File saveTibcoXML(Document doc) throws Exception {
		File tempXml = File.createTempFile("bwear", "xml");
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
		for(File file : tempFiles) {
			file.delete();
		}
		getLog().debug("cleaned up the temporary files.");
    }
}

package com.tibco.bw.maven.plugin.application;



import java.io.File;
import java.io.FileFilter;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.tibco.bw.maven.plugin.utils.FileUtilsProject;

/**
 * Display bwimport information on bw6-maven-plugin-resource.      
 * Call mvn com.tibco.plugins:bw6-maven-plugin-resource:bwimport -Dprofile=ProfileNameFile -Dpropertyfile=propertyFile
 * @phase N/A
 */
@Mojo(name = "bwimport" ,defaultPhase = LifecyclePhase.INSTALL)
public class BWEARResourceImportMojo extends AbstractMojo {

    @Component
    private MavenSession session;

    @Component
    private MavenProject project;
	
	@Parameter(property="project.build.directory")
    private File outputDirectory;

	@Parameter(property="project.basedir")
	private File projectBasedir;

    
	@Parameter(property="profile",required=true)
	private String profile;
	
	@Parameter(property="propertyfile",required=true)
	private String propertyfile;

    

    /**
     * Execute Method.
     * 
     */
    public void execute() throws MojoExecutionException {
    	try {

    		
    		 getLog().info("bwresourceImport Mojo started execution");
    		 getLog().info("Loading property file -->" + propertyfile);
    		 getLog().info("update  profile -->" + profile);
    		 
    		 
    		 
    		 if (com.tibco.bw.maven.plugin.utils.FileUtilsProject.getApplicationMetaInf(projectBasedir)!=null)
    		 {
    		 
				for (File file : getApplicationMetaInf().listFiles()) {

					if (file.getName().equals(profile.toString())) {
						getLog().info("backup file " + file.getName() + " to " + file.getName() + ".backup");
						FileUtilsProject.copyFile(file, new File(file.getAbsolutePath() + ".backup"));
						Properties prop = FileUtilsProject.LoadProperties(
								FileUtilsProject.getApplicationMSrcResources(projectBasedir).getAbsolutePath() + "/"
										+ propertyfile);
						// Properties prop =
						// FileUtilsProject.LoadProperties(projectBasedir+"/"+propertyfile);
						getLog().info("Properties loaded : " + propertyfile);

						DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
						DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
						Document doc = docBuilder.parse(file);

						NodeList globalVariableList = doc.getElementsByTagName("globalVariable");

						for (int i = 0; i < globalVariableList.getLength(); ++i) {

							Element node = (Element) globalVariableList.item(i);

							FileUtilsProject.setvalueNode(node, prop);

						}

						FileUtilsProject.setUpdatePropertyXML(file, doc);

						getLog().info("Done " + profile + " updating.");
					}

				}
    		 
    		 }
    		 else
    		 {
    			 getLog().info("bwresourceImport: Skip Import properties");
    		 }
    		
    		 getLog().info("bwresourceImport Mojo finished execution");
		} catch (Exception e1) {
			throw new MojoExecutionException("Failed to Import BW property file ", e1);
		}
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

	private File getResourceProperties() throws Exception {
		projectBasedir.getParent();

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
	



}

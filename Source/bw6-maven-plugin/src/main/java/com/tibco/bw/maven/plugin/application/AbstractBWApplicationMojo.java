package com.tibco.bw.maven.plugin.application;

import org.apache.maven.plugin.AbstractMojo;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by tcosley on 4/14/2016.
 */
public abstract class AbstractBWApplicationMojo extends AbstractMojo {

	//This map is required for maintaining the module name vs is version which needs
	//to be updated in the TibcoXML at the later stage.
	Map<String, String> moduleVersionMap;

	List<File> tempFiles;


	protected void initialize(){
		tempFiles = new ArrayList<File>();
	}
	/**
	 * Gets the Tibco XML file with the updated Module versions.
	 *
	 * @param tibcoxML the Application Project TIBCO.xml file
	 *
	 * @return the updated TIBCO.xml file.
	 *
	 * @throws Exception
	 */
	protected File getUpdatedTibcoXML(File tibcoxML) throws Exception
	{
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
	private Document loadTibcoXML(File file) throws Exception
	{

		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		docFactory.setNamespaceAware(true);

		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		Document doc = docBuilder.parse(file );

		getLog().debug( "Loaded Tibco.xml file");
		return doc;

	}

	/**
	 * Updates the document with the Module mvn-ersion for the Modules.
	 *
	 * @param doc the Root document object.
	 *
	 * @return the Document updated with the Module mvn-versions.
	 *
	 * @throws Exception
	 */
	private Document updateTibcoXMLVersion( Document doc ) throws Exception
	{
		// The modules are listed under the Modules tag with name as "module"
		NodeList nList = doc.getElementsByTagNameNS("http://schemas.tibco.com/tra/model/core/PackagingModel" , "module");

		for( int i = 0 ; i < nList.getLength(); i++ )
		{

			Element node = (Element)nList.item(i);

			// The Symbolic name is the Module name. The version for this needs to be updated under the tag technologyVersion
			NodeList childList = node.getElementsByTagNameNS("http://schemas.tibco.com/tra/model/core/PackagingModel", "symbolicName");
			String module = childList.item(0).getTextContent();

			NodeList technologyVersionList = node.getElementsByTagNameNS("http://schemas.tibco.com/tra/model/core/PackagingModel", "technologyVersion");
			Node technologyVersion = technologyVersionList.item(0);

			//Get the version from the Module from the Map and set it in the Document.
			technologyVersion.setTextContent(  moduleVersionMap.get( module) );

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
	private File saveTibcoXML( Document doc ) throws Exception
	{
		File tempXml = File.createTempFile("bwear", "xml");
		doc.getDocumentElement().normalize();

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();

        DOMSource source = new DOMSource(doc);

        StreamResult result = new StreamResult( tempXml );
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.transform(source, result);
        tempFiles.add(tempXml);

        getLog().debug( "Updated TibcoXML file to temp location " + tempXml.toString() );

        return tempXml;
	}

	/**
	 * Clean the updated MANIFEST.MF and TIBCO.xml files
	 */
	protected void cleanup()
	{
		for( File file : tempFiles )
		{
			file.delete();
		}

		getLog().debug( "cleaned up the temporary files. " );
	}
}

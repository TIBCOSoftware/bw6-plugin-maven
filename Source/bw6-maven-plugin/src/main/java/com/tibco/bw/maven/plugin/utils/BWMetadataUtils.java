package com.tibco.bw.maven.plugin.utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.List;
import java.util.jar.Manifest;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.project.MavenProject;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.tibco.bw.maven.plugin.osgi.helpers.ManifestParser;
import com.tibco.bw.maven.plugin.utils.BWProjectUtils.MODULE;

public class BWMetadataUtils {

	public static MavenProject updateManifest(MavenSession session) {

		List<MavenProject> projects = session.getAllProjects();
		for (MavenProject project : projects) {
			Manifest mf = ManifestParser.parseManifest(project.getBasedir());
			MODULE module = BWProjectUtils.getModuleType(mf);
			if (module == MODULE.SHAREDMODULE) {
				mf.getMainAttributes().putValue("TIBCO-BW-SharedModule-METADATA", "METADATA.xml");
				updateManifest(project.getBasedir(), mf);
				File metadataFile = new File(project.getBasedir(), "METADATA.xml");
				try {
					String symbName = mf.getMainAttributes().getValue("Bundle-SymbolicName");
					createMetadatFile(metadataFile, symbName);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	public static void updateManifest(File basedir, Manifest mf) {
		File manifest = new File(basedir, "META-INF/MANIFEST.MF");
		BufferedOutputStream os = null;
		try {
			os = new BufferedOutputStream(new FileOutputStream(manifest));
			mf.write(os);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				os.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static void createMetadatFile(File file, String symboicName) throws Exception {
		Document doc = null;
		doc = createMetadataFile(symboicName);
		saveMetadataXML(doc, file);

	}

	private static Document createMetadataFile(String smName) {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		try {
			builder = factory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Document doc = builder.newDocument();
		org.w3c.dom.Element packagingUnit = doc.createElement("packaging:packageUnit");
		packagingUnit.setAttribute("xmlns:packaging", Constants.PACKAGING_MODEL_NAMESPACE_URI);
		packagingUnit.setAttribute("xmlns:xsd", "http://www.w3.org/2001/XMLSchema");
		
		Element pkgProperties = doc.createElement("packaging:properties");
		packagingUnit.appendChild(pkgProperties);
		
		org.w3c.dom.Element packagingModules = doc.createElement("packaging:modules");

		org.w3c.dom.Element packagingModule = doc.createElement("packaging:module");

		org.w3c.dom.Element symbolicName = doc.createElement("packaging:symbolicName");
		symbolicName.appendChild(doc.createTextNode(smName));
		packagingModule.appendChild(symbolicName);

		org.w3c.dom.Element techType = doc.createElement("packaging:technologyType");
		String nodeValue = "bw-sharedmodule,osgi-bundle";
		techType.appendChild(doc.createTextNode(nodeValue));
		packagingModule.appendChild(techType);

		org.w3c.dom.Element techVersion = doc.createElement("packaging:technologyVersion");
		techVersion.appendChild(doc.createTextNode("1.0.0.qualifier"));
		packagingModule.appendChild(techVersion);

		packagingModules.appendChild(packagingModule);
		packagingUnit.appendChild(packagingModules);
		doc.appendChild(packagingUnit);

		return doc;
	}

	public static Document updateMetadataXML(File file, String moduleName) throws Exception {
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		docFactory.setNamespaceAware(true);
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		Document doc = docBuilder.parse(file);

		NodeList nList = doc.getElementsByTagNameNS(Constants.PACKAGING_MODEL_NAMESPACE_URI, Constants.MODULE);
		for (int i = 0; i < nList.getLength(); i++) {
			Element node = (Element) nList.item(i);
			// The Symbolic name is the Module name. The version for this needs to be
			// updated under the tag technologyVersion
			NodeList childList = node.getElementsByTagNameNS(Constants.PACKAGING_MODEL_NAMESPACE_URI,
					Constants.SYMBOLIC_NAME);
			childList.item(0).setTextContent(moduleName);
		}
		return doc;
	}

	private static void saveMetadataXML(Document doc, File file) throws Exception {
		doc.getDocumentElement().normalize();
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();

		// Set indentation properties
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

		DOMSource source = new DOMSource(doc);
		StreamResult result = new StreamResult(file);
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.transform(source, result);
	}

}

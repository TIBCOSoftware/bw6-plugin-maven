package com.tibco.bw.maven.plugin.utils;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;

public class BWProjectUtils {

    public static String getModuleVersion(File jarFile) throws Exception {
        JarInputStream jarStream = new JarInputStream(new FileInputStream(jarFile));
        Manifest moduleManifest = jarStream.getManifest();
        jarStream.close();

        return moduleManifest.getMainAttributes().getValue(Constants.BUNDLE_VERSION);
    }

    public static String convertMvnVersionToOSGI(Log log, String mvnVersion, boolean includeQualifier) throws Exception {
        return convertMvnVersionToOSGI(log, mvnVersion, includeQualifier, false);
    }

    public static String convertMvnVersionToOSGI(Log log, String mvnVersion, boolean includeQualifier, boolean useTimestamps) throws Exception {
        String qualifier = Constants.QUALIFIER;
        if (useTimestamps) {
            qualifier = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
        }
        String convertedVersion = "1.0.0.qualifier";

        String[] parts = mvnVersion.replaceAll("-", ".").replaceAll("_", ".").split("\\.");
        // may have '1.0.0-SNAPSHOT' or other things like '1.0.0-Feature1-SNAPSHOT' or '1.0.1-20160422.203047-2'
        // use the first three and tag on 'qualifier' (OSGI term for SNAPSHOT)
        if (parts.length > 3) {
            String parts3 = parts[3];
            log.debug("convertMvnVersionToOSGI: third part: |" + parts3 + "|");
            if ((parts3.equals(Constants.SNAPSHOT) || mvnVersion.endsWith(Constants.SNAPSHOT) || parts3.matches("\\d+")) && includeQualifier) {
                parts3 = Constants.QUALIFIER; // 'qualifier' means SNAPSHOT according to Tycho conventions for OSGI versioning
                if (useTimestamps) {
                    parts3 = qualifier;
                }
                convertedVersion = parts[0] + "." + parts[1] + "." + parts[2] + "." + parts3;
            } else {
                convertedVersion = parts[0] + "." + parts[1] + "." + parts[2];
            }
        } else if ((parts.length == 3) || !includeQualifier) {
            // release versions
            // just use first three here
            convertedVersion = parts[0] + "." + parts[1] + "." + parts[2];
            if (useTimestamps) {
                convertedVersion = convertedVersion + "." + qualifier;
            }
        } else {
            throw new MojoFailureException("Maven version format unsupported: " + mvnVersion);
        }

        return convertedVersion;
    }

    public static File getBWAdminHome(String tibcoHome, String bwVersion) throws Exception {

        File bwAdminHome = new File(new File(tibcoHome), "bw/" + bwVersion + "/bin/");

        if (bwAdminHome.exists()) {
            return bwAdminHome;
        }

        throw new MojoExecutionException("Failed to find Admin Home at location : " + bwAdminHome);


    }

    /**
     * @param artifact
     * @return
     */
    public static String getClassifierOrEmptyString(Artifact artifact) {
        String classifier = "";
        if (artifact.hasClassifier()) {
            return artifact.getClassifier();
        }
        return classifier;
    }

    /**
     * Loads the TibcoXMLfile in a Document object (DOM)
     *
     * @param log
     * @param file the XML File to load
     * @return the root Document object for the File file
     * @throws Exception
     */
    public static Document loadXML(Log log, File file) throws Exception {

        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        docFactory.setNamespaceAware(true);

        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        Document doc = docBuilder.parse(file);

        log.debug("Loaded XML File into DOM: " + file.getName());
        return doc;

    }

    /**
     * Save the XML Dcoument to a temporary file with the new changes.
     *
     * @param log          maven logger
     * @param tmpFileName  name for temp file
     * @param doc          the Document
     * @param indentAmount amount of spaces to indent
     * @param standalone   whether this is a standalone document
     * @return the temporary File created
     * @throws Exception
     */
    public static File saveXML(Log log, String tmpFileName, Document doc, int indentAmount, boolean standalone) throws Exception {
        File tempXml = File.createTempFile(tmpFileName, "xml");

        doc.setXmlStandalone(standalone);
        doc.normalizeDocument();

        // remove any prior formatting..
        XPathFactory xpathFactory = XPathFactory.newInstance();
        // XPath to find empty text nodes.

        XPathExpression xpathExp = xpathFactory.newXPath().compile(
                "//text()[normalize-space(.) = '']");
        NodeList emptyTextNodes = (NodeList)
                xpathExp.evaluate(doc, XPathConstants.NODESET);

        // Remove each empty text node from document.
        for (int i = 0; i < emptyTextNodes.getLength(); i++) {
            Node emptyTextNode = emptyTextNodes.item(i);
            emptyTextNode.getParentNode().removeChild(emptyTextNode);
        }

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();

        DOMSource source = new DOMSource(doc);

        StreamResult result = new StreamResult(tempXml);
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", Integer.toString(indentAmount));
        transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, "yes");

        transformer.transform(source, result);

        log.debug("Wrote DOM as XML file in temp location " + tempXml.toString());

        return tempXml;
    }
}

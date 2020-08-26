package com.tibco.bw.maven.plugin.test.helpers;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.tibco.bw.maven.plugin.utils.Constants;

public class BWMFileParser {

	public static BWMFileParser INSTANCE = new BWMFileParser();
	
	private BWMFileParser(){
		
	}

	public HashSet<String> collectMainProcesses(String scaxml) {
		InputStream is = null;
		HashSet<String> skipInitMainProcessSet = new HashSet<String>();
		try {
			
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			is = new ByteArrayInputStream(scaxml.getBytes(StandardCharsets.UTF_8));
			Document document = builder.parse(is);
			NodeList nodeList = document.getDocumentElement().getElementsByTagName("sca:component");

			if(nodeList != null)
			{
			for (int i = 0; i < nodeList.getLength(); i++) 
			{
				NodeList childNodeList = ((Element)nodeList.item(i)).getElementsByTagName("scaext:implementation");
				if(childNodeList != null)
				{
				for (int j = 0; j < childNodeList.getLength(); j++) 
				{
					String processName = ((Element)childNodeList.item(j)).getAttribute("processName");
					if(processName != null){
						BWTestConfig.INSTANCE.getLogger().debug("Adding Component Process to skip init -> "+ processName);
						skipInitMainProcessSet.add("-D"+processName+"="+Constants.COMPONENT_PROCESS);
					}
				}
				}
			}
			}
		}catch (ParserConfigurationException |SAXException | IOException e) {
			e.printStackTrace();
		}   
		finally {
			try {
				if (is != null) {
					is.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return skipInitMainProcessSet;
	}
}

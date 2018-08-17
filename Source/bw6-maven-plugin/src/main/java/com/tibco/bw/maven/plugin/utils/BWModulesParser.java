package com.tibco.bw.maven.plugin.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.jar.Manifest;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.project.MavenProject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.tibco.bw.maven.plugin.osgi.helpers.ManifestParser;
import com.tibco.bw.maven.plugin.utils.BWProjectUtils.MODULE;

public class BWModulesParser {
	private MavenSession session;
	private MavenProject project;
	public String bwEdition;

	public BWModulesParser(MavenSession session, MavenProject project) {
		this.session = session;
		this.project = project;		 
	}

	public List<Artifact> getModulesSet() {
		List<Artifact> list = new ArrayList<Artifact>();
		List<String> modules = getModulesFromTibcoXML();
		for(String module : modules) {
			Artifact file = getArtifactForModule(module);
			if(file != null) {
				list.add(file);	
			}
		}
		return list;
	}
	
	public List<MavenProject> getModulesProjectSet(){
		List<MavenProject> list = new ArrayList<MavenProject>();
		List<String> modules = getModulesFromTibcoXML();
		for(String module : modules) {
			MavenProject project = getProjectForModule(module);
			if(project != null) {
				list.add(project);	
			}
		}
		
		return list;
	}

	private List<String> getModulesFromTibcoXML() {
		List<String> modules = new ArrayList<String>();
		try {
			File tibcoXML = new File(project.getBasedir(), "META-INF/TIBCO.xml");
			NodeList nList = getModuleList(tibcoXML);
			for(int i = 0; i < nList.getLength(); i++) {
				Element node = (Element)nList.item(i);
				NodeList childList = node.getElementsByTagNameNS(Constants.PACKAGING_MODEL_NAMESPACE_URI, Constants.SYMBOLIC_NAME);
				String module = childList.item(0).getTextContent();
				modules.add(module);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return modules;
	}

	private Artifact getArtifactForModule(String module) {
		List<MavenProject> projects = new ArrayList<MavenProject>();
		if(bwEdition != null && bwEdition.equals(Constants.BWCF)) {
			projects = session.getAllProjects();
		} else {
			projects = session.getProjects();
		}

		
		Set<Artifact> depArtifacts = project.getDependencyArtifacts();
		for( Artifact depArtifact : depArtifacts )
		{
			if( depArtifact.getArtifactId().equals(module))
			{
				return depArtifact;
			}
		}
		
		for(MavenProject project : projects) {
			if(project.getArtifactId().equals(module)) {
				Artifact artifact = project.getArtifact();
				return artifact;
			}
		}
		return null;
	}
	
	private MavenProject getProjectForModule(String module){
		List<MavenProject> projects = new ArrayList<MavenProject>();
		if(bwEdition != null && bwEdition.equals(Constants.BWCF)) {
			projects = session.getAllProjects();
		} else {
			projects = session.getProjects();
		}

		for(MavenProject project : projects) {
			if(project.getArtifactId().equals(module)) {
				return project;
			}
		}
		return null;
	}

	private NodeList getModuleList(File tibcoXML) throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		dbFactory.setNamespaceAware(true);
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(tibcoXML);
		NodeList nList = doc.getElementsByTagNameNS(Constants.PACKAGING_MODEL_NAMESPACE_URI, Constants.MODULE);
		return nList;
	}	
	
	
}

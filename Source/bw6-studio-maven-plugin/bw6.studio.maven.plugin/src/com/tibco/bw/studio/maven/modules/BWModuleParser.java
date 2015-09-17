package com.tibco.bw.studio.maven.modules;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class BWModuleParser 
{

	public static BWModuleParser INSTANCE = new BWModuleParser();
	
	public BWModuleParser()
	{
		
		
	}
	
	public List<BWModuleData> parseBWModules( File file )
	{
		
		List<BWModuleData> list = new ArrayList<BWModuleData>();
		
		try
		{
			NodeList nodeList = getModuleList(file);
			for ( int i = 0 ; i < nodeList.getLength(); i++)
			{
				Element node = (Element)nodeList.item(i);
				
				NodeList childList = node.getElementsByTagNameNS("http://schemas.tibco.com/tra/model/core/PackagingModel", "symbolicName");
				NodeList techList = node.getElementsByTagNameNS("http://schemas.tibco.com/tra/model/core/PackagingModel", "technologyType");

				String module = childList.item(0).getTextContent();
				String technologyType = techList.item(0).getTextContent();

				BWModuleData data = new BWModuleData(module, technologyType);
				list.add(data);
			}

		}
		catch(Exception e )
		{
			System.out.println( e );
			e.printStackTrace();
		}

		
		return list;
	}
	
	
	
	private NodeList getModuleList(File tibcoXML) throws ParserConfigurationException, SAXException, IOException 
	{
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		dbFactory.setNamespaceAware(true);

		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(tibcoXML);
		
		NodeList nList = doc.getElementsByTagNameNS("http://schemas.tibco.com/tra/model/core/PackagingModel" , "module");
		return nList;
	}

	
	
	public static class BWModuleData
	{
		private String moduleName;
		private BWModuleType moduleType;
		
		
		public BWModuleData( String moduleName, String moduleType )
		{
			this.moduleName = moduleName;
			this.moduleType = getModuleType(moduleType);
		}
		
		private BWModuleType getModuleType( String moduleType )
		{
			if( moduleType.indexOf("bw-appmodule")  != -1 )
			{
				return BWModuleType.AppModule;
			}
			else if(moduleType.indexOf("bw-sharedmodule")  != -1 )
			{
				return BWModuleType.SharedModule;
			}
			else if( moduleType.equals(("osgi-bundle") ) )
			{
				return BWModuleType.PluginProject;
			}
				
			return null;
		}

		public String getModuleName() {
			return moduleName;
		}

		public BWModuleType getModuleType() {
			return moduleType;
		}
		
		
		
	}
	
}

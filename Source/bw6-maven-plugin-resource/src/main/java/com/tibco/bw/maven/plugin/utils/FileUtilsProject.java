package com.tibco.bw.maven.plugin.utils;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Properties;
import java.util.TreeSet;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.maven.project.ProjectDependenciesResolver;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class FileUtilsProject {
	
	private static final String RESOURCES = "resources";
	private static final String SRC = "src";

	
	/**
	 * @param prop
	 * @param FileOutput 
	 * @throws IOException
	 */
	public static void setSavePropertyOrder(Properties prop, String FileOutput) throws IOException {
		Properties tmp = new Properties() {
			    @Override
			    public synchronized Enumeration<Object> keys() {
			        return Collections.enumeration(new TreeSet<Object>(super.keySet()));
			    }
			};
			tmp.putAll(prop);
			tmp.store(new FileWriter(FileOutput), null);
	}
	
	

    /**
     * Finds the folder name META-INF inside the Application Project.
     * 
     * @return the META-INF folder
     * 
     * @throws Exception
     */
	public static File getApplicationMetaInf(File projectBasedir) throws Exception {
		File[] fileList = projectBasedir.listFiles(new FileFilter() {
			public boolean accept(File pathname) {
				
				if(pathname.getName().indexOf("META-INF") != -1) {
        			return true;
				}
				return false;
			}
		});
		if (fileList.length > 0) {
		    
		    return fileList[0];
		}
		else
		{
			return null;
		}
       
	}
	
	
    /**
     * Finds the folder name src/resources inside the Application Project.
     * 
     * @return the src/resources folder
     * 
     * @throws Exception
     */
	public static File getApplicationMSrcResources(File projectBasedir) throws Exception {
		
		File[] fileList = new File(projectBasedir.getAbsolutePath()+"/" +SRC).listFiles(new FileFilter() {
			public boolean accept(File pathname) {
		
				if(pathname.getName().indexOf(RESOURCES) != -1) {
        			return true;
				}
				return false;
			}
		});
       return fileList[0];
	}

	
	public static void copyFile(File source, File dest) throws IOException {
	    InputStream is = null;
	    OutputStream os = null;
	    try {
	        is = new FileInputStream(source);
	        os = new FileOutputStream(dest);
	        byte[] buffer = new byte[1024];
	        int length;
	        while ((length = is.read(buffer)) > 0) {
	            os.write(buffer, 0, length);
	        }
	    } finally {
	        is.close();
	        os.close();
	    }
	}
	
	public static Properties LoadProperties(String PropertyFile) throws IOException
	{
		Properties prop = new Properties();
		System.out.println(PropertyFile);
		try {
			InputStream input = null;
			input = new FileInputStream(PropertyFile);
			prop.load(input);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// load a properties file

		
		return prop;
	}
	
	
	/**
	 * @param Element name
	 * @return String
	 */
	public static String setvalueNode(Element name, Properties prop) {
		String ValueValueText="";
		NodeList nameList = name.getElementsByTagName("name");
		 //getLog().info(TagName.item(0).getNodeName()+"="+ TagName.item(0).getTextContent());
		 for (int k = 0; k < nameList.getLength(); ++k)
		 {
//        					 Element value = (Element)TagName.item(j);
//        					 getLog().info( TagName.item(j).getNodeValue() +TagName.item(j).getTextContent());
		       Element ValueText = (Element) nameList.item(k);
		       prop.getProperty(ValueText.getTextContent());
		       
				NodeList ValueList = name.getElementsByTagName("value");
				 //getLog().info(TagName.item(0).getNodeName()+"="+ TagName.item(0).getTextContent());
				 for (int l = 0; l < ValueList.getLength(); ++l)
				 {
					 if (prop.getProperty(ValueText.getTextContent())!=null )
					 {
					 
					 Node ValueSetText = ValueList.item(l);
					 ValueSetText.setTextContent(prop.getProperty(ValueText.getTextContent()));
					 }
				 }
		       
		       
		       
		       

		 }
		return ValueValueText;
	}
	
	
	/**
	 * @param file
	 * @param doc
	 * @throws TransformerFactoryConfigurationError
	 * @throws TransformerConfigurationException
	 * @throws TransformerException
	 */
	public static void setUpdatePropertyXML(File file, Document doc)
			throws TransformerFactoryConfigurationError, TransformerConfigurationException, TransformerException {
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(doc);
		StreamResult result = new StreamResult(file);
		transformer.transform(source, result);
	}


}

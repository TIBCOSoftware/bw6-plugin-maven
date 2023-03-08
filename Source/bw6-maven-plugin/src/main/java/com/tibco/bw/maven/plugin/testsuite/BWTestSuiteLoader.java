package com.tibco.bw.maven.plugin.testsuite;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.maven.project.MavenProject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.tibco.bw.maven.plugin.test.helpers.BWTestConfig;
import com.tibco.bw.maven.plugin.utils.BWFileUtils;

public class BWTestSuiteLoader {

	private String testFolderName = null;
	
	public List<File> collectTestCasesList(String baseDir, MavenProject project) throws IOException{
		List<File> testSuitefile = new ArrayList<File>();
		List<String> testSuiteNamePathList = new ArrayList<String>();
		List<String> testSuiteNameList = new ArrayList<String>();
		String testFolderPath = "";
		
		File[] fileList = getFileList(baseDir);
		String contents = FileUtils.readFileToString(fileList[0]);

		if(contents != null) {
			testFolderName = readProperties(contents,fileList[0]);
		}
		
		for(String testSuiteName : BWTestConfig.INSTANCE.getUserTestSuiteNames().keySet())
		{
			String folderPath = BWFileUtils.getTestFolderName(baseDir.toString(),testSuiteName, testFolderName);
			if(null != folderPath){
					testFolderPath = folderPath;
					String[] finalTestSuiteName = null;
					if(testSuiteName.contains(File.separator)) {
						finalTestSuiteName = testSuiteName.split(Pattern.quote(File.separator));
					}
					if(finalTestSuiteName != null) {
						testSuiteNamePathList.add(StringUtils.substringBefore(folderPath,File.separator+finalTestSuiteName[0]).concat("//"+testSuiteName));
						testSuiteNameList.add(testSuiteName);
					}else {
						testSuiteNamePathList.add(folderPath.concat("//"+testSuiteName));
						testSuiteNameList.add(testSuiteName);
					}
					BWTestConfig.INSTANCE.getUserTestSuiteNames().replace(testSuiteName, true);
			}
			else{
				//throw new FileNotFoundException("Test Suite file " +testSuiteName+ " is not found");
				BWTestConfig.INSTANCE.getLogger().debug("Test Suite file " +testSuiteName+ " is not found at "+ baseDir);
			}
		}
		
		BWTestConfig.INSTANCE.setTestSuiteNameList(project, testSuiteNameList);

		

		BWTSFileReaderWrapper fileReader = new BWTSFileReaderWrapper();
		testSuitefile = fileReader.readBWTSFile(testSuiteNamePathList,testFolderPath, project,testFolderName);
		return testSuitefile;



	}
	
	private File[] getFileList(String baseDir) {
		File dir = new File(baseDir);   

		File[] fileList = dir.listFiles(new FilenameFilter() {
			
			public boolean accept(File dir, String name) {
				return name.endsWith(".config");
			}
			
		});
		return fileList;
	}
	
	protected static String readProperties(String contents,File propsFile) {
		InputStream is = null;
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			is = new ByteArrayInputStream(contents.getBytes(StandardCharsets.UTF_8));
			Document document = builder.parse(is);
			NodeList nodeList = document.getDocumentElement().getChildNodes();

			for (int i = 0; i < nodeList.getLength(); i++) 
			{
				Node node = nodeList.item(i);
				if (node instanceof Element) 
				{
					Element el = (Element) node;
					if ("config:specialFolders".equals(el.getNodeName())) {

						NodeList childNodes = el.getChildNodes();

						for (int j = 0; j < childNodes.getLength(); j++) {

							Node cNode = childNodes.item(j);

							if (cNode instanceof Element) {
								Element cEl = (Element) cNode;

								if ("config:folder".equals(cEl.getNodeName()))
								{

									if(cNode.getAttributes().getNamedItem("kind").getNodeValue().equals("bwtf")) {
										return cNode.getAttributes().getNamedItem("location").getNodeValue();
									}

								}
							}

						}
					}
				}
			}

		} catch (ParserConfigurationException | SAXException | IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	
	public List<File> collectTestCasesListFromESM(String baseDir) throws IOException{
		List<File> testSuitefile = new ArrayList<File>();
		List<String> testSuiteNamePathList = new ArrayList<String>();
		List<String> testSuiteNameList = new ArrayList<String>();
		
		String testFolderPath = "";
		for(String testSuiteName : BWTestConfig.INSTANCE.getUserESMTestSuiteNames().keySet())
		{
			if(testFolderName == null) {
				File[] fileList = getFileList(baseDir);
				String contents = FileUtils.readFileToString(fileList[0]);

				if(contents != null) {
					testFolderName = readProperties(contents,fileList[0]);
				}
			}
			
			String folderPath = BWFileUtils.getTestFolderName(baseDir,testSuiteName,testFolderName);
			if(null != folderPath){
					testFolderPath = folderPath;
					testSuiteNamePathList.add(folderPath.concat("//"+testSuiteName));
					testSuiteNameList.add(testSuiteName);
					BWTestConfig.INSTANCE.getUserTestSuiteNames().replace(testSuiteName, true);
			}
			else{
				//throw new FileNotFoundException("Test Suite file " +testSuiteName+ " is not found");
				BWTestConfig.INSTANCE.getLogger().debug("Test Suite file " +testSuiteName+ " is not found at "+ baseDir);
			}
		}
		
		BWTestConfig.INSTANCE.setEsmTestSuiteNameList(baseDir, testSuiteNameList);

		BWTSFileReaderWrapper fileReader = new BWTSFileReaderWrapper();
		testSuitefile = fileReader.readBWTSFileFromESM(testSuiteNamePathList,testFolderPath, baseDir);
		return testSuitefile;



	}
	
}

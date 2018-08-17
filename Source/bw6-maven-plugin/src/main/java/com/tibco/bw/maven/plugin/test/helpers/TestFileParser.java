package com.tibco.bw.maven.plugin.test.helpers;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.tibco.bw.maven.plugin.test.dto.AssertionDTO;
import com.tibco.bw.maven.plugin.test.dto.ConditionLanguageDTO;
import com.tibco.bw.maven.plugin.test.dto.TestCaseDTO;
import com.tibco.bw.maven.plugin.test.dto.TestSetDTO;
import com.tibco.bw.maven.plugin.test.dto.TestSuiteDTO;
import com.tibco.bw.maven.plugin.utils.BWFileUtils;


public class TestFileParser {

	public static TestFileParser INSTANCE = new TestFileParser();

	private TestFileParser() {

	}

	
	@SuppressWarnings({ "unchecked" })
	public void collectAssertions(String contents , TestSuiteDTO suite ) 
	{
		
		
		InputStream is = null;
		try {
			
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			is = new ByteArrayInputStream(contents.getBytes(StandardCharsets.UTF_8));
			Document document = builder.parse(is);
			NodeList nodeList = document.getDocumentElement().getChildNodes();

			String testCaseFile = document.getDocumentElement().getAttribute("location");
			for (int i = 0; i < nodeList.getLength(); i++) 
			{
				Node node = nodeList.item(i);
				if (node instanceof Element) 
				{
					Element el = (Element) node;
					if ("ProcessNode".equals(el.getNodeName())) 
					{
						
						
						String processId = el.getAttributes().getNamedItem("Id").getNodeValue();

						String packageName = BWFileUtils.getFileNameWithoutExtn(processId);
						
						TestSetDTO testset = getProcessTestSet(processId, suite);
						testset.setPackageName(packageName);
						TestCaseDTO testcase = new TestCaseDTO();
						testcase.setTestCaseFile(testCaseFile);
						
						
						
						NodeList childNodes = el.getChildNodes();
						for (int j = 0; j < childNodes.getLength(); j++) 
						{
							Node cNode = childNodes.item(j);
							if (cNode instanceof Element) {
								Element cEl = (Element) cNode;
								if ("Assertion".equals(cEl.getNodeName()))
								{
									AssertionDTO ast = new AssertionDTO();
									ast.setProcessId(processId);

									String location = cEl.getAttributes().getNamedItem("Id").getNodeValue();
									ast.setLocation(location);

									NodeList gChildNodes = cEl.getChildNodes();
									for (int k = 0; k < gChildNodes.getLength(); k++) {
										Node gcNode = gChildNodes.item(k);
										if (gcNode instanceof Element) {
											Element gcEl = (Element) gcNode;
											switch (gcEl.getNodeName()) {
											case "Lang":
												String conditionLanguage = gcEl.getLastChild().getTextContent();
												if ("urn:oasis:names:tc:wsbpel:2.0:sublang:xslt1.0".equals(conditionLanguage)) {
													ast.setConditionLanguage(ConditionLanguageDTO.XSLT10);
												} else {
													ast.setConditionLanguage(ConditionLanguageDTO.XSLT20);
												}
												break;
											case "Expression":
												String expression = gcEl.getLastChild().getTextContent();
												ast.setExpression(expression);
												break;
											}
										}
									}
									testcase.getAssertionList().add(ast);
								}
								else if( "Operation".equals(cEl.getNodeName()))
								{
									NodeList gChildNodes = cEl.getChildNodes();
									for (int k = 0; k < gChildNodes.getLength(); k++) {
										Node gcNode = gChildNodes.item(k);
										if (gcNode instanceof Element) {
											Element e1 = (Element) gcNode;
											if ("resolvedInput".equals(e1.getNodeName()))
											{
												String inputValue = e1.getAttribute("inputValue");
												testcase.setXmlInput(inputValue);
												break;
											}
										}
									}
								}
							}
						}
						
						if( testcase.getAssertionList().isEmpty() )
						{
							BWTestConfig.INSTANCE.getLogger().info( "No assertions found in the Test File : " + testcase.getTestCaseFile() + " . Skipping the running of file." );
						}
						else
						{
							testset.getTestCaseList().add(testcase);
						}
					}
				}
			}
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (is != null) {
					is.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}
	
	@SuppressWarnings("unchecked")
	private TestSetDTO getProcessTestSet( String processName , TestSuiteDTO suite )
	{
		
		for( int i = 0 ; i < suite.getTestSetList().size() ; i++ )
		{
			if( suite.getTestSetList().get(i) != null && ((TestSetDTO)suite.getTestSetList().get(i)).getProcessName().equals( processName ) )
			{
				return (TestSetDTO)suite.getTestSetList().get(i);
			}
		}
		
		TestSetDTO set = new TestSetDTO();
		set.setProcessName(processName);
		suite.getTestSetList().add( set );
		
		return set;
	}

}

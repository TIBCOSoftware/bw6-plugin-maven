package com.tibco.bw.maven.plugin.test.helpers;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.tibco.bw.maven.plugin.test.dto.AssertionDTO;
import com.tibco.bw.maven.plugin.test.dto.ConditionLanguageDTO;
import com.tibco.bw.maven.plugin.test.dto.MockActivityDTO;
import com.tibco.bw.maven.plugin.test.dto.TestCaseDTO;
import com.tibco.bw.maven.plugin.test.dto.TestSetDTO;
import com.tibco.bw.maven.plugin.test.dto.TestSuiteDTO;
import com.tibco.bw.maven.plugin.utils.BWFileUtils;
import com.tibco.bw.maven.plugin.utils.Constants;


public class TestFileParser {

	public static TestFileParser INSTANCE = new TestFileParser();

	/**
	 * BWCE-11850 : separates the .bwt location from the input-row label inside
	 * {@code TestCaseDTO.testCaseFile}, which is the only identity the engine echoes back.
	 * Only appended when a .bwt actually declares more than one input row, so single-input
	 * test files keep the exact identity - and the exact report output - they had before.
	 */
	public static final String ROW_SEPARATOR = "#";

	boolean disableMocking = false;
	
	boolean disableAssertions = false;
	
	boolean showFailureDetails = false;
	

	private TestFileParser() {

	}

	
	/**
	 * BWCE-11850 : a single .bwt may declare several {@code <Inputs>} rows under its
	 * {@code <Operation>}. Each row is one invocation of the process, so the file fans out
	 * into one {@link TestCaseDTO} per row - exactly the shape the engine already receives
	 * today when the same process is covered by N separate .bwt files.
	 *
	 * Assertions, mocked activities and skipped activities are declared once per ProcessNode
	 * and replayed for every row; only the starter payload and the gold file can vary per row.
	 */
	@SuppressWarnings({ "unchecked" })
	public void collectAssertions(String contents , TestSuiteDTO suite , String baseDirectoryPath ) throws Exception,FileNotFoundException
	{
		String assertionMode = "Primitive";
		String moduleName;

		InputStream is = null;
		try {

			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			is = new ByteArrayInputStream(contents.getBytes(StandardCharsets.UTF_8));
			Document document = builder.parse(is);
			NodeList nodeList = document.getDocumentElement().getChildNodes();

			// Normalized once so that gold, input and mock paths are resolved the same way
			// regardless of the order the elements appear in the .bwt.
			String basePath = baseDirectoryPath.replace("\\", "/");
			String testCaseFile = document.getDocumentElement().getAttribute("location");
			for (int i = 0; i < nodeList.getLength(); i++)
			{
				Node node = nodeList.item(i);
				if (node instanceof Element)
				{
					Element el = (Element) node;
					if ("ProcessNode".equals(el.getNodeName()))
					{
						String componentName = null;
						suite.setShowFailureDetails(showFailureDetails);
						String processId = el.getAttributes().getNamedItem("Id").getNodeValue();
						String processName = el.getAttributes().getNamedItem("Name").getNodeValue();
						moduleName = el.getAttributes().getNamedItem("moduleName").getNodeValue();
						if(null != el.getAttributes().getNamedItem("componentProcessName")) {
							componentName =el.getAttributes().getNamedItem("componentProcessName").getNodeValue();
						}
						if(null != BWTestConfig.INSTANCE.getTestSuiteName() && !BWTestConfig.INSTANCE.getTestSuiteName().isEmpty()){
							BWTestConfig.INSTANCE.getTestCaseWithProcessNameMap().put(testCaseFile, processId);
						}

						String packageName = BWFileUtils.getFileNameWithoutExtn(processId);

						TestSetDTO testset = getProcessTestSet(processId, suite);
						testset.setPackageName(packageName);
						testset.setComponentName(componentName);

						// Collected once for the whole ProcessNode, then replayed per input row.
						List<AssertionSpec> assertionSpecs = new ArrayList<AssertionSpec>();
						List<MockActivityDTO> mockActivities = new ArrayList<MockActivityDTO>();
						List<String> skipActivities = new ArrayList<String>();
						ArrayList<TestCaseDTO.Property> properties = new ArrayList<TestCaseDTO.Property>();
						List<InputRow> inputRows = new ArrayList<InputRow>();
						String lastMockOutputFilePath = null;
						String operationName = null;
						String serviceName = null;
						String serviceType = null;
						String processStarterID = null;
						String inlineXmlInput = null;

						NodeList childNodes = el.getChildNodes();
						for (int j = 0; j < childNodes.getLength(); j++) 
						{
							Node cNode = childNodes.item(j);
							if (cNode instanceof Element) {
								Element cEl = (Element) cNode;
								if ("Assertion".equals(cEl.getNodeName()))
								{
									
									if(!disableAssertions){
											if(null != cEl.getAttributes().getNamedItem("goldOutputFromFile") && "true".equals(cEl.getAttributes().getNamedItem("goldOutputFromFile").getNodeValue())){
												assertionMode = "ActivityWithGoldFile";
											}
											else if(null != cEl.getAttributes().getNamedItem("assertionType") && "Activity".equals(cEl.getAttributes().getNamedItem("assertionType").getNodeValue())){
												assertionMode = "Activity";
											}
											else{
												assertionMode = "Primitive";
											}
									AssertionSpec spec = new AssertionSpec();
									spec.processId = processId;
									spec.assertionMode = assertionMode;

									String location = cEl.getAttributes().getNamedItem("Id").getNodeValue();
									String activityName = cEl.getAttributes().getNamedItem("Name").getNodeValue();
									spec.activityName = activityName;
									spec.location = location;

									NodeList gChildNodes = cEl.getChildNodes();
									for (int k = 0; k < gChildNodes.getLength(); k++) {
										Node gcNode = gChildNodes.item(k);
										if (gcNode instanceof Element) {
											Element gcEl = (Element) gcNode;
											switch (gcEl.getNodeName()) {
											case "Lang":
												String conditionLanguage = gcEl.getLastChild().getTextContent();
												if ("urn:oasis:names:tc:wsbpel:2.0:sublang:xslt1.0".equals(conditionLanguage)) {
													spec.conditionLanguage = ConditionLanguageDTO.XSLT10;
												} else {
													spec.conditionLanguage = ConditionLanguageDTO.XSLT20;
												}
												break;
											case "Expression":
												String expression = null;
												if(gcEl.getLastChild() != null)
													expression = gcEl.getLastChild().getTextContent();
												else
													throw new Exception("Process : "+ processName + ", Activity : "+ activityName + ", Id : "+ location +", Error : Invalid Activity Assertion Configuration.");
												if("ActivityWithGoldFile".equals(assertionMode)){
													// The gold path stays relative here - it is resolved per input row,
													// so that each row can point the same assertion at its own gold file.
													String goldPath = StringUtils.substringBetween(expression,"file:///", "')");
													BWTestConfig.INSTANCE.getLogger().debug("Expression - "+ expression);
													if(goldPath == null){
														BWTestConfig.INSTANCE.getLogger().debug("Process : "+ processName + ", Activity : "+ activityName+ ", Id : "+ location +", Error : Invalid Gold File Path. Valid example is - doc('file:///<path-to-file>')");
														throw new Exception("Process : "+ processName + ", Activity : "+ activityName+ ", Id : "+ location +", Error : Invalid Gold File Path. Valid example is - doc('file:///<path-to-file>')");
													}
													spec.goldRelativePath = goldPath;
												}
												spec.expression = expression;
												break;
											}
										}
									}
									assertionSpecs.add(spec);
								 }
								}
								else if( "Operation".equals(cEl.getNodeName()))
								{
									//support input from file
									if(null != cEl.getAttribute("Name")) {
										operationName = cEl.getAttribute("Name");
									}
									if(null != cEl.getAttribute("serviceName")) {
										serviceName = cEl.getAttribute("serviceName");
									}
									serviceType = cEl.getAttribute("restOperationName");
									NodeList inputNodes = cEl.getElementsByTagName("Inputs");
									if(inputNodes != null && inputNodes.getLength() > 0){
										// All rows target the same starter activity, so the id of the first
										// row is the starter id for every one of them.
										String location = ((Element) inputNodes.item(0)).getAttribute("Id");
										processStarterID = StringUtils.substringBefore(location, moduleName);
									}
									inputRows = collectInputRows(cEl, basePath, processName);
									if(inputRows.isEmpty())
									{
										NodeList gChildNodes = cEl.getChildNodes();
										for (int k = 0; k < gChildNodes.getLength(); k++) {
											Node gcNode = gChildNodes.item(k);
											if (gcNode instanceof Element) {
												Element e1 = (Element) gcNode;
												if ("resolvedInput".equals(e1.getNodeName()))
												{
													inlineXmlInput = e1.getAttribute("inputValue");
												}else if("properties".equals(e1.getNodeName())){
													Element propetiesElement = (Element) e1;
														NodeList list = propetiesElement.getChildNodes();
														properties.clear();
														for (int index = 0; index < list.getLength(); index++) {
															Node currNode = list.item(index);
															if (currNode instanceof Element) {
																Element currentProperty = (Element) currNode;
																TestCaseDTO.Property property = new TestCaseDTO.Property();
																String value = currentProperty.getAttribute("value");
																property.setValue(value);
																String propertyType = currentProperty.getAttribute("propertyType");
																property.setPropertyType(propertyType);
																String name = currentProperty.getAttribute("Name");
																property.setName(name);
																String type = currentProperty.getAttribute("type");
																property.setType(type);

																properties.add(property);
															}
														}
												}
											}
										}
									}
								}else if("MockActivity".equals(cEl.getNodeName()) || "MockFault".equals(cEl.getNodeName())){
									
									MockActivityDTO mockActivity = new MockActivityDTO();
									
									String location = cEl.getAttributes().getNamedItem("Id").getNodeValue();
									String activityName = cEl.getAttributes().getNamedItem("Name").getNodeValue();
									Node outputItem = cEl.getAttributes().getNamedItem("outputPresent");
									mockActivity.setLocation(location);
									
									NodeList gChildNodes = cEl.getChildNodes();
									for (int k = 0; k < gChildNodes.getLength(); k++) {
										Node gcNode = gChildNodes.item(k);
										if (gcNode instanceof Element) {
											Element e1 = (Element) gcNode;
											if ("MockOutputFilePath".equals(e1.getNodeName()))
											{
												String mockOutputFilePath = e1.getTextContent();
												if(mockOutputFilePath == null || mockOutputFilePath.trim().length() == 0){
													BWTestConfig.INSTANCE.getLogger().debug("Process : "+ processName + ", Activity : "+ activityName+ ", Id : "+ location +", Error : Invalid Mock Output File Path - "+mockOutputFilePath);
													throw new Exception("Process : "+ processName + ", Activity : "+ activityName+ ", Id : "+ location +", Error : Invalid Mock Output File Path - "+mockOutputFilePath);
												}
												// on Mac and Unix - will replace backslash with forward slash
												// on Windows - noop
												mockOutputFilePath = mockOutputFilePath.replace("\\", File.separator);
												File file = new File(mockOutputFilePath);
												if(!file.isAbsolute()){
													BWTestConfig.INSTANCE.getLogger().debug("Provided Mock File path is relative "+file.getPath());
													mockOutputFilePath = basePath.concat("/"+mockOutputFilePath);
												}
												boolean isActivityHasOutput = false;
												if (outputItem != null) {
													// do not execute if activity does have output
													String outputPresent = outputItem.getNodeValue();
													if (outputPresent == null || outputPresent.isEmpty()
															|| outputPresent.equals("true")) {
														isActivityHasOutput = true;
													}
												}
												
												if (!disableMocking && isActivityHasOutput){
													boolean isValidFile = validateMockXMLFile(mockOutputFilePath,
															activityName, processName);
													if (isValidFile) {
														mockActivity.setmockOutputFilePath(mockOutputFilePath);
														lastMockOutputFilePath = mockOutputFilePath;
														mockActivities.add(mockActivity);
														break;
													}
												}
												
											}
								      }
						       	}
						     }else if( "restNode".equals(cEl.getNodeName())) {

									//support input from file
						    	 NodeList operationNodes = cEl.getElementsByTagName("Operation");
						    	 if(operationNodes != null && operationNodes.getLength() > 0){
						    		 Element operation = (Element) operationNodes.item(0);
									if(null != operation.getAttribute("Name")) {
										operationName = operation.getAttribute("Name");
									}
									if(null != operation.getAttribute("serviceName")) {
										serviceName = operation.getAttribute("serviceName");
									}
									serviceType = operation.getAttribute("restOperationName");
									List<InputRow> restRows = collectInputRows(operation, basePath, processName);
									if(!restRows.isEmpty()){
										// A restNode input supersedes anything read from a sibling Operation,
										// matching the previous last-one-wins behaviour.
										inputRows = restRows;
									}
						    	 }

						       	}else if("SkipActivity".equals(cEl.getNodeName())) {
						       		String activityName = cEl.getAttributes().getNamedItem("Name").getNodeValue();
						       		skipActivities.add(activityName);
						       	}
						   }
						}
						if(disableMocking){
							BWTestConfig.INSTANCE.getLogger().info("-----------------------------------------------------------------------------------------------");
							BWTestConfig.INSTANCE.getLogger().info("## Mocking will be disabled for all Mocked Activities. DisableMocking :" + disableMocking +" ##");
							BWTestConfig.INSTANCE.getLogger().info("-----------------------------------------------------------------------------------------------");
						}
						
						if(disableAssertions){
							BWTestConfig.INSTANCE.getLogger().info("-----------------------------------------------------------------------------------------------");
							BWTestConfig.INSTANCE.getLogger().info("## All Assertions will be disabled. DisableAssertions :" + disableAssertions +" ##");
							BWTestConfig.INSTANCE.getLogger().info("-----------------------------------------------------------------------------------------------");
						}
						
						if( assertionSpecs.isEmpty() && mockActivities.isEmpty() && !disableMocking && !disableAssertions)
						{
							BWTestConfig.INSTANCE.getLogger().info( "No assertions and Mock Activities found in the Test File : " + testCaseFile + " . Skipping the running of file." );

						}
						else
						{
							if(inputRows.isEmpty())
							{
								// No <Inputs> row declared - the single legacy test case, driven by
								// the inline resolvedInput (or by nothing at all).
								InputRow legacyRow = new InputRow();
								legacyRow.xmlInput = inlineXmlInput;
								inputRows.add(legacyRow);
							}

							boolean parameterized = inputRows.size() > 1;
							if(parameterized)
							{
								BWTestConfig.INSTANCE.getLogger().info( "Test File : " + testCaseFile + " declares " + inputRows.size()
										+ " input rows - it will run as " + inputRows.size() + " test cases." );
								assignUniqueLabels(inputRows);
							}

							for( InputRow row : inputRows )
							{
								TestCaseDTO testcase = new TestCaseDTO();
								testcase.setTestCaseFile(parameterized ? testCaseFile + ROW_SEPARATOR + row.label : testCaseFile);
								testcase.setOperationName(operationName);
								testcase.setServiceName(serviceName);
								testcase.setServiceType(serviceType);
								testcase.setProcessStarterID(processStarterID);
								testcase.setXmlInput(row.xmlInput);
								testcase.setmockOutputFilePath(lastMockOutputFilePath);

								for( AssertionSpec spec : assertionSpecs )
								{
									testcase.getAssertionList().add( materializeAssertion(spec, row, basePath) );
								}
								for( MockActivityDTO mock : mockActivities )
								{
									testcase.getMockActivityList().add( copyOf(mock) );
								}
								testcase.getSkipActivityList().addAll(skipActivities);
								testcase.getPropertiesList().addAll(properties);

								testset.getTestCaseList().add(testcase);
							}
						}
					}
				}
			}
			
		} catch (ParserConfigurationException e) {
			throw e;
		} catch (FileNotFoundException e) {
			throw e;
		} catch (SAXException e) {
			throw e;
		} catch (IOException e) {
			throw e;
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
	
	
	/**
	 * Reads every {@code <Inputs>} row declared under the given Operation element.
	 *
	 * A row is only a data row when it carries {@code isInputFile="true"}; a lone row without
	 * it is the legacy inline-input shape and yields no rows, so the caller falls back to
	 * {@code resolvedInput}. Inline input cannot be parameterized, so a non-file row is an
	 * error as soon as more than one row is declared.
	 */
	private List<InputRow> collectInputRows(Element operationEl, String basePath, String processName) throws Exception
	{
		List<InputRow> rows = new ArrayList<InputRow>();
		NodeList inputNodes = operationEl.getElementsByTagName("Inputs");
		if(inputNodes == null || inputNodes.getLength() == 0){
			return rows;
		}

		for(int r = 0; r < inputNodes.getLength(); r++){
			Element input = (Element) inputNodes.item(r);
			if(!"true".equals(input.getAttribute("isInputFile"))){
				if(inputNodes.getLength() == 1){
					// Legacy single inline input - handled by the resolvedInput branch.
					return new ArrayList<InputRow>();
				}
				throw new Exception("Process : "+ processName + ", Activity : Start"
						+ ", Error : Input row " + (r + 1) + " of " + inputNodes.getLength()
						+ " does not set isInputFile=\"true\". Every row of a multi-input test must read its payload from a file.");
			}
			if(!input.hasAttribute("inputFile")){
				BWTestConfig.INSTANCE.getLogger().debug("Process : "+ processName + ", Activity : Start "+ ", Error : Invalid Start Input File Path - "+input.getAttribute("InputFile"));
				throw new Exception("Process : "+ processName + ", Activity : Start"+ ", Error : Invalid Start Input File Path - "+input.getAttribute("InputFile"));
			}

			String inputFilePath = input.getAttribute("inputFile");
			BWTestConfig.INSTANCE.getLogger().debug("Reading start activity input from file -> "+ inputFilePath);
			if(inputFilePath == null || inputFilePath.isEmpty()){
				BWTestConfig.INSTANCE.getLogger().debug("Process : "+ processName + ", Activity : Start "+ ", Error : Invalid Start Input File Path - "+inputFilePath);
				throw new Exception("Process : "+ processName + ", Activity : Start"+ ", Error : Invalid Start Input File Path - "+inputFilePath);
			}

			String declaredInputFile = inputFilePath;
			File file = new File(inputFilePath);
			if(!file.isAbsolute()){
				BWTestConfig.INSTANCE.getLogger().debug("Provided Start Input File path is relative -> "+file.getPath());
				inputFilePath = basePath.concat("/"+inputFilePath);
			}

			InputRow row = new InputRow();
			row.xmlInput = FileUtils.readFileToString( new File(inputFilePath) );
			row.label = deriveRowLabel(input, declaredInputFile, r);
			if(input.hasAttribute("goldFile") && !input.getAttribute("goldFile").isEmpty()){
				row.goldFile = input.getAttribute("goldFile");
			}

			// Optional per-activity gold override, for a row that has to pair a different
			// expected output with each of several gold-file assertions.
			NodeList goldNodes = input.getElementsByTagName("GoldFile");
			for(int g = 0; g < goldNodes.getLength(); g++){
				Element gold = (Element) goldNodes.item(g);
				String activityId = gold.getAttribute("activityId");
				String path = gold.getAttribute("path");
				if(activityId == null || activityId.isEmpty() || path == null || path.isEmpty()){
					throw new Exception("Process : "+ processName + ", Activity : Start"
							+ ", Error : <GoldFile> on input row " + (r + 1) + " needs both an activityId and a path.");
				}
				row.goldFileByActivity.put(activityId, path);
			}

			rows.add(row);
		}

		return rows;
	}

	private String deriveRowLabel(Element input, String declaredInputFile, int index)
	{
		if(input.hasAttribute("label") && !input.getAttribute("label").isEmpty()){
			return input.getAttribute("label");
		}
		if(declaredInputFile != null && !declaredInputFile.isEmpty()){
			String basename = declaredInputFile.replace("\\", "/");
			basename = basename.substring(basename.lastIndexOf('/') + 1);
			if(!basename.isEmpty()){
				return basename;
			}
		}
		return "row-" + (index + 1);
	}

	/**
	 * The label ends up in the test case identity, so two rows pointing at same-named files
	 * in different folders must not collapse into one result.
	 */
	private void assignUniqueLabels(List<InputRow> rows)
	{
		HashSet<String> used = new HashSet<String>();
		for(int r = 0; r < rows.size(); r++){
			InputRow row = rows.get(r);
			String label = row.label;
			if(!used.add(label)){
				label = label + "-" + (r + 1);
				used.add(label);
				row.label = label;
			}
		}
	}

	/**
	 * Builds the per-row {@link AssertionDTO}. Only the gold file can differ between rows -
	 * the row may override it wholesale ({@code goldFile}) or per activity ({@code <GoldFile>}).
	 */
	private AssertionDTO materializeAssertion(AssertionSpec spec, InputRow row, String basePath) throws Exception
	{
		AssertionDTO ast = new AssertionDTO();
		ast.setProcessId(spec.processId);
		ast.setAssertionMode(spec.assertionMode);
		ast.setActivityId(spec.activityName);
		ast.setLocation(spec.location);
		ast.setConditionLanguage(spec.conditionLanguage);

		String expression = spec.expression;
		String goldFile = null;

		if("ActivityWithGoldFile".equals(spec.assertionMode) && spec.goldRelativePath != null){
			String override = row == null ? null : row.goldFileByActivity.get(spec.location);
			if(override == null && row != null){
				override = row.goldFile;
			}

			if(override == null){
				BWTestConfig.INSTANCE.getLogger().debug("Provided Gold File path is relative "+spec.goldRelativePath);
				goldFile = basePath.concat("/"+spec.goldRelativePath);
			}
			else if(new File(override).isAbsolute()){
				goldFile = override.replace("\\", "/");
			}
			else{
				goldFile = basePath.concat("/"+override);
			}

			BWTestConfig.INSTANCE.getLogger().debug("Absolute File path "+goldFile);
			expression = StringUtils.replace(expression, spec.goldRelativePath, goldFile);
		}

		if(showFailureDetails){
			setGoldData(spec.assertionMode, expression, ast, goldFile);
		}
		ast.setExpression(expression);

		return ast;
	}

	private MockActivityDTO copyOf(MockActivityDTO source)
	{
		MockActivityDTO copy = new MockActivityDTO();
		copy.setLocation(source.getLocation());
		copy.setmockOutputFilePath(source.getmockOutputFilePath());
		return copy;
	}

	/**
	 * One {@code <Inputs>} row - a single invocation of the process under test.
	 */
	private static class InputRow
	{
		String label;
		String xmlInput;
		String goldFile;
		HashMap<String,String> goldFileByActivity = new HashMap<String,String>();
	}

	/**
	 * An assertion as declared in the .bwt, before its gold path is bound to a row.
	 */
	private static class AssertionSpec
	{
		String processId;
		String assertionMode;
		String location;
		String activityName;
		ConditionLanguageDTO conditionLanguage;
		String expression;
		String goldRelativePath;
	}

	private void setGoldData(String assertionMode, String expression, AssertionDTO ast, String inputFile) throws Exception {
		switch(assertionMode){
		case "Primitive":
				String goldValueWithElement = StringUtils.substringBetween(expression, "test=\"", "\">");
				String goldValue = StringUtils.substringAfter(goldValueWithElement, "=");
				if (goldValue == null) {
					showFailureDetails = false;
					return;
				}
				if(goldValue.contains("'")){
					goldValue = StringUtils.substringBetween(goldValue, "'");
				}
				if(goldValue.equals("xsd:boolean(1)") || goldValue.equals("xsd:boolean(0)")){
					String temp = StringUtils.substringBetween(goldValue, "(", ")");
					if(temp.equals("1")){
						goldValue = "true";
					}
					else{
						goldValue = "false";
					}
				}
				String elementNameString = StringUtils.substringBefore(goldValueWithElement, "=");
				String[] elementNameArray = StringUtils.split(elementNameString, "/");
				String elementName = elementNameArray[elementNameArray.length-1];
				if(elementName.contains(")")){
					elementName = StringUtils.removeEnd(elementName, ")");
				}
				String startElementTag = "<".concat(elementName).concat(">");
				String endElementTag = "</".concat(elementName).concat(">");
	
				ast.setGoldInput(startElementTag.concat(goldValue).concat(endElementTag));
				ast.setStartElementNameTag(startElementTag);
				ast.setEndElementNameTag(endElementTag);
				break;
				
		case "Activity":
			   String activityGoldValue = StringUtils.substringBetween(expression,"<xsl:variable name=\"AssertType\" as=\"item()*\"><Activity-Assertion>","</Activity-Assertion>");
			   activityGoldValue = StringUtils.replace(activityGoldValue, "<xsl:value-of select=\"&quot;" , "");
			   activityGoldValue = StringUtils.replace(activityGoldValue, "&quot;\"  />", "");
			   ast.setGoldInput(activityGoldValue);
			   break;
		
		case "ActivityWithGoldFile":
			  ast.setGoldInput(readXMLFile(inputFile));
			  break;
			   
		}
		
	}

	public HashSet<String> collectSkipInitActivities(String contents){
			InputStream is = null;
			HashSet<String> skipInitActivitiesSet = new HashSet<String>();

			try {

				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
				DocumentBuilder builder = factory.newDocumentBuilder();
				is = new ByteArrayInputStream(contents.getBytes(StandardCharsets.UTF_8));
				Document document = builder.parse(is);
				NodeList nodeList = document.getDocumentElement().getChildNodes();

				for (int i = 0; i < nodeList.getLength(); i++) 
				{
					Node node = nodeList.item(i);
					if (node instanceof Element) 
					{
						String	componentName = null;
						Element el = (Element) node;
						if ("ProcessNode".equals(el.getNodeName())) 
						{
							String processId = el.getAttributes().getNamedItem("Id").getNodeValue();
							String key = "-D"+"Test"+processId+"=true";
							skipInitActivitiesSet.add(key);
							if(null != el.getAttributes().getNamedItem("componentProcessName")) {
									componentName =el.getAttributes().getNamedItem("componentProcessName").getNodeValue();
							
							}
							NodeList childNodes = el.getChildNodes();
							for (int j = 0; j < childNodes.getLength(); j++) 
							{
								Node cNode = childNodes.item(j);
								if (cNode instanceof Element) {
									Element cEl = (Element) cNode;
									if("MockActivity".equals(cEl.getNodeName())){
										MockActivityDTO mockActivity = new MockActivityDTO();
										String location = cEl.getAttributes().getNamedItem("Id").getNodeValue();
										mockActivity.setLocation(location);
										if(!disableMocking){
											String activityName = cEl.getAttributes().getNamedItem("Name").getNodeValue();
											if(null!=activityName){
												skipInitActivitiesSet.add("-D"+processId+activityName+"=true");
											}
										}
									}
									else if( "Operation".equals(cEl.getNodeName()))
									{
										if(null != componentName && !componentName.isEmpty()) {
											NodeList inputNodes = cEl.getElementsByTagName("Inputs");
											if(inputNodes != null && inputNodes.getLength() > 0){
												Element input = (Element) inputNodes.item(0);
												String activityName =  input.getAttribute("Name");;
												if(null!=activityName){
													skipInitActivitiesSet.add("-D"+processId+activityName+"=true");
												}
											}
										}
										String serviceName = cEl.getAttribute("restOperationName");
										if(null != serviceName && serviceName.equals("SOAP")) {
											skipInitActivitiesSet.add("-DskipSOAPReferenceBinding"+"=true");
										}
									}

								}
							}
						}
					}
				}

			} catch (ParserConfigurationException |SAXException | IOException e) {
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
			return skipInitActivitiesSet;

		}
	
	private boolean validateMockXMLFile(String mockOutputFilePath, String activityName, String processName) throws Exception {
		File mockOutputFile = new File(mockOutputFilePath);
		if(mockOutputFile.exists()){
			try {
				 String mockOutputString = readXMLFile(mockOutputFilePath);// TODO Set mockOutputString to TestCase variable mockOutput 
				 DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(new StringReader(mockOutputString)));
				 return true;
		    } catch (Exception e) {
		    	String errorMessage = "Process : "+ processName + ", Activity : "+ activityName + "Error : Provided XML file "+ mockOutputFilePath +" is not valid";
		    	BWTestConfig.INSTANCE.getLogger().error(errorMessage, e);
		    	throw e;
		    }
		}
		else{
			String errorMessage = "Process : "+ processName + ", Activity : "+ activityName + ", Error: Provided XML file "+ mockOutputFilePath +" is not Present";
	    	BWTestConfig.INSTANCE.getLogger().error(errorMessage, new FileNotFoundException());
			throw new Exception();
		}
		
	}


	private String readXMLFile(String mockOutputFilePath) throws IOException {
		String sCurrentLine;
		StringBuilder sb = new StringBuilder();
		try (BufferedReader br = new BufferedReader(new FileReader(mockOutputFilePath))) {
			while ((sCurrentLine = br.readLine()) != null) {
				sb.append(sCurrentLine);
			}
		} catch (IOException e1) {
			throw e1;
		}
		return sb.toString();
	}

	public void setdisbleMocking(boolean disableMocking){
		this.disableMocking = disableMocking;
	}
	
	public void setdisbleAssertions(boolean disableAssertions){
		this.disableAssertions = disableAssertions;
	}
	
	public void setshowFailureDetails(boolean showFailureDetails){
		this.showFailureDetails = showFailureDetails;
	}
	
	public boolean getshowFailureDetails(){
		return showFailureDetails;
	}

	/**
	 * Gives back the .bwt location of a test case identity, dropping any input-row label.
	 * Callers that key off the file itself - the test-suite grouping and the
	 * test-case-to-process map - must go through this, since every row of a parameterized
	 * .bwt shares one underlying file.
	 */
	public static String stripRowLabel(String testCaseFile)
	{
		if(testCaseFile == null){
			return null;
		}
		int index = testCaseFile.lastIndexOf(ROW_SEPARATOR);
		return index > 0 ? testCaseFile.substring(0, index) : testCaseFile;
	}

	/**
	 * The input-row label of a test case identity, or null when the .bwt had a single input.
	 */
	public static String rowLabelOf(String testCaseFile)
	{
		if(testCaseFile == null){
			return null;
		}
		int index = testCaseFile.lastIndexOf(ROW_SEPARATOR);
		return index > 0 ? testCaseFile.substring(index + ROW_SEPARATOR.length()) : null;
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

package com.tibco.bw.maven.plugin.test.rest;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.jar.Manifest;
import java.util.logging.Logger;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.filter.LoggingFilter;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.xmlunit.builder.DiffBuilder;
import org.xmlunit.diff.ComparisonControllers;
import org.xmlunit.diff.Diff;
import org.xmlunit.diff.Difference;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tibco.bw.maven.plugin.osgi.helpers.ManifestParser;
import com.tibco.bw.maven.plugin.osgi.helpers.Version;
import com.tibco.bw.maven.plugin.osgi.helpers.VersionParser;
import com.tibco.bw.maven.plugin.test.dto.AssertionDTO;
import com.tibco.bw.maven.plugin.test.dto.AssertionResultDTO;
import com.tibco.bw.maven.plugin.test.dto.BWTestSuiteDTO;
import com.tibco.bw.maven.plugin.test.dto.CompleteReportDTO;
import com.tibco.bw.maven.plugin.test.dto.ModuleInfoDTO;
import com.tibco.bw.maven.plugin.test.dto.TestCaseDTO;
import com.tibco.bw.maven.plugin.test.dto.TestCaseResultDTO;
import com.tibco.bw.maven.plugin.test.dto.TestSetDTO;
import com.tibco.bw.maven.plugin.test.dto.TestSetResultDTO;
import com.tibco.bw.maven.plugin.test.dto.TestSuiteDTO;
import com.tibco.bw.maven.plugin.test.dto.TestSuiteResultDTO;
import com.tibco.bw.maven.plugin.test.helpers.BWTestConfig;
import com.tibco.bw.maven.plugin.test.helpers.TestFileParser;
import com.tibco.bw.maven.plugin.test.setuplocal.BWTestExecutor;
import com.tibco.bw.maven.plugin.utils.BWProjectUtils;

public class BWTestRunner 
{

	private static final String CONTEXT_ROOT = "/bwut";
	private Client jerseyClient;
	private WebTarget r;
	private String scheme = "http";
	private final String host;
	private final int port;
	File reportDir = new File( BWTestConfig.INSTANCE.getProject().getBasedir() , "target/bwtest");
	

	public BWTestRunner(final String host, final int port) {
		this.host = host;
		this.port = port;
	}
	
	
	private void init() 
	{
		if (this.jerseyClient == null) 
		{
			ClientConfig clientConfig = new ClientConfig();
			clientConfig.register(JacksonFeature.class).register(MultiPartFeature.class);
			
			if(BWTestConfig.INSTANCE.getLogger().isDebugEnabled()){
				Logger logger = Logger.getLogger(getClass().getName());
				clientConfig.register(new LoggingFilter(logger, true));
			}
			this.jerseyClient = ClientBuilder.newClient(clientConfig);
		}
		this.r = this.jerseyClient.target(UriBuilder.fromPath(CONTEXT_ROOT).scheme(this.scheme).host(this.host).port(this.port).build());
		reportDir.mkdirs();
	
	}
	
	public void runTests() throws MojoFailureException, Exception
	{
		init();
		removePidFolder();
		r.path("tests").path("enabledebug").request().get();
		
		List<MavenProject> projects = BWTestConfig.INSTANCE.getSession().getProjects();
		
		CompleteReportDTO result = new CompleteReportDTO();
		
		TestSuiteDTO suite = null;
		
		int failures = 0;
		
		if(BWTestConfig.INSTANCE.getRunESMTest()==true){
			for(File baseDirectory : BWTestConfig.INSTANCE.getESMDirectories()){
				failures = failures + runTestsPerESM( baseDirectory , result ,suite);	
			}
		}
		
		for( MavenProject project : projects )
		{
			if( project.getPackaging().equals("bwmodule") && !isCXF(project))	//skip tests if CXF
			{
				failures = failures + runTestsPerModule( project , result );	
			}
			
		}
		
		saveReport(result);
		
		if( failures > 0 )
		{
        	throw new MojoFailureException( "There are tests failure. Please refer to test results for individual test results" );
        }

		
	}
	
	private void removePidFolder() throws IOException {
		String parentFolder = getWorkspacepath() + ".parent";
		File file = new File(parentFolder);
		if (file.exists()) {
			File[] files = file.listFiles();
			if (files != null) {
				for (File tempFile : files) {
					if (tempFile!= null && tempFile.getName().startsWith("pid_")) {
						FileUtils.deleteDirectory(tempFile);
					}
				}
			}
		}
	}
	
	private String getWorkspacepath() {
		String workspacePath= System.getProperty("user.dir");
		String wsPath= workspacePath;
		if(wsPath.indexOf(".parent")!=-1){
			wsPath= workspacePath.substring(0, workspacePath.lastIndexOf(".parent"));
		}
		return wsPath;
	}
	
	private boolean isCXF(MavenProject project){
		for(Dependency dep : project.getDependencies()){
			if(dep.getArtifactId().equalsIgnoreCase("com.tibco.xml.cxf.common")){
				return true;
			}
		}
		return false;
	}
	
	@SuppressWarnings("unchecked")
	public int runTestsPerModule( MavenProject project , CompleteReportDTO result ) throws MojoFailureException, Exception
	{
			AssertionsLoader loader = new AssertionsLoader( project);
			TestSuiteDTO suite = loader.loadAssertions();

			BWTestConfig.INSTANCE.getLogger().info( "Starting Tests in Module : " + project.getArtifactId() );			
			
			ModuleInfoDTO minfo = getModuleInfo( project );
			
			suite.setModuleInfo(minfo);

	        printTestStats(suite);

	        /*String response = r.path("tests").path("runtest").request(MediaType.APPLICATION_XML).post(Entity.entity(suite, MediaType.APPLICATION_XML) , String.class);
	        BWTestConfig.INSTANCE.getLogger().info(response);*/
	        
			TestSuiteResultDTO resultDTO = r.path("tests").path("runtest").request(MediaType.APPLICATION_XML).post(Entity.entity(suite, MediaType.APPLICATION_XML) , TestSuiteResultDTO.class);
			if( null != resultDTO ){
				int failures = printTestResults(resultDTO, suite, project);
				result.getModuleResult().add(resultDTO);
				
				return failures;
			}
			else{
				throw new MojoFailureException("An Exception occurred while running test. Please enable BW debug logs to know more.");
			}
		

			
		
	}
	
	
	@SuppressWarnings("unchecked")
	public int runTestsPerESM( File project , CompleteReportDTO result, TestSuiteDTO suite ) throws MojoFailureException, Exception
	{
			AssertionsLoader loader = new AssertionsLoader(project);
			 suite = loader.loadAssertionsFromESM();

			//BWTestConfig.INSTANCE.getLogger().info( "Starting Tests in Module : " + project.getArtifactId() );			
			
			ModuleInfoDTO minfo = getModuleInfoFromESM( project );
			
			suite.setModuleInfo(minfo);
			
			printTestStats(suite);
			
			TestSuiteResultDTO resultDTO = r.path("tests").path("runtest").request(MediaType.APPLICATION_XML).post(Entity.entity(suite, MediaType.APPLICATION_XML) , TestSuiteResultDTO.class);
			if( null != resultDTO ){
				int failures = printTestResults(resultDTO, suite, null);
				result.getModuleResult().add(resultDTO);
				
				return failures;
			}
			else{
				throw new MojoFailureException("An Exception occurred while running test. Please enable BW debug logs to know more.");
			}
			
		
	}
	
	private ModuleInfoDTO getModuleInfo( MavenProject module )
	{
		
		
		MavenProject application = BWProjectUtils.getApplicationProject(BWTestConfig.INSTANCE.getSession()); 
		
		Manifest projectManifest = ManifestParser.parseManifest( module.getBasedir() );
		String moduleVersion = projectManifest.getMainAttributes().getValue("Bundle-Version");
		String moduleName = projectManifest.getMainAttributes().getValue("Bundle-SymbolicName");

		
		Manifest appManifest = ManifestParser.parseManifest( application.getBasedir() );
		Version version = VersionParser.parseVersion(appManifest.getMainAttributes().getValue("Bundle-Version"));
		String appVersion = version.getMajor() + "." + version.getMinor();
		
		String appName = appManifest.getMainAttributes().getValue("Bundle-SymbolicName");
		
		
		ModuleInfoDTO minfo = new ModuleInfoDTO();
		minfo.setAppName(appName);
		minfo.setAppVersion(appVersion);
		minfo.setModuleName(moduleName);
		minfo.setModuleVersion(moduleVersion);

		return minfo;
	}
	
	private ModuleInfoDTO getModuleInfoFromESM( File module )
	{
		
		
		MavenProject application = BWProjectUtils.getApplicationProject(BWTestConfig.INSTANCE.getSession()); 
		
		Manifest projectManifest = ManifestParser.parseManifest( module );
		String moduleVersion = projectManifest.getMainAttributes().getValue("Bundle-Version");
		String moduleName = projectManifest.getMainAttributes().getValue("Bundle-SymbolicName");

		
		Manifest appManifest = ManifestParser.parseManifest( application.getBasedir() );
		Version version = VersionParser.parseVersion(appManifest.getMainAttributes().getValue("Bundle-Version"));
		String appVersion = version.getMajor() + "." + version.getMinor();
		
		String appName = appManifest.getMainAttributes().getValue("Bundle-SymbolicName");
		
		
		ModuleInfoDTO minfo = new ModuleInfoDTO();
		minfo.setAppName(appName);
		minfo.setAppVersion(appVersion);
		minfo.setModuleName(moduleName);
		minfo.setModuleVersion(moduleVersion);

		return minfo;
	}
	
	private void saveReport( CompleteReportDTO resultDTO) 
	{
		try {

			
			reportDir.mkdirs();
			File resultFile = new File( reportDir , "bwtestreport.xml");
			
			JAXBContext jaxbContext = JAXBContext.newInstance(CompleteReportDTO.class);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

			// output pretty printed
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

			jaxbMarshaller.marshal(resultDTO, resultFile);
			//jaxbMarshaller.marshal(resultDTO, System.out);
			
			File generatedResultFile = new File( reportDir , "generatedJunitReport.xml");
			GeneratejunitReport obj = new GeneratejunitReport();
			obj.genereateReport(resultFile.getPath(), generatedResultFile.getPath());

		      } catch (JAXBException e) {
			e.printStackTrace();
		      }
	}

	private void printTestStats( TestSuiteDTO suite )
	{
		StringBuilder builder = new StringBuilder();
		
		builder.append( "Uploading tests for Processes in Module :  " + suite.getModuleInfo().getModuleName() );
		
		for( int i = 0 ; i < suite.getTestSetList().size() ; i++ )
		{
			builder.append( "\n " + ((TestSetDTO)suite.getTestSetList().get( i)).getProcessName() );
		}
		
		
        BWTestConfig.INSTANCE.getLogger().info( builder.toString() );
		
	}
	
	private int printTestResults( TestSuiteResultDTO result, TestSuiteDTO suite, MavenProject project ) throws MojoFailureException
	{
		StringBuilder builder = new StringBuilder();
		int totaltests = 0;
		int totalsuccess = 0;
		int totalfailure = 0;
		int totalSkipped = 0;
		int totalProcessFailure = 0;
		int finalResult = 0;
		TestCaseResultDTO testcase = null;
		
		if (null != BWTestConfig.INSTANCE.getTestSuiteName() && !BWTestConfig.INSTANCE.getTestSuiteName().isEmpty() && null != project) {
			Map<String, List<File>> testSuiteMap = BWTestConfig.INSTANCE.getTestSuiteMap(project);
			finalResult = printTestSuiteWiseResult(result, testSuiteMap);
			return finalResult;
		}
		
		for( int i =0 ; i < result.getTestSetResult().size() ; i++ )
		{
			StringBuilder processFileBuilder = new StringBuilder();
			TestSetResultDTO testset = (TestSetResultDTO) result.getTestSetResult().get( i );
			builder.append("\n");
			processFileBuilder.append( "Tests for " + testset.getProcessName() + "\n");
			processFileBuilder.append( "Tests run : " + testset.getTestCaseResult().size() );

			int success = 0;
			int failure = 0;
			int skipped = 0;
			int processFilure = 0;
			
			for( int j = 0 ; j < testset.getTestCaseResult().size() ; j++ )
			{
				 testcase = (TestCaseResultDTO) testset.getTestCaseResult().get( j );
				
				if( testcase.getAssertionFailure() > 0 )
				{
					failure= failure + testcase.getAssertionFailure();
					
					if(suite.isShowFailureDetails()){
						printFailureDetails(testcase,testcase.getTestCaseFile(),testset.getProcessName(),"");
					}
				}
				if( testcase.getProcessFailures() > 0 )
				{
					processFilure++;
					totalProcessFailure++;
					
				}
				if( testcase.getAssertionsRun()>0)
				{
					success = success + testcase.getAssertionsRun();
					
				}
				totaltests++;

				// AMBW-46991 BEGINS
				int currSkipped = testcase.getAssertions() - (testcase.getAssertionsRun() + testcase.getAssertionFailure());
				if (currSkipped > 0) {
					skipped = skipped + currSkipped;
				}
				// AMBW-46991 ENDS
			}
			totalsuccess = totalsuccess + success;
			totalfailure = totalfailure + failure;
			totalSkipped = totalSkipped + skipped;
			
			processFileBuilder.append( "    Success : " + success + " 	Failure : " + failure + "	Skipped : " + skipped + "	Errors : " + processFilure);
			builder.append( processFileBuilder.toString() );
			writeProcessResult( result.getModuleInfo().getModuleName() , testset , processFileBuilder.toString() );
		}
		
		builder.append( "\n\nResults \n");
		builder.append( "Success : " + totalsuccess + "    Failure : " + totalfailure  + "    Skipped : " + totalSkipped + "    Errors : " + totalProcessFailure);
       
		BWTestConfig.INSTANCE.getLogger().info( builder.toString() );
		
		if(BWTestExecutor.INSTANCE.isSkippedTestError()) {
			totalfailure = totalfailure + totalSkipped;
		}
		
		if(totalfailure>0){
        	finalResult = totalfailure;
        }
        else{
        	finalResult = totalProcessFailure;
        }
        
        return finalResult ;
        
	}
	
	private int printTestSuiteWiseResult(TestSuiteResultDTO result,Map<String, List<File>> testSuiteMap) {
		TestCaseResultDTO testcase = null;
		List<BWTestSuiteDTO> testSuiteList = new ArrayList<BWTestSuiteDTO>();
		BWTestSuiteDTO bwTestSuite = null;
		StringBuilder builder = new StringBuilder();
		int totalfailure = 0;
		int totalProcessFailure = 0;
		int totalsuccess = 0;
		int totaltests = 0;
		int finalResult = 0;

		for (Map.Entry<String, List<File>> entry : testSuiteMap.entrySet()) {
			List<TestCaseResultDTO> testCaseList = new ArrayList<TestCaseResultDTO>();
			bwTestSuite = new BWTestSuiteDTO();
			bwTestSuite.setTestSuiteName(entry.getKey());
			for (File file : entry.getValue()) {
				for (int i = 0; i < result.getTestSetResult().size(); i++) {
					TestSetResultDTO testset = (TestSetResultDTO) result
							.getTestSetResult().get(i);

					for (int j = 0; j < testset.getTestCaseResult().size(); j++) {
						testcase = (TestCaseResultDTO) testset
								.getTestCaseResult().get(j);
						if (file.getName().equals(
								testcase.getTestCaseFile().substring(
										testcase.getTestCaseFile().lastIndexOf(
												"/") + 1))) {
							testCaseList.add(testcase);
							break;
						}
					}
				}
			}

			bwTestSuite.setTestCaseList(testCaseList);
			bwTestSuite.setTestCaseWithProcessNameMap(BWTestConfig.INSTANCE.getTestCaseWithProcessNameMap());
			testSuiteList.add(bwTestSuite);
		}
        result.setBWTestSuite(testSuiteList);
		for (BWTestSuiteDTO bwTestSuiteData : testSuiteList) {
			StringBuilder processFileBuilder = new StringBuilder();
			builder.append("\n");
			processFileBuilder.append("Tests for TestSuite "
					+ bwTestSuiteData.getTestSuiteName() + "\n");
			processFileBuilder.append("Tests run : "
					+ bwTestSuiteData.getTestCaseList().size());

			@SuppressWarnings("unchecked")
			List<TestCaseResultDTO> testCaseList = bwTestSuiteData
					.getTestCaseList();
			int success = 0;
			int failure = 0;
			int processFilure = 0;
			for (TestCaseResultDTO testCase : testCaseList) {

				if (testCase.getAssertionFailure() > 0) {
					failure= failure+testCase.getAssertionFailure();
					
					if (TestFileParser.INSTANCE.getshowFailureDetails()) {
						printFailureDetails(testCase,
								testCase.getTestCaseFile(),
								BWTestConfig.INSTANCE.getTestCaseWithProcessNameMap().get(testCase.getTestCaseFile()),bwTestSuiteData.getTestSuiteName());
					}
				} 
				
				if (testCase.getProcessFailures() > 0) {
					processFilure++;
					totalProcessFailure++;

				} 
				if (testCase.getAssertionsRun() > 0) {
					success = success + testCase.getAssertionsRun();
					
				}
				totaltests++;
			}
			totalsuccess = totalsuccess+success;
			totalfailure = totalfailure + failure;
			processFileBuilder.append("    Success : " + success
					+ " 	Failure : " + failure + "	Errors : " + processFilure);
			builder.append(processFileBuilder.toString());

		}
		
		builder.append("\n\nResults \n");
		builder.append("Success : " + totalsuccess + "    Failure : "
				+ totalfailure + "    Errors : " + totalProcessFailure);
		BWTestConfig.INSTANCE.getLogger().info(builder.toString());
		if (totalfailure > 0) {
			finalResult = totalfailure;
		} else {
			finalResult = totalProcessFailure;
		}

		return finalResult;

	}

	
	private void printFailureDetails(TestCaseResultDTO testcase,String testCaseFile, String subProcessName, String testSuiteName) {
		for(int k = 0 ; k < testcase.getAssertionResult().size() ; k++){
			AssertionResultDTO assertion =  (AssertionResultDTO) testcase.getAssertionResult().get(k);
			if(!"passed".equals(assertion.getAssertionStatus())) {
				String inputValue = assertion.getActivityOutput();
				if(assertion.getAssertionMode().equals("Primitive") ){
					if (inputValue.startsWith(assertion.getStartElementNameTag())) {
						inputValue = StringUtils.substringBetween(inputValue, assertion.getStartElementNameTag(), assertion.getEndElementNameTag());
						inputValue = inputValue!=null? inputValue:assertion.getActivityOutput();
						if(inputValue!= null && inputValue.contains(assertion.getStartElementNameTag())){
							inputValue = StringUtils.substringAfter(inputValue, assertion.getStartElementNameTag());
						}
					    if(null != assertion.getStartElementNameTag() && null != assertion.getEndElementNameTag()) {
					    	inputValue = assertion.getStartElementNameTag().concat(inputValue!= null ? inputValue : "").concat(assertion.getEndElementNameTag());
					    	assertion.setActivityOutput(inputValue);
					    }
					}
				    else {
						String goldinput = assertion.getGoldInput();
						if (goldinput != null) {
							if (goldinput.startsWith("<")) {
								inputValue = StringUtils.substringBetween(goldinput, "<", ">");
								String goldInput = StringUtils.substringBetween(goldinput, ">", "<");
								assertion.setGoldInput(goldInput);
							}
						}
						assertion.setActivityOutput(inputValue);
				    }
				}
				else{
					inputValue = assertion.getActivityOutput();
				}

				StringBuilder assertionFileBuilder = new StringBuilder();
				assertionFileBuilder.append("-----------------------------------------------------Fault Data---------------------------------------------------------------------\n");
				assertionFileBuilder.append(" Assertion Failed For Activity with name "+"["+assertion.getActivityName()+"]");
				assertionFileBuilder.append(" in Process/Sub-Process ["+subProcessName+"]");
				if(null != testSuiteName && !testSuiteName.isEmpty()){
					assertionFileBuilder.append(" in TestSuite ["
							+ testSuiteName + "]");	
				}
				assertionFileBuilder.append(" for TestCase File ["+testCaseFile+"]");
				assertionFileBuilder.append(" [Reason] - Validation failed against Gold file. Please compare Activity output against Gold output values");
				assertionFileBuilder.append("\n");

				String cause = null;
				if (isXmlContent(inputValue)) {
					cause = doXmlDiff(inputValue, assertion.getGoldInput());
				}
				if (cause != null) {
					assertionFileBuilder.append(" Potential Cause -->  ");
					assertionFileBuilder.append(cause);
					assertionFileBuilder.append("\n");
				}
				else {
					assertionFileBuilder.append(" [Activity Output:  "+inputValue+"]");
					assertionFileBuilder.append(" [Gold Output:  "+assertion.getGoldInput()+"]");
					assertionFileBuilder.append("\n");					
				}

				BWTestConfig.INSTANCE.getLogger().error(assertionFileBuilder.toString());
			}
		}
	}
		
	


	private boolean isXmlContent(String inputValue) {
		if (inputValue != null && inputValue.startsWith("<"))
			return true;
		return false;
	}


	private String doXmlDiff(String inputValue, String goldInput) {
        
        Diff myDiff;
		try {
			myDiff = DiffBuilder
			  .compare(inputValue)
			  .withTest(goldInput)
			  .ignoreComments()
			  .ignoreWhitespace()
			  .withComparisonController(ComparisonControllers.StopWhenDifferent)
			   .build();
		} catch (Exception e) {
			return null;
		}
        
        Iterator<Difference> iter = myDiff.getDifferences().iterator();
        int size = 0;
        StringBuilder result = new StringBuilder();
        while (iter.hasNext()) {
        	result.append(iter.next().toString());
            result.append(System.lineSeparator() );
            size++;
        }
        
        return result.toString();
	}


	static File getReportFile(File reportsDirectory , String moduleName , String processName) {
		String fileName =  moduleName + "-" + processName + "." + "txt";
		return new File(reportsDirectory, fileName);
	}
	
	
	 private void writeProcessResult( String moduleName , TestSetResultDTO testset , String report )
	    {
	        File reportFile = getReportFile( reportDir , moduleName ,   testset.getProcessName()  );

	        PrintWriter writer = null;
	        try
	        {
	            Writer encodedStream = new OutputStreamWriter( new FileOutputStream( reportFile ), "UTF-8" );

	            writer = new PrintWriter( new BufferedWriter( encodedStream, 16 * 1024 ) );

	            writer.print( report );

	            writer.flush();
	        
	        }
	        catch ( Exception e )
	        {
	            e.printStackTrace();
	        }
	        finally 
	        {
	        	if ( writer != null )
	            {
	                writer.close();
	            }
	        }
	    }
	
	
}

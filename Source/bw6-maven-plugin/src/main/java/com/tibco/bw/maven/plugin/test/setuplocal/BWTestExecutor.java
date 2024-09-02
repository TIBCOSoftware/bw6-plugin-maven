package com.tibco.bw.maven.plugin.test.setuplocal;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectDependenciesResolver;

import com.tibco.bw.maven.plugin.test.helpers.BWMFileParser;
import com.tibco.bw.maven.plugin.test.helpers.BWTestConfig;
import com.tibco.bw.maven.plugin.test.helpers.TestFileParser;
import com.tibco.bw.maven.plugin.test.rest.BWTestRunner;
import com.tibco.bw.maven.plugin.testsuite.BWTestSuiteLoader;
import com.tibco.bw.maven.plugin.utils.BWFileUtils;



public class BWTestExecutor 
{
	public static BWTestExecutor INSTANCE = new BWTestExecutor();
	
	int engineDebugPort;
	int engineStartupWaitTime;
	List<String> osgiCommands;
	boolean skipInitMainProcessActivities;
	boolean skipInitAllNonTestProcessActivities;
	boolean independentComponentStartup;
	boolean skippedTestError;
	String customArgEngine;
	MavenSession session;
	ProjectDependenciesResolver resolver;
	

	
	List<String> mockActivity = new ArrayList<String>();
	
	public BWTestExecutor() {
		
	}
	
	public BWTestExecutor(MavenSession session, ProjectDependenciesResolver resolver) {
		this.session = session;
		this.resolver = resolver;
	}

	public void execute() throws MojoFailureException , Exception
	{
		try
		{
			
			initialize();
			
			runEngine();
			
			runTests();
			
			
			
    		
		}
		catch(Exception e )
		{
			throw e;
		}
		finally
		{
			if( BWTestConfig.INSTANCE.getEngineProcess() != null )
			{
				BWTestConfig.INSTANCE.getEngineProcess().destroyForcibly();
			}
			if( BWTestConfig.INSTANCE.getConfigDir() != null )
			{
				BWTestConfig.INSTANCE.getConfigDir().delete();
			}
			if( BWTestConfig.INSTANCE.getESMDirectories() != null )
			{
				for(File ESMDirectory :BWTestConfig.INSTANCE.getESMDirectories() ){
				FileUtils.cleanDirectory(ESMDirectory);
				ESMDirectory.delete();
				}
			}
			
		}
		
	}
	
	/**
	 * This method is used to collect the all mock activities and subprocess whose activities "init()" lifecycle wants
	 * to skip at runtime. 
	 * @throws Exception
	 * @throws FileNotFoundException
	 */
	public void collectSkipInitActivityName() throws Exception,FileNotFoundException
	{
		List<MavenProject> projects = BWTestConfig.INSTANCE.getSession().getProjects();
		
		//extract the ESM 
		if(BWTestConfig.INSTANCE.getRunESMTest() == true){
			ESMTestFile esmTestFile = new ESMTestFile();
			esmTestFile.extractESM();
		}
		
		//user provided testsuite names in mvn goal
		if(null != BWTestConfig.INSTANCE.getTestSuiteName() && !BWTestConfig.INSTANCE.getTestSuiteName().isEmpty())
		{
			String[] testSuiteNames;
			
			if(BWTestConfig.INSTANCE.getTestSuiteName().contains(";")){
				testSuiteNames = StringUtils.splitByWholeSeparator(BWTestConfig.INSTANCE.getTestSuiteName(), ";");
			}
			else{
				testSuiteNames = new String []{BWTestConfig.INSTANCE.getTestSuiteName()};
			}
			Map<String,Boolean> userTestSuiteNames = new HashMap<String, Boolean>();
			for(String testSuite : testSuiteNames)
				userTestSuiteNames.put(testSuite, false);
			
			BWTestConfig.INSTANCE.setUserTestSuiteNames(userTestSuiteNames);
		}
		
		for( MavenProject project : projects )
		{
			if( project.getPackaging().equals("bwmodule") )
			{
				List<File> files;
				File baseDir = project.getBasedir();
				if(!BWTestConfig.INSTANCE.getUserTestSuiteNames().isEmpty()){
					BWTestSuiteLoader testSuiteLoader = new BWTestSuiteLoader();
					files = 	testSuiteLoader.collectTestCasesList(baseDir.toString(), project);
				}
				else{
					files = BWFileUtils.getEntitiesfromLocation( baseDir.toString() , "bwt");

				}
				BWTestConfig.INSTANCE.setTestCasesList(project, files);
				for( File file : files )
				{
					HashSet<String> tempSkipSet = new HashSet<String>();
					String assertionxml = FileUtils.readFileToString( file );
					tempSkipSet = TestFileParser.INSTANCE.collectSkipInitActivities(assertionxml);
					if(!tempSkipSet.isEmpty()){
						mockActivity.addAll(tempSkipSet);
						BWTestExecutor.INSTANCE.setMockActivityList(mockActivity);;
						
					}

				}
				
				//skip init for main processes
				List<File> bwmfiles = BWFileUtils.getEntitiesfromLocation( baseDir.toString() , "bwm");
				for( File file : bwmfiles )
				{
					BWTestConfig.INSTANCE.getLogger().debug("Parsing bwm file -> "+ file.getPath());
					HashSet<String> tempSkipSet = new HashSet<String>();
					String scaxml = FileUtils.readFileToString( file );
					tempSkipSet = BWMFileParser.INSTANCE.collectMainProcesses(scaxml);
					if(!tempSkipSet.isEmpty()){
						mockActivity.addAll(tempSkipSet);
						BWTestExecutor.INSTANCE.setMockActivityList(mockActivity);;
						
					}
				}
			}
		}
		
		//user provided ESM testsuite names in mvn goal
		
				if(BWTestConfig.INSTANCE.getRunESMTest() == true && null != BWTestConfig.INSTANCE.getEsmTestSuiteName() && !BWTestConfig.INSTANCE.getEsmTestSuiteName().isEmpty())
				{
					String[] esmTestSuiteNames;
					
					if(BWTestConfig.INSTANCE.getEsmTestSuiteName().contains("/")){
						esmTestSuiteNames = StringUtils.splitByWholeSeparator(BWTestConfig.INSTANCE.getEsmTestSuiteName(), "/");
					}
					else{
						esmTestSuiteNames = new String []{BWTestConfig.INSTANCE.getEsmTestSuiteName()};
					}
					Map<String,Boolean> userTestSuiteNames = new HashMap<String, Boolean>();
					for(String testSuite : esmTestSuiteNames)
						userTestSuiteNames.put(testSuite, false);
					
					BWTestConfig.INSTANCE.setUserESMTestSuiteNames(userTestSuiteNames);
				}
				
				//Collect The Test Cases for External Shared Module
				
				if(BWTestConfig.INSTANCE.getRunESMTest() == true){
					List<File> ESMbwtFiles = null;

					
						for(File esmFile : BWTestConfig.INSTANCE.getESMDirectories() ){

							
							String esmProjectPath = esmFile.getAbsolutePath();
							if(!BWTestConfig.INSTANCE.getUserESMTestSuiteNames().isEmpty()){
								BWTestSuiteLoader testSuiteLoader = new BWTestSuiteLoader();
								ESMbwtFiles = 	testSuiteLoader.collectTestCasesListFromESM(esmProjectPath);
							}else{
								ESMbwtFiles = BWFileUtils.getEntitiesfromLocation( esmProjectPath , "bwt");
								BWTestConfig.INSTANCE.setEsmTestCasesList(esmProjectPath,ESMbwtFiles);
							
							}
							if(null !=ESMbwtFiles && ESMbwtFiles.isEmpty()){
								Map<String, Map<String, List<File>>> testSuiteMap = BWTestConfig.INSTANCE.getEsmTestSuites();
								
								if(null == testSuiteMap.get(esmFile)){
									ESMbwtFiles = BWFileUtils.getEntitiesfromLocation( esmProjectPath , "bwt");
									BWTestConfig.INSTANCE.setEsmTestCasesList(esmProjectPath,ESMbwtFiles);
								}
								
							}
						
							for( File file : ESMbwtFiles )
							{
								HashSet<String> tempSkipSet = new HashSet<String>();
								String assertionxml = FileUtils.readFileToString( file );
								tempSkipSet = TestFileParser.INSTANCE.collectSkipInitActivities(assertionxml);
								if(!tempSkipSet.isEmpty()){
									mockActivity.addAll(tempSkipSet);
									BWTestExecutor.INSTANCE.setMockActivityList(mockActivity);;

								}

							
							}

						}
					}
		
		//throw error if test suite not found in any module
		if(BWTestConfig.INSTANCE.getUserTestSuiteNames().containsValue(false))
		{
			for(Map.Entry<String, Boolean> testSuite : BWTestConfig.INSTANCE.getUserTestSuiteNames().entrySet())
				if(testSuite.getValue() == false)
					throw new Exception("Test Suite not found - "+ testSuite.getKey());
		}
	}
	
	private void initialize() throws Exception
	{
		
		collectSkipInitActivityName();
		EngineLaunchConfigurator config = new EngineLaunchConfigurator();
		config.loadConfiguration();
		
		ConfigFileGenerator gen = new ConfigFileGenerator(session, resolver);
		gen.generateConfig();
		
		
	}
	

	private void runEngine() throws Exception
	{
		EngineRunner runner = new EngineRunner(BWTestExecutor.INSTANCE.getEngineStartupWaitTime(), BWTestExecutor.INSTANCE.getOsgiCommands());
		runner.run();
	}
	
	private void runTests() throws MojoFailureException , Exception
	{
		BWTestRunner runner = new BWTestRunner("localhost", BWTestExecutor.INSTANCE.getEngineDebugPort() );
		runner.runTests();
	}
	
	public void setEngineDebugPort(int engineDebugPort){
		this.engineDebugPort = engineDebugPort;
	}

	public int getEngineDebugPort(){
		return engineDebugPort;
	}
	
	public List<String> getMockActivityList() {
		return mockActivity;
	}

	public void setMockActivityList(List<String> mockActivity) {
		this.mockActivity.addAll(mockActivity);
	}
	
	public int getEngineStartupWaitTime() {
		return engineStartupWaitTime;
	}

	public void setEngineStartupWaitTime(int engineStartupWaitTime) {
		this.engineStartupWaitTime = engineStartupWaitTime;
	}

	public List<String> getOsgiCommands() {
		return osgiCommands;
	}

	public void setOsgiCommands(List<String> osgiCommands) {
		this.osgiCommands = osgiCommands;
	}

	public boolean isSkipInitMainProcessActivities() {
		return skipInitMainProcessActivities;
	}

	public void setSkipInitMainProcessActivities(
			boolean skipInitMainProcessActivities) {
		this.skipInitMainProcessActivities = skipInitMainProcessActivities;
	}

	public boolean isSkipInitAllNonTestProcessActivities() {
		return skipInitAllNonTestProcessActivities;
	}

	public void setSkipInitAllNonTestProcessActivities(
			boolean skipInitAllNonTestProcessActivities) {
		this.skipInitAllNonTestProcessActivities = skipInitAllNonTestProcessActivities;
	}
	
	public boolean isIndependentComponentStartup() {
		return independentComponentStartup;
	}
	
	public void setIndependentComponentStartup(boolean independentComponentStartup) {
		this.independentComponentStartup = independentComponentStartup;
	}
	
	public String getCustomArgEngine() {
		return customArgEngine;
	}

	public void setCustomArgEngine(String customArgEngine) {
		this.customArgEngine = customArgEngine;
	}

	
	public boolean isSkippedTestError() {
		return skippedTestError;
	}
	
	public void setSkippedTestError(boolean skippedTestError) {
		this.skippedTestError = skippedTestError;
	}
	
	
}

package com.tibco.bw.maven.plugin.test.helpers;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;

public class BWTestConfig 
{

	public static BWTestConfig INSTANCE = new BWTestConfig();

	private File configDir;

	private Process engineProcess;
	
	private List<String> launchConfig;
	
	private Map<MavenProject,List<File>> testCasesList = new HashMap<>();
	
	private Map<MavenProject,Map<String,List<File>>> testSuiteMap = new HashMap<>();

	private String tibcoHome;
	
	private String bwHome;
	
	private String testSuiteName;
	
	private MavenSession session;
	
	private MavenProject project;
	
	private Log logger;
	
	public HashMap<String,String> testCaseWithProcessNameMap = new HashMap<>(); 
	
	public Map<MavenProject,List<String>> testSuiteNameList = new HashMap<>();
	
	private BWTestConfig()
	{
		
	}
	
	public  void reset()
	{
		INSTANCE = new BWTestConfig();
	}
	
	public void init( String tibcoHome , String bwHome , MavenSession session , MavenProject project , Log logger ) throws Exception 
	{
		this.tibcoHome = tibcoHome;
		this.session = session;
		this.project = project;
		this.bwHome = bwHome;
		this.logger = logger;
		
		initConfig();
	}
	
	private void initConfig() throws Exception
	{
		String temp = System.getProperty( "java.io.tmpdir" );
		File file = new File( temp );
		
		configDir = new File( file , "bwconfig");
		
		configDir.mkdir();
		
		FileUtils.cleanDirectory(configDir );
	}

	public File getConfigDir() 
	{
		return configDir;
	}

	public void setConfigDir(File configDir) 
	{
		this.configDir = configDir;
	}

	public Process getEngineProcess() 
	{
		return engineProcess;
	}

	public void setEngineProcess(Process engineProcess)
	{
		this.engineProcess = engineProcess;
	}

	public List<String> getLaunchConfig() 
	{
		return launchConfig;
	}

	public void setLaunchConfig(List<String> launchConfig)
	{
		this.launchConfig = launchConfig;
	}

	public String getTibcoHome() 
	{
		return tibcoHome;
	}

	public void setTibcoHome(String tibcoHome) 
	{
		this.tibcoHome = tibcoHome;
	}

	public String getBwHome() 
	{
		return bwHome;
	}

	public void setBwHome(String bwHome)
	{
		this.bwHome = bwHome;
	}

	public MavenSession getSession()
	{
		return session;
	}

	public void setSession(MavenSession session) 
	{
		this.session = session;
	}

	public MavenProject getProject() 
	{
		return project;
	}

	public void setProject(MavenProject project) 
	{
		this.project = project;
	}

	public Log getLogger() 
	{
		return logger;
	}

	public void setLogger(Log logger) 
	{
		this.logger = logger;
	}

	public String getTestSuiteName() {
		return testSuiteName;
	}

	public void setTestSuiteName(String testSuiteName) {
		this.testSuiteName = testSuiteName;
	}

	public List<File> getTestCasesList(MavenProject project) {
		return testCasesList.get(project);
	}

	public void setTestCasesList(MavenProject project, List<File> testCasesList) {
		this.testCasesList.put(project, testCasesList);
	}

	public Map<String, List<File>> getTestSuiteMap(MavenProject project) {
		return testSuiteMap.get(project);
	}

	public void setTestSuiteMap(MavenProject project, Map<String, List<File>> testSuiteMap) {
		this.testSuiteMap.put(project, testSuiteMap);
	}

	public HashMap<String, String> getTestCaseWithProcessNameMap() {
		return testCaseWithProcessNameMap;
	}

	public void setTestCaseWithProcessNameMap(
			HashMap<String, String> testCaseWithProcessNameMap) {
		this.testCaseWithProcessNameMap = testCaseWithProcessNameMap;
	}
	
	public List<String> getTestSuiteNameList(MavenProject project) {
		return testSuiteNameList.get(project);
	}

	public void setTestSuiteNameList(MavenProject project, List<String> testSuiteNameList) {
		this.testSuiteNameList.put(project, testSuiteNameList);
	}
	
}

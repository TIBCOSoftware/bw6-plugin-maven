package com.tibco.bw.maven.plugin.test.helpers;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectDependenciesResolver;

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
	
	ProjectDependenciesResolver resolver;
	
	private Log logger;
	
	private Set<File> ESMDirectories = new HashSet<>();
	
	private boolean runESMTest;
	
	private String esmTestSuiteName;
	
	public HashMap<String,String> testCaseWithProcessNameMap = new HashMap<>(); 
	
	public Map<MavenProject,List<String>> testSuiteNameList = new HashMap<>();
	
	Map<String,Boolean> userTestSuiteNames = new HashMap<String,Boolean>();
	
	private Map<String,List<File>> esmTestCasesList = new HashMap<>();
	
	public HashMap<String,Map<String,List<File>>> esmTestSuiteMap = new HashMap<>(); 
	
	public Map<String,List<String>> esmTestSuiteNameList = new HashMap<>();
	
	Map<String,Boolean> userESMTestSuiteNames = new HashMap<String,Boolean>();
	
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

	public Map<String, Boolean> getUserTestSuiteNames() {
		return userTestSuiteNames;
	}

	public void setUserTestSuiteNames(Map<String, Boolean> userTestSuiteNames) {
		this.userTestSuiteNames = userTestSuiteNames;
	}

	public ProjectDependenciesResolver getResolver() {
		return resolver;
	}

	public void setResolver(ProjectDependenciesResolver resolver) {
		this.resolver = resolver;
	}

	
	public String getEsmTestSuiteName() {
		return esmTestSuiteName;
	}

	public void setEsmTestSuiteName(String esmTestSuiteName) {
		this.esmTestSuiteName = esmTestSuiteName;
	}
	public Set<File> getESMDirectories() {
		return ESMDirectories;
	}

	public void setESMDirectories(File ESMDir) {
		this.ESMDirectories.add(ESMDir);
	}

	public List<File> getEsmTestCasesList(String fileName) {
		return esmTestCasesList.get(fileName);
	}

	public void setEsmTestCasesList(String dirName , List<File> file) {
		esmTestCasesList.put(dirName, file);
	}
	
	public boolean getRunESMTest() {
		return runESMTest;
	}

	public void setRunESMTest(boolean runESMTest) {
		this.runESMTest = runESMTest;
	}
	
	public Map<String, Boolean> getUserESMTestSuiteNames() {
		return userESMTestSuiteNames;
	}

	public void setUserESMTestSuiteNames(Map<String, Boolean> userESMTestSuiteNames) {
		this.userESMTestSuiteNames = userESMTestSuiteNames;
	}

	public  List<String> getEsmTestSuiteNameList(String esmDir) {
		return esmTestSuiteNameList.get(esmDir);
	}

	public void setEsmTestSuiteNameList(String esmDir, List<String> esmTestSuiteList) {
		esmTestSuiteNameList.put(esmDir, esmTestSuiteList);
	}
	
	public Map <String, List<File>> getEsmTestSuiteMap(String esmDir) {
		return esmTestSuiteMap.get(esmDir);
	}
	
	public HashMap<String, Map<String, List<File>>> getEsmTestSuites() {
		return esmTestSuiteMap;
	}


	public void setEsmTestSuiteMap(String esmDir, Map<String, List<File>> esmTestSuite) {
		this.esmTestSuiteMap.put(esmDir, esmTestSuite);
	}


	
}

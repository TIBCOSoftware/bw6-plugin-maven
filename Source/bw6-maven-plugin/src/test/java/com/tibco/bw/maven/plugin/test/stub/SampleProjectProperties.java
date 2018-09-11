package com.tibco.bw.maven.plugin.test.stub;

import java.io.File;

import com.tibco.bw.plugin.tests.TestAGitSampleProject;

public class SampleProjectProperties {

	//public static Properties prop = new Properties();
	private static File repopath;
	private File modulepath;
	private File applicationpath;
	private File sharedmodulepath;
	//private String propFileName = "TestCaseProperties.properties";	
	 
	public SampleProjectProperties() {
		//prop.load(inputStream);
		repopath = TestAGitSampleProject.gitRepo;
		this.modulepath = new File(repopath+"/TestProjects/Test/");
		this.applicationpath = new File(repopath+"/TestProjects/Test.application/");
		this.sharedmodulepath = new File(repopath+"/TestProjects/TestSharedModule/"); 
	}
	
	
	public File getApplicationpath() {
		return applicationpath;
	}



	public File getModulepath() {
		return modulepath;
	}
	
	public File getSharedModulepath() {
		return sharedmodulepath;
	}

	
}

package com.tibco.bw.maven.plugin.test.rest;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.maven.project.MavenProject;

import com.tibco.bw.maven.plugin.test.dto.TestSuiteDTO;
import com.tibco.bw.maven.plugin.test.helpers.BWTestConfig;
import com.tibco.bw.maven.plugin.test.helpers.TestFileParser;
import com.tibco.bw.maven.plugin.testsuite.BWTestSuiteLoader;
import com.tibco.bw.maven.plugin.utils.BWFileUtils;

public class AssertionsLoader 
{

	MavenProject project;
	
	  File esmFile;
	
	public AssertionsLoader( MavenProject project)
	{
		
		this.project = project;
	}
	
	public AssertionsLoader(File file)
		{
			this.esmFile = file;
	 	}
	 
	
	@SuppressWarnings({ "unused", "rawtypes" })
	public TestSuiteDTO loadAssertions() throws Exception
	{
		List<File> files = getAssertionsFromProject();
		Map<String, List<File>> testSuiteMap = BWTestConfig.INSTANCE.getTestSuiteMap(this.project);
		TestSuiteDTO suite = new TestSuiteDTO();
		List testCaseList = new ArrayList();
		BWTestConfig.INSTANCE.getLogger().info("");
		//BWTestConfig.INSTANCE.getLogger().info("-----------------BW Engine Logs End--------------------");

		if(null != BWTestConfig.INSTANCE.getTestSuiteName() && !BWTestConfig.INSTANCE.getTestSuiteName().isEmpty()){
			for (String suiteName : BWTestConfig.INSTANCE.getTestSuiteNameList(this.project)){
				BWTestConfig.INSTANCE.getLogger().info("");
				BWTestConfig.INSTANCE.getLogger().info(" ## Running Test Suite "+ suiteName + " ##");
				for( File file : testSuiteMap.get(suiteName) ){
					
					BWTestConfig.INSTANCE.getLogger().info("      Running Test for "+ file.getName());

					String assertionxml = FileUtils.readFileToString( file );

					try {
						TestFileParser.INSTANCE.collectAssertions(assertionxml , suite ,project.getBasedir().getAbsolutePath());
					} catch (Exception e) {
						BWTestConfig.INSTANCE.getLogger().info("## ERRORS in collecting assertions  - Will skip running Test for "+file.getName()+" ##");
						BWTestConfig.INSTANCE.getLogger().info("## Check the error indicated by following exception stack ##");
						e.printStackTrace();
					}
				}
			}
			return suite;
		}
		else{
			for( File file : files )
			{
				BWTestConfig.INSTANCE.getLogger().info("## Running Test for "+file.getName()+" ##");

				String assertionxml = FileUtils.readFileToString( file );

				try {
					TestFileParser.INSTANCE.collectAssertions(assertionxml , suite ,project.getBasedir().getAbsolutePath());
				} catch (Exception e) {
					BWTestConfig.INSTANCE.getLogger().info("## ERRORS in collecting assertions  - Will skip running Test for "+file.getName()+" ##");
					BWTestConfig.INSTANCE.getLogger().info("## Check the error indicated by following exception stack ##");
					e.printStackTrace();
				}

			}

			return suite;
		}
	}
	
	public TestSuiteDTO loadAssertionsFromESM() throws Exception
	{
		TestSuiteDTO suite = new TestSuiteDTO();
		String filePath = esmFile.getAbsolutePath();
		Map<String, List<File>> testSuiteMap = BWTestConfig.INSTANCE.getEsmTestSuiteMap(filePath);
		
		if(null !=testSuiteMap && !testSuiteMap.isEmpty()){
			for (String suiteName : BWTestConfig.INSTANCE.getEsmTestSuiteNameList(filePath)){
				BWTestConfig.INSTANCE.getLogger().info("");
				BWTestConfig.INSTANCE.getLogger().info(" ## Running Test Suite ["+ suiteName+"]" +" From ESM ["+ esmFile.getName()  + "] ##");
				for( File file : testSuiteMap.get(suiteName) ){
					
					BWTestConfig.INSTANCE.getLogger().info("      Running Test for "+ file.getName());

					String assertionxml = FileUtils.readFileToString( file );

					TestFileParser.INSTANCE.collectAssertions(assertionxml , suite ,filePath);
				}
			}
			return suite;
		}else{
			List<File> files = getAssertionsFromESM();
			for( File file : files )
			{
				BWTestConfig.INSTANCE.getLogger().info("");

				
				BWTestConfig.INSTANCE.getLogger().info("## Running Test for "+file.getName()+" ##");

				String assertionxml = FileUtils.readFileToString( file );

				TestFileParser.INSTANCE.collectAssertions(assertionxml , suite ,filePath);

			}

			return suite;
		}
		
	}

	
	private List<File> getAssertionsFromProject()
	{
			return BWTestConfig.INSTANCE.getTestCasesList(this.project);
	
	}
	
	private List<File> getAssertionsFromESM()
	{
			return BWTestConfig.INSTANCE.getEsmTestCasesList(esmFile.getAbsolutePath());
	
	}
	
}

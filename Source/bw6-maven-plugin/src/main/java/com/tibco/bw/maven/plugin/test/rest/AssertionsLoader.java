package com.tibco.bw.maven.plugin.test.rest;

import java.io.File;
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
	
	public AssertionsLoader( MavenProject project)
	{
		
		this.project = project;
	}
	 
	
	@SuppressWarnings({ "unused", "rawtypes" })
	public TestSuiteDTO loadAssertions() throws Exception
	{
		List<File> files = getAssertionsFromProject();
		Map<String, List<File>> testSuiteMap = BWTestConfig.INSTANCE.getTestSuiteMap(this.project);
		TestSuiteDTO suite = new TestSuiteDTO();
		List testCaseList = new ArrayList();
		BWTestConfig.INSTANCE.getLogger().info("");
		BWTestConfig.INSTANCE.getLogger().info("-----------------BW Engine Logs End--------------------");

		if(null != BWTestConfig.INSTANCE.getTestSuiteName() && !BWTestConfig.INSTANCE.getTestSuiteName().isEmpty()){
			for (String suiteName : BWTestConfig.INSTANCE.getTestSuiteNameList(this.project)){
				BWTestConfig.INSTANCE.getLogger().info("");
				BWTestConfig.INSTANCE.getLogger().info(" ## Running Test Suite "+ suiteName + " ##");
				for( File file : testSuiteMap.get(suiteName) ){
					
					BWTestConfig.INSTANCE.getLogger().info("      Running Test for "+ file.getName());

					String assertionxml = FileUtils.readFileToString( file );

					TestFileParser.INSTANCE.collectAssertions(assertionxml , suite ,project.getBasedir().getAbsolutePath());
				}
			}
			return suite;
		}
		else{
			for( File file : files )
			{
				BWTestConfig.INSTANCE.getLogger().info("## Running Test for "+file.getName()+" ##");

				String assertionxml = FileUtils.readFileToString( file );

				TestFileParser.INSTANCE.collectAssertions(assertionxml , suite ,project.getBasedir().getAbsolutePath());

			}

			return suite;
		}
	}
	
	private List<File> getAssertionsFromProject()
	{
			return BWTestConfig.INSTANCE.getTestCasesList(this.project);
	
	}
	
}

package com.tibco.bw.maven.plugin.test.rest;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.maven.project.MavenProject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tibco.bw.maven.plugin.test.dto.TestSuiteDTO;
import com.tibco.bw.maven.plugin.test.helpers.BWTestConfig;
import com.tibco.bw.maven.plugin.test.helpers.TestFileParser;
import com.tibco.bw.maven.plugin.test.setuplocal.EngineRunner;
import com.tibco.bw.maven.plugin.utils.BWFileUtils;

public class AssertionsLoader 
{

	MavenProject project;
	private final Logger logger = LoggerFactory.getLogger(AssertionsLoader.class);
	
	public AssertionsLoader( MavenProject project)
	{
		
		this.project = project;
	}
	 
	
	@SuppressWarnings({ "unused", "rawtypes" })
	public TestSuiteDTO loadAssertions() throws Exception
	{
		List<File> files = getAssertionsFromProject();
		TestSuiteDTO suite = new TestSuiteDTO();
		List testCaseList = new ArrayList();
		for( File file : files )
		{
			logger.info("## Running Test for "+file.getName()+" ##");
			String assertionxml = FileUtils.readFileToString( file );
			
			TestFileParser.INSTANCE.collectAssertions(assertionxml , suite);
			
		}
		
		return suite;
	}
	
	private List<File> getAssertionsFromProject()
	{
		File baseDir = project.getBasedir();
		List<File> files = BWFileUtils.getEntitiesfromLocation( baseDir.toString() , "bwt");
		
		return files;
		
		
		
	}
	
}

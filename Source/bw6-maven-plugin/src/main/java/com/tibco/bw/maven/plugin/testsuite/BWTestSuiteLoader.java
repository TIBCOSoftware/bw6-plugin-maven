package com.tibco.bw.maven.plugin.testsuite;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.maven.project.MavenProject;

import com.tibco.bw.maven.plugin.test.helpers.BWTestConfig;
import com.tibco.bw.maven.plugin.utils.BWFileUtils;

public class BWTestSuiteLoader {

	public List<File> collectTestCasesList(String baseDir, MavenProject project) throws IOException{
		List<File> testSuitefile = new ArrayList<File>();
		List<String> testSuiteNamePathList = new ArrayList<String>();
		List<String> testSuiteNameList = new ArrayList<String>();
		String[] testSuiteNames;
		
		if(BWTestConfig.INSTANCE.getTestSuiteName().contains("/")){
			testSuiteNames = StringUtils.splitByWholeSeparator(BWTestConfig.INSTANCE.getTestSuiteName(), "/");
		}
		else{
			testSuiteNames = new String []{BWTestConfig.INSTANCE.getTestSuiteName()};
		}
		Arrays.asList(testSuiteNames);
		
		String testFolderPath = "";
		for(String testSuiteName : testSuiteNames)
		{
			String folderPath = BWFileUtils.getTestFolderName(baseDir.toString(),testSuiteName);
			if(null != folderPath){
					testFolderPath = folderPath;
					testSuiteNamePathList.add(folderPath.concat("//"+testSuiteName));
					testSuiteNameList.add(testSuiteName);
			}
			else{
				//throw new FileNotFoundException("Test Suite file " +testSuiteName+ " is not found");
				BWTestConfig.INSTANCE.getLogger().debug("Test Suite file " +testSuiteName+ " is not found at "+ baseDir);
			}
		}
		
		BWTestConfig.INSTANCE.setTestSuiteNameList(project, testSuiteNameList);

		BWTSFileReaderWrapper fileReader = new BWTSFileReaderWrapper();
		testSuitefile = fileReader.readBWTSFile(testSuiteNamePathList,testFolderPath, project);
		return testSuitefile;



	}
	
}

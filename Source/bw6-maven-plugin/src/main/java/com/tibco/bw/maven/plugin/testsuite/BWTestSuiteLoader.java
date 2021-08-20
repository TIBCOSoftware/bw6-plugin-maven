package com.tibco.bw.maven.plugin.testsuite;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.maven.project.MavenProject;

import com.tibco.bw.maven.plugin.test.helpers.BWTestConfig;
import com.tibco.bw.maven.plugin.utils.BWFileUtils;

public class BWTestSuiteLoader {

	public List<File> collectTestCasesList(String baseDir, MavenProject project) throws IOException{
		List<File> testSuitefile = new ArrayList<File>();
		List<String> testSuiteNamePathList = new ArrayList<String>();
		List<String> testSuiteNameList = new ArrayList<String>();
		
		String testFolderPath = "";
		for(String testSuiteName : BWTestConfig.INSTANCE.getUserTestSuiteNames().keySet())
		{
			String folderPath = BWFileUtils.getTestFolderName(baseDir.toString(),testSuiteName);
			if(null != folderPath){
					testFolderPath = folderPath;
					testSuiteNamePathList.add(folderPath.concat("//"+testSuiteName));
					testSuiteNameList.add(testSuiteName);
					BWTestConfig.INSTANCE.getUserTestSuiteNames().replace(testSuiteName, true);
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
	
	public List<File> collectTestCasesListFromESM(String baseDir) throws IOException{
		List<File> testSuitefile = new ArrayList<File>();
		List<String> testSuiteNamePathList = new ArrayList<String>();
		List<String> testSuiteNameList = new ArrayList<String>();
		
		String testFolderPath = "";
		for(String testSuiteName : BWTestConfig.INSTANCE.getUserESMTestSuiteNames().keySet())
		{
			String folderPath = BWFileUtils.getTestFolderName(baseDir,testSuiteName);
			if(null != folderPath){
					testFolderPath = folderPath;
					testSuiteNamePathList.add(folderPath.concat("//"+testSuiteName));
					testSuiteNameList.add(testSuiteName);
					BWTestConfig.INSTANCE.getUserTestSuiteNames().replace(testSuiteName, true);
			}
			else{
				//throw new FileNotFoundException("Test Suite file " +testSuiteName+ " is not found");
				BWTestConfig.INSTANCE.getLogger().debug("Test Suite file " +testSuiteName+ " is not found at "+ baseDir);
			}
		}
		
		BWTestConfig.INSTANCE.setEsmTestSuiteNameList(baseDir, testSuiteNameList);

		BWTSFileReaderWrapper fileReader = new BWTSFileReaderWrapper();
		testSuitefile = fileReader.readBWTSFileFromESM(testSuiteNamePathList,testFolderPath, baseDir);
		return testSuitefile;



	}
	
}

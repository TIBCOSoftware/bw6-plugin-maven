package com.tibco.bw.maven.plugin.testsuite;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.project.MavenProject;

import com.tibco.bw.maven.plugin.test.helpers.BWTestConfig;

public class BWTSFileReaderWrapper {
	
	
	public List<File>	readBWTSFile(List<String> testSuiteList , String TestFolderPath, MavenProject project, String testFoldername) throws IOException{
		List<File> returnList = new ArrayList<>();
		List<File> tempReturnList ;
		HashMap<String,List<File>> testSuiteMap = new HashMap<String,List<File>>();
		try {
			for(String testSuiteName : testSuiteList){
				Path path = Paths.get(testSuiteName);
				BWTSModel bwtsModel = YMLBWTSFileReader.getModelFrom(path);
				Object testCaseList = bwtsModel.getOthers().get("testCases");
				tempReturnList = new ArrayList<>();
				//to get the path till the the project module
				String[] locationArray = TestFolderPath.split(testFoldername);
				
				if(null != testCaseList){
					for (Object obj : (ArrayList<?>)testCaseList) {
						if(obj instanceof String){
							String location = locationArray[0]+testFoldername+File.separator+FilenameUtils.separatorsToSystem((String)obj);
							returnList.add(new File(location));
							tempReturnList.add(new File(location));
						}
					}
				}
				String testSuite =  StringUtils.substringAfter(testSuiteName, "//");
				testSuiteMap.put(testSuite, tempReturnList);
			}
			
			BWTestConfig.INSTANCE.setTestSuiteMap(project, testSuiteMap);
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		}
		return returnList;

	}
	
	public List<File>	readBWTSFileFromESM(List<String> testSuiteList , String TestFolderPath, String esmDir) throws IOException{
		List<File> returnList = new ArrayList<>();
		List<File> tempReturnList ;
		HashMap<String,List<File>> testSuiteMap = new HashMap<String,List<File>>();
		try {
			for(String testSuiteName : testSuiteList){
				Path path = Paths.get(testSuiteName);
				BWTSModel bwtsModel = YMLBWTSFileReader.getModelFrom(path);
				Object testCaseList = bwtsModel.getOthers().get("testCases");
				tempReturnList = new ArrayList<>();
				if(null != testCaseList){
					for (Object obj : (ArrayList<?>)testCaseList) {
						if(obj instanceof String){
							returnList.add(new File(TestFolderPath.concat("//"+(String)obj)));
							tempReturnList.add(new File(TestFolderPath.concat("//"+(String)obj)));
						}
					}
				}
				String testSuite =  StringUtils.substringAfter(testSuiteName, TestFolderPath.concat("//"));
				testSuiteMap.put(testSuite, tempReturnList);
			}
			
			BWTestConfig.INSTANCE.setEsmTestSuiteMap(esmDir, testSuiteMap);
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		}
		return returnList;

	}


}

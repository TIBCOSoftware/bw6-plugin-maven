package com.tibco.bw.maven.plugin.testsuite;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.maven.project.MavenProject;

import com.tibco.bw.maven.plugin.test.helpers.BWTestConfig;

public class BWTSFileReaderWrapper {
	
	
	public List<File>	readBWTSFile(List<String> testSuiteList , String TestFolderPath, MavenProject project) throws IOException{
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
			
			BWTestConfig.INSTANCE.setTestSuiteMap(project, testSuiteMap);
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		}
		return returnList;

	}

}

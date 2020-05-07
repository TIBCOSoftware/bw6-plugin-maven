package com.tibco.bw.maven.plugin.test.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


@XmlType
@XmlSeeAlso({TestCaseResultDTO.class})
public class BWTestSuiteDTO  implements Serializable {
	
   /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

    private String testSuiteName;
	
    private HashMap<String,String> testCaseWithProcessNameMap = new HashMap<>(); 

	@SuppressWarnings("rawtypes")
	private List testCaseList = new ArrayList();

	@XmlElement
	public String getTestSuiteName() {
		return testSuiteName;
	}

	public void setTestSuiteName(String testSuiteName) {
		this.testSuiteName = testSuiteName;
	}

	@SuppressWarnings("rawtypes")
	@XmlElement(name="testCaseResult")
	public List getTestCaseList() {
		return testCaseList;
	}

	@SuppressWarnings("rawtypes")
	public void setTestCaseList(List testCaseList) {
		this.testCaseList = testCaseList;
	}

	@XmlElement(name="testSuiteWithProcessMap")
	public HashMap<String, String> getTestCaseWithProcessNameMap() {
		return testCaseWithProcessNameMap;
	}

	public void setTestCaseWithProcessNameMap(
			HashMap<String, String> testCaseWithProcessNameMap) {
		this.testCaseWithProcessNameMap = testCaseWithProcessNameMap;
	}
	


}

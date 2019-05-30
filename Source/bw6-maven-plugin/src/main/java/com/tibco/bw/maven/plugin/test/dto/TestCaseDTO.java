package com.tibco.bw.maven.plugin.test.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

@SuppressWarnings("serial")
@XmlType
@XmlSeeAlso({AssertionDTO.class,MockActivityDTO.class})

public class TestCaseDTO implements Serializable{


	
	private String xmlInput;
	
	private String mockOutputFilePath;
	
	private String testCaseFile;

	@SuppressWarnings("rawtypes")
	private List assertionList = new ArrayList();
	
	@SuppressWarnings("rawtypes")
	private List mockActivityList = new ArrayList();

	@XmlElement
	public String getXmlInput() 
	{
		return xmlInput;
	}

	public void setXmlInput(String xmlInput) 
	{
		this.xmlInput = xmlInput;
	}
	
	public void setmockOutputFilePath(String mockOutputFilePath) 
	{
		this.mockOutputFilePath = mockOutputFilePath;
	}
	
	@XmlElement
	public String getmockOutputFilePath() 
	{
		return mockOutputFilePath;
	}

	@SuppressWarnings("rawtypes")
	@XmlElement(name="assertionList")
	public List getAssertionList() 
	{
		return assertionList;
	}

	@SuppressWarnings("rawtypes")
	public void setAssertionList(List assertionList) {
		this.assertionList = assertionList;
	}

	@XmlElement
	public String getTestCaseFile() 
	{
		return testCaseFile;
	}

	public void setTestCaseFile(String testCaseFile) 
	{
		this.testCaseFile = testCaseFile;
	}
	
	@SuppressWarnings("rawtypes")
	@XmlElement(name="mockActivityList")
	public List getMockActivityList() 
	{
		return mockActivityList;
	}

	@SuppressWarnings("rawtypes")
	public void setMockActivityList(List mockActivityList) {
		this.mockActivityList = mockActivityList;
	}
}

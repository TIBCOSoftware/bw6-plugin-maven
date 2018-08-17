package com.tibco.bw.maven.plugin.test.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

@SuppressWarnings("serial")
@XmlType
@XmlSeeAlso({AssertionDTO.class})
public class TestCaseDTO implements Serializable{


	
	private String xmlInput;
	
	private String testCaseFile;

	@SuppressWarnings("rawtypes")
	private List assertionList = new ArrayList();

	@XmlElement
	public String getXmlInput() 
	{
		return xmlInput;
	}

	public void setXmlInput(String xmlInput) 
	{
		this.xmlInput = xmlInput;
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
}

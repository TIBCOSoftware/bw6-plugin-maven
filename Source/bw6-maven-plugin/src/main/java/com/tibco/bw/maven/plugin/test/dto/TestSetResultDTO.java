package com.tibco.bw.maven.plugin.test.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

@SuppressWarnings("serial")
@XmlType
@XmlSeeAlso({TestCaseResultDTO.class})
public class TestSetResultDTO implements Serializable
{

	private String packageName;
	
	private String processName;
	
	@SuppressWarnings("rawtypes")
	private List testCaseResult = new ArrayList();

	@XmlElement
	public String getPackageName() 
	{
		return packageName;
	}

	public void setPackageName(String packageName) 
	{
		this.packageName = packageName;
	}

	@XmlElement
	public String getProcessName() 
	{
		return processName;
	}

	public void setProcessName(String processName)
	{
		this.processName = processName;
	}

	@SuppressWarnings("rawtypes")
	@XmlElement(name="testCaseResult")
	public List getTestCaseResult() 
	{
		return testCaseResult;
	}

	@SuppressWarnings("rawtypes")
	public void setTestCaseResult(List testCaseResult) 
	{
		this.testCaseResult = testCaseResult;
	}
}

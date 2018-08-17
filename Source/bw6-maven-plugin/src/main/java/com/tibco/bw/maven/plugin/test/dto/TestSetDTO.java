package com.tibco.bw.maven.plugin.test.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

@SuppressWarnings("serial")
@XmlType
@XmlSeeAlso({TestCaseDTO.class})
public class TestSetDTO implements Serializable
{
	
	private String packageName;
	
	private String processName;

	@SuppressWarnings("rawtypes")
	private List testCaseList = new ArrayList();

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
	@XmlElement(name="testCaseList")
	public List getTestCaseList() 
	{
		return testCaseList;
	}

	@SuppressWarnings("rawtypes")
	public void setTestCaseList(List testCaseList)
	{
		this.testCaseList = testCaseList;
	} 
	
}

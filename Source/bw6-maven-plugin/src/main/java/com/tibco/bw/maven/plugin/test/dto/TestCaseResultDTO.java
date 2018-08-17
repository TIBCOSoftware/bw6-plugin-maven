package com.tibco.bw.maven.plugin.test.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

@SuppressWarnings("serial")
@XmlType
@XmlSeeAlso({AssertionResultDTO.class})
public class TestCaseResultDTO implements Serializable
{

	private String testCaseFile;
	
	@SuppressWarnings({ "rawtypes" })
	private List assertionResult = new ArrayList();
	
	private int assertions;
	
	private int assertionsRun;
	
	private int assertionFailure;
	
	private int processFailures;

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
	@XmlElement(name="assertionResult")
	public List getAssertionResult() 
	{
		return assertionResult;
	}

	@SuppressWarnings("rawtypes")
	public void setAssertionResult(List assertionResult)
	{
		this.assertionResult = assertionResult;
	}

	@XmlElement
	public int getAssertions() 
	{
		return assertions;
	}

	public void setAssertions(int assertions)
	{
		this.assertions = assertions;
	}

	@XmlElement
	public int getAssertionsRun() 
	{
		return assertionsRun;
	}

	public void setAssertionsRun(int assertionsRun) 
	{
		this.assertionsRun = assertionsRun;
	}

	@XmlElement
	public int getAssertionFailure() 
	{
		return assertionFailure;
	}

	public void setAssertionFailure(int assertionFailure)
	{
		this.assertionFailure = assertionFailure;
	}

	@XmlElement
	public int getProcessFailures() 
	{
		return processFailures;
	}

	public void setProcessFailures(int processFailures) 
	{
		this.processFailures = processFailures;
	}
	
	
}

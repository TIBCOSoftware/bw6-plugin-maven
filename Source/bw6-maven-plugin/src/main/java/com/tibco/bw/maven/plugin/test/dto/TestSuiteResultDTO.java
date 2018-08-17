package com.tibco.bw.maven.plugin.test.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

@SuppressWarnings("serial")
@XmlRootElement
@XmlType
@XmlSeeAlso({TestSetResultDTO.class , ProcessCoverageDTO.class})
public class TestSuiteResultDTO implements Serializable 
{

	private ModuleInfoDTO moduleInfo;
	
	@SuppressWarnings("rawtypes")
	private List testSetResult = new ArrayList();

	@SuppressWarnings("rawtypes")
	private List codeCoverage = new ArrayList();
	
	@SuppressWarnings("rawtypes")
	@XmlElement(name="testSetResult")
	public List getTestSetResult() 
	{
		return testSetResult;
	}

	@SuppressWarnings("rawtypes")
	public void setTestSetResult(List testSetResult) 
	{
		this.testSetResult = testSetResult;
	}

	@SuppressWarnings("rawtypes")
	@XmlElement(name="codeCoverage")
	public List getCodeCoverage() 
	{
		return codeCoverage;
	}

	@SuppressWarnings("rawtypes")
	public void setCodeCoverage(List codeCoverage) 
	{
		this.codeCoverage = codeCoverage;
	}

	@XmlElement
	public ModuleInfoDTO getModuleInfo() 
	{
		return moduleInfo;
	}

	public void setModuleInfo(ModuleInfoDTO moduleInfo) 
	{
		this.moduleInfo = moduleInfo;
	}	
	
}

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
@XmlSeeAlso({TestSetResultDTO.class, BWTestSuiteDTO.class , ProcessCoverageDTO.class})
public class TestSuiteResultDTO implements Serializable 
{

	private ModuleInfoDTO moduleInfo;
	
	private boolean showFailureDetails;
	
	@SuppressWarnings("rawtypes")
	private List testSetResult = new ArrayList();
	
	@SuppressWarnings("rawtypes")
	private List BWTestSuite = new ArrayList();
	

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
	@XmlElement(name="BWTestSuite")
	public List getBWTestSuite() 
	{
		return BWTestSuite;
	}

	@SuppressWarnings("rawtypes")
	public void setBWTestSuite(List BWTestSuite) 
	{
		this.BWTestSuite = BWTestSuite;
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
	
	@XmlElement(name="showFailureDetails")
	public boolean getshowFailureDetails() {
		return showFailureDetails;
	}

	public void setshowFailureDetails(boolean showFailureDetails) {
		this.showFailureDetails = showFailureDetails;
	}	
	
}

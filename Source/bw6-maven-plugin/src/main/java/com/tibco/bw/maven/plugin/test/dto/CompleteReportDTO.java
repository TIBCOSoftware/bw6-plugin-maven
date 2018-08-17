package com.tibco.bw.maven.plugin.test.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;

@SuppressWarnings("serial")
@XmlRootElement
@XmlSeeAlso({TestSuiteResultDTO.class})
public class CompleteReportDTO implements Serializable
{
	@SuppressWarnings("rawtypes")
	List moduleResult = new ArrayList();

	@SuppressWarnings("rawtypes")
	public List getModuleResult() 
	{
		return moduleResult;
	}

	@SuppressWarnings("rawtypes")
	public void setModuleResult(List moduleResult)
	{
		this.moduleResult = moduleResult;
	}
	
	
	
	
}

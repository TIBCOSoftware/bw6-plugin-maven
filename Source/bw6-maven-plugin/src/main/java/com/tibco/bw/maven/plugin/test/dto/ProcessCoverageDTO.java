package com.tibco.bw.maven.plugin.test.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@SuppressWarnings("serial")
@XmlType
public class ProcessCoverageDTO implements Serializable 
{

	private String moduleName;
	
	private String processName;
	
	@SuppressWarnings("rawtypes")
	private List activityCoverage = new ArrayList();
	
	@SuppressWarnings("rawtypes")
	private List transitionCoverage = new ArrayList();

	@XmlElement
	public String getModuleName()
	{
		return moduleName;
	}

	public void setModuleName(String moduleName) 
	{
		this.moduleName = moduleName;
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
	@XmlElement(name="activityCoverage")
	public List getActivityCoverage() 
	{
		return activityCoverage;
	}

	@SuppressWarnings("rawtypes")
	public void setActivityCoverage(List activityCoverage)
	{
		this.activityCoverage = activityCoverage;
	}

	@SuppressWarnings("rawtypes")
	@XmlElement(name="transitionCoverage")
	public List getTransitionCoverage()
	{
		return transitionCoverage;
	}

	@SuppressWarnings("rawtypes")
	public void setTransitionCoverage(List transitionCoverage) 
	{
		this.transitionCoverage = transitionCoverage;
	}
	
}

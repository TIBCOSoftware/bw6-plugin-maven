package com.tibco.bw.maven.plugin.test.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;

@SuppressWarnings("serial")
@XmlRootElement
@XmlSeeAlso({TestSetDTO.class}) 
public class TestSuiteDTO implements Serializable
{
	@SuppressWarnings("rawtypes")
	private List testSetList = new ArrayList();

	private ModuleInfoDTO moduleInfo;
	
	private boolean showFailureDetails;

	
	@SuppressWarnings("rawtypes")
	@XmlElement(name="testSetList")
	public List getTestSetList() 
	{
		return testSetList;
	}

	@SuppressWarnings("rawtypes")
	public void setTestSetList(List testSetList) 
	{
		this.testSetList = testSetList;
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
	
	@XmlElement (name = "showFailureDetails")
	public boolean isShowFailureDetails() {
		return showFailureDetails;
	}

	public void setShowFailureDetails(boolean showFailureDetails) {
		this.showFailureDetails = showFailureDetails;
	}

}

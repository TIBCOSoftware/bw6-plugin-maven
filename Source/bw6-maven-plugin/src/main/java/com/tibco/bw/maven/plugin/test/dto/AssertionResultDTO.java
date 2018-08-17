package com.tibco.bw.maven.plugin.test.dto;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@SuppressWarnings("serial")
@XmlType
public class AssertionResultDTO implements Serializable
{

	private String instanceId;
	private String activityName;
	private String assertionStatus;
	private String message;
	
	@XmlElement
	public String getInstanceId() 
	{
		return instanceId;
	}
	
	public void setInstanceId(String instanceId) 
	{
		this.instanceId = instanceId;
	}
	
	@XmlElement
	public String getActivityName() 
	{
		return activityName;
	}
	
	public void setActivityName(String activityName) 
	{
		this.activityName = activityName;
	}
	
	@XmlElement
	public String getAssertionStatus() 
	{
		return assertionStatus;
	}
	
	public void setAssertionStatus(String assertionStatus) 
	{
		this.assertionStatus = assertionStatus;
	}
	
	@XmlElement
	public String getMessage() 
	{
		return message;
	}
	
	public void setMessage(String message) 
	{
		this.message = message;
	}

}

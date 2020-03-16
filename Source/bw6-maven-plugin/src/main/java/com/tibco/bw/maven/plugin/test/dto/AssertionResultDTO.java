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
	private String goldInput;
	private String assertionMode;
	private String startElementNameTag;
	private String endElementNameTag;
	private String activityOutput;
	
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

	public String getGoldInput() {
		return goldInput;
	}

	public void setGoldInput(String goldInput) {
		this.goldInput = goldInput;
	}

	public String getAssertionMode() {
		return assertionMode;
	}

	public void setAssertionMode(String assertionMode) {
		this.assertionMode = assertionMode;
	}

	public String getStartElementNameTag() {
		return startElementNameTag;
	}

	public void setStartElementNameTag(String startElementNameTag) {
		this.startElementNameTag = startElementNameTag;
	}

	public String getEndElementNameTag() {
		return endElementNameTag;
	}

	public void setEndElementNameTag(String endElementNameTag) {
		this.endElementNameTag = endElementNameTag;
	}

	@XmlElement
	public String getActivityOutput() {
		return activityOutput;
	}

	public void setActivityOutput(String activityOutput) {
		this.activityOutput = activityOutput;
	}

}

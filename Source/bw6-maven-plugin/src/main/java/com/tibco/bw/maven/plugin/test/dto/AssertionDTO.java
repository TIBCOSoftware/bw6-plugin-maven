package com.tibco.bw.maven.plugin.test.dto;


import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@SuppressWarnings("serial")
@XmlType
public class AssertionDTO implements Serializable {

	private String processId;
	private String location;
	private String expression;
	private ConditionLanguageDTO language;
	private String activityId;

	@XmlElement
	public String getProcessId() {
		return processId;
	}

	public void setProcessId(String processId) {
		this.processId = processId;
	}

	@XmlElement
	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	@XmlElement
	public String getExpression() {
		return expression;
	}

	public void setExpression(String expression) {
		this.expression = expression;
	}

	@XmlElement
	public ConditionLanguageDTO getConditionLanguage() {
		return language;
	}

	public void setConditionLanguage(ConditionLanguageDTO language) {
		this.language = language;
	}

	@XmlElement
	public String getActivityId() 
	{
		return activityId;
	}

	public void setActivityId(String activityId) 
	{
		this.activityId = activityId;
	}

}

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
	private String goldInput;
	private String assertionMode;
	private String startElementNameTag;
	private String endElementNameTag;

	public String getGoldInput() {
		return goldInput;
	}

	public void setGoldInput(String goldInput) {
		this.goldInput = goldInput;
	}

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

}

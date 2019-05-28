package com.tibco.bw.maven.plugin.test.dto;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


@SuppressWarnings("serial")
@XmlType
public class MockActivityDTO implements Serializable {
	
	private String location;
	private String mockOutputFilePath;
	
	@XmlElement
	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}
	public void setmockOutputFilePath(String mockOutputFilePath) 
	{
		this.mockOutputFilePath = mockOutputFilePath;
	}
	
	@XmlElement
	public String getmockOutputFilePath() 
	{
		return mockOutputFilePath;
	}
	
}

package com.tibco.bw.maven.plugin.test.dto;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@SuppressWarnings("serial")
@XmlType
public class ModuleInfoDTO implements Serializable
{

	private String appName;
	
	private String appVersion;
	
	private String moduleName;
	
	private String moduleVersion;

	@XmlElement
	public String getAppName() 
	{
		return appName;
	}

	public void setAppName(String appName) 
	{
		this.appName = appName;
	}

	@XmlElement
	public String getAppVersion() 
	{
		return appVersion;
	}

	public void setAppVersion(String appVersion) 
	{
		this.appVersion = appVersion;
	}

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
	public String getModuleVersion() 
	{
		return moduleVersion;
	}

	public void setModuleVersion(String moduleVersion) 
	{
		this.moduleVersion = moduleVersion;
	}
	
}

package com.tibco.bw.maven.utils;

import java.util.List;

public class BWApplicationInfo extends BWModuleInfo
{

	private List<BWAppModuleInfo> appModules;
	
	private List<BWSharedModuleInfo> sharedModules;
	
	private List<BWOSGiModuleInfo> osgiModules;
	
	private BWDeploymentInfo deploymentInfo = new BWDeploymentInfo();

	public List<BWAppModuleInfo> getAppModules() 
	{
		return appModules;
	}

	public void setAppModules(List<BWAppModuleInfo> appModules) 
	{
		this.appModules = appModules;
	}

	public List<BWSharedModuleInfo> getSharedModules() 
	{
		return sharedModules;
	}

	public void setSharedModules(List<BWSharedModuleInfo> sharedModules) 
	{
		this.sharedModules = sharedModules;
	}

	public List<BWOSGiModuleInfo> getOsgiModules() 
	{
		return osgiModules;
	}

	public void setOsgiModules(List<BWOSGiModuleInfo> osgiModules) 
	{
		this.osgiModules = osgiModules;
	}

	public BWDeploymentInfo getDeploymentInfo() 
	{
		return deploymentInfo;
	}

	public void setDeploymentInfo(BWDeploymentInfo deploymentInfo) 
	{
		this.deploymentInfo = deploymentInfo;
	}

	
}



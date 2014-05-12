package com.tibco.bw.maven.utils;


public class BWProjectInfo implements IBWProjectInfo
{

	private String tibcoHome;
	
	private BWApplicationInfo appInfo;
	
	
	public String getTibcoHome() 
	{
		return tibcoHome;
	}
	
	public void setTibcoHome(String tibcoHome) 
	{
		this.tibcoHome = tibcoHome;
	}
	

	public BWApplicationInfo getAppInfo() 
	{
		return appInfo;
	}
	
	public void setAppInfo(BWApplicationInfo appInfo) 
	{
		this.appInfo = appInfo;
	}


}

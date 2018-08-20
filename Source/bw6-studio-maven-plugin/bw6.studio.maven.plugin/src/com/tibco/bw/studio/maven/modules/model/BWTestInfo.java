package com.tibco.bw.studio.maven.modules.model;

public class BWTestInfo 
{
	
	protected String tibcoHome;
	
	protected String bwHome;

	protected String skipTests;
	
	protected String failIfNoTests;
	
	
	public String getTibcoHome() {
		return tibcoHome;
	}

	public void setTibcoHome(String tibcoHome) 
	{
		this.tibcoHome = tibcoHome;
	}

	public String getBwHome() {
		return bwHome;
	}

	public void setBwHome(String bwHome) {
		this.bwHome = bwHome;
	}

	public String getSkipTests() 
	{
		return skipTests;
	}

	public void setSkipTests(String runTests)
	{
		this.skipTests = runTests;
	}

	public String getFailIfNoTests() 
	{
		return failIfNoTests;
	}

	public void setFailIfNoTests(String skipOnError)
	{
		this.failIfNoTests = skipOnError;
	}


}

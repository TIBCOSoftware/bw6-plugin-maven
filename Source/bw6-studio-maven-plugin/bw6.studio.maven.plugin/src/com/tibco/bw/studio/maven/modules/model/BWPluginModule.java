package com.tibco.bw.studio.maven.modules.model;

import com.tibco.bw.studio.maven.pom.builders.IPOMBuilder;
import com.tibco.bw.studio.maven.pom.builders.PluginPOMBuilder;

public class BWPluginModule extends BWModule 
{

	private boolean isCustomXpath;
	
	
	public BWModuleType getType() 
	{
		return BWModuleType.PluginProject;
	}

	public IPOMBuilder getPOMBuilder()
	{
		return new PluginPOMBuilder();
	}

	public boolean isCustomXpath()
	{
		return isCustomXpath;
	}

	public void setCustomXpath(boolean isCustomXpath)
	{
		this.isCustomXpath = isCustomXpath;
	}
	
}

package com.tibco.bw.studio.maven.modules;

import com.tibco.bw.studio.maven.pom.builders.IPOMBuilder;
import com.tibco.bw.studio.maven.pom.builders.PluginPOMBuilder;

public class BWPluginModule extends BWModule 
{

	public BWModuleType getType() 
	{
		return BWModuleType.PluginProject;
	}

	public IPOMBuilder getPOMBuilder()
	{
		return new PluginPOMBuilder();
	}
	
}

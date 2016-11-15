package com.tibco.bw.studio.maven.modules.model;

import com.tibco.bw.studio.maven.pom.builders.IPOMBuilder;
import com.tibco.bw.studio.maven.pom.builders.ModulePOMBuilder;

public class BWCustomXPathModule extends BWModule 
{

	public BWModuleType getType() 
	{
		return BWModuleType.CustomXPathProject;
	}

	public IPOMBuilder getPOMBuilder()
	{
		return new ModulePOMBuilder();
	}
}

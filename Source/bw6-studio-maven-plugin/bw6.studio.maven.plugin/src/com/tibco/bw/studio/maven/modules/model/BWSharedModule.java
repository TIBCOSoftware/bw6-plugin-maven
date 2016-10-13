package com.tibco.bw.studio.maven.modules.model;

import com.tibco.bw.studio.maven.pom.builders.IPOMBuilder;
import com.tibco.bw.studio.maven.pom.builders.SharedModulePOMBuilder;

public class BWSharedModule extends BWModule 
{

	public BWModuleType getType()
	{
		return BWModuleType.SharedModule;
	}
	
	public IPOMBuilder getPOMBuilder()
	{
		return new SharedModulePOMBuilder();
	}

}

package com.tibco.bw.studio.maven.modules;

import com.tibco.bw.studio.maven.pom.builders.ApplicationPOMBuilder;
import com.tibco.bw.studio.maven.pom.builders.IPOMBuilder;

public class BWApplication extends BWModule 
{

	public BWModuleType getType() 
	{
		return BWModuleType.Application;
	}

	public IPOMBuilder getPOMBuilder()
	{
		return new ApplicationPOMBuilder();
	}
}

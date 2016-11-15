package com.tibco.bw.studio.maven.modules.model;

import com.tibco.bw.studio.maven.pom.builders.AppModulePOMBuilder;
import com.tibco.bw.studio.maven.pom.builders.IPOMBuilder;


public class BWAppModule extends BWModule 
{
	public BWModuleType getType() 
	{
		return BWModuleType.AppModule;
	}
	
	public IPOMBuilder getPOMBuilder()
	{
		return new AppModulePOMBuilder();
	}

}

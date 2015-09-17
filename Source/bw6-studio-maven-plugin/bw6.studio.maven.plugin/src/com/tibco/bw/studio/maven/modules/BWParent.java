package com.tibco.bw.studio.maven.modules;

import com.tibco.bw.studio.maven.pom.builders.IPOMBuilder;
import com.tibco.bw.studio.maven.pom.builders.ParentPOMBuilder;

public class BWParent extends BWModule 
{
	

	public BWModuleType getType() 
	{
		return BWModuleType.Parent;
	}

	public IPOMBuilder getPOMBuilder()
	{
		return new ParentPOMBuilder();
	}
	
}

package com.tibco.bw.studio.maven.helpers;

import java.util.List;

import com.tibco.bw.studio.maven.modules.BWModule;
import com.tibco.bw.studio.maven.modules.BWModuleType;
import com.tibco.bw.studio.maven.modules.BWParent;

public class ModuleHelper 
{

	public static BWParent getParentModule( List<BWModule> modules )
	{
		for( BWModule module : modules )
		{
			if( module.getType() == BWModuleType.Parent )
			{
				return (BWParent)module;
			}
		}
		
		return null;
	}
	

	public static BWModule getApplication( List<BWModule> modules )
	{
		for( BWModule module : modules )
		{
			if( module.getType() == BWModuleType.Application )
			{
				return module;
			}
		}
		
		return null;
	}

	public static BWModule getAppModule( List<BWModule> modules )
	{
		for( BWModule module : modules )
		{
			if( module.getType() == BWModuleType.AppModule )
			{
				return module;
			}
		}
		
		return null;
	}
	
}

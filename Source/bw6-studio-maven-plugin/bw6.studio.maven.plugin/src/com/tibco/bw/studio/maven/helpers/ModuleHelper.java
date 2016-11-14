package com.tibco.bw.studio.maven.helpers;

import java.util.List;

import com.tibco.bw.studio.maven.modules.model.BWModule;
import com.tibco.bw.studio.maven.modules.model.BWModuleType;
import com.tibco.bw.studio.maven.modules.model.BWParent;

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
	
	
	public static BWModule getModule( List<BWModule> modules , String name )
	{
		for( BWModule module : modules )
		{
			if( module.getArtifactId().equals( name ) )
			{
				return module;
			}
		}
		
		return null;
	}
}

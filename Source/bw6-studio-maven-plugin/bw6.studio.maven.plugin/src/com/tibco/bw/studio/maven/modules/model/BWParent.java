package com.tibco.bw.studio.maven.modules.model;

import com.tibco.bw.studio.maven.pom.builders.IPOMBuilder;
import com.tibco.bw.studio.maven.pom.builders.ParentPOMBuilder;

public class BWParent extends BWModule 
{
	
	private boolean valueChanged;
	
	public BWModuleType getType() 
	{
		return BWModuleType.Parent;
	}

	public IPOMBuilder getPOMBuilder()
	{
		return new ParentPOMBuilder();
	}

	public boolean isValueChanged() 
	{
		return valueChanged;
	}

	public void setValueChanged(boolean valueChanged)
	{
		this.valueChanged = valueChanged;
	}
	
}

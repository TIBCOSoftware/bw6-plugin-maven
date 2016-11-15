package com.tibco.bw.studio.maven.modules.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class BWProject 
{

	private BWParent parent;

	private List<BWModule> modules;
	
	private Map<String, List<String>> dependencies = new HashMap<String, List<String>>();

	
	public List<BWModule> getModules() 
	{
		return modules;
	}


	public void setModules(List<BWModule> modules) 
	{
		this.modules = modules;
	}


	public Map<String, List<String>> getDependencies() 
	{
		return dependencies;
	}


	public void setDependencies(Map<String, List<String>> dependencies) 
	{
		this.dependencies = dependencies;
	}


	public BWParent getParent() 
	{
		return parent;
	}


	public void setParent(BWParent parent)
	{
		this.parent = parent;
	}


	
}

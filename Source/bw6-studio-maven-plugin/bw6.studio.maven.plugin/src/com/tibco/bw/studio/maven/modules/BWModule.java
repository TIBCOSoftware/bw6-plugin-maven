package com.tibco.bw.studio.maven.modules;

import java.io.File;
import java.util.List;

import org.eclipse.core.resources.IProject;

import com.tibco.bw.studio.maven.pom.builders.IPOMBuilder;


public abstract class BWModule 
{

	protected IProject project;

	protected String artifactId;
	protected String groupId;
	protected String version;
	
	protected String tibcoHome;
	
	protected File pomfileLocation;

	protected List<String> depModules;

	public IProject getProject() 
	{
		return project;
	}

	public void setProject(IProject project) 
	{
		this.project = project;
	}

	public String getArtifactId() 
	{
		return artifactId;
	}

	public void setArtifactId(String artifactId)
	{
		this.artifactId = artifactId;
	}

	public String getGroupId() 
	{
		return groupId;
	}

	public void setGroupId(String groupId) 
	{
		this.groupId = groupId;
	}

	public String getVersion() 
	{
		return version;
	}

	public void setVersion(String version)
	{
		this.version = version;
	}

	public String getTibcoHome() {
		return tibcoHome;
	}

	public void setTibcoHome(String tibcoHome) 
	{
		this.tibcoHome = tibcoHome;
	}

	public File getPomfileLocation() 
	{
		return pomfileLocation;
	}

	public void setPomfileLocation(File pomfileLocation) 
	{
		this.pomfileLocation = pomfileLocation;
	}

	public List<String> getDepModules() 
	{
		return depModules;
	}

	public void setDepModules(List<String> depModules) 
	{
		this.depModules = depModules;
	}
	
	abstract public BWModuleType getType();

	abstract public IPOMBuilder getPOMBuilder();
}

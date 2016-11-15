package com.tibco.bw.studio.maven.modules.model;

import java.io.File;
import java.util.List;

import org.apache.maven.model.Model;
import org.eclipse.core.resources.IProject;

import com.tibco.bw.studio.maven.pom.builders.IPOMBuilder;


public abstract class BWModule 
{

	protected IProject project;

	protected String artifactId;
	protected String groupId;
	protected String version;
	protected String name;

	protected String tibcoHome;
	
	protected File pomfileLocation;

	protected List<String> depModules;
	
	protected boolean overridePOM;
	
	protected String projectName;
	
	protected String fromPath;
	
	protected String toPath;
	
	protected BWPCFModule bwpcfModule;
	
	protected BWDockerModule bwDockerModule;
	
	protected BWK8SModule bwk8sModule;
	
	protected boolean pomExists;
	
	protected Model mavenModel;
	

	abstract public BWModuleType getType();

	abstract public IPOMBuilder getPOMBuilder();

	
	
	public BWK8SModule getBwk8sModule() {
		return bwk8sModule;
	}

	public void setBwk8sModule(BWK8SModule bwk8sModule) {
		this.bwk8sModule = bwk8sModule;
	}

	public BWDockerModule getBwDockerModule() {
		return bwDockerModule;
	}

	public void setBwDockerModule(BWDockerModule bwDockerModule) {
		this.bwDockerModule = bwDockerModule;
	}

	public BWPCFModule getBwpcfModule()
	{
		return bwpcfModule;
	}
	
	public void setBwpcfModule(BWPCFModule bwpcfModule)
	{
		this.bwpcfModule=bwpcfModule;
	}
	
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
	
	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public boolean isOverridePOM()
	{
		return overridePOM;
	}

	public void setOverridePOM(boolean overridePOM) 
	{
		this.overridePOM = overridePOM;
	}

	public String getProjectName() 
	{
		return projectName;
	}

	public void setProjectName(String projectName)
	{
		this.projectName = projectName;
	}

	public String getFromPath() 
	{
		return fromPath;
	}

	public void setFromPath(String fromPath)
	{
		this.fromPath = fromPath;
	}

	public String getToPath() 
	{
		return toPath;
	}

	public void setToPath(String toPath)
	{
		this.toPath = toPath;
	}


	public boolean isPomExists() {
		return pomExists;
	}

	public void setPomExists(boolean pomExists) 
	{
		this.pomExists = pomExists;
	}

	public Model getMavenModel() 
	{
		return mavenModel;
	}

	public void setMavenModel(Model mavenModel) 
	{
		this.mavenModel = mavenModel;
	}
}

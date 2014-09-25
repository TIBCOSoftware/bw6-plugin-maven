/*
 * Copyright (c) 2013-2014 TIBCO Software Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.tibco.bw.maven.utils;

import java.io.File;
import java.util.List;

import org.eclipse.core.resources.IProject;

public abstract class BWModuleInfo 
{
	protected IProject project;
	
	protected String name;
	protected String version;
	protected String artifactId;
	protected String groupId;

	protected String tibcoHome;
	
	protected String bwVersion;
	
	protected File pomfileLocation;
	
	protected List<String> capabilities;

	
	public String getName() 
	{
		return name;
	}
	
	public void setName(String name) 
	{
		this.name = name;
	}
	public String getVersion() {
		return version;
	}
	
	public void setVersion(String version)
	{
		this.version = version;
	}
	
	public List<String> getCapabilities() 
	{
		return capabilities;
	}
	
	public void setCapabilities(List<String> capabilities) 
	{
		this.capabilities = capabilities;
	}
	
	public File getPomfileLocation() 
	{
		return pomfileLocation;
	}
	
	public void setPomfileLocation(File pomfileLocation) 
	{
		this.pomfileLocation = pomfileLocation;
	}
	
	public IProject getProject() 
	{
		return project;
	}
	
	public void setProject(IProject project) 
	{
		this.project = project;
	}
	
	public String getTibcoHome() 
	{
		return tibcoHome;
	}
	
	public void setTibcoHome(String tibcoHome) 
	{
		this.tibcoHome = tibcoHome;
	}
	
	public String getBwVersion() 
	{
		return bwVersion;
	}
	
	public void setBwVersion(String bwVersion) 
	{
		this.bwVersion = bwVersion;
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

}

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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BWApplicationInfo extends BWModuleInfo
{

	private List<BWAppModuleInfo> appModules;
	
	private List<BWSharedModuleInfo> sharedModules;
	
	private List<BWOSGiModuleInfo> osgiModules;
	
	private BWDeploymentInfo deploymentInfo = new BWDeploymentInfo();
	
	private Map<String, List<String>> dependencies = new HashMap<String, List<String>>();


	public List<BWAppModuleInfo> getAppModules() 
	{
		return appModules;
	}

	public void setAppModules(List<BWAppModuleInfo> appModules) 
	{
		this.appModules = appModules;
	}

	public List<BWSharedModuleInfo> getSharedModules() 
	{
		return sharedModules;
	}

	public void setSharedModules(List<BWSharedModuleInfo> sharedModules) 
	{
		this.sharedModules = sharedModules;
	}

	public List<BWOSGiModuleInfo> getOsgiModules() 
	{
		return osgiModules;
	}

	public void setOsgiModules(List<BWOSGiModuleInfo> osgiModules) 
	{
		this.osgiModules = osgiModules;
	}

	public BWDeploymentInfo getDeploymentInfo() 
	{
		return deploymentInfo;
	}

	public void setDeploymentInfo(BWDeploymentInfo deploymentInfo) 
	{
		this.deploymentInfo = deploymentInfo;
	}
	
	public Map<String, List<String>> getDependencies() 
	{
		return dependencies;
	}

	public void setDependencies(Map<String, List<String>> dependencies)
	{
		this.dependencies = dependencies;
	}


	
}



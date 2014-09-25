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

public class BWDeploymentInfo 
{

	private boolean deployToAdmin; 
	
	private String domain;
	
	private String appspace;
	
	private String appNode;
	
	private String domainDesc;
	
	private String appspaceDesc;
	
	private String appNodeDesc;

	private boolean createNewDomain;
	
	private boolean createNewAppSpace;
	
	private boolean createNewAppNode;
	
	private boolean redeploy;
	
	private String osgiPort;
	
	private String httpPort;
	
	private String agent;

	private String profile;
	
	public boolean isDeployToAdmin() 
	{
		return deployToAdmin;
	}

	public void setDeployToAdmin(boolean deployToAdmin)
	{
		this.deployToAdmin = deployToAdmin;
	}

	public String getDomain() 
	{
		return domain;
	}

	public void setDomain(String domain)
	{
		this.domain = domain;
	}

	public String getAppspace() 
	{
		return appspace;
	}

	public void setAppspace(String appspace) 
	{
		this.appspace = appspace;
	}

	public String getAppNode() 
	{
		return appNode;
	}

	public void setAppNode(String appNode)
	{
		this.appNode = appNode;
	}

	public boolean isCreateNewDomain() 
	{
		return createNewDomain;
	}

	public void setCreateNewDomain(boolean createNewDomain)
	{
		this.createNewDomain = createNewDomain;
	}

	public boolean isCreateNewAppSpace() 
	{
		return createNewAppSpace;
	}

	public void setCreateNewAppSpace(boolean createNewAppSpace)
	{
		this.createNewAppSpace = createNewAppSpace;
	}

	public boolean isCreateNewAppNode() 
	{
		return createNewAppNode;
	}

	public void setCreateNewAppNode(boolean createNewAppNode)
	{
		this.createNewAppNode = createNewAppNode;
	}

	public String getOsgiPort() 
	{
		return osgiPort;
	}

	public void setOsgiPort(String osgiPort) 
	{
		this.osgiPort = osgiPort;
	}

	public String getHttpPort() 
	{
		return httpPort;
	}

	public void setHttpPort(String httpPort)
	{
		this.httpPort = httpPort;
	}

	public String getAgent() 
	{
		return agent;
	}

	public String getDomainDesc() {
		return domainDesc;
	}

	public void setDomainDesc(String domainDesc) 
	{
		this.domainDesc = domainDesc;
	}

	public String getAppspaceDesc() 
	{
		return appspaceDesc;
	}

	public void setAppspaceDesc(String appspaceDesc)
	{
		this.appspaceDesc = appspaceDesc;
	}

	public String getAppNodeDesc()
	{
		return appNodeDesc;
	}

	public void setAppNodeDesc(String appNodeDesc)
	{
		this.appNodeDesc = appNodeDesc;
	}

	public void setAgent(String agent) 
	{
		this.agent = agent;
	}

	public String getProfile()
	{
		return profile;
	}

	public void setProfile(String profile) 
	{
		this.profile = profile;
	}

	public boolean isRedeploy() 
	{
		return redeploy;
	}

	public void setRedeploy(boolean redeploy)
	{
		this.redeploy = redeploy;
	}
	
}

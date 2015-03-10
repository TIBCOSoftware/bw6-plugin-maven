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

package com.tibco.bw.maven.generators;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.apache.maven.model.Build;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.PluginExecution;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.eclipse.core.runtime.IStatus;

import com.tibco.bw.maven.Activator;
import com.tibco.bw.maven.utils.BWAppModuleInfo;
import com.tibco.bw.maven.utils.BWApplicationInfo;
import com.tibco.bw.maven.utils.BWOSGiModuleInfo;

public class BWApplicationPOMGenerator extends BWPOMGenerator
{

	public ArrayList<String> resolved = new ArrayList<String>();

	private BWApplicationInfo appInfo;
	
	public BWApplicationPOMGenerator( BWApplicationInfo appInfo )
	{
		super( appInfo );
		this.appInfo = appInfo;
	}
	
	public void generate()
	{
		try
		{
			
			addPrimaryTags( "pom" );
			addProperties();
			addModules();
			writeBuildInfo();
			generatePOMFile();

		}
		
		catch(Exception e)
		{
			 Activator.logException("Failed to generate the POM file for Application ", IStatus.ERROR , e );		
		}
		
	}

	private void resolveDependency(Map<String, List<String>> map, String start) 
	{
		Queue<String> queue = new LinkedList<String>();
		
		if(map.get(start)==null)
		{
			resolved.add(start);
			return;
		}

		queue.addAll(map.get(start));
		
		while(!queue.isEmpty())
		{
			String newStart = (String) queue.remove();
			

			if(!resolved.contains(newStart)) 
			{
				resolveDependency(map, newStart);			
			}
			
		}
		resolved.add(start);
	}	
	
	
	

	/**
	 * Add the Modules to the POM. First the OSGi, then the Shared and then the App.
	 * The Maven reactor will build the modules in this order. 	
	 */
	private void addModules()
	{
		List<String> modules = new ArrayList<String>();

		for(BWOSGiModuleInfo info : appInfo.getOsgiModules() )
		{
			modules.add( "../"+info.getName());
		}

		resolveDependency( appInfo.getDependencies(),  appInfo.getAppModules().get(0).getName() );
		
		for( String info : resolved )
		{
			modules.add( "../"+info );
		}
		
//		for( BWAppModuleInfo info : appInfo.getAppModules() )
//		{
//			modules.add( "../"+info.getName());
//		}
		model.setModules(modules);
	}
	
	
	
	private List<String> sortSharedModuleDeps()
	{
		Map<String,List<String>> map = appInfo.getDependencies();
		Map<String,List<String>> mapcopy = new HashMap<String,List<String>>(map);
	
		List<String> depList = new LinkedList<String>();
		
		List<String> noDeplist = new ArrayList<String>();
		
		Set<String> set = map.keySet();
		
		depList.addAll( mapcopy.keySet() );
		
		
		while( depList.size() != 0 )
		{
			for( String str : set )
			{
				if ( mapcopy.get( str).size() != 0 && hasDependency(mapcopy, str ))
				{
					continue;
				}
				else
				{
					noDeplist.add(str );
					depList.remove( str );
					for(  String str1 : set )
					{
						mapcopy.get(str1).remove( str );
					}
					mapcopy.remove(str);

				}
			}
			
		}
		
		
		
		
		return noDeplist;
	}
	

	private boolean hasDependency( Map<String,List<String>> map , String module )
	{

		Set<String> set = map.keySet();
		
		for( String str : set )
		{
			if( map.get( str).contains( module ))
			{
				return true;
			}
			
		}

		
		return false;
	}
	
	
	private void writeBuildInfo()
	{
    	Build build = new Build();
    	
		addEARPlugin(build);
		
		model.setBuild(build);
	}

	private void addEARPlugin(Build build) {
		Plugin plugin1 = new Plugin();
		plugin1.setGroupId("com.tibco.bw");
		plugin1.setArtifactId("bw-archiver");
		plugin1.setVersion("1.0.0");
		plugin1.setExtensions("true");
		
		Xpp3Dom config = new Xpp3Dom("configuration");

		Xpp3Dom tibcoHome  = new Xpp3Dom("tibcohome");
		tibcoHome.setValue( appInfo.getTibcoHome() );


		Xpp3Dom bwVersion  = new Xpp3Dom("bw.version");
		bwVersion.setValue( appInfo.getBwVersion() );

		Xpp3Dom deployToAdmin  = new Xpp3Dom("deployToAdmin");
		deployToAdmin.setValue( Boolean.toString(appInfo.getDeploymentInfo().isDeployToAdmin()) );

		Xpp3Dom redeploy  = new Xpp3Dom("redeployear");
		redeploy.setValue( Boolean.toString(appInfo.getDeploymentInfo().isRedeploy()) );

		Xpp3Dom domain  = new Xpp3Dom("domain");
		domain.setValue( appInfo.getDeploymentInfo().getDomain() );
		
		Xpp3Dom appspace  = new Xpp3Dom("appspace");
		appspace.setValue( appInfo.getDeploymentInfo().getAppspace() );
		
		Xpp3Dom appnode  = new Xpp3Dom("appnode");
		appnode.setValue( appInfo.getDeploymentInfo().getAppNode() );
		
		Xpp3Dom domainDesc  = new Xpp3Dom("domainDesc");
		domainDesc.setValue( appInfo.getDeploymentInfo().getDomainDesc() );
		
		Xpp3Dom appspaceDesc  = new Xpp3Dom("appspaceDesc");
		appspaceDesc.setValue( appInfo.getDeploymentInfo().getAppspaceDesc() );
		
		Xpp3Dom appnodeDesc  = new Xpp3Dom("appnodeDesc");
		appnodeDesc.setValue( appInfo.getDeploymentInfo().getAppNodeDesc() );

		Xpp3Dom osgiport  = new Xpp3Dom("osgiport");
		osgiport.setValue( appInfo.getDeploymentInfo().getOsgiPort() );

		Xpp3Dom httpPort  = new Xpp3Dom("httpport");
		httpPort.setValue( appInfo.getDeploymentInfo().getHttpPort() );

		Xpp3Dom profile  = new Xpp3Dom("profile");
		profile.setValue( appInfo.getDeploymentInfo().getProfile() );
		
		Xpp3Dom agent  = new Xpp3Dom("agent");
		agent.setValue( appInfo.getDeploymentInfo().getAgent() );

		
		config.addChild(tibcoHome);
		config.addChild(bwVersion);
		config.addChild(deployToAdmin);
		config.addChild(redeploy);
		config.addChild(domain);
		config.addChild(domainDesc);
		config.addChild(appspace);
		config.addChild(appspaceDesc);		
		config.addChild(appnode);
		config.addChild(appnodeDesc);
		config.addChild(httpPort );
		config.addChild(agent );
		config.addChild(profile );
		plugin1.setConfiguration(config);

		
		List<PluginExecution> execList = new ArrayList<PluginExecution>();
		PluginExecution ear = new PluginExecution();
		ear.setId("bw-packager");
		ear.setPhase("package");
		ear.addGoal("bw-packager");

		PluginExecution exec = new PluginExecution();
		exec.setId("bw-installer");
		exec.setPhase("install");
		exec.addGoal("bw-installer");

		execList.add(ear);
		execList.add(exec);
		plugin1.setExecutions(execList);

		build.addPlugin(plugin1);
	}
	
}


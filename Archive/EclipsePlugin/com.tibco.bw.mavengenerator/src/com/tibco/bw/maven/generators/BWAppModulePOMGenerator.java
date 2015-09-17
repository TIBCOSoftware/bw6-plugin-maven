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

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.model.Build;
import org.apache.maven.model.Model;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.PluginExecution;
import org.apache.maven.model.PluginManagement;
import org.apache.maven.model.Repository;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.eclipse.core.runtime.IStatus;

import com.tibco.bw.maven.Activator;
import com.tibco.bw.maven.utils.BWAppModuleInfo;
import com.tibco.bw.maven.utils.BWOSGiModuleInfo;
import com.tibco.bw.maven.utils.BWSharedModuleInfo;
import com.tibco.bw.maven.utils.Capability;

public class BWAppModulePOMGenerator extends BWPOMGenerator
{
	private BWAppModuleInfo bwinfo;
	
	private List<BWSharedModuleInfo> shared;
	
	private List<BWOSGiModuleInfo> osgi;
	
	public BWAppModulePOMGenerator(BWAppModuleInfo bwinfo , List<BWSharedModuleInfo> shared, List<BWOSGiModuleInfo> osgi )
	{
		super(bwinfo);
		this.bwinfo = bwinfo;
		this.shared = shared;
		this.osgi = osgi;
	}
	
	public void generate()
	{
		try
		{
			addPrimaryTags("eclipse-plugin");
			addProperties();
			addRepoInfo();
			addBuildInfo();		
			generatePOMFile();
		}
		catch(Exception e )
		{
			 Activator.logException("Failed to generate the POM file for ApModule  ", IStatus.ERROR , e );		
		}
	}
	
	

	protected void addProperties()
	{
		super.addProperties();
		model.addProperty("tycho-version", "0.22.0");
		model.addProperty("main.p2.repo", "${tibco.home}/bw/${bw.version}/maven/p2repo");
		model.addProperty("shared.p2.repo", "${tibco.home}/bw/${bw.version}/maven/sharedmodulerepo");
		

	}
	
	private void addRepoInfo()
	{
		addMainRepoInfo();
		
		if(shared.size() > 0 || osgi.size() > 0 )
		{
			Repository reposhared = new Repository();
			reposhared.setId("shared.bw.bundle");
			reposhared.setUrl("file:///${shared.p2.repo}");
			reposhared.setLayout("p2");
			model.getRepositories().add(reposhared);
		}
		
	}
	
	private void addBuildInfo()
	{
    	Build build = new Build();
    	
    	Plugin tychoMavenPlugin = addTychoMavenPlugin();
		build.addPlugin(tychoMavenPlugin);
		
		Plugin tychoJarPlugin = addTychoJARPlugin();
		build.addPlugin(tychoJarPlugin);
		
		Plugin tychoResourcesPlugin = addTychoResourcesPlugin(); 		
		build.addPlugin( tychoResourcesPlugin );
		
		Plugin tychoPackagingPlugin = addTychoPackagingPlugin();
		build.addPlugin(tychoPackagingPlugin);
		
		Plugin tychoTargetPlugin = addTychoTargetPlatformPlugin();
		build.addPlugin(tychoTargetPlugin);
		
		Plugin validatorPlugin = addBWPluginsPlugin( true , false );
		build.addPlugin(validatorPlugin);
		
		PluginManagement manage = new PluginManagement();
		Plugin m2ePlugin = addm2ePlugin();
		manage.addPlugin(m2ePlugin);
		
		build.setPluginManagement(manage);
		
		model.setBuild(build);
	}
	


	

	private Plugin addTychoTargetPlatformPlugin() 
	{

		Plugin plugin = new Plugin();
		plugin.setGroupId("org.eclipse.tycho");
		plugin.setArtifactId("target-platform-configuration");
		plugin.setVersion("${tycho-version}");

		Xpp3Dom config = new Xpp3Dom("configuration");
		Xpp3Dom resolver = new Xpp3Dom("resolver");

		resolver.setValue("p2");

		Xpp3Dom depRes = new Xpp3Dom("dependency-resolution");
		Xpp3Dom extraReq = new Xpp3Dom("extraRequirements");

		for( Capability str : bwinfo.getCapabilities())
		{
			Xpp3Dom req = addRequirement( str.getName() , str.getVersion() ) ;
			extraReq.addChild(req);
		}
		
		for( BWSharedModuleInfo sharedModule : shared)
		{
			Xpp3Dom req = addRequirement( sharedModule.getName() , sharedModule.getVersion().substring( 0 , (sharedModule.getVersion().lastIndexOf("-")  ) ) ) ;
			extraReq.addChild(req);
		}
		
		for( BWOSGiModuleInfo osgiModule : osgi)
		{
			Xpp3Dom req = addRequirement( osgiModule.getName() , osgiModule.getVersion().substring( 0 , (osgiModule.getVersion().lastIndexOf("-")  ) )) ;
			extraReq.addChild(req);
		}
		
		depRes.addChild(extraReq);

		Xpp3Dom envs = addEnvironments();
		
		config.addChild(resolver);
		config.addChild(depRes);
		config.addChild(envs);
		
		plugin.setConfiguration(config);
		
		return plugin;
	}



}

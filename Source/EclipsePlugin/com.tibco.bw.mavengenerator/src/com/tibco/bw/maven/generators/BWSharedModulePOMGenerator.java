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

import com.tibco.bw.maven.utils.BWSharedModuleInfo;

public class BWSharedModulePOMGenerator extends BWPOMGenerator
{

	private BWSharedModuleInfo bwinfo;
	
	public BWSharedModulePOMGenerator(BWSharedModuleInfo bwinfo)
	{
		super(bwinfo);
		this.bwinfo = bwinfo;	
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
			e.printStackTrace();
		}
	}
	

	protected void addProperties()
	{
		super.addProperties();
		model.addProperty("tycho-version", "0.20.0");
		model.addProperty("main.p2.repo", "${tibco.home}/bw/${bw.version}/maven/p2repo");
		model.addProperty("shared.p2.repo", "${tibco.home}/bw/${bw.version}/maven/sharedmodulerepo");
		

	}
	
	private void addRepoInfo()
	{
		addMainRepoInfo();
		
		Repository reposhared = new Repository();
		reposhared.setId("shared.bw.bundle");
		reposhared.setUrl("file:///${shared.p2.repo}");
		reposhared.setLayout("p2");

		model.getRepositories().add(reposhared);		
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
		
		Plugin validatorPlugin = addBWPluginsPlugin( true , true );
		build.addPlugin(validatorPlugin);
		
		PluginManagement manage = new PluginManagement();
		Plugin m2ePlugin = addm2ePlugin();
		manage.addPlugin(m2ePlugin);
		
		build.setPluginManagement(manage);
		
		model.setBuild(build);
	}
	

	
	private Plugin addTychoTargetPlatformPlugin() 
	{

		Plugin plugin1 = new Plugin();
		plugin1.setGroupId("org.eclipse.tycho");
		plugin1.setArtifactId("target-platform-configuration");
		plugin1.setVersion("${tycho-version}");

		Xpp3Dom config = new Xpp3Dom("configuration");
		Xpp3Dom resolver = new Xpp3Dom("resolver");

		resolver.setValue("p2");

		Xpp3Dom depRes = new Xpp3Dom("dependency-resolution");
		Xpp3Dom extraReq = new Xpp3Dom("extraRequirements");

		for( String str : bwinfo.getCapabilities())
		{
			Xpp3Dom req = addRequirement( str ) ;
			extraReq.addChild(req);
		}
		depRes.addChild(extraReq);

		Xpp3Dom envs = addEnvironments();
		
		config.addChild(resolver);
		config.addChild(depRes);
		config.addChild(envs);
		
		plugin1.setConfiguration(config);
		
		return plugin1;

	}

}

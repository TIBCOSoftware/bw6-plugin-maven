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
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.model.Build;
import org.apache.maven.model.Model;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.PluginExecution;
import org.apache.maven.model.Repository;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.codehaus.plexus.util.xml.Xpp3Dom;

import com.tibco.bw.maven.utils.BWModuleInfo;

public abstract class BWPOMGenerator implements IBWPOMGenerator
{
	protected Model model = new Model();
	
	protected BWModuleInfo info;
	
	public BWPOMGenerator( BWModuleInfo info )
	{
		this.info = info;
	}

	
	protected void addPrimaryTags( String packaging )
	{
    	model.setGroupId( info.getGroupId() == null ? "tibco.bw" :  info.getGroupId() );
    	model.setArtifactId( info.getArtifactId() == null ? info.getName() : info.getArtifactId());
    	model.setVersion( info.getVersion());
    	model.setPackaging( packaging );
    	model.setModelVersion( "4.0.0" );
	}
	
	protected void addProperties()
	{
		model.addProperty("tibco.home", info.getTibcoHome());
		model.addProperty("bw.version", info.getBwVersion());	

	}
	
	protected void generatePOMFile() throws Exception
	{
		FileWriter writer = new FileWriter( info.getPomfileLocation());
		new MavenXpp3Writer().write(writer, model);
	}

	protected void addMainRepoInfo()
	{
		Repository repo = new Repository();
		repo.setId("main.bw.bundle");
		repo.setUrl("file:///${main.p2.repo}");
		repo.setLayout("p2");
		
		model.getRepositories().add(repo);

	}

	
	protected Plugin addTychoMavenPlugin()
	{
		
		Plugin plugin = new Plugin();
		plugin.setGroupId("org.eclipse.tycho");
		plugin.setArtifactId("tycho-maven-plugin");
		plugin.setVersion("${tycho-version}");
		plugin.setExtensions("true");

		return plugin;
	}
	
	protected Plugin addTychoJARPlugin()
	{
		Plugin plugin = new Plugin();
		plugin.setGroupId("org.apache.maven.plugins");
		plugin.setArtifactId("maven-jar-plugin");
		plugin.setVersion("2.4");
		
		return plugin;

	}
	
	protected Plugin addTychoResourcesPlugin()
	{
		Plugin plugin = new Plugin();
		plugin.setGroupId("org.apache.maven.plugins");
		plugin.setArtifactId("maven-resources-plugin");
		plugin.setVersion("2.6");
		
    	Xpp3Dom dom = new Xpp3Dom("configuration");
    	Xpp3Dom child = new Xpp3Dom("encoding") ;
    	child.setValue("UTF-8");
    	dom.addChild(child);
    	
    	plugin.setConfiguration(dom);

    	return plugin;
	}
	
	protected Plugin addTychoPackagingPlugin()
	{
		 
		Plugin plugin = new Plugin();
		plugin.setGroupId("org.eclipse.tycho");
		plugin.setArtifactId("tycho-packaging-plugin");
		plugin.setVersion("${tycho-version}");

    	Xpp3Dom config = new Xpp3Dom("configuration");
    	Xpp3Dom archive = new Xpp3Dom("archive") ;
    	Xpp3Dom mvnDesc = new Xpp3Dom("addMavenDescriptor");
    	
    	mvnDesc.setValue("false");
    	
    	archive.addChild(mvnDesc);
    	config.addChild(archive);
    	
    	plugin.setConfiguration(config);
		
    	PluginExecution pluginExecution = new PluginExecution();
    	pluginExecution.setId("default-package-plugin");
    	pluginExecution.setPhase("package");
    	pluginExecution.addGoal("package-plugin");
    	
    	Xpp3Dom exConfig = new Xpp3Dom("configuration");
    	Xpp3Dom finalName =  new Xpp3Dom("finalName") ;
    	finalName.setValue("${project.artifactId}_${unqualifiedVersion}.${buildQualifier}");
    	
    	exConfig.addChild(finalName);
    	pluginExecution.setConfiguration(exConfig);
    	
		plugin.addExecution(pluginExecution);
    	
		return plugin;
			
	}
	
	protected Plugin addBWPluginsPlugin( boolean addValidator, boolean addSharedRepoPublisher ) {
		Plugin plugin = new Plugin();
		plugin.setGroupId("com.tibco.bw");
		plugin.setArtifactId("bw-archiver");
		plugin.setVersion("1.0.0");
		plugin.setExtensions("true");
		
		List<PluginExecution> execList = new ArrayList<PluginExecution>();
		
		if( addValidator )
		{
			PluginExecution exec = new PluginExecution();
			exec.setId("bw-validator");
			exec.setPhase("prepare-package");
			exec.addGoal("bw-validator");
			
			execList.add(exec);
			
		}
		
		if(addSharedRepoPublisher )
		{
			PluginExecution execPublish = new PluginExecution();
			execPublish.setId("bw-sharedrepopublisher");
			execPublish.setPhase("package");
			execPublish.addGoal("bw-sharedrepopublisher");
			
			execList.add(execPublish);
			
		}
		
		plugin.setExecutions(execList);

		return plugin;
	}
	
	protected Plugin addm2ePlugin()
	{
		Plugin plugin = new Plugin();
		plugin.setGroupId("org.eclipse.m2e");
		plugin.setArtifactId("lifecycle-mapping");
		plugin.setVersion("1.0.0");
		
		Xpp3Dom configuration = new Xpp3Dom("configuration");
		
		Xpp3Dom lifecycleMappingMetadata = new Xpp3Dom("lifecycleMappingMetadata");
		
		Xpp3Dom pluginExecutions = new Xpp3Dom("pluginExecutions");
		
		Xpp3Dom pluginExCompiler = getPluginExecutionCompiler();
		
		Xpp3Dom pluginExValidate = getPluginExecutionValidate();
		
		pluginExecutions.addChild(pluginExCompiler);
		
		pluginExecutions.addChild(pluginExValidate);
		
		lifecycleMappingMetadata.addChild(pluginExecutions);
		
		configuration.addChild(lifecycleMappingMetadata );
		
		plugin.setConfiguration( configuration );
		
		return plugin;
	}

	private Xpp3Dom getPluginExecutionCompiler() 
	{
		Xpp3Dom pluginExecution = new Xpp3Dom("pluginExecution");
		
		Xpp3Dom pluginExecutionFilter = new Xpp3Dom("pluginExecutionFilter");
		
		Xpp3Dom groupId1 = new Xpp3Dom("groupId");
		groupId1.setValue("org.eclipse.tycho");
		
		Xpp3Dom artifactId1 = new Xpp3Dom("artifactId");
		artifactId1.setValue("tycho-compiler-plugin");
		
		Xpp3Dom version1 = new Xpp3Dom("versionRange");
		version1.setValue("[0.20.0,)");
		
		Xpp3Dom goals1 = new Xpp3Dom("goals");
		
		Xpp3Dom goal1 = new Xpp3Dom("goal");
		goal1.setValue("compile");
		
		goals1.addChild(goal1);
		
		pluginExecutionFilter.addChild(groupId1);
		pluginExecutionFilter.addChild(artifactId1);
		pluginExecutionFilter.addChild(version1);
		pluginExecutionFilter.addChild(goals1);
		
		pluginExecution.addChild(pluginExecutionFilter);
		
		
		Xpp3Dom action1 = new Xpp3Dom("action");
		
		Xpp3Dom ignore = new Xpp3Dom("ignore");
		
		action1.addChild(ignore);
		
		pluginExecution.addChild(action1);
		
		return pluginExecution;
	}
	

	private Xpp3Dom getPluginExecutionValidate()
	{
		Xpp3Dom pluginExecution1 = new Xpp3Dom("pluginExecution");
		
		Xpp3Dom pluginExecutionFilter1 = new Xpp3Dom("pluginExecutionFilter");
		
		Xpp3Dom groupId1 = new Xpp3Dom("groupId");
		groupId1.setValue("org.eclipse.tycho");
		
		Xpp3Dom artifactId1 = new Xpp3Dom("artifactId");
		artifactId1.setValue("tycho-packaging-plugin");
		
		Xpp3Dom version1 = new Xpp3Dom("versionRange");
		version1.setValue("[0.20.0,)");
		
		Xpp3Dom goals1 = new Xpp3Dom("goals");
		
		Xpp3Dom goal1 = new Xpp3Dom("goal");
		goal1.setValue("build-qualifier");
		

		Xpp3Dom goal2 = new Xpp3Dom("goal");
		goal2.setValue("validate-id");

		Xpp3Dom goal3 = new Xpp3Dom("goal");
		goal3.setValue("validate-version");
		
		goals1.addChild(goal1);
		goals1.addChild(goal2);
		goals1.addChild(goal3);
		
		pluginExecutionFilter1.addChild(groupId1);
		pluginExecutionFilter1.addChild(artifactId1);
		pluginExecutionFilter1.addChild(version1);
		pluginExecutionFilter1.addChild(goals1);
		
		pluginExecution1.addChild(pluginExecutionFilter1);
		
		
		Xpp3Dom action1 = new Xpp3Dom("action");
		
		Xpp3Dom ignore = new Xpp3Dom("ignore");
		
		action1.addChild(ignore);
		
		pluginExecution1.addChild(action1);
		
		return pluginExecution1;
	}

	protected Xpp3Dom addEnvironments() 
	{
		Xpp3Dom envs = new Xpp3Dom("environments");
		Xpp3Dom envWin = addEnvironment("win32", "win32" , "x86");
		Xpp3Dom envLinux = addEnvironment("linux", "gtk" , "x86_64");
		Xpp3Dom envMac = addEnvironment("macosx", "cocoa" , "x86_84");
		
		envs.addChild(envWin);
		envs.addChild(envLinux);
		envs.addChild(envMac);
		
		return envs;
	}
	
	protected Xpp3Dom addEnvironment(String osVal, String wsVal, String archVal )
	{
		Xpp3Dom env = new Xpp3Dom("environment");
		
		Xpp3Dom os = new Xpp3Dom("os");
		os.setValue( osVal );

		Xpp3Dom ws = new Xpp3Dom("ws");
		ws.setValue( wsVal );

		Xpp3Dom arch = new Xpp3Dom("arch");
		arch.setValue( archVal );

		env.addChild(os);
		env.addChild(ws);
		env.addChild(arch);
		
		return env;
		
	}


	protected Xpp3Dom addRequirement(String str) 
	{
		return addRequirement(str, "6.0.0");		
		
		
	}

	protected Xpp3Dom addRequirement(String str , String version) 
	{
		Xpp3Dom req = new Xpp3Dom("requirement");
		
		Xpp3Dom type = new Xpp3Dom("type");
		type.setValue("eclipse-plugin");

		Xpp3Dom id = new Xpp3Dom("id");
		id.setValue( str );

		Xpp3Dom versionRange = new Xpp3Dom("versionRange");
		versionRange.setValue(version);

		req.addChild(type);
		req.addChild(id);
		req.addChild(versionRange);
		
		return req;
		
	}



}

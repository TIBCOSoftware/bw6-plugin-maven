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

import com.tibco.bw.maven.utils.BWAppModuleInfo;
import com.tibco.bw.maven.utils.BWOSGiModuleInfo;
import com.tibco.bw.maven.utils.BWProjectInfo;
import com.tibco.bw.maven.utils.BWSharedModuleInfo;

/**
 * 
 * Process the Project information and generates the POM file for each of the Project.
 * 
 * @author Ashutosh
 * 
 * @version 1.0
 * 
 */
public class BWPOMProcessor implements IBWPOMProcessor
{


	private BWProjectInfo projectInfo;
	
	
	public BWPOMProcessor(BWProjectInfo projectInfo)
	{
		this.projectInfo = projectInfo;
	}
	
	/**
	 * 
	 */
	public void process()
	{
	
		BWApplicationPOMGenerator appPomGen = new BWApplicationPOMGenerator(projectInfo.getAppInfo());
		appPomGen.generate();
		
		for(BWAppModuleInfo module : projectInfo.getAppInfo().getAppModules())
		{
			IBWPOMGenerator moduleGen = new BWAppModulePOMGenerator( module , projectInfo.getAppInfo().getSharedModules() , projectInfo.getAppInfo().getOsgiModules() );
			moduleGen.generate();
		}
		
		for(BWSharedModuleInfo module : projectInfo.getAppInfo().getSharedModules())
		{
			IBWPOMGenerator moduleGen = new BWSharedModulePOMGenerator( module );
			moduleGen.generate();
		}

		
		for(BWOSGiModuleInfo module : projectInfo.getAppInfo().getOsgiModules())
		{
			IBWPOMGenerator moduleGen = new BWOSGiModulePOMGenerator( module );
			moduleGen.generate();
		}

	}

	public BWProjectInfo getProjectInfo() 
	{
		return projectInfo;
	}

	public void setProjectInfo(BWProjectInfo projectInfo) 
	{
		this.projectInfo = projectInfo;
	}
	
}


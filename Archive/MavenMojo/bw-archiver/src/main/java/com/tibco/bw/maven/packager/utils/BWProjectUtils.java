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

package com.tibco.bw.maven.packager.utils;

import java.io.File;
import java.io.FileInputStream;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;

import org.apache.maven.plugin.MojoExecutionException;

public class BWProjectUtils 
{


	public static String getModuleVersion( File jarFile ) throws Exception
	{
		JarInputStream jarStream = new JarInputStream( new FileInputStream( jarFile ));
		Manifest moduleManifest = jarStream.getManifest();
		jarStream.close();
		
		return moduleManifest.getMainAttributes().getValue("Bundle-Version");

	}

	
	public static String getAdminExecutable()
	{
		String os = System.getProperty("os.name");
		if (os.indexOf("Windows") != -1) 
		{
			return "/bwadmin.exe";
		}

		return "/bwadmin";
	}

	
	public static File getBWAdminHome( String tibcoHome , String bwVersion ) throws Exception
	{
		
		File bwAdminHome = new File ( new File(tibcoHome) , "bw/"  + bwVersion + "/bin/" );
		
		if(bwAdminHome.exists())
		{
			return bwAdminHome;
		}
		
		throw new MojoExecutionException("Failed to find Admin Home at location : " + bwAdminHome );
		
		
	}
	
	
}

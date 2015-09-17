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
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.logging.Log;

/**
 * Publishes the Module JAR to the P2 Repository.
 * First the JAR is copied to the Temporary location.
 * From the Temporary location the JAR is Published to the P2 Repository using the P2 Publisher.
 * 
 * @author Ashutosh
 * 
 * @version 1.0
 *
 */
public class BWP2Publisher 
{
	
	private String tibcoHome;
	
    private String tibcoSharedP2Home;
    
    private String tibcoSharedTempHome; 
	
    private Log log;

    public BWP2Publisher( String tibcoHome, String tibcoSharedP2Home , String tibcoSharedTempHome , Log log )
    {
    	this.tibcoHome = tibcoHome;
    	this.tibcoSharedP2Home = tibcoSharedP2Home;
    	this.tibcoSharedTempHome = tibcoSharedTempHome;
    	this.log = log;
    	
    }
	
	public void publishToP2( File jar ) throws Exception
	{
		copyJARToTemp(jar);
		checkForP2Home();
		String equinoxLauncher = getEquinoxLauncher();
		if(equinoxLauncher == null )
		{
			throw new Exception( "Failed to find Equinox Launcher. Tibco Home might be wrong in the POM file. Please correct TibcoHome location");
		}
		
		List<String> list = new ArrayList<String>();
        

		list.add( "java"); 	
		list.add("-jar");

		list.add( equinoxLauncher );
		
		list.add("-install");
		list.add(tibcoHome + "/studio/3.6/eclipse");
		
		list.add("-application");
		list.add("org.eclipse.equinox.p2.publisher.FeaturesAndBundlesPublisher");
		
		list.add("-metadataRepository");
		list.add("file://" + tibcoSharedP2Home);
		
		list.add("-artifactRepository");
		list.add("file://" + tibcoSharedP2Home );
		
		list.add("-source");
		list.add( tibcoSharedTempHome);
		
		list.add("-append");
		list.add("-publishArtifacts");

		ProcessExecutor executor = new ProcessExecutor( tibcoSharedTempHome , log );
		String response = executor.executeProcess( list );

		log.info( response );
	}
	
	/**
	 * Get the Equinox Launcher.
	 * Equinox Launcher JAR is present under the "eclipse-platform/bundlepool" in case of V-Build
	 * Equinox Launcher JAR is present under the "studio/3.6/eclipse" in case of Dev-Build
	 * @return the path of the Equinox Launcher
	 */
	private String getEquinoxLauncher()
	{
		String equuinoxLauncher = tibcoHome + "/eclipse-platform/bundlepool/1.0/org.eclipse.equinox.p2.touchpoint.eclipse/plugins/org.eclipse.equinox.launcher_1.2.0.v20110502.jar";
				
		//Check the V-Build location 
		File file = new File ( equuinoxLauncher );
		if(file.exists() )
		{
			return equuinoxLauncher;
		}
		
		equuinoxLauncher = tibcoHome +  "/studio/3.6/eclipse/plugins/org.eclipse.equinox.launcher_1.2.0.v20110502.jar";
		
		// Check the Dev-Build location
		file = new File (equuinoxLauncher );
		
		if(file.exists() )
		{
			return equuinoxLauncher;
		}
		
		return null;
	}
	
	
    /**
     * Copy the Module JAR File to the Temp location for P2 Installation.
     * @param jar
     * @throws Exception
     */
    private void copyJARToTemp( File jar ) throws Exception
    {
    	
    	File file = new File( tibcoSharedTempHome );
    	
    	if( !file.exists() )
    	{
    		file.mkdir();
    	}
    	
    	File plugins = new File( file , "plugins");
    	if( plugins.exists() )
    	{
    		FileUtils.cleanDirectory(plugins);    		
    	}
    	else
    	{
    		plugins.mkdir();
    	}
    	FileUtils.copyFile(jar, new File( plugins ,  jar.getName()) );
    	
    }


    private void checkForP2Home() throws Exception
    {
    	File file = new File( tibcoSharedP2Home );
    	if( !file.exists() )
    	{
    		file.createNewFile();
    	}
    }

}

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

package com.tibco.bw.maven.packager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Properties;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Execute;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectBuilder;
import org.apache.maven.settings.Settings;


/**
 * This Mojo is executed before the packaging stage.
 * The JAR by the Tycho is built in the "target" folder. The target folder also gets included 
 * in the build.properties. 
 * So while packaging the Tycho tries to package the contents of "target" due to which the 
 * packager gives the exception.
 * So this Mojo removes the target folder from the build.properties 
 * 
 * @author Ashutosh
 * 
 * @version 1.0
 * 
 * 
 */
@Mojo( name="bw-validator", defaultPhase=LifecyclePhase.PREPARE_PACKAGE  )
@Execute(goal="bw-validator", phase= LifecyclePhase.PREPARE_PACKAGE)
public class BWAppModuleValidator extends AbstractMojo
{

	@Parameter( property="project.build.directory")
    private File outputDirectory;
    
	@Parameter( property="project.basedir")
	private File projectBasedir;
	
    @Component
    private MavenSession session;

    @Component
    private MavenProject project;

    @Component
    private MojoExecution mojo;


    @Component
    private ProjectBuilder builder;

    @Component
    private Settings settings;

    /**
     * Execute method.
     */
	public void execute() throws MojoExecutionException, MojoFailureException 
	{
		validateProperties();
	}
    
	
	/**
	 * Removes the target folder from the build.properties  
	 */
	private void validateProperties() 
	{
		try {
			Properties prop = new Properties();

			//Read the build.properties
			File file = new File(projectBasedir, "build.properties");
			InputStream in = new FileInputStream(file);
			prop.load(in);
			in.close();
			
			//Remove the target entry from the build.properties 
			String updated = ((String) prop.get("bin.includes")).replaceAll( ",target/", "");
			prop.put("bin.includes", updated);
			
			//Write back the build.properties
			prop.store(new FileOutputStream(file), null);
			

		} 
		catch (Exception e) 
		{
			//Silent the Exception.No need to stop execution.
			getLog().warn( "Failed to update build.properties due to " + e.getMessage() );
			getLog().warn( e );
		}
	}
	
	
}

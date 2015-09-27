package com.tibco.bw.maven.plugin.process;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.codehaus.plexus.logging.Logger;

public class MvnInstallExecutor
{

	private Logger logger;
	public MvnInstallExecutor( Logger logger )
	{
		this.logger = logger;
	
	}
	
	public void execute( Model model , File jarFile ) 
	{
		List<String> list = new ArrayList<String>();
        

		list.add( "mvn.bat"); 	
		list.add("install:install-file");
		

		list.add("-Dfile=" + jarFile.getAbsolutePath() );
		
		list.add("-DgroupId=tempbw");
		
		list.add("-DartifactId=" + jarFile.getName().substring(0,jarFile.getName().lastIndexOf(".")) );
		
		list.add("-Dversion=0.0.0");
		
		list.add("-Dpackaging=jar");

		ProcessExecutor executor = new ProcessExecutor( System.getProperty("user.home") , logger );
		try
		{
			executor.executeProcess( list );	
		}
		catch(Exception e )
		{
			logger.error( "Failed to install JAR file to Maven Repository" );
			return;
		}
		
		Dependency dep = new Dependency();
		dep.setGroupId("tempbw");
		dep.setArtifactId(jarFile.getName().substring(0,jarFile.getName().lastIndexOf(".")));
		dep.setVersion("0.0.0");
		
		model.addDependency(dep);

		logger.debug("Set the Dependency to Model");
	}
	
}

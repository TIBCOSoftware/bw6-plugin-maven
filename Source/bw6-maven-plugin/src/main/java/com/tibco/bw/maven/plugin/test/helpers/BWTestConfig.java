package com.tibco.bw.maven.plugin.test.helpers;

import java.io.File;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;

public class BWTestConfig 
{

	public static BWTestConfig INSTANCE = new BWTestConfig();

	private File configDir;

	private Process engineProcess;
	
	private List<String> launchConfig;

	private String tibcoHome;
	
	private String bwHome;
	
	private MavenSession session;
	
	private MavenProject project;
	
	private Log logger;
	
	private BWTestConfig()
	{
		
	}
	
	public  void reset()
	{
		INSTANCE = new BWTestConfig();
	}
	
	public void init( String tibcoHome , String bwHome , MavenSession session , MavenProject project , Log logger ) throws Exception 
	{
		this.tibcoHome = tibcoHome;
		this.session = session;
		this.project = project;
		this.bwHome = bwHome;
		this.logger = logger;
		
		initConfig();
	}
	
	private void initConfig() throws Exception
	{
		String temp = System.getProperty( "java.io.tmpdir" );
		File file = new File( temp );
		
		configDir = new File( file , "bwconfig");
		
		configDir.mkdir();
		
		FileUtils.cleanDirectory(configDir );
	}

	public File getConfigDir() 
	{
		return configDir;
	}

	public void setConfigDir(File configDir) 
	{
		this.configDir = configDir;
	}

	public Process getEngineProcess() 
	{
		return engineProcess;
	}

	public void setEngineProcess(Process engineProcess)
	{
		this.engineProcess = engineProcess;
	}

	public List<String> getLaunchConfig() 
	{
		return launchConfig;
	}

	public void setLaunchConfig(List<String> launchConfig)
	{
		this.launchConfig = launchConfig;
	}

	public String getTibcoHome() 
	{
		return tibcoHome;
	}

	public void setTibcoHome(String tibcoHome) 
	{
		this.tibcoHome = tibcoHome;
	}

	public String getBwHome() 
	{
		return bwHome;
	}

	public void setBwHome(String bwHome)
	{
		this.bwHome = bwHome;
	}

	public MavenSession getSession()
	{
		return session;
	}

	public void setSession(MavenSession session) 
	{
		this.session = session;
	}

	public MavenProject getProject() 
	{
		return project;
	}

	public void setProject(MavenProject project) 
	{
		this.project = project;
	}

	public Log getLogger() 
	{
		return logger;
	}

	public void setLogger(Log logger) 
	{
		this.logger = logger;
	}
	
}

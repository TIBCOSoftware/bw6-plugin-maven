package com.tibco.bw.maven.plugin.test.setuplocal;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;

import com.tibco.bw.maven.plugin.test.helpers.BWTestConfig;
import com.tibco.bw.maven.plugin.test.rest.BWTestRunner;


public class BWTestExecutor 
{
	
	
	public void execute() throws MojoFailureException , Exception
	{
		try
		{
			
			initialize();
			
			runEngine();
			
			runTests();
			
    		
		}
		catch(Exception e )
		{
			throw e;
		}
		finally
		{
			if( BWTestConfig.INSTANCE.getEngineProcess() != null )
			{
				BWTestConfig.INSTANCE.getEngineProcess().destroyForcibly();
			}
			if( BWTestConfig.INSTANCE.getConfigDir() != null )
			{
				BWTestConfig.INSTANCE.getConfigDir().delete();
			}
			
		}
		
	}
	
	
	private void initialize() throws Exception
	{
		
		
		EngineLaunchConfigurator config = new EngineLaunchConfigurator();
		config.loadConfiguration();
		
		ConfigFileGenerator gen = new ConfigFileGenerator();
		gen.generateConfig();
		
		
	}
	
	private void runEngine() throws Exception
	{
		EngineRunner runner = new EngineRunner();
		runner.run();
	}
	
	private void runTests() throws MojoFailureException , Exception
	{
		BWTestRunner runner = new BWTestRunner("localhost", 8090 );
		runner.runTests();
	}
	
}

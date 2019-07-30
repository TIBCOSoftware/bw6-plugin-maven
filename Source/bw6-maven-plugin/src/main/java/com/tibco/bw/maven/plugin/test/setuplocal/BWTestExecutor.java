package com.tibco.bw.maven.plugin.test.setuplocal;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

import com.tibco.bw.maven.plugin.test.helpers.BWTestConfig;
import com.tibco.bw.maven.plugin.test.helpers.TestFileParser;
import com.tibco.bw.maven.plugin.test.rest.BWTestRunner;
import com.tibco.bw.maven.plugin.utils.BWFileUtils;


public class BWTestExecutor 
{
	public static BWTestExecutor INSTANCE = new BWTestExecutor();
	
	int engineDebugPort;
	
	List<String> mockActivity = new ArrayList<String>();
	
	
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
	
	public void collectMockActivityName() throws Exception,FileNotFoundException
	{
		List<MavenProject> projects = BWTestConfig.INSTANCE.getSession().getProjects();
		for( MavenProject project : projects )
		{
			if( project.getPackaging().equals("bwmodule") )
			{
				File baseDir = project.getBasedir();
				List<File> files = BWFileUtils.getEntitiesfromLocation( baseDir.toString() , "bwt");
				for( File file : files )
				{
					List<String> tempList = new ArrayList<String>();
					String assertionxml = FileUtils.readFileToString( file );
					tempList = TestFileParser.INSTANCE.collectMockActivities(assertionxml);
					if(!tempList.isEmpty()){
						mockActivity.addAll(tempList);
						BWTestExecutor.INSTANCE.setMockActivityList(mockActivity);;
						
					}

				}
			}

		}
	}
	
	private void initialize() throws Exception
	{
		
		collectMockActivityName();
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
		BWTestRunner runner = new BWTestRunner("localhost", BWTestExecutor.INSTANCE.getEngineDebugPort() );
		runner.runTests();
	}
	
	public void setEngineDebugPort(int engineDebugPort){
		this.engineDebugPort = engineDebugPort;
	}

	public int getEngineDebugPort(){
		return engineDebugPort;
	}
	
	public List<String> getMockActivityList() {
		return mockActivity;
	}

	public void setMockActivityList(List<String> mockActivity) {
		this.mockActivity = mockActivity;
	}
	
}

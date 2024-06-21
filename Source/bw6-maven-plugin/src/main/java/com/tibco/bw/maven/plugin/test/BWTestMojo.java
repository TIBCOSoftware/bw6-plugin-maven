package com.tibco.bw.maven.plugin.test;

import java.io.File;
import java.util.List;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectDependenciesResolver;

import com.tibco.bw.maven.plugin.test.helpers.BWTestConfig;
import com.tibco.bw.maven.plugin.test.helpers.TestFileParser;
import com.tibco.bw.maven.plugin.test.setuplocal.BWTestExecutor;
import com.tibco.bw.maven.plugin.utils.BWFileUtils;

@Mojo(name = "bwtest", defaultPhase = LifecyclePhase.TEST)
public class BWTestMojo extends AbstractMojo {
	@Parameter(defaultValue="${session}", readonly=true)
    private MavenSession session;

	@Parameter(defaultValue="${project}", readonly=true)
    private MavenProject project;

    @Parameter( property = "testFailureIgnore", defaultValue = "false" )
    private boolean testFailureIgnore;
    
    @Parameter( property = "skipTests", defaultValue = "false" )
    protected boolean skipTests;

    @Parameter( property = "failIfNoTests" , defaultValue = "true" )
    private boolean failIfNoTests;
    
    @Parameter( property = "disableMocking" , defaultValue = "false" )
    private boolean disableMocking;
    
    @Parameter( property = "disableAssertions" , defaultValue = "false" )
    private boolean disableAssertions;
    
    @Parameter( property = "engineDebugPort" , defaultValue = "8090" )
    private int engineDebugPort;
    
    @Parameter( property = "showFailureDetails" , defaultValue = "true" )
    private boolean showFailureDetails;
    
    @Parameter( property = "testSuiteName" , defaultValue = "" )
    private String testSuiteName;
    
    @Parameter( property = "engineStartupWaitTime" , defaultValue = "2" )
    private int engineStartupWaitTime;
    
    @Parameter( property = "osgiCommands" )
    private List<String> osgiCommands;
    
    @Parameter( property = "skipInitMainProcessActivities" , defaultValue = "false" )
    private boolean skipInitMainProcessActivities;
    
    @Parameter( property = "skipInitAllNonTestProcessActivities" , defaultValue = "false" )
    private boolean skipInitAllNonTestProcessActivities;
    
    @Parameter( property = "independentComponentStartup" , defaultValue = "false" )
    private boolean independentComponentStartup;
    
    @Parameter( property = "customArgEngine"  )
    private String customArgEngine;
    
    @Parameter( property = "runESMTest" , defaultValue = "false" )
    private boolean runESMTest;
    
    @Parameter( property = "ESMtestSuiteName" , defaultValue = "" )
    private String ESMtestSuiteName;
    
    @Parameter( property = "skippedTestError" , defaultValue = "false" )
    private boolean skippedTestError;
    
    @Component
    ProjectDependenciesResolver resolver;
    
    
    
    public void execute() throws MojoExecutionException , MojoFailureException
    {
    	
    	BWTestExecutor executor = new BWTestExecutor(session, resolver);
    	try 
    	{
    	
    		session.getProjects();
    		String property = System.getProperty("java.version");
    		String javapath = System.getProperty("java.home");
    		getLog().info("Executing test Using java lib from " + javapath +" Java Version " + property);
    		
    		if( !verifyParameters() )
    		{
    			return;
    		}
    		   		
    		initialize();
    		
			executor.execute();
		}
    	catch (Exception e) 
    	{

    		if( e instanceof MojoFailureException )
    		{
    			if( ! testFailureIgnore )
    			{
        			throw (MojoFailureException)e;

    			}
    			else
    			{
    				System.out.println( "Ignoring the exception for generating the report");
    			}
    		}
    		else
    		{
    			
    			e.printStackTrace();
    			throw new MojoExecutionException( e.getMessage(), e);
    		}
		}
    	
    }

    
    
    private boolean verifyParameters() throws Exception
    {
    	
    	
    	if( isSkipTests() )
    	{
			getLog().info( "-------------------------------------------------------" );
    		getLog().info( "Skipping Test phase.");
			getLog().info( "-------------------------------------------------------" );

    		
    		return false;
    	}
    	
    	String tibcoHome = project.getProperties().getProperty("tibco.Home");
		String bwHome = project.getProperties().getProperty("bw.Home");

		if( tibcoHome == null || tibcoHome.isEmpty() || bwHome == null || bwHome.isEmpty() )
		{
			getLog().info( "-------------------------------------------------------" );
			getLog().info( "TIBCO Home or BW Home is not provided. Skipping Test Phase.");
			getLog().info( "-------------------------------------------------------" );

			return false;
		}
		
		File file = new File( tibcoHome + bwHome );
		if( !file.exists() || !file.isDirectory()  )
		{
			getLog().info( "-------------------------------------------------------" );
			getLog().info( "Provided TibcoHome directory - "+ (tibcoHome + bwHome) +" is invalid. Skipping Test Phase.");
			getLog().info( "-------------------------------------------------------" );
	
			return false;
		}
		boolean exists = checkForTest();
		
    	
    	if( getFailIfNoTests() )
    	{
    		if(!exists )
    		{
    			throw new MojoFailureException( "No Test files existing in any of the Module." );
    		}
    	}
    	else
    	{
    		if( !exists )
    		{
    			getLog().info( "-------------------------------------------------------" );
    			getLog().info( "No Tests found in any Module. Skipping Test Phase.");
    			getLog().info( "-------------------------------------------------------" );
    			return false;
    		}
    	}
    		
    	
    	
    	return true;
    	
    }
    
    
    private boolean checkForTest()
    {
    	List<MavenProject> projects = session.getProjects();
    	
    	for( MavenProject project : projects )
		{
			if( project.getPackaging().equals("bwmodule") )
			{
				List<File> files = BWFileUtils.getEntitiesfromLocation( project.getBasedir().toString() , "bwt");
				if( files.size() > 0 )
				{
					return true;
				}
			}
		}
		getLog().info( "-------------------------------------------------------" );
		getLog().info( "No BWT Test files exist. ");
		getLog().info( "-------------------------------------------------------" );
    	
    	return false;
    }
    
    private void initialize() throws Exception
    {
		String tibcoHome = project.getProperties().getProperty("tibco.Home");
		String bwHome = project.getProperties().getProperty("bw.Home");
		
		TestFileParser.INSTANCE.setdisbleMocking(disableMocking);
		
		TestFileParser.INSTANCE.setdisbleAssertions(disableAssertions);
		
		TestFileParser.INSTANCE.setshowFailureDetails(showFailureDetails);
		
		BWTestExecutor.INSTANCE.setEngineDebugPort(engineDebugPort);
		
		BWTestExecutor.INSTANCE.setEngineStartupWaitTime(engineStartupWaitTime);
		
		BWTestExecutor.INSTANCE.setOsgiCommands(osgiCommands);
		
		BWTestExecutor.INSTANCE.setSkipInitMainProcessActivities(skipInitMainProcessActivities);
		
		BWTestExecutor.INSTANCE.setSkipInitAllNonTestProcessActivities(skipInitAllNonTestProcessActivities);
		
		BWTestExecutor.INSTANCE.setIndependentComponentStartup(independentComponentStartup);

		BWTestExecutor.INSTANCE.setCustomArgEngine(customArgEngine);
    	
		BWTestConfig.INSTANCE.reset();
    	
    	BWTestConfig.INSTANCE.setTestSuiteName(testSuiteName);
    	
        BWTestConfig.INSTANCE.setEsmTestSuiteName(ESMtestSuiteName);
    	
    	BWTestConfig.INSTANCE.setRunESMTest(runESMTest);
    	
    	BWTestConfig.INSTANCE.setResolver(resolver);
    	
    	BWTestExecutor.INSTANCE.setSkippedTestError(skippedTestError);
    	
    	
		BWTestConfig.INSTANCE.init(  tibcoHome , bwHome , session, project , getLog() );
		
		getLog().info( "" );
		getLog().info( "-------------------------------------------------------" );
		getLog().info( " Running BW Tests " );
		getLog().info( "-------------------------------------------------------" );
    }
    
    
	public boolean isSkipTests()
	{
		if( ! skipTests )
		{
			if( "true".equals(project.getProperties().get("skipTests")) )
			{
				return true;
			}
		}
		return skipTests;
	}




	public void setSkipTests(boolean skipTests)
	{
		this.skipTests = skipTests;
	}




	public boolean isTestFailureIgnore() 
	{
		return testFailureIgnore;
	}

	public void setTestFailureIgnore(boolean testFailureIgnore) 
	{
		this.testFailureIgnore = testFailureIgnore;
	}



	public boolean getFailIfNoTests() 
	{
		return failIfNoTests;
	}



	public void setFailIfNoTests(boolean failIfNoTests)
	{
		this.failIfNoTests = failIfNoTests;
	}
}

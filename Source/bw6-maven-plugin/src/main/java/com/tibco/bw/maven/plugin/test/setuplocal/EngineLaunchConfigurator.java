package com.tibco.bw.maven.plugin.test.setuplocal;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.ws.rs.ProcessingException;

import com.tibco.bw.maven.plugin.test.helpers.BWTestConfig;

public class EngineLaunchConfigurator 
{

	public void loadConfiguration() throws Exception
	{
		
		List<String> customPropertyList = null;
		BufferedReader reader = readEnvFile();
		List<String> result = null; 
		
		String customEnginePropertyFile = BWTestExecutor.INSTANCE.getCustomArgEngine();
		
		if(null != customEnginePropertyFile && !customEnginePropertyFile.isEmpty()){
			BufferedReader proertyReader = null;
			try {
				URL url = new URL(customEnginePropertyFile);
				proertyReader = new BufferedReader(
					        new InputStreamReader(url.openStream()));
			}
			catch (MalformedURLException ex){
				File file = new File(customEnginePropertyFile);
				if(!file.isAbsolute()){
					customEnginePropertyFile =	BWTestConfig.INSTANCE.getProject().getBasedir().getAbsolutePath().concat(file.getPath());
				}
				if (new File(customEnginePropertyFile).exists()) {
					proertyReader = new BufferedReader(new FileReader(customEnginePropertyFile));
				}
				else {
					throw new Exception("File Not Found " +customEnginePropertyFile);
				}

			}catch(ProcessingException e){
				throw e;
			}
			catch(Exception e){
				throw e;
			}
			
			customPropertyList =  readArguments( proertyReader );
		}
		
		if( reader == null )
		{
			throw new Exception();
		}
		
		List<String> list =  readArguments( reader );
		
		if(customPropertyList!= null && !customPropertyList.isEmpty()){
			result = Stream.concat(list.stream(), customPropertyList.stream())
					.collect(Collectors.toList()); 
		}
		else{
			result = list;
		}

		
		BWTestConfig.INSTANCE.setLaunchConfig(result);	
		
		
	}
	
	private BufferedReader readEnvFile()
	{
		
		
		
		ClassLoader classLoader = getClass().getClassLoader();
	
		InputStream stream = null;
		
		if( isWindowsOS() )
		{
			
			stream = classLoader.getResourceAsStream(  "com/tibco/resources/win_environment.properties" );
		}
		else if(isMacOS()){
			stream = classLoader.getResourceAsStream(  "com/tibco/resources/mac_environment.properties" );
		}
		else
		{
			stream = classLoader.getResourceAsStream(  "com/tibco/resources/unix_environment.properties" );	
		}
		
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
		return reader;
	}
	
	private boolean isWindowsOS()
	{
		
		String os = System.getProperty("os.name").toLowerCase();
		return (os.startsWith("windows"));
	}
	
	private boolean isMacOS()
	{
		String os = System.getProperty("os.name").toLowerCase();
		return (os.startsWith("mac"));
	}
	
	private List<String> readArguments( BufferedReader reader )
	{
		String currentLine;
		
		String currentUsersHomeDir = System.getProperty("user.home");
		File file = new File(currentUsersHomeDir + "/bwutdev.properties"); 
		boolean isDev = file.exists();
		
		List<String> list = new ArrayList<String>();		
		try
		{
			
			while ( (currentLine = reader.readLine()) != null )
			{
				if( currentLine.startsWith("#"))
				{
					continue;
				}

				if( currentLine.contains("%%TIBCO_HOME%%"))
				{
					currentLine = currentLine.replace("%%TIBCO_HOME%%", BWTestConfig.INSTANCE.getTibcoHome() );
				}
				if( currentLine.contains("%%BW_HOME%%"))
				{
					currentLine = currentLine.replace("%%BW_HOME%%", BWTestConfig.INSTANCE.getBwHome() );
					if(!BWTestExecutor.INSTANCE.getMockActivityList().isEmpty()){
						
						list.addAll(BWTestExecutor.INSTANCE.getMockActivityList());
						BWTestExecutor.INSTANCE.getMockActivityList().clear();
					}
					
					//set skipinit system properties
					if(BWTestExecutor.INSTANCE.isSkipInitMainProcessActivities())
						list.add("-Dbw.unittest.skipinit.mainprocessactivities=true");
					
					if(BWTestExecutor.INSTANCE.isSkipInitAllNonTestProcessActivities())
						list.add("-Dbw.unittest.skipinit.allnontestprocessactivities=true");
					
				}

				if( currentLine.contains("%%CONFIG_DIR%%"))
				{
					currentLine = currentLine.replace("%%CONFIG_DIR%%", BWTestConfig.INSTANCE.getConfigDir().toString().replace("\\", "/") );
				}
				if( currentLine.contains("%%ENGINE_DEBUG_PORT%%"))
				{
					currentLine = currentLine.replace("%%ENGINE_DEBUG_PORT%%", String.valueOf(BWTestExecutor.INSTANCE.getEngineDebugPort()) );
				}
				if( currentLine.equals("-dev"))
				{
					if( isDev)
					{
						list.add(currentLine);
						list.add( "file:" + file.toString());
					} else {
						list.add(currentLine);
						list.add( "file:" + BWTestConfig.INSTANCE.getConfigDir().toString().replace("\\", "/") + "/dev.properties");
					}
					continue;

				}
				
				list.add(currentLine);
				
			}

		}
		
		catch(Exception e )
		{
			e.printStackTrace();
		}
		finally
		{
			try {
				if( reader != null )
				{
					reader.close();	
				}

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return list;
	}
	
	
	
	
}

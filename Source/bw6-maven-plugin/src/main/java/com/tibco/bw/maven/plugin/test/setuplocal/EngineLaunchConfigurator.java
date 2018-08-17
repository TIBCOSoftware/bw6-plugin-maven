package com.tibco.bw.maven.plugin.test.setuplocal;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.tibco.bw.maven.plugin.test.helpers.BWTestConfig;

public class EngineLaunchConfigurator 
{

	public void loadConfiguration() throws Exception
	{
		
		
		BufferedReader reader = readEnvFile();
		
		if( reader == null )
		{
			throw new Exception();
		}
		
		List<String> list =  readArguments( reader );
		
		BWTestConfig.INSTANCE.setLaunchConfig(list);	
		
		
	}
	
	private BufferedReader readEnvFile()
	{
		
		
		
		ClassLoader classLoader = getClass().getClassLoader();
	
		InputStream stream = null;
		
		if( isUnixOS() )
		{
			stream = classLoader.getResourceAsStream(  "com/tibco/resources/unix_environment.properties" );
		}
		else
		{
			stream = classLoader.getResourceAsStream(  "com/tibco/resources/win_environment.properties" );	
		}
		
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
		return reader;
	}
	
	private boolean isUnixOS()
	{
		String os = System.getProperty("os.name");
		return !(os.startsWith("Windows"));
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
				}

				if( currentLine.contains("%%CONFIG_DIR%%"))
				{
					currentLine = currentLine.replace("%%CONFIG_DIR%%", BWTestConfig.INSTANCE.getConfigDir().toString().replace("\\", "/") );
				}
				if( currentLine.equals("-dev"))
				{
					if( isDev)
					{
						list.add(currentLine);
						list.add( "file:" + file.toString());
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

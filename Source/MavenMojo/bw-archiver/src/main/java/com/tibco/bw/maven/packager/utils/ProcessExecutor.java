package com.tibco.bw.maven.packager.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import org.apache.maven.plugin.logging.Log;

public class ProcessExecutor 
{

	private String executorHome;
	
	private Log log;
	
	public ProcessExecutor( String executorHome , Log log)	
	{
		this.executorHome = executorHome;
		this.log = log;
	}
	
	public String executeProcess(List<String> params ) throws Exception 
	{
		log.info( "Executing command =-> " + params.toString());
		
        ProcessBuilder builder = new ProcessBuilder( params);
        
        builder.directory( new File( executorHome ) );
        
        final Process process = builder.start();	

        final StringBuilder sb  = new StringBuilder();
        
        Runnable input = getInputRunnable(process, sb);
        Runnable error = getErrorRunnable(process, sb);

		Thread inputThread = new Thread( input );
		Thread errorThread = new Thread( error );
		inputThread.start();
		errorThread.start();
		
		int exitValue = process.waitFor();
		
        log.info("Exit Value is " + exitValue);
        log.info(sb.toString());
        
        return sb.toString();
	}

	private Runnable getErrorRunnable(final Process process, final StringBuilder sb) 
	{
		Runnable error = new Runnable() 
		{
			public void run() 
			{
				try
				{
					InputStream is = process.getErrorStream();
					InputStreamReader isr = new InputStreamReader(is);
					BufferedReader br = new BufferedReader(isr);
					String line;
		        
			        while ((line = br.readLine()) != null) 
			        {
			        	//log.info(line);
			            sb.append( line );
			        }
				}
		        catch(Exception e )
		        {
		        	log.error( e );
		        }
			}
		};
		return error;
	}

	private Runnable getInputRunnable( final Process process, final StringBuilder sb)
	{
		Runnable input = new Runnable() 
		{
			public void run() 
			{
				try
				{
					InputStream is = process.getInputStream(); 
					InputStreamReader isr = new InputStreamReader(is);
					BufferedReader br = new BufferedReader(isr);
					String line;		        
			        while ((line = br.readLine()) != null) 
			        {
			            sb.append( line );
			        }
				}
		        catch(Exception e )
		        {
		        	log.error( e );
		        }				
			}
		};
		return input;
	}

	
}

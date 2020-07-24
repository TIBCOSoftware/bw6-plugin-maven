package com.tibco.bw.maven.plugin.test.setuplocal;

import java.io.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import com.tibco.bw.maven.plugin.test.helpers.BWTestConfig;

public class EngineRunner 
{   
	private int engineStartupWaitTime = 2;
	CountDownLatch latch = new CountDownLatch(1);
	AtomicBoolean isEngineStarted = new AtomicBoolean(false);
	AtomicBoolean isImpaired = new AtomicBoolean(false);
	
	public EngineRunner(int engineStartupWaitTime){
		this.engineStartupWaitTime = engineStartupWaitTime;
	}
	
	public void run() throws Exception
	{
		Process process = null;
	
		ProcessBuilder builder = new ProcessBuilder( BWTestConfig.INSTANCE.getLaunchConfig());
        process = builder.start();
        
        BWTestConfig.INSTANCE.getLogger().info("## Starting BW Engine in Test Mode ##");
        BWTestConfig.INSTANCE.getLogger().info("----------------------BW Engine Logs Start------------------------------");
        BWTestConfig.INSTANCE.getLogger().info("" );
        BWTestConfig.INSTANCE.setEngineProcess(process);

        Runnable input = getInputRunnable(process);
        Runnable error = getErrorRunnable(process);

        Thread inputThread = new Thread( input );
		Thread errorThread = new Thread( error );
		inputThread.start();
		errorThread.start();

		BWTestConfig.INSTANCE.getLogger().debug("Engine Startup wait time -> "+ this.engineStartupWaitTime + " mins");
		latch.await(this.engineStartupWaitTime, TimeUnit.MINUTES);
		
		if(isImpaired.get() && !isEngineStarted.get()){
			BWTestConfig.INSTANCE.getLogger().info("------------------------------------------------------------------------");
			BWTestConfig.INSTANCE.getLogger().info("## Issue OSGi command (la) to print Application state ##");
			BWTestConfig.INSTANCE.getLogger().info("------------------------------------------------------------------------");
			
			OSGICommandExecutor cmdExecutor = new OSGICommandExecutor();
			cmdExecutor.executeCommand("la");

			BWTestConfig.INSTANCE.getLogger().info("------------------------------------------------------------------------");
		}
		
		if(!isEngineStarted.get()){
			throw new EngineProcessException("BW Engine not started successfully.Please see logs for more details");
		}
		
		
        BWTestConfig.INSTANCE.getLogger().info( "## BW Engine Successfully Started ##");

		
	}
	
	
	
	
	private Runnable getErrorRunnable(final Process process) {
		Runnable error = new Runnable() {
			public void run() {
				try {
					InputStream is = process.getErrorStream();
					InputStreamReader isr = new InputStreamReader(is);
					BufferedReader br = new BufferedReader(isr);
					String line;
					
			        while((line = br.readLine()) != null) {
						BWTestConfig.INSTANCE.getLogger().error(line);
			        }
				} catch(Exception e) {
					BWTestConfig.INSTANCE.getLogger().error(e);
		        }
			}
		};
		return error;
	}

	private Runnable getInputRunnable(final Process process){
		Runnable input = new Runnable() {
			public void run() {
				try {
					InputStream is = process.getInputStream(); 
					InputStreamReader isr = new InputStreamReader(is);
					BufferedReader br = new BufferedReader(isr);
					String line;
			        while((line = br.readLine()) != null) {
						BWTestConfig.INSTANCE.getLogger().info(line);
						//TIBCO-THOR-FRWK-300006: Started BW Application
						if( line.contains( "TIBCO-THOR-FRWK-300006") && line.contains( "Started BW Application") )
						{
							isEngineStarted.set(true);
							latch.countDown();
						}
						//TIBCO-THOR-FRWK-300019: BW Application is impaired
						if( line.contains( "TIBCO-THOR-FRWK-300019") && line.contains("impaired"))
						{
							isImpaired.set(true);
						}
			        }
			        if(latch.getCount()>0){
			        	isEngineStarted.set(false);
			        	latch.countDown();
			        }
			        
				} catch(Exception e) {
					BWTestConfig.INSTANCE.getLogger().error(e);
		        }
			}
		};
		return input;
	}
	
	
}
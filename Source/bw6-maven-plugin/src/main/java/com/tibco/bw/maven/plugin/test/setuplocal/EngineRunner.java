package com.tibco.bw.maven.plugin.test.setuplocal;

import java.io.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import com.tibco.bw.maven.plugin.test.helpers.BWTestConfig;

public class EngineRunner 
{     
	CountDownLatch latch = new CountDownLatch(1);
	AtomicBoolean isEngineStarted = new AtomicBoolean(true);

	public void run() throws Exception
	{
		Process process = null;
	
		ProcessBuilder builder = new ProcessBuilder( BWTestConfig.INSTANCE.getLaunchConfig());
        process = builder.start();
        
        BWTestConfig.INSTANCE.getLogger().info("## Starting BW Engine in Test Mode ##");
        BWTestConfig.INSTANCE.getLogger().info("----BW Engine Logs Start------------------------------------------------");
        BWTestConfig.INSTANCE.getLogger().info("" );
        BWTestConfig.INSTANCE.setEngineProcess(process);

        Runnable input = getInputRunnable(process);
        Runnable error = getErrorRunnable(process);

        Thread inputThread = new Thread( input );
		Thread errorThread = new Thread( error );
		inputThread.start();
		errorThread.start();

		latch.await(5, TimeUnit.MINUTES); //if the node don't starts in 5 minutes ... for sure somethings is wrong
		
		if(!isEngineStarted.get()){
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
			// use OSGi Console to issue commands in order to see the state of server
			writer.newLine();
			Thread.currentThread().sleep(1000);
			BWTestConfig.INSTANCE.getLogger().info("------------------------------------------------------------------------");
			BWTestConfig.INSTANCE.getLogger().info("## Issue OSGi Console commands to print BWApp state ##");
			BWTestConfig.INSTANCE.getLogger().info("------------------------------------------------------------------------");

			writer.write("la"); //Print information about all applications.
			writer.newLine();
			writer.flush();

			Thread.currentThread().sleep(5000); //wait for prev commands output
			BWTestConfig.INSTANCE.getLogger().info("------------------------------------------------------------------------");

			writer.close();

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
						if( line.contains( "TIBCO-THOR-FRWK-300006") )
						{
							latch.countDown();
						}
						//TIBCO-THOR-FRWK-300019: BW Application is impaired
						if( line.contains( "TIBCO-THOR-FRWK-300019") )
						{
							isEngineStarted.set(false);
							latch.countDown();
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

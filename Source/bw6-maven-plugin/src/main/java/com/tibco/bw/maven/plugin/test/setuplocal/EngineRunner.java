package com.tibco.bw.maven.plugin.test.setuplocal;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;

import com.tibco.bw.maven.plugin.test.helpers.BWTestConfig;

public class EngineRunner 
{
	 CountDownLatch latch = new CountDownLatch(1);

	public void run() throws Exception
	{
		Process process = null;
	
	
		String[] cmdLine = BWTestConfig.INSTANCE.getLaunchConfig().toArray( new String[]{} );

		//FileUtils.copyFile( new File ("D:/Codebase/BW6/Trunk/test/UnitTestsPOC/dev.properties") , new File("C:/Users/abhide/AppData/Local/Temp/bwconfig/dev.properties"));
		
		ProcessBuilder builder = new ProcessBuilder( BWTestConfig.INSTANCE.getLaunchConfig());
        //builder.directory( new File( executorHome ) );
        process = builder.start();
        
        BWTestConfig.INSTANCE.getLogger().info( "## Starting BW Engine in Test Mode ##");
        
        BWTestConfig.INSTANCE.setEngineProcess(process);
        
        final StringBuilder sb  = new StringBuilder();

        Runnable input = getInputRunnable(process, sb);
        Runnable error = getErrorRunnable(process, sb);

        Thread inputThread = new Thread( input );
		Thread errorThread = new Thread( error );
		inputThread.start();
		errorThread.start();

		TimerTask task = new TimerTask() {
	        public void run() {
	            System.out.print( "." );
	        }
	    };
	    Timer timer = new Timer("Timer");
	     
	    timer.schedule(task, 0, 1500);
		
		
		latch.await();
		
		timer.cancel();
		
		System.out.println();
        BWTestConfig.INSTANCE.getLogger().info( "## BW Engine Successfully Started ##");

		
	}
	
	
	
	
	private Runnable getErrorRunnable(final Process process, final StringBuilder sb) {
		Runnable error = new Runnable() {
			public void run() {
				try {
					InputStream is = process.getErrorStream();
					InputStreamReader isr = new InputStreamReader(is);
					BufferedReader br = new BufferedReader(isr);
					String line;
					
			        while((line = br.readLine()) != null) {
			        	//System.out.println( line);
			            sb.append( line );
			        }
				} catch(Exception e) {
		        	//logger.error ( e.getMessage() , e);
		        }
			}
		};
		return error;
	}

	private Runnable getInputRunnable(final Process process, final StringBuilder sb) {
		Runnable input = new Runnable() {
			public void run() {
				try {
					InputStream is = process.getInputStream(); 
					InputStreamReader isr = new InputStreamReader(is);
					BufferedReader br = new BufferedReader(isr);
					String line;		        
			        while((line = br.readLine()) != null) {
			        	
			        	//System.out.println( line);
			        	if( line.contains( "Started BW Application") )
						{
							latch.countDown();
						}
			        	
			            sb.append( line );
			        }
				} catch(Exception e) {
		        	//logger.error ( e.getMessage() , e);
		        }
			}
		};
		return input;
	}
	
	
}

package com.tibco.bw.plugin.tests;

import java.io.File;
import java.io.IOException;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;

import com.tibco.bw.maven.plugin.application.BWEARPackagerMojo;
import com.tibco.bw.maven.plugin.test.stub.SampleProjectProperties;

public class TestBWEarMojo extends AbstractMojoTestCase{

	
	
	private File pom;
	SampleProjectProperties prop = new SampleProjectProperties();
	
	protected void setUp() throws Exception {		
		super.setUp();
		pom = new File( prop.getApplicationpath(),"pom.xml" );		
	}	
	
	
	public void testexecute() throws Exception {
		try {

			assertNotNull( pom );
	        assertTrue( pom.exists()); 	      
	        BWEARPackagerMojo myMojo =(BWEARPackagerMojo) lookupMojo( "bwear", pom );
	        assertNotNull( myMojo );
	        
	        myMojo.execute();
			
			System.out.println("Bwear Packager Mojo finished execution.");
		} catch (IOException e) {
			e.printStackTrace();
			throw new MojoExecutionException("Error assembling ear", e);
		} 
	}

	protected void tearDown() throws Exception {
			super.tearDown();
	}


}

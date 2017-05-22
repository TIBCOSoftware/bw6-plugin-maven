package com.tibco.plugins;

import static org.junit.Assert.*;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;

import java.io.File;

import org.junit.Test;

public class MyMojoTest extends AbstractMojoTestCase{

	@Test
	public void testExecute() {

	        File pom = getTestFile( "src/test/resources/unit/project-to-test/pom.xml" );
	        //assertNotNull( pom );
	        assertTrue( pom.exists() );

	        MyMojo myMojo;
			try {
				myMojo = (MyMojo) lookupMojo( "bwresource", pom );
				
		        assertNotNull( myMojo );
		        myMojo.execute();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}


	 
	    }
	}



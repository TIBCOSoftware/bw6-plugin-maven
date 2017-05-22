package com.tibco.bw.maven.plugin.application;

import static org.junit.Assert.*;

import java.io.File;

import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.apache.maven.project.MavenProject;
import org.junit.Test;

import com.tibco.plugins.MyMojo;

public class BWEARResourceImportMojoTest extends AbstractMojoTestCase{

	@Test
	public void testExecute() {

	        File pom = getTestFile( "src/test/resources/unit/BWEARResourceImportMojo/pom.xml" );
	        //assertNotNull( pom );
	        assertTrue( pom.exists() );

	        BWEARResourceImportMojo myMojo;
			try {
				myMojo = (BWEARResourceImportMojo) lookupMojo( "bwimport", pom );
				
		        assertNotNull( myMojo );
		        myMojo.execute();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}


	 
	    }
	

}



package com.tibco.bw.maven.plugin.application;

import static org.junit.Assert.*;

import java.io.File;

import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.apache.maven.project.MavenProject;
import org.junit.Test;


public class BWEARResourceExportMojoTest extends AbstractMojoTestCase{

	@Test
	public void testExecute() {

	        File pom = getTestFile( "src/test/resources/unit/BWEARResourceExportMojo/pom.xml" );
	        //assertNotNull( pom );
	        assertTrue( pom.exists() );

	        BWEARResourceExportMojo myMojo;
			try {
				myMojo = (BWEARResourceExportMojo) lookupMojo( "bwexport", pom );
				File delete = new File( "target/perro.properties" );
				delete.delete();
				
		        assertNotNull( myMojo );
		        myMojo.execute();
		        
		        
		        assertTrue(delete.exists());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}


	 
	    }
	

}



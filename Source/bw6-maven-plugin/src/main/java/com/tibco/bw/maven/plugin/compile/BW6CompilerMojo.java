package com.tibco.bw.maven.plugin.compile;

import java.io.File;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.compiler.CompilerMojo;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

@Mojo( name = "bwcompile", defaultPhase = LifecyclePhase.COMPILE )
public class BW6CompilerMojo extends CompilerMojo {
	@Parameter( property="project.build.directory")
    private File outputDirectory;

	@Parameter( property="project.basedir")
	private File projectBasedir;
    
    public void execute() throws MojoExecutionException {
    	try {
    		super.execute();    	
    	} catch(Exception e) {
    	}
    }
}

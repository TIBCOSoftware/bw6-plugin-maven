package com.tibco.bw.maven.plugin.application;

import java.io.File;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

@Mojo( name = "bwinstall", defaultPhase = LifecyclePhase.INSTALL )
public class BWEARInstallerMojo extends AbstractMojo
{
	@Parameter( property="project.build.directory")
    private File outputDirectory;
    
	@Parameter( property="project.basedir")
	private File projectBasedir;
	
    @Component
    private MavenSession session;

    @Component
    private MavenProject project;
    

	@Parameter( property="deployToAdmin")
	private boolean deployToAdmin; 
	
	@Parameter( property="agentHost")
	private String agentHost;

	@Parameter( property="agentPort")
	private String agentPort;

	@Parameter( property="domain")
	private String domain;
		
	@Parameter( property="domainDesc")
	private String domainDesc;
	
	
	@Parameter( property="appSpace")
	private String appspace;

	@Parameter( property="appSpaceDesc")
	private String appspaceDesc;

	@Parameter( property="appNode")
	private String appNode;
	
	@Parameter( property="appNodeDesc")
	private String appNodeDesc;

	@Parameter( property="httpPort")
	private String httpPort;
	
	@Parameter( property="osgiPort")
	private String osgiPort;
	
	@Parameter( property="profile")
	private String profile;
	
	@Parameter( property="redeploy")
	private boolean redeploy;

	

    public void execute() throws MojoExecutionException
    {
    	try 
    	{    		
    		getLog().info("BWEAR Installer Mojo started ...");
    		
    	}
    	catch(Exception e )
    	{
    		
    	}
    }
}

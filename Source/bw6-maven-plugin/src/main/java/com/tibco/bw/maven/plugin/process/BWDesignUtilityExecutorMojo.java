package com.tibco.bw.maven.plugin.process;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

@Mojo(name = "bwdesignUtility")
public class BWDesignUtilityExecutorMojo extends AbstractMojo{
	
	@Parameter(defaultValue="${session}", readonly=true)
    private MavenSession session;

	
	@Parameter(defaultValue="${project}", readonly=true)
    private MavenProject project;
	
	@Parameter( property = "diagramLoc" , defaultValue = "" )
    private String diagramLoc;
	
	@Parameter( property = "commandName" , defaultValue = "" )
    private String commandName;
    
	private Log logger = getLog();
	
	String executorHome = null;
	
	String tibcoHome = null;
	
	String bwHome = null;
	
	String binDir = null;
	
	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
	try{	
		if( project.getPackaging().equals("bwear") )
			{
			if(null != project.getProperties().getProperty("tibco.Home") && !project.getProperties().getProperty("tibco.Home").isEmpty()){
				tibcoHome = project.getProperties().getProperty("tibco.Home");
			}
			else{
				logger.error("Please provide the Tibco Home in POM ");
				throw new MojoFailureException("Value for Tibco Home is empty");
			}
			
			if(null != project.getProperties().getProperty("bw.Home") && !project.getProperties().getProperty("bw.Home").isEmpty()){
				 bwHome = project.getProperties().getProperty("bw.Home");
			}
			else{
				logger.error("Please provide the BW Home in POM ");
				throw new MojoFailureException("Value for BW Home is empty");
			}
				
				binDir = tibcoHome.concat(bwHome).concat("//bin");
				executorHome = binDir;
				if(null != commandName && commandName.equals("validate")){
					validateBWProject();
				}
				else if(null != commandName && commandName.equals("gen_diagrams"))
				{
					generateProcessDiagram();
				}
				else {
					validateBWProject();
					generateProcessDiagram();
				}
			}
	}catch(MojoFailureException e){
		throw e;
	}
	}
	
	private void generateProcessDiagram() throws MojoExecutionException {
		List<String> params = new ArrayList<>();
		params = createUtilityArgument(params);
		try {
			ProcessBuilder builder = new ProcessBuilder( params);
	        builder.directory( new File( executorHome ) );
	        final Process process = builder.start();	
	        logger.info("---------------------Generating Process diagram-----------------------");
	        
	        BufferedWriter writer = new BufferedWriter(
	                new OutputStreamWriter(process.getOutputStream()));
	        if(null != diagramLoc && !diagramLoc.isEmpty()){
	        	writer.write("diagram:gen_diagrams "+project.getName()+" "+diagramLoc);
	        }
	        else{
	        	writer.write("diagram:gen_diagrams "+project.getName());
	        }
	        writer.close();
	        
	        BufferedReader reader = new BufferedReader(new InputStreamReader(
	                process.getInputStream()));
	        String line;
	        while ((line = reader.readLine()) != null) {
	            System.out.println(line);
	        }
	        reader.close();
	        
		} catch (Exception e) {
			e.printStackTrace();
			throw new MojoExecutionException( e.getMessage(), e);
		}
		
		
	}

	

	private List<String> createUtilityArgument(List<String> params) {

		String utilityName = executorHome.concat("//bwdesign");
		String workSpaceLocation = project.getBasedir().getParent();
		params.add(utilityName);
		params.add("-data");
		params.add(workSpaceLocation);
		return params;
	}

	
	private void validateBWProject() throws MojoExecutionException{
		Process validateProcess = null;
		List<String> validateParam = new ArrayList<>();
		validateParam = createUtilityArgument(validateParam);
		try {
			ProcessBuilder validateBuilder = new ProcessBuilder( validateParam);
			validateBuilder.directory( new File( executorHome ) );
			logger.info("---------------------Validating BW Project-----------------------");
			validateProcess = validateBuilder.start();
			String applicationName = project.getName();
			String moduleName = applicationName.replace(".application", "");
			
			 BufferedWriter writer = new BufferedWriter(
		                new OutputStreamWriter(validateProcess.getOutputStream()));
		        writer.write("validate "+moduleName+","+applicationName);
		        writer.close();
			
		        BufferedReader reader = new BufferedReader(new InputStreamReader(
		        		validateProcess.getInputStream()));
		        String line;
		        while ((line = reader.readLine()) != null) {
		            System.out.println(line);
		        }

		} catch (IOException e) {
			e.printStackTrace();
			throw new MojoExecutionException( e.getMessage(), e);
		}
		
		
		
	}
	
}

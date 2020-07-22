package com.tibco.bw.maven.plugin.process;

import com.tibco.bw.maven.plugin.utils.BWProjectUtils;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

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
				
				binDir = tibcoHome.concat(bwHome).concat(File.separator).concat("bin");
				executorHome = binDir;
				if(null != commandName && commandName.equals("validate")){
					importWorkspace();
					validateBWProject();
				}
				else if(null != commandName && commandName.equals("gen_diagrams"))
				{
					importWorkspace();
					generateProcessDiagram();
				}
				else {
					importWorkspace();
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
		params.add("diagram:gen_diagrams");
		params.add(project.getName());
		if(null != diagramLoc && !diagramLoc.isEmpty()){
			params.add(diagramLoc);
		}

		try {
			ProcessBuilder builder = new ProcessBuilder( params);
	        builder.directory( new File( executorHome ) );
			// redirect error stream to /dev/null
			if(BWProjectUtils.OS.WINDOWS.equals(BWProjectUtils.getOS())) {
				builder.redirectError(new File("NUL"));
			} else {
				builder.redirectError(new File("/dev/null"));
			}
			final Process process = builder.start();
	        logger.info("---------------------Generating Process diagram-----------------------");
			logger.debug("Launching bwdesign utility with params: " + params);

			printProcessOutput(process);

		} catch (Exception e) {
			e.printStackTrace();
			throw new MojoExecutionException( e.getMessage(), e);
		}
	}

	

	private List<String> createUtilityArgument(List<String> params) {

		String utilityName = executorHome.concat(File.separator).concat("bwdesign.exe");
		if(BWProjectUtils.OS.UNIX.equals(BWProjectUtils.getOS())) {
			utilityName = executorHome.concat(File.separator).concat("bwdesign");
		}
		String workSpaceLocation = project.getBasedir().getParent();
		params.add(utilityName);
		params.add("-data");
		params.add(workSpaceLocation);
		return params;
	}

	
	private void validateBWProject() throws MojoExecutionException{
		List<String> params = new ArrayList<>();
		params = createUtilityArgument(params);
		params.add("validate");
		params.add(projectList());

		try {
			ProcessBuilder builder = new ProcessBuilder( params);
			builder.directory( new File( executorHome ) );
			// redirect error stream to /dev/null
			if(BWProjectUtils.OS.WINDOWS.equals(BWProjectUtils.getOS())) {
				builder.redirectError(new File("NUL"));
			} else {
				builder.redirectError(new File("/dev/null"));
			}
			logger.info("---------------------Validating BW Project-----------------------");
			logger.debug("Launching bwdesign utility with params: " + params);
			final Process process = builder.start();

			printProcessOutput(process);

		} catch (IOException e) {
			e.printStackTrace();
			throw new MojoExecutionException( e.getMessage(), e);
		}
	}

	/*
	Should run import task before validate and diagram:gen_diagrams if you don't run mvn into existing eclipse workspace
	 */
	private void importWorkspace() throws MojoExecutionException {
		List<String> params = new ArrayList<>();
		params = createUtilityArgument(params);
		params.add("import");
		params.add(project.getBasedir().getParent());

		try {
			ProcessBuilder builder = new ProcessBuilder( params);
			builder.directory( new File( executorHome ) );
			// redirect error stream to /dev/null
			if(BWProjectUtils.OS.WINDOWS.equals(BWProjectUtils.getOS())) {
				builder.redirectError(new File("NUL"));
			} else {
				builder.redirectError(new File("/dev/null"));
			}
			final Process process = builder.start();
			logger.info("-----------------Import Projects to Workspaces-------------------");
			logger.debug("Launching bwdesign utility with params: " + params);

			printProcessOutput(process);

		} catch (Exception e) {
			e.printStackTrace();
			throw new MojoExecutionException( e.getMessage(), e);
		}


	}

	private String projectList() {
		List<String> projectList = new ArrayList<String>();
		for (MavenProject mvnProject: session.getProjects()) {
			projectList.add(mvnProject.getName());
		}
		return String.join(",", projectList);
	}

	private void printProcessOutput(Process process) throws IOException {
		BufferedReader reader= null;
		String line = null;

		reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
		while ((line = reader.readLine()) != null) {
			System.err.println(line);
		}

		reader.close();
	}
}
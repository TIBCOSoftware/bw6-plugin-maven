package com.tibco.bw.maven.plugin.process;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Arrays;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.settings.io.DefaultSettingsWriter;
import org.codehaus.plexus.logging.Logger;

import com.tibco.bw.maven.plugin.utils.BWProjectUtils;

public class MvnInstallExecutor {
	private Logger logger;

	public MvnInstallExecutor(Logger logger) {
		this.logger = logger;
	}

	private void executeUnix( String command ) throws IOException, InterruptedException {
		executeUnixCommand(command);
	}

	public void execute(Model model, File jarFile, MavenSession session){
		StringBuffer buffer = new StringBuffer();
		try{
			File fSettings = File.createTempFile("settings", ".xml");
			DefaultSettingsWriter defaultSettingsWriter = new DefaultSettingsWriter();
			defaultSettingsWriter.write(fSettings, null, session.getSettings());
			buffer.append("mvn");
			buffer.append(" -s ");
			buffer.append(fSettings.getAbsolutePath());
			buffer.append(" install:install-file ");
			buffer.append(" -Dfile=" + "\"" + jarFile.getAbsolutePath() + "\"");
			buffer.append(" -DgroupId=tempbw");
			buffer.append(" -DartifactId=" + jarFile.getName().substring(0,jarFile.getName().lastIndexOf(".")) );
			buffer.append(" -Dversion=0.0.0");
			buffer.append(" -Dpackaging=jar");
			buffer.append(" -DlocalRepositoryPath=" + "\"" + session.getLocalRepository().getBasedir() + "\"");
			switch( BWProjectUtils.getOS()) {
			case WINDOWS:
				executeWinCommand(buffer.toString());	
				break;
			case UNIX:
				executeUnix( buffer.toString() );
				break;
			}
			if (fSettings.delete()){
				logger.info("The "+fSettings.getAbsolutePath()+" Temporal file is deleted sucessfully");
			}else{
				logger.warn("The "+fSettings.getAbsolutePath()+" Temporal file is not deleted");
			}
			Dependency dep = new Dependency();
			dep.setGroupId("tempbw");
			dep.setArtifactId(jarFile.getName().substring(0,jarFile.getName().lastIndexOf(".")));
			dep.setVersion("0.0.0");
			model.addDependency(dep);
			logger.debug("Set the Dependency to Model");
		} catch(Throwable e) {
			logger.error( "Failed to add Dependency to Maven Repository for JAR " + jarFile.getName() + " .Please do it manually");
			e.printStackTrace();
		}
	}

	public void executeUnixCommand(String command) throws IOException, InterruptedException {
	    File tempScript = createUnixScript(command);
	    try {
	        ProcessBuilder pb = new ProcessBuilder("bash", tempScript.toString());
	        pb.inheritIO();
	        pb.directory( new File(System.getProperty( "user.home")));
	        Process process = pb.start();
	        process.waitFor();
	        logger.debug("Process Exit Value: " + process.exitValue());
	    } finally {
	        tempScript.delete();
	    }
	}

	public void executeWinCommand(String command) throws IOException, InterruptedException {
	    File tempScript = createWinScript(command);
	    try {
	        ProcessBuilder pb = new ProcessBuilder(Arrays.asList(new String[] {"cmd.exe", "/C", tempScript.toString()}));
	        pb.inheritIO();
	        pb.directory( new File(System.getProperty("user.home")));
	        Process process = pb.start();
	        process.waitFor();
	        logger.debug("Process Exit Value: " + process.exitValue());
	    } finally {
	        tempScript.delete();
	    }
	}

	public File createUnixScript(String command) throws IOException {
	    File tempScript = File.createTempFile("script", ".sh");
	    Writer streamWriter = new OutputStreamWriter(new FileOutputStream(tempScript));
	    PrintWriter printWriter = new PrintWriter(streamWriter);
	    printWriter.println("#!/bin/bash");
	    printWriter.println("source ~/.bash_profile");
	    printWriter.println("source ~/.bashrc");
	    printWriter.println(command);
	    printWriter.close();
	    return tempScript;
	}

	public File createWinScript(String command) throws IOException {
	    File tempScript = File.createTempFile("script", ".bat");
	    Writer streamWriter = new OutputStreamWriter(new FileOutputStream(tempScript));
	    PrintWriter printWriter = new PrintWriter(streamWriter);
	    printWriter.println(command);
	    printWriter.close();
	    return tempScript;
	}
}

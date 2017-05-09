package com.tibco.bw.maven.plugin.lifecycle;

import java.io.File;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.maven.AbstractMavenLifecycleParticipant;
import org.apache.maven.MavenExecutionException;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.codehaus.plexus.logging.Logger;

import com.tibco.bw.maven.plugin.process.MvnInstallExecutor;
import com.tibco.bw.maven.plugin.utils.BWFileUtils;

@Component(role = AbstractMavenLifecycleParticipant.class)
public class BWProjectLifeCycleListener extends AbstractMavenLifecycleParticipant {
	@Requirement
	private Logger logger;

    public BWProjectLifeCycleListener() {
    }

    @Override
    public void afterSessionStart(MavenSession session) throws MavenExecutionException {
        super.afterSessionStart(session);
    }

	@Override
	public void afterProjectsRead(MavenSession session) throws MavenExecutionException {
		logger.info("Starting Maven Build for BW6 Project.................................");
		logger.info("Checking for In-Project JAR dependencies if any and Pushing them to Local Maven Repository");
		logger.debug("Cleaning existing JARs from Mavne Repository");
		File file = new File(session.getLocalRepository().getBasedir() + "/tempbw");
		try {
			if(file.exists()) {
				FileUtils.deleteDirectory(file) ;	
			}
		} catch(Exception e) {
			logger.error("Failed to clean the existing bwtemp group in Maven Repository.");
		}

		List<MavenProject> projects = session.getProjects();

		for(MavenProject project : projects) {
			if(project.getPackaging().equals("bwmodule")) {
				logger.debug("Checking JAR dependencies for Project " + project.getName());
				addJARToDependency(project);
			}
		}
		super.afterProjectsRead(session);
	}

	public void addJARToDependency(MavenProject project) {
		File baseDir = project.getBasedir();
		File[] list = BWFileUtils.getFilesForTypeRec(baseDir, project.getBuild().getDirectory(), ".jar");
		if(list == null || list.length == 0) {
			return;
		}
		logger.debug("Found JAR dependencies for Project " + project.getName() + " Adding them to Local Maven Repo");
		MvnInstallExecutor executor = new MvnInstallExecutor(logger);
		for(File file : list) {
			// Skip any jar which is inside a folder that starts with '.'
			if(file.getParent().contains(File.separator + ".")) {
				continue;
			}
			logger.debug("Adding JAR to Local Maven Repo " + file.toString()) ;
			executor.execute(project.getModel(), file);
		}
	}

	@Override
	public void afterSessionEnd(MavenSession session) throws MavenExecutionException {
		super.afterSessionEnd(session);
		File file = new File(session.getLocalRepository().getBasedir() + "/tempbw");
		try {
			if(file.exists()) {
				FileUtils.deleteDirectory(file) ;	
			}
		} catch(Exception e) {
			logger.error("Failed to clean the existing bwtemp group in Maven Repository.");
		}
	}
}

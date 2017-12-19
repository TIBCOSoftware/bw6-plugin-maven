package com.tibco.bw.studio.maven.validation;

import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import com.tibco.zion.common.refactoring.base.WorkingCopyHelper;

public class BWMavenValidationJob extends Job {

	protected Set<IProject> projects;
	
	public static void schedule(Set<IProject> projects, int delay){
		BWMavenValidationJob job = new BWMavenValidationJob(projects);
		job.schedule(delay);
	}
	
	public BWMavenValidationJob(Set<IProject> projects) {
		super("Validating External Shared Modules from Maven dependencies");
		this.projects = projects;
	}
	
	@Override
	protected IStatus run(IProgressMonitor arg0) {
		System.out.println("EXECUTING VALIDATIONS");
		WorkingCopyHelper.INSTANCE.waitForBuildJobsToComplete();
		
		for(IProject project : projects){
			if(project != null && project.exists()){
				try {
					System.out.println("Validating Project " + project.getName());
					project.build(IncrementalProjectBuilder.FULL_BUILD, null);
				} catch (CoreException e) {
					e.printStackTrace();
				}
			}		
		}
		
		return Status.OK_STATUS;
	}
}

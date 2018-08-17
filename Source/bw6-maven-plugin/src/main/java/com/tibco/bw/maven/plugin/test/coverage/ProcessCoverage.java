package com.tibco.bw.maven.plugin.test.coverage;

import java.util.ArrayList;
import java.util.List;

public class ProcessCoverage 
{

	private String moduleName ;
	
	private String processName;
	
	private boolean isSubProcess;
	
	private List<String> transitions = new ArrayList<>();
	
	private List<String> activities = new ArrayList<>();
	
	private List<String> transitionExec = new ArrayList<>();

	private List<String> activitiesExec = new ArrayList<>();
	
	private boolean processExecuted = false;
	
	public String getModuleName() {
		return moduleName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

	public String getProcessName() {
		return processName;
	}

	public void setProcessName(String processName) {
		this.processName = processName;
	}

	public boolean isSubProcess() {
		return isSubProcess;
	}

	public void setSubProcess(boolean isSubProcess) {
		this.isSubProcess = isSubProcess;
	}

	public List<String> getTransitions() {
		return transitions;
	}

	public void setTransitions(List<String> transitions) {
		this.transitions = transitions;
	}

	public List<String> getActivities() {
		return activities;
	}

	public void setActivities(List<String> activities) {
		this.activities = activities;
	}

	public List<String> getTransitionExec() {
		return transitionExec;
	}

	public void setTransitionExec(List<String> transitionExec) {
		this.transitionExec = transitionExec;
	}

	public List<String> getActivitiesExec() {
		return activitiesExec;
	}

	public void setActivitiesExec(List<String> activitiesExec) {
		this.activitiesExec = activitiesExec;
	}

	public boolean isProcessExecuted() {
		return processExecuted;
	}

	public void setProcessExecuted(boolean processExecuted) {
		this.processExecuted = processExecuted;
	}
	
}

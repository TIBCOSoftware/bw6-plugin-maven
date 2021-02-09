package com.tibco.bw.studio.maven.modules.model;

public class TCIDeploymentInfo {
	
	private int instanceCount;
	private String appVariablesFile;
	private boolean forceOverwrite;
	private boolean retainAppProps;
	
	public int getInstanceCount() {
		return instanceCount;
	}
	public void setInstanceCount(int instanceCount) {
		this.instanceCount = instanceCount;
	}
	public String getAppVariablesFile() {
		return appVariablesFile;
	}
	public void setAppVariablesFile(String appVariablesFile) {
		this.appVariablesFile = appVariablesFile;
	}
	public boolean isRetainAppProps() {
		return retainAppProps;
	}
	public void setRetainAppProps(boolean retainAppProps) {
		this.retainAppProps = retainAppProps;
	}
	public boolean isForceOverwrite() {
		return forceOverwrite;
	}
	public void setForceOverwrite(boolean forceOverwrite) {
		this.forceOverwrite = forceOverwrite;
	}
	
	

}

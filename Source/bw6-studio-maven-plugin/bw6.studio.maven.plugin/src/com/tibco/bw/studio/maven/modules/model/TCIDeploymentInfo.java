package com.tibco.bw.studio.maven.modules.model;

public class TCIDeploymentInfo {
	
	private int instanceCount;
	private String appVariablesFile;
	private String engineVariablesFile;
	private boolean forceOverwrite;
	private boolean retainAppProps;
	private boolean deployToAdmin = true;
	
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
	public String getEngineVariablesFile() {
		return engineVariablesFile;
	}
	public void setEngineVariablesFile(String engineVariablesFile) {
		this.engineVariablesFile = engineVariablesFile;
	}
	public boolean isDeployToAdmin() {
		return deployToAdmin;
	}

	public void setDeployToAdmin(boolean deployToAdmin) {
		this.deployToAdmin = deployToAdmin;
	}

	

}

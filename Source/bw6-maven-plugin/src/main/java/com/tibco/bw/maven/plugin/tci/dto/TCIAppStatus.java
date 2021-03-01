package com.tibco.bw.maven.plugin.tci.dto;

public class TCIAppStatus {
	
	private String status;
	private String message;
	private TCIInstanceStatus instanceStatus; 
	
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public TCIInstanceStatus getInstanceStatus() {
		return instanceStatus;
	}
	public void setInstanceStatus(TCIInstanceStatus instanceStatus) {
		this.instanceStatus = instanceStatus;
	}
	
	

}

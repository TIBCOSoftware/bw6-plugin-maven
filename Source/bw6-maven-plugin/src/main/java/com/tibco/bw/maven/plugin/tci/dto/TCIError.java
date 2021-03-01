package com.tibco.bw.maven.plugin.tci.dto;

public class TCIError {
	
	private String error;
	private String errorDetail;
	
	public TCIError(){
		
	}
	
	public TCIError(String error, String errorDetail) {
		super();
		this.error = error;
		this.errorDetail = errorDetail;
	}
	
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
	public String getErrorDetail() {
		return errorDetail;
	}
	public void setErrorDetail(String errorDetail) {
		this.errorDetail = errorDetail;
	}
	
	

}

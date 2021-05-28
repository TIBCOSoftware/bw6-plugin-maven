package com.tibco.bw.maven.plugin.tci.dto;

import java.util.List;

public class TCIVariables {
	
	private List<TCIProperty> userVariables;

	public List<TCIProperty> getUserVariables() {
		return userVariables;
	}

	public void setUserVariables(List<TCIProperty> userVariables) {
		this.userVariables = userVariables;
	}
	

}

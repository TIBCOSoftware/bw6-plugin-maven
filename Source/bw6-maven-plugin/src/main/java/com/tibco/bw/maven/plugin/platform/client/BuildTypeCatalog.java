package com.tibco.bw.maven.plugin.platform.client;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BuildTypeCatalog {

	private int totalBuildtypes;
    private List<BuildType> buildtypeCatalog;
    
	public int getTotalBuildtypes() {
		return totalBuildtypes;
	}
	
	public void setTotalBuildtypes(int totalBuildtypes) {
		this.totalBuildtypes = totalBuildtypes;
	}
	
	public List<BuildType> getBuildtypeCatalog() {
		return buildtypeCatalog;
	}
	
	public void setBuildtypeCatalog(List<BuildType> buildtypeCatalog) {
		this.buildtypeCatalog = buildtypeCatalog;
	}
	
	@Override
	public String toString() {
		return "BuildTypeCatalog{" + "totalBuildtypes=" + totalBuildtypes + ", buildtypeCatalog=" + buildtypeCatalog + '}';
	}
	
}

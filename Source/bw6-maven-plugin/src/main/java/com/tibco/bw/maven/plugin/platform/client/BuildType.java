package com.tibco.bw.maven.plugin.platform.client;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BuildType {
	
	private String buildtypeTag;
    private List<BaseImage> baseImages;
    private String createdBy;
    private long createdDate;
    
	public String getBuildtypeTag() {
		return buildtypeTag;
	}
	
	public void setBuildtypeTag(String buildtypeTag) {
		this.buildtypeTag = buildtypeTag;
	}
	
	public List<BaseImage> getBaseImages() {
		return baseImages;
	}
	
	public void setBaseImages(List<BaseImage> baseImages) {
		this.baseImages = baseImages;
	}
	
	public String getCreatedBy() {
		return createdBy;
	}
	
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	
	public long getCreatedDate() {
		return createdDate;
	}
	
	public void setCreatedDate(long createdDate) {
		this.createdDate = createdDate;
	}
	
	@Override
	public String toString() {
		return "BuildType{" + "buildtypeTag='" + buildtypeTag + '\'' + ", baseImages=" + baseImages + ", createdBy='" + createdBy + '\'' + ", createdDate=" + createdDate + '}';
	}
	
}

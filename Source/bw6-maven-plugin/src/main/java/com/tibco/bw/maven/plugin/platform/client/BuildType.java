package com.tibco.bw.maven.plugin.platform.client;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BuildType {
	
	@JsonProperty("buildtypeTag")
	private String buildtypeTag;
	@JsonProperty("baseImages")
    private List<BaseImage> baseImages;
	@JsonProperty("createdBy")
    private String createdBy;
	@JsonProperty("createdDate")
    private long createdDate;
	@JsonProperty("tags")
    private List<String> tags;
    
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
	
	public List<String> getTags() {
		return tags;
	}
	
	public void setTags(List<String> tags) {
		this.tags = tags;
	}
	
	@Override
	public String toString() {
		return "BuildType{" + "buildtypeTag='" + buildtypeTag + '\'' + ", baseImages=" + baseImages + ", createdBy='" + createdBy + '\'' + ", createdDate=" + createdDate + ", tags=" + tags + '}';
	}
	
}

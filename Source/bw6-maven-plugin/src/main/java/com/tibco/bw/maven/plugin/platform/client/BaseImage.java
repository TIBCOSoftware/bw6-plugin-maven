package com.tibco.bw.maven.plugin.platform.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BaseImage {
	
	@JsonProperty("baseImageName")
	private String baseImageName;
	@JsonProperty("imageTag")
    private String imageTag;
	@JsonProperty("baseImagePath")
	private String baseImagePath;
	
	public String getBaseImageName() {
		return baseImageName;
	}

	public void setBaseImageName(String baseImageName) {
		this.baseImageName = baseImageName;
	}

	public String getImageTag() {
		return imageTag;
	}

	public void setImageTag(String imageTag) {
		this.imageTag = imageTag;
	}
	
	public String getBaseImagePath() {
		return baseImagePath;
	}

	public void setBaseImagePath(String baseImagePath) {
		this.baseImagePath = baseImagePath;
	}

	@Override
	public String toString() {
		return "BaseImage{" + "baseImageName='" + baseImageName + '\'' + ", imageTag='" + imageTag + '\'' + ", baseImagePath='" + baseImagePath + '\'' + '}';
	}
	
}

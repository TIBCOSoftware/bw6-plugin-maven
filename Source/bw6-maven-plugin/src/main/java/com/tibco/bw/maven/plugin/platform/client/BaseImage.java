package com.tibco.bw.maven.plugin.platform.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BaseImage {
	
	private String baseImageName;
    private String imageTag;
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

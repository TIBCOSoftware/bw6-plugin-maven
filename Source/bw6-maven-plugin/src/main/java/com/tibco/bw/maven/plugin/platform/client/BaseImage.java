package com.tibco.bw.maven.plugin.platform.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BaseImage {
	
	private String baseImageName;
    private String imageTag;
	
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

	@Override
	public String toString() {
		return "BaseImage{" + "baseImageName='" + baseImageName + '\'' + ", imageTag='" + imageTag + '\'' + '}';
	}
	
}

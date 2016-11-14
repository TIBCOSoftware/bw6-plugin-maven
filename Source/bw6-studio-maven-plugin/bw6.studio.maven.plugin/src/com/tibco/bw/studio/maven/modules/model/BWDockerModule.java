package com.tibco.bw.studio.maven.modules.model;

import java.util.List;
import java.util.Map;

public class BWDockerModule {

	private String dockerHost;
	
	private String dockerHostCertPath;
	
	private String dockerImageName;
	
	private String dockerImageFrom;
	
	private String dockerImageMaintainer;
	
	private String dockerAppName;
	
	private List<String> dockerVolumes;
	
	private List<String> dockerLinks;
	
	private List<String> dockerPorts;
	
	private Map<String, String> dockerEnvs;
	
	private String platform;
	
	

	public String getDockerHost() {
		return dockerHost;
	}

	public void setDockerHost(String dockerHost) {
		this.dockerHost = dockerHost;
	}

	public String getDockerHostCertPath() {
		return dockerHostCertPath;
	}

	public void setDockerHostCertPath(String dockerHostCertPath) {
		this.dockerHostCertPath = dockerHostCertPath;
	}

	public String getDockerImageName() {
		return dockerImageName;
	}

	public void setDockerImageName(String dockerImageName) {
		this.dockerImageName = dockerImageName;
	}

	public String getDockerImageFrom() {
		return dockerImageFrom;
	}

	public void setDockerImageFrom(String dockerImageFrom) {
		this.dockerImageFrom = dockerImageFrom;
	}

	public String getDockerImageMaintainer() {
		return dockerImageMaintainer;
	}

	public void setDockerImageMaintainer(String dockerImageMaintainer) {
		this.dockerImageMaintainer = dockerImageMaintainer;
	}

	public String getDockerAppName() {
		return dockerAppName;
	}

	public void setDockerAppName(String dockerAppName) {
		this.dockerAppName = dockerAppName;
	}
	
	
	

	public Map<String, String> getDockerEnvs() {
		return dockerEnvs;
	}

	public void setDockerEnvs(Map<String, String> dockerEnvs) {
		this.dockerEnvs = dockerEnvs;
	}

	public List<String> getDockerVolumes() {
		return dockerVolumes;
	}

	public void setDockerVolumes(List<String> dockerVolumes) {
		this.dockerVolumes = dockerVolumes;
	}

	public List<String> getDockerLinks() {
		return dockerLinks;
	}

	public void setDockerLinks(List<String> dockerLinks) {
		this.dockerLinks = dockerLinks;
	}

	public List<String> getDockerPorts() {
		return dockerPorts;
	}

	public void setDockerPorts(List<String> dockerPorts) {
		this.dockerPorts = dockerPorts;
	}
	
	
	

	public String getPlatform() {
		return platform;
	}

	public void setPlatform(String platform) {
		this.platform = platform;
	}


	
	
}

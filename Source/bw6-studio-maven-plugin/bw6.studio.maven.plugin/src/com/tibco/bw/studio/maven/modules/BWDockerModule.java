package com.tibco.bw.studio.maven.modules;

import java.util.List;
import java.util.Map;

public class BWDockerModule {

	private String dockerHost;
	
	private String dockerHostCertPath;
	
	private String dockerImageName;
	
	private String dockerImageFrom;
	
	private String dockerImageMaintainer;
	
	private String dockerAppName;
	
	private String dockerVolume;
	
	private String dockerLink;
	
	private List<String> dockerPorts;
	
	private String platform;
	
	private String rcName;
	
	private String numOfReplicas;
	
	private String serviceName;
	
	private String containerPort;
	
	private String k8sNamespace;
	
	private Map<String, String> k8sEnvVariables;

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
	
	public String getDockerVolume() {
		return dockerVolume;
	}

	public void setDockerVolume(String dockerVolume) {
		this.dockerVolume = dockerVolume;
	}

	public String getDockerLink() {
		return dockerLink;
	}

	public void setDockerLink(String dockerLink) {
		this.dockerLink = dockerLink;
	}
	
	

	public List<String> getDockerPorts() {
		return dockerPorts;
	}

	public void setDockerPorts(List<String> dockerPorts) {
		this.dockerPorts = dockerPorts;
	}
	
	
	
	
	public Map<String, String> getK8sEnvVariables() {
		return k8sEnvVariables;
	}

	public void setK8sEnvVariables(Map<String, String> k8sEnvVariables) {
		this.k8sEnvVariables = k8sEnvVariables;
	}

	public String getPlatform() {
		return platform;
	}

	public void setPlatform(String platform) {
		this.platform = platform;
	}

	public String getRcName() {
		return rcName;
	}

	public void setRcName(String rcName) {
		this.rcName = rcName;
	}

	public String getNumOfReplicas() {
		return numOfReplicas;
	}

	public void setNumOfReplicas(String numOfReplicas) {
		this.numOfReplicas = numOfReplicas;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getContainerPort() {
		return containerPort;
	}

	public void setContainerPort(String containerPort) {
		this.containerPort = containerPort;
	}

	public String getK8sNamespace() {
		return k8sNamespace;
	}

	public void setK8sNamespace(String k8sNamespace) {
		this.k8sNamespace = k8sNamespace;
	}
	
	
}

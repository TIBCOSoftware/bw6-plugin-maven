package com.tibco.bw.studio.maven.modules.model;

import java.util.Map;

public class BWK8SModule {

	private String rcName;
	
	private String deploymentName;
	
	private String numOfReplicas;
	
	private String serviceName;
	
	private String containerPort;
	
	private String k8sNamespace;
	
	private Map<String, String> k8sEnvVariables;

	private String subk8splatform;

	public String getRcName() {
		return rcName;
	}
	public String getDeploymentName() {
		return deploymentName;
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

	public Map<String, String> getK8sEnvVariables() {
		return k8sEnvVariables;
	}

	public void setK8sEnvVariables(Map<String, String> k8sEnvVariables) {
		this.k8sEnvVariables = k8sEnvVariables;
	}
	
	public String getSubK8sPlatform() {
		return subk8splatform;
	}

	public void setSubK8sPlatform(String subk8splatform) {
		this.subk8splatform = subk8splatform;
	}
	
}

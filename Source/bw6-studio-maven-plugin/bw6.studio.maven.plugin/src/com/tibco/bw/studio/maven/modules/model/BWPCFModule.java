package com.tibco.bw.studio.maven.modules.model;

import java.util.List;
import java.util.Map;

import org.cloudfoundry.client.lib.CloudFoundryClient;

public class BWPCFModule {

	private String target;
	
	private String credString;
	
	private String org;
	
	private String space;
	
	private String appName;
	
	private String instances;
	
	private String memory;
	
	private String buildpack;
	
	private List<BWPCFServicesModule> services;
	
	private Map<String, String> cfEnvVariables;
	
	private CloudFoundryClient client;
	
	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public String getOrg() {
		return org;
	}

	public void setOrg(String org) {
		this.org = org;
	}

	public String getSpace() {
		return space;
	}

	public void setSpace(String space) {
		this.space = space;
	}

	public String getCredString() {
		return credString;
	}

	public void setCredString(String credString) {
		this.credString = credString;
	}

	public String getInstances() {
		return instances;
	}

	public void setInstances(String instances) {
		this.instances = instances;
	}

	public String getMemory() {
		return memory;
	}

	public void setMemory(String memory) {
		this.memory = memory;
	}

	public String getBuildpack() {
		return buildpack;
	}

	public void setBuildpack(String buildpack) {
		this.buildpack = buildpack;
	}

	public List<BWPCFServicesModule> getServices() {
		return services;
	}

	public void setServices(List<BWPCFServicesModule> services) {
		this.services = services;
	}

	public CloudFoundryClient getClient() {
		return client;
	}

	public void setClient(CloudFoundryClient client) {
		this.client = client;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public Map<String, String> getCfEnvVariables() {
		return cfEnvVariables;
	}

	public void setCfEnvVariables(Map<String, String> cfEnvVariables) {
		this.cfEnvVariables = cfEnvVariables;
	}

	
	
}




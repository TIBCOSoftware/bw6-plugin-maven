package com.tibco.bw.studio.maven.modules.model;

public class BWPCFServicesModule {

	private String serviceName;
	private String serviceLabel;
	private String serviceVersion;
	private String servicePlan;
	
	
	public String getServiceName() {
		return serviceName;
	}
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	public String getServiceVersion() {
		return serviceVersion;
	}
	public void setServiceVersion(String serviceVersion) {
		this.serviceVersion = serviceVersion;
	}
	public String getServicePlan() {
		return servicePlan;
	}
	public void setServicePlan(String servicePlan) {
		this.servicePlan = servicePlan;
	}
	public String getServiceLabel() {
		return serviceLabel;
	}
	public void setServiceLabel(String serviceLabel) {
		this.serviceLabel = serviceLabel;
	}
}

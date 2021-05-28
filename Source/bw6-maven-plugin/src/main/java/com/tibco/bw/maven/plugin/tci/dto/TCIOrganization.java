package com.tibco.bw.maven.plugin.tci.dto;

public class TCIOrganization {
	
	private String subscriptionLocator;
	private String organizationName;
	private boolean isCurrentOrg;
	public String getSubscriptionLocator() {
		return subscriptionLocator;
	}
	public void setSubscriptionLocator(String subscriptionLocator) {
		this.subscriptionLocator = subscriptionLocator;
	}
	public String getOrganizationName() {
		return organizationName;
	}
	public void setOrganizationName(String organizationName) {
		this.organizationName = organizationName;
	}
	public boolean isCurrentOrg() {
		return isCurrentOrg;
	}
	public void setIsCurrentOrg(boolean isCurrentOrg) {
		this.isCurrentOrg = isCurrentOrg;
	}
	
	

}

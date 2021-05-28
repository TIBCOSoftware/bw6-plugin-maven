package com.tibco.bw.maven.plugin.tci.dto;

import java.util.List;

public class TCIUserInfo {
	
	private String email;
	private String firstName;
	private String lastName;
	private String region;
	private String role;
	private List<TCIOrganization> organizations;
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getRegion() {
		return region;
	}
	public void setRegion(String region) {
		this.region = region;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public List<TCIOrganization> getOrganizations() {
		return organizations;
	}
	public void setOrganizations(List<TCIOrganization> organizations) {
		this.organizations = organizations;
	}
	
	

}

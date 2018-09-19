package com.tibco.bw.studio.maven.modules.model;

import java.util.ArrayList;
import java.util.List;

public class BWDeploymentInfo {
	private boolean deployToAdmin = false;
	private String agentHost = "";
	private String agentPort = "";
	private String agentAuth = "";
	private String agentUsername = "";
	private String agentPassword = "";
	private boolean agentSSL = false;
	private String trustPath = "";
	private String trustPassword = "";
	private String keyPath = "";
	private String keyPassword = "";
	private String domain = "";
	private String appspace = "";
	private String appNode = "";
	private String domainDesc = "";
	private String appspaceDesc = "";
	private String appNodeDesc = "";
	private String httpPort = "";
	private String osgiPort = "";
	private String profile = "";
	private boolean redeploy = false;
	private boolean backup = false;
	private String backupLocation = "";
	private boolean externalProfile = false;
	private String externalProfileLoc = "";
	private List<String> profiles = new ArrayList<String>();

	public boolean isDeployToAdmin() {
		return deployToAdmin;
	}

	public void setDeployToAdmin(boolean deployToAdmin) {
		this.deployToAdmin = deployToAdmin;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getAppspace() {
		return appspace;
	}

	public void setAppspace(String appspace) {
		this.appspace = appspace;
	}

	public String getAppNode() {
		return appNode;
	}

	public void setAppNode(String appNode) {
		this.appNode = appNode;
	}

	public String getDomainDesc() {
		return domainDesc;
	}

	public void setDomainDesc(String domainDesc) {
		this.domainDesc = domainDesc;
	}

	public String getAppspaceDesc() {
		return appspaceDesc;
	}

	public void setAppspaceDesc(String appspaceDesc) {
		this.appspaceDesc = appspaceDesc;
	}

	public String getAppNodeDesc() {
		return appNodeDesc;
	}

	public void setAppNodeDesc(String appNodeDesc) {
		this.appNodeDesc = appNodeDesc;
	}

	public boolean isRedeploy() {
		return redeploy;
	}

	public void setRedeploy(boolean redeploy) {
		this.redeploy = redeploy;
	}

	public String getAgentHost() {
		return agentHost;
	}

	public void setAgentHost(String agentHost) {
		this.agentHost = agentHost;
	}

	public String getAgentPort() {
		return agentPort;
	}

	public void setAgentPort(String agentPort) {
		this.agentPort = agentPort;
	}

	public String getProfile() {
		return profile;
	}

	public void setProfile(String profile) {
		this.profile = profile;
	}

	public String getHttpPort() {
		return httpPort;
	}

	public void setHttpPort(String httpPort) {
		this.httpPort = httpPort;
	}

	public String getOsgiPort() {
		return osgiPort;
	}

	public void setOsgiPort(String osgiPort) {
		this.osgiPort = osgiPort;
	}

	public List<String> getProfiles() {
		return profiles;
	}

	public void setProfiles(List<String> profiles) {
		this.profiles = profiles;
	}

	public boolean isBackup() {
		return backup;
	}

	public void setBackup(boolean backup) {
		this.backup = backup;
	}

	public String getBackupLocation() {
		return backupLocation;
	}

	public void setBackupLocation(String backupLocation) {
		this.backupLocation = backupLocation;
	}
	public boolean isexternalProfile() {
		return externalProfile;
	}

	public void setexternalProfile(boolean externalProfile) {
		this.externalProfile = externalProfile;
	}

	public String getexternalProfileLoc() {
		return externalProfileLoc;
	}

	public void setexternalProfileLoc(String externalProfileLoc) {
		this.externalProfileLoc = externalProfileLoc;
	}
	
	public String getAgentUsername() {
		return agentUsername;
	}

	public void setAgentUsername(String agentUsername) {
		this.agentUsername = agentUsername;
	}

	public String getAgentPassword() {
		return agentPassword;
	}

	public void setAgentPassword(String agentPassword) {
		this.agentPassword = agentPassword;
	}

	public String getAgentAuth() {
		return agentAuth;
	}

	public void setAgentAuth(String agentAuth) {
		this.agentAuth = agentAuth;
	}

	public boolean isAgentSSL() {
		return agentSSL;
	}

	public void setAgentSSL(boolean agentSSL) {
		this.agentSSL = agentSSL;
	}

	public String getTrustPath() {
		return trustPath;
	}

	public void setTrustPath(String trustPath) {
		this.trustPath = trustPath;
	}

	public String getTrustPassword() {
		return trustPassword;
	}

	public void setTrustPassword(String trustPassword) {
		this.trustPassword = trustPassword;
	}

	public String getKeyPath() {
		return keyPath;
	}

	public void setKeyPath(String keyPath) {
		this.keyPath = keyPath;
	}

	public String getKeyPassword() {
		return keyPassword;
	}

	public void setKeyPassword(String keyPassword) {
		this.keyPassword = keyPassword;
	}
}

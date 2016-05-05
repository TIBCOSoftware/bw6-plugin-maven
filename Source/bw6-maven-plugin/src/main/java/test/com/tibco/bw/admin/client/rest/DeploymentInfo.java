package test.com.tibco.bw.admin.client.rest;

public class DeploymentInfo {
	  String domainName = null;
	  public String getDomainName() {
		return domainName;
	}
	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}
	public String getAsName() {
		return asName;
	}
	public void setAsName(String asName) {
		this.asName = asName;
	}
	public String getNodeName() {
		return nodeName;
	}
	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}
	public String getArchiveFilePath() {
		return archiveFilePath;
	}
	public void setArchiveFilePath(String archiveFilePath) {
		this.archiveFilePath = archiveFilePath;
	}
	public String getArchiveFileName() {
		return archiveFileName;
	}
	public void setArchiveFileName(String archiveFileName) {
		this.archiveFileName = archiveFileName;
	}
	public String getAppName() {
		return appName;
	}
	public void setAppName(String appName) {
		this.appName = appName;
	}
	public String getAppVersion() {
		return appVersion;
	}
	public void setAppVersion(String appVersion) {
		this.appVersion = appVersion;
	}
	String asName = null;
	  String nodeName = null;
	  String archiveFilePath = null;
	  String archiveFileName = null;
	  String appName = null;
	  String appVersion = null;

}

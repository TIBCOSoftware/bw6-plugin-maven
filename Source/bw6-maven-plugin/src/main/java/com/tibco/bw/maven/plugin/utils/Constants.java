package com.tibco.bw.maven.plugin.utils;

public interface Constants {
	public static final String ADMINEXEC = "bwadmin";
	public static final String BUNDLE_VERSION = "Bundle-Version";
	public static final String BUNDLE_CLASSPATH = "Bundle-ClassPath";
	public static final String BUNDLE_SYMBOLIC_NAME = "Bundle-SymbolicName";
	public static final String BUNDLE_PROVIDE_CAPABILITY = "Provide-Capability";
	public static final String TIBCO_BW_EDITION = "TIBCO-BW-Edition";
	public static final String BWCF = "bwcf";
	public static final String PACKAGING_MODEL_NAMESPACE_URI = "http://schemas.tibco.com/tra/model/core/PackagingModel";
	public static final String MODULE = "module";
	public static final String SYMBOLIC_NAME = "symbolicName";
	public static final String TECHNOLOGY_VERSION = "technologyVersion";
	public static final String BASIC_AUTH = "BASIC";
	public static final String DIGEST_AUTH = "DIGEST";
	public static final String TIBCO_SHARED_MODULE = "TIBCO-BW-SharedModule";
	public static final String TEMP_SUBSTVAR= "external.substvar";
	public static final String TIMESTAMP= "timestamp";
	public static final String COMPONENT_PROCESS = "ComponentProcess";
	
	//TCI
	public static final String TCI_SERVER_ENDPOINT_ENV = "TCI_PLATFORM_API_ENDPOINT";
	public static final String TCI_ACCESS_TOKEN_ENV = "TCI_PLATFORM_API_ACCESS_TOKEN";
	public static final String BWCLOUD = "bwcloud";
	public static final String TCI = "TCI";
	public static final String TCI_SUBSCRIPTION_ID_ENV = "TCI_PLATFORM_SUBSCRIPTION_ID";
	public static final String TCI_SERVER = "api.cloud.tibco.com";
	public static final String TCI_APP_STATUS_UPDATING = "updating";
	public static final String TCI_APP_STATUS_BUILDING = "building";
	public static final String TCI_CONTEXT_ROOT = "/tci/v1/subscriptions/";
}

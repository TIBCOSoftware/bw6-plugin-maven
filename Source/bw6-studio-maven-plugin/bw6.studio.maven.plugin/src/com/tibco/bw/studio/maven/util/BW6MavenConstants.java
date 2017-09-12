package com.tibco.bw.studio.maven.util;

import org.eclipse.core.runtime.QualifiedName;

public class BW6MavenConstants {

	public static final String MAVEN_NATURE_ID = "org.eclipse.m2e.core.maven2Nature"; //$NON-NLS-1$
	public static final String POM_XML_LOCATION = "/pom.xml"; //$NON-NLS-1$
	public static final String HEADER_BW_SHARED_MODULE = "TIBCO-BW-SharedModule"; //$NON-NLS-1$
	public static final String HEADER_BW_SHARED_MODULE_VALUE = "META-INF/module.bwm"; //$NON-NLS-1$
	public static final String HEADER_BUNDLE_NAME = "Bundle-SymbolicName"; //$NON-NLS-1$
	
	public static final String EXTERNAL_SM_URI_SCHEME = "zip:/?file:/"; //$NON-NLS-1$
	
	public static final QualifiedName PLUGIN_PROPERTY_EXTERNAL_SM = new QualifiedName("PLUGIN_ID", "SHARED_MODULE_TYPE"); //$NON-NLS-1$ //$NON-NLS-2$
	public static final String PLUGIN_PROPERTY_VALUE_EXTERNAL_SM = "EXT_SM"; //$NON-NLS-1$
}

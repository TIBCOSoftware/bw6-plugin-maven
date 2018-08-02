/*
 * Copyright� 2011 - 2013 TIBCO Software Inc. 
 * All rights reserved. 
 * 
 * This software is confidential and proprietary information of TIBCO Software Inc.
 */
package com.tibco.bw.studio.maven.preferences;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {

	protected static final String BUNDLE_NAME = "com.tibco.bw.studio.maven.preferences.messages"; //$NON-NLS-1$

	static {
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	public static ResourceBundle getBundle() {
		return ResourceBundle.getBundle(BUNDLE_NAME);
	}

	public static String getString(String key) {
		try {
			return ResourceBundle.getBundle(BUNDLE_NAME).getString(key);
		} catch (MissingResourceException e) {
			return key;
		}
	}


	public static String MavenDefaultsPreferencePage_description_label;
	public static String MavenDefaultsPreferencePage_defaultgroupID;
	public static String MavenDefaultsPreferencePage_defaultdockerURL;

	public static String MavenDefaultsPreferencePage_MavenWizardGroup_label;
	public static String MavenDefaultsPreferencePage_groupID_label;
	public static String MavenDefaultsPreferencePage_dockerURL_label;
	
	public static String MavenProjectPreferenceHelper_emptygroupID_label;
	public static String MavenProjectPreferenceHelper_emptydockerURL_label;
	
}

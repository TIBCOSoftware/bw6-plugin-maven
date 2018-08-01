/*
 * Copyright� 2011 - 2013 TIBCO Software Inc. 
 * All rights reserved. 
 * 
 * This software is confidential and proprietary information of TIBCO Software Inc.
 */
package com.tibco.bw.studio.maven.preferences;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.pde.internal.core.util.VersionUtil;

import com.tibco.bw.studio.maven.plugin.Activator;


public class MavenProjectPreferenceHelper {
	public static final String PREFS_MAVEN_DEFAULTS_GROUP_ID = "tibco.preference.bw.MavenDefaults.groupID"; //$NON-NLS-1$
	public static final String PREFS_MAVEN_DEFAULTS_DOCKER_URL = "tibco.preference.bw.MavenDefaults.dockerURL"; //$NON-NLS-1$
	
	public static MavenProjectPreferenceHelper INSTANCE = new MavenProjectPreferenceHelper();

	
	public String getDefaultGroupID() {
		return getStringPreference(PREFS_MAVEN_DEFAULTS_GROUP_ID);
	}

	public String getDefaultGroupID(String defaultValue) {
		String value = getStringPreference(PREFS_MAVEN_DEFAULTS_GROUP_ID);
		if (value == null || value.equals("")) { // $NON-NLS-1$
			value = defaultValue;
		}
		return value;
	}

	public void setDefaultGroupID(String organizationName) {
		setPreference(PREFS_MAVEN_DEFAULTS_GROUP_ID, organizationName);
	}

	public String getDefaultDockerURL() {
		return getStringPreference(PREFS_MAVEN_DEFAULTS_DOCKER_URL);
	}

	public String getDefaultDockerURL(String defaultValue) {
		String value = getStringPreference(PREFS_MAVEN_DEFAULTS_DOCKER_URL);
		if (value == null || value.equals("")) { // $NON-NLS-1$
			value = defaultValue;
		}
		return value;
	}

	public void setDefaultDockerURL(String version) {
		setPreference(PREFS_MAVEN_DEFAULTS_DOCKER_URL, version);
	}


	/**
	 * 
	 * @param value
	 * @return
	 */
	public IStatus validategroupID(String value) {
		if (value == null || value.equals("")) { // $NON-NLS-1$
			return new Status(IStatus.ERROR, Activator.PLUGIN_ID, Messages.MavenProjectPreferenceHelper_emptygroupID_label, null);
		}
		return Status.OK_STATUS;
	}

	/**
	 * 
	 * @param value
	 * @return
	 */
	public IStatus validatedockerURL(String value) {
		if (value == null || value.equals("")) { // $NON-NLS-1$
			return new Status(IStatus.ERROR, Activator.PLUGIN_ID, Messages.MavenProjectPreferenceHelper_emptydockerURL_label, null);
		}
		return null;
	}

	/**
	 * 
	 * @param value
	 * @return
	 */
	
	/**
     * 
     * @param preferenceName
     * @return
     */
    protected boolean containsPreference(String preferenceName) {
        IPreferenceStore store = getPreferenceStore();
        if (store != null) {
            return store.contains(preferenceName);
        }
        return false;
    }
	
	/**
     * 
     * @param preferenceName
     * @return
     */
	protected boolean getBooleanPreference(String preferenceName) {
        IPreferenceStore store = getPreferenceStore();
        if (store != null) {
            return store.getBoolean(preferenceName);
        }
        return false;
    }

	/**
	 * 
	 * @param preferenceName
	 * @return
	 */
	protected String getStringPreference(String preferenceName) {
		IPreferenceStore store = getPreferenceStore();
		if (store != null) {
			return store.getString(preferenceName);
		}
		return null;
	}
	
	/**
     * 
     * @param preferenceName
     * @param preferenceValue
     */
    protected void setPreference(String preferenceName, boolean preferenceValue) {
        IPreferenceStore store = getPreferenceStore();
        if (store != null) {
            store.setValue(preferenceName, preferenceValue);
        }
    }

	/**
	 * 
	 * @param preferenceName
	 * @param preferenceValue
	 */
	protected void setPreference(String preferenceName, String preferenceValue) {
		IPreferenceStore store = getPreferenceStore();
		if (store != null) {
			store.setValue(preferenceName, preferenceValue);
		}
	}

	/**
	 * 
	 * @return
	 */
	protected IPreferenceStore getPreferenceStore() {
		return Activator.getDefault().getPreferenceStore();
	}
	

}

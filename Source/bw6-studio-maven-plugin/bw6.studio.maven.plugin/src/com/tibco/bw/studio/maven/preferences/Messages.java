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

	public static String MavenDefaultsPreferencePage_MavenWizardGroup_label;
	public static String MavenDefaultsPreferencePage_groupID_label;
	
	public static String MavenDefaultsPreferencePage_dockerGroup_label;
	public static String MavenDefaultsPreferencePage_docker_URL_label;
	public static String MavenDefaultsPreferencePage_docker_CertPath_label;
	public static String MavenDefaultsPreferencePage_docker_ImageName_label;
	public static String MavenDefaultsPreferencePage_docker_BWCEImage_label;
	public static String MavenDefaultsPreferencePage_docker_Maintainer_label;
	public static String MavenDefaultsPreferencePage_docker_AppName_label;
	public static String MavenDefaultsPreferencePage_docker_Ports_label;

	public static String MavenDefaultsPreferencePage_KubernetesGroup_label;
	public static String MavenDefaultsPreferencePage_Kubernetes_DeploymentName_label;
	public static String MavenDefaultsPreferencePage_Kubernetes_NoOfReplicas_label;
	public static String MavenDefaultsPreferencePage_Kubernetes_ServiceName_label;
	public static String MavenDefaultsPreferencePage_Kubernetes_ServiceType_label;
	public static String MavenDefaultsPreferencePage_Kubernetes_ContainerPort_label;
	public static String MavenDefaultsPreferencePage_Kubernetes_K8SNamespace_label;
	public static String MavenDefaultsPreferencePage_Kubernetes_EnvVars_label;

	public static String MavenDefaultsPreferencePage_PCFGroup_label;
	public static String MavenDefaultsPreferencePage_PCF_Target_label;
	public static String MavenDefaultsPreferencePage_PCF_ServerName_label;
	public static String MavenDefaultsPreferencePage_PCF_Org_label;
	public static String MavenDefaultsPreferencePage_PCF_Space_label;
	public static String MavenDefaultsPreferencePage_PCF_AppName_label;
	public static String MavenDefaultsPreferencePage_PCF_AppInstances_label;
	public static String MavenDefaultsPreferencePage_PCF_AppMemory_label;
	public static String MavenDefaultsPreferencePage_PCF_AppBuildpack_label;
	public static String MavenDefaultsPreferencePage_PCF_EnvVars_label;
	public static String MavenDefaultsPreferencePage_PCFLoginGroup_label;
	public static String MavenDefaultsPreferencePage_PCFLogin_Username_label;

	public static String MavenProjectPreferenceHelper_emptygroupID_label;
	
	public static String MavenProjectPreferenceHelper_emptyPCF_Target_label;
	public static String MavenProjectPreferenceHelper_emptyPCF_ServerName_label;
	public static String MavenProjectPreferenceHelper_emptyPCF_Org_label;
	public static String MavenProjectPreferenceHelper_emptyPCF_Space_label;
	public static String MavenProjectPreferenceHelper_emptyPCF_AppName_label;
	public static String MavenProjectPreferenceHelper_emptyPCF_AppInstances_label;
	public static String MavenProjectPreferenceHelper_emptyPCF_AppMemory_label;
	public static String MavenProjectPreferenceHelper_emptyPCF_AppBuildpack_label;
	public static String MavenProjectPreferenceHelper_emptyPCF_EnvVars_label;
	public static String MavenProjectPreferenceHelper_emptyPCFLogin_Username_label;
	
	public static String MavenProjectPreferenceHelper_emptydockerURL_label;
	public static String MavenProjectPreferenceHelper_emptydocker_CertPath_label;
	public static String MavenProjectPreferenceHelper_emptydocker_ImageName_label;
	public static String MavenProjectPreferenceHelper_emptydocker_BWCEImage_label;
	public static String MavenProjectPreferenceHelper_emptydocker_Maintainer_label;
	public static String MavenProjectPreferenceHelper_emptydocker_AppName_label;
	public static String MavenProjectPreferenceHelper_emptydocker_Ports_label;
	
	public static String MavenProjectPreferenceHelper_emptyKubernetes_DeploymentName_label;
	public static String MavenProjectPreferenceHelper_emptyKubernetes_NoOfReplicas_label;
	public static String MavenProjectPreferenceHelper_emptyKubernetes_ServiceName_label;
	public static String MavenProjectPreferenceHelper_emptyKubernetes_ServiceType_label;
	public static String MavenProjectPreferenceHelper_emptyKubernetes_ContainerPort_label;
	public static String MavenProjectPreferenceHelper_emptyKubernetes_K8SNamespace_label;
	public static String MavenProjectPreferenceHelper_emptyKubernetes_EnvVars_label;
	
}

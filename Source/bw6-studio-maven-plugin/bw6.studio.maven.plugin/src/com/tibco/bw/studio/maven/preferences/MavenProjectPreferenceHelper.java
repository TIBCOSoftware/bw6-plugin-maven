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

	public static final String PREFS_MAVEN_DEFAULTS_PCF_TARGET = "tibco.preference.bw.MavenDefaults.PCF.Target";
	public static final String PREFS_MAVEN_DEFAULTS_PCF_SERVERNAME = "tibco.preference.bw.MavenDefaults.PCF.ServerName";
	public static final String PREFS_MAVEN_DEFAULTS_PCF_ORG = "tibco.preference.bw.MavenDefaults.PCF.Org";
	public static final String PREFS_MAVEN_DEFAULTS_PCF_SPACE = "tibco.preference.bw.MavenDefaults.PCF.Space";
	public static final String PREFS_MAVEN_DEFAULTS_PCF_APPNAME = "tibco.preference.bw.MavenDefaults.PCF.AppName";
	public static final String PREFS_MAVEN_DEFAULTS_PCF_APPINSTANCES = "tibco.preference.bw.MavenDefaults.PCF.AppInstances";
	public static final String PREFS_MAVEN_DEFAULTS_PCF_APPMEMORY = "tibco.preference.bw.MavenDefaults.PCF.AppMemory";
	public static final String PREFS_MAVEN_DEFAULTS_PCF_APPBUILDPACK = "tibco.preference.bw.MavenDefaults.PCF.AppBuildpack";
	public static final String PREFS_MAVEN_DEFAULTS_PCF_ENVVARS = "tibco.preference.bw.MavenDefaults.PCF.EnvVars";
	public static final String PREFS_MAVEN_DEFAULTS_PCF_USERNAME = "tibco.preference.bw.MavenDefaults.PCF.Username";
	
	public static final String PREFS_MAVEN_DEFAULTS_DOCKER_URL = "tibco.preference.bw.MavenDefaults.docker.URL"; 
	public static final String PREFS_MAVEN_DEFAULTS_DOCKER_CERTPATH = "tibco.preference.bw.MavenDefaults.docker.CertPath";
	public static final String PREFS_MAVEN_DEFAULTS_DOCKER_IMAGENAME = "tibco.preference.bw.MavenDefaults.docker.ImageName";
	public static final String PREFS_MAVEN_DEFAULTS_DOCKER_BWCEIMAGE = "tibco.preference.bw.MavenDefaults.docker.BWCEImage";
	public static final String PREFS_MAVEN_DEFAULTS_DOCKER_MAINTAINER = "tibco.preference.bw.MavenDefaults.docker.Maintainer";
	public static final String PREFS_MAVEN_DEFAULTS_DOCKER_APPNAME = "tibco.preference.bw.MavenDefaults.docker.AppName";
	public static final String PREFS_MAVEN_DEFAULTS_DOCKER_PORTS = "tibco.preference.bw.MavenDefaults.docker.Ports";
	
	public static final String PREFS_MAVEN_DEFAULTS_KUBERNETES_DEPLOYMENTNAME = "tibco.preference.bw.MavenDefaults.Kubernetes.DeploymentName";
	public static final String PREFS_MAVEN_DEFAULTS_KUBERNETES_NOOFREPLICAS = "tibco.preference.bw.MavenDefaults.Kubernetes.NoOfReplicas";
	public static final String PREFS_MAVEN_DEFAULTS_KUBERNETES_SERVICENAME = "tibco.preference.bw.MavenDefaults.Kubernetes.ServiceName";
	public static final String PREFS_MAVEN_DEFAULTS_KUBERNETES_SERVICETYPE = "tibco.preference.bw.MavenDefaults.Kubernetes.ServiceType";
	public static final String PREFS_MAVEN_DEFAULTS_KUBERNETES_CONTAINERPORT = "tibco.preference.bw.MavenDefaults.Kubernetes.ContainerPort";
	public static final String PREFS_MAVEN_DEFAULTS_KUBERNETES_K8SNAMESPACE = "tibco.preference.bw.MavenDefaults.Kubernetes.K8SNamespace";
	public static final String PREFS_MAVEN_DEFAULTS_KUBERNETES_ENVVARS = "tibco.preference.bw.MavenDefaults.Kubernetes.EnvVars";
	
	
	
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


	public String getDefaultPCF_Target(String defaultValue) {
		String value = getStringPreference(PREFS_MAVEN_DEFAULTS_PCF_TARGET);
		if (value == null || value.equals("")) { // $NON-NLS-1$
			value = defaultValue;
		}
		return value;
	}

	public void setDefaultPCF_Target(String Target) {
		setPreference(PREFS_MAVEN_DEFAULTS_PCF_TARGET, Target);
	}
	
	public String getDefaultPCF_ServerName(String defaultValue) {
		String value = getStringPreference(PREFS_MAVEN_DEFAULTS_PCF_SERVERNAME );
		if (value == null || value.equals("")) { // $NON-NLS-1$
			value = defaultValue;
		}
		return value;
	}

	public void setDefaultPCF_ServerName(String ServerName) {
		setPreference(PREFS_MAVEN_DEFAULTS_PCF_SERVERNAME , ServerName);
	}
	
	public String getDefaultPCF_Org(String defaultValue) {
		String value = getStringPreference(PREFS_MAVEN_DEFAULTS_PCF_ORG );
		if (value == null || value.equals("")) { // $NON-NLS-1$
			value = defaultValue;
		}
		return value;
	}

	public void setDefaultPCF_Org(String Org) {
		setPreference(PREFS_MAVEN_DEFAULTS_PCF_ORG , Org);
	}
	
	public String getDefaultPCF_Space(String defaultValue) {
		String value = getStringPreference(PREFS_MAVEN_DEFAULTS_PCF_SPACE );
		if (value == null || value.equals("")) { // $NON-NLS-1$
			value = defaultValue;
		}
		return value;
	}

	public void setDefaultPCF_Space(String Space) {
		setPreference(PREFS_MAVEN_DEFAULTS_PCF_SPACE , Space);
	}
	
	public String getDefaultPCF_AppName(String defaultValue) {
		String value = getStringPreference(PREFS_MAVEN_DEFAULTS_PCF_APPNAME );
		if (value == null || value.equals("")) { // $NON-NLS-1$
			value = defaultValue;
		}
		return value;
	}

	public void setDefaultPCF_AppName(String AppName) {
		setPreference(PREFS_MAVEN_DEFAULTS_PCF_APPNAME , AppName);
	}
	
	public String getDefaultPCF_AppInstances(String defaultValue) {
		String value = getStringPreference(PREFS_MAVEN_DEFAULTS_PCF_APPINSTANCES );
		if (value == null || value.equals("")) { // $NON-NLS-1$
			value = defaultValue;
		}
		return value;
	}

	public void setDefaultPCF_AppInstances(String AppInstances) {
		setPreference(PREFS_MAVEN_DEFAULTS_PCF_APPINSTANCES, AppInstances);
	}
	
	public String getDefaultPCF_AppMemory(String defaultValue) {
		String value = getStringPreference(PREFS_MAVEN_DEFAULTS_PCF_APPMEMORY );
		if (value == null || value.equals("")) { // $NON-NLS-1$
			value = defaultValue;
		}
		return value;
	}

	public void setDefaultPCF_AppMemory(String AppMemory) {
		setPreference(PREFS_MAVEN_DEFAULTS_PCF_APPMEMORY, AppMemory);
	}
	
	public String getDefaultPCF_AppBuildpack(String defaultValue) {
		String value = getStringPreference(PREFS_MAVEN_DEFAULTS_PCF_APPBUILDPACK );
		if (value == null || value.equals("")) { // $NON-NLS-1$
			value = defaultValue;
		}
		return value;
	}

	public void setDefaultPCF_AppBuildpack(String AppBuildpack) {
		setPreference(PREFS_MAVEN_DEFAULTS_PCF_APPBUILDPACK, AppBuildpack);
	}
	
	public String getDefaultPCF_EnvVars(String defaultValue) {
		String value = getStringPreference(PREFS_MAVEN_DEFAULTS_PCF_ENVVARS );
		if (value == null || value.equals("")) { // $NON-NLS-1$
			value = defaultValue;
		}
		return value;
	}

	public void setDefaultPCF_EnvVars(String EnvVars) {
		setPreference(PREFS_MAVEN_DEFAULTS_PCF_ENVVARS, EnvVars);
	}
	
	public String getDefaultPCF_Username(String defaultValue) {
		String value = getStringPreference(PREFS_MAVEN_DEFAULTS_PCF_USERNAME );
		if (value == null || value.equals("")) { // $NON-NLS-1$
			value = defaultValue;
		}
		return value;
	}

	public void setDefaultPCF_Username(String Username) {
		setPreference(PREFS_MAVEN_DEFAULTS_PCF_USERNAME, Username);
	}
	
	public String getDefaultDocker_URL() {
		return getStringPreference(PREFS_MAVEN_DEFAULTS_DOCKER_URL);
	}

	public String getDefaultDocker_URL(String defaultValue) {
		String value = getStringPreference(PREFS_MAVEN_DEFAULTS_DOCKER_URL);
		if (value == null || value.equals("")) { // $NON-NLS-1$
			value = defaultValue;
		}
		return value;
	}

	public void setDefaultDocker_URL(String version) {
		setPreference(PREFS_MAVEN_DEFAULTS_DOCKER_URL, version);
	}

	public String getDefaultDocker_CertPath(String defaultValue) {
		String value = getStringPreference(PREFS_MAVEN_DEFAULTS_DOCKER_CERTPATH);
		if (value == null || value.equals("")) { // $NON-NLS-1$
			value = defaultValue;
		}
		return value;
	}

	public void setDefaultDocker_CertPath(String CertPath) {
		setPreference(PREFS_MAVEN_DEFAULTS_DOCKER_CERTPATH, CertPath);
	}
	
	public String getDefaultDocker_ImageName(String defaultValue) {
		String value = getStringPreference(PREFS_MAVEN_DEFAULTS_DOCKER_IMAGENAME);
		if (value == null || value.equals("")) { // $NON-NLS-1$
			value = defaultValue;
		}
		return value;
	}

	public void setDefaultDocker_ImageName(String ImageName) {
		setPreference(PREFS_MAVEN_DEFAULTS_DOCKER_IMAGENAME, ImageName);
	}
	
	public String getDefaultDocker_BWCEImage(String defaultValue) {
		String value = getStringPreference(PREFS_MAVEN_DEFAULTS_DOCKER_BWCEIMAGE);
		if (value == null || value.equals("")) { // $NON-NLS-1$
			value = defaultValue;
		}
		return value;
	}

	public void setDefaultDocker_BWCEImage(String BWCEImage) {
		setPreference(PREFS_MAVEN_DEFAULTS_DOCKER_BWCEIMAGE, BWCEImage);
	}
	
	public String getDefaultDocker_Maintainer(String defaultValue) {
		String value = getStringPreference(PREFS_MAVEN_DEFAULTS_DOCKER_MAINTAINER);
		if (value == null || value.equals("")) { // $NON-NLS-1$
			value = defaultValue;
		}
		return value;
	}

	public void setDefaultDocker_Maintainer(String Maintainer) {
		setPreference(PREFS_MAVEN_DEFAULTS_DOCKER_MAINTAINER, Maintainer);
	}
	
	public String getDefaultDocker_AppName(String defaultValue) {
		String value = getStringPreference(PREFS_MAVEN_DEFAULTS_DOCKER_APPNAME);
		if (value == null || value.equals("")) { // $NON-NLS-1$
			value = defaultValue;
		}
		return value;
	}

	public void setDefaultDocker_AppName(String AppName) {
		setPreference(PREFS_MAVEN_DEFAULTS_DOCKER_APPNAME, AppName);
	}
	
	public String getDefaultDocker_Ports(String defaultValue) {
		String value = getStringPreference(PREFS_MAVEN_DEFAULTS_DOCKER_PORTS);
		if (value == null || value.equals("")) { // $NON-NLS-1$
			value = defaultValue;
		}
		return value;
	}

	public void setDefaultDocker_Ports(String Ports) {
		setPreference(PREFS_MAVEN_DEFAULTS_DOCKER_PORTS, Ports);
	}
	
	public String getDefaultKubernetes_DeploymentName(String defaultValue) {
		String value = getStringPreference(PREFS_MAVEN_DEFAULTS_KUBERNETES_DEPLOYMENTNAME);
		if (value == null || value.equals("")) { // $NON-NLS-1$
			value = defaultValue;
		}
		return value;
	}

	public void setDefaultKubernetes_DeploymentName(String DeploymentName) {
		setPreference(PREFS_MAVEN_DEFAULTS_KUBERNETES_DEPLOYMENTNAME, DeploymentName);
	}
	
	public String getDefaultKubernetes_NoOfReplicas(String defaultValue) {
		String value = getStringPreference(PREFS_MAVEN_DEFAULTS_KUBERNETES_NOOFREPLICAS);
		if (value == null || value.equals("")) { // $NON-NLS-1$
			value = defaultValue;
		}
		return value;
	}

	public void setDefaultKubernetes_NoOfReplicas(String NoOfReplicas) {
		setPreference(PREFS_MAVEN_DEFAULTS_KUBERNETES_NOOFREPLICAS, NoOfReplicas);
	}
	
	public String getDefaultKubernetes_ServiceName(String defaultValue) {
		String value = getStringPreference(PREFS_MAVEN_DEFAULTS_KUBERNETES_SERVICENAME);
		if (value == null || value.equals("")) { // $NON-NLS-1$
			value = defaultValue;
		}
		return value;
	}

	public void setDefaultKubernetes_ServiceName(String ServiceName) {
		setPreference(PREFS_MAVEN_DEFAULTS_KUBERNETES_SERVICENAME, ServiceName);
	}
	
	public String getDefaultKubernetes_ServiceType(String defaultValue) {
		String value = getStringPreference(PREFS_MAVEN_DEFAULTS_KUBERNETES_SERVICETYPE);
		if (value == null || value.equals("")) { // $NON-NLS-1$
			value = defaultValue;
		}
		return value;
	}

	public void setDefaultKubernetes_ServiceType(String ServiceType) {
		setPreference(PREFS_MAVEN_DEFAULTS_KUBERNETES_SERVICETYPE, ServiceType);
	}
	
	public String getDefaultKubernetes_ContainerPort(String defaultValue) {
		String value = getStringPreference(PREFS_MAVEN_DEFAULTS_KUBERNETES_CONTAINERPORT);
		if (value == null || value.equals("")) { // $NON-NLS-1$
			value = defaultValue;
		}
		return value;
	}

	public void setDefaultKubernetes_ContainerPort(String ContainerPort) {
		setPreference(PREFS_MAVEN_DEFAULTS_KUBERNETES_CONTAINERPORT, ContainerPort);
	}
	
	public String getDefaultKubernetes_K8SNamespace(String defaultValue) {
		String value = getStringPreference(PREFS_MAVEN_DEFAULTS_KUBERNETES_K8SNAMESPACE);
		if (value == null || value.equals("")) { // $NON-NLS-1$
			value = defaultValue;
		}
		return value;
	}

	public void setDefaultKubernetes_K8SNamespace(String K8SNamespace) {
		setPreference(PREFS_MAVEN_DEFAULTS_KUBERNETES_K8SNAMESPACE, K8SNamespace);
	}
	
	public String getDefaultKubernetes_EnvVars(String defaultValue) {
		String value = getStringPreference(PREFS_MAVEN_DEFAULTS_KUBERNETES_ENVVARS);
		if (value == null || value.equals("")) { // $NON-NLS-1$
			value = defaultValue;
		}
		return value;
	}

	public void setDefaultKubernetes_EnvVars(String EnvVars) {
		setPreference(PREFS_MAVEN_DEFAULTS_KUBERNETES_ENVVARS, EnvVars);
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

	public IStatus validatePCF_Target(String value) {
		if (value == null || value.equals("")) { // $NON-NLS-1$
			return new Status(IStatus.ERROR, Activator.PLUGIN_ID, Messages.MavenProjectPreferenceHelper_emptyPCF_Target_label, null);
		}
		return Status.OK_STATUS;
	}
	
	public IStatus validatePCF_ServerName(String value) {
		if (value == null || value.equals("")) { // $NON-NLS-1$
			return new Status(IStatus.ERROR, Activator.PLUGIN_ID, Messages.MavenProjectPreferenceHelper_emptyPCF_ServerName_label, null);
		}
		return Status.OK_STATUS;
	}
	
	public IStatus validatePCF_Org(String value) {
		if (value == null || value.equals("")) { // $NON-NLS-1$
			return new Status(IStatus.ERROR, Activator.PLUGIN_ID, Messages.MavenProjectPreferenceHelper_emptyPCF_Org_label, null);
		}
		return Status.OK_STATUS;
	}
	
	public IStatus validatePCF_Space(String value) {
		if (value == null || value.equals("")) { // $NON-NLS-1$
			return new Status(IStatus.ERROR, Activator.PLUGIN_ID, Messages.MavenProjectPreferenceHelper_emptyPCF_Space_label, null);
		}
		return Status.OK_STATUS;
	}
	
	public IStatus validatePCF_AppName(String value) {
		if (value == null || value.equals("")) { // $NON-NLS-1$
			return new Status(IStatus.ERROR, Activator.PLUGIN_ID, Messages.MavenProjectPreferenceHelper_emptyPCF_AppName_label, null);
		}
		return Status.OK_STATUS;
	}
	
	public IStatus validatePCF_AppInstances(String value) {
		if (value == null || value.equals("")) { // $NON-NLS-1$
			return new Status(IStatus.ERROR, Activator.PLUGIN_ID, Messages.MavenProjectPreferenceHelper_emptyPCF_AppInstances_label, null);
		}
		return Status.OK_STATUS;
	}
	
	public IStatus validatePCF_AppMemory(String value) {
		if (value == null || value.equals("")) { // $NON-NLS-1$
			return new Status(IStatus.ERROR, Activator.PLUGIN_ID, Messages.MavenProjectPreferenceHelper_emptyPCF_AppMemory_label, null);
		}
		return Status.OK_STATUS;
	}
	
	public IStatus validatePCF_AppBuildpack(String value) {
		if (value == null || value.equals("")) { // $NON-NLS-1$
			return new Status(IStatus.ERROR, Activator.PLUGIN_ID, Messages.MavenProjectPreferenceHelper_emptyPCF_AppBuildpack_label, null);
		}
		return Status.OK_STATUS;
	}
	
	public IStatus validatePCF_EnvVars(String value) {
		if (value == null || value.equals("")) { // $NON-NLS-1$
			return new Status(IStatus.ERROR, Activator.PLUGIN_ID, Messages.MavenProjectPreferenceHelper_emptyPCF_EnvVars_label, null);
		}
		return Status.OK_STATUS;
	}
	
	public IStatus validatePCFLogin_Username(String value) {
		if (value == null || value.equals("")) { // $NON-NLS-1$
			return new Status(IStatus.ERROR, Activator.PLUGIN_ID, Messages.MavenDefaultsPreferencePage_PCFLogin_Username_label, null);
		}
		return Status.OK_STATUS;
	}
	
	public IStatus validatedocker_URL(String value) {
		if (value == null || value.equals("")) { // $NON-NLS-1$
			return new Status(IStatus.ERROR, Activator.PLUGIN_ID, Messages.MavenProjectPreferenceHelper_emptydockerURL_label, null);
		}
		return null;
	}

	public IStatus validatedocker_CertPath(String value) {
		if (value == null || value.equals("")) { // $NON-NLS-1$
			return new Status(IStatus.ERROR, Activator.PLUGIN_ID, Messages.MavenProjectPreferenceHelper_emptydocker_CertPath_label, null);
		}
		return null;
	}
	
	public IStatus validatedocker_ImageName(String value) {
		if (value == null || value.equals("")) { // $NON-NLS-1$
			return new Status(IStatus.ERROR, Activator.PLUGIN_ID, Messages.MavenProjectPreferenceHelper_emptydocker_ImageName_label, null);
		}
		return null;
	}
	
	public IStatus validatedocker_BWCEImage(String value) {
		if (value == null || value.equals("")) { // $NON-NLS-1$
			return new Status(IStatus.ERROR, Activator.PLUGIN_ID, Messages.MavenProjectPreferenceHelper_emptydocker_BWCEImage_label, null);
		}
		return null;
	}
	
	public IStatus validatedocker_Maintainer(String value) {
		if (value == null || value.equals("")) { // $NON-NLS-1$
			return new Status(IStatus.ERROR, Activator.PLUGIN_ID, Messages.MavenProjectPreferenceHelper_emptydocker_Maintainer_label, null);
		}
		return null;
	}
	
	public IStatus validatedocker_AppName(String value) {
		if (value == null || value.equals("")) { // $NON-NLS-1$
			return new Status(IStatus.ERROR, Activator.PLUGIN_ID, Messages.MavenProjectPreferenceHelper_emptydocker_AppName_label, null);
		}
		return null;
	}
	
	public IStatus validatedocker_Ports(String value) {
		if (value == null || value.equals("")) { // $NON-NLS-1$
			return new Status(IStatus.ERROR, Activator.PLUGIN_ID, Messages.MavenProjectPreferenceHelper_emptydocker_Ports_label, null);
		}
		return null;
	}
	
	public IStatus validateKubernetes_DeploymentName(String value) {
		if (value == null || value.equals("")) { // $NON-NLS-1$
			return new Status(IStatus.ERROR, Activator.PLUGIN_ID, Messages.MavenProjectPreferenceHelper_emptyKubernetes_DeploymentName_label, null);
		}
		return null;
	}
	
	public IStatus validateKubernetes_NoOfReplicas(String value) {
		if (value == null || value.equals("")) { // $NON-NLS-1$
			return new Status(IStatus.ERROR, Activator.PLUGIN_ID, Messages.MavenProjectPreferenceHelper_emptyKubernetes_NoOfReplicas_label, null);
		}
		return null;
	}
	
	public IStatus validateKubernetes_ServiceName(String value) {
		if (value == null || value.equals("")) { // $NON-NLS-1$
			return new Status(IStatus.ERROR, Activator.PLUGIN_ID, Messages.MavenProjectPreferenceHelper_emptyKubernetes_ServiceName_label, null);
		}
		return null;
	}
	
	public IStatus validateKubernetes_ServiceType(String value) {
		if (value == null || value.equals("")) { // $NON-NLS-1$
			return new Status(IStatus.ERROR, Activator.PLUGIN_ID, Messages.MavenProjectPreferenceHelper_emptyKubernetes_ServiceType_label, null);
		}
		return null;
	}
	
	public IStatus validateKubernetes_ContainerPort(String value) {
		if (value == null || value.equals("")) { // $NON-NLS-1$
			return new Status(IStatus.ERROR, Activator.PLUGIN_ID, Messages.MavenProjectPreferenceHelper_emptyKubernetes_ContainerPort_label, null);
		}
		return null;
	}
	
	public IStatus validateKubernetes_K8SNamespace(String value) {
		if (value == null || value.equals("")) { // $NON-NLS-1$
			return new Status(IStatus.ERROR, Activator.PLUGIN_ID, Messages.MavenProjectPreferenceHelper_emptyKubernetes_K8SNamespace_label, null);
		}
		return null;
	}
	
	public IStatus validateKubernetes_EnvVars(String value) {
		if (value == null || value.equals("")) { // $NON-NLS-1$
			return new Status(IStatus.ERROR, Activator.PLUGIN_ID, Messages.MavenProjectPreferenceHelper_emptyKubernetes_EnvVars_label, null);
		}
		return null;
	}
	
	
	
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

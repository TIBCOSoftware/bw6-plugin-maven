package com.tibco.bw.studio.maven.preferences;

import org.eclipse.jface.preference.IPreferenceStore;

import com.tibco.bw.studio.maven.plugin.Activator;

public class MavenPropertiesFileDefaults {
	public static final String MavenDefaultsPreferencePage_defaultgroupID_value="tibco.preference.bw.MavenDefaults.groupID.value";
	public static final String MavenDefaultsPreferencePage_defaultPCF_Target_value= "tibco.preference.bw.MavenDefaults.PCF.Target.value";
	public static final String MavenDefaultsPreferencePage_defaultPCF_ServerName_value= "tibco.preference.bw.MavenDefaults.PCF.ServerName.value";
	public static final String MavenDefaultsPreferencePage_defaultPCF_Org_value= "tibco.preference.bw.MavenDefaults.PCF.Org.value";
	public static final String MavenDefaultsPreferencePage_defaultPCF_Space_value= "tibco.preference.bw.MavenDefaults.PCF.Space.value";
	public static final String MavenDefaultsPreferencePage_defaultPCF_AppName_value= "tibco.preference.bw.MavenDefaults.PCF.AppName.value";
	public static final String MavenDefaultsPreferencePage_defaultPCF_AppInstances_value = "tibco.preference.bw.MavenDefaults.PCF.AppInstances.value";
	public static final String MavenDefaultsPreferencePage_defaultPCF_AppMemory_value = "tibco.preference.bw.MavenDefaults.PCF.AppMemory.value";
	public static final String MavenDefaultsPreferencePage_defaultPCF_AppBuildpack_value = "tibco.preference.bw.MavenDefaults.PCF.AppBuildpack.value";
	public static final String MavenDefaultsPreferencePage_defaultPCF_EnvVars_value = "tibco.preference.bw.MavenDefaults.PCF.EnvVars.value";
	public static final String MavenDefaultsPreferencePage_defaultPCFLogin_Username_value = "tibco.preference.bw.MavenDefaults.PCF.Username.value";
	public static final String MavenDefaultsPreferencePage_defaultdockerURL_value = "tibco.preference.bw.MavenDefaults.docker.URL.value";
	public static final String MavenDefaultsPreferencePage_defaultdocker_CertPath_value = "tibco.preference.bw.MavenDefaults.docker.CertPath.value";
	public static final String MavenDefaultsPreferencePage_defaultdocker_ImageName_value = "tibco.preference.bw.MavenDefaults.docker.ImageName.value";
	public static final String MavenDefaultsPreferencePage_defaultdocker_BWCEImage_value = "tibco.preference.bw.MavenDefaults.docker.BWCEImage.value";
	public static final String MavenDefaultsPreferencePage_defaultdocker_Maintainer_value = "tibco.preference.bw.MavenDefaults.docker.Maintainer.value";
	public static final String MavenDefaultsPreferencePage_defaultdocker_AppName_value = "tibco.preference.bw.MavenDefaults.docker.AppName.value";
	public static final String MavenDefaultsPreferencePage_defaultdocker_Ports_value = "tibco.preference.bw.MavenDefaults.docker.Ports.value";
	public static final String MavenDefaultsPreferencePage_defaultKubernetes_DeploymentName_value = "tibco.preference.bw.MavenDefaults.Kubernetes.DeploymentName.value";
	public static final String MavenDefaultsPreferencePage_defaultKubernetes_NoOfReplicas_value = "tibco.preference.bw.MavenDefaults.Kubernetes.NoOfReplicas.value";
	public static final String MavenDefaultsPreferencePage_defaultKubernetes_ServiceName_value = "tibco.preference.bw.MavenDefaults.Kubernetes.ServiceName.value";
	public static final String MavenDefaultsPreferencePage_defaultKubernetes_ServiceType_value = "tibco.preference.bw.MavenDefaults.Kubernetes.ServiceType.value";
	public static final String MavenDefaultsPreferencePage_defaultKubernetes_ContainerPort_value = "tibco.preference.bw.MavenDefaults.Kubernetes.ContainerPort.value";
	public static final String MavenDefaultsPreferencePage_defaultKubernetes_K8SNamespace_value = "tibco.preference.bw.MavenDefaults.Kubernetes.K8SNamespace.value";
	public static final String MavenDefaultsPreferencePage_defaultKubernetes_EnvVars_value="tibco.preference.bw.MavenDefaults.Kubernetes.EnvVars.value";
	
	public static MavenPropertiesFileDefaults INSTANCE = new MavenPropertiesFileDefaults();
	
	public String getDefaultGroupID() {
		return getStringPreference(MavenDefaultsPreferencePage_defaultgroupID_value);
	}

	public String getDefaultGroupID(String defaultValue) {
		String value = getStringPreference(MavenDefaultsPreferencePage_defaultgroupID_value);
		if (value == null || value.equals("")) { // $NON-NLS-1$
			value = defaultValue;
		}
		return value;
	}

	public void setDefaultGroupID(String organizationName) {
		setPreference(MavenDefaultsPreferencePage_defaultgroupID_value, organizationName);
	}


	public String getDefaultPCF_Target(String defaultValue) {
		String value = getStringPreference(MavenDefaultsPreferencePage_defaultPCF_Target_value);
		if (value == null || value.equals("")) { // $NON-NLS-1$
			value = defaultValue;
		}
		return value;
	}

	public void setDefaultPCF_Target(String Target) {
		setPreference(MavenDefaultsPreferencePage_defaultPCF_Target_value, Target);
	}
	
	public String getDefaultPCF_ServerName(String defaultValue) {
		String value = getStringPreference(MavenDefaultsPreferencePage_defaultPCF_ServerName_value );
		if (value == null || value.equals("")) { // $NON-NLS-1$
			value = defaultValue;
		}
		return value;
	}

	public void setDefaultPCF_ServerName(String ServerName) {
		setPreference(MavenDefaultsPreferencePage_defaultPCF_ServerName_value , ServerName);
	}
	
	public String getDefaultPCF_Org(String defaultValue) {
		String value = getStringPreference(MavenDefaultsPreferencePage_defaultPCF_Org_value );
		if (value == null || value.equals("")) { // $NON-NLS-1$
			value = defaultValue;
		}
		return value;
	}

	public void setDefaultPCF_Org(String Org) {
		setPreference(MavenDefaultsPreferencePage_defaultPCF_Org_value , Org);
	}
	
	public String getDefaultPCF_Space(String defaultValue) {
		String value = getStringPreference(MavenDefaultsPreferencePage_defaultPCF_Space_value );
		if (value == null || value.equals("")) { // $NON-NLS-1$
			value = defaultValue;
		}
		return value;
	}

	public void setDefaultPCF_Space(String Space) {
		setPreference(MavenDefaultsPreferencePage_defaultPCF_Space_value , Space);
	}
	
	public String getDefaultPCF_AppName(String defaultValue) {
		String value = getStringPreference(MavenDefaultsPreferencePage_defaultPCF_AppName_value );
		if (value == null || value.equals("")) { // $NON-NLS-1$
			value = defaultValue;
		}
		return value;
	}

	public void setDefaultPCF_AppName(String AppName) {
		setPreference(MavenDefaultsPreferencePage_defaultPCF_AppName_value , AppName);
	}
	
	public String getDefaultPCF_AppInstances(String defaultValue) {
		String value = getStringPreference(MavenDefaultsPreferencePage_defaultPCF_AppInstances_value );
		if (value == null || value.equals("")) { // $NON-NLS-1$
			value = defaultValue;
		}
		return value;
	}

	public void setDefaultPCF_AppInstances(String AppInstances) {
		setPreference(MavenDefaultsPreferencePage_defaultPCF_AppInstances_value, AppInstances);
	}
	
	public String getDefaultPCF_AppMemory(String defaultValue) {
		String value = getStringPreference(MavenDefaultsPreferencePage_defaultPCF_AppMemory_value );
		if (value == null || value.equals("")) { // $NON-NLS-1$
			value = defaultValue;
		}
		return value;
	}

	public void setDefaultPCF_AppMemory(String AppMemory) {
		setPreference(MavenDefaultsPreferencePage_defaultPCF_AppMemory_value, AppMemory);
	}
	
	public String getDefaultPCF_AppBuildpack(String defaultValue) {
		String value = getStringPreference(MavenDefaultsPreferencePage_defaultPCF_AppBuildpack_value );
		if (value == null || value.equals("")) { // $NON-NLS-1$
			value = defaultValue;
		}
		return value;
	}

	public void setDefaultPCF_AppBuildpack(String AppBuildpack) {
		setPreference(MavenDefaultsPreferencePage_defaultPCF_AppBuildpack_value, AppBuildpack);
	}
	
	public String getDefaultPCF_EnvVars(String defaultValue) {
		String value = getStringPreference(MavenDefaultsPreferencePage_defaultPCF_EnvVars_value );
		if (value == null || value.equals("")) { // $NON-NLS-1$
			value = defaultValue;
		}
		return value;
	}

	public void setDefaultPCF_EnvVars(String EnvVars) {
		setPreference(MavenDefaultsPreferencePage_defaultPCF_EnvVars_value, EnvVars);
	}
	
	public String getDefaultPCF_Username(String defaultValue) {
		String value = getStringPreference(MavenDefaultsPreferencePage_defaultPCFLogin_Username_value );
		if (value == null || value.equals("")) { // $NON-NLS-1$
			value = defaultValue;
		}
		return value;
	}

	public void setDefaultPCF_Username(String Username) {
		setPreference(MavenDefaultsPreferencePage_defaultPCFLogin_Username_value, Username);
	}
	
	public String getDefaultDocker_URL() {
		return getStringPreference(MavenDefaultsPreferencePage_defaultdockerURL_value);
	}

	public String getDefaultDocker_URL(String defaultValue) {
		String value = getStringPreference(MavenDefaultsPreferencePage_defaultdockerURL_value);
		if (value == null || value.equals("")) { // $NON-NLS-1$
			value = defaultValue;
		}
		return value;
	}

	public void setDefaultDocker_URL(String version) {
		setPreference(MavenDefaultsPreferencePage_defaultdockerURL_value, version);
	}

	public String getDefaultDocker_CertPath(String defaultValue) {
		String value = getStringPreference(MavenDefaultsPreferencePage_defaultdocker_CertPath_value);
		if (value == null || value.equals("")) { // $NON-NLS-1$
			value = defaultValue;
		}
		return value;
	}

	public void setDefaultDocker_CertPath(String CertPath) {
		setPreference(MavenDefaultsPreferencePage_defaultdocker_CertPath_value, CertPath);
	}
	
	public String getDefaultDocker_ImageName(String defaultValue) {
		String value = getStringPreference(MavenDefaultsPreferencePage_defaultdocker_ImageName_value);
		if (value == null || value.equals("")) { // $NON-NLS-1$
			value = defaultValue;
		}
		return value;
	}

	public void setDefaultDocker_ImageName(String ImageName) {
		setPreference(MavenDefaultsPreferencePage_defaultdocker_ImageName_value, ImageName);
	}
	
	public String getDefaultDocker_BWCEImage(String defaultValue) {
		String value = getStringPreference(MavenDefaultsPreferencePage_defaultdocker_BWCEImage_value);
		if (value == null || value.equals("")) { // $NON-NLS-1$
			value = defaultValue;
		}
		return value;
	}

	public void setDefaultDocker_BWCEImage(String BWCEImage) {
		setPreference(MavenDefaultsPreferencePage_defaultdocker_BWCEImage_value, BWCEImage);
	}
	
	public String getDefaultDocker_Maintainer(String defaultValue) {
		String value = getStringPreference(MavenDefaultsPreferencePage_defaultdocker_Maintainer_value);
		if (value == null || value.equals("")) { // $NON-NLS-1$
			value = defaultValue;
		}
		return value;
	}

	public void setDefaultDocker_Maintainer(String Maintainer) {
		setPreference(MavenDefaultsPreferencePage_defaultdocker_Maintainer_value, Maintainer);
	}
	
	public String getDefaultDocker_AppName(String defaultValue) {
		String value = getStringPreference(MavenDefaultsPreferencePage_defaultdocker_AppName_value);
		if (value == null || value.equals("")) { // $NON-NLS-1$
			value = defaultValue;
		}
		return value;
	}

	public void setDefaultDocker_AppName(String AppName) {
		setPreference(MavenDefaultsPreferencePage_defaultdocker_AppName_value, AppName);
	}
	
	public String getDefaultDocker_Ports(String defaultValue) {
		String value = getStringPreference(MavenDefaultsPreferencePage_defaultdocker_Ports_value);
		if (value == null || value.equals("")) { // $NON-NLS-1$
			value = defaultValue;
		}
		return value;
	}

	public void setDefaultDocker_Ports(String Ports) {
		setPreference(MavenDefaultsPreferencePage_defaultdocker_Ports_value, Ports);
	}
	
	public String getDefaultKubernetes_DeploymentName(String defaultValue) {
		String value = getStringPreference(MavenDefaultsPreferencePage_defaultKubernetes_DeploymentName_value);
		if (value == null || value.equals("")) { // $NON-NLS-1$
			value = defaultValue;
		}
		return value;
	}

	public void setDefaultKubernetes_DeploymentName(String DeploymentName) {
		setPreference(MavenDefaultsPreferencePage_defaultKubernetes_DeploymentName_value, DeploymentName);
	}
	
	public String getDefaultKubernetes_NoOfReplicas(String defaultValue) {
		String value = getStringPreference(MavenDefaultsPreferencePage_defaultKubernetes_NoOfReplicas_value);
		if (value == null || value.equals("")) { // $NON-NLS-1$
			value = defaultValue;
		}
		return value;
	}

	public void setDefaultKubernetes_NoOfReplicas(String NoOfReplicas) {
		setPreference(MavenDefaultsPreferencePage_defaultKubernetes_NoOfReplicas_value, NoOfReplicas);
	}
	
	public String getDefaultKubernetes_ServiceName(String defaultValue) {
		String value = getStringPreference(MavenDefaultsPreferencePage_defaultKubernetes_ServiceName_value);
		if (value == null || value.equals("")) { // $NON-NLS-1$
			value = defaultValue;
		}
		return value;
	}

	public void setDefaultKubernetes_ServiceName(String ServiceName) {
		setPreference(MavenDefaultsPreferencePage_defaultKubernetes_ServiceName_value, ServiceName);
	}
	
	public String getDefaultKubernetes_ServiceType(String defaultValue) {
		String value = getStringPreference(MavenDefaultsPreferencePage_defaultKubernetes_ServiceType_value);
		if (value == null || value.equals("")) { // $NON-NLS-1$
			value = defaultValue;
		}
		return value;
	}

	public void setDefaultKubernetes_ServiceType(String ServiceType) {
		setPreference(MavenDefaultsPreferencePage_defaultKubernetes_ServiceType_value, ServiceType);
	}
	
	public String getDefaultKubernetes_ContainerPort(String defaultValue) {
		String value = getStringPreference(MavenDefaultsPreferencePage_defaultKubernetes_ContainerPort_value);
		if (value == null || value.equals("")) { // $NON-NLS-1$
			value = defaultValue;
		}
		return value;
	}

	public void setDefaultKubernetes_ContainerPort(String ContainerPort) {
		setPreference(MavenDefaultsPreferencePage_defaultKubernetes_ContainerPort_value, ContainerPort);
	}
	
	public String getDefaultKubernetes_K8SNamespace(String defaultValue) {
		String value = getStringPreference(MavenDefaultsPreferencePage_defaultKubernetes_K8SNamespace_value);
		if (value == null || value.equals("")) { // $NON-NLS-1$
			value = defaultValue;
		}
		return value;
	}

	public void setDefaultKubernetes_K8SNamespace(String K8SNamespace) {
		setPreference(MavenDefaultsPreferencePage_defaultKubernetes_K8SNamespace_value, K8SNamespace);
	}
	
	public String getDefaultKubernetes_EnvVars(String defaultValue) {
		String value = getStringPreference(MavenDefaultsPreferencePage_defaultKubernetes_EnvVars_value);
		if (value == null || value.equals("")) { // $NON-NLS-1$
			value = defaultValue;
		}
		return value;
	}

	public void setDefaultKubernetes_EnvVars(String EnvVars) {
		setPreference(MavenDefaultsPreferencePage_defaultKubernetes_EnvVars_value, EnvVars);
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

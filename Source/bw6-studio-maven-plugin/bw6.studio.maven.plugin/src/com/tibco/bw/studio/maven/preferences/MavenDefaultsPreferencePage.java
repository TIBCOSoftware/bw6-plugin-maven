/*
 * Copyright© 2011 - 2013 TIBCO Software Inc. 
 * All rights reserved. 
 * 
 * This software is confidential and proprietary information of TIBCO Software Inc.
 */
package com.tibco.bw.studio.maven.preferences;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.tibco.bw.studio.maven.plugin.Activator;
import com.tibco.zion.common.util.BWIniConfigurationUtil;


public class MavenDefaultsPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {


	public static final String ID = "tibco.preference.bw.MavenDefaults"; // $NON-NLS-1$
	File propertyFile = null;
	protected StringFieldEditor groupID;
	protected StringFieldEditor Target;
	protected StringFieldEditor ServerName;
	protected StringFieldEditor Org;
	protected StringFieldEditor Space;
	protected StringFieldEditor PCF_AppName;
	protected StringFieldEditor AppInstances;
	protected StringFieldEditor AppMemory;
	protected StringFieldEditor AppBuildpack;
	protected StringFieldEditor PCF_EnvVars;
	protected StringFieldEditor Username;
	protected StringFieldEditor dockerURL;
	protected StringFieldEditor CertPath;
	protected StringFieldEditor ImageName;
	protected StringFieldEditor BWCEImage;
	protected StringFieldEditor Maintainer;
	protected StringFieldEditor Docker_AppName;
	protected StringFieldEditor Ports;
	protected StringFieldEditor DeploymentName;
	protected StringFieldEditor NoOfReplicas;
	protected StringFieldEditor ServiceName;
	protected StringFieldEditor ServiceType;
	protected StringFieldEditor ContainerPort;
	protected StringFieldEditor K8SNamespace;
	protected StringFieldEditor Kubernetes_EnvVars;
		
	
	public MavenDefaultsPreferencePage() {
		super(GRID);
		setDescription(Messages.MavenDefaultsPreferencePage_description_label);
		
		try {
			String tibcohome = BWIniConfigurationUtil.INSTANCE.getTibcoHome();
			findFile("MavenDefault.properties",new File(tibcohome));
			FileInputStream fileInput = new FileInputStream(propertyFile);
			Properties properties = new Properties();
			properties.load(fileInput);
			fileInput.close();

			    MavenPropertiesFileDefaults.INSTANCE.setDefaultGroupID(properties.getProperty("MavenDefaultsPreferencePage_defaultgroupID"));			
			    MavenPropertiesFileDefaults.INSTANCE.setDefaultPCF_Target(properties.getProperty("MavenDefaultsPreferencePage_defaultPCF_Target"));
			    MavenPropertiesFileDefaults.INSTANCE.setDefaultPCF_ServerName(properties.getProperty("MavenDefaultsPreferencePage_defaultPCF_ServerName"));
			    MavenPropertiesFileDefaults.INSTANCE.setDefaultPCF_Org(properties.getProperty("MavenDefaultsPreferencePage_defaultPCF_Org"));
			    MavenPropertiesFileDefaults.INSTANCE.setDefaultPCF_Space(properties.getProperty("MavenDefaultsPreferencePage_defaultPCF_Space"));	
			    MavenPropertiesFileDefaults.INSTANCE.setDefaultPCF_AppName(properties.getProperty("MavenDefaultsPreferencePage_defaultPCF_AppName"));
			    MavenPropertiesFileDefaults.INSTANCE.setDefaultPCF_AppInstances(properties.getProperty("MavenDefaultsPreferencePage_defaultPCF_AppInstances"));
			    MavenPropertiesFileDefaults.INSTANCE.setDefaultPCF_AppMemory(properties.getProperty("MavenDefaultsPreferencePage_defaultPCF_AppMemory"));
			    MavenPropertiesFileDefaults.INSTANCE.setDefaultPCF_AppBuildpack(properties.getProperty("MavenDefaultsPreferencePage_defaultPCF_AppBuildpack"));
			    MavenPropertiesFileDefaults.INSTANCE.setDefaultPCF_EnvVars(properties.getProperty("MavenDefaultsPreferencePage_defaultPCF_EnvVars"));
			    MavenPropertiesFileDefaults.INSTANCE.setDefaultPCF_Username(properties.getProperty("MavenDefaultsPreferencePage_defaultPCFLogin_Username"));
			    MavenPropertiesFileDefaults.INSTANCE.setDefaultDocker_URL(properties.getProperty("MavenDefaultsPreferencePage_defaultdockerURL"));
			    MavenPropertiesFileDefaults.INSTANCE.setDefaultDocker_CertPath(properties.getProperty("MavenDefaultsPreferencePage_defaultdocker_CertPath"));
			    MavenPropertiesFileDefaults.INSTANCE.setDefaultDocker_ImageName(properties.getProperty("MavenDefaultsPreferencePage_defaultdocker_ImageName"));
			    MavenPropertiesFileDefaults.INSTANCE.setDefaultDocker_BWCEImage(properties.getProperty("MavenDefaultsPreferencePage_defaultdocker_BWCEImage"));
			    MavenPropertiesFileDefaults.INSTANCE.setDefaultDocker_Maintainer(properties.getProperty("MavenDefaultsPreferencePage_defaultdocker_Maintainer"));
			    MavenPropertiesFileDefaults.INSTANCE.setDefaultDocker_AppName(properties.getProperty("MavenDefaultsPreferencePage_defaultdocker_AppName"));
			    MavenPropertiesFileDefaults.INSTANCE.setDefaultDocker_Ports(properties.getProperty("MavenDefaultsPreferencePage_defaultdocker_Ports"));
			    MavenPropertiesFileDefaults.INSTANCE.setDefaultKubernetes_DeploymentName(properties.getProperty("MavenDefaultsPreferencePage_defaultKubernetes_DeploymentName"));
			    MavenPropertiesFileDefaults.INSTANCE.setDefaultKubernetes_NoOfReplicas(properties.getProperty("MavenDefaultsPreferencePage_defaultKubernetes_NoOfReplicas"));
			    MavenPropertiesFileDefaults.INSTANCE.setDefaultKubernetes_ServiceName(properties.getProperty("MavenDefaultsPreferencePage_defaultKubernetes_ServiceName"));
			    MavenPropertiesFileDefaults.INSTANCE.setDefaultKubernetes_ServiceType(properties.getProperty("MavenDefaultsPreferencePage_defaultKubernetes_ServiceType"));
			    MavenPropertiesFileDefaults.INSTANCE.setDefaultKubernetes_ContainerPort(properties.getProperty("MavenDefaultsPreferencePage_defaultKubernetes_ContainerPort"));
			    MavenPropertiesFileDefaults.INSTANCE.setDefaultKubernetes_K8SNamespace(properties.getProperty("MavenDefaultsPreferencePage_defaultKubernetes_K8SNamespace"));
			    MavenPropertiesFileDefaults.INSTANCE.setDefaultKubernetes_EnvVars(properties.getProperty("MavenDefaultsPreferencePage_defaultKubernetes_EnvVars"));
//			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected IPreferenceStore doGetPreferenceStore() {
		return Activator.getDefault().getPreferenceStore();
	}

	protected void setPreference(String preferenceName, String preferenceValue) {
		IPreferenceStore store = getPreferenceStore();
		if (store != null) {
			store.setValue(preferenceName, preferenceValue);
		}
	}
	protected String getStringPreference(String preferenceName) {
		IPreferenceStore store = getPreferenceStore();
		if (store != null) {
			return store.getString(preferenceName);
		}
		return null;
	}
	@Override
	public void init(IWorkbench workbench) {
		
		if (!getPreferenceStore().contains(MavenProjectPreferenceHelper.PREFS_MAVEN_DEFAULTS_GROUP_ID)) {
			getPreferenceStore().setDefault(MavenProjectPreferenceHelper.PREFS_MAVEN_DEFAULTS_GROUP_ID, MavenPropertiesFileDefaults.INSTANCE.getDefaultGroupID("com.tibco.bw"));
		}
		if (!getPreferenceStore().contains(MavenProjectPreferenceHelper.PREFS_MAVEN_DEFAULTS_PCF_TARGET)) {
			getPreferenceStore().setDefault(MavenProjectPreferenceHelper.PREFS_MAVEN_DEFAULTS_PCF_TARGET,MavenPropertiesFileDefaults.INSTANCE.getDefaultPCF_Target("https://api.run.pivotal.io"));
		}
		if (!getPreferenceStore().contains(MavenProjectPreferenceHelper.PREFS_MAVEN_DEFAULTS_PCF_SERVERNAME)) {
			getPreferenceStore().setDefault(MavenProjectPreferenceHelper.PREFS_MAVEN_DEFAULTS_PCF_SERVERNAME,MavenPropertiesFileDefaults.INSTANCE.getDefaultPCF_ServerName("PCF_UK_credential"));
		}
		if (!getPreferenceStore().contains(MavenProjectPreferenceHelper.PREFS_MAVEN_DEFAULTS_PCF_ORG)) {
			getPreferenceStore().setDefault(MavenProjectPreferenceHelper.PREFS_MAVEN_DEFAULTS_PCF_ORG,MavenPropertiesFileDefaults.INSTANCE.getDefaultPCF_Org("tibco"));
		}
		if (!getPreferenceStore().contains(MavenProjectPreferenceHelper.PREFS_MAVEN_DEFAULTS_PCF_SPACE)) {
			getPreferenceStore().setDefault(MavenProjectPreferenceHelper.PREFS_MAVEN_DEFAULTS_PCF_SPACE,MavenPropertiesFileDefaults.INSTANCE.getDefaultPCF_Space("development"));
		}
		if (!getPreferenceStore().contains(MavenProjectPreferenceHelper.PREFS_MAVEN_DEFAULTS_PCF_APPNAME)) {
			getPreferenceStore().setDefault(MavenProjectPreferenceHelper.PREFS_MAVEN_DEFAULTS_PCF_APPNAME,MavenPropertiesFileDefaults.INSTANCE.getDefaultPCF_AppName("AppName"));
		}
		if (!getPreferenceStore().contains(MavenProjectPreferenceHelper.PREFS_MAVEN_DEFAULTS_PCF_APPINSTANCES)) {
			getPreferenceStore().setDefault(MavenProjectPreferenceHelper.PREFS_MAVEN_DEFAULTS_PCF_APPINSTANCES, MavenPropertiesFileDefaults.INSTANCE.getDefaultPCF_AppInstances("1"));
		}
		if (!getPreferenceStore().contains(MavenProjectPreferenceHelper.PREFS_MAVEN_DEFAULTS_PCF_APPMEMORY)) {
			getPreferenceStore().setDefault(MavenProjectPreferenceHelper.PREFS_MAVEN_DEFAULTS_PCF_APPMEMORY,MavenPropertiesFileDefaults.INSTANCE.getDefaultPCF_AppMemory("1024"));
		}
		if (!getPreferenceStore().contains(MavenProjectPreferenceHelper.PREFS_MAVEN_DEFAULTS_PCF_APPBUILDPACK)) {
			getPreferenceStore().setDefault(MavenProjectPreferenceHelper.PREFS_MAVEN_DEFAULTS_PCF_APPBUILDPACK,MavenPropertiesFileDefaults.INSTANCE.getDefaultPCF_AppBuildpack("buildpack"));
		}
		if (!getPreferenceStore().contains(MavenProjectPreferenceHelper.PREFS_MAVEN_DEFAULTS_PCF_ENVVARS)) {
			getPreferenceStore().setDefault(MavenProjectPreferenceHelper.PREFS_MAVEN_DEFAULTS_PCF_ENVVARS,MavenPropertiesFileDefaults.INSTANCE.getDefaultPCF_EnvVars("APP_CONFIG_PROFILE=PCF, BW_LOGLEVEL=DEBUG"));
		}
		if (!getPreferenceStore().contains(MavenProjectPreferenceHelper.PREFS_MAVEN_DEFAULTS_PCF_USERNAME)) {
			getPreferenceStore().setDefault(MavenProjectPreferenceHelper.PREFS_MAVEN_DEFAULTS_PCF_USERNAME,MavenPropertiesFileDefaults.INSTANCE.getDefaultPCF_Username("admin"));
		}
		if (!getPreferenceStore().contains(MavenProjectPreferenceHelper.PREFS_MAVEN_DEFAULTS_DOCKER_URL)) {
			getPreferenceStore().setDefault(MavenProjectPreferenceHelper.PREFS_MAVEN_DEFAULTS_DOCKER_URL,MavenPropertiesFileDefaults.INSTANCE.getDefaultDocker_URL("tcp://0.0.0.0:2376"));
		}
		if (!getPreferenceStore().contains(MavenProjectPreferenceHelper.PREFS_MAVEN_DEFAULTS_DOCKER_CERTPATH)) {
			getPreferenceStore().setDefault(MavenProjectPreferenceHelper.PREFS_MAVEN_DEFAULTS_DOCKER_CERTPATH, MavenPropertiesFileDefaults.INSTANCE.getDefaultDocker_CertPath("</home/user/machine/>"));
		}
		if (!getPreferenceStore().contains(MavenProjectPreferenceHelper.PREFS_MAVEN_DEFAULTS_DOCKER_IMAGENAME)) {
			getPreferenceStore().setDefault(MavenProjectPreferenceHelper.PREFS_MAVEN_DEFAULTS_DOCKER_IMAGENAME, MavenPropertiesFileDefaults.INSTANCE.getDefaultDocker_ImageName("gcr.io/<project_id>/<image-name>"));
		}
		if (!getPreferenceStore().contains(MavenProjectPreferenceHelper.PREFS_MAVEN_DEFAULTS_DOCKER_BWCEIMAGE)) {
			getPreferenceStore().setDefault(MavenProjectPreferenceHelper.PREFS_MAVEN_DEFAULTS_DOCKER_BWCEIMAGE, MavenPropertiesFileDefaults.INSTANCE.getDefaultDocker_BWCEImage("tibco/bwce"));
		}
		if (!getPreferenceStore().contains(MavenProjectPreferenceHelper.PREFS_MAVEN_DEFAULTS_DOCKER_MAINTAINER)) {
			getPreferenceStore().setDefault(MavenProjectPreferenceHelper.PREFS_MAVEN_DEFAULTS_DOCKER_MAINTAINER, MavenPropertiesFileDefaults.INSTANCE.getDefaultDocker_Maintainer("abc@tibco.com"));
		}
		if (!getPreferenceStore().contains(MavenProjectPreferenceHelper.PREFS_MAVEN_DEFAULTS_DOCKER_APPNAME)) {
			getPreferenceStore().setDefault(MavenProjectPreferenceHelper.PREFS_MAVEN_DEFAULTS_DOCKER_APPNAME, MavenPropertiesFileDefaults.INSTANCE.getDefaultDocker_AppName("BWCEAPP"));
		}
		if (!getPreferenceStore().contains(MavenProjectPreferenceHelper.PREFS_MAVEN_DEFAULTS_DOCKER_PORTS)) {
			getPreferenceStore().setDefault(MavenProjectPreferenceHelper.PREFS_MAVEN_DEFAULTS_DOCKER_PORTS, MavenPropertiesFileDefaults.INSTANCE.getDefaultDocker_Ports("18080:8080,17777:7777"));
		}
		if (!getPreferenceStore().contains(MavenProjectPreferenceHelper.PREFS_MAVEN_DEFAULTS_KUBERNETES_DEPLOYMENTNAME)) {
			getPreferenceStore().setDefault(MavenProjectPreferenceHelper.PREFS_MAVEN_DEFAULTS_KUBERNETES_DEPLOYMENTNAME,MavenPropertiesFileDefaults.INSTANCE.getDefaultKubernetes_DeploymentName("bwce-sample"));
		}
		if (!getPreferenceStore().contains(MavenProjectPreferenceHelper.PREFS_MAVEN_DEFAULTS_KUBERNETES_NOOFREPLICAS)) {
			getPreferenceStore().setDefault(MavenProjectPreferenceHelper.PREFS_MAVEN_DEFAULTS_KUBERNETES_NOOFREPLICAS,MavenPropertiesFileDefaults.INSTANCE.getDefaultKubernetes_NoOfReplicas("1"));
		}
		if (!getPreferenceStore().contains(MavenProjectPreferenceHelper.PREFS_MAVEN_DEFAULTS_KUBERNETES_SERVICENAME)) {
			getPreferenceStore().setDefault(MavenProjectPreferenceHelper.PREFS_MAVEN_DEFAULTS_KUBERNETES_SERVICENAME,MavenPropertiesFileDefaults.INSTANCE.getDefaultKubernetes_ServiceName("bwce-sample-service"));
		}
		if (!getPreferenceStore().contains(MavenProjectPreferenceHelper.PREFS_MAVEN_DEFAULTS_KUBERNETES_SERVICETYPE)) {
			getPreferenceStore().setDefault(MavenProjectPreferenceHelper.PREFS_MAVEN_DEFAULTS_KUBERNETES_SERVICETYPE, MavenPropertiesFileDefaults.INSTANCE.getDefaultKubernetes_ServiceType("LoadBalancer"));
		}
		if (!getPreferenceStore().contains(MavenProjectPreferenceHelper.PREFS_MAVEN_DEFAULTS_KUBERNETES_CONTAINERPORT)) {
			getPreferenceStore().setDefault(MavenProjectPreferenceHelper.PREFS_MAVEN_DEFAULTS_KUBERNETES_CONTAINERPORT,MavenPropertiesFileDefaults.INSTANCE.getDefaultKubernetes_ContainerPort("8080"));
			
		}
		if (!getPreferenceStore().contains(MavenProjectPreferenceHelper.PREFS_MAVEN_DEFAULTS_KUBERNETES_K8SNAMESPACE)) {
			getPreferenceStore().setDefault(MavenProjectPreferenceHelper.PREFS_MAVEN_DEFAULTS_KUBERNETES_K8SNAMESPACE,MavenPropertiesFileDefaults.INSTANCE.getDefaultKubernetes_K8SNamespace("default"));
		}
		if (!getPreferenceStore().contains(MavenProjectPreferenceHelper.PREFS_MAVEN_DEFAULTS_KUBERNETES_ENVVARS)) {
			getPreferenceStore().setDefault(MavenProjectPreferenceHelper.PREFS_MAVEN_DEFAULTS_KUBERNETES_ENVVARS, MavenPropertiesFileDefaults.INSTANCE.getDefaultKubernetes_EnvVars("APP_CONFIG_PROFILE=docker, abc=xyz"));
		}
	}
	public void findFile(String name,File file)
    {

        File[] list = file.listFiles();
        if(list!=null)
        for (File fil : list)
        {
            if (fil.isDirectory())
            {
              findFile(name,fil);
            }
            else if (name.equalsIgnoreCase(fil.getName()))
            {
                propertyFile=fil;
            } 
        }
        }
	@Override
	protected void createFieldEditors() {
		Composite parent = getFieldEditorParent();

		Group projectWizardGroup = createProjectWizardGroup(parent);
		GridData gd1 = new GridData(GridData.FILL_HORIZONTAL);
		projectWizardGroup.setLayoutData(gd1);
		updateLayout(projectWizardGroup, 3);
		
		Group PCFWizardGroup = createPCFWizardGroup(parent);
		GridData gd2 = new GridData(GridData.FILL_HORIZONTAL);
		PCFWizardGroup.setLayoutData(gd2);
		updateLayout(PCFWizardGroup, 3);
		
		Group DockerWizardGroup = createDockerWizardGroup(parent);
		GridData gd3 = new GridData(GridData.FILL_HORIZONTAL);
		DockerWizardGroup.setLayoutData(gd3);
		updateLayout(DockerWizardGroup, 3);
		
		Group KubernetesWizardGroup = createKubernetesWizardGroup(parent);
		GridData gd4 = new GridData(GridData.FILL_HORIZONTAL);
		KubernetesWizardGroup.setLayoutData(gd4);
		updateLayout(KubernetesWizardGroup, 3);
		
		getFieldEditorParent().layout(true);
	}

	/**
	 * 
	 * @param parent
	 * @return
	 */
	protected Group createProjectWizardGroup(Composite parent) {
		Group group = new Group(parent, SWT.NONE);
		group.setLayout(new GridLayout());
		group.setText(Messages.MavenDefaultsPreferencePage_MavenWizardGroup_label);

		// line 1
		groupID = new StringFieldEditor( //
				MavenProjectPreferenceHelper.PREFS_MAVEN_DEFAULTS_GROUP_ID, //
				Messages.MavenDefaultsPreferencePage_groupID_label, //
				StringFieldEditor.UNLIMITED, //
				StringFieldEditor.VALIDATE_ON_KEY_STROKE, group) {
			@Override
			protected boolean doCheckState() {
				String value = groupID.getStringValue();
				IStatus status = MavenProjectPreferenceHelper.INSTANCE.validategroupID(value);
				if (status != null && !status.isOK()) {
					setErrorMessage(status.getMessage());
					return false;
				}
				return true;
			}
		};
		addField(groupID);

		Label organizationNameLabel = groupID.getLabelControl(group);
		Text organizationNameText = groupID.getTextControl(group);

		int labelWidth = 110;

		GridData gd11 = new GridData();
		gd11.widthHint = labelWidth;
		organizationNameLabel.setLayoutData(gd11);
		GridData gd12 = new GridData(GridData.FILL_HORIZONTAL);
		gd12.horizontalSpan = 2;
		gd12.grabExcessHorizontalSpace = true;
		organizationNameText.setLayoutData(gd12);
	
		return group;
	}
	protected Group createPCFWizardGroup(Composite parent){
		Group group = new Group(parent, SWT.NONE);
		group.setLayout(new GridLayout());
		group.setText(Messages.MavenDefaultsPreferencePage_PCFGroup_label);

		// line 1
		Target = new StringFieldEditor( //
				MavenProjectPreferenceHelper.PREFS_MAVEN_DEFAULTS_PCF_TARGET, //
				Messages.MavenDefaultsPreferencePage_PCF_Target_label, //
				StringFieldEditor.UNLIMITED, //
				StringFieldEditor.VALIDATE_ON_KEY_STROKE, group) {
			@Override
			protected boolean doCheckState() {
				String value = Target.getStringValue();
				IStatus status = MavenProjectPreferenceHelper.INSTANCE.validatePCF_Target(value);
				if (status != null && !status.isOK()) {
					setErrorMessage(status.getMessage());
					return false;
				}
				return true;
			}
		};
		addField(Target);
		Label targetNameLabel = Target.getLabelControl(group);
		Text targetNameText = Target.getTextControl(group);

		int labelWidth = 110;

		GridData gd11 = new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1);
		gd11.widthHint = labelWidth;
		targetNameLabel.setLayoutData(gd11);
		GridData gd12 = new GridData(GridData.FILL_HORIZONTAL);
		gd12.horizontalSpan = 2;
		gd12.grabExcessHorizontalSpace = true;
		targetNameText.setLayoutData(gd12);
		
		ServerName = new StringFieldEditor( //
				MavenProjectPreferenceHelper.PREFS_MAVEN_DEFAULTS_PCF_SERVERNAME, //
				Messages.MavenDefaultsPreferencePage_PCF_ServerName_label, //
				StringFieldEditor.UNLIMITED, //
				StringFieldEditor.VALIDATE_ON_KEY_STROKE, group) {
			@Override
			protected boolean doCheckState() {
				String value = ServerName.getStringValue();
				IStatus status = MavenProjectPreferenceHelper.INSTANCE.validatePCF_ServerName(value);
				if (status != null && !status.isOK()) {
					setErrorMessage(status.getMessage());
					return false;
				}
				return true;
			}
		};
		addField(ServerName);

		Label ServerNameLabel = ServerName.getLabelControl(group);
		Text ServerNameText = ServerName.getTextControl(group);

		GridData gd21 = new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1);
		gd11.widthHint = labelWidth;
		ServerNameLabel.setLayoutData(gd21);
		GridData gd22 = new GridData(GridData.FILL_HORIZONTAL);
		gd22.horizontalSpan = 2;
		gd22.grabExcessHorizontalSpace = true;
		ServerNameText.setLayoutData(gd22);
		
		Org = new StringFieldEditor( //
				MavenProjectPreferenceHelper.PREFS_MAVEN_DEFAULTS_PCF_ORG, //
				Messages.MavenDefaultsPreferencePage_PCF_Org_label, //
				StringFieldEditor.UNLIMITED, //
				StringFieldEditor.VALIDATE_ON_KEY_STROKE, group) {
			@Override
			protected boolean doCheckState() {
				String value = Org.getStringValue();
				IStatus status = MavenProjectPreferenceHelper.INSTANCE.validatePCF_Org(value);
				if (status != null && !status.isOK()) {
					setErrorMessage(status.getMessage());
					return false;
				}
				return true;
			}
		};
		addField(Org);

		Label OrgNameLabel = Org.getLabelControl(group);
		Text OrgNameText = Org.getTextControl(group);

		GridData gd31 = new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1);
		gd11.widthHint = labelWidth;
		OrgNameLabel.setLayoutData(gd31);
		GridData gd32 = new GridData(GridData.FILL_HORIZONTAL);
		gd32.horizontalSpan = 2;
		gd32.grabExcessHorizontalSpace = true;
		OrgNameText.setLayoutData(gd32);
		
		Space = new StringFieldEditor( //
				MavenProjectPreferenceHelper.PREFS_MAVEN_DEFAULTS_PCF_SPACE, //
				Messages.MavenDefaultsPreferencePage_PCF_Space_label, //
				StringFieldEditor.UNLIMITED, //
				StringFieldEditor.VALIDATE_ON_KEY_STROKE, group) {
			@Override
			protected boolean doCheckState() {
				String value = Space.getStringValue();
				IStatus status = MavenProjectPreferenceHelper.INSTANCE.validatePCF_Space(value);
				if (status != null && !status.isOK()) {
					setErrorMessage(status.getMessage());
					return false;
				}
				return true;
			}
		};
		addField(Space);

		Label SpaceNameLabel = Space.getLabelControl(group);
		Text SpaceNameText = Space.getTextControl(group);

		GridData gd41 = new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1);
		gd41.widthHint = labelWidth;
		SpaceNameLabel.setLayoutData(gd41);
		GridData gd42 = new GridData(GridData.FILL_HORIZONTAL);
		gd42.horizontalSpan = 2;
		gd42.grabExcessHorizontalSpace = true;
		SpaceNameText.setLayoutData(gd42);
		
		PCF_AppName = new StringFieldEditor( //
				MavenProjectPreferenceHelper.PREFS_MAVEN_DEFAULTS_PCF_APPNAME, //
				Messages.MavenDefaultsPreferencePage_PCF_AppName_label, //
				StringFieldEditor.UNLIMITED, //
				StringFieldEditor.VALIDATE_ON_KEY_STROKE, group) {
			@Override
			protected boolean doCheckState() {
				String value = PCF_AppName.getStringValue();
				IStatus status = MavenProjectPreferenceHelper.INSTANCE.validatePCF_AppName(value);
				if (status != null && !status.isOK()) {
					setErrorMessage(status.getMessage());
					return false;
				}
				return true;
			}
		};
		addField(PCF_AppName);
		
		Label PCF_AppNameLabel = PCF_AppName.getLabelControl(group);
		Text PCF_AppNameText = PCF_AppName.getTextControl(group);

		GridData gd51 = new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1);
		gd51.widthHint = labelWidth;
		PCF_AppNameLabel.setLayoutData(gd51);
		GridData gd52 = new GridData(GridData.FILL_HORIZONTAL);
		gd52.horizontalSpan = 2;
		gd52.grabExcessHorizontalSpace = true;
		PCF_AppNameText.setLayoutData(gd52);
		
		AppInstances = new StringFieldEditor( //
				MavenProjectPreferenceHelper.PREFS_MAVEN_DEFAULTS_PCF_APPINSTANCES, //
				Messages.MavenDefaultsPreferencePage_PCF_AppInstances_label, //
				StringFieldEditor.UNLIMITED, //
				StringFieldEditor.VALIDATE_ON_KEY_STROKE, group) {
			@Override
			protected boolean doCheckState() {
				String value = AppInstances.getStringValue();
				IStatus status = MavenProjectPreferenceHelper.INSTANCE.validatePCF_AppInstances(value);
				if (status != null && !status.isOK()) {
					setErrorMessage(status.getMessage());
					return false;
				}
				return true;
			}
		};
		addField(AppInstances);
		
		Label AppInstancesLabel = AppInstances.getLabelControl(group);
		Text AppInstancesText = AppInstances.getTextControl(group);

		GridData gd61 = new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1);
		gd61.widthHint = labelWidth;
		AppInstancesLabel.setLayoutData(gd61);
		GridData gd62 = new GridData(GridData.FILL_HORIZONTAL);
		gd62.horizontalSpan = 2;
		gd62.grabExcessHorizontalSpace = true;
		AppInstancesText.setLayoutData(gd62);
		
		AppMemory = new StringFieldEditor( //
				MavenProjectPreferenceHelper.PREFS_MAVEN_DEFAULTS_PCF_APPMEMORY, //
				Messages.MavenDefaultsPreferencePage_PCF_AppMemory_label, //
				StringFieldEditor.UNLIMITED, //
				StringFieldEditor.VALIDATE_ON_KEY_STROKE, group) {
			@Override
			protected boolean doCheckState() {
				String value = AppMemory.getStringValue();
				IStatus status = MavenProjectPreferenceHelper.INSTANCE.validatePCF_AppMemory(value);
				if (status != null && !status.isOK()) {
					setErrorMessage(status.getMessage());
					return false;
				}
				return true;
			}
		};
		addField(AppMemory);
		
		Label AppMemoryLabel = AppMemory.getLabelControl(group);
		Text AppMemoryText = AppMemory.getTextControl(group);

		GridData gd71 = new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1);
		gd61.widthHint = labelWidth;
		AppMemoryLabel.setLayoutData(gd71);
		GridData gd72 = new GridData(GridData.FILL_HORIZONTAL);
		gd72.horizontalSpan = 2;
		gd72.grabExcessHorizontalSpace = true;
		AppMemoryText.setLayoutData(gd72);
		
		AppBuildpack = new StringFieldEditor( //
				MavenProjectPreferenceHelper.PREFS_MAVEN_DEFAULTS_PCF_APPBUILDPACK, //
				Messages.MavenDefaultsPreferencePage_PCF_AppBuildpack_label, //
				StringFieldEditor.UNLIMITED, //
				StringFieldEditor.VALIDATE_ON_KEY_STROKE, group) {
			@Override
			protected boolean doCheckState() {
				String value = AppBuildpack.getStringValue();
				IStatus status = MavenProjectPreferenceHelper.INSTANCE.validatePCF_AppBuildpack(value);
				if (status != null && !status.isOK()) {
					setErrorMessage(status.getMessage());
					return false;
				}
				return true;
			}
		};
		addField(AppBuildpack);
		
		Label AppBuildpackLabel = AppBuildpack.getLabelControl(group);
		Text AppBuildpackText = AppBuildpack.getTextControl(group);

		GridData gd81 = new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1);
		gd81.widthHint = labelWidth;
		AppBuildpackLabel.setLayoutData(gd81);
		GridData gd82 = new GridData(GridData.FILL_HORIZONTAL);
		gd82.horizontalSpan = 2;
		gd82.grabExcessHorizontalSpace = true;
		AppBuildpackText.setLayoutData(gd82);
		
		PCF_EnvVars = new StringFieldEditor( //
				MavenProjectPreferenceHelper.PREFS_MAVEN_DEFAULTS_PCF_ENVVARS, //
				Messages.MavenDefaultsPreferencePage_PCF_EnvVars_label, //
				StringFieldEditor.UNLIMITED, //
				StringFieldEditor.VALIDATE_ON_KEY_STROKE, group) {
			@Override
			protected boolean doCheckState() {
				String value = PCF_EnvVars.getStringValue();
				IStatus status = MavenProjectPreferenceHelper.INSTANCE.validatePCF_EnvVars(value);
				if (status != null && !status.isOK()) {
					setErrorMessage(status.getMessage());
					return false;
				}
				return true;
			}
		};
		addField(PCF_EnvVars);
		
		Label PCF_EnvVarsLabel = PCF_EnvVars.getLabelControl(group);
		Text PCF_EnvVarsText = PCF_EnvVars.getTextControl(group);

		GridData gd91 = new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1);
		gd91.widthHint = labelWidth;
		PCF_EnvVarsLabel.setLayoutData(gd91);
		GridData gd92 = new GridData(GridData.FILL_HORIZONTAL);
		gd92.horizontalSpan = 2;
		gd92.grabExcessHorizontalSpace = true;
		PCF_EnvVarsText.setLayoutData(gd92);
		
		Username = new StringFieldEditor( //
				MavenProjectPreferenceHelper.PREFS_MAVEN_DEFAULTS_PCF_USERNAME, //
				Messages.MavenDefaultsPreferencePage_PCFLogin_Username_label, //
				StringFieldEditor.UNLIMITED, //
				StringFieldEditor.VALIDATE_ON_KEY_STROKE, group) {
			@Override
			protected boolean doCheckState() {
				String value = Username.getStringValue();
				IStatus status = MavenProjectPreferenceHelper.INSTANCE.validatePCFLogin_Username(value);
				if (status != null && !status.isOK()) {
					setErrorMessage(status.getMessage());
					return false;
				}
				return true;
			}
		};
		addField(Username);
		
		Label UsernameLabel = Username.getLabelControl(group);
		Text UsernameText = Username.getTextControl(group);

		GridData gd101 = new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1);
		gd101.widthHint = labelWidth;
		UsernameLabel.setLayoutData(gd101);
		GridData gd102 = new GridData(GridData.FILL_HORIZONTAL);
		gd102.horizontalSpan = 2;
		gd102.grabExcessHorizontalSpace = true;
		UsernameText.setLayoutData(gd102);
		
		return group;
		
	}
	
	private Group createDockerWizardGroup(Composite parent) {
		Group group = new Group(parent, SWT.NONE);
		group.setLayout(new GridLayout());
		group.setText(Messages.MavenDefaultsPreferencePage_dockerGroup_label);

		dockerURL = new StringFieldEditor( //
				MavenProjectPreferenceHelper.PREFS_MAVEN_DEFAULTS_DOCKER_URL, //
				Messages.MavenDefaultsPreferencePage_docker_URL_label, //
				StringFieldEditor.UNLIMITED, //
				StringFieldEditor.VALIDATE_ON_KEY_STROKE, group) {
			@Override
			protected boolean doCheckState() {
				String value = dockerURL.getStringValue();
				IStatus status = MavenProjectPreferenceHelper.INSTANCE.validatedocker_URL(value);
				if (status != null && !status.isOK()) {
					setErrorMessage(status.getMessage());
					return false;
				}
				return true;
			}
		};
		addField(dockerURL);

		Label URLLabel = dockerURL.getLabelControl(group);
		Text URLText = dockerURL.getTextControl(group);

		int labelWidth = 110;

		GridData gd11 = new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1);
		gd11.widthHint = labelWidth;
		URLLabel.setLayoutData(gd11);
		GridData gd12 = new GridData(GridData.FILL_HORIZONTAL);
		gd12.grabExcessHorizontalSpace = true;
		gd12.horizontalSpan = 2;
		URLText.setLayoutData(gd12);
		
		CertPath = new StringFieldEditor( //
				MavenProjectPreferenceHelper.PREFS_MAVEN_DEFAULTS_DOCKER_CERTPATH, //
				Messages.MavenDefaultsPreferencePage_docker_CertPath_label, //
				StringFieldEditor.UNLIMITED, //
				StringFieldEditor.VALIDATE_ON_KEY_STROKE, group) {
			@Override
			protected boolean doCheckState() {
				String value = CertPath.getStringValue();
				IStatus status = MavenProjectPreferenceHelper.INSTANCE.validatedocker_CertPath(value);
				if (status != null && !status.isOK()) {
					setErrorMessage(status.getMessage());
					return false;
				}
				return true;
			}
		};
		addField(CertPath);

		Label CertPathLabel = CertPath.getLabelControl(group);
		Text CertPathText = CertPath.getTextControl(group);

		GridData gd21 = new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1);
		gd21.widthHint = labelWidth;
		CertPathLabel.setLayoutData(gd21);
		GridData gd22 = new GridData(GridData.FILL_HORIZONTAL);
		gd22.grabExcessHorizontalSpace = true;
		gd22.horizontalSpan = 2;
		CertPathText.setLayoutData(gd22);
		
		ImageName = new StringFieldEditor( //
				MavenProjectPreferenceHelper.PREFS_MAVEN_DEFAULTS_DOCKER_IMAGENAME, //
				Messages.MavenDefaultsPreferencePage_docker_ImageName_label, //
				StringFieldEditor.UNLIMITED, //
				StringFieldEditor.VALIDATE_ON_KEY_STROKE, group) {
			@Override
			protected boolean doCheckState() {
				String value = ImageName.getStringValue();
				IStatus status = MavenProjectPreferenceHelper.INSTANCE.validatedocker_ImageName(value);
				if (status != null && !status.isOK()) {
					setErrorMessage(status.getMessage());
					return false;
				}
				return true;
			}
		};
		addField(ImageName);

		Label ImageNameLabel = ImageName.getLabelControl(group);
		Text ImageNameText = ImageName.getTextControl(group);

		GridData gd31 = new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1);
		gd31.widthHint = labelWidth;
		ImageNameLabel.setLayoutData(gd31);
		GridData gd32 = new GridData(GridData.FILL_HORIZONTAL);
		gd32.grabExcessHorizontalSpace = true;
		gd32.horizontalSpan = 2;
		ImageNameText.setLayoutData(gd32);
		
		BWCEImage = new StringFieldEditor( //
				MavenProjectPreferenceHelper.PREFS_MAVEN_DEFAULTS_DOCKER_BWCEIMAGE, //
				Messages.MavenDefaultsPreferencePage_docker_BWCEImage_label, //
				StringFieldEditor.UNLIMITED, //
				StringFieldEditor.VALIDATE_ON_KEY_STROKE, group) {
			@Override
			protected boolean doCheckState() {
				String value = BWCEImage.getStringValue();
				IStatus status = MavenProjectPreferenceHelper.INSTANCE.validatedocker_BWCEImage(value);
				if (status != null && !status.isOK()) {
					setErrorMessage(status.getMessage());
					return false;
				}
				return true;
			}
		};
		addField(BWCEImage);

		Label BWCEImageLabel = BWCEImage.getLabelControl(group);
		Text BWCEImageText = BWCEImage.getTextControl(group);

		GridData gd41 = new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1);
		gd41.widthHint = labelWidth;
		BWCEImageLabel.setLayoutData(gd41);
		GridData gd42 = new GridData(GridData.FILL_HORIZONTAL);
		gd42.grabExcessHorizontalSpace = true;
		gd42.horizontalSpan = 2;
		BWCEImageText.setLayoutData(gd42);
		
		Maintainer = new StringFieldEditor( //
				MavenProjectPreferenceHelper.PREFS_MAVEN_DEFAULTS_DOCKER_MAINTAINER, //
				Messages.MavenDefaultsPreferencePage_docker_Maintainer_label, //
				StringFieldEditor.UNLIMITED, //
				StringFieldEditor.VALIDATE_ON_KEY_STROKE, group) {
			@Override
			protected boolean doCheckState() {
				String value = Maintainer.getStringValue();
				IStatus status = MavenProjectPreferenceHelper.INSTANCE.validatedocker_Maintainer(value);
				if (status != null && !status.isOK()) {
					setErrorMessage(status.getMessage());
					return false;
				}
				return true;
			}
		};
		addField(Maintainer);

		Label MaintainerLabel = Maintainer.getLabelControl(group);
		Text MaintainerText = Maintainer.getTextControl(group);

		GridData gd51 = new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1);
		gd51.widthHint = labelWidth;
		MaintainerLabel.setLayoutData(gd51);
		GridData gd52 = new GridData(GridData.FILL_HORIZONTAL);
		gd52.grabExcessHorizontalSpace = true;
		gd52.horizontalSpan = 2;
		MaintainerText.setLayoutData(gd52);
		
		Docker_AppName = new StringFieldEditor( //
				MavenProjectPreferenceHelper.PREFS_MAVEN_DEFAULTS_DOCKER_APPNAME, //
				Messages.MavenDefaultsPreferencePage_docker_AppName_label, //
				StringFieldEditor.UNLIMITED, //
				StringFieldEditor.VALIDATE_ON_KEY_STROKE, group) {
			@Override
			protected boolean doCheckState() {
				String value = Docker_AppName.getStringValue();
				IStatus status = MavenProjectPreferenceHelper.INSTANCE.validatedocker_AppName(value);
				if (status != null && !status.isOK()) {
					setErrorMessage(status.getMessage());
					return false;
				}
				return true;
			}
		};
		addField(Docker_AppName);

		Label Docker_AppNameLabel = Docker_AppName.getLabelControl(group);
		Text Docker_AppNameText = Docker_AppName.getTextControl(group);

		GridData gd61 = new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1);
		gd61.widthHint = labelWidth;
		Docker_AppNameLabel.setLayoutData(gd61);
		GridData gd62 = new GridData(GridData.FILL_HORIZONTAL);
		gd62.grabExcessHorizontalSpace = true;
		gd62.horizontalSpan = 2;
		Docker_AppNameText.setLayoutData(gd62);
		
		Ports = new StringFieldEditor( //
				MavenProjectPreferenceHelper.PREFS_MAVEN_DEFAULTS_DOCKER_PORTS, //
				Messages.MavenDefaultsPreferencePage_docker_Ports_label, //
				StringFieldEditor.UNLIMITED, //
				StringFieldEditor.VALIDATE_ON_KEY_STROKE, group) {
			@Override
			protected boolean doCheckState() {
				String value = Ports.getStringValue();
				IStatus status = MavenProjectPreferenceHelper.INSTANCE.validatedocker_Ports(value);
				if (status != null && !status.isOK()) {
					setErrorMessage(status.getMessage());
					return false;
				}
				return true;
			}
		};
		addField(Ports);

		Label PortsLabel = Ports.getLabelControl(group);
		Text PortsText = Ports.getTextControl(group);

		GridData gd71 = new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1);
		gd71.widthHint = labelWidth;
		PortsLabel.setLayoutData(gd71);
		GridData gd72 = new GridData(GridData.FILL_HORIZONTAL);
		gd72.grabExcessHorizontalSpace = true;
		gd72.horizontalSpan = 2;
		PortsText.setLayoutData(gd72);
		
		return group;
	}
	
	private Group createKubernetesWizardGroup(Composite parent) {
		Group group = new Group(parent, SWT.NONE);
		group.setLayout(new GridLayout());
		group.setText(Messages.MavenDefaultsPreferencePage_KubernetesGroup_label);

		DeploymentName = new StringFieldEditor( //
				MavenProjectPreferenceHelper.PREFS_MAVEN_DEFAULTS_KUBERNETES_DEPLOYMENTNAME, //
				Messages.MavenDefaultsPreferencePage_Kubernetes_DeploymentName_label, //
				StringFieldEditor.UNLIMITED, //
				StringFieldEditor.VALIDATE_ON_KEY_STROKE, group) {
			@Override
			protected boolean doCheckState() {
				String value = DeploymentName.getStringValue();
				IStatus status = MavenProjectPreferenceHelper.INSTANCE.validateKubernetes_DeploymentName(value);
				if (status != null && !status.isOK()) {
					setErrorMessage(status.getMessage());
					return false;
				}
				return true;
			}
		};
		addField(DeploymentName);

		Label DeploymentNameLabel = DeploymentName.getLabelControl(group);
		Text DeploymentNameText = DeploymentName.getTextControl(group);

		int labelWidth = 110;

		GridData gd11 = new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1);
		gd11.widthHint = labelWidth;
		DeploymentNameLabel.setLayoutData(gd11);
		GridData gd12 = new GridData(GridData.FILL_HORIZONTAL);
		gd12.grabExcessHorizontalSpace = true;
		gd12.horizontalSpan = 2;
		DeploymentNameText.setLayoutData(gd12);
		
		NoOfReplicas = new StringFieldEditor( //
				MavenProjectPreferenceHelper.PREFS_MAVEN_DEFAULTS_KUBERNETES_NOOFREPLICAS, //
				Messages.MavenDefaultsPreferencePage_Kubernetes_NoOfReplicas_label, //
				StringFieldEditor.UNLIMITED, //
				StringFieldEditor.VALIDATE_ON_KEY_STROKE, group) {
			@Override
			protected boolean doCheckState() {
				String value = NoOfReplicas.getStringValue();
				IStatus status = MavenProjectPreferenceHelper.INSTANCE.validateKubernetes_NoOfReplicas(value);
				if (status != null && !status.isOK()) {
					setErrorMessage(status.getMessage());
					return false;
				}
				return true;
			}
		};
		addField(NoOfReplicas);

		Label NoOfReplicasLabel = NoOfReplicas.getLabelControl(group);
		Text NoOfReplicasText = NoOfReplicas.getTextControl(group);

		GridData gd21 = new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1);
		gd21.widthHint = labelWidth;
		NoOfReplicasLabel.setLayoutData(gd21);
		GridData gd22 = new GridData(GridData.FILL_HORIZONTAL);
		gd22.grabExcessHorizontalSpace = true;
		gd22.horizontalSpan = 2;
		NoOfReplicasText.setLayoutData(gd22);
		
		ServiceName = new StringFieldEditor( //
				MavenProjectPreferenceHelper.PREFS_MAVEN_DEFAULTS_KUBERNETES_SERVICENAME, //
				Messages.MavenDefaultsPreferencePage_Kubernetes_ServiceName_label, //
				StringFieldEditor.UNLIMITED, //
				StringFieldEditor.VALIDATE_ON_KEY_STROKE, group) {
			@Override
			protected boolean doCheckState() {
				String value = ServiceName.getStringValue();
				IStatus status = MavenProjectPreferenceHelper.INSTANCE.validateKubernetes_ServiceName(value);
				if (status != null && !status.isOK()) {
					setErrorMessage(status.getMessage());
					return false;
				}
				return true;
			}
		};
		addField(ServiceName);

		Label ServiceNameLabel = ServiceName.getLabelControl(group);
		Text ServiceNameText = ServiceName.getTextControl(group);

		GridData gd31 = new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1);
		gd31.widthHint = labelWidth;
		ServiceNameLabel.setLayoutData(gd31);
		GridData gd32 = new GridData(GridData.FILL_HORIZONTAL);
		gd32.grabExcessHorizontalSpace = true;
		gd32.horizontalSpan = 2;
		ServiceNameText.setLayoutData(gd32);
		
		ServiceType = new StringFieldEditor( //
				MavenProjectPreferenceHelper.PREFS_MAVEN_DEFAULTS_KUBERNETES_SERVICETYPE, //
				Messages.MavenDefaultsPreferencePage_Kubernetes_ServiceType_label, //
				StringFieldEditor.UNLIMITED, //
				StringFieldEditor.VALIDATE_ON_KEY_STROKE, group) {
			@Override
			protected boolean doCheckState() {
				String value = ServiceType.getStringValue();
				IStatus status = MavenProjectPreferenceHelper.INSTANCE.validateKubernetes_ServiceType(value);
				if (status != null && !status.isOK()) {
					setErrorMessage(status.getMessage());
					return false;
				}
				return true;
			}
		};
		addField(ServiceType);

		Label ServiceTypeLabel = ServiceType.getLabelControl(group);
		Text ServiceTypeText = ServiceType.getTextControl(group);

		GridData gd41 = new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1);
		gd41.widthHint = labelWidth;
		ServiceTypeLabel.setLayoutData(gd41);
		GridData gd42 = new GridData(GridData.FILL_HORIZONTAL);
		gd42.grabExcessHorizontalSpace = true;
		gd42.horizontalSpan = 2;
		ServiceTypeText.setLayoutData(gd42);
		
		ContainerPort = new StringFieldEditor( //
				MavenProjectPreferenceHelper.PREFS_MAVEN_DEFAULTS_KUBERNETES_CONTAINERPORT, //
				Messages.MavenDefaultsPreferencePage_Kubernetes_ContainerPort_label, //
				StringFieldEditor.UNLIMITED, //
				StringFieldEditor.VALIDATE_ON_KEY_STROKE, group) {
			@Override
			protected boolean doCheckState() {
				String value = ContainerPort.getStringValue();
				IStatus status = MavenProjectPreferenceHelper.INSTANCE.validateKubernetes_ContainerPort(value);
				if (status != null && !status.isOK()) {
					setErrorMessage(status.getMessage());
					return false;
				}
				return true;
			}
		};
		addField(ContainerPort);

		Label ContainerPortLabel = ContainerPort.getLabelControl(group);
		Text ContainerPortText = ContainerPort.getTextControl(group);

		GridData gd51 = new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1);
		gd51.widthHint = labelWidth;
		ContainerPortLabel.setLayoutData(gd51);
		GridData gd52 = new GridData(GridData.FILL_HORIZONTAL);
		gd52.grabExcessHorizontalSpace = true;
		gd52.horizontalSpan = 2;
		ContainerPortText.setLayoutData(gd52);
		
		K8SNamespace = new StringFieldEditor( //
				MavenProjectPreferenceHelper.PREFS_MAVEN_DEFAULTS_KUBERNETES_K8SNAMESPACE, //
				Messages.MavenDefaultsPreferencePage_Kubernetes_K8SNamespace_label, //
				StringFieldEditor.UNLIMITED, //
				StringFieldEditor.VALIDATE_ON_KEY_STROKE, group) {
			@Override
			protected boolean doCheckState() {
				String value = K8SNamespace.getStringValue();
				IStatus status = MavenProjectPreferenceHelper.INSTANCE.validateKubernetes_K8SNamespace(value);
				if (status != null && !status.isOK()) {
					setErrorMessage(status.getMessage());
					return false;
				}
				return true;
			}
		};
		addField(K8SNamespace);

		Label K8SNamespaceLabel = K8SNamespace.getLabelControl(group);
		Text K8SNamespaceText = K8SNamespace.getTextControl(group);

		GridData gd61 = new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1);
		gd61.widthHint = labelWidth;
		K8SNamespaceLabel.setLayoutData(gd61);
		GridData gd62 = new GridData(GridData.FILL_HORIZONTAL);
		gd62.grabExcessHorizontalSpace = true;
		gd62.horizontalSpan = 2;
		K8SNamespaceText.setLayoutData(gd62);
		
		Kubernetes_EnvVars = new StringFieldEditor( //
				MavenProjectPreferenceHelper.PREFS_MAVEN_DEFAULTS_KUBERNETES_ENVVARS, //
				Messages.MavenDefaultsPreferencePage_Kubernetes_EnvVars_label, //
				StringFieldEditor.UNLIMITED, //
				StringFieldEditor.VALIDATE_ON_KEY_STROKE, group) {
			@Override
			protected boolean doCheckState() {
				String value = Kubernetes_EnvVars.getStringValue();
				IStatus status = MavenProjectPreferenceHelper.INSTANCE.validateKubernetes_EnvVars(value);
				if (status != null && !status.isOK()) {
					setErrorMessage(status.getMessage());
					return false;
				}
				return true;
			}
		};
		addField(Kubernetes_EnvVars);

		Label Kubernetes_EnvVarsLabel = Kubernetes_EnvVars.getLabelControl(group);
		Text Kubernetes_EnvVarsText = Kubernetes_EnvVars.getTextControl(group);

		GridData gd71 = new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1);
		gd71.widthHint = labelWidth;
		Kubernetes_EnvVarsLabel.setLayoutData(gd71);
		GridData gd72 = new GridData(GridData.FILL_HORIZONTAL);
		gd72.grabExcessHorizontalSpace = true;
		gd72.horizontalSpan = 2;
		Kubernetes_EnvVarsText.setLayoutData(gd72);
		
		return group;
	}
	/**
	 * 
	 * @param composite
	 * @param numColumns
	 */
	protected void updateLayout(Composite composite, int numColumns) {
		GridLayout layout = new GridLayout();
		layout.numColumns = numColumns;
		layout.marginWidth = 5;
		layout.marginHeight = 5;
		layout.horizontalSpacing = 5;
		layout.verticalSpacing = 5;
		composite.setLayout(layout);
	}

	@Override
	protected void performDefaults() {
		getPreferenceStore().setDefault(MavenProjectPreferenceHelper.PREFS_MAVEN_DEFAULTS_GROUP_ID, MavenPropertiesFileDefaults.INSTANCE.getDefaultGroupID("com.tibco.bw"));
		getPreferenceStore().setDefault(MavenProjectPreferenceHelper.PREFS_MAVEN_DEFAULTS_PCF_TARGET, MavenPropertiesFileDefaults.INSTANCE.getDefaultPCF_Target("https://api.run.pivotal.io"));
		getPreferenceStore().setDefault(MavenProjectPreferenceHelper.PREFS_MAVEN_DEFAULTS_PCF_SERVERNAME, MavenPropertiesFileDefaults.INSTANCE.getDefaultPCF_ServerName("PCF_UK_credential"));
		getPreferenceStore().setDefault(MavenProjectPreferenceHelper.PREFS_MAVEN_DEFAULTS_PCF_ORG,MavenPropertiesFileDefaults.INSTANCE.getDefaultPCF_Org("tibco"));
		getPreferenceStore().setDefault(MavenProjectPreferenceHelper.PREFS_MAVEN_DEFAULTS_PCF_SPACE, MavenPropertiesFileDefaults.INSTANCE.getDefaultPCF_Space("development"));
		getPreferenceStore().setDefault(MavenProjectPreferenceHelper.PREFS_MAVEN_DEFAULTS_PCF_APPNAME, MavenPropertiesFileDefaults.INSTANCE.getDefaultPCF_AppName("AppName"));
		getPreferenceStore().setDefault(MavenProjectPreferenceHelper.PREFS_MAVEN_DEFAULTS_PCF_APPINSTANCES, MavenPropertiesFileDefaults.INSTANCE.getDefaultPCF_AppInstances("1"));
		getPreferenceStore().setDefault(MavenProjectPreferenceHelper.PREFS_MAVEN_DEFAULTS_PCF_APPMEMORY, MavenPropertiesFileDefaults.INSTANCE.getDefaultPCF_AppMemory("1024"));
		getPreferenceStore().setDefault(MavenProjectPreferenceHelper.PREFS_MAVEN_DEFAULTS_PCF_APPBUILDPACK,MavenPropertiesFileDefaults.INSTANCE.getDefaultPCF_AppBuildpack("buildpack"));
		getPreferenceStore().setDefault(MavenProjectPreferenceHelper.PREFS_MAVEN_DEFAULTS_PCF_ENVVARS, MavenPropertiesFileDefaults.INSTANCE.getDefaultPCF_EnvVars("APP_CONFIG_PROFILE=PCF, BW_LOGLEVEL=DEBUG"));
		getPreferenceStore().setDefault(MavenProjectPreferenceHelper.PREFS_MAVEN_DEFAULTS_PCF_USERNAME, MavenPropertiesFileDefaults.INSTANCE.getDefaultPCF_Username("admin"));
		getPreferenceStore().setDefault(MavenProjectPreferenceHelper.PREFS_MAVEN_DEFAULTS_DOCKER_URL,MavenPropertiesFileDefaults.INSTANCE.getDefaultDocker_URL("tcp://0.0.0.0:2376"));
		getPreferenceStore().setDefault(MavenProjectPreferenceHelper.PREFS_MAVEN_DEFAULTS_DOCKER_CERTPATH, MavenPropertiesFileDefaults.INSTANCE.getDefaultDocker_CertPath("</home/user/machine/>"));
		getPreferenceStore().setDefault(MavenProjectPreferenceHelper.PREFS_MAVEN_DEFAULTS_DOCKER_IMAGENAME,MavenPropertiesFileDefaults.INSTANCE.getDefaultDocker_ImageName("gcr.io/<project_id>/<image-name>"));
		getPreferenceStore().setDefault(MavenProjectPreferenceHelper.PREFS_MAVEN_DEFAULTS_DOCKER_BWCEIMAGE,MavenPropertiesFileDefaults.INSTANCE.getDefaultDocker_BWCEImage("tibco/bwce"));
		getPreferenceStore().setDefault(MavenProjectPreferenceHelper.PREFS_MAVEN_DEFAULTS_DOCKER_MAINTAINER,MavenPropertiesFileDefaults.INSTANCE.getDefaultDocker_Maintainer("abc@tibco.com"));
		getPreferenceStore().setDefault(MavenProjectPreferenceHelper.PREFS_MAVEN_DEFAULTS_DOCKER_APPNAME,  MavenPropertiesFileDefaults.INSTANCE.getDefaultDocker_AppName("BWCEAPP"));
		getPreferenceStore().setDefault(MavenProjectPreferenceHelper.PREFS_MAVEN_DEFAULTS_DOCKER_PORTS,  MavenPropertiesFileDefaults.INSTANCE.getDefaultDocker_Ports("18080:8080,17777:7777"));
		getPreferenceStore().setDefault(MavenProjectPreferenceHelper.PREFS_MAVEN_DEFAULTS_KUBERNETES_DEPLOYMENTNAME, MavenPropertiesFileDefaults.INSTANCE.getDefaultKubernetes_DeploymentName("bwce-sample"));
		getPreferenceStore().setDefault(MavenProjectPreferenceHelper.PREFS_MAVEN_DEFAULTS_KUBERNETES_NOOFREPLICAS,MavenPropertiesFileDefaults.INSTANCE.getDefaultKubernetes_NoOfReplicas("1"));
		getPreferenceStore().setDefault(MavenProjectPreferenceHelper.PREFS_MAVEN_DEFAULTS_KUBERNETES_SERVICENAME,MavenPropertiesFileDefaults.INSTANCE.getDefaultKubernetes_ServiceName("bwce-sample-service"));
		getPreferenceStore().setDefault(MavenProjectPreferenceHelper.PREFS_MAVEN_DEFAULTS_KUBERNETES_SERVICETYPE,  MavenPropertiesFileDefaults.INSTANCE.getDefaultKubernetes_ServiceType("LoadBalancer"));
		getPreferenceStore().setDefault(MavenProjectPreferenceHelper.PREFS_MAVEN_DEFAULTS_KUBERNETES_CONTAINERPORT,MavenPropertiesFileDefaults.INSTANCE.getDefaultKubernetes_ContainerPort("8080"));
		getPreferenceStore().setDefault(MavenProjectPreferenceHelper.PREFS_MAVEN_DEFAULTS_KUBERNETES_K8SNAMESPACE,MavenPropertiesFileDefaults.INSTANCE.getDefaultKubernetes_K8SNamespace("default"));
		getPreferenceStore().setDefault(MavenProjectPreferenceHelper.PREFS_MAVEN_DEFAULTS_KUBERNETES_ENVVARS, MavenPropertiesFileDefaults.INSTANCE.getDefaultKubernetes_EnvVars("APP_CONFIG_PROFILE=docker, abc=xyz"));
		super.performDefaults();
	}
	@Override
	protected void performApply() {
		MavenProjectPreferenceHelper.INSTANCE.setDefaultGroupID(groupID.getStringValue());
		MavenProjectPreferenceHelper.INSTANCE.setDefaultPCF_Target(Target.getStringValue());
		MavenProjectPreferenceHelper.INSTANCE.setDefaultPCF_ServerName(ServerName.getStringValue());
		MavenProjectPreferenceHelper.INSTANCE.setDefaultPCF_Org(Org.getStringValue());
		MavenProjectPreferenceHelper.INSTANCE.setDefaultPCF_Space(Space.getStringValue());
		MavenProjectPreferenceHelper.INSTANCE.setDefaultPCF_AppName(PCF_AppName.getStringValue());
		MavenProjectPreferenceHelper.INSTANCE.setDefaultPCF_AppInstances(AppInstances.getStringValue());
		MavenProjectPreferenceHelper.INSTANCE.setDefaultPCF_AppMemory(AppMemory.getStringValue());
		MavenProjectPreferenceHelper.INSTANCE.setDefaultPCF_AppBuildpack(AppBuildpack.getStringValue());
		MavenProjectPreferenceHelper.INSTANCE.setDefaultPCF_EnvVars(PCF_EnvVars.getStringValue());
		MavenProjectPreferenceHelper.INSTANCE.setDefaultPCF_Username(Username.getStringValue());
		MavenProjectPreferenceHelper.INSTANCE.setDefaultDocker_URL(dockerURL.getStringValue());
		MavenProjectPreferenceHelper.INSTANCE.setDefaultDocker_CertPath(CertPath.getStringValue());
		MavenProjectPreferenceHelper.INSTANCE.setDefaultDocker_ImageName(ImageName.getStringValue());
		MavenProjectPreferenceHelper.INSTANCE.setDefaultDocker_BWCEImage(BWCEImage.getStringValue());
		MavenProjectPreferenceHelper.INSTANCE.setDefaultDocker_Maintainer(Maintainer.getStringValue());
		MavenProjectPreferenceHelper.INSTANCE.setDefaultDocker_AppName(Docker_AppName.getStringValue());
		MavenProjectPreferenceHelper.INSTANCE.setDefaultDocker_Ports(Ports.getStringValue());
		MavenProjectPreferenceHelper.INSTANCE.setDefaultKubernetes_DeploymentName(DeploymentName.getStringValue());
		MavenProjectPreferenceHelper.INSTANCE.setDefaultKubernetes_NoOfReplicas(NoOfReplicas.getStringValue());
		MavenProjectPreferenceHelper.INSTANCE.setDefaultKubernetes_ServiceName(ServiceName.getStringValue());
		MavenProjectPreferenceHelper.INSTANCE.setDefaultKubernetes_ServiceType(ServiceType.getStringValue());
		MavenProjectPreferenceHelper.INSTANCE.setDefaultKubernetes_ContainerPort(ContainerPort.getStringValue());
		MavenProjectPreferenceHelper.INSTANCE.setDefaultKubernetes_K8SNamespace(K8SNamespace.getStringValue());
		MavenProjectPreferenceHelper.INSTANCE.setDefaultKubernetes_EnvVars(Kubernetes_EnvVars.getStringValue());
	    super.performApply();
	}
}

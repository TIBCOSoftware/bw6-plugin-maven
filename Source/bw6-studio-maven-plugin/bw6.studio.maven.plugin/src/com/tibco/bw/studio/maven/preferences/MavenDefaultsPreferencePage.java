/*
 * Copyright© 2011 - 2013 TIBCO Software Inc. 
 * All rights reserved. 
 * 
 * This software is confidential and proprietary information of TIBCO Software Inc.
 */
package com.tibco.bw.studio.maven.preferences;

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


public class MavenDefaultsPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {


	public static final String ID = "tibco.preference.bw.MavenDefaults"; // $NON-NLS-1$

	protected StringFieldEditor groupID;
	protected StringFieldEditor dockerURL;

	public MavenDefaultsPreferencePage() {
		super(GRID);
		setDescription(Messages.MavenDefaultsPreferencePage_description_label);
	}

	@Override
	protected IPreferenceStore doGetPreferenceStore() {
		return Activator.getDefault().getPreferenceStore();
	}

	@Override
	public void init(IWorkbench workbench) {
		if (!getPreferenceStore().contains(MavenProjectPreferenceHelper.PREFS_MAVEN_DEFAULTS_GROUP_ID)) {
			getPreferenceStore().setDefault(MavenProjectPreferenceHelper.PREFS_MAVEN_DEFAULTS_GROUP_ID, Messages.MavenDefaultsPreferencePage_defaultgroupID);
		}
		if (!getPreferenceStore().contains(MavenProjectPreferenceHelper.PREFS_MAVEN_DEFAULTS_DOCKER_URL)) {
			getPreferenceStore().setDefault(MavenProjectPreferenceHelper.PREFS_MAVEN_DEFAULTS_DOCKER_URL, Messages.MavenDefaultsPreferencePage_defaultdockerURL);
		}
	}

	@Override
	protected void createFieldEditors() {
		Composite parent = getFieldEditorParent();

		Group projectWizardGroup = createProjectWizardGroup(parent);

		GridData gd1 = new GridData(GridData.FILL_HORIZONTAL);
		projectWizardGroup.setLayoutData(gd1);

		updateLayout(projectWizardGroup, 3);
		getFieldEditorParent().layout(true);
	}

	@Override
	protected void adjustGridLayout() {
		// do not remove this empty method.
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



		dockerURL = new StringFieldEditor( //
				MavenProjectPreferenceHelper.PREFS_MAVEN_DEFAULTS_DOCKER_URL, //
				Messages.MavenDefaultsPreferencePage_dockerURL_label, //
				StringFieldEditor.UNLIMITED, //
				StringFieldEditor.VALIDATE_ON_KEY_STROKE, group) {
			@Override
			protected boolean doCheckState() {
				String value = dockerURL.getStringValue();
				IStatus status = MavenProjectPreferenceHelper.INSTANCE.validatedockerURL(value);
				if (status != null && !status.isOK()) {
					setErrorMessage(status.getMessage());
					return false;
				}
				return true;
			}
		};
		addField(dockerURL);

		Label organizationNameLabel = groupID.getLabelControl(group);
		Text organizationNameText = groupID.getTextControl(group);


		Label versionLabel = dockerURL.getLabelControl(group);
		Text versionText = dockerURL.getTextControl(group);


		int labelWidth = 110;

		GridData gd11 = new GridData();
		gd11.widthHint = labelWidth;
		organizationNameLabel.setLayoutData(gd11);
		GridData gd12 = new GridData(GridData.FILL_HORIZONTAL);
		gd12.horizontalSpan = 2;
		gd12.grabExcessHorizontalSpace = true;
		organizationNameText.setLayoutData(gd12);


		GridData gd31 = new GridData();
		gd31.widthHint = labelWidth;
		versionLabel.setLayoutData(gd31);
		GridData gd32 = new GridData(GridData.FILL_HORIZONTAL);
		gd32.grabExcessHorizontalSpace = true;
		gd32.horizontalSpan = 2;
		versionText.setLayoutData(gd32);
		
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
		getPreferenceStore().setDefault(MavenProjectPreferenceHelper.PREFS_MAVEN_DEFAULTS_GROUP_ID, Messages.MavenDefaultsPreferencePage_defaultgroupID);
		getPreferenceStore().setDefault(MavenProjectPreferenceHelper.PREFS_MAVEN_DEFAULTS_DOCKER_URL, Messages.MavenDefaultsPreferencePage_defaultdockerURL);
		super.performDefaults();
	}
	@Override
	protected void performApply() {
		MavenProjectPreferenceHelper.INSTANCE.setDefaultGroupID(groupID.getStringValue());
		MavenProjectPreferenceHelper.INSTANCE.setDefaultDockerURL(dockerURL.getStringValue());
		super.performApply();
	}
}

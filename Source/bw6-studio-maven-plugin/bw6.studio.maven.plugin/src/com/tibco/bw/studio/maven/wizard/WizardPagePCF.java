package com.tibco.bw.studio.maven.wizard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.tibco.bw.studio.maven.modules.model.BWModule;
import com.tibco.bw.studio.maven.modules.model.BWModuleType;
import com.tibco.bw.studio.maven.modules.model.BWPCFModule;
import com.tibco.bw.studio.maven.modules.model.BWProject;
import com.tibco.bw.studio.maven.preferences.MavenDefaultsPreferencePage;
import com.tibco.bw.studio.maven.preferences.MavenProjectPreferenceHelper;
import com.tibco.bw.studio.maven.preferences.MavenPropertiesFileDefaults;

public class WizardPagePCF extends WizardPage {
	private Composite container;
	private BWProject project;

	private Text appPCFTarget;
	private Text appPCFCred;
	private Text appPCFOrg;
	private Text appPCFSpace;
	private Text appPCFAppName;
	private Text appPCFInstances;
	private Text appPCFMemory;
	private Text appPCFDiskQuota;
	private Text appPCFBuildpack;
	private Text cfEnvVars;


	protected WizardPagePCF(String pageName, BWProject project) {
		super(pageName);
		this.project = project;
		setTitle("CloudFoundry Plugin for Apache Maven and TIBCO BusinessWorks Container Edition");
		setDescription("Enter CloudFoundry Platform details for pushing and running BWCE apps");
	}

	@Override
	public void createControl(Composite parent) {
		container = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 4;
		setApplicationPCFPOMFields();
		setControl(container);
		setPageComplete(true);
	}

	private void setApplicationPCFPOMFields() {
		Label targetLabel = new Label(container, SWT.NONE);
		targetLabel.setText("PCF Target");

		appPCFTarget = new Text(container, SWT.BORDER | SWT.SINGLE);
		appPCFTarget.setText(MavenProjectPreferenceHelper.INSTANCE.getDefaultPCF_Target(MavenPropertiesFileDefaults.INSTANCE.getDefaultPCF_Target("https://api.run.pivotal.io")));
		GridData targetData = new GridData(150, 15);
		appPCFTarget.setLayoutData(targetData);

		Label credLabel = new Label(container, SWT.RIGHT);
		credLabel.setText("PCF Server Name");

		appPCFCred = new Text(container, SWT.BORDER | SWT.SINGLE);
		appPCFCred.setText(MavenProjectPreferenceHelper.INSTANCE.getDefaultPCF_ServerName(MavenPropertiesFileDefaults.INSTANCE.getDefaultPCF_ServerName("PCF_UK_credential")));
		GridData credData = new GridData(100, 15);
		appPCFCred.setLayoutData(credData);

		Label orgLabel = new Label(container, SWT.RIGHT);
		orgLabel.setText("PCF Org");

		appPCFOrg = new Text(container, SWT.BORDER | SWT.SINGLE);
		appPCFOrg.setText(MavenProjectPreferenceHelper.INSTANCE.getDefaultPCF_Org(MavenPropertiesFileDefaults.INSTANCE.getDefaultPCF_Org("tibco")));
		GridData orgData = new GridData(100, 15);
		appPCFOrg.setLayoutData(orgData);

		Label spaceLabel = new Label(container, SWT.RIGHT);
		spaceLabel.setText("PCF Space");

		appPCFSpace = new Text(container, SWT.BORDER | SWT.SINGLE);
		appPCFSpace.setText(MavenProjectPreferenceHelper.INSTANCE.getDefaultPCF_Space(MavenPropertiesFileDefaults.INSTANCE.getDefaultPCF_Space("development")));
		GridData spaceData = new GridData(100, 15);
		appPCFSpace.setLayoutData(spaceData);

		Label appNameLabel = new Label(container, SWT.RIGHT);
		appNameLabel.setText("App Name");

		appPCFAppName = new Text(container, SWT.BORDER | SWT.SINGLE);
		appPCFAppName.setText(MavenProjectPreferenceHelper.INSTANCE.getDefaultPCF_AppName(MavenPropertiesFileDefaults.INSTANCE.getDefaultPCF_AppName("AppName")));
		GridData appNameData = new GridData(100, 15);
		appPCFAppName.setLayoutData(appNameData);

		Label instancesLabel = new Label(container, SWT.RIGHT);
		instancesLabel.setText("App Instances");

		appPCFInstances = new Text(container, SWT.BORDER | SWT.SINGLE);
		appPCFInstances.setText(MavenProjectPreferenceHelper.INSTANCE.getDefaultPCF_AppInstances(MavenPropertiesFileDefaults.INSTANCE.getDefaultPCF_AppInstances("1")));
		GridData instancesData = new GridData(50, 15);
		appPCFInstances.setLayoutData(instancesData);

		Label memoryLabel = new Label(container, SWT.RIGHT);
		memoryLabel.setText("App Memory");

		appPCFMemory = new Text(container, SWT.BORDER | SWT.SINGLE);
		appPCFMemory.setText(MavenProjectPreferenceHelper.INSTANCE.getDefaultPCF_AppMemory(MavenPropertiesFileDefaults.INSTANCE.getDefaultPCF_AppMemory("1024")));
		GridData memoryData = new GridData(50, 15);
		appPCFMemory.setLayoutData(memoryData);
		
		
		Label diskLabel = new Label(container, SWT.RIGHT);
		diskLabel.setText("App Disk Quota");
		
		
		appPCFDiskQuota = new Text(container, SWT.BORDER | SWT.SINGLE);
		appPCFDiskQuota.setText("1024");
		GridData diskData = new GridData(50, 15);
		appPCFDiskQuota.setLayoutData(diskData);

		Label buildpackLabel = new Label(container, SWT.RIGHT);
		buildpackLabel.setText("App Buildpack");

		appPCFBuildpack = new Text(container, SWT.BORDER | SWT.SINGLE);
		appPCFBuildpack.setText(MavenProjectPreferenceHelper.INSTANCE.getDefaultPCF_AppBuildpack(MavenPropertiesFileDefaults.INSTANCE.getDefaultPCF_AppBuildpack("buildpack")));
		GridData buildpackData = new GridData(200, 15);
		appPCFBuildpack.setLayoutData(buildpackData);

		Label envVarsLabel = new Label(container, SWT.NONE);
		envVarsLabel.setText("Env Vars");

		cfEnvVars = new Text(container, SWT.BORDER | SWT.SINGLE);
		cfEnvVars.setText(MavenProjectPreferenceHelper.INSTANCE.getDefaultPCF_EnvVars(MavenPropertiesFileDefaults.INSTANCE.getDefaultPCF_EnvVars("APP_CONFIG_PROFILE=PCF, BW_LOGLEVEL=DEBUG")));
		GridData envvarData = new GridData(400, 15);
		envvarData.horizontalSpan = 3;
		cfEnvVars.setLayoutData(envvarData);

		Label pcfServicesLabel = new Label(container, SWT.RIGHT);
		pcfServicesLabel.setText("PCF Services");

		// Add a Button to select PCF services
		final Button servicesButton = new Button(container, SWT.PUSH | SWT.BORDER);
		servicesButton.setText("Select Services");
		servicesButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// Set BWCFModule with above provided values
				for (BWModule module : project.getModules()) {
					if (module.getType() == BWModuleType.Application) {
						module.setBwpcfModule(setBWPCFValues(module));
						break;
					}
				}

				// call Services Wizard
				PCFServiceWizard serviceWizard = new PCFServiceWizard(project);
				WizardDialog dialog = new WizardDialog(container.getShell(), serviceWizard);
				if (dialog.open() == Window.OK) {
					project = serviceWizard.getProject();
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
	}

	private BWPCFModule setBWPCFValues(BWModule module) {
		// BWPCFModule will not be null if its set through Services Wizard
		BWPCFModule bwpcf = module.getBwpcfModule();
		if (bwpcf == null) {
			bwpcf = new BWPCFModule();
		}

		bwpcf.setTarget(appPCFTarget.getText());
		bwpcf.setCredString(appPCFCred.getText());
		bwpcf.setOrg(appPCFOrg.getText());
		bwpcf.setSpace(appPCFSpace.getText());
		bwpcf.setAppName(appPCFAppName.getText());
		bwpcf.setInstances(appPCFInstances.getText());
		bwpcf.setMemory(appPCFMemory.getText());
		bwpcf.setDiskQuota(appPCFDiskQuota.getText());
		bwpcf.setBuildpack(appPCFBuildpack.getText());

		List<String> envvars = new ArrayList<String>();
		if (cfEnvVars.getText() != null && !cfEnvVars.getText().isEmpty()) {
			envvars = Arrays.asList(cfEnvVars.getText().split("\\s*,\\s*"));
		}

		Map<String, String> envMap = new HashMap<String, String>();
		for (String env : envvars) {
			String[] keyval = env.split("=");
			if (keyval[0] != null && keyval[1] != null) {
				envMap.put(keyval[0].trim(), keyval[1].trim());
			}
		}
		bwpcf.setCfEnvVariables(envMap);
		return bwpcf;
	}

	public BWProject getUpdatedProject() {
		for (BWModule module : project.getModules()) {
			if (module.getType() == BWModuleType.Application) {
				module.setBwpcfModule(setBWPCFValues(module));
			}
		}
		return project;
	}
	@Override
	public boolean canFlipToNextPage() 
	{
		return false;
	}
}

package com.tibco.bw.studio.maven.wizard;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

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
import org.eclipse.swt.widgets.Group;
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
	private Group container;
	private BWProject project;

	private Text appPCFTarget;
	private Text appPCFCred;
	private Text appPCFOrg;
	private Text appPCFSpace;
	private Text appPCFAppName;
	private Text appPCFDomain;
	private Text appPCFInstances;
	private Text appPCFMemory;
	private Text appPCFDiskQuota;
	private Text appPCFBuildpack;
	private Text cfEnvVars;
	private Map<String, String> properties= new HashMap();
	private FileInputStream devPropfile = null;
	private StringBuilder envStr = new StringBuilder();
	private int textHeight = 18;


	protected WizardPagePCF(String pageName, BWProject project) {
		super(pageName);
		this.project = project;
		setTitle("CloudFoundry Configuration for TIBCO BusinessWorks Container Edition Application");
		setDescription("Enter CloudFoundry Platform details for pushing and running BWCE apps");
	}

	@Override
	public void createControl(Composite parent) {
		//container = new Composite(parent, SWT.NONE);
		container = new Group(parent, SWT.NONE);
		container.setText("PCF Configuration : ");
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 4;
		container.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		setApplicationPCFPOMFields();
		setControl(container);
		setPageComplete(true);
	}
	
	private String getWorkspacepath() {
		for (BWModule module : project.getModules()) {
			if (module.getType() == BWModuleType.Application) {
				String pomloc = module.getPomfileLocation().toString();
				String workspace = pomloc.substring(0,pomloc.indexOf("pom.xml"));
				return workspace;
			}
		}
		return null;
	}

	private void setApplicationPCFPOMFields() {
		File devfile = new File(getWorkspacepath() + File.separator
				+ "pcfdev.properties");
		if (devfile.exists()) {
			try {
				devPropfile = new FileInputStream(devfile);
				Properties props = new Properties();
				props.load(devPropfile);
				devPropfile.close();
				Enumeration enuKeys = props.keys();

				while (enuKeys.hasMoreElements()) {
					String key = (String) enuKeys.nextElement();
					String value = props.getProperty(key);
					properties.put(key, value);
					String s = "bwpcf.env.";
					if (key.startsWith(s)) {
						envStr.append(key.substring(s.length()));
						envStr.append("=");
						envStr.append(value);
						envStr.append(",");
					}
				}
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			} finally {
				try {
					devPropfile.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}

		Label targetLabel = new Label(container, SWT.NONE);
		targetLabel.setText("PCF Target");

		appPCFTarget = new Text(container, SWT.BORDER | SWT.SINGLE);
		if(properties.containsKey("bwpcf.target"))
			appPCFTarget.setText(properties.get("bwpcf.target"));
		else	
			appPCFTarget.setText(MavenProjectPreferenceHelper.INSTANCE.getDefaultPCF_Target(MavenPropertiesFileDefaults.INSTANCE.getDefaultPCF_Target("https://api.run.pivotal.io")));
		GridData targetData = new GridData(300, textHeight);
		appPCFTarget.setLayoutData(targetData);

		Label credLabel = new Label(container, SWT.RIGHT);
		credLabel.setText("PCF Server Name");

		appPCFCred = new Text(container, SWT.BORDER | SWT.SINGLE);
		if(properties.containsKey("bwpcf.server"))
			appPCFCred.setText(properties.get("bwpcf.server"));
		else
			appPCFCred.setText(MavenProjectPreferenceHelper.INSTANCE.getDefaultPCF_ServerName(MavenPropertiesFileDefaults.INSTANCE.getDefaultPCF_ServerName("PCF_UK_credential")));
		GridData credData = new GridData(300, textHeight);
		appPCFCred.setLayoutData(credData);

		Label orgLabel = new Label(container, SWT.RIGHT);
		orgLabel.setText("PCF Org");

		appPCFOrg = new Text(container, SWT.BORDER | SWT.SINGLE);
		if(properties.containsKey("bwpcf.org"))
			appPCFOrg.setText(properties.get("bwpcf.org"));
		else
			appPCFOrg.setText(MavenProjectPreferenceHelper.INSTANCE.getDefaultPCF_Org(MavenPropertiesFileDefaults.INSTANCE.getDefaultPCF_Org("tibco")));
		GridData orgData = new GridData(300, textHeight);
		appPCFOrg.setLayoutData(orgData);

		Label spaceLabel = new Label(container, SWT.RIGHT);
		spaceLabel.setText("PCF Space");

		appPCFSpace = new Text(container, SWT.BORDER | SWT.SINGLE);
		if(properties.containsKey("bwpcf.space"))
			appPCFSpace.setText(properties.get("bwpcf.space"));
		else
			appPCFSpace.setText(MavenProjectPreferenceHelper.INSTANCE.getDefaultPCF_Space(MavenPropertiesFileDefaults.INSTANCE.getDefaultPCF_Space("development")));
		GridData spaceData = new GridData(300, textHeight);
		appPCFSpace.setLayoutData(spaceData);

		Label appNameLabel = new Label(container, SWT.RIGHT);
		appNameLabel.setText("App Name");

		appPCFAppName = new Text(container, SWT.BORDER | SWT.SINGLE);
		if(properties.containsKey("bwpcf.appName"))
			appPCFAppName.setText(properties.get("bwpcf.appName"));
		else
			appPCFAppName.setText(MavenProjectPreferenceHelper.INSTANCE.getDefaultPCF_AppName(MavenPropertiesFileDefaults.INSTANCE.getDefaultPCF_AppName("AppName")));
		GridData appNameData = new GridData(300, textHeight);
		appPCFAppName.setLayoutData(appNameData);
		
		Label pcfDomainLabel = new Label(container, SWT.RIGHT);
		pcfDomainLabel.setText("PCF Domain");
		
		appPCFDomain = new Text(container, SWT.BORDER | SWT.SINGLE);
		
		GridData appDomainData = new GridData(300, textHeight);
		appPCFDomain.setLayoutData(appDomainData);

		Label instancesLabel = new Label(container, SWT.RIGHT);
		instancesLabel.setText("App Instances");

		appPCFInstances = new Text(container, SWT.BORDER | SWT.SINGLE);
		if(properties.containsKey("bwpcf.instances"))
			appPCFInstances.setText(properties.get("bwpcf.instances"));
		else
			appPCFInstances.setText(MavenProjectPreferenceHelper.INSTANCE.getDefaultPCF_AppInstances(MavenPropertiesFileDefaults.INSTANCE.getDefaultPCF_AppInstances("1")));
		GridData instancesData = new GridData(50, textHeight);
		appPCFInstances.setLayoutData(instancesData);

		Label memoryLabel = new Label(container, SWT.RIGHT);
		memoryLabel.setText("App Memory");

		appPCFMemory = new Text(container, SWT.BORDER | SWT.SINGLE);
		if(properties.containsKey("bwpcf.memory"))
			appPCFMemory.setText(properties.get("bwpcf.memory"));
		else
			appPCFMemory.setText(MavenProjectPreferenceHelper.INSTANCE.getDefaultPCF_AppMemory(MavenPropertiesFileDefaults.INSTANCE.getDefaultPCF_AppMemory("1024")));
		GridData memoryData = new GridData(80, textHeight);
		appPCFMemory.setLayoutData(memoryData);
		
		Label diskLabel = new Label(container, SWT.RIGHT);
		diskLabel.setText("App Disk Quota");
		
		appPCFDiskQuota = new Text(container, SWT.BORDER | SWT.SINGLE);
		if(properties.containsKey("bwpcf.diskQuota"))
			appPCFDiskQuota.setText(properties.get("bwpcf.diskQuota"));
		else
			appPCFDiskQuota.setText("1024");
		GridData diskData = new GridData(80, textHeight);
		appPCFDiskQuota.setLayoutData(diskData);

		Label buildpackLabel = new Label(container, SWT.RIGHT);
		buildpackLabel.setText("App Buildpack");

		appPCFBuildpack = new Text(container, SWT.BORDER | SWT.SINGLE);
		if(properties.containsKey("bwpcf.buildpack"))
			appPCFBuildpack.setText(properties.get("bwpcf.buildpack"));
		else
			appPCFBuildpack.setText(MavenProjectPreferenceHelper.INSTANCE.getDefaultPCF_AppBuildpack(MavenPropertiesFileDefaults.INSTANCE.getDefaultPCF_AppBuildpack("buildpack")));
		GridData buildpackData = new GridData(300, textHeight);
		appPCFBuildpack.setLayoutData(buildpackData);

		Label envVarsLabel = new Label(container, SWT.NONE);
		envVarsLabel.setText("Env Vars");

		cfEnvVars = new Text(container, SWT.BORDER | SWT.SINGLE);
		if(devfile.exists()){
			if (envStr.length() != 0)
				cfEnvVars.setText(envStr.substring(0, envStr.length() - 1));
		}
		else
			cfEnvVars.setText(MavenProjectPreferenceHelper.INSTANCE.getDefaultPCF_EnvVars(MavenPropertiesFileDefaults.INSTANCE.getDefaultPCF_EnvVars("APP_CONFIG_PROFILE=PCF, BW_LOGLEVEL=DEBUG")));
		GridData envvarData = new GridData(400, textHeight);
		//envvarData.horizontalSpan = 3;
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
		bwpcf.setPCFDomain(appPCFDomain.getText());
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

package com.tibco.bw.studio.maven.wizard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.tibco.bw.studio.maven.modules.model.BWDockerModule;
import com.tibco.bw.studio.maven.modules.model.BWModule;
import com.tibco.bw.studio.maven.modules.model.BWModuleType;
import com.tibco.bw.studio.maven.modules.model.BWProject;
import com.tibco.bw.studio.maven.preferences.MavenDefaultsPreferencePage;
import com.tibco.bw.studio.maven.preferences.MavenProjectPreferenceHelper;
import com.tibco.bw.studio.maven.preferences.MavenPropertiesFileDefaults;

public class WizardPageDocker extends WizardPage {
	private Composite container;
	private BWProject project;
	private Text dockerHost;
	private Text dockerHostCertPath;
	private Text dockerImageName;
	private Text dockerImageFrom;
	private Button autoPullImage;
	private boolean isAutoPull;
	private Text dockerImageMaintainer;
	private Text dockerAppName;
	private Text dockerVolume;
	private Text dockerLink;
	private Text dockerPort;
	private Text dockerEnv;
	private WizardPageK8S k8sPage;
	// private static int numDockerElements=0;//24;

	private Text platform;

	protected WizardPageDocker(String pageName, BWProject project) {
		super(pageName);
		this.project = project;
		setTitle("Docker Plugin for Apache Maven and TIBCO BusinessWorks Container Edition");
		setDescription("Enter Docker and Platform details for pushing and running docker image.");
	}

	@Override
	public void createControl(Composite parent) {
		container = new Composite(parent, SWT.NONE);

		GridLayout layout = new GridLayout();
		layout.numColumns = 4;
		container.setLayout(layout);
		setApplicationDockerBuildPOMFields();
		addSeperator(parent);
		setApplicationDockerRunPOMFields();
		addSeperator(parent);
		selectDockerDeploymentPlatforms();
		// addSeperator(parent);
		setControl(container);
		setPageComplete(true);
	}

	private void selectDockerDeploymentPlatforms() {
		Label lLabel = new Label(container, SWT.NONE);
		lLabel.setText("Select platform where you want to deploy your docker image:");
		GridData lData = new GridData(400, 15);
		lData.horizontalSpan = 4;
		lLabel.setLayoutData(lData);

		platform = new Text(container, SWT.NONE);
		platform.setVisible(false);
		platform.setText("");

		Composite innerContainer = new Composite(container, SWT.NONE);

		GridLayout layout = new GridLayout();
		innerContainer.setLayout(layout);
		layout.numColumns = 2;

		final Button k8s = new Button(innerContainer, SWT.CHECK);
		k8s.setSelection(false);

		k8s.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (k8s.getSelection()) {
					platform.setText("K8S");
					container.layout();
					MavenWizardContext.INSTANCE.getNextButton().setEnabled(true);
				} else {
					MavenWizardContext.INSTANCE.getNextButton().setEnabled(false);
					platform.setText("");
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		Label k8slabel = new Label(innerContainer, SWT.NONE);
		k8slabel.setText("Kubernetes/Openshift");
	}

	@Override
	public IWizardPage getNextPage() {
		if (platform.getText().equals("K8S")) {
			k8sPage = new WizardPageK8S("Kubernetes configuration", project);
			k8sPage.setWizard(getWizard());
			return k8sPage;
		}
		IWizardPage page = super.getNextPage();
		return page;
	}

	private void setApplicationDockerBuildPOMFields() {
		Label lLabel = new Label(container, SWT.NONE);
		lLabel.setText("Docker host build configuration:");
		GridData lData = new GridData(300, 15);
		lData.horizontalSpan = 4;
		lLabel.setLayoutData(lData);

		Label l1Label = new Label(container, SWT.NONE);
		l1Label.setText("");
		GridData l1Data = new GridData(300, 15);
		l1Data.horizontalSpan = 4;
		l1Label.setLayoutData(l1Data);

		Label targetLabel = new Label(container, SWT.NONE);
		targetLabel.setText("Docker Host");

		dockerHost = new Text(container, SWT.BORDER | SWT.SINGLE);
		dockerHost.setText(MavenProjectPreferenceHelper.INSTANCE.getDefaultDocker_URL(MavenPropertiesFileDefaults.INSTANCE.getDefaultDocker_URL("tcp://0.0.0.0:2376")));
		GridData dockerHostData = new GridData(200, 15);
		dockerHost.setLayoutData(dockerHostData);

		Label certLabel = new Label(container, SWT.NONE);
		certLabel.setText("Cert Path");

		dockerHostCertPath = new Text(container, SWT.BORDER | SWT.SINGLE);
		dockerHostCertPath.setText(MavenProjectPreferenceHelper.INSTANCE.getDefaultDocker_CertPath(MavenPropertiesFileDefaults.INSTANCE.getDefaultDocker_CertPath("</home/user/machine/>")));
		GridData dockerHostCertData = new GridData(200, 15);
		dockerHostCertPath.setLayoutData(dockerHostCertData);

		Label imgNameLabel = new Label(container, SWT.NONE);
		imgNameLabel.setText("Image Name");

		dockerImageName = new Text(container, SWT.BORDER | SWT.SINGLE);
		dockerImageName.setText(MavenProjectPreferenceHelper.INSTANCE.getDefaultDocker_ImageName(MavenPropertiesFileDefaults.INSTANCE.getDefaultDocker_ImageName("gcr.io/<project_id>/<image-name>")));
		GridData dockerImgNameData = new GridData(200, 15);
		dockerImageName.setLayoutData(dockerImgNameData);

		Label imgFromLabel = new Label(container, SWT.NONE);
		imgFromLabel.setText("BWCE Image");

		dockerImageFrom = new Text(container, SWT.BORDER | SWT.SINGLE);
		dockerImageFrom.setText(MavenProjectPreferenceHelper.INSTANCE.getDefaultDocker_BWCEImage(MavenPropertiesFileDefaults.INSTANCE.getDefaultDocker_BWCEImage("tibco/bwce")));
		GridData imgFromData = new GridData(100, 15);
		dockerImageFrom.setLayoutData(imgFromData);
		
		Label autoPullLabel = new Label(container, SWT.NONE);
		autoPullLabel.setText("Auto Pull Base Image");
		GridData autoPullData = new GridData(50, 15);
		autoPullImage= new Button(container, SWT.CHECK);
		autoPullImage.setSelection(false);
		autoPullImage.setLayoutData(autoPullData);
		autoPullImage.addSelectionListener(new SelectionListener(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				if(autoPullImage.getSelection()){
					isAutoPull= true;
				}
				else{
					isAutoPull= false;
				}
				
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				
				
			}
			
		});

		Label maintainerLabel = new Label(container, SWT.NONE);
		maintainerLabel.setText("Maintainer");

		dockerImageMaintainer = new Text(container, SWT.BORDER | SWT.SINGLE);
		dockerImageMaintainer.setText(MavenProjectPreferenceHelper.INSTANCE.getDefaultDocker_Maintainer(MavenPropertiesFileDefaults.INSTANCE.getDefaultDocker_Maintainer("abc@tibco.com")));
		GridData maintainerData = new GridData(200, 15);

		dockerImageMaintainer.setLayoutData(maintainerData);
		
		
		Composite innerContainer = new Composite(container, SWT.NONE);

		GridLayout layout = new GridLayout();
		innerContainer.setLayout(layout);
		layout.numColumns = 2;
		final Button dkr = new Button(innerContainer, SWT.CHECK);
		
		dkr.setSelection(false);

		dkr.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (dkr.getSelection()) {
					dockerAppName.setEditable(true);
					dockerAppName.setText(MavenProjectPreferenceHelper.INSTANCE.getDefaultDocker_AppName(MavenPropertiesFileDefaults.INSTANCE.getDefaultDocker_AppName("BWCEAPP")));
					dockerVolume.setEditable(true);
					dockerLink.setEditable(true);
					dockerEnv.setEditable(true);
					dockerPort.setEditable(true);
					dockerPort.setText(MavenProjectPreferenceHelper.INSTANCE.getDefaultDocker_Ports(MavenPropertiesFileDefaults.INSTANCE.getDefaultDocker_Ports("18080:8080,17777:7777")));
					container.layout();
				} else {
					dockerAppName.setEditable(false);
					dockerAppName.setText("");
					dockerVolume.setEditable(false);
					dockerVolume.setText("");
					dockerLink.setEditable(false);
					dockerLink.setText("");
					dockerEnv.setEditable(false);
					dockerEnv.setText("");
					dockerPort.setEditable(false);
					dockerPort.setText("");
					container.layout();
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		
		
		Label dkrlabel = new Label(innerContainer, SWT.NONE);
		dkrlabel.setText("Run on docker host");
		
		// createContents(container);
	}

	private void setApplicationDockerRunPOMFields() {
		Label lLabel = new Label(container, SWT.NONE);
		lLabel.setText("Docker host run configuration:");
		GridData lData = new GridData(300, 15);
		lData.horizontalSpan = 4;
		lLabel.setLayoutData(lData);

		Label l1Label = new Label(container, SWT.NONE);
		l1Label.setText("");
		GridData l1Data = new GridData(300, 15);
		l1Data.horizontalSpan = 4;
		l1Label.setLayoutData(l1Data);

		Label appNameLabel = new Label(container, SWT.NONE);
		appNameLabel.setText("App Name");

		dockerAppName = new Text(container, SWT.BORDER | SWT.SINGLE | SWT.READ_ONLY);
		dockerAppName.setText("");
		GridData appNameData = new GridData(100, 15);
		dockerAppName.setLayoutData(appNameData);

		Label volumeLabel = new Label(container, SWT.NONE);
		volumeLabel.setText("Volumes");

		dockerVolume = new Text(container, SWT.BORDER | SWT.SINGLE | SWT.READ_ONLY);
		dockerVolume.setText("");
		GridData volData = new GridData(200, 15);
		dockerVolume.setLayoutData(volData);

		Label portLabel = new Label(container, SWT.NONE);
		portLabel.setText("Ports");

		dockerPort = new Text(container, SWT.BORDER | SWT.SINGLE | SWT.READ_ONLY);
		dockerPort.setText("");
		GridData port1Data = new GridData(200, 15);
		dockerPort.setLayoutData(port1Data);

		Label linkLabel = new Label(container, SWT.NONE);
		linkLabel.setText("Links");

		dockerLink = new Text(container, SWT.BORDER | SWT.SINGLE | SWT.READ_ONLY);
		dockerLink.setText("");
		GridData linkData = new GridData(200, 15);
		dockerLink.setLayoutData(linkData);

		Label envLabel = new Label(container, SWT.NONE);
		envLabel.setText("Env Vars");

		dockerEnv = new Text(container, SWT.BORDER | SWT.SINGLE | SWT.READ_ONLY);
		dockerEnv.setText("");
		GridData envData = new GridData(200, 15);
		dockerEnv.setLayoutData(envData);
	}

	private BWDockerModule setBWCEDockerValues(BWModule module) {
		BWDockerModule bwdocker = module.getBwDockerModule();
		if (bwdocker == null) {
			bwdocker = new BWDockerModule();
		}

		bwdocker.setDockerHost(dockerHost.getText());
		bwdocker.setDockerHostCertPath(dockerHostCertPath.getText());
		bwdocker.setDockerImageName(dockerImageName.getText());
		bwdocker.setDockerImageFrom(dockerImageFrom.getText());
		
		bwdocker.setAutoPullImage(isAutoPull);
		bwdocker.setDockerImageMaintainer(dockerImageMaintainer.getText());

		// Below are Docker Run values - set them only if checked otherwise blank
		bwdocker.setDockerAppName(dockerAppName.getText());

		List<String> volumes = new ArrayList<String>();
		if (dockerVolume.getText() != null && !dockerVolume.getText().isEmpty()) {
			volumes = Arrays.asList(dockerVolume.getText().split("\\s*,\\s*"));
		}
		bwdocker.setDockerVolumes(volumes);

		List<String> links = new ArrayList<String>();
		if (dockerLink.getText() != null && !dockerLink.getText().isEmpty()) {
			links = Arrays.asList(dockerLink.getText().split("\\s*,\\s*"));
		}
		bwdocker.setDockerLinks(links);

		if (dockerPort.getEditable()) {
			List<String> ports = new ArrayList<String>();
			if (dockerPort.getText() != null && !dockerPort.getText().isEmpty()) {
				ports = Arrays.asList(dockerPort.getText().split("\\s*,\\s*"));
			}
			bwdocker.setDockerPorts(ports);
		}

		List<String> envs = new ArrayList<String>();
		if (dockerEnv.getText() != null && !dockerEnv.getText().isEmpty()) {
			envs = Arrays.asList(dockerEnv.getText().split("\\s*,\\s*"));
		}

		Map<String, String> envMap = new HashMap<String, String>();
		for (String env : envs) {
			String[] keyval = env.split("=");
			if (keyval[0] != null && keyval[1] != null) {
				envMap.put(keyval[0].trim(), keyval[1].trim());
			}
		}
		bwdocker.setDockerEnvs(envMap);
		// Run ends

		bwdocker.setPlatform(platform.getText());
		return bwdocker;
	}

	public BWProject getUpdatedProject() {
		for (BWModule module : project.getModules()) {
			if (module.getType() == BWModuleType.Application) {
				module.setBwDockerModule(setBWCEDockerValues(module));
				if (platform.getText().equals("K8S")) {
					module.setBwk8sModule(k8sPage.setBWCEK8SValues(module));
				}
			}
		}
		return project;
	}

	private void addSeperator(Composite parent) {
		Label horizontalLine = new Label(container, SWT.SEPARATOR | SWT.HORIZONTAL | SWT.LINE_DASH);
		horizontalLine.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 4, 1));
		horizontalLine.setFont(parent.getFont());
	}
	
	@Override
	public boolean canFlipToNextPage() 
	{
		return false;
	}
}

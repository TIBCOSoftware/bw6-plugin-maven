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

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
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
	private int textHeight = 18;
	
	// private static int numDockerElements=0;//24;
	private FileInputStream devPropfile = null;
	Map<String, String> properties = new HashMap();
	StringBuilder envStr = new StringBuilder();

	private Text platform;

	protected WizardPageDocker(String pageName, BWProject project) {
		super(pageName);
		this.project = project;
		setTitle("Docker Configuration for TIBCO BusinessWorks Container Edition");
		setDescription("Enter Docker and Platform details for pushing and running docker image.");
	}

	@Override
	public void createControl(Composite parent) {
		container = new Composite(parent, SWT.NONE);

		GridLayout layout = new GridLayout();
		layout.numColumns = 4;
		container.setLayout(layout);
		final File dkrdevfile = setApplicationDockerBuildPOMFields();
		//addSeperator(parent);
		setApplicationDockerRunPOMFields(dkrdevfile);
		//addSeperator(parent);
		selectDockerDeploymentPlatforms();
		// addSeperator(parent);
		setControl(container);
		setPageComplete(true);
	}
	
	private String getWorkspacepath() {
		for (BWModule module : project.getModules()) {
			if (module.getType() == BWModuleType.Application) {
				String pomloc = module.getPomfileLocation().toString();
				String workspace = pomloc.substring(0,
						pomloc.indexOf("pom.xml"));
				return workspace;
			}
		}
		return null;
	}

	private void selectDockerDeploymentPlatforms() {
		/*Label lLabel = new Label(container, SWT.NONE);
		lLabel.setText("Select Platform :");
		GridData lData = new GridData(500, textHeight+5);
		lData.horizontalSpan = 4;
		lLabel.setLayoutData(lData);*/

		Group dockerDeploymentPlatformConfig = new Group(container, SWT.SHADOW_ETCHED_IN);
		dockerDeploymentPlatformConfig.setText("Select Platform : ");
		GridData lData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		lData.horizontalSpan = 4;
		dockerDeploymentPlatformConfig.setLayoutData(lData);
		dockerDeploymentPlatformConfig.setLayout(new GridLayout(4, false));
		
		platform = new Text(dockerDeploymentPlatformConfig, SWT.NONE);
		platform.setVisible(false);
		platform.setText("");
		GridData platformData = new GridData(20, textHeight);
		platform.setLayoutData(platformData);

		Composite innerContainer = new Composite(dockerDeploymentPlatformConfig, SWT.NONE);

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

	private File setApplicationDockerBuildPOMFields() {
		final File dkrdevfile = new File(getWorkspacepath() + File.separator
				+ "docker-dev.properties");
		final File dkrdevEnvfile = new File(getWorkspacepath() + File.separator
				+ "docker-host-env-dev.properties");
		if (dkrdevfile.exists()) {
			try {
				devPropfile = new FileInputStream(dkrdevfile);
				Properties props = new Properties();
				props.load(devPropfile);
				devPropfile.close();
				Enumeration enuKeys = props.keys();

				while (enuKeys.hasMoreElements()) {
					String key = (String) enuKeys.nextElement();
					String value = props.getProperty(key);
					properties.put(key, value);
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
		if (dkrdevEnvfile.exists()) {

			FileInputStream devEnvPropFile = null;
			try {
				devEnvPropFile = new FileInputStream(dkrdevEnvfile);
				Properties propEnv = new Properties();
				propEnv.load(devEnvPropFile);
				devEnvPropFile.close();
				Enumeration enuKeys = propEnv.keys();

				while (enuKeys.hasMoreElements()) {
					String key = (String) enuKeys.nextElement();
					String value = propEnv.getProperty(key);
					envStr.append(key);
					envStr.append("=");
					envStr.append(value);
					envStr.append(",");
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			} finally {
				try {
					devEnvPropFile.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}

		/*Label lLabel = new Label(container, SWT.NONE);
		lLabel.setText("Docker host build configuration:");
		GridData lData = new GridData(300, textHeight+5);
		lData.horizontalSpan = 4;
		lLabel.setLayoutData(lData);*/

		Group dockerHostConfig = new Group(container, SWT.SHADOW_ETCHED_IN);
		dockerHostConfig.setText("Docker Host Build Configuration : ");
		GridData lData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		lData.horizontalSpan = 4;
		dockerHostConfig.setLayoutData(lData);
		dockerHostConfig.setLayout(new GridLayout(4, false));
		
		
		Label l1Label = new Label(dockerHostConfig, SWT.NONE);
		l1Label.setText("");
		GridData l1Data = new GridData(300, textHeight);
		l1Data.horizontalSpan = 4;
		l1Label.setLayoutData(l1Data);

		Label targetLabel = new Label(dockerHostConfig, SWT.NONE);
		targetLabel.setText("Docker Host");

		dockerHost = new Text(dockerHostConfig, SWT.BORDER | SWT.SINGLE);
		if (properties.containsKey("bwdocker.host"))
			dockerHost.setText(properties.get("bwdocker.host"));
		else
			dockerHost.setText(MavenProjectPreferenceHelper.INSTANCE.getDefaultDocker_URL(MavenPropertiesFileDefaults.INSTANCE.getDefaultDocker_URL("tcp://0.0.0.0:2376")));
		GridData dockerHostData = new GridData(300, textHeight);
		dockerHost.setLayoutData(dockerHostData);

		Label certLabel = new Label(dockerHostConfig, SWT.NONE);
		certLabel.setText("Cert Path");

		dockerHostCertPath = new Text(dockerHostConfig, SWT.BORDER | SWT.SINGLE);
		if (properties.containsKey("bwdocker.certPath"))
			dockerHostCertPath.setText(properties.get("bwdocker.certPath"));
		else
			dockerHostCertPath.setText(MavenProjectPreferenceHelper.INSTANCE.getDefaultDocker_CertPath(MavenPropertiesFileDefaults.INSTANCE.getDefaultDocker_CertPath("</home/user/machine/>")));
		GridData dockerHostCertData = new GridData(300, textHeight);
		dockerHostCertPath.setLayoutData(dockerHostCertData);

		Label imgNameLabel = new Label(dockerHostConfig, SWT.NONE);
		imgNameLabel.setText("Image Name");

		dockerImageName = new Text(dockerHostConfig, SWT.BORDER | SWT.SINGLE);
		if (properties.containsKey("docker.image"))
			dockerImageName.setText(properties.get("docker.image"));
		else
			dockerImageName.setText(MavenProjectPreferenceHelper.INSTANCE.getDefaultDocker_ImageName(MavenPropertiesFileDefaults.INSTANCE.getDefaultDocker_ImageName("gcr.io/<project_id>/<image-name>")));
		GridData dockerImgNameData = new GridData(300, textHeight);
		dockerImageName.setLayoutData(dockerImgNameData);

		Label imgFromLabel = new Label(dockerHostConfig, SWT.NONE);
		imgFromLabel.setText("BWCE Image");

		dockerImageFrom = new Text(dockerHostConfig, SWT.BORDER | SWT.SINGLE);
		if (properties.containsKey("bwdocker.from"))
			dockerImageFrom.setText(properties.get("bwdocker.from"));
		else
			dockerImageFrom.setText(MavenProjectPreferenceHelper.INSTANCE.getDefaultDocker_BWCEImage(MavenPropertiesFileDefaults.INSTANCE.getDefaultDocker_BWCEImage("tibco/bwce")));
		GridData imgFromData = new GridData(300, textHeight);
		dockerImageFrom.setLayoutData(imgFromData);
		
		Label autoPullLabel = new Label(dockerHostConfig, SWT.NONE);
		autoPullLabel.setText("Auto Pull Base Image");
		GridData autoPullData = new GridData(50, textHeight);
		autoPullImage= new Button(dockerHostConfig, SWT.CHECK);
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

		Label maintainerLabel = new Label(dockerHostConfig, SWT.NONE);
		maintainerLabel.setText("Maintainer");

		dockerImageMaintainer = new Text(dockerHostConfig, SWT.BORDER | SWT.SINGLE);
		if (properties.containsKey("bwdocker.maintainer"))
			dockerImageMaintainer.setText(properties.get("bwdocker.maintainer"));
		else
			dockerImageMaintainer.setText(MavenProjectPreferenceHelper.INSTANCE.getDefaultDocker_Maintainer(MavenPropertiesFileDefaults.INSTANCE.getDefaultDocker_Maintainer("abc@tibco.com")));
		GridData maintainerData = new GridData(300, textHeight);

		dockerImageMaintainer.setLayoutData(maintainerData);
		
		return dkrdevfile;
		// createContents(container);
	}

	private void setApplicationDockerRunPOMFields(final File dkrdevfile) {
		/*Label lLabel = new Label(container, SWT.NONE);
		lLabel.setText("Docker host run configuration:");
		GridData lData = new GridData(300, textHeight+5);
		lData.horizontalSpan = 4;
		lLabel.setLayoutData(lData);
		 */
		
		Group dockerHostRunConfig = new Group(container, SWT.SHADOW_ETCHED_IN);
		dockerHostRunConfig.setText("Docker Host Run Configuration : ");
		GridData lData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		lData.horizontalSpan = 4;
		dockerHostRunConfig.setLayoutData(lData);
		dockerHostRunConfig.setLayout(new GridLayout(4, false));
		
		Label l1Label = new Label(dockerHostRunConfig, SWT.NONE);
		l1Label.setText("");
		GridData l1Data = new GridData(300, textHeight);
		l1Data.horizontalSpan = 4;
		l1Label.setLayoutData(l1Data);

		//Composite innerContainer = new Composite(dockerHostRunConfig, SWT.NONE);

		Label dkrlabel = new Label(dockerHostRunConfig, SWT.NONE);
		dkrlabel.setText("Run on docker host");
		GridData dkrlabelData = new GridData(150, textHeight);
		dkrlabel.setLayoutData(dkrlabelData);
		
		final Button dkr = new Button(dockerHostRunConfig, SWT.CHECK);
		GridData dkrData = new GridData();
		dkrData.horizontalSpan = 3;
		dkr.setLayoutData(dkrData);
		
		dkr.setSelection(false);

		dkr.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (dkr.getSelection()) {
					dockerAppName.setEditable(true);
					if (properties.containsKey("bwdocker.containername"))
						dockerAppName.setText(properties
								.get("bwdocker.containername"));
					else
						dockerAppName.setText("BWCEAPP");

					dockerVolume.setEditable(true);
					dockerLink.setEditable(true);
					dockerPort.setEditable(true);
					dockerEnv.setEditable(true);
					if (envStr.length() != 0)
						dockerEnv.setText(envStr.substring(0, envStr.length() - 1));

					if (dkrdevfile.exists()) {
						setPropertyVars(dockerVolume, "bwdocker.volume.v");
						setPropertyVars(dockerLink, "bwdocker.link.l");
						setPropertyVars(dockerPort, "bwdocker.port.p");
					} else {
						dockerPort.setText("18080:8080,17777:7777");
					}

					container.layout();
				} else {
					setEditableFalseForAllText(dockerAppName, dockerVolume,dockerLink, dockerEnv, dockerPort);
					container.layout();
				}
			}

			private void setEditableFalseForAllText(Text... texts) {
				for (Text t : texts) {
					t.setEditable(false);
					t.setText("");
				}
			}

			private void setPropertyVars(Text text, String strContained) {
				StringBuilder str = new StringBuilder();
				for (int i = 0; i < properties.size(); i++) {
					if (properties.containsKey(strContained + i)) {
						str.append(properties.get(strContained + i));
						str.append(",");
					}
				}
				if (str.length() != 0)
					text.setText(str.substring(0, str.length() - 1));
			}
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		
		Label appNameLabel = new Label(dockerHostRunConfig, SWT.NONE);
		appNameLabel.setText("App Name");

		dockerAppName = new Text(dockerHostRunConfig, SWT.BORDER | SWT.SINGLE | SWT.READ_ONLY);
		dockerAppName.setText("");
		GridData appNameData = new GridData(300, textHeight);
		dockerAppName.setLayoutData(appNameData);

		Label volumeLabel = new Label(dockerHostRunConfig, SWT.NONE);
		volumeLabel.setText("Volumes");

		dockerVolume = new Text(dockerHostRunConfig, SWT.BORDER | SWT.SINGLE | SWT.READ_ONLY);
		dockerVolume.setText("");
		GridData volData = new GridData(300, textHeight);
		dockerVolume.setLayoutData(volData);

		Label portLabel = new Label(dockerHostRunConfig, SWT.NONE);
		portLabel.setText("Ports");

		dockerPort = new Text(dockerHostRunConfig, SWT.BORDER | SWT.SINGLE | SWT.READ_ONLY);
		dockerPort.setText("");
		GridData port1Data = new GridData(300, textHeight);
		dockerPort.setLayoutData(port1Data);

		Label linkLabel = new Label(dockerHostRunConfig, SWT.NONE);
		linkLabel.setText("Links");

		dockerLink = new Text(dockerHostRunConfig, SWT.BORDER | SWT.SINGLE | SWT.READ_ONLY);
		dockerLink.setText("");
		GridData linkData = new GridData(300, textHeight);
		dockerLink.setLayoutData(linkData);

		Label envLabel = new Label(dockerHostRunConfig, SWT.NONE);
		envLabel.setText("Env Vars");

		dockerEnv = new Text(dockerHostRunConfig, SWT.BORDER | SWT.SINGLE | SWT.READ_ONLY);
		dockerEnv.setText("");
		GridData envData = new GridData(300, textHeight);
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

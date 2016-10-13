package com.tibco.bw.studio.maven.wizard;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.tibco.bw.studio.maven.helpers.ManifestParser;
import com.tibco.bw.studio.maven.helpers.ModuleHelper;
import com.tibco.bw.studio.maven.modules.BWDeploymentInfo;
import com.tibco.bw.studio.maven.modules.BWModule;
import com.tibco.bw.studio.maven.modules.BWModuleType;
import com.tibco.bw.studio.maven.modules.BWProject;
import com.tibco.bw.studio.maven.modules.model.BWApplication;

public class WizardPageEnterprise extends WizardPage {
	private Composite container;
	private BWProject project;
	private String bwEdition;
	private Button deployToAdmin;
	private Text agentHost;
	private Text agentPort;
	private Text domain;
	private Text domainDesc;
	private Text appspace;	
	private Text appspaceDesc;
	private Text minNodes;
	private Text appNode;
	private Text appNodeDesc;
	private Button redeploy;
	private Text httpPort;
	private Text osgiPort;
	private Combo profile;
	private BWModule appModule;
	private BWDeploymentInfo info;

	protected WizardPageEnterprise(String pageName, BWProject project) {
		super(pageName);
		this.project = project;		 
		setTitle("Deployment Details for Apache Maven and TIBCO BusinessWorks�");
		setDescription("Please Enter the Deployment details to Deploy the EAR file to BWAgent. \r\nThe EAR file will be deployed to the Agent provided below during the Maven \"install \" lifecycle phase.");	
	}

	public boolean validate() {
		StringBuffer errorMessage = new StringBuffer();
		boolean isValidHost = !agentHost.getText().isEmpty();
		if(!isValidHost) {
			errorMessage.append("[Agent Host value is required]");
		}
		boolean isValidPort = false;
		try {
			if(agentPort.getText().isEmpty()) {
				errorMessage.append("[Agent Port value is required]");
			} else if(Integer.parseInt(agentPort.getText()) < 0) {
				errorMessage.append("[Agent Port value must be an Integer]");
			} else {
				isValidPort = true;
			}
		} catch(Exception e) {
			errorMessage.append("[Agent Port value must be an Integer]");
		}

		boolean isValidDomain = !domain.getText().isEmpty();
		if(!isValidDomain) {
			errorMessage.append("[Domain value is required]");
		}

		boolean isValidAppSpace = !appspace.getText().isEmpty(); 
		if(!isValidAppSpace) {
			errorMessage.append("[AppSpace value is required]");
		}

		boolean isValidAppNode = !appNode.getText().isEmpty();
		if(!isValidAppNode) {
			errorMessage.append("[AppNode value is required]");
		}

		boolean isValidHTTPPort = false;
		try {
			if(httpPort.getText().isEmpty()) {
				errorMessage.append("[HTTP Port value is required]");
			} else if(Integer.parseInt(httpPort.getText()) < 0) {
				errorMessage.append("[HTTP Port value must be an Integer]");
			} else {
				isValidHTTPPort = true;
			}
		} catch(Exception e) {
			errorMessage.append("[HTTP Port value must be an Integer]");
		}

		boolean isValidOSGi = false;
		try {
			if(osgiPort.getText().isEmpty()) {
				isValidOSGi = true;
			} else if(Integer.parseInt(osgiPort.getText()) < 0) {
				isValidOSGi = false;
				errorMessage.append("[OSGi Port value must be an Integer]");
			} else {
				isValidOSGi = true;
			}
		} catch(Exception e) {
			errorMessage.append("[OSGi Port value must be an Integer]");
		}

		if(!errorMessage.toString().isEmpty()) {
			setErrorMessage(errorMessage.toString());
			return false;
		}
		if(isValidHost && isValidPort && isValidDomain && isValidAppSpace && isValidAppNode && isValidHTTPPort) {
			return true;
		}
		return false;
	}

	@Override
	public void createControl(Composite parent) {
		container = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(4, false);
		container.setLayout(layout);
		layout.numColumns = 4;
		appModule = ModuleHelper.getAppModule(project.getModules());
		info = ((BWApplication)ModuleHelper.getApplication(project.getModules())).getDeploymentInfo();
		bwEdition = "bw6";
		try {
			Map<String, String> manifest = ManifestParser.parseManifest(project.getModules().get(0).getProject());
			if (manifest.containsKey("TIBCO-BW-Edition") && manifest.get("TIBCO-BW-Edition").equals("bwcf")) {
				bwEdition = "bwcf";
			} else {
				bwEdition = "bw6";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		addNotes();
		addSeperator(parent);
		addDeploymentFields(parent);
		setControl(container);
		setPageComplete(true);
	}

	private void addNotes() {
		Label label = new Label(container, SWT.NONE);
		label.setText("Please Enter the Host and Port of the Machine where the BWAgent is running. \r\n"
				+ "Please Enter the Domain, AppSpace and AppNode Information\r\n"
				+ "Note* : If the Domain, Appspace and AppNode do not exist then they will be created.\r\n"
				+ "EAR file will be started on deployment");
		GridData versionData = new GridData();
		versionData.horizontalSpan = 4;
		label.setLayoutData(versionData);
	}

	private void addSeperator(Composite parent) {
		Label horizontalLine = new Label(container, SWT.SEPARATOR | SWT.HORIZONTAL | SWT.LINE_DASH);
		horizontalLine.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 4, 1));
		horizontalLine.setFont(parent.getFont());
	}

	public BWProject getUpdatedProject() {
		for (BWModule module : project.getModules()) {
			if(bwEdition.equals("bw6") && module.getType() == BWModuleType.Application) {
				BWDeploymentInfo info = ((BWApplication)module).getDeploymentInfo();
				info.setAgentHost(agentHost.getText());
				info.setAgentPort(agentPort.getText());
				info.setDomain(domain.getText());
				info.setDomainDesc(domainDesc.getText());
				info.setAppspace(appspace.getText());
				info.setAppspaceDesc(appspaceDesc.getText());
				info.setAppNode(appNode.getText());
				info.setAppNodeDesc(appNodeDesc.getText());
				info.setHttpPort(httpPort.getText());
				info.setOsgiPort(osgiPort.getText());
				info.setProfile(profile.getText());
				info.setRedeploy(redeploy.getSelection());
			}
			module.setOverridePOM(true);
		}
		return project;
	}

	private void addDeploymentFields(Composite parent) {
		addAgentInfo();
		addSeperator(parent);
		addDomain();
		addAppSpace();
		addSeperator(parent);
		addAppNode();
		addSeperator(parent);
		addProfile();
		addSeperator(parent);
	}

	private void addAgentInfo() {
		Label agentLabel = new Label(container, SWT.NONE);
		agentLabel.setText("Agent Host");

		agentHost = new Text(container, SWT.BORDER | SWT.SINGLE);
		agentHost.setText(info.getAgentHost());
		GridData agentData = new GridData(150, 15);
		agentHost.setLayoutData(agentData);

		Label agentPortLabel = new Label(container, SWT.NONE);
		agentPortLabel.setText("Agent Port");

		agentPort = new Text(container, SWT.BORDER | SWT.SINGLE);
		agentPort.setText(info.getAgentPort());
		GridData agentPortData = new GridData(150, 15);
		agentPort.setLayoutData(agentPortData);
	}

	private void addDomain() {
		Label domainLabel = new Label(container, SWT.NONE);
		domainLabel.setText("Domain");

		domain = new Text(container, SWT.BORDER | SWT.SINGLE);
		if(info.getDomain() != null && !info.getDomain().isEmpty()) {
			domain.setText(info.getDomain());
		} else {
			domain.setText(appModule.getArtifactId() + "-Domain");	
		}

		GridData domainData = new GridData(150, 15);
		domain.setLayoutData(domainData);

		Label domainDescLabel = new Label(container, SWT.NONE);
		domainDescLabel.setText("Description");

		domainDesc = new Text(container, SWT.BORDER | SWT.SINGLE);
		domainDesc.setText(info.getDomainDesc());
		GridData domainDescData = new GridData(300, 15);
		domainDesc.setLayoutData(domainDescData);
	}

	private void addAppSpace() {
		Label appspaceLabel = new Label(container, SWT.NONE);
		appspaceLabel.setText("AppSpace");

		appspace = new Text(container, SWT.BORDER | SWT.SINGLE);
		if(info.getAppspace() != null && !info.getAppspace().isEmpty()) {
			appspace.setText(info.getAppspace());
		} else {
			appspace.setText(appModule.getArtifactId() + "-AppSpace");	
		}

		GridData appspaceData = new GridData(150, 15);
		appspace.setLayoutData(appspaceData);
		
		Label appspaceDescLabel = new Label(container, SWT.NONE);
		appspaceDescLabel.setText("Description");

		appspaceDesc = new Text(container, SWT.BORDER | SWT.SINGLE);
		appspaceDesc.setText(info.getAppspaceDesc());
		GridData appspaceDescData = new GridData(300, 15);
		appspaceDesc.setLayoutData(appspaceDescData);
	}

	private void addAppNode() {
		Label appNodeLabel = new Label(container, SWT.NONE);
		appNodeLabel.setText("AppNode");

		appNode = new Text(container, SWT.BORDER | SWT.SINGLE);
		if(info.getAppNode() != null && !info.getAppNode().isEmpty()) {
			appNode.setText(info.getAppNode());
		} else {
			appNode.setText(appModule.getArtifactId() + "-AppNode");	
		}

		GridData appNodeData = new GridData(150, 15);
		appNode.setLayoutData(appNodeData);

		Label appnodeDescLabel = new Label(container, SWT.NONE);
		appnodeDescLabel.setText("Description");

		appNodeDesc = new Text(container, SWT.BORDER | SWT.SINGLE);
		appNodeDesc.setText(info.getAppNodeDesc());
		GridData appnodeDescData = new GridData(300, 15);
		appNodeDesc.setLayoutData(appnodeDescData);

		Label httpLabel = new Label(container, SWT.NONE);
		httpLabel.setText("HTTP Port");

		httpPort = new Text(container, SWT.BORDER | SWT.SINGLE);
		httpPort.setText(info.getHttpPort());
		GridData httpData = new GridData(150, 15);
		httpPort.setLayoutData(httpData);

		Label osgiPortLabel = new Label(container, SWT.NONE);
		osgiPortLabel.setText("OSGI Port");

		osgiPort = new Text(container, SWT.BORDER | SWT.SINGLE);
		osgiPort.setText(info.getOsgiPort());
		GridData agentData = new GridData(150, 15);
		osgiPort.setLayoutData(agentData);
	}
	
	private void addProfile() {
		Label profileLabel = new Label(container, SWT.NONE);
		profileLabel.setText("Profile");

		profile = new Combo(container, SWT.BORDER | SWT.SINGLE);
		List<String> profiles = getProfiles(); 
		for(String name : profiles) {
			profile.add(name);
		}
		int index = getSelectedProfile(profiles);
		if(index != -1) {
			profile.select(index);	
		}

		GridData profileData = new GridData(120, 15);
		profileData.horizontalSpan = 3;
		profile.setLayoutData(profileData);
		addRedeployBox();
	}

	private void addRedeployBox() {
		redeploy = new Button(container, SWT.CHECK);
		redeploy.setSelection(info.isRedeploy());
		redeploy.setToolTipText("If this is checked the the Application will be redeployed if exists.");

		Label domainLabel = new Label(container, SWT.NONE);
		domainLabel.setText("Re Deploy the Application if exists.");
		domainLabel.setToolTipText("Re Deploy the Application if exists.");

		GridData deployData = new GridData(350, 15);
		deployData.horizontalSpan = 3;

		domainLabel.setLayoutData(deployData);
	}

	private int getSelectedProfile(List<String> profiles) {
		if(info.getProfile() != null && !info.getProfile().isEmpty()) {
			if(profiles.contains(info.getProfile())) {
				return profiles.indexOf(info.getProfile());	
			}
		}
		String os = System.getProperty("os.name");
		boolean isWindows = false;
		if (os.indexOf("Windows") != -1) {
			isWindows = true;
		}

		if(isWindows && profiles.contains("WindowsProfile.substvar")) {
			return profiles.indexOf("WindowsProfile.substvar");
		} else if(!isWindows && profiles.contains("UnixProfile.substvar")) {
			return profiles.indexOf("UnixProfile.substvar");
		} else if(profiles.size() == 1) {
			return 0;
		} else {
			if(profiles.contains("default.substvar")) {
				return profiles.indexOf("default.substvar");	
			}
		}
		return -1;
	}

	private List<String> getProfiles() {
		File appProject = new File(ModuleHelper.getApplication(project.getModules()).getProject().getLocationURI());
		File metainf = new File (appProject, "META-INF");
		File[] files = metainf.listFiles(new FileFilter() {
			public boolean accept(File pathname) {
				if (pathname.getName().indexOf(".substvar") != -1) {
        			return true;
				}
				return false;
			}
		});
		List<String> list = new ArrayList<String>();
		for(File file : files) {
			list.add(file.getName());
		}
		return list;
	}
}

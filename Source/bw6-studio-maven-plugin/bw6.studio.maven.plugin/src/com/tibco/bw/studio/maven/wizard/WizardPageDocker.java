package com.tibco.bw.studio.maven.wizard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.tibco.bw.studio.maven.modules.BWDockerModule;
import com.tibco.bw.studio.maven.modules.BWModule;
import com.tibco.bw.studio.maven.modules.BWModuleType;
import com.tibco.bw.studio.maven.modules.BWProject;

public class WizardPageDocker extends WizardPage{

	private Composite container;
	private BWProject project;

	private Text dockerHost;
	private Text dockerHostCertPath;
	private Text dockerImageName;
	private Text dockerImageFrom;
	private Text dockerImageMaintainer;
	private Text dockerAppName;
	private Text dockerVolume;
	private Text dockerLink;
	private Text dockerPort1;
	private Text dockerPort2;
	private static int numDockerElements=24;
	
	private Text platform;
	private Text rcName;
	private Text numOfReplicas;
	private Text serviceName;
	private Text containerPort;
	private Text k8sNamespace;
	private Text k8sEnvKey1;
	private Text k8sEnvVal1;
	private Text k8sEnvKey2;
	private Text k8sEnvVal2;
	
	protected WizardPageDocker ( String pageName , BWProject project ) 
	{
		super(pageName);
		this.project = project;		 
		setTitle("Docker Plugin for Apache Maven and TIBCO BusinessWorks Container Edition™");
		setDescription("Enter Docker and Platform details for pushing and running docker image.");	
	}

	@Override
	public void createControl(Composite parent) 
	{
		container = new Composite(parent, SWT.NONE);

		GridLayout layout = new GridLayout();
		layout.numColumns = 4;
		container.setLayout(layout);
		setApplicationDockerPOMFields();

		addSeperator(parent);

		setPlatformValuesForDockerImage();

		addSeperator(parent);

		setControl(container);
		setPageComplete(true);

	}

	private void setK8SPOMFields(){

		Label rcLabel = new Label(container, SWT.NONE);
		rcLabel.setText("RC Name");

		rcName = new Text(container, SWT.BORDER | SWT.SINGLE);
		rcName.setText("");
		GridData rcData = new GridData(200, 15);
		rcName.setLayoutData(rcData);


		Label replicaLabel = new Label(container, SWT.NONE);
		replicaLabel.setText("No Of Replicas");

		numOfReplicas = new Text(container, SWT.BORDER | SWT.SINGLE);
		numOfReplicas.setText("1");
		GridData replicaData = new GridData(50, 15);
		numOfReplicas.setLayoutData(replicaData);


		Label srvNameLabel = new Label(container, SWT.NONE);
		srvNameLabel.setText("Service Name");

		serviceName = new Text(container, SWT.BORDER | SWT.SINGLE);
		serviceName.setText("");
		GridData serviceNamData = new GridData(200, 15);
		serviceName.setLayoutData(serviceNamData);


		Label contPortLabel = new Label(container, SWT.NONE);
		contPortLabel.setText("Container Port");

		containerPort = new Text(container, SWT.BORDER | SWT.SINGLE);
		containerPort.setText("8080");
		GridData contPortData = new GridData(50, 15);
		containerPort.setLayoutData(contPortData);
		
		Label namespaceLabel = new Label(container, SWT.NONE);
		namespaceLabel.setText("K8S Namespace");

		k8sNamespace = new Text(container, SWT.BORDER | SWT.SINGLE);
		k8sNamespace.setText("default");
		GridData namespcData = new GridData(100, 15);
		k8sNamespace.setLayoutData(namespcData);
		
		Label envVarsLabel = new Label(container, SWT.NONE);
		envVarsLabel.setText("K8S Environment Variables");
		GridData envVarData = new GridData(150, 15);
		envVarData.horizontalSpan=2;
		envVarsLabel.setLayoutData(envVarData);
		
		Label bLabel = new Label(container, SWT.NONE);
		bLabel.setText("");
		GridData bData = new GridData(70, 15);
		bData.horizontalSpan=2;
		bLabel.setLayoutData(bData);
		
		Label envVarKeyLabel = new Label(container, SWT.BORDER);
		envVarKeyLabel.setText("Env Key");
		GridData envVarKeyData = new GridData(70, 15);
		envVarKeyLabel.setLayoutData(envVarKeyData);
		
		Label envVarValLabel = new Label(container, SWT.BORDER);
		envVarValLabel.setText("Env Val");
		GridData envVarValData = new GridData(70, 15);
		envVarValLabel.setLayoutData(envVarValData);
		
		
		Label b1Label = new Label(container, SWT.NONE);
		b1Label.setText("");
		GridData b1Data = new GridData(70, 15);
		b1Data.horizontalSpan=2;
		b1Label.setLayoutData(b1Data);
		
		k8sEnvKey1 = new Text(container, SWT.BORDER | SWT.SINGLE);
		k8sEnvKey1.setText("APP_CONFIG_PROFILE");
		GridData envkey1Data = new GridData(80, 15);
		k8sEnvKey1.setLayoutData(envkey1Data);
		
		k8sEnvVal1 = new Text(container, SWT.BORDER | SWT.SINGLE);
		k8sEnvVal1.setText("docker");
		GridData envval1Data = new GridData(150, 15);
		k8sEnvVal1.setLayoutData(envval1Data);
		
		
		Label b2Label = new Label(container, SWT.NONE);
		b2Label.setText("");
		GridData b2Data = new GridData(70, 15);
		b2Data.horizontalSpan=2;
		b2Label.setLayoutData(b2Data);
		
		k8sEnvKey2 = new Text(container, SWT.BORDER | SWT.SINGLE);
		k8sEnvKey2.setText("CONSUL_SERVER_URL");
		GridData envkey2Data = new GridData(80, 15);
		k8sEnvKey2.setLayoutData(envkey2Data);
		
		k8sEnvVal2 = new Text(container, SWT.BORDER | SWT.SINGLE);
		k8sEnvVal2.setText("http://0.0.0.0:80");
		GridData envval2Data = new GridData(150, 15);
		k8sEnvVal2.setLayoutData(envval2Data);
	}

	private void setPlatformValuesForDockerImage() 
	{
		Group group1 = new Group(container, SWT.SHADOW_IN);
		group1.setText("Select platform where you want to run docker image?");
		GridData gridData = new GridData();
		gridData.horizontalSpan = 4;
		GridLayout layout = new GridLayout(4,false);
		group1.setLayout(layout);
		group1.setLayoutData(gridData);
		
		platform=new Text(container, SWT.NONE);
		platform.setVisible(false);
		final Button b1=new Button(group1, SWT.RADIO);
		b1.setText("K8S");
		b1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(b1.getSelection()){
					platform.setText("K8S");
					Control [] ctrls = container.getChildren();
					if(ctrls.length>numDockerElements){
						for(int i=numDockerElements;i<ctrls.length;i++){
							ctrls[i].dispose();
						}
					}
					setK8SPOMFields();
					container.layout();
				}
			}
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		final Button b2=new Button(group1, SWT.RADIO);
		b2.setText("Mesos");
		b2.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(b2.getSelection()){
					platform.setText("Mesos");
					Control [] ctrls = container.getChildren();
					if(ctrls.length>numDockerElements){
						for(int i=numDockerElements;i<ctrls.length;i++){
							ctrls[i].dispose();
						}
					}
					container.layout();
				}
			}
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		final Button b3=new Button(group1, SWT.RADIO);
		b3.setText("Swarm");
		b3.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(b3.getSelection()){
					platform.setText("Swarm");
					Control [] ctrls = container.getChildren();
					if(ctrls.length>numDockerElements){
						for(int i=numDockerElements;i<ctrls.length;i++){
							ctrls[i].dispose();
						}
					}
					container.layout();
				}
			}
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});


	}

	private void setApplicationDockerPOMFields() 
	{


		Label targetLabel = new Label(container, SWT.NONE);
		targetLabel.setText("Docker Host");

		dockerHost = new Text(container, SWT.BORDER | SWT.SINGLE);
		dockerHost.setText( "tcp://0.0.0.0:2376");
		GridData dockerHostData = new GridData(200, 15);
		dockerHost.setLayoutData(dockerHostData);


		Label certLabel = new Label(container, SWT.NONE);
		certLabel.setText("Cert Path");

		dockerHostCertPath = new Text(container, SWT.BORDER | SWT.SINGLE);
		dockerHostCertPath.setText("</home/user/machine/>");
		GridData dockerHostCertData = new GridData(200, 15);
		dockerHostCertPath.setLayoutData(dockerHostCertData);

		Label imgNameLabel = new Label(container, SWT.NONE);
		imgNameLabel.setText("Image Name");

		dockerImageName = new Text(container, SWT.BORDER | SWT.SINGLE);
		dockerImageName.setText("gcr.io/<project_id>/<image-name>");
		GridData dockerImgNameData = new GridData(200, 15);
		dockerImageName.setLayoutData(dockerImgNameData);

		Label imgFromLabel = new Label(container, SWT.NONE);
		imgFromLabel.setText("BWCE Image");

		dockerImageFrom = new Text(container, SWT.BORDER | SWT.SINGLE);
		dockerImageFrom.setText("tibco/bwce");
		GridData imgFromData = new GridData(100, 15);
		dockerImageFrom.setLayoutData(imgFromData);


		Label maintainerLabel = new Label(container, SWT.NONE);
		maintainerLabel.setText("Maintainer");

		dockerImageMaintainer = new Text(container, SWT.BORDER | SWT.SINGLE);
		dockerImageMaintainer.setText("abc@tibco.com");
		GridData maintainerData = new GridData(200, 15);
		dockerImageMaintainer.setLayoutData(maintainerData);


		Label appNameLabel = new Label(container, SWT.NONE);
		appNameLabel.setText("App Name");

		dockerAppName = new Text(container, SWT.BORDER | SWT.SINGLE);
		dockerAppName.setText("BWCEAPP");
		GridData appNameData = new GridData(100, 15);
		dockerAppName.setLayoutData(appNameData);
		
		
		Label volumeLabel = new Label(container, SWT.NONE);
		volumeLabel.setText("Volume Path");

		dockerVolume = new Text(container, SWT.BORDER | SWT.SINGLE);
		dockerVolume.setText("");
		GridData volData = new GridData(200, 15);
		volData.horizontalSpan=3;
		dockerVolume.setLayoutData(volData);
		
		
		Label portLabel = new Label(container, SWT.NONE);
		portLabel.setText("Docker-run Ports");

		dockerPort1 = new Text(container, SWT.BORDER | SWT.SINGLE);
		dockerPort1.setText("18080:8080");
		GridData port1Data = new GridData(70, 15);
		dockerPort1.setLayoutData(port1Data);
		
		Label linkLabel = new Label(container, SWT.NONE);
		linkLabel.setText("Docker-run Link");

		dockerLink = new Text(container, SWT.BORDER | SWT.SINGLE);
		dockerLink.setText("");
		GridData linkData = new GridData(100, 15);
		dockerLink.setLayoutData(linkData);
		
		Label bLabel = new Label(container, SWT.NONE);
		bLabel.setText("");
		
		dockerPort2 = new Text(container, SWT.BORDER | SWT.SINGLE);
		dockerPort2.setText("17777:7777");
		GridData port2Data = new GridData(70, 15);
		port2Data.horizontalSpan=3;
		dockerPort2.setLayoutData(port2Data);
		
		
		
		//createContents(container);
	}

	private BWDockerModule setBWCEDockerValues(BWModule module){

		BWDockerModule bwdocker=module.getBwDockerModule();
		if(bwdocker==null){
			bwdocker=new BWDockerModule();
		}
		
		bwdocker.setDockerHost(dockerHost.getText());
		bwdocker.setDockerHostCertPath(dockerHostCertPath.getText());
		bwdocker.setDockerImageName(dockerImageName.getText());
		bwdocker.setDockerImageFrom(dockerImageFrom.getText());
		bwdocker.setDockerImageMaintainer(dockerImageMaintainer.getText());
		bwdocker.setDockerAppName(dockerAppName.getText());
		bwdocker.setDockerVolume(dockerVolume.getText());
		bwdocker.setDockerLink(dockerLink.getText());
		
		List<String> ports=new ArrayList<String>();
		if(dockerPort1.getText()!=null && !dockerPort1.getText().isEmpty()){
			ports.add(dockerPort1.getText());
		}
		if(dockerPort2.getText()!=null && !dockerPort2.getText().isEmpty()){
			ports.add(dockerPort2.getText());
		}
		bwdocker.setDockerPorts(ports);
		
		bwdocker.setPlatform(platform.getText());
		//Set K8S platform configuration
		if(platform.getText().equals("K8S"))
		{
			bwdocker.setRcName(rcName.getText());
			bwdocker.setNumOfReplicas(numOfReplicas.getText());
			bwdocker.setServiceName(serviceName.getText());
			bwdocker.setContainerPort(containerPort.getText());
			bwdocker.setK8sNamespace(k8sNamespace.getText());
			
			//set Env Variables of K8S
			Map<String, String> envVar=new HashMap<String, String>();
			if(k8sEnvKey1.getText()!=null && !k8sEnvKey1.getText().isEmpty()){
				envVar.put(k8sEnvKey1.getText(), k8sEnvVal1.getText());
			}
			if(k8sEnvKey2.getText()!=null && !k8sEnvKey2.getText().isEmpty()){
				envVar.put(k8sEnvKey2.getText(), k8sEnvVal2.getText());
			}
			
			if(!envVar.isEmpty()){
				bwdocker.setK8sEnvVariables(envVar);
			}
		}
		
		return bwdocker;
	}
	
	public BWProject getUpdatedProject() 
	{
		for (BWModule module : project.getModules() )
		{
			if(module.getType() == BWModuleType.Application){
				module.setBwDockerModule(setBWCEDockerValues(module));
			}
		}
		return project;
	}

	private void addSeperator(Composite parent) 
	{
		Label horizontalLine = new Label(container, SWT.SEPARATOR | SWT.HORIZONTAL | SWT.LINE_DASH);
		horizontalLine.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 4, 1));
		horizontalLine.setFont(parent.getFont());
	}
}

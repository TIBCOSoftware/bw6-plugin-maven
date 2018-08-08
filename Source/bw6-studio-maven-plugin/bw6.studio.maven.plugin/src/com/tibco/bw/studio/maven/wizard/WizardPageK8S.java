package com.tibco.bw.studio.maven.wizard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.tibco.bw.studio.maven.modules.model.BWK8SModule;
import com.tibco.bw.studio.maven.modules.model.BWModule;
import com.tibco.bw.studio.maven.modules.model.BWProject;

public class WizardPageK8S extends WizardPage {
	private Composite container;
	@SuppressWarnings("unused")
	private BWProject project;
	private Text rcName;
	private Text numOfReplicas;
	private Text serviceName;
	private Text serviceType;
	private Text containerPort;
	private Text k8sNamespace;
	private Text k8sEnvVars;

	protected WizardPageK8S(String pageName, BWProject project) {
		super(pageName);
		this.project = project;
		setTitle("Kubernetes Plugin for Apache Maven and TIBCO BusinessWorks Container Edition");
		setDescription("Enter Kubernetes Platform details for pushing and running BWCE apps.");
	}

	@Override
	public void createControl(Composite parent) {
		container = new Composite(parent, SWT.NONE);

		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 4;

		setK8SPOMFields();
		// addSeperator(parent);
		setControl(container);
		setPageComplete(true);
	}

	private void setK8SPOMFields() {
		Label lLabel = new Label(container, SWT.NONE);
		lLabel.setText("Kubernetes configuration:");
		GridData lData = new GridData(150, 15);
		lData.horizontalSpan = 4;
		lLabel.setLayoutData(lData);

		Label l1Label = new Label(container, SWT.NONE);
		l1Label.setText("");
		GridData l1Data = new GridData(20, 15);
		l1Data.horizontalSpan = 4;
		l1Label.setLayoutData(l1Data);

		Label rcLabel = new Label(container, SWT.NONE);
		rcLabel.setText("Deployment Name");

		rcName = new Text(container, SWT.BORDER | SWT.SINGLE);
		rcName.setText("bwce-sample");
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
		serviceName.setText("bwce-sample-service");
		GridData serviceNamData = new GridData(200, 15);
		serviceName.setLayoutData(serviceNamData);
		
		Label srvTypeLabel = new Label(container, SWT.NONE);
		srvTypeLabel.setText("Service Type");
		
		serviceType = new Text(container, SWT.BORDER | SWT.SINGLE);
		serviceType.setText("LoadBalancer");
		GridData serviceTypeData = new GridData(200, 15);
		serviceName.setLayoutData(serviceTypeData);

		Label contPortLabel = new Label(container, SWT.NONE);
		contPortLabel.setText("Container Port");

		containerPort = new Text(container, SWT.BORDER | SWT.SINGLE);
		containerPort.setText("8080");
		GridData contPortData = new GridData(100, 15);
		containerPort.setLayoutData(contPortData);

		Label namespaceLabel = new Label(container, SWT.NONE);
		namespaceLabel.setText("K8S Namespace");

		k8sNamespace = new Text(container, SWT.BORDER | SWT.SINGLE);
		k8sNamespace.setText("default");
		GridData namespcData = new GridData(100, 15);
	
		k8sNamespace.setLayoutData(namespcData);

		Label envVarsLabel = new Label(container, SWT.NONE);
		envVarsLabel.setText("Env Vars");

		k8sEnvVars = new Text(container, SWT.BORDER | SWT.SINGLE);
		k8sEnvVars.setText("APP_CONFIG_PROFILE=docker, abc=xyz");
		GridData envvarData = new GridData(400, 15);
		envvarData.horizontalSpan = 3;
		k8sEnvVars.setLayoutData(envvarData);
	}

	public BWK8SModule setBWCEK8SValues(BWModule module) {
		BWK8SModule bwk8s = module.getBwk8sModule();
		if (bwk8s == null) {
			bwk8s = new BWK8SModule();
		}

		bwk8s.setRcName(rcName.getText());
		bwk8s.setNumOfReplicas(numOfReplicas.getText());
		bwk8s.setServiceName(serviceName.getText());
		bwk8s.setContainerPort(containerPort.getText());
		bwk8s.setK8sNamespace(k8sNamespace.getText());

		List<String> envvars = new ArrayList<String>();
		if (k8sEnvVars.getText() != null && !k8sEnvVars.getText().isEmpty()) {
			envvars = Arrays.asList(k8sEnvVars.getText().split("\\s*,\\s*"));
		}

		Map<String, String> envMap = new HashMap<String, String>();
		for (String env : envvars) {
			String[] keyval = env.split("=");
			if (keyval[0] != null && keyval[1] != null) {
				envMap.put(keyval[0].trim(), keyval[1].trim());
			}
		}
		bwk8s.setK8sEnvVariables(envMap);

		return bwk8s;
	}

	/*private void addSeperator(Composite parent) {
		Label horizontalLine = new Label(container, SWT.SEPARATOR | SWT.HORIZONTAL | SWT.LINE_DASH);
		horizontalLine.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 4, 1));
		horizontalLine.setFont(parent.getFont());
	}*/
}

package com.tibco.bw.studio.maven.wizard;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.tibco.bw.studio.maven.modules.model.BWK8SModule;
import com.tibco.bw.studio.maven.modules.model.BWModule;
import com.tibco.bw.studio.maven.modules.model.BWModuleType;
import com.tibco.bw.studio.maven.modules.model.BWProject;
import com.tibco.bw.studio.maven.preferences.MavenProjectPreferenceHelper;
import com.tibco.bw.studio.maven.preferences.MavenPropertiesFileDefaults;

public class WizardPageK8S extends WizardPage {
	private Composite container;
	
	@SuppressWarnings("unused")
	private BWProject project;
	private Text rcName;
	private Spinner numOfReplicas;
	private Text serviceName;
	private Text serviceType;
	private Text containerPort;
	private Text k8sNamespace;
	//private Text k8sEnvVars;
	private Table tableEnvVars;
	private Button provideYmlResources;
	private Text yamlResources;
	private Button browseButton;
	private Label resourceLabel;
	private FileInputStream devPropfile = null;
	Map<String, String> properties= new HashMap();
	private StringBuilder envStr = new StringBuilder();
	private int textHeight = 18;

	protected WizardPageK8S(String pageName, BWProject project) {
		super(pageName);
		this.project = project;
		setTitle("Kubernetes Configuration for  TIBCO BusinessWorks Container Edition Application");
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
	
	private void setK8SPOMFields() {
		File devfile = new File(getWorkspacepath() + File.separator
				+ "k8s-dev.properties");
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
					String s = "fabric8.env.";
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
		
		/*Label lLabel = new Label(container, SWT.NONE);
		lLabel.setText("Kubernetes configuration:");
		GridData lData = new GridData(200, textHeight+5);
		lData.horizontalSpan = 4;
		lLabel.setLayoutData(lData);*/

		Group kubeConfig = new Group(container, SWT.SHADOW_ETCHED_IN);
		kubeConfig.setText("Kubernetes Configuration : ");
		GridData lData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		lData.horizontalSpan = 4;
		kubeConfig.setLayoutData(lData);
		kubeConfig.setLayout(new GridLayout(4, false));
		
		Label l1Label = new Label(kubeConfig, SWT.NONE);
		l1Label.setText("");
		GridData l1Data = new GridData(20, textHeight);
		l1Data.horizontalSpan = 4;
		l1Label.setLayoutData(l1Data);

		Label rcLabel = new Label(kubeConfig, SWT.NONE);
		rcLabel.setText("Deployment Name");

		rcName = new Text(kubeConfig, SWT.BORDER | SWT.SINGLE);
		if(properties.containsKey("fabric8.label.container"))
			rcName.setText(properties.get("fabric8.label.container"));
		else
			rcName.setText(MavenProjectPreferenceHelper.INSTANCE.getDefaultKubernetes_DeploymentName(MavenPropertiesFileDefaults.INSTANCE.getDefaultKubernetes_DeploymentName("bwce-sample")));
		GridData rcData = new GridData(200, textHeight);
		rcName.setLayoutData(rcData);

		
		
		Label replicaLabel = new Label(kubeConfig, SWT.NONE);
		replicaLabel.setText("No of Replicas");
		
		//numOfReplicas = new Text(kubeConfig, SWT.BORDER | SWT.SINGLE);
		numOfReplicas = new Spinner (kubeConfig, SWT.BORDER);
		numOfReplicas.setMinimum(1);
		if(properties.containsKey("fabric8.replicas"))
			numOfReplicas.setSelection(Integer.parseInt(properties.get("fabric8.replicas")));
		else
			numOfReplicas.setSelection(Integer.parseInt(MavenProjectPreferenceHelper.INSTANCE.getDefaultKubernetes_NoOfReplicas(MavenPropertiesFileDefaults.INSTANCE.getDefaultKubernetes_NoOfReplicas("1"))));
		GridData replicaData = new GridData(50, textHeight);
		numOfReplicas.setLayoutData(replicaData);
		

		Label srvNameLabel = new Label(kubeConfig, SWT.NONE);
		srvNameLabel.setText("Service Name");

		serviceName = new Text(kubeConfig, SWT.BORDER | SWT.SINGLE);
		if(properties.containsKey("fabric8.service.name"))
			serviceName.setText(properties.get("fabric8.service.name"));
		else
			serviceName.setText(MavenProjectPreferenceHelper.INSTANCE.getDefaultKubernetes_ServiceName(MavenPropertiesFileDefaults.INSTANCE.getDefaultKubernetes_ServiceName("bwce-sample-service")));
		GridData serviceNamData = new GridData(200, textHeight);
		serviceName.setLayoutData(serviceNamData);

		Label srvTypeLabel = new Label(kubeConfig, SWT.NONE);
		srvTypeLabel.setText("Service Type");

		serviceType = new Text(kubeConfig, SWT.BORDER | SWT.SINGLE);
		if(properties.containsKey("fabric8.service.type"))
			serviceType.setText(properties.get("fabric8.service.type"));
		else
			serviceType.setText(MavenProjectPreferenceHelper.INSTANCE.getDefaultKubernetes_ServiceType(MavenPropertiesFileDefaults.INSTANCE.getDefaultKubernetes_ServiceType("LoadBalancer")));
		GridData serviceTypeData = new GridData(250, textHeight);
		serviceType.setLayoutData(serviceTypeData);

		Label contPortLabel = new Label(kubeConfig, SWT.NONE);
		contPortLabel.setText("Container Port");

		containerPort = new Text(kubeConfig, SWT.BORDER | SWT.SINGLE);
		if(properties.containsKey("fabric8.service.containerPort"))
			containerPort.setText(properties.get("fabric8.service.containerPort"));
		else
			containerPort.setText(MavenProjectPreferenceHelper.INSTANCE.getDefaultKubernetes_ContainerPort(MavenPropertiesFileDefaults.INSTANCE.getDefaultKubernetes_ContainerPort("8080")));
		GridData contPortData = new GridData(50, textHeight);
		containerPort.setLayoutData(contPortData);

		Label namespaceLabel = new Label(kubeConfig, SWT.NONE);
		namespaceLabel.setText("K8S Namespace");

		k8sNamespace = new Text(kubeConfig, SWT.BORDER | SWT.SINGLE);
		if(properties.containsKey("fabric8.apply.namespace"))
			k8sNamespace.setText(properties.get("fabric8.apply.namespace"));
		else
			k8sNamespace.setText(MavenProjectPreferenceHelper.INSTANCE.getDefaultKubernetes_K8SNamespace(MavenPropertiesFileDefaults.INSTANCE.getDefaultKubernetes_K8SNamespace("default")));
		GridData namespcData = new GridData(250, textHeight);
		k8sNamespace.setLayoutData(namespcData);

		Label provideResourcesLabel = new Label(kubeConfig, SWT.NONE);
		provideResourcesLabel.setText("Provide the YAML Resources");
		GridData provideResData = new GridData(50, textHeight);
		provideYmlResources= new Button(kubeConfig, SWT.CHECK);
		provideYmlResources.setSelection(false);
		provideYmlResources.setLayoutData(provideResData);
		
		resourceLabel = new Label(kubeConfig, SWT.NONE);
		resourceLabel.setText("YAML Resources location");
		
		Composite innerContainer = new Composite(kubeConfig, SWT.NONE);
		GridLayout innerLayout = new GridLayout();
		innerContainer.setLayout(innerLayout);
		innerLayout.numColumns = 2;
		
		GridData resourceData = new GridData(200, textHeight);
		
		yamlResources = new Text(innerContainer, SWT.BORDER | SWT.SINGLE);
		yamlResources.setLayoutData(resourceData);
		
		browseButton = new Button(innerContainer, SWT.PUSH|SWT.ICON_SEARCH);
		browseButton.setText("Browse ...");
		
		//Image image = innerContainer.getDisplay().getSystemImage(SWT.ICON_SEARCH);
		//browseButton.setImage(image);
		browseButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				DirectoryDialog dialog = new DirectoryDialog(new Shell());
				String path = dialog.open();
				if (path != null) {

					yamlResources.setText(path);

				}
			}
		});
		
		
		resourceLabel.setVisible(false);
		browseButton.setVisible(false);
		yamlResources.setVisible(false);



		provideYmlResources.addSelectionListener(new SelectionListener(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(provideYmlResources.getSelection()){
				resourceLabel.setVisible(true);
				browseButton.setVisible(true);
				yamlResources.setVisible(true);
				}
				else{
					resourceLabel.setVisible(false);
					browseButton.setVisible(false);
					yamlResources.setVisible(false);
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				

			}
		});

		
		Label envVarsLabel = new Label(kubeConfig, SWT.NONE);
		envVarsLabel.setText("Environment Variables");

		/*k8sEnvVars = new Text(kubeConfig, SWT.BORDER | SWT.SINGLE);
		if(devfile.exists()){
			if (envStr.length() != 0)
				k8sEnvVars.setText(envStr.substring(0, envStr.length() - 1));
		}
		else
			k8sEnvVars.setText(MavenProjectPreferenceHelper.INSTANCE.getDefaultKubernetes_EnvVars(MavenPropertiesFileDefaults.INSTANCE.getDefaultKubernetes_EnvVars("APP_CONFIG_PROFILE=docker, abc=xyz")));
		GridData envvarData = new GridData(400, textHeight);
		envvarData.horizontalSpan = 3;
		k8sEnvVars.setLayoutData(envvarData);*/
		
		tableEnvVars = new Table (kubeConfig, SWT.MULTI | SWT.BORDER | SWT.FULL_SELECTION);
		tableEnvVars.setLinesVisible(true);
		tableEnvVars.setHeaderVisible(true);
		GridData data = new GridData(SWT.FILL, SWT.NONE, true, false);
		data.heightHint = 150;
		data.horizontalSpan = 2;
		tableEnvVars.setLayoutData(data);
		String[] titles = {"Variable Name            ", "Value                                                     "};
		for (String title : titles) {
			TableColumn column = new TableColumn (tableEnvVars, SWT.NONE);
			column.setText (title);
			column.setResizable(true);
			//column.setWidth(tableEnvVars.getBounds().width / 2);
		}
		
		String k8sEnvVars = "";
		if(devfile.exists()){
			if (envStr.length() != 0)
				k8sEnvVars = envStr.substring(0, envStr.length() - 1);
		}
		else
			k8sEnvVars = MavenProjectPreferenceHelper.INSTANCE.getDefaultKubernetes_EnvVars(MavenPropertiesFileDefaults.INSTANCE.getDefaultKubernetes_EnvVars("APP_CONFIG_PROFILE=docker"));
		
		String[] vars = k8sEnvVars.split(",");
		for(String var : vars)
		{
			String[] varItem = var.split("=");
			TableItem item = new TableItem (tableEnvVars, SWT.NONE);
			if(varItem.length == 2)
			{
			item.setText (0, (varItem[0] != null ? varItem[0].trim() : ""));
			item.setText (1, (varItem[1] != null ? varItem[1].trim() : ""));
			}
		}
	
		for (int i=0; i<titles.length; i++) {
			tableEnvVars.getColumn (i).pack ();
		}
		
		final TableEditor editor = new TableEditor(tableEnvVars);
		//The editor must have the same size as the cell and must
		//not be any smaller than 50 pixels.
		editor.horizontalAlignment = SWT.LEFT;
		editor.grabHorizontal = true;
		editor.minimumWidth = 50;
		// editing the second column
		final int EDITABLECOLUMN = 1;

		tableEnvVars.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseUp(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseDown(MouseEvent e) {
				// Clean up any previous editor control
				Control oldEditor = editor.getEditor();
				if (oldEditor != null)
					oldEditor.dispose();

				TableItem item = (tableEnvVars.getSelection().length > 0 ? tableEnvVars.getSelection()[0] : null);
				if(item == null)
					return;
				
				int column = EDITABLECOLUMN;
				Point pt = new Point (e.x, e.y);
				for(int i=0;i<tableEnvVars.getColumnCount();i++)
				{
					Rectangle rect = item.getBounds (i);
					if (rect.contains (pt)) {
						column = i;
					}
				}
				// The control that will be the editor must be a child of the Table
				final Text newEditor = new Text(tableEnvVars, SWT.NONE);
				newEditor.setText(item.getText(column));
				newEditor.setData(column);
				newEditor.addModifyListener(new ModifyListener() {
					
					@Override
					public void modifyText(ModifyEvent arg0) {
						Text text = (Text) editor.getEditor();
						int column = (int) newEditor.getData();
						editor.getItem().setText(column, text.getText());
					}
				});
				newEditor.selectAll();
				newEditor.setFocus();
				editor.setEditor(newEditor, item, column);

				
			}
			
			@Override
			public void mouseDoubleClick(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
					
		Composite buttonComp = new Composite(kubeConfig, SWT.NONE | SWT.TOP);
		GridData gd = new GridData(150, 150);
		buttonComp.setLayoutData(gd);
		buttonComp.setLayout(new GridLayout(1, false));
		Button addVar = new Button(buttonComp, SWT.PUSH);
		Image imageAdd = new Image(buttonComp.getDisplay(),  getClass().getClassLoader().getResourceAsStream("icons/add_16_16.png"));
		addVar.setImage(imageAdd);
		//addVar.setText("Add");
		addVar.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				TableItem item = new TableItem (tableEnvVars, SWT.NONE);
				item.setText (0, "ENV_VAR");
				item.setText (1, "value");
			}
		});
		
		Button removeVar = new Button(buttonComp, SWT.PUSH);
		Image imageRemove = new Image(buttonComp.getDisplay(),  getClass().getClassLoader().getResourceAsStream("icons/remove_16_16.png"));
		removeVar.setImage(imageRemove);
		//removeVar.setText("Remove");
		removeVar.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				tableEnvVars.remove(tableEnvVars.getSelectionIndices());
				for(Control ctrl : tableEnvVars.getChildren())
					if(ctrl.getClass() == Text.class)
						ctrl.dispose();
				tableEnvVars.redraw();
			}
		});

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
		bwk8s.setServiceType(serviceType.getText());

		/*List<String> envvars = new ArrayList<String>();
		if (k8sEnvVars.getText() != null && !k8sEnvVars.getText().isEmpty()) {
			envvars = Arrays.asList(k8sEnvVars.getText().split("\\s*,\\s*"));
		}

		Map<String, String> envMap = new HashMap<String, String>();
		for (String env : envvars) {
			String[] keyval = env.split("=");
			if (keyval[0] != null && keyval[1] != null) {
				envMap.put(keyval[0].trim(), keyval[1].trim());
			}
		}*/
		
		Map<String, String> envMap = new HashMap<String, String>();
		for(TableItem item : tableEnvVars.getItems()){
			envMap.put(item.getText(0).trim(), item.getText(1).trim());
		}
		bwk8s.setK8sEnvVariables(envMap);
		
		if(yamlResources.isVisible()){
			bwk8s.setResourcesLocation(yamlResources.getText());
		}
		

		return bwk8s;
	}

	/*private void addSeperator(Composite parent) {
		Label horizontalLine = new Label(container, SWT.SEPARATOR | SWT.HORIZONTAL | SWT.LINE_DASH);
		horizontalLine.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 4, 1));
		horizontalLine.setFont(parent.getFont());
	}*/
	@Override
	public boolean canFlipToNextPage() 
	{
		return false;
	}
	
	@Override
	public void performHelp() {
		// TODO Auto-generated method stub
		super.performHelp();
		
		try {
			PlatformUI.getWorkbench().getBrowserSupport().getExternalBrowser().openURL(new URL("https://github.com/TIBCOSoftware/bw6-plugin-maven/wiki"));
		} catch (PartInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

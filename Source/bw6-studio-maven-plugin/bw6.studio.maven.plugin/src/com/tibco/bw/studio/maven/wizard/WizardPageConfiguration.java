package com.tibco.bw.studio.maven.wizard;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.eclipse.core.internal.resources.Project;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.tibco.amf.sca.policy.intent.helpers.IntentApplicability.Applicability;
import com.tibco.bw.studio.maven.helpers.ManifestParser;
import com.tibco.bw.studio.maven.helpers.ModuleHelper;
import com.tibco.bw.studio.maven.modules.model.BWApplication;
import com.tibco.bw.studio.maven.modules.model.BWDeploymentInfo;
import com.tibco.bw.studio.maven.modules.model.BWModule;
import com.tibco.bw.studio.maven.modules.model.BWModuleType;
import com.tibco.bw.studio.maven.modules.model.BWParent;
import com.tibco.bw.studio.maven.modules.model.BWProject;
import com.tibco.bw.studio.maven.modules.model.BWProjectType;
import com.tibco.bw.studio.maven.modules.model.BWTestInfo;
import com.tibco.bw.studio.maven.plugin.Activator;

public class WizardPageConfiguration extends WizardPage {
	private BWProject project;
	private Text appGroupId;
	private Text appArtifactId;
	private Text appVersion;
	private Composite container;
	

	private Text tibcoHome;
	private Text bwHome;
	private Text engineDebugPort;
	private Button runTests;
	private Button failIfSkip;
	private int textHeight = 18;


	private Map<String, Button> buttonMap = new HashMap<String, Button>();

	private Combo addDeploymentConfig;

	
	public WizardPageConfiguration(String pageName, BWProject project) {
		super(pageName);
		this.project = project;
		setTitle("Maven Configuration Details for TIBCO BusinessWorks(TM) Application");
		if(project.getType() == BWProjectType.Application){
			setDescription("Enter the GroupId and ArtifactId for Maven POM File generation.");


			BWApplication application = (BWApplication) ModuleHelper.getApplication(project.getModules());

			if( null!=application && application.getProjectType() != null )
			{
				MavenWizardContext.INSTANCE.setSelectedType( application.getProjectType() );
			}
			else if(null!=application)
			{
				BWDeploymentInfo info = application.getDeploymentInfo();
				if (info.isDeployToAdmin()) {
					MavenWizardContext.INSTANCE.setSelectedType( BWProjectTypes.AppSpace);
				}
				else
				{
					MavenWizardContext.INSTANCE.setSelectedType( BWProjectTypes.None);
				}
			}
		}
		

	}

	@Override
	public IWizardPage getNextPage() 
	{
		switch( MavenWizardContext.INSTANCE.getSelectedType() )
		{
			case AppSpace:
				return MavenWizardContext.INSTANCE.getEnterprisePage();		
		
			case PCF:
				return MavenWizardContext.INSTANCE.getPCFPage();
				
			case Docker:
				return MavenWizardContext.INSTANCE.getDockerPage();
				
			case TCI:
				return MavenWizardContext.INSTANCE.getTCIPage();
				
			default:
				break;		
		}
		
		return null;
		
	}
	
	
	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);

		Job job = new Job("Validating Page") {
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				try {
					Thread.sleep(300);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				final Display display = PlatformUI.getWorkbench().getDisplay();
				display.syncExec(new Runnable() {

					@Override
					public void run() {
						BWDeploymentInfo info = ((BWApplication) ModuleHelper.getApplication(project.getModules())).getDeploymentInfo();
						if ( MavenWizardContext.INSTANCE.getSelectedType() != BWProjectTypes.None ||  info.isDeployToAdmin()) {
							MavenWizardContext.INSTANCE.getNextButton().setEnabled(true);
						} else {
							MavenWizardContext.INSTANCE.getNextButton().setEnabled(false);
						}
					}
				});
				return Status.OK_STATUS;
			}
		};

		if (visible) {
			job.schedule();
		}
	}

	@Override
	public void createControl(Composite parent) {
	

		container = new Composite(parent, SWT.NONE);

		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 1;

		addNotes();
		addSeperator(parent);
		setApplicationPOMFields();
		//addSeperator(parent);
		
		setTestFields();
		
		//addSeperator(parent);
		
		if(project.getType() == BWProjectType.Application){
			setDeploymentComboBox();
			addSeperator(parent);
		
			Label label = new Label(container, SWT.NONE);
			label.setText("Selected Projects : ");
			GridData versionData = new GridData();
			versionData.horizontalSpan = 4;
			label.setLayoutData(versionData);
	
			createModulesTable();
		}
		
		setControl(container);
		setPageComplete(true);
	}

	private void addNotes() {
		Group noteGroup = new Group(container, SWT.SHADOW_ETCHED_IN);
		noteGroup.setText("Note : ");
		noteGroup.setLayout(new GridLayout(1, false));
		GridData noteData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		noteData.horizontalSpan = 4;
		noteGroup.setLayoutData(noteData);
		
		Label label = new Label(noteGroup, SWT.NONE);
		label.setText("- POM files will be generated for Projects listed below and Parent POM file will be generated aggregating the Projects. \r\n"
				+ "- Maven ArtifactId and Bundle-SymbolicName should be same.\r\n"
				+ "- Maven Version and Bundle-Version should be same.\r\n"
				+ "- TIBCO Home and BW Home values are required to run the Unit tests defined in the Project.");
		label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
	}

	private void addSeperator(Composite parent) {
		Label horizontalLine = new Label(container, SWT.SEPARATOR | SWT.HORIZONTAL | SWT.LINE_DASH);
		horizontalLine.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 4, 1));
		horizontalLine.setFont(parent.getFont());
	}

	private void setDeploymentComboBox() 
	{
		Composite innerContainer = new Composite(container, SWT.NONE);

		GridLayout layout = new GridLayout();
		innerContainer.setLayout(layout);
		layout.numColumns = 2;

		Label label = new Label(innerContainer, SWT.NONE);
		label.setText("Deploy Options: ");

		addDeploymentConfig = new Combo(innerContainer, SWT.DROP_DOWN| SWT.BORDER);
		
		
		for( int i = 0 ; i < MavenWizardContext.INSTANCE.getProjectTypes().size() ; i++ )	
		{
			BWProjectTypes types  = MavenWizardContext.INSTANCE.getProjectTypes().get(i);
			if( MavenWizardContext.INSTANCE.getSelectedType() == types)
			{
				addDeploymentConfig.select(i);
				addDeploymentConfig.setText( types.name());
			}
			addDeploymentConfig.add(types.name() );
			
		}
		
		if( MavenWizardContext.INSTANCE.getSelectedType() == null )
		{
			addDeploymentConfig.select(0);
		}
			
		
	
		addDeploymentConfig.addSelectionListener(new SelectionListener() 
		{

			@Override
			public void widgetSelected(SelectionEvent e) 
			{

				String selected = addDeploymentConfig.getText();

				switch (selected) {
				case "None":
					MavenWizardContext.INSTANCE.setSelectedType(BWProjectTypes.None);
					MavenWizardContext.INSTANCE.getNextButton().setEnabled(false);
				break;

				case "AppSpace":
					MavenWizardContext.INSTANCE.setSelectedType(BWProjectTypes.AppSpace);
					MavenWizardContext.INSTANCE.getNextButton().setEnabled(true);
				break;
				
				case "PCF":
					MavenWizardContext.INSTANCE.setSelectedType(BWProjectTypes.PCF);
					MavenWizardContext.INSTANCE.getNextButton().setEnabled(true);
				break;

				
				case "Docker":
					MavenWizardContext.INSTANCE.setSelectedType(BWProjectTypes.Docker);
					MavenWizardContext.INSTANCE.getNextButton().setEnabled(true);
				break;
				
				case "TCI":
					MavenWizardContext.INSTANCE.setSelectedType(BWProjectTypes.TCI);
					MavenWizardContext.INSTANCE.getNextButton().setEnabled(true);
				break;
				
				default:
					break;
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {

			}
		});
		}
	
	private void setTestFields()
	{
	
		BWTestInfo info = null;
		for( BWModule module : project.getModules() )
		{
			if( module.getType() == BWModuleType.Application )
			{
				info = ((BWApplication)module).getTestInfo();
				break;
			}
		}
		if(null != info){
			Group innerContainer = new Group(container, SWT.SHADOW_ETCHED_IN);
			innerContainer.setText("Unit Test Configuration : ");
			GridLayout layout = new GridLayout();
			innerContainer.setLayout(layout);
			layout.numColumns = 2;
			innerContainer.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
			

			//		Label label = new Label(container, SWT.NONE);
			//		label.setText("Note* : Tibco Home and BW Home values are required to run the Unit-Tests on the Project");

			/*Label label = new Label(innerContainer, SWT.NONE);
			label.setText("Note : TIBCO Home and BW Home values are required to run the Unit-Tests defined in the Project.");
			GridData versionData = new GridData();
			versionData.horizontalSpan = 2;
			label.setLayoutData(versionData);*/

			Label runTestsLabel = new Label(innerContainer, SWT.NONE);

			runTestsLabel.setText("Skip Tests:");
			runTests = new Button(innerContainer, SWT.CHECK);
			runTests.setSelection( info.getSkipTests() != null && !info.getSkipTests().isEmpty() ? Boolean.parseBoolean(info.getSkipTests()) : false);



			Label skipLabel = new Label(innerContainer, SWT.NONE);
			skipLabel.setText("Fail if No Tests :");

			failIfSkip  = new Button(innerContainer, SWT.CHECK);
			failIfSkip.setSelection( false );


			Label tibcoHomeLabel = new Label(innerContainer, SWT.NONE);
			tibcoHomeLabel.setText("TIBCO Home : ");

			//find tibco home
			String tibcoHomePath = "";
			String bwHomePath = "";
			File bundleFile;
			try {
				//bundleFile = FileLocator.getBundleFile(Activator.getDefault().getBundle());
				bundleFile = FileLocator.getBundleFile(Platform.getBundle("org.eclipse.platform"));
				if(bundleFile != null)
				{
					if(bundleFile.getPath().contains("studio"))
					{
						tibcoHomePath = bundleFile.getPath().substring(0,bundleFile.getPath().indexOf(bundleFile.separator + "studio"));
						File bwFolder = new File(tibcoHomePath + bundleFile.separator + "bw");
						if(bwFolder == null || !bwFolder.exists() || !bwFolder.isDirectory())
							bwFolder = new File(tibcoHomePath + bundleFile.separator + "bwce");
						for(String folder : bwFolder.list())
						{
							if(isNumeric(folder))
							{
								bwHomePath = bundleFile.separator + bwFolder.getName() + bundleFile.separator + folder;
								break;
							}
						}						
					}
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		    
			
			tibcoHome = new Text(innerContainer, SWT.BORDER | SWT.SINGLE);
			tibcoHome.setText( info.getTibcoHome() != null && !info.getTibcoHome().isEmpty() ? info.getTibcoHome() : tibcoHomePath) ;
			GridData groupData = new GridData(200, textHeight);
			tibcoHome.setLayoutData(groupData);
			
			Label bwHomeLabel = new Label(innerContainer, SWT.NONE);
			bwHomeLabel.setText( "BW Home : ");

			bwHome = new Text(innerContainer, SWT.BORDER | SWT.SINGLE);
			bwHome.setText( info.getBwHome() != null && !info.getBwHome().isEmpty() ? info.getBwHome() : bwHomePath );
			GridData artifactData = new GridData(200, textHeight);
			bwHome.setLayoutData(artifactData);
			
			Label engineDebugPortLabel = new Label(innerContainer, SWT.NONE);
			engineDebugPortLabel.setText( "Engine Debug Port : ");

			engineDebugPort = new Text(innerContainer, SWT.BORDER | SWT.SINGLE);
			engineDebugPort.setText("8090");
			GridData engineDebugPortData = new GridData(200, textHeight);
			bwHome.setLayoutData(engineDebugPortData);

		}
		
	}
	

	public static boolean isNumeric(String str) {
		  return str.matches("\\d+(\\.\\d+)?"); 
	}
	
	private void setApplicationPOMFields() {
		Composite innerContainer = new Composite(container, SWT.NONE);

		GridLayout layout = new GridLayout();
		innerContainer.setLayout(layout);
		layout.numColumns = 2;

		BWModule module;
		switch(project.getType()){
			
			case SharedModule:{
				module = ModuleHelper.getSharedModule(project.getModules());
				break;
			}
		
			case Application:
				default:{
				module = ModuleHelper.getParentModule(project.getModules());
				break;
			}
		}
		
		Label groupLabel = new Label(innerContainer, SWT.NONE);
		groupLabel.setText("Group Id:");

		appGroupId = new Text(innerContainer, SWT.BORDER | SWT.SINGLE);
		File pomFile = module.getPomfileLocation();
		if(readModel(pomFile).getGroupId() != null)
			appGroupId.setText(readModel(pomFile).getGroupId());
		else
		appGroupId.setText(module.getGroupId());
		GridData groupData = new GridData(250, textHeight);
		appGroupId.setLayoutData(groupData);

		Label artifactLabel = new Label(innerContainer, SWT.NONE);
		String label;
		if(project.getType() == BWProjectType.SharedModule){
			label = "Artifact Id:";
		}else{
			label = "Parent Artifact Id";
		}
		artifactLabel.setText(label);

		appArtifactId = new Text(innerContainer, SWT.BORDER | SWT.SINGLE);
		appArtifactId.setText(module.getArtifactId());
		GridData artifactData = new GridData(250, textHeight);
		appArtifactId.setLayoutData(artifactData);
		if(project.getType() == BWProjectType.SharedModule){
			appArtifactId.setEditable(false);
		}
		
		Label versionLabel = new Label(innerContainer, SWT.NONE);
		versionLabel.setText("Version:");

		appVersion = new Text(innerContainer, SWT.BORDER | SWT.SINGLE);
		appVersion.setText(module.getVersion());
		GridData versionData = new GridData(120, textHeight);
//		versionData.horizontalSpan = 3;
		appVersion.setLayoutData(versionData);
		appVersion.setEditable(false);
	}
	
	protected Model readModel(File pomXmlFile) {
		Model model = null;
		try (Reader reader = new FileReader(pomXmlFile)) {
			MavenXpp3Reader xpp3Reader = new MavenXpp3Reader();
			if (pomXmlFile.length() != 0)
				model = xpp3Reader.read(reader);
			else
				model = new Model();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return model;
	}

	private void createModulesTable() {
		Table table = new Table(container, SWT.MULTI | SWT.BORDER
				| SWT.FULL_SELECTION);
		table.setLinesVisible(true);
		table.setHeaderVisible(true);

		//GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		GridData data = new GridData(SWT.FILL, SWT.TOP, true, false, 2, 1);
		data.horizontalSpan = 4;
		data.heightHint = 100;
		table.setLayoutData(data);

		String[] titles = { "Module Name", "Module Type", "ArtifactId" };
		for (int i = 0; i < titles.length; i++) {
			TableColumn column = new TableColumn(table, SWT.NONE);
			column.setText(titles[i]);
		}
		BWModule application = ModuleHelper
				.getApplication(project.getModules());

		TableItem applicationItem = new TableItem(table, SWT.BORDER);
		applicationItem.setText(0, application.getName());
		applicationItem.setText(1, "Application");
		applicationItem.setText(2, application.getArtifactId());
		// addCheckBox(table, applicationItem, application);

		BWModule appModule = ModuleHelper.getAppModule(project.getModules());

		TableItem appmoduleItem = new TableItem(table, SWT.BORDER);
		appmoduleItem.setText(0, appModule.getName());
		appmoduleItem.setText(1, "AppModule");
		appmoduleItem.setText(2, appModule.getArtifactId());
		// addCheckBox(table, appmoduleItem, appModule);

		for (BWModule module : project.getModules()) {
			if (module.getType() == BWModuleType.Application
					|| module.getType() == BWModuleType.AppModule
					|| module.getType() == BWModuleType.Parent) {
				continue;
			}
			TableItem item = new TableItem(table, SWT.BORDER);
			item.setText(0, module.getName());

			switch (module.getType()) {
			case SharedModule:
				item.setText(1, "Shared Module");
				break;

			case PluginProject:
				item.setText(1, "OSGi Project");
				break;
			}
			item.setText(2, module.getArtifactId());
		}

		for (int i = 0; i < titles.length; i++) {
			table.getColumn(i).pack();
		}
	}



	public BWProject getProject() {
		return project;
	}

	public BWProject getUpdatedProject() {
		for (BWModule module : project.getModules())
		{
			module.setGroupId(appGroupId.getText());
			if ( module.getType() == BWModuleType.Application) 
			{
				
				if( MavenWizardContext.INSTANCE.getSelectedType() == BWProjectTypes.AppSpace )
				{
					((BWApplication) module).getDeploymentInfo().setDeployToAdmin(true);
					
				}
				else
				{
					((BWApplication) module).getDeploymentInfo().setDeployToAdmin(false);
				}
				
				BWTestInfo info = ((BWApplication) module).getTestInfo();
				info.setTibcoHome( tibcoHome.getText() );
				info.setBwHome( bwHome.getText());
				info.setSkipTests( String.valueOf(runTests.getSelection()));
				info.setFailIfNoTests(String.valueOf(failIfSkip.getSelection()));
				info.setEngineDebugPort(engineDebugPort.getText());
				
			}		
			module.setOverridePOM(true);
			
		}

		if(project.getType() == BWProjectType.Application)
		{
			BWModule parent = ModuleHelper.getParentModule(project.getModules());
	
			if (!parent.getArtifactId().equals(appArtifactId.getText()) || !parent.getGroupId().equals(appGroupId.getText())) {
				parent.setArtifactId(appArtifactId.getText());
				((BWParent) parent).setValueChanged(true);
			}
		}
		return project;
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

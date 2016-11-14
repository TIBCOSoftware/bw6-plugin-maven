package com.tibco.bw.studio.maven.wizard;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

import com.tibco.bw.studio.maven.helpers.ManifestParser;
import com.tibco.bw.studio.maven.helpers.ModuleHelper;
import com.tibco.bw.studio.maven.modules.model.BWApplication;
import com.tibco.bw.studio.maven.modules.model.BWDeploymentInfo;
import com.tibco.bw.studio.maven.modules.model.BWModule;
import com.tibco.bw.studio.maven.modules.model.BWModuleType;
import com.tibco.bw.studio.maven.modules.model.BWParent;
import com.tibco.bw.studio.maven.modules.model.BWProject;
import com.tibco.zion.project.core.ContainerPreferenceProject;

public class WizardPageConfiguration extends WizardPage {
	private BWProject project;
	private Text appGroupId;
	private Text appArtifactId;
	private Text appVersion;
	private Button addDeploymentConfig;
	private Composite container;
	private String bwEdition;
	private Map<String, Button> buttonMap = new HashMap<String, Button>();
	
	public WizardPageConfiguration(String pageName , BWProject project) {
		super(pageName);
		this.project = project;		 
		setTitle("Maven Configuration Details for Plugin Code for Apache Maven and TIBCO BusinessWorks�");
		setDescription("Enter the GroupId and ArtifactId for Maven POM File generation. \nThe POM files will be generated for Projects listed below and a Parent POM file will be generated aggregating the Projects");	
	}

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);

		Job job = new Job( "Validating Page") {
			
			@Override
			protected IStatus run(IProgressMonitor monitor) 
			{
				
				try 
				{
					Thread.sleep( 300 );
				} catch (InterruptedException e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				final Display display = PlatformUI.getWorkbench().getDisplay();

				display.syncExec( new Runnable() 
				{
					
					@Override
					public void run() 
					{
						
						BWDeploymentInfo info = ((BWApplication) ModuleHelper.getApplication(project.getModules())).getDeploymentInfo();
						if(info.isDeployToAdmin() )
						{
							MavenWizardContext.INSTANCE.getNextButton().setEnabled( true);
						}
						else
						{
							MavenWizardContext.INSTANCE.getNextButton().setEnabled( false);

						}

					}
				});
				return Status.OK_STATUS;

				
			}
			
		};

		if( visible )
		{
			job. schedule();
		}
		
		
	}




	@Override
	public void createControl(Composite parent) 
	{
		
		try {
			Map<String,String> manifest = ManifestParser.parseManifest(project.getModules().get(0).getProject());
			if(manifest.containsKey("TIBCO-BW-Edition") && manifest.get("TIBCO-BW-Edition").equals("bwcf")){
				String targetPlatform = ContainerPreferenceProject.getCurrentContainer().getLabel();
				if(targetPlatform.equals("Cloud Foundry")){
					  bwEdition="cf";
				  }else{
					  bwEdition="docker";
				  }
			}else bwEdition="bw6";
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		container = new Composite(parent, SWT.NONE);

		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 1;

		addNotes();
		

		addSeperator(parent);

		setApplicationPOMFields();
		
		addSeperator(parent);
		
		setDeploymentCheckBox();
		
		addSeperator(parent);
		
		Label label = new Label(container, SWT.NONE);
		label.setText( "Note* : The POM File will be generated/updated(if exists) for following Projects." );
		GridData versionData = new GridData();
		versionData.horizontalSpan = 4;
		label.setLayoutData(versionData);

		
		createModulesTable();
		
		setControl(container);
		setPageComplete(true);


	}
	
	private void addNotes()
	{
		Label label = new Label(container, SWT.NONE);
		label.setText( "Note* : The Maven ArtifactId and the Bundle-SymbolicName should be same." );
		GridData versionData = new GridData();
		versionData.horizontalSpan = 4;
		label.setLayoutData(versionData);

		
		Label label1 = new Label(container, SWT.NONE);
		label1.setText( "Note* : The Maven Version and the Bundle-Version should be same." );
		versionData = new GridData();
		versionData.horizontalSpan = 4;
		label1.setLayoutData(versionData);

	}
	
	
	
	private void addSeperator(Composite parent) 
	{
		Label horizontalLine = new Label(container, SWT.SEPARATOR | SWT.HORIZONTAL | SWT.LINE_DASH);
		horizontalLine.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 4, 1));
		horizontalLine.setFont(parent.getFont());
	}
	
	
	private void setDeploymentCheckBox()
	{
		
		Composite innerContainer = new Composite(container, SWT.NONE);

		GridLayout layout = new GridLayout();
		innerContainer.setLayout(layout);
		layout.numColumns = 2;

		addDeploymentConfig = new Button(innerContainer, SWT.CHECK );
		
		BWDeploymentInfo info = ((BWApplication) ModuleHelper.getApplication(project.getModules())).getDeploymentInfo();
		
		addDeploymentConfig.setSelection( info.isDeployToAdmin() );
		
		addDeploymentConfig.addSelectionListener( new SelectionListener() 
		{
			
			@Override
			public void widgetSelected(SelectionEvent e) 
			{
				if( addDeploymentConfig.getSelection() )
				{
					MavenWizardContext.INSTANCE.getNextButton().setEnabled( true);
				}
				else
				{
					MavenWizardContext.INSTANCE.getNextButton().setEnabled( false );
				}
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {}
		});

		
		Label label = new Label(innerContainer, SWT.NONE );
		

		if(bwEdition.equals("cf"))
		{
			label.setText( "Deploy EAR to Cloud Foundry" );
		}
		else if(bwEdition.equals("docker"))
		{
			label.setText( "Deploy EAR to Docker" );
		}
		else
		{
			label.setText( "Deploy EAR to BW Administrator" );
		}

	}
	
	private void setApplicationPOMFields() 
	{
		
		Composite innerContainer = new Composite(container, SWT.NONE);

		GridLayout layout = new GridLayout();
		innerContainer.setLayout(layout);
		layout.numColumns = 4;

		BWParent parent = ModuleHelper.getParentModule( project.getModules() );
				
		Label groupLabel = new Label(innerContainer, SWT.NONE);
		groupLabel.setText("Group Id");

		appGroupId = new Text(innerContainer, SWT.BORDER | SWT.SINGLE);
		appGroupId.setText( parent.getGroupId() );
		GridData groupData = new GridData(200, 15);
		appGroupId.setLayoutData(groupData);
		
		Label artifactLabel = new Label(innerContainer, SWT.NONE);
		artifactLabel.setText( "Parent ArtifactId" );

		appArtifactId = new Text(innerContainer, SWT.BORDER | SWT.SINGLE);
		appArtifactId.setText(parent.getArtifactId() );
		GridData artifactData = new GridData(200, 15);
		appArtifactId.setLayoutData(artifactData);
		
		Label versionLabel = new Label(innerContainer, SWT.NONE);
		versionLabel.setText("Version");

		appVersion = new Text(innerContainer, SWT.BORDER | SWT.SINGLE);
		appVersion.setText(parent.getVersion());
		GridData versionData = new GridData(120, 15);
		versionData.horizontalSpan = 3;
		appVersion.setLayoutData(versionData);
		appVersion.setEditable( false);
		


	
		
	}

	
	private void createModulesTable()
	{
		
//		Composite innerContainer = new Composite(container, SWT.NONE);
//
//		GridLayout layout = new GridLayout();
//		innerContainer.setLayout(layout);
//		layout.numColumns = 3;

				
		Table table = new Table (container, SWT.MULTI | SWT.BORDER | SWT.FULL_SELECTION  );
		table.setLinesVisible (true);
		table.setHeaderVisible (true);
		
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		data.horizontalSpan = 4;
		table.setLayoutData(data);
		
		String[] titles = {" Module Name", "Module Type", "ArtifactId"  };
		for (int i=0; i<titles.length; i++) {
			TableColumn column = new TableColumn (table, SWT.NONE);
			column.setText (titles [i]);
		}	
		
		BWModule application =  ModuleHelper.getApplication( project.getModules() );
		
		TableItem applicationItem = new TableItem(table, SWT.BORDER );
		applicationItem.setText (0 , application.getName() );

		applicationItem.setText (1 , "Application");

		applicationItem.setText (2 ,  application.getArtifactId());
		
		//addCheckBox(table, applicationItem , application);
		
		
		BWModule appModule = ModuleHelper.getAppModule( project.getModules() );

		TableItem appmoduleItem = new TableItem(table, SWT.BORDER );
		appmoduleItem.setText (0 , appModule.getName() );

		appmoduleItem.setText (1 , "AppModule");

		appmoduleItem.setText (2 ,  appModule.getArtifactId());
		
		//addCheckBox(table, appmoduleItem , appModule);

		
		for( BWModule module : project.getModules() )
		{
			
			if( module.getType() == BWModuleType.Application || module.getType() == BWModuleType.AppModule ||  module.getType() == BWModuleType.Parent )
			{
				continue;
			}
			
			
			
			TableItem item = new TableItem(table, SWT.BORDER );
			item.setText (0 , module.getName() );

			switch( module.getType() )
			{
				case SharedModule:
					item.setText (1 , "Shared Module");
				break;	
				
				case PluginProject:
					item.setText(1 , "OSGi Project");
				break;
			}
			
			item.setText (2 ,  module.getArtifactId());
			
			//addCheckBox(table, item , module);
			
		}

	
		for (int i=0; i<titles.length; i++) {
			table.getColumn (i).pack ();
		}	

	}
	
	private void addCheckBox( Table table , TableItem item , BWModule module )
	{
		int minWidth = 0;
		item.setText(3 , "Override");
		Button b = new Button(table, SWT.CHECK);
		
		buttonMap.put( module.getArtifactId() , b);
		
		b.pack(); 
		TableEditor editor = new TableEditor(table); 
		Point size = b.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		editor.setEditor(b, item , 3); 
		editor.minimumWidth = size.x; 
		minWidth = Math.max(size.x, minWidth); 
		editor.minimumHeight = size.y; 
		editor.horizontalAlignment = SWT.RIGHT; 
		editor.verticalAlignment = SWT.CENTER; 


	}
	
	public BWProject getProject() 
	{
		return project;
	}

	public BWProject getUpdatedProject() 
	{
		for (BWModule module : project.getModules() )
		{
			module.setGroupId( appGroupId.getText() );
			
			if(bwEdition.equals("bw6") && module.getType() == BWModuleType.Application)
			{
				if( addDeploymentConfig.getSelection() )
				{
					((BWApplication)module).getDeploymentInfo().setDeployToAdmin( true);	
				}
				else
				{
					((BWApplication)module).setDeploymentInfo( new BWDeploymentInfo() );
					((BWApplication)module).getDeploymentInfo().setDeployToAdmin( false );	

				}
				
			}
			module.setOverridePOM(true);
			
			
			//module.setOverridePOM( buttonMap.containsKey( module.getArtifactId() ) ? (buttonMap.get(module.getArtifactId())).getSelection() : true  );
		}
		
		BWModule parent = ModuleHelper.getParentModule(project.getModules());
		
		if( !parent.getArtifactId().equals(appArtifactId.getText()  ) || !parent.getGroupId().equals(appGroupId.getText() ))
		{
			parent.setArtifactId( appArtifactId.getText() );
			((BWParent)parent).setValueChanged(true);
		}
		return project;
	}
}

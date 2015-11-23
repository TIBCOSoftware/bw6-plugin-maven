package com.tibco.bw.studio.maven.wizard;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import com.tibco.bw.studio.maven.helpers.ModuleHelper;
import com.tibco.bw.studio.maven.modules.BWModule;
import com.tibco.bw.studio.maven.modules.BWModuleType;
import com.tibco.bw.studio.maven.modules.BWProject;

public class WizardPage1 extends WizardPage
{
	
	private BWProject project;

	private Text appGroupId;
	private Text appArtifactId;
	private Text appVersion;
	
	private Composite container;


	private Map<String, Button> buttonMap = new HashMap<String, Button>();
	
	public WizardPage1( String pageName , BWProject project ) 
	{
		super(pageName);
		this.project = project;		 
		setTitle("Maven Configuration Details for Plugin Code for Apache Maven and TIBCO BusinessWorks™");
		setDescription("Enter the GroupId and ArtifactId for for Maven POM File generation. \nThe POM files will be generated for Projects listed below and a Parent POM file will be generated aggregating the Projects");	
	}
	
	@Override
	public void createControl(Composite parent) 
	{
		
		container = new Composite(parent, SWT.NONE);

		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 4;

		addNotes();
		
		addSeperator(parent);

		setApplicationPOMFields();
		
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
	
	
	private void setApplicationPOMFields() 
	{
		BWModule application = null;
		for( BWModule module : project.getModules() )
		{
			if( module.getType() == BWModuleType.Application )
			{
				application = module;
				break;
			}
		}
		
				
		Label groupLabel = new Label(container, SWT.NONE);
		groupLabel.setText("Group Id");

		appGroupId = new Text(container, SWT.BORDER | SWT.SINGLE);
		appGroupId.setText( "com.tibco.bw");
		GridData groupData = new GridData(200, 15);
		appGroupId.setLayoutData(groupData);

		Label artifactLabel = new Label(container, SWT.NONE);
		artifactLabel.setText( "Parent ArtifactId" );

		appArtifactId = new Text(container, SWT.BORDER | SWT.SINGLE);
		appArtifactId.setText(application.getArtifactId() + ".parent");
		GridData artifactData = new GridData(200, 15);
		appArtifactId.setLayoutData(artifactData);

		
		Label versionLabel = new Label(container, SWT.NONE);
		versionLabel.setText("Version");

		appVersion = new Text(container, SWT.BORDER | SWT.SINGLE);
		appVersion.setText(application.getVersion());
		GridData versionData = new GridData(120, 15);
		versionData.horizontalSpan = 3;
		appVersion.setLayoutData(versionData);
		appVersion.setEditable( false);
		
	}

	
	private void createModulesTable()
	{
		
				
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
			
			module.setOverridePOM(true);
			
			
			//module.setOverridePOM( buttonMap.containsKey( module.getArtifactId() ) ? (buttonMap.get(module.getArtifactId())).getSelection() : true  );
		}
		
		BWModule parent = ModuleHelper.getParentModule(project.getModules());
		parent.setArtifactId( appArtifactId.getText() );

		
		return project;
	}
	
	


}

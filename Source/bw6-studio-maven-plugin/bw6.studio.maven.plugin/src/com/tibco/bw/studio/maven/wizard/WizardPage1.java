package com.tibco.bw.studio.maven.wizard;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;

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


	public WizardPage1( String pageName , BWProject project ) 
	{
		super(pageName);
		this.project = project;		 
		setTitle("Maven Configuration Details");
		setDescription("Enter the information below for Maven POM File generation.");	
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
		GridData groupData = new GridData(120, 15);
		appGroupId.setLayoutData(groupData);

		Label artifactLabel = new Label(container, SWT.NONE);
		artifactLabel.setText( "ArtifactId" );

		appArtifactId = new Text(container, SWT.BORDER | SWT.SINGLE);
		appArtifactId.setText(application.getArtifactId());
		GridData artifactData = new GridData(120, 15);
		appArtifactId.setLayoutData(artifactData);
		appArtifactId.setEditable( false);

		
		Label versionLabel = new Label(container, SWT.NONE);
		versionLabel.setText("Version");

		appVersion = new Text(container, SWT.BORDER | SWT.SINGLE);
		appVersion.setText(application.getVersion());
		GridData versionData = new GridData(120, 15);
		versionData.horizontalSpan = 3;
		appVersion.setLayoutData(versionData);
		appVersion.setEditable( false);
		
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
		}
		
		return project;
	}


}

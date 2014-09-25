/*
 * Copyright (c) 2013-2014 TIBCO Software Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.tibco.bw.maven.wizard;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.tibco.bw.maven.utils.BWAppModuleInfo;
import com.tibco.bw.maven.utils.BWApplicationInfo;
import com.tibco.bw.maven.utils.BWDeploymentInfo;
import com.tibco.bw.maven.utils.BWProjectInfo;

public class WizardOne extends WizardPage 
{
	

	private BWProjectInfo bwProjectInfo;

	private Text tibcoHome;

	private Text appGroupId;
	private Text appArtifactId;
	private Text appVersion;

	private Text domain;
	private Text appspace;
	private Text appNode;

	private Text domainDesc;
	private Text appspaceDesc;
	private Text appNodeDesc;

	
	Button createDomain;
	Button createAppSpace;
	Button createAppNode;

	
	Button redeploy;
	
	Button deployToAdmin;

	private Text httpPort;
	private Text osgiPort;
	private Text agent;
	
	private Combo profile;
	
	private Composite container;

	protected WizardOne(String pageName) 
	{
		super(pageName);
		setTitle("Maven Configuration Details");
		setDescription("Enter the information below for Maven POM File generation. \nTo install the genrated EAR file to BWAdmin enter the Deployment configuration below.");	
	}

	
	@Override
	public void createControl(Composite parent) 
	{
		container = new Composite(parent, SWT.NONE);

		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 4;
		
		setTibcoHome();
		
		addSeperator(parent);

		setApplicationPOMFields();

		addSeperator(parent);

		addDeploymentBox();
		
		addSeperator(parent);

		
		addDeploymentFields();

		addSeperator(parent);
		
		addNodeInfo();
		
		setControl(container);
		setPageComplete(true);
	}

	private void addDeploymentBox()
	{
		deployToAdmin = new Button(container, SWT.CHECK);
		deployToAdmin.setSelection(true);
		deployToAdmin.setToolTipText("If this is checked the the Application will be deployed on Admin during Maven Install lifecycle event.");
		
		Label domainLabel = new Label(container, SWT.NONE);
		domainLabel.setText("Deploy the EAR to Admin. Enter the Deployment Configuration below");
		domainLabel.setToolTipText("If this is checked the the Application will be deployed on Admin during Maven Install lifecycle event.");
		
		GridData deployData = new GridData(350, 15);
		deployData.horizontalSpan = 3;
		
		domainLabel.setLayoutData(deployData);

		deployToAdmin.addSelectionListener(new SelectionListener()
		{
			
			@Override
			public void widgetSelected(SelectionEvent e) 
			{
				if( deployToAdmin.getSelection() )
				{
					domain.setEditable(true);
					
					appspace.setEditable(true);
					
					appNode.setEditable(true);
				}
				else
				{
					domain.setEditable(false);
					
					appspace.setEditable(false);
					
					appNode.setEditable(false);

				}
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) 
			{
				
			}
		});
	}

	private void addRedeployBox()
	{
		redeploy = new Button(container, SWT.CHECK);
		redeploy.setSelection(true);
		redeploy.setToolTipText("If this is checked the the Application will be redeployed if exists.");
		
		Label domainLabel = new Label(container, SWT.NONE);
		domainLabel.setText("Re Deploy the Application if exists.");
		domainLabel.setToolTipText("Re Deploy the Application if exists.");
		
		GridData deployData = new GridData(350, 15);
		deployData.horizontalSpan = 3;
		
		domainLabel.setLayoutData(deployData);

	}

	private void addDeploymentFields() 
	{

		addDomain();
		addAppSpace();
		addAppNode();
		addRedeployBox();
	}


	private void addDomain() 
	{
		Label domainLabel = new Label(container, SWT.NONE);
		domainLabel.setText("Domain");

		domain = new Text(container, SWT.BORDER | SWT.SINGLE);
		domain.setText("bwdomain");
		GridData domainData = new GridData(120, 15);
		domain.setLayoutData(domainData);

		Label domainDescLabel = new Label(container, SWT.NONE);
		domainDescLabel.setText("Description");

		domainDesc = new Text(container, SWT.BORDER | SWT.SINGLE);
		domainDesc.setText("");
		GridData domainDescData = new GridData(120, 15);
		domainDesc.setLayoutData(domainDescData);

	}

	private void addAppSpace() 
	{
		Label appspaceLabel = new Label(container, SWT.NONE);
		appspaceLabel.setText("AppSpace");

		appspace = new Text(container, SWT.BORDER | SWT.SINGLE);
		appspace.setText( bwProjectInfo.getAppInfo().getArtifactId() + "-AppSpace");
		GridData appspaceData = new GridData(120, 15);
		appspace.setLayoutData(appspaceData);

		
		Label appspaceDescLabel = new Label(container, SWT.NONE);
		appspaceDescLabel.setText("Description");

		appspaceDesc = new Text(container, SWT.BORDER | SWT.SINGLE);
		appspaceDesc.setText("");
		GridData appspaceDescData = new GridData(120, 15);
		appspaceDesc.setLayoutData(appspaceDescData);

	}

	private void addAppNode() 
	{
		Label appNodeLabel = new Label(container, SWT.NONE);
		appNodeLabel.setText("AppNode");

		appNode = new Text(container, SWT.BORDER | SWT.SINGLE);
		appNode.setText(bwProjectInfo.getAppInfo().getArtifactId() + "-AppNode");
		GridData appNodeData = new GridData(120, 15);
		appNode.setLayoutData(appNodeData);

		Label appnodeDescLabel = new Label(container, SWT.NONE);
		appnodeDescLabel.setText("Description");

		appNodeDesc = new Text(container, SWT.BORDER | SWT.SINGLE);
		appNodeDesc.setText("");
		GridData appnodeDescData = new GridData(120, 15);
		appNodeDesc.setLayoutData(appnodeDescData);


	}

	
	private void addNodeInfo() 
	{
//		Label osgiLabel = new Label(container, SWT.NONE);
//		osgiLabel.setText("OSGi Port");
//
//		osgiPort = new Text(container, SWT.BORDER | SWT.SINGLE);
//		osgiPort.setText("1112");
//		GridData osgiData = new GridData(120, 15);
//		osgiPort.setLayoutData(osgiData);

		Label httpLabel = new Label(container, SWT.NONE);
		httpLabel.setText("HTTP Port");

		httpPort = new Text(container, SWT.BORDER | SWT.SINGLE);
		httpPort.setText("9065");
		GridData httpData = new GridData(120, 15);
		httpPort.setLayoutData(httpData);
		
		Label agentLabel = new Label(container, SWT.NONE);
		agentLabel.setText("Agent");

		agent = new Text(container, SWT.BORDER | SWT.SINGLE);
		agent.setText("");
		GridData agentData = new GridData(120, 15);
		//agentData.horizontalSpan = 3;
		agent.setLayoutData(agentData);

		
		Label profileLabel = new Label(container, SWT.NONE);
		profileLabel.setText("Profile");

		profile = new Combo(container, SWT.BORDER | SWT.SINGLE);
		
		List<String> profiles = getProfiles(); 
		for(String name : profiles )
		{
			profile.add( name );
		}
		
		int index = getSelectedProfile(profiles);
		if( index != -1 )
		{
			profile.select(index);	
		}
		
		GridData profileData = new GridData(120, 15);
		//agentData.horizontalSpan = 3;
		profile.setLayoutData(profileData);

	}
	
	private int getSelectedProfile( List<String> profiles )
	{
		String os = System.getProperty("os.name");
		boolean isWindows = false;
		if (os.indexOf("Windows") != -1) 
		{
			isWindows = true;
		}
		
		if( isWindows && profiles.contains( "WindowsProfile.substvar" ) )
		{
			return profiles.indexOf("WindowsProfile.substvar");
		}
		
		else if( !isWindows && profiles.contains( "UnixProfile.substvar" ) )
		{
			return profiles.indexOf("WindowsProfile.substvar");
		}

		else if( profiles.size() == 1 )			
		{
			return 0;
		}
		
		return -1;

	}
	
	private List<String> getProfiles()
	{
		File project = new File(bwProjectInfo.getAppInfo().getProject().getLocationURI());
		File metainf = new File ( project , "META-INF" );
		File[] files = metainf.listFiles(new FileFilter() {
			
			public boolean accept(File pathname) {
				if (pathname.getName().indexOf(".substvar") != -1 )
				{
        			return true;
				}
				return false;
			}
		});
		
		List<String> list = new ArrayList<String>();
		for( File file : files )
		{
			list.add( file.getName());
		}
		
		return list;
	}
 
	private void addSeperator(Composite parent) 
	{
		Label horizontalLine = new Label(container, SWT.SEPARATOR
				| SWT.HORIZONTAL | SWT.LINE_DASH);
		horizontalLine.setLayoutData(new GridData(GridData.FILL, GridData.FILL,
				true, false, 4, 1));
		horizontalLine.setFont(parent.getFont());
	}

	private void setTibcoHome() 
	{

		Label homeLabel = new Label(container, SWT.NONE);
		homeLabel.setText("Tibco Home");

		tibcoHome = new Text(container, SWT.BORDER | SWT.SINGLE);
		tibcoHome.setText( bwProjectInfo.getTibcoHome() );
		GridData tibcoHomeData = new GridData(120, 15);
		tibcoHomeData.horizontalSpan = 3;
		tibcoHome.setLayoutData(tibcoHomeData);
	}


	private void setApplicationPOMFields() 
	{
		Label groupLabel = new Label(container, SWT.NONE);
		groupLabel.setText("Group Id");

		appGroupId = new Text(container, SWT.BORDER | SWT.SINGLE);
		appGroupId.setText( bwProjectInfo.getAppInfo().getGroupId());
		GridData groupData = new GridData(120, 15);
		appGroupId.setLayoutData(groupData);

		Label artifactLabel = new Label(container, SWT.NONE);
		artifactLabel.setText("Artifact Id");

		appArtifactId = new Text(container, SWT.BORDER | SWT.SINGLE);
		appArtifactId.setText(bwProjectInfo.getAppInfo().getArtifactId());
		GridData artifactData = new GridData(120, 15);
		appArtifactId.setLayoutData(artifactData);

		Label versionLabel = new Label(container, SWT.NONE);
		versionLabel.setText("Version");

		appVersion = new Text(container, SWT.BORDER | SWT.SINGLE);
		appVersion.setText(bwProjectInfo.getAppInfo().getVersion());
		GridData versionData = new GridData(120, 15);
		versionData.horizontalSpan = 3;
		appVersion.setLayoutData(versionData);
		appVersion.setEditable( false);
	}


	public BWProjectInfo getUpdatedBWInfo() 
	{
		
		bwProjectInfo.setTibcoHome(tibcoHome.getText());
		
		BWApplicationInfo info = bwProjectInfo.getAppInfo();
		info.setGroupId(appGroupId.getText());
		info.setArtifactId(appArtifactId.getText());
		info.setVersion(appVersion.getText());
		info.setTibcoHome(tibcoHome.getText());
		setDeploymentInfo();
		
		for(BWAppModuleInfo module : info.getAppModules() )
		{
			module.setTibcoHome(tibcoHome.getText());
		}
		
		return bwProjectInfo;
	}
	
	private void setDeploymentInfo()
	{
		BWDeploymentInfo info = bwProjectInfo.getAppInfo().getDeploymentInfo();
		info.setAppNode( appNode.getText() );
		info.setAppspace( appspace.getText() );
		info.setDomain( domain.getText() );
		
		info.setAppspaceDesc( appspaceDesc.getText() );
		info.setDomainDesc( domainDesc.getText() );
		info.setAppNodeDesc( appNodeDesc.getText() );
		

		info.setRedeploy(redeploy.getSelection());
		info.setDeployToAdmin( deployToAdmin.getSelection() );
		
		//info.setOsgiPort(osgiPort.getText());
		info.setHttpPort(httpPort.getText());
		info.setAgent(agent.getText());
		info.setProfile(profile.getText() );
	}

	public BWProjectInfo getBwProjectInfo() 
	{
		return bwProjectInfo;
	}

	public void setBwProjectInfo(BWProjectInfo bwProjectInfo)
	{
		this.bwProjectInfo = bwProjectInfo;
	}
}
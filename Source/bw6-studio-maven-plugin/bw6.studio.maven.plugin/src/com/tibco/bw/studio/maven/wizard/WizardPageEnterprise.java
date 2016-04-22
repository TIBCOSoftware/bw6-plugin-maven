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
import com.tibco.bw.studio.maven.modules.BWModule;
import com.tibco.bw.studio.maven.modules.BWModuleType;
import com.tibco.bw.studio.maven.modules.BWProject;

public class WizardPageEnterprise extends WizardPage
{
	private Composite container;
	private BWProject project;
	private String bwEdition;
	
	private Text domain;
	private Text appspace;
	private Text appNode;

	private Text domainDesc;
	private Text appspaceDesc;
	private Text appNodeDesc;

	
	private Button createDomain;
	private Button createAppSpace;
	private Button createAppNode;
 
	
	private Button redeploy;
	
	private Button deployToAdmin;

	private Text httpPort;
	private Text osgiPort;
	private Text agent;
	
	private Combo profile;

	private BWModule appModule;

	protected WizardPageEnterprise( String pageName , BWProject project ) 
	{
		super(pageName);
		this.project = project;		 
		setTitle("Maven Configuration Details for Apache Maven and TIBCO BusinessWorks™");
		setDescription("Enter the GroupId and ArtifactId for for Maven POM File generation. \nThe POM files will be generated for Projects listed below and a Parent POM file will be generated aggregating the Projects");	
	}

	@Override
	public void createControl(Composite parent) 
	{	
		container = new Composite(parent, SWT.NONE);

		GridLayout layout = new GridLayout( 4 , false );
		container.setLayout(layout);
		layout.numColumns = 4;

		appModule = ModuleHelper.getAppModule( project.getModules() );

		  bwEdition = "bw6";
		  try
		  {
			  Map<String,String> manifest = ManifestParser.parseManifest(project.getModules().get(0).getProject());
			  if(manifest.containsKey("TIBCO-BW-Edition") && manifest.get("TIBCO-BW-Edition").equals("bwcf"))
			  {
				  bwEdition="bwcf";
			  }
			  else
			  {
				  bwEdition="bw6";
			  }
		  } catch (Exception e) 
		  {
			  e.printStackTrace();
		  }
		  
		  addNotes();
		  addSeperator(parent);
		  addDeploymentFields();
		  
			setControl(container);
			setPageComplete(true);


	}
	
	private void addNotes()
	{
		Label label = new Label(container, SWT.NONE);
		label.setText( "Note* : Please Enter the Host and Port of the Machine where the BWAgent is running. \r\n "
			+	"Note* : Please Enter the Domain Name, AppSpace Name and AppNode Name. \r\n"
			+ "Note* : If the Domain, Appspace and AppNode do not exist then they will be created."
			+ "");
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

	
	public BWProject getUpdatedProject() 
	{
		for (BWModule module : project.getModules() )
		{
			
			if(bwEdition.equals("bw6") && module.getType() == BWModuleType.Application)
			{
				
			}
			module.setOverridePOM(true);
			
			
			//module.setOverridePOM( buttonMap.containsKey( module.getArtifactId() ) ? (buttonMap.get(module.getArtifactId())).getSelection() : true  );
		}
		
		
		return project;
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
		appspace.setText( appModule.getArtifactId() + "-AppSpace");
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
		appNode.setText(appModule.getArtifactId() + "-AppNode");
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
		File project = new File(appModule.getProject().getLocationURI());
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


}

package com.tibco.bw.studio.maven.wizard;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.cloudfoundry.client.lib.CloudCredentials;
import org.cloudfoundry.client.lib.CloudFoundryClient;
import org.cloudfoundry.client.lib.domain.CloudService;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.tibco.bw.studio.maven.modules.model.BWModule;
import com.tibco.bw.studio.maven.modules.model.BWModuleType;
import com.tibco.bw.studio.maven.modules.model.BWProject;
import com.tibco.bw.studio.maven.preferences.MavenProjectPreferenceHelper;
import com.tibco.bw.studio.maven.preferences.MavenPropertiesFileDefaults;

public class PCFLoginWizardPage extends WizardPage {
	private BWProject project;
	private Composite container;
	private CloudFoundryClient client;
	private BWModule appmodule;
	List<CloudService> pcfServices = new ArrayList<CloudService>();
	private Text target;
	private Text org;
	private Text space;
	private Text username;
	private Text password;

	protected PCFLoginWizardPage(String pageName, BWProject project) {
		super(pageName);
		this.project = project;
		setTitle("PCF Login");
		setDescription("Login to PCF instance");
	}

	@Override
	public void createControl(Composite parent) {
		container = new Composite(parent, SWT.NONE);

		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 2;

		for (BWModule module : project.getModules()) {
			if (module.getType() == BWModuleType.Application) {
				appmodule = module;
				break;
			}
		}

		Label targetLabel = new Label(container, SWT.NONE);
		targetLabel.setText("Target");

		target = new Text(container, SWT.BORDER | SWT.SINGLE);
		target.setText(appmodule.getBwpcfModule().getTarget());
		GridData targetData = new GridData(150, 15);
		target.setLayoutData(targetData);
		target.setEditable(false);

		Label usernameLabel = new Label(container, SWT.NONE);
		usernameLabel.setText("Username");

		username = new Text(container, SWT.BORDER | SWT.SINGLE);
		username.setText(MavenProjectPreferenceHelper.INSTANCE.getDefaultPCF_Username(MavenPropertiesFileDefaults.INSTANCE.getDefaultPCF_Username("admin")));
		GridData usernameData = new GridData(150, 15);
		username.setLayoutData(usernameData);

		Label pswdLabel = new Label(container, SWT.RIGHT);
		pswdLabel.setText("Password");

		password = new Text(container, SWT.BORDER | SWT.PASSWORD);
		password.setText("");
		GridData pswdData = new GridData(150, 15);
		password.setLayoutData(pswdData);

		Label orgLabel = new Label(container, SWT.NONE);
		orgLabel.setText("Org");

		org = new Text(container, SWT.BORDER | SWT.SINGLE);
		org.setText(appmodule.getBwpcfModule().getOrg());
		GridData orgData = new GridData(150, 15);
		org.setLayoutData(orgData);
		org.setEditable(false);

		Label spaceLabel = new Label(container, SWT.NONE);
		spaceLabel.setText("Space");

		space = new Text(container, SWT.BORDER | SWT.SINGLE);
		space.setText(appmodule.getBwpcfModule().getSpace());
		GridData spaceData = new GridData(150, 15);
		space.setLayoutData(spaceData);
		space.setEditable(false);

		setControl(container);
		setPageComplete(true);
	}

	public CloudFoundryClient login() {
		String targetURL = target.getText();
		String user = username.getText();
		String pswd = password.getText();
		String orgn = org.getText();
		String spacen = space.getText();

		CloudCredentials credentials = new CloudCredentials(user, pswd);
		client = createConnection(credentials, targetURL, orgn, spacen, true);

		return client;
	}

	private CloudFoundryClient createConnection(CloudCredentials credentials, String target, String org, String space, boolean trustSelfSignedCert) {
		CloudFoundryClient cloudFoundryClient = new CloudFoundryClient(credentials, getTargetURL(target), org, space, trustSelfSignedCert);
		return cloudFoundryClient;
	}

	private static URL getTargetURL(String target) {
		try {
			return URI.create(target).toURL();
		} catch (MalformedURLException e) {
			throw new RuntimeException("The target URL is not valid: " + e.getMessage());
		}
	}
}

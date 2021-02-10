package com.tibco.bw.studio.maven.wizard;

import java.io.File;
import java.util.Map;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;

import com.tibco.bw.studio.maven.helpers.ManifestParser;
import com.tibco.bw.studio.maven.helpers.ModuleHelper;
import com.tibco.bw.studio.maven.modules.model.BWApplication;
import com.tibco.bw.studio.maven.modules.model.BWModule;
import com.tibco.bw.studio.maven.modules.model.BWModuleType;
import com.tibco.bw.studio.maven.modules.model.BWProject;
import com.tibco.bw.studio.maven.modules.model.TCIDeploymentInfo;

public class WizardPageTCI extends WizardPage {

	private Composite container;
	private BWProject project;
	private String bwEdition;
	private BWModule appModule;
	private TCIDeploymentInfo info;
	//private Text variablesFile;
	private int textHeight = 18;
	private Spinner instanceCount;
	private Button forceOverwrite;
	private Button retainAppProps;

	protected WizardPageTCI(String pageName, BWProject project) {
		super(pageName);
		this.project = project;
		setTitle("Deployment Details for TIBCO BusinessWorks(TM) Application");
		setDescription("Please enter the deployment details for TIBCO Cloud Integration");
	}

	@Override
	public void createControl(Composite parent) {
		container = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(2, false);
		container.setLayout(layout);
		layout.numColumns = 4;
		appModule = ModuleHelper.getAppModule(project.getModules());
		info = ((BWApplication) ModuleHelper.getApplication(project
				.getModules())).getTCIDeploymentInfo();
		bwEdition = "bw6";
		try {
			Map<String, String> manifest = ManifestParser.parseManifest(project
					.getModules().get(0).getProject());
			if (manifest.containsKey("TIBCO-BW-Edition")
					&& manifest.get("TIBCO-BW-Edition").contains("bwcloud")) {
				bwEdition = "bwcloud";
			} else {
				bwEdition = "bw6";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		addNotes();
		addSeperator(parent);
		addDeploymentFields(parent);
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
		label.setText("- The EAR file will be deployed to the TIBCO Cloud Integration Platform\r\n"
				+ "- Please set the OAuth access token via TCI_PLATFORM_API_ACCESS_TOKEN environment variable.\r\n"
				+ "- Please set the TCI subscription ID via TCI_PLATFORM_SUBSCRIPTION_ID environment variable. Default is 0.\r\n"
				+ "- The wizard will generate Process Diagrams, default_appvar.json, default_enginevar.json and manifest.json file under application project.");
		label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
	}

	private void addSeperator(Composite parent) {
		Label horizontalLine = new Label(container, SWT.SEPARATOR
				| SWT.HORIZONTAL | SWT.LINE_DASH);
		horizontalLine.setLayoutData(new GridData(GridData.FILL, GridData.FILL,
				true, false, 4, 1));
		horizontalLine.setFont(parent.getFont());
	}

	private void addDeploymentFields(Composite parent) {

		Label instanceCountLabel = new Label(container, SWT.NONE);
		instanceCountLabel.setText("Instance Count");
		instanceCount = new Spinner(container, SWT.BORDER | SWT.SINGLE);
		instanceCount.setMinimum(0);
		instanceCount.setSelection(info.getInstanceCount());
		GridData gridData = new GridData(50, textHeight);
		gridData.horizontalSpan = 3;
		instanceCount.setLayoutData(gridData);

		/*Label variablesFileLabel = new Label(container, SWT.NONE);
		variablesFileLabel.setText("App Variables JSON File");
		variablesFile = new Text(container, SWT.BORDER | SWT.SINGLE);
		variablesFile.setText((info.getVariablesFile() == null ? "" : info
				.getVariablesFile()));
		GridData variablesFileData = new GridData(300, textHeight);
		variablesFile.setLayoutData(variablesFileData);
		Button browseButton = new Button(container, SWT.PUSH);
		browseButton.setText("Browse");
		browseButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				FileDialog dialog = new FileDialog(container.getShell(),
						SWT.OPEN);
				dialog.setFilterExtensions(new String[] { "*.json" });
				String jsonFilePath = dialog.open();
				variablesFile.setText(jsonFilePath);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				// TODO Auto-generated method stub

			}
		});
		
		Label filler = new Label(container, SWT.NONE);
*/		
		//force overwrite
		Label forceOverwriteLabel = new Label(container, SWT.NONE);
		forceOverwriteLabel.setText("Force Overwrite");
		forceOverwrite = new Button(container, SWT.CHECK);
		forceOverwrite.setSelection( info.isForceOverwrite());
		GridData gridDataForceOverwrite = new GridData(50, textHeight);
		gridDataForceOverwrite.horizontalSpan = 3;
		forceOverwrite.setLayoutData(gridDataForceOverwrite);

		
		//retain app props
		Label retainAppPropsLabel = new Label(container, SWT.NONE);
		retainAppPropsLabel.setText("Retain App Properties");
		retainAppProps = new Button(container, SWT.CHECK);
		retainAppProps.setSelection( info.isRetainAppProps());
		GridData gridDataretainAppProps = new GridData(50, textHeight);
		gridDataretainAppProps.horizontalSpan = 3;
		retainAppProps.setLayoutData(gridDataretainAppProps);

	}

	public BWProject getUpdatedProject() {
		for (BWModule module : project.getModules()) {
			if (bwEdition.equals("bwcloud")
					&& module.getType() == BWModuleType.Application) {
				TCIDeploymentInfo info = ((BWApplication) module)
						.getTCIDeploymentInfo();
				info.setInstanceCount(instanceCount.getSelection());
				info.setAppVariablesFile(module.getProject().getLocation().toString() + "/default_appvar.json");
				info.setEngineVariablesFile(module.getProject().getLocation().toString() + "/default_enginevar.json");
				info.setForceOverwrite(forceOverwrite.getSelection());
				info.setRetainAppProps(retainAppProps.getSelection());
			}
			module.setOverridePOM(true);
		}
		return project;
	}

	@Override
	public boolean canFlipToNextPage() {
		return false;
	}

}

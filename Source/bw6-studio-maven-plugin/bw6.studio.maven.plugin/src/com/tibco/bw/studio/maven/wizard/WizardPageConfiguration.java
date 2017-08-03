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
import org.eclipse.swt.widgets.Group;
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

/**
 * The Class WizardPageConfiguration.
 */
public class WizardPageConfiguration extends WizardPage {

	private static final String BW6 = "bw6";

	private static final String DOCKER = "docker";

	private static final String CF = "cf";

	private static final String CLOUD_FOUNDRY_PLATFORM_NAME = "Cloud Foundry";

	private static final String BWCF = "bwcf";

	private static final String TIBCO_BW_EDITION = "TIBCO-BW-Edition";

	/** The project. */
	private BWProject project;

	/** The app group id. */
	private Text appGroupId;

	/** The app artifact id. */
	private Text appArtifactId;

	/** The app version. */
	private Text appVersion;

	/** The add deployment config. */
	private Button addDeploymentConfig;

	/** The container. */
	private Composite container;

	/** The bw edition. */
	private String bwEdition;

	/** The button map. */
	private Map<String, Button> buttonMap = new HashMap<String, Button>();

	/**
	 * Instantiates a new wizard page configuration.
	 *
	 * @param pageName
	 *            the page name
	 * @param project
	 *            the project
	 */
	public WizardPageConfiguration(String pageName, BWProject project) {
		super(pageName);
		this.project = project;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.dialogs.DialogPage#getTitle()
	 */
	@Override
	public String getTitle() {
		return "Maven Configuration Details";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.dialogs.DialogPage#getDescription()
	 */
	@Override
	public String getDescription() {
		return "Enter the parameters required to generate the POM file.";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.dialogs.DialogPage#setVisible(boolean)
	 */
	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);

		Job job = new Job("Validating Page") {
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				try {
					Thread.sleep(300);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				final Display display = PlatformUI.getWorkbench().getDisplay();

				display.syncExec(new Runnable() {
					@Override
					public void run() {
						BWDeploymentInfo info = ((BWApplication) ModuleHelper
								.getApplication(project.getModules()))
								.getDeploymentInfo();
						if (info.isDeployToAdmin()) {
							MavenWizardContext.INSTANCE.getNextButton()
									.setEnabled(true);
						} else {
							MavenWizardContext.INSTANCE.getNextButton()
									.setEnabled(false);
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets
	 * .Composite)
	 */
	@Override
	public void createControl(Composite parent) {
		try {
			Map<String, String> manifest = ManifestParser.parseManifest(project
					.getModules().get(0).getProject());
			if (manifest.containsKey(TIBCO_BW_EDITION)
					&& manifest.get(TIBCO_BW_EDITION).equals(BWCF)) {
				String targetPlatform = ContainerPreferenceProject
						.getCurrentContainer().getLabel();
				if (targetPlatform.equals(CLOUD_FOUNDRY_PLATFORM_NAME)) {
					bwEdition = CF;
				} else {
					bwEdition = DOCKER;
				}
			} else
				bwEdition = BW6;
		} catch (Exception e) {
			e.printStackTrace();
		}

		container = new Composite(parent, SWT.NONE);

		GridLayout layout = new GridLayout(1, false);
		container.setLayout(layout);
		GridData data = new GridData(SWT.CENTER, SWT.TOP, true, true);

		// addNotes();
		// addSeperator(parent);

		Group pomFieldsGroup = new Group(container, SWT.NONE);
		data = new GridData(SWT.FILL, SWT.TOP, true, true);
		pomFieldsGroup.setLayoutData(data);
		pomFieldsGroup.setLayout(new GridLayout());
		pomFieldsGroup.setText("Application Details");

		setApplicationPOMFields(pomFieldsGroup);

		// addSeperator(parent);

		setDeploymentCheckBox();

		// addSeperator(parent);

		createModulesTable();

		Composite notesComposite = new Composite(container, SWT.NONE);
		notesComposite.setLayout(new GridLayout(2, false));
		Label label = new Label(notesComposite, SWT.NONE);
		label.setText("Note :");
		GridData versionData = new GridData();
		versionData.verticalSpan = 3;
		versionData.verticalAlignment = SWT.TOP;
		label.setLayoutData(versionData);

		Label label1 = new Label(notesComposite, SWT.NONE);
		label1.setText("Existing POM file will be overwritten!");

		Label label2 = new Label(notesComposite, SWT.NONE);
		label2.setText("The wizard will generate the POM files for each project listed above.");

		Label label3 = new Label(notesComposite, SWT.NONE);
		label3.setText("It will also generate a parent POM file aggregating all the projects.");

		setControl(container);
		setPageComplete(true);

	}

	/**
	 * Adds the notes.
	 */
	private void addNotes() {
		Label label = new Label(container, SWT.NONE);
		label.setText("Note* : The Maven ArtifactId and the Bundle-SymbolicName should be same.");
		GridData versionData = new GridData();
		versionData.horizontalSpan = 4;
		label.setLayoutData(versionData);

		Label label1 = new Label(container, SWT.NONE);
		label1.setText("Note* : The Maven Version and the Bundle-Version should be same.");
		versionData = new GridData();
		versionData.horizontalSpan = 4;
		label1.setLayoutData(versionData);

	}

	/**
	 * Adds the seperator.
	 *
	 * @param parent
	 *            the parent
	 */
	private void addSeperator(Composite parent) {
		Label horizontalLine = new Label(container, SWT.SEPARATOR
				| SWT.HORIZONTAL | SWT.LINE_DASH);
		horizontalLine.setLayoutData(new GridData(GridData.FILL, GridData.FILL,
				true, false, 4, 1));
		horizontalLine.setFont(parent.getFont());
	}

	/**
	 * Sets the deployment check box.
	 */
	private void setDeploymentCheckBox() {
		Composite innerContainer = new Composite(container, SWT.NONE);

		GridLayout layout = new GridLayout(2, false);
		innerContainer.setLayout(layout);

		addDeploymentConfig = new Button(innerContainer, SWT.CHECK);

		BWDeploymentInfo info = ((BWApplication) ModuleHelper
				.getApplication(project.getModules())).getDeploymentInfo();

		addDeploymentConfig.setSelection(info.isDeployToAdmin());

		addDeploymentConfig.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Button nextButton = MavenWizardContext.INSTANCE.getNextButton();
				if (addDeploymentConfig.getSelection()) {
					nextButton.setEnabled(true);
				} else {
					nextButton.setEnabled(false);
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		Label label = new Label(innerContainer, SWT.NONE);

		if (bwEdition.equals(CF)) {
			label.setText("Deploy EAR to Cloud Foundry");
		} else if (bwEdition.equals(DOCKER)) {
			label.setText("Deploy EAR to Docker");
		} else {
			label.setText("Deploy EAR to BW Administrator");
		}
	}

	/**
	 * Sets the application POM fields.
	 *
	 * @param parentComposite
	 *            the new application POM fields
	 */
	private void setApplicationPOMFields(Composite parentComposite) {
		Composite pomFieldsComposite = new Composite(parentComposite, SWT.NONE);

		GridLayout layout = new GridLayout();
		pomFieldsComposite.setLayout(layout);
		layout.numColumns = 3;

		BWParent parent = ModuleHelper.getParentModule(project.getModules());

		// Row #1
		Label groupLabel = new Label(pomFieldsComposite, SWT.NONE);
		groupLabel.setText("Group Id :");
		appGroupId = new Text(pomFieldsComposite, SWT.BORDER | SWT.SINGLE);
		appGroupId.setText(parent.getGroupId());
		GridData groupData = new GridData(200, 15);
		appGroupId.setLayoutData(groupData);
		Label message1 = new Label(pomFieldsComposite, SWT.NONE);
		message1.setText("");

		// Row #2
		Label artifactLabel = new Label(pomFieldsComposite, SWT.NONE);
		artifactLabel.setText("Parent ArtifactId :");

		appArtifactId = new Text(pomFieldsComposite, SWT.BORDER | SWT.SINGLE);
		appArtifactId.setText(parent.getArtifactId());
		GridData artifactData = new GridData(200, 15);
		appArtifactId.setLayoutData(artifactData);
		Label message2 = new Label(pomFieldsComposite, SWT.NONE);
		message2.setText("same as Bundle-SymbolicName");

		// Row #3
		Label versionLabel = new Label(pomFieldsComposite, SWT.NONE);
		versionLabel.setText("Version");
		Label appversionvalue = new Label(pomFieldsComposite, SWT.NONE);
		appversionvalue.setText(parent.getVersion());
		GridData versionData = new GridData(120, 15);
		appversionvalue.setLayoutData(versionData);
		Label message3 = new Label(pomFieldsComposite, SWT.NONE);
		message3.setText("same as Bundle-Version");
	}

	/**
	 * Creates the modules table.
	 */
	private void createModulesTable() {
		Table table = new Table(container, SWT.MULTI | SWT.BORDER
				| SWT.FULL_SELECTION);
		table.setLinesVisible(true);
		table.setHeaderVisible(true);

		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		data.horizontalSpan = 4;
		table.setLayoutData(data);

		String[] titles = { " Module Name", "Module Type", "ArtifactId" };
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

		// addCheckBox(table, applicationItem , application);

		BWModule appModule = ModuleHelper.getAppModule(project.getModules());

		TableItem appmoduleItem = new TableItem(table, SWT.BORDER);
		appmoduleItem.setText(0, appModule.getName());

		appmoduleItem.setText(1, "AppModule");

		appmoduleItem.setText(2, appModule.getArtifactId());

		// addCheckBox(table, appmoduleItem , appModule);

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

			// addCheckBox(table, item , module);

		}

		for (int i = 0; i < titles.length; i++) {
			table.getColumn(i).pack();
		}

	}

	/**
	 * Adds the check box.
	 *
	 * @param table
	 *            the table
	 * @param item
	 *            the item
	 * @param module
	 *            the module
	 */
	private void addCheckBox(Table table, TableItem item, BWModule module) {
		int minWidth = 0;
		item.setText(3, "Override");
		Button b = new Button(table, SWT.CHECK);

		buttonMap.put(module.getArtifactId(), b);

		b.pack();
		TableEditor editor = new TableEditor(table);
		Point size = b.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		editor.setEditor(b, item, 3);
		editor.minimumWidth = size.x;
		minWidth = Math.max(size.x, minWidth);
		editor.minimumHeight = size.y;
		editor.horizontalAlignment = SWT.RIGHT;
		editor.verticalAlignment = SWT.CENTER;
	}

	/**
	 * Gets the project.
	 *
	 * @return the project
	 */
	public BWProject getProject() {
		return project;
	}

	/**
	 * Gets the updated project.
	 *
	 * @return the updated project
	 */
	public BWProject getUpdatedProject() {
		for (BWModule module : project.getModules()) {
			module.setGroupId(appGroupId.getText());

			if (bwEdition.equals(BW6)
					&& module.getType() == BWModuleType.Application) {
				if (addDeploymentConfig.getSelection()) {
					((BWApplication) module).getDeploymentInfo()
							.setDeployToAdmin(true);
				} else {
					((BWApplication) module)
							.setDeploymentInfo(new BWDeploymentInfo());
					((BWApplication) module).getDeploymentInfo()
							.setDeployToAdmin(false);

				}

			}
			module.setOverridePOM(true);

			// module.setOverridePOM( buttonMap.containsKey(
			// module.getArtifactId() ) ?
			// (buttonMap.get(module.getArtifactId())).getSelection() : true );
		}

		BWModule parent = ModuleHelper.getParentModule(project.getModules());

		if (!parent.getArtifactId().equals(appArtifactId.getText())
				|| !parent.getGroupId().equals(appGroupId.getText())) {
			parent.setArtifactId(appArtifactId.getText());
			((BWParent) parent).setValueChanged(true);
		}
		return project;
	}
}

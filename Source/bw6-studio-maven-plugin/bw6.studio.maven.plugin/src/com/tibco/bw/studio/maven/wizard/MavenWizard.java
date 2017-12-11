package com.tibco.bw.studio.maven.wizard;

import java.util.Map;

import org.eclipse.jface.wizard.Wizard;

import com.tibco.bw.studio.maven.MavenPluginConstants;
import com.tibco.bw.studio.maven.helpers.ManifestParser;
import com.tibco.bw.studio.maven.helpers.ModuleHelper;
import com.tibco.bw.studio.maven.modules.model.BWApplication;
import com.tibco.bw.studio.maven.modules.model.BWProject;
import com.tibco.zion.project.core.ContainerPreferenceProject;

public class MavenWizard extends Wizard {

	protected WizardPageConfiguration configPage;
	protected WizardPagePCF pcfPage;
	protected WizardPageDocker dockerPage;
	protected WizardPageEnterprise enterprisePage;
	protected String bwEdition = MavenPluginConstants.BW6;
	private BWProject project;

	public MavenWizard(BWProject project) {
		super();
		this.project = project;
		setNeedsProgressMonitor(true);
		setWindowTitle(Messages.MavenWizard_WindowTitle);
	}

	@Override
	public void addPages() {
		try {
			Map<String, String> manifest = ManifestParser.parseManifest(project
					.getModules().get(0).getProject());
			if (manifest.containsKey(MavenPluginConstants.TIBCO_BW_EDITION)
					&& manifest.get(MavenPluginConstants.TIBCO_BW_EDITION).equals(MavenPluginConstants.BWCF)) {
				String targetPlatform = ContainerPreferenceProject
						.getCurrentContainer().getLabel();
				if (targetPlatform.equals(MavenPluginConstants.CLOUD_FOUNDRY_PLATFORM_NAME)) {
					bwEdition = MavenPluginConstants.CF;
				} else {
					bwEdition = MavenPluginConstants.DOCKER;
				}
			} else {
				bwEdition = MavenPluginConstants.BW6;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		configPage = new WizardPageConfiguration(Messages.MavenWizard_POMConfig, project);
		pcfPage = new WizardPagePCF(Messages.MavenWizard_PCF_Config, project);
		enterprisePage = new WizardPageEnterprise(Messages.MavenWizard_Deployemnt_Config,
				project);
		dockerPage = new WizardPageDocker(Messages.MavenWizard_Docker_Config,
				project);

		addPage(configPage);

		// Zoher - Just for UI changes. To be removed before checkin
		bwEdition = MavenPluginConstants.BW6;

		if (bwEdition.equals(MavenPluginConstants.BW6)) {
			addPage(enterprisePage);
		} else if (bwEdition.equals(MavenPluginConstants.CF)) {
			addPage(pcfPage);
		} else if (bwEdition.equals(MavenPluginConstants.DOCKER)) {
			addPage(dockerPage);
		}
	}

	@Override
	public boolean performFinish() {

		project = configPage.getUpdatedProject();

		// Zoher - Just for UI changes. To be removed before checkin
		bwEdition = MavenPluginConstants.DOCKER;

		if (bwEdition.equals(MavenPluginConstants.BW6)) {
			if (((BWApplication) ModuleHelper.getApplication(project
					.getModules())).getDeploymentInfo().isDeployToAdmin()) {
				if (((WizardPageEnterprise) enterprisePage).validate()) {
					enterprisePage.getUpdatedProject();
				} else {
					return false;
				}

			}

		} else if (bwEdition.equals(MavenPluginConstants.CF)) {
			pcfPage.getUpdatedProject();
		} else if (bwEdition.equals(MavenPluginConstants.DOCKER)) {
			dockerPage.getUpdatedProject();
		}

		return true;
	}

	public BWProject getProject() {
		return project;
	}

}

package com.tibco.bw.studio.maven.wizard;

import org.cloudfoundry.client.lib.CloudFoundryClient;
import org.eclipse.jface.wizard.Wizard;

import com.tibco.bw.studio.maven.modules.model.BWModule;
import com.tibco.bw.studio.maven.modules.model.BWModuleType;
import com.tibco.bw.studio.maven.modules.model.BWPCFModule;
import com.tibco.bw.studio.maven.modules.model.BWProject;

public class PCFLoginWizard extends Wizard {

	protected PCFLoginWizardPage one;
	private BWProject project;

	public PCFLoginWizard(BWProject project) {
		super();
		this.project = project;
		setNeedsProgressMonitor(true);
	}

	@Override
	public void addPages() {
		one = new PCFLoginWizardPage("PCF Login", project);
		addPage(one);
	}

	@Override
	public boolean performFinish() {
		CloudFoundryClient client = one.login();
		for (BWModule module : project.getModules()) {
			if (module.getType() == BWModuleType.Application) {
				BWPCFModule bwpcf = module.getBwpcfModule();
				bwpcf.setClient(client);
				module.setBwpcfModule(bwpcf);
				break;
			}
		}
		return true;
	}

	public BWProject getProject() {
		return project;
	}
}

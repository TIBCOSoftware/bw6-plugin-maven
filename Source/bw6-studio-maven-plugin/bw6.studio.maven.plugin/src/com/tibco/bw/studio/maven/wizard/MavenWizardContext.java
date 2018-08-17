package com.tibco.bw.studio.maven.wizard;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.Button;

public class MavenWizardContext {
	private Button nextButton;
	private Button previousButton;
	private Button finishButton;
	private Button cancelButton;
	private WizardPageConfiguration wizardPageConfiguration;
	private WizardPageEnterprise wizardPageEnterprise;
	private WizardPagePCF wizardPagePCF;
	private WizardPageDocker wizardPageDocker;
	private WizardPageK8S wizardPageK8S;
	
	
	private List<BWProjectTypes> projectTypes = new ArrayList<BWProjectTypes>();
	
	private BWProjectTypes selectedType = BWProjectTypes.None;
	
	
	public static MavenWizardContext INSTANCE = new MavenWizardContext();

	public void reset() {
		INSTANCE = new MavenWizardContext();
	}

	private MavenWizardContext() {
	}

	public Button getNextButton() {
		return nextButton;
	}

	public void setNextButton(Button nextButton) {
		this.nextButton = nextButton;
	}

	public Button getPreviousButton() {
		return previousButton;
	}

	public void setPreviousButton(Button previousButton) {
		this.previousButton = previousButton;
	}

	public Button getFinishButton() {
		return finishButton;
	}

	public void setFinishButton(Button finishButton) {
		this.finishButton = finishButton;
	}

	public Button getCancelButton() {
		return cancelButton;
	}

	public void setCancelButton(Button cancelButton) {
		this.cancelButton = cancelButton;
	}
	public void setConfigPage(WizardPageConfiguration wizardPageConfiguration)
	{
		this.wizardPageConfiguration = wizardPageConfiguration;
	}
	public WizardPageConfiguration getConfigPage() {
		return wizardPageConfiguration;
	}
	public void setDockerPage(WizardPageDocker wizardPageDocker) {
		this.wizardPageDocker = wizardPageDocker;
	}
	public WizardPageDocker getDockerPage() {
		return wizardPageDocker;
	}
	public void setKubernetesPage(WizardPageK8S wizardPageK8S) {
		this.wizardPageK8S = wizardPageK8S;
	}
	public WizardPageK8S getKubernetesPage() {
		return wizardPageK8S;
	}
	public void setPCFPage(WizardPagePCF wizardPagePCF) {
		this.wizardPagePCF = wizardPagePCF;
	}
	public WizardPagePCF getPCFPage() {
		return wizardPagePCF;
	}
	public void setEnterprisePage(WizardPageEnterprise wizardPageEnterprise) {
		this.wizardPageEnterprise = wizardPageEnterprise;
	}
	public WizardPageEnterprise getEnterprisePage() {
		return wizardPageEnterprise;
	}
	public List<BWProjectTypes> getProjectTypes() {
		return projectTypes;
	}
	public void setProjectTypes(List<BWProjectTypes> projectTypes) {
		this.projectTypes = projectTypes;
	}
	public void setSelectedType(BWProjectTypes selectedType) 
	{
		this.selectedType = selectedType;
	}
	public BWProjectTypes getSelectedType() 
	{
		return selectedType;
	}

}

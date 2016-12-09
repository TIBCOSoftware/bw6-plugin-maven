package com.tibco.bw.studio.maven.wizard;

import org.eclipse.swt.widgets.Button;

public class MavenWizardContext {
	private Button nextButton;
	private Button previousButton;
	private Button finishButton;
	private Button cancelButton;
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
}

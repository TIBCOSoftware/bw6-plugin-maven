package com.tibco.bw.studio.maven.wizard;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

public class PCFLoginWizardDialog extends WizardDialog{

	public PCFLoginWizardDialog(Shell parentShell, IWizard newWizard) {
		super(parentShell, newWizard);
	}
	
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
	    super.createButtonsForButtonBar(parent);

	    Button finish = getButton(IDialogConstants.FINISH_ID);
	    finish.setText("Login");
	    setButtonLayoutData(finish);
	}
}

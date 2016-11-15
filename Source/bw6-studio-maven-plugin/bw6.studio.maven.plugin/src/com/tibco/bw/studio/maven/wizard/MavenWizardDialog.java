package com.tibco.bw.studio.maven.wizard;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;


public class MavenWizardDialog extends WizardDialog 
{
	
	public MavenWizardDialog (Shell parentShell, IWizard newWizard )
	{	
		super(parentShell, newWizard);
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) 
	{
		super.createButtonsForButtonBar(parent);

		
		MavenWizardContext.INSTANCE.setNextButton( getButton( IDialogConstants.NEXT_ID ));
		
		MavenWizardContext.INSTANCE.setPreviousButton( getButton( IDialogConstants.BACK_ID ));

		MavenWizardContext.INSTANCE.setFinishButton( getButton( IDialogConstants.FINISH_ID ));

		MavenWizardContext.INSTANCE.setCancelButton( getButton( IDialogConstants.CANCEL_ID ));

	}
	
	@Override
	protected Point getInitialSize() {
		return super.getInitialSize();
		//return new Point(750, 610);
	}
	
}

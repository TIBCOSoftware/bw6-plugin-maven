package com.tibco.bw.studio.maven.wizard;

import org.eclipse.jface.wizard.Wizard;

import com.tibco.bw.studio.maven.modules.BWProject;

public class PCFServiceWizard extends Wizard {
	
	protected PCFServicesWizardPage one;
	  private BWProject project;

	  public PCFServiceWizard( BWProject project ) 
	  {
		  super();
		  this.project = project;
		  setNeedsProgressMonitor(true);
	  }
	
	@Override
	  public void addPages() 
	  {
	    one = new PCFServicesWizardPage( "PCF Services Configuration" , project );
	    addPage(one);
	  }
	
	@Override
	public boolean performFinish() {
		project = one.getSelectedServices();
	    return true;
	}

	public BWProject getProject() 
	{
		return project;
	}
}

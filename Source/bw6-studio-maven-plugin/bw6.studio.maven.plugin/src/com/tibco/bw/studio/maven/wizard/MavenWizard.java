package com.tibco.bw.studio.maven.wizard;

import org.eclipse.jface.wizard.Wizard;

import com.tibco.bw.studio.maven.modules.BWProject;

public class MavenWizard extends Wizard 
{

	  protected WizardPage1 one;
	  private BWProject project;
  
	  public MavenWizard( BWProject project ) 
	  {
		  super();
		  this.project = project;
		  setNeedsProgressMonitor(true);
	  }

	  @Override
	  public void addPages() 
	  {
	    one = new WizardPage1( "POM Configuration" , project );
	    addPage(one);
	  }

	  @Override
	  public boolean performFinish() 
	  {
		project = one.getUpdatedProject();
	    return true;
	  }

	public BWProject getProject() 
	{
		return project;
	}

	 
}



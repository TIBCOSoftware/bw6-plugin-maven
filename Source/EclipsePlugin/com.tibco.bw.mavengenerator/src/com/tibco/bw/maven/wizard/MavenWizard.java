package com.tibco.bw.maven.wizard;

import org.eclipse.jface.wizard.Wizard;

import com.tibco.bw.maven.utils.BWProjectInfo;

public class MavenWizard extends Wizard 
{

	  protected WizardOne one;
	  
	  private BWProjectInfo bwProjectInfo;
	  
	  public MavenWizard() 
	  {
	    super();
	    setNeedsProgressMonitor(true);
	  }

	  @Override
	  public void addPages() 
	  {
	    one = new WizardOne( "POM Configuration");
	    one.setBwProjectInfo(bwProjectInfo);
	    addPage(one);
	  }

	  @Override
	  public boolean performFinish() 
	  {
		bwProjectInfo = one.getUpdatedBWInfo();  
	    return true;
	  }

	public BWProjectInfo getBwProjectInfo() 
	{
		return bwProjectInfo;
	}

	public void setBwProjectInfo(BWProjectInfo bwProjectInfo) 
	{
		this.bwProjectInfo = bwProjectInfo;
	}
	
	 
}



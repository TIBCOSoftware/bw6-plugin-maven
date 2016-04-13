package com.tibco.bw.studio.maven.wizard;

import java.util.Map;

import org.eclipse.jface.wizard.Wizard;

import com.tibco.bw.studio.maven.helpers.ManifestParser;
import com.tibco.bw.studio.maven.modules.BWProject;

public class MavenWizard extends Wizard 
{

	  protected WizardPageConfiguration configPage;
	  protected WizardPagePCF pcfPage;
	  protected WizardPageEnterprise enterprisePage;
	  String bwEdition = "bw6";
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
		  try
		  {
			  Map<String,String> manifest = ManifestParser.parseManifest(project.getModules().get(0).getProject());
			  if(manifest.containsKey("TIBCO-BW-Edition") && manifest.get("TIBCO-BW-Edition").equals("bwcf"))
			  {
				  bwEdition="bwcf";
			  }
			  else
			  {
				  bwEdition="bw6";
			  }
		  } catch (Exception e) 
		  {
			  e.printStackTrace();
		  }

	    configPage = new WizardPageConfiguration( "POM Configuration" , project );
	    pcfPage = new WizardPagePCF("PCF Deployment Configuration", project);
	    enterprisePage = new WizardPageEnterprise("Deployment Configuration" , project);
	    
	    addPage(configPage);
	    if( bwEdition.equals("bw6"))
	    {
	    	addPage(enterprisePage);
	    }
	    else
	    {
	    	addPage(pcfPage);
	    }
	  }

	  @Override
	  public boolean performFinish() 
	  {
		project = configPage.getUpdatedProject();
		
	    if( bwEdition.equals("bw6"))
	    {
	    	enterprisePage.getUpdatedProject();
	    }
	    else
	    {
	    	pcfPage.getUpdatedProject();
	    }

	    return true;
	  }

	public BWProject getProject() 
	{
		return project;
	}

	 
}



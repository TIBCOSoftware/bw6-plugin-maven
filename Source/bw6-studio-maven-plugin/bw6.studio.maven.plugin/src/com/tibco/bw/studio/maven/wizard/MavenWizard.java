package com.tibco.bw.studio.maven.wizard;

import java.util.Map;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Control;

import com.tibco.bw.studio.maven.helpers.ManifestParser;
import com.tibco.bw.studio.maven.modules.BWProject;
import com.tibco.zion.project.core.ContainerPreferenceProject;

public class MavenWizard extends Wizard 
{

	  protected WizardPageConfiguration configPage;
	  protected WizardPagePCF pcfPage;
	  protected WizardPageDocker dockerPage;
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
				  String targetPlatform = ContainerPreferenceProject.getCurrentContainer().getLabel();
				  if(targetPlatform.equals("Cloud Foundry")){
					  bwEdition="cf";
				  }else{
					  bwEdition="docker";
				  }
			  }
			  else
			  {
				  bwEdition="cf";
			  }
		  } catch (Exception e) 
		  {
			  e.printStackTrace();
		  }

		  configPage = new WizardPageConfiguration( "POM Configuration" , project );
		  pcfPage = new WizardPagePCF("PCF Deployment Configuration", project);
		  enterprisePage = new WizardPageEnterprise("Deployment Configuration" , project);
		  dockerPage = new WizardPageDocker("Docker Deployment Configuration", project);
		  
		  addPage(configPage);
		  if( bwEdition.equals("bw6"))
		  {
			  addPage(enterprisePage);
		  }
		  else if(bwEdition.equals("cf"))
		  {
			  addPage(pcfPage);
		  }
		  else if(bwEdition.equals("docker"))
		  {
			  addPage(dockerPage);
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
	    else if(bwEdition.equals("cf"))
	    {
	    	pcfPage.getUpdatedProject();
	    }
	    else if(bwEdition.equals("docker"))
	    {
	    	dockerPage.getUpdatedProject();
	    }

	    return true;
	  }

	public BWProject getProject() 
	{
		return project;
	}
	
}



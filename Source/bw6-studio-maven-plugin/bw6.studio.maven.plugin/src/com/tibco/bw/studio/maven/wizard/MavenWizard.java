package com.tibco.bw.studio.maven.wizard;

import java.util.Map;

import org.eclipse.jface.wizard.Wizard;

import com.tibco.bw.studio.maven.helpers.ManifestParser;
import com.tibco.bw.studio.maven.helpers.ModuleHelper;
import com.tibco.bw.studio.maven.modules.model.BWApplication;
import com.tibco.bw.studio.maven.modules.model.BWProject;
import com.tibco.bw.studio.maven.modules.model.BWProjectType;

public class MavenWizard extends Wizard {
	private BWProject project;
	private String windowTitle ="Generate POM for Application";

	public MavenWizard(BWProject project) {
		super();
		this.project = project;
		setNeedsProgressMonitor(true);
		MavenWizardContext.INSTANCE.reset();
	}

	@Override
	public void addPages() {
		try {
			Map<String, String> manifest = ManifestParser.parseManifest(project.getModules().get(0).getProject());
			
			MavenWizardContext.INSTANCE.getProjectTypes().add( BWProjectTypes.None );
			
			if (manifest.containsKey("TIBCO-BW-Edition") )				
			{
				String editions = manifest.get( "TIBCO-BW-Edition" );
				
				if( editions.isEmpty() )
				{
					MavenWizardContext.INSTANCE.getProjectTypes().add( BWProjectTypes.AppSpace );
				}
				
				else 
				{	
					String[] editionList = editions.split(",");
					for( String str : editionList )
					{
						switch ( str )
						{
						case "bwe":
							MavenWizardContext.INSTANCE.getProjectTypes().add( BWProjectTypes.AppSpace );
							break;

						case "bwcf":
							MavenWizardContext.INSTANCE.getProjectTypes().add( BWProjectTypes.PCF );
							MavenWizardContext.INSTANCE.getProjectTypes().add( BWProjectTypes.Docker );							
						break;
						
						case "bwcloud":
							MavenWizardContext.INSTANCE.getProjectTypes().add( BWProjectTypes.TCI );
						break;

						default:
							break;
						}
					}
				}
			}else{
				// "TIBCO-BW-Edition" value in manifest is not set, default will be "bwe".
				MavenWizardContext.INSTANCE.getProjectTypes().add( BWProjectTypes.AppSpace );
			}

			MavenWizardContext.INSTANCE.setConfigPage(new WizardPageConfiguration("POM Configuration", project));
			addPage(MavenWizardContext.INSTANCE.getConfigPage());
			
			if(project.getType() == BWProjectType.Application){
				MavenWizardContext.INSTANCE.setEnterprisePage(new WizardPageEnterprise("Deployment Configuration", project));
				MavenWizardContext.INSTANCE.setPCFPage(new WizardPagePCF("PCF Deployment Configuration",project));
				MavenWizardContext.INSTANCE.setDockerPage(new WizardPageDocker("Docker Deployment Configuration", project));
				MavenWizardContext.INSTANCE.setKubernetesPage(new WizardPageK8S("Kubernetes Deployment Configuration", project));
				MavenWizardContext.INSTANCE.setTCIPage(new WizardPageTCI("TCI Deployment Configuration", project));
				addPage(MavenWizardContext.INSTANCE.getEnterprisePage());
				addPage(MavenWizardContext.INSTANCE.getPCFPage());
				addPage(MavenWizardContext.INSTANCE.getDockerPage());
				addPage(MavenWizardContext.INSTANCE.getKubernetesPage());
				addPage(MavenWizardContext.INSTANCE.getTCIPage());
			}
		}
		catch(Exception e )
		{
			e.printStackTrace();
		}
	}


	@Override
	public boolean performFinish() 
	{
		project = MavenWizardContext.INSTANCE.getConfigPage().getUpdatedProject();
		
		if(project.getType() == BWProjectType.SharedModule)
		{
			return true;
		}
		else
		{
		
			switch ( MavenWizardContext.INSTANCE.getSelectedType() )
			{
			
				case AppSpace:
					if (((WizardPageEnterprise) MavenWizardContext.INSTANCE.getEnterprisePage()).validate()) 
					{
						MavenWizardContext.INSTANCE.getEnterprisePage().getUpdatedProject();
					} else {
						return false;
					}
			
					break;
					
				case PCF:
					MavenWizardContext.INSTANCE.getPCFPage().getUpdatedProject();
					
				case Docker:
					MavenWizardContext.INSTANCE.getDockerPage().getUpdatedProject();
					
				case TCI:
					MavenWizardContext.INSTANCE.getTCIPage().getUpdatedProject();
					
			default:
				break;
			}
		}
		
		return true;
	}

	public BWProject getProject() {
		return project;
	}
	
	@Override
	public String getWindowTitle() {
        return windowTitle;
    }
}

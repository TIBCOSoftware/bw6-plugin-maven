package com.tibco.bw.studio.maven.util;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.widgets.Display;

import java.util.Iterator;

import com.tibco.bw.core.design.project.core.util.BWCompositeHelper;
import com.tibco.bw.core.design.project.core.util.BWManifestHelper;
import com.tibco.bw.core.design.svg.BWGenerateSVGServiceImpl;
import com.tibco.bw.core.design.resource.util.BWProjectHelper;
import com.tibco.schemas.tra.model.core.packaging.Module;
import com.tibco.schemas.tra.model.core.packaging.Modules;
import com.tibco.schemas.tra.model.core.packaging.PackageUnit;
import com.tibco.zion.dc.util.DeploymentConfigurationsHelper;
import com.tibco.zion.dc.util.DeploymentDescriptorConstants;


public class ProcessDiagramGenerator {
	
	protected static final String MAIN_FOLDER = "resources";
	
	public static void generateDiagrams(IProject bwModule) throws Exception 
	{	
		boolean isBWApplication = BWManifestHelper.INSTANCE.isBWApplication(bwModule);
		if (!isBWApplication) {
			throw new Exception("The project '" + bwModule.getName() + "' is not a deployable project.");
		}

		File folderFile = new File(bwModule.getLocation().append(MAIN_FOLDER).toOSString());
		if (!folderFile.exists()) {
			folderFile.mkdir();
		}
		
		
		Set<String> projectsGenerated = new HashSet<String>();
		Set<IProject> projectsTogenerate = new HashSet<IProject>();
		
		Module[] appModules = getModules(bwModule);
		for(Module module : appModules){
			IProject project =  BWProjectHelper.INSTANCE.getProject(module.getSymbolicName());
			if(project == null)
				continue;
			projectsTogenerate.add(project);
			projectsTogenerate.addAll(BWCompositeHelper.INSTANCE.getDependentProjects(bwModule, false));
		}
		
		Iterator<IProject> projectIterator = projectsTogenerate.iterator();
		while(projectIterator.hasNext()){
			IProject project = projectIterator.next();
			if(!projectsGenerated.contains(project.getName())){
				projectsGenerated.add(project.getName());
				createDiagrams(project, folderFile);
			}
		}

	}
	
	private static void createDiagrams(final IProject project, final File folderFile){
		Display.getCurrent().syncExec(new Runnable() {
			public void run() {
				
				BWGenerateSVGServiceImpl sgv = new BWGenerateSVGServiceImpl();
				sgv.saveSVG(project, folderFile);
			}
		});
	}
	
	public static Module[] getModules(IProject project) throws CoreException, IOException {
		IFile tibcoXmlFile = project.getFile(DeploymentDescriptorConstants.DEFAULT_TIBCO_XML_DESCRIPTOR);
		PackageUnit packageUnit = DeploymentConfigurationsHelper.INSTANCE.loadPackageUnit(tibcoXmlFile);
		if (packageUnit != null) {
			Modules modules = packageUnit.getModules();
			if (modules != null && modules.getModule() != null)
				return modules.getModule().toArray(new Module[modules.getModule().size()]);
		}
		return new Module[0];
	}

}

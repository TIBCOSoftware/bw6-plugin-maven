package com.tibco.bw.studio.maven.validation;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.pde.internal.core.bundle.WorkspaceBundlePluginModel;
import org.eclipse.pde.internal.core.ibundle.IBundle;
import org.eclipse.pde.internal.core.project.PDEProject;

import com.tibco.bw.design.util.BWDesignConstants;

public class ProjectUtils {

	public static ProjectUtils INSTANCE = new ProjectUtils();
	
	private ProjectUtils(){}
	
	public boolean isBWSharedModule(IProject project) {
		if (project != null) {
			IFile pluginXml = PDEProject.getPluginXml(project);
			IFile manifest = PDEProject.getManifest(project);
			WorkspaceBundlePluginModel bundlePluginModel = new WorkspaceBundlePluginModel(manifest, pluginXml);
			if (bundlePluginModel.getBundleModel() != null) {
				IBundle bundle = bundlePluginModel.getBundleModel().getBundle();
				return isBWSharedModule(bundle);
			}
		}
		return false;
	}
	
	public boolean isBWSharedModule(IBundle bundle) {
		if (bundle != null) {
			String headerValue = bundle.getHeader(BWDesignConstants.TIBCO_BW_SHARED_MODULE_HEADER);
			if (headerValue != null) {
				return true;
			}
		}
		return false;
	}

}

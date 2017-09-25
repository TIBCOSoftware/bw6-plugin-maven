package com.tibco.bw.studio.maven.validation;

import java.io.File;
import java.io.InputStream;
import java.net.URI;

import org.apache.maven.model.Model;
import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;

import com.tibco.bw.design.external.dependencies.AbstractExternalDependencyHandler;
import com.tibco.bw.studio.maven.helpers.POMHelper;
import com.tibco.bw.studio.maven.util.BWMavenConstants;

public class BWMavenDependencyHandler extends AbstractExternalDependencyHandler{

	public BWMavenDependencyHandler(String name, String version, URI uri){
		this(name, version, null, uri);
	}
	
	public BWMavenDependencyHandler(String name, String version, String location, URI locationURI){
		this.depName = name;
		this.depVersion = version;
		this.depLocation = location;
		this.depLocationURI = locationURI;
	}
	
	@Override
	public String getVersionFromProject(IProject project) {
		String version = ""; //$NON-NLS-1$
		
		if(project != null && project.exists()){
			Model mavenModel = getMavenModel(project);
			if(mavenModel != null){
				version = mavenModel.getVersion();
			}
		}
		return version;
	}
	
	protected Model getMavenModel(IProject project){
		IFile pomFile = project.getFile(BWMavenConstants.POM_XML_LOCATION);
		
		if(!pomFile.exists()){
			return null;
		}
		Model mavenModel = null;
		try{
			 File f = pomFile.getRawLocation().toFile();
			 mavenModel = POMHelper.readModelFromPOM(f);
		}catch(Exception e){
			//File is not in the workspace, it may come from a zip package
			URI uri = pomFile.getLocationURI();
			try {
				IFileStore store = EFS.getStore(uri);
				InputStream is = store.openInputStream(EFS.NONE, null);
				mavenModel = POMHelper.readModelFromPOM(is);
				is.close();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
		return mavenModel;
	}

}

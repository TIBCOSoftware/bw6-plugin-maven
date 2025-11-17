package com.tibco.bw.maven.plugin.osgi.helpers;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.Attributes.Name;
import java.util.jar.Manifest;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Dependency;
import org.apache.maven.project.MavenProject;

import com.tibco.bw.maven.plugin.utils.BWProjectUtils;
import com.tibco.bw.maven.plugin.utils.BWProjectUtils.MODULE;
import com.tibco.bw.maven.plugin.utils.Constants;


public class ManifestWriter {

    public static File updateManifest(MavenProject project , Manifest mf) throws IOException {
        
        File mfile = new File(project.getBuild().getDirectory(), "MANIFEST.MF");
        mfile.getParentFile().mkdirs();
        BufferedOutputStream os = new BufferedOutputStream(new FileOutputStream(mfile));
        try {
            mf.write(os);
        } finally {
        	if(os != null) {
        		os.close();	
        	}
        }
        return mfile;
    }
    
    
    public static void updateManifestVersion(MavenProject project , Manifest mf, String qualifierReplacement, MavenSession session)
    {
        Attributes attributes = mf.getMainAttributes();
        
        String projectVersion = project.getVersion();
        if( projectVersion.indexOf("-SNAPSHOT") != -1 )
        {
        	projectVersion = projectVersion.replace("-SNAPSHOT", ".qualifier");
        	projectVersion = getManifestVersion(mf, projectVersion, qualifierReplacement);
        }
        
    	attributes.put(Name.MANIFEST_VERSION, projectVersion);
        attributes.putValue(Constants.BUNDLE_VERSION, projectVersion );

        //Updating provide capability for Shared Modules
        if(BWProjectUtils.getModuleType(mf) == MODULE.SHAREDMODULE){
        	String updatedProvide = ManifestParser.getUpdatedProvideCapabilities(mf, projectVersion);
        	if(updatedProvide!= null && !updatedProvide.isEmpty()) {
        		attributes.putValue(Constants.BUNDLE_PROVIDE_CAPABILITY, updatedProvide);
        	}
        }
        
        if(BWProjectUtils.getModuleType(mf) == MODULE.APPMODULE || BWProjectUtils.getModuleType(mf) == MODULE.SHAREDMODULE) {
        	List<Dependency> list=project.getDependencies();
        	if(list != null && !list.isEmpty()) {
	        	String updatedRequire=ManifestParser.getRequiredCapabilities(mf.getMainAttributes().getValue(Constants.BUNDLE_REQUIRE_CAPABILITY), list,session);
	        	
	        	if(updatedRequire != null  &&  !updatedRequire.isEmpty()) {
	        		attributes.putValue(Constants.BUNDLE_REQUIRE_CAPABILITY, updatedRequire);
	        	}
        	
        	}
        }
        

    }
    
    private static String getManifestVersion( Manifest manifest , String version, String qualifierReplacement) 
    {    	
    	return VersionParser.getcalculatedOSGiVersion(version, qualifierReplacement);
    }

}

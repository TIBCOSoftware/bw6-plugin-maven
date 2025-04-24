package com.tibco.bw.maven.plugin.osgi.helpers;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.jar.Attributes;
import java.util.jar.Attributes.Name;
import java.util.jar.Manifest;

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
    
    
    public static void udpateManifestAttributes(MavenProject project , Manifest mf, String qualifierReplacement)
    {
        Attributes attributes = mf.getMainAttributes();
        
        String bundleVersion = project.getVersion();
        String archiveVersion = bundleVersion;
        if( bundleVersion.indexOf("-SNAPSHOT") != -1 )
        {
        	archiveVersion = bundleVersion.replace("-SNAPSHOT", ".qualifier");
        	bundleVersion = archiveVersion;
        }
        
        archiveVersion = getManifestVersion(mf, archiveVersion, qualifierReplacement);
    	attributes.putValue(Constants.BUNDLE_VERSION, bundleVersion);
    	attributes.putValue(Constants.ARCHIVE_FILE_VERSION, archiveVersion);
        //Updating provide capability for Shared Modules
        if(BWProjectUtils.getModuleType(mf) == MODULE.SHAREDMODULE){
        	String updatedProvide = ManifestParser.getUpdatedProvideCapabilities(mf, bundleVersion);
        	attributes.putValue(Constants.BUNDLE_PROVIDE_CAPABILITY, updatedProvide);
        }
        
        if(BWProjectUtils.getModuleType(mf) == MODULE.APPLICATION || BWProjectUtils.getModuleType(mf) == MODULE.APPMODULE){
        	String reqCapbilityValue = attributes.getValue(Constants.BUNDLE_REQUIRE_CAPABILITY);
        	if (reqCapbilityValue != null) {
        		String requiredCapability = ManifestParser.getRequiredCapabilities(reqCapbilityValue, bundleVersion);
        		attributes.putValue(Constants.BUNDLE_REQUIRE_CAPABILITY, requiredCapability);
        	}
        }
    }
    
    private static String getManifestVersion( Manifest manifest , String version, String qualifierReplacement) 
    {    	
    	return VersionParser.getcalculatedOSGiVersion(version, qualifierReplacement);
    }
}

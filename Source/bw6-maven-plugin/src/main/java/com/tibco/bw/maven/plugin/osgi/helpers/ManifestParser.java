package com.tibco.bw.maven.plugin.osgi.helpers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;

import com.tibco.bw.maven.plugin.utils.Constants;

public class ManifestParser {

	public static Manifest parseManifest(File baseDir) {
		Manifest mf = null;
        File mfile = new File(baseDir , "META-INF/MANIFEST.MF");
        InputStream is = null;
        try {
            is = new FileInputStream(mfile);
            mf = new Manifest(is);
        } catch(FileNotFoundException f) {
        	f.printStackTrace();
        } catch(IOException e) {
        	e.printStackTrace();
        } finally {
            try {
            	if(is != null) {
            		is.close();	
            	}
			} catch(IOException e) {
				e.printStackTrace();
			}
        }
        return mf;
	}
	

	public static Manifest parseManifestFromJAR(File jarFile) 
	{
		Manifest moduleManifest = null;
		
		try
		{
			JarInputStream jarStream = new JarInputStream( new FileInputStream( jarFile ));
			moduleManifest = jarStream.getManifest();
			jarStream.close();
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
		
        return moduleManifest;
	}

	public static String getUpdatedProvideCapabilities(Manifest manifest, String oldVersion){
		String updatedProvidesCapabilities = ""; //$NON-NLS-1$
		
		Version versionObject = VersionParser.parseVersion(oldVersion);
		String newVersion = versionObject.getMajor() + "." + versionObject.getMinor() + "." + versionObject.getMicro();
		
		if(manifest != null){
			String capabilities = manifest.getMainAttributes().getValue(Constants.BUNDLE_PROVIDE_CAPABILITY);
			if(capabilities != null && !capabilities.isEmpty()){
				String[] entries = capabilities.split(",");
				boolean updated = false;
				for(int i = 0; i<entries.length; i++){
					String entry = entries[i];
					String[] filters = entry.split(";");
					if(filters[0].trim().equals("com.tibco.bw.module") ){
						filters[2] = "version:Version=\""+newVersion+"\"";
						updated = true;
					}
					if(updated){
						String newEntry = filters[0].trim() + "; " + filters[1].trim() + "; " + filters[2].trim();
						entries[i] = newEntry;
						break;
					}
				}
				
				if(updated){
					for(int i = 0; i<entries.length; i++){
						String entry = entries[i];
						updatedProvidesCapabilities += entry;
						if(i < entries.length -  1){
							updatedProvidesCapabilities+= ",";
						}
					}
				}
			}
		}
		
		return updatedProvidesCapabilities;
	}
}

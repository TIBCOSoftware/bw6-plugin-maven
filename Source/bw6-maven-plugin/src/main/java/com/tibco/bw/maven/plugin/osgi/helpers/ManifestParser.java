package com.tibco.bw.maven.plugin.osgi.helpers;

import com.tibco.bw.maven.plugin.utils.Constants;

import java.io.*;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;

public class ManifestParser {

	public static Manifest parseManifest(File baseDir) {
		Manifest mf = null;
        File mfile = new File(baseDir , "META-INF/MANIFEST.MF");
        if(mfile.exists())
        {
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

	public static boolean isSharedModule(Manifest mf) {

		//mf.getMainAttributes().containsKey() require an Object not a String and don't return right value
		return (mf.getMainAttributes().getValue(Constants.TIBCO_SHARED_MODULE) != null);
	}
}

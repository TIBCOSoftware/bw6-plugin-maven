package com.tibco.bw.maven.plugin.osgi.helpers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;

import com.tibco.bw.maven.plugin.utils.Constants;

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
	
	
	public static void updateWriteManifest(File baseDir, Manifest mf) {
		File mfile = new File(baseDir , "META-INF/MANIFEST.MF");
		try {
			OutputStream outStream = new FileOutputStream(mfile);
			mf.write(outStream);
		} catch (IOException e) {
			e.printStackTrace();
		}
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
		String newVersion = oldVersion;
		
		if (newVersion != null && newVersion.contains(".qualifier")) {
			String vers[] = newVersion.split(".qualifier");
			newVersion = vers[0];
		}
		
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

	
	public static String getRequiredCapabilities(String reqCapbilitySource, String newVersion){
		if (newVersion != null && newVersion.contains(".qualifier")) {
			String vers[] = newVersion.split(".qualifier");
			newVersion = vers[0];
		}
		
		String processedText = "";
		String[] entries = reqCapbilitySource.split(",");
		for(int i = 0; i<entries.length; i++){
			String entry = entries[i];
			String[] filters = entry.split(";");
			if(filters[0].trim().equals("com.tibco.bw.module") ){
				if (filters[1].contains("version=")) {
					String dependencies[] = filters[1].split("version=");
					processedText += "," + filters[0] + ";";
					processedText += dependencies[0];
					processedText += "version=" + newVersion + "))\"";
				}else {
					processedText += "," + entry;
				}
			}else {
				if (processedText.length() > 0) {
					processedText += ",";
				}
				processedText += entry;
			}
		}
		
		return processedText;
	}
}

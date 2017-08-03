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
        } catch(IOException e) {
        } finally {
            try {
            	if(is != null) {
            		is.close();	
            	}
			} catch(IOException e) {
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

}

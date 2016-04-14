package com.tibco.bw.maven.plugin.osgi.helpers;

import java.io.*;
import java.util.jar.Manifest;

public class ManifestParser 
{

	public static Manifest parseManifest( File baseDir )
	{
		Manifest mf = null;
        File mfile = new File(baseDir , "META-INF/MANIFEST.MF");
        InputStream is = null;
        
        
        try {
            is = new FileInputStream(mfile);
            mf = new Manifest(is);
        }
        catch(FileNotFoundException f )
        {
        	
        } catch (IOException e) {
		}
        finally
        {
            try 
            {
				is.close();
			}
            catch (IOException e) 
            {
			}
        }

        return mf;
        
	}
	
}

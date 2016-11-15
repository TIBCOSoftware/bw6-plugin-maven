package com.tibco.bw.studio.maven.helpers;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.jar.Manifest;

public class ManifestWriter 
{

	
	   public static File updateManifest( File mfile , Manifest mf ) throws IOException
	    {
	
	        BufferedOutputStream os = new BufferedOutputStream(new FileOutputStream(mfile));
	        try {
	            mf.write(os);
	        } finally {
	            os.close();
	        }

	        return mfile;
	    }

}

package com.tibco.bw.maven.plugin.osgi.helpers;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.jar.Attributes;
import java.util.jar.Attributes.Name;
import java.util.jar.Manifest;

import org.apache.maven.project.MavenProject;

public class ManifestWriter 
{

    public static File updateManifest( MavenProject project , Manifest mf ) throws IOException
    {
        Attributes attributes = mf.getMainAttributes();

        if (attributes.getValue(Name.MANIFEST_VERSION) == null)
        {
            attributes.put(Name.MANIFEST_VERSION, "1.0");
        }

        File mfile = new File(project.getBuild().getDirectory(), "MANIFEST.MF");
        mfile.getParentFile().mkdirs();
        BufferedOutputStream os = new BufferedOutputStream(new FileOutputStream(mfile));
        try {
            mf.write(os);
        } finally {
            os.close();
        }

        return mfile;
    }
	
}

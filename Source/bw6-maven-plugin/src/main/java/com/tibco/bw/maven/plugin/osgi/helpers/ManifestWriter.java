package com.tibco.bw.maven.plugin.osgi.helpers;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.jar.Attributes;
import java.util.jar.Attributes.Name;
import java.util.jar.Manifest;

import org.apache.maven.project.MavenProject;

public class ManifestWriter {

    public static File updateManifest(MavenProject project , Manifest mf) throws IOException {
        Attributes attributes = mf.getMainAttributes();
        if((attributes.getValue(Name.MANIFEST_VERSION) == null || attributes.getValue(Name.MANIFEST_VERSION).equals("1.0")) && project.getVersion().equals("1.0.0-SNAPSHOT")) {
            attributes.put(Name.MANIFEST_VERSION, project.getVersion());
        }

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
}

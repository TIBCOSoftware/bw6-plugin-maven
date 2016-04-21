package com.tibco.bw.maven.plugin.osgi.helpers;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.jar.Manifest;

@SuppressWarnings("ThrowFromFinallyBlock")
public class ManifestParser {

    public static Manifest parseManifest(File baseDir) throws Exception {
        Manifest mf = null;
        File mfile = new File(baseDir, "META-INF/MANIFEST.MF");
        InputStream is = null;

        try {
            is = new FileInputStream(mfile);
            mf = new Manifest(is);
        } finally {
            if (is != null) {
                is.close();
            }
        }

        return mf;
    }

}

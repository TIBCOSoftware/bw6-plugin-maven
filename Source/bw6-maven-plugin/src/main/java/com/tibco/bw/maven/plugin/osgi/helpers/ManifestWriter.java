package com.tibco.bw.maven.plugin.osgi.helpers;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.jar.Attributes;
import java.util.jar.Attributes.Name;
import java.util.jar.Manifest;

@SuppressWarnings("ThrowFromFinallyBlock")
public class ManifestWriter {

    /**
     * uses the Maven artifact version to populate OSGI Bundle-Version
     *
     * @param outputDir
     * @param mf
     * @return
     * @throws IOException
     */
    public static File updateManifest(String outputDir, Manifest mf) throws IOException {
        Attributes attributes = mf.getMainAttributes();

        if (attributes.getValue(Name.MANIFEST_VERSION) == null) {
            attributes.put(Name.MANIFEST_VERSION, "1.0");
        }

        File mfile = new File(outputDir, "MANIFEST.MF");
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

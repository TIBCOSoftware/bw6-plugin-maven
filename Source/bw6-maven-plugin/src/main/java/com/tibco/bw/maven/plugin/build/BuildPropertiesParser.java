package com.tibco.bw.maven.plugin.build;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class BuildPropertiesParser {
    public static final String BUILD_PROPERTIES = "build.properties";

    public static BuildProperties parse(File baseDir) {
        File propsFile = new File(baseDir, BUILD_PROPERTIES);
        Properties properties = readProperties(propsFile);
        BuildProperties buildProperties = new BuildPropertiesImpl(properties);
        return buildProperties;
    }

    protected static Properties readProperties(File propsFile) {
        Properties properties = new Properties();
        if(propsFile.canRead()) {
            // TODO should we fail the build if build.properties is missing?
            InputStream is = null;
            try {
                try {
                    is = new FileInputStream(propsFile);
                    properties.load(is);
                } finally {
                    if (is != null) {
                        is.close();
                    }
                }
            } catch(IOException e) {
                // ignore
            }
        }
        return properties;
    }
}

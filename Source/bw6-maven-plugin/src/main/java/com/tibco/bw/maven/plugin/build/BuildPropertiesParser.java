package com.tibco.bw.maven.plugin.build;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

@SuppressWarnings("ThrowFromFinallyBlock")
public class BuildPropertiesParser {

    private static final String BUILD_PROPERTIES = "build.properties";


    public static BuildProperties parse(File baseDir) throws Exception {
        File propsFile = new File(baseDir, BUILD_PROPERTIES);
        Properties properties = readProperties(propsFile);
        return new BuildPropertiesImpl(properties);
    }


    private static Properties readProperties(File propsFile) throws Exception {
        Properties properties = new Properties();
        if (propsFile.canRead()) {
            InputStream is = null;

            try {
                is = new FileInputStream(propsFile);
                properties.load(is);
            } finally {
                if (is != null) {
                    is.close();
                }
            }
        }
        return properties;
    }


}

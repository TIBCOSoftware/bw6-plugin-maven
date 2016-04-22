package com.tibco.bw.maven.plugin.utils;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import java.io.File;
import java.io.FileInputStream;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;

public class BWProjectUtils {


    public static final String BW_APPMODULE = "bw-appmodule";
    public static final String BW_SHAREDMODULE = "bw-sharedmodule";

    public static String getModuleVersion(File jarFile) throws Exception {
        JarInputStream jarStream = new JarInputStream(new FileInputStream(jarFile));
        Manifest moduleManifest = jarStream.getManifest();
        jarStream.close();

        return moduleManifest.getMainAttributes().getValue("Bundle-Version");
    }

    public static String convertMvnVersionToOSGI(String mvnVersion, boolean includeQualifier) throws Exception {
        String convertedVersion = "1.0.0.qualifier";

        String[] parts = mvnVersion.replaceAll("-", ".").replaceAll("_", ".").split("\\.");
        // may have '1.0.0-SNAPSHOT' or other things like '1.0.1-20160422.203047-2'
        // use the first three and tag on 'qualifier' (OSGI term for SNAPSHOT)
        if (parts.length > 3) {
            String parts3 = parts[3];
            if ((parts3.equals("SNAPSHOT") || !parts3.matches("[0-9]]")) && includeQualifier) {
                parts3 = "qualifier"; // 'qaulifier' means SNAPSHOT according to Tycho conventions for OSGI versioning
                convertedVersion = parts[0] + "." + parts[1] + "." + parts[2] + "." + parts3;
            } else {
                convertedVersion = parts[0] + "." + parts[1] + "." + parts[2];
            }
        } else if ((parts.length == 3) || !includeQualifier) {
            // release versions
            // just use first three here
            convertedVersion = parts[0] + "." + parts[1] + "." + parts[2];
        } else {
            throw new MojoFailureException("Maven version format unsupported: " + mvnVersion);
        }

        return convertedVersion;
    }

    public static File getBWAdminHome(String tibcoHome, String bwVersion) throws Exception {

        File bwAdminHome = new File(new File(tibcoHome), "bw/" + bwVersion + "/bin/");

        if (bwAdminHome.exists()) {
            return bwAdminHome;
        }

        throw new MojoExecutionException("Failed to find Admin Home at location : " + bwAdminHome);


    }

    /**
     * @param artifact
     * @return
     */
    public static String getClassifierOrEmptyString(Artifact artifact) {
        String classifier = "";
        if (artifact.hasClassifier()) {
            return artifact.getClassifier();
        }
        return classifier;
    }
}

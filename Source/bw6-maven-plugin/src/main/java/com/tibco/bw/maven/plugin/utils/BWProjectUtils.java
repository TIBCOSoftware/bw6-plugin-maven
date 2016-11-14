package com.tibco.bw.maven.plugin.utils;

import java.io.File;
import java.io.FileInputStream;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;

import org.apache.maven.plugin.MojoExecutionException;

public class BWProjectUtils {
	public enum OS {
		WINDOWS, UNIX
	}

	public static String getModuleVersion( File jarFile ) throws Exception {
		JarInputStream jarStream = new JarInputStream( new FileInputStream( jarFile ));
		Manifest moduleManifest = jarStream.getManifest();
		jarStream.close();
		return moduleManifest.getMainAttributes().getValue(Constants.BUNDLE_VERSION);
	}

	public static String getAdminExecutable() {
		String os = System.getProperty("os.name");
		if (os.indexOf("Windows") != -1) {
			return "/bwadmin.exe";
		}
		return "/bwadmin";
	}

	public static OS getOS() {
		String os = System.getProperty("os.name");
		if (os.indexOf("Windows") != -1) {
			return OS.WINDOWS;
		}
		return OS.UNIX;
	}

	public static File getBWAdminHome(String tibcoHome, String bwVersion) throws Exception {
		File bwAdminHome = new File (new File(tibcoHome), "bw/"  + bwVersion + "/bin/");
		if(bwAdminHome.exists()) {
			return bwAdminHome;
		}
		throw new MojoExecutionException("Failed to find Admin Home at location: " + bwAdminHome);
	}
}

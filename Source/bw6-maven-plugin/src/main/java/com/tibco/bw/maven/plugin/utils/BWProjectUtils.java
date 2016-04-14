package com.tibco.bw.maven.plugin.utils;

import org.apache.maven.plugin.MojoExecutionException;

import java.io.File;
import java.io.FileInputStream;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;

public class BWProjectUtils 
{


	public static String getModuleVersion( File jarFile ) throws Exception
	{
		JarInputStream jarStream = new JarInputStream( new FileInputStream( jarFile ));
		Manifest moduleManifest = jarStream.getManifest();
		jarStream.close();
		
		return moduleManifest.getMainAttributes().getValue("Bundle-Version");
	}

	
	public static String getAdminExecutable()
	{
		String os = System.getProperty("os.name");
		if (os.indexOf("Windows") != -1) 
		{
			return "/bwadmin.exe";
		}

		return "/bwadmin";
	}


	public static OS getOS()
	{
		String os = System.getProperty("os.name");
		if (os.indexOf("Windows") != -1) 
		{
			return OS.WINDOWS;
		}

		return OS.UNIX;
	}

	public static String convertMvnVersionToOSGI(String mvnVersion) {
		String convertedVersion="1.0.0.qualifier";

		String[] parts = mvnVersion.replaceAll("-", ".").replaceAll("_", ".").split("\\.");
		if (parts.length == 4){
			String parts3 = parts[3];
			if (parts3.equals("SNAPSHOT") || ! parts3.matches("[0-9]]")){
				parts3 = "qualifier"; // 'qaulifier' means SNAPSHOT according to Tycho conventions for OSGI versioning
			}
			convertedVersion=parts[0]+"."+parts[1]+"."+parts[2]+"."+parts3;
		}
		else if (parts.length == 3){
			// release versions
			convertedVersion=parts[0]+"."+parts[1]+"."+parts[2];
		}

		return convertedVersion;
	}

	public enum OS
	{
		WINDOWS , UNIX
	}

	
	public static File getBWAdminHome( String tibcoHome , String bwVersion ) throws Exception
	{
		
		File bwAdminHome = new File ( new File(tibcoHome) , "bw/"  + bwVersion + "/bin/" );
		
		if(bwAdminHome.exists())
		{
			return bwAdminHome;
		}
		
		throw new MojoExecutionException("Failed to find Admin Home at location : " + bwAdminHome );
		
		
	}
	
	
}

package com.tibco.bw.studio.maven.helpers;

public class VersionHelper 
{

	public static String getOSGi2MavenVersion( String version)
	{
		return version.replaceAll(".qualifier", "-SNAPSHOT");
	}
	
}

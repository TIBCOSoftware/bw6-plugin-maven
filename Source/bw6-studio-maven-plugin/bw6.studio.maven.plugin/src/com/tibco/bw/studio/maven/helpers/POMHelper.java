package com.tibco.bw.studio.maven.helpers;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;

import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;

public class POMHelper
{

	public static  Model readModelFromPOM( File pomXmlFile)
	{
		Model model = null;
		try
		{
			Reader reader = new FileReader(pomXmlFile);
			try {
			    MavenXpp3Reader xpp3Reader = new MavenXpp3Reader();
			    model = xpp3Reader.read(reader);
			} finally {
			    reader.close();
			}
			
		}
		catch( Exception e)
		{
			e.printStackTrace();
		}
		
		return model;
	}

	
}

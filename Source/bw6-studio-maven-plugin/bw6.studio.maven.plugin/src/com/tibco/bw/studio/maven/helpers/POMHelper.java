package com.tibco.bw.studio.maven.helpers;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

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
	
	public static  Model readModelFromPOM( InputStream inputStream){
		Model model = null;
		if(inputStream != null){
			MavenXpp3Reader xpp3Reader = new MavenXpp3Reader();
		    try {
				model = xpp3Reader.read(inputStream);
			} catch (Exception e) {
				e.printStackTrace();
			}
	    }
	return model;
	}
}

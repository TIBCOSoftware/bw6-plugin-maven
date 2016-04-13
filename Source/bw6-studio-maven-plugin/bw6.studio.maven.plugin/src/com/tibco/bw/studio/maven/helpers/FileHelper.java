package com.tibco.bw.studio.maven.helpers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileHelper 
{

	public static String getRelativePath( String path1 , String path2 )
	{
        Path pathAbsolute = Paths.get(path1);
        Path pathBase = Paths.get(path2);
        Path pathRelative = pathBase.relativize(pathAbsolute);
        
        return pathRelative.toString();

	}

	
	
	public static void main( String [] args) throws IOException
	{
		File file = new File ( "C:/temp/" + ".." , "output.txt");
		System.out.println(file.getCanonicalFile().toString());
	}
}



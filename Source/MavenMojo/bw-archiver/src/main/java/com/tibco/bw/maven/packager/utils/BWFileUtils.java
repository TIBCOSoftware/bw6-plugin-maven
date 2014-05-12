package com.tibco.bw.maven.packager.utils;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.Comparator;

public class BWFileUtils 
{
	
	public static File[] getFilesForType( final File target , final String extension )
	{
	    File[] files = target.listFiles( new FileFilter() {
			
				public boolean accept(File pathname) {
					if (pathname.getName().indexOf( extension ) != -1 )
					{
	      			return true;
					}
					return false;
				}
			});
	    
	    return files;
	      		
	}
	
	public static File[] sortFilesByDateDesc( File[] files )
	{
	      Arrays.sort( files, new Comparator<File>()
	    	      {
	    	          public int compare(File o1, File o2) 
	    	          {

	    	              if ((o1).lastModified() > (o2).lastModified()) 
	    	              {
	    	                  return -1;
	    	              } else if (((File)o1).lastModified() < ((File)o2).lastModified()) 
	    	              {
	    	                  return +1;
	    	              } else 
	    	              {
	    	                  return 0;
	    	              }
	    	          }
	    	      });
	      
	      return files;

	}
}

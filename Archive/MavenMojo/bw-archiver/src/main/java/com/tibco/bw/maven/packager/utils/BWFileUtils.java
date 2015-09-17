/*
 * Copyright (c) 2013-2014 TIBCO Software Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

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

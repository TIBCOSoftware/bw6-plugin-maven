package com.tibco.bw.maven.plugin.utils;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.io.FileUtils;

public class BWFileUtils {
	public static File[] getFilesForType(final File target, final String extension) {
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

	@SuppressWarnings("unchecked")
	public static File[] getFilesForTypeRec(final File target, String filterDir, final String extension) {
		String[] extensions = new String[] {"jar"};
		List<File> files = (List<File>) FileUtils.listFiles(target, extensions, true);
		List<File> filesSel = new ArrayList<File>();
		for(File file : files) {
			if(file.getPath().indexOf(filterDir) == -1) {
				filesSel.add(file);
			}
		}
		return filesSel.toArray(new File[] {});
	}

	public static File[] sortFilesByDateDesc(File[] files) {
		Arrays.sort(files, new Comparator<File>() {
			public int compare(File o1, File o2) {
				if ((o1).lastModified() > (o2).lastModified()) {
					return -1;
	            } else if (((File)o1).lastModified() < ((File)o2).lastModified()) {
	                return +1;
	            } else {
	                return 0;
	            }
	        }
	    });
		return files;
	}
}

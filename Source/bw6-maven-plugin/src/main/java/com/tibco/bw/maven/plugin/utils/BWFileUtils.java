package com.tibco.bw.maven.plugin.utils;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.io.FileUtils;

public class BWFileUtils {
	
	 public static final char EXTENSION = '.';

	 public static final String EXTENSION_STR = Character.toString(EXTENSION);
	
	 private static final int NOT_PRESENT = -1;

	 private static final char UNIX_SEPARATOR = '/';

	 private static final char WINDOWS_SEPARATOR = '\\';

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
	
	public static List<File> getEntitiesfromLocation( final String location, final String extension)
	{
		final List<File> list = new ArrayList<File>();
		
		if( location == null || !new File(location).exists ( ))
		{
			return list;
		}
		try
		{
			
			Files.walkFileTree( Paths.get( location ), new FileVisitor<Path>() {

				@Override
				public FileVisitResult preVisitDirectory(Path dir , BasicFileAttributes attrs) throws IOException 
				{
					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult visitFile(Path file , BasicFileAttributes attrs) throws IOException 
				{
					if( getExtension( file.toString() ).equalsIgnoreCase(extension) )
					{
						list.add( file.toFile() );
						return FileVisitResult.CONTINUE;
					}
					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult visitFileFailed(Path file, IOException exc)throws IOException 
				{
					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException 
				{

					return FileVisitResult.CONTINUE;
				}
				
			});

		}
		catch( Exception e )
		{
			
		}
		
		return list ;

	}

    public static String getExtension(final String filename) 
    {
        if (filename == null) {
            return null;
        }
        final int index = indexOfExtension(filename);
        if (index == NOT_PRESENT) {
        	return "";
        } else {
            return filename.substring(index + 1);
        }
    }
    
    public static String replaceLast(String string, String toReplace, String replacement) 
    {
        int pos = string.lastIndexOf(toReplace);
        if (pos > -1) {
            return string.substring(0, pos)
                 + replacement
                 + string.substring(pos + toReplace.length(), string.length());
        } else {
            return string;
        }
    }

    public static int indexOfExtension(final String filename) 
    {
        if (filename == null) {
            return NOT_PRESENT;
        }
        final int extensionPos = filename.lastIndexOf(EXTENSION);
        final int lastSeparator = indexOfLastSeparator(filename);
        return lastSeparator > extensionPos ? NOT_PRESENT : extensionPos;
    }
    
    public static String getFileNameWithoutExtn( String fileName )
    {
        int pos = fileName.lastIndexOf(".");
        String justName = pos > 0 ? fileName.substring(0, pos) : fileName;
    
        return justName;
    }
    
    public static int indexOfLastSeparator(final String filename) 
    {
        if (filename == null) {
            return NOT_PRESENT;
        }
        final int lastUnixPos = filename.lastIndexOf(UNIX_SEPARATOR);
        final int lastWindowsPos = filename.lastIndexOf(WINDOWS_SEPARATOR);
        return Math.max(lastUnixPos, lastWindowsPos);
    }
}

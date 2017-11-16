package com.tibco.bw.maven.plugin.utils;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;

public class BWFileUtils {
	private static List<File> listFiles(final File target, final String extension, final boolean recursive) {
		try {
			final List<File> files = new LinkedList<>();
			Files.walkFileTree(target.toPath(), new SimpleFileVisitor<Path>() {
				@Override
				public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
					Objects.requireNonNull(dir);
					Objects.requireNonNull(attrs);
					if (attrs.isSymbolicLink()) {
						return FileVisitResult.SKIP_SUBTREE;
					}
					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
					if (!attrs.isSymbolicLink() && file.endsWith(extension)) {
						files.add(file.toFile());
					}
					return FileVisitResult.CONTINUE;
				}
			});
			return files;
		} catch (IOException ioex) {
			ioex.printStackTrace();
		}
		return new LinkedList<>();
	}

	public static File[] getFilesForType(final File target, final String extension) {
	    File[] files = target.listFiles( new FileFilter() {
			public boolean accept(File pathname) {
				return pathname.getName().indexOf( extension ) != -1;
			}
		});
	    return files;
	}

	@SuppressWarnings("unchecked")
	public static File[] getFilesForTypeRec(final File target, String filterDir, final String extension) {
		List<File> files = listFiles(target, "jar", true);
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

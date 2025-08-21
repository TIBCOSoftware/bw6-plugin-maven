package com.tibco.bw.maven.plugin.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.Assertions;

public class BWFileUtilsTest {

    @Test
    public void testGetFilesForType() {
         File directory = new File("src/test/resources/textfiles");
		File[] files = BWFileUtils.getFilesForType(directory , "txt");
		Assertions.assertNotNull(files);
    }

    @Test
    public void testGetFilesForTypeRec() {
    	 File directory = new File("src/test/resources/textfiles");
		 // Assuming the directory contains subdirectories with XML files
		 // Adjust the filterDir and extension as needed
		 String filterDir = "subdir"; // Example subdirectory to filter out
    	 File[] files = BWFileUtils.getFilesForTypeRec(directory, "","xml");
    	 Assertions.assertNotNull(files);
    }

    @Test
    public void testSortFilesByDateDesc() {
    	File directory = new File("src/test/resources/textfiles");
         File[] files = BWFileUtils.getFilesForType(directory , "txt");
         File[] sorted = BWFileUtils.sortFilesByDateDesc(files);
         Assertions.assertEquals(files.length, sorted.length);
    }

    @Test
    public void testGetEntitiesfromLocation() {
    	File directory = new File("src/test/resources/textfiles");
    	List<File> files = BWFileUtils.getEntitiesfromLocation(directory.getPath(), "txt");
    	Assertions.assertNotNull(files);
    }
    
    @Test
    public void testFindByFileName() {
    	File directory = new File("src/test/resources/textfiles");
    	
    
		
		 
		 Path path = Paths.get(directory.getAbsolutePath(), "Sample1.txt");
	// Now test finding the file
		 List<Path> pathList;
		try {
			pathList = BWFileUtils.findByFileName(path , "Sample1.txt");
			Assertions.assertTrue(pathList.size() > 0);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
         
    }

    @Test
    public void testGetExtension() {
        String ext = BWFileUtils.getExtension("file.txt");
        Assertions.assertEquals("txt", ext);
    }

    @Test
    public void testReplaceLast() {
        String result = BWFileUtils.replaceLast("foo.bar.bar", "bar", "baz");
        Assertions.assertEquals("foo.bar.baz", result);
    }

    @Test
    public void testIndexOfExtension() {
        int idx = BWFileUtils.indexOfExtension("file.txt");
        Assertions.assertEquals(4, idx);
    }

    @Test
    public void testGetFileNameWithoutExtn() {
        String name = BWFileUtils.getFileNameWithoutExtn("file.txt");
        Assertions.assertEquals("file", name);
    }

    @Test
    public void testIndexOfLastSeparator() {
        int idx = BWFileUtils.indexOfLastSeparator("dir/file.txt");
        Assertions.assertEquals(3, idx);
    }




}



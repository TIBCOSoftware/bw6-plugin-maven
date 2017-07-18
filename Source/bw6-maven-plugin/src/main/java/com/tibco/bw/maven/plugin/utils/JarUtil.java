package com.tibco.bw.maven.plugin.utils;

/**
 * Created by tcosley on 11/1/2016.
 */

import com.tibco.bw.maven.plugin.osgi.helpers.ManifestParser;
import org.apache.maven.plugin.logging.Log;

import java.io.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

public class JarUtil {

    public static File extractJarToTargetFolder(Log log, String projectBasedir, File moduleJar, String newJarName) throws IOException {

        File extractToFolderFile = null;

        JarFile jar = new JarFile(moduleJar);
        String extractToFolder = projectBasedir + File.separator + "target" + File.separator + newJarName + "-exploded";
        log.debug("Extracting " + moduleJar.getName() + " to " + extractToFolder);
        extractToFolderFile = new File(extractToFolder);
        extractToFolderFile.mkdirs();

        Enumeration enumEntries = jar.entries();
        while (enumEntries.hasMoreElements()) {
            JarEntry file = (JarEntry) enumEntries.nextElement();
            File f = new File(extractToFolder + File.separator + file.getName());
            if (file.isDirectory()) { // if its a directory, create it
                f.mkdir();
                continue;
            }
            byte[] byteArray = new byte[1024];
            int i;
            InputStream is = jar.getInputStream(file); // get the input stream
            java.io.FileOutputStream fos = new java.io.FileOutputStream(f);
            while ((i = is.read(byteArray)) > 0) {  // write contents of 'is' to 'fos'
                fos.write(byteArray, 0, i);
            }
            fos.flush();
            fos.close();
            is.close();
        }


        return extractToFolderFile;
    }

    public static File jarUpFolderContents(Log log, String inputFolderPath, String outputFilePath) throws Exception {

        // get files in the directory, not the directory itself..
        File inputFolder = new File(inputFolderPath);
        File[] rawList = inputFolder.listFiles();
        ArrayList<String> contentList = new ArrayList<String>();

        for (File nextRawFile : rawList) {
            log.debug("jarUpFolderContents: NEXT FILE: " + nextRawFile.getName());
            contentList.add(nextRawFile.getName());
        }

        byte[] buffer = new byte[1024];
        File outputFile = null;
        FileOutputStream fos = null;
        JarOutputStream targetJarStream = null;

        try {
            // use manifest already on disk..
            Manifest manifest = ManifestParser.parseManifest(new File(inputFolderPath));

            outputFile = new File(outputFilePath);
            fos = new FileOutputStream(outputFile);
            targetJarStream = new JarOutputStream(fos, manifest);

            log.debug("JarUtil - Output to Jar : " + outputFilePath);
            FileInputStream in = null;

            for (String nextFile : contentList) {
                add(inputFolderPath, nextFile, targetJarStream);
            }
            targetJarStream.flush();

            log.debug("JarUtil - Folder successfully compressed");
        } finally {
            if (targetJarStream != null) {
                targetJarStream.close();
            }
        }
        return outputFile;
    }

    private static void add(String basePath, String relativeFilePath, JarOutputStream targetJarStream) throws IOException {
        BufferedInputStream in = null;
        try {
            String name = relativeFilePath.replace("\\", "/");
            if (name.startsWith("META-INF/MANIFEST.MF")) {
                return; // skip it.. already added when JarOutputStream was created..
            }
            File inputFile = new File(basePath + File.separator + name);

            if (inputFile.isDirectory()) {
                if (!name.isEmpty()) {
                    if (!name.endsWith("/")) {
                        name += "/";
                    }
                    JarEntry entry = new JarEntry(name);
                    entry.setTime(inputFile.lastModified());
                    targetJarStream.putNextEntry(entry);
                    targetJarStream.closeEntry();
                }
                for (File nestedFile : inputFile.listFiles())
                    add(basePath, name + nestedFile.getName(), targetJarStream);
                return;
            }

            JarEntry entry = new JarEntry(name);
            entry.setTime(inputFile.lastModified());
            targetJarStream.putNextEntry(entry);
            in = new BufferedInputStream(new FileInputStream(inputFile));

            byte[] buffer = new byte[1024];
            while (true) {
                int count = in.read(buffer);
                if (count == -1)
                    break;
                targetJarStream.write(buffer, 0, count);
            }
            targetJarStream.closeEntry();
        } finally {
            if (in != null)
                in.close();
        }
    }
}

package com.tibco.bw.maven.plugin.utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BWEarUtils {
	final static Logger logger = LoggerFactory.getLogger(BWEarUtils.class);

	public static void extractEARFile(File earLocation, File EARFile)
			throws IOException {
		logger.debug("Extracting EAR File");
		ZipInputStream zipIn = new ZipInputStream(new FileInputStream(EARFile));
		ZipEntry entry = zipIn.getNextEntry();
		while (entry != null) {
			String filePath = earLocation.getAbsolutePath() + File.separator
					+ entry.getName();
			if (!entry.isDirectory()) {
				extractEARFileEntry(zipIn, filePath);
			} else {
				File dir = new File(filePath);
				dir.mkdir();
			}
			zipIn.closeEntry();
			entry = zipIn.getNextEntry();
		}
		zipIn.close();
	}

	public static void extractEARFileEntry(ZipInputStream zipIn, String filePath)
			throws IOException {
		logger.debug("Extracting EAR File Entries ");
		new File(new File(filePath).getParent()).mkdirs();
		BufferedOutputStream bos = new BufferedOutputStream(
				new FileOutputStream(filePath));
		byte[] bytesIn = new byte[4096];
		int read = 0;
		while ((read = zipIn.read(bytesIn)) != -1) {
			bos.write(bytesIn, 0, read);
		}
		bos.close();
	}

	public static void deleteSubFolder(File element) {
		if (element.isDirectory()) {
			for (File sub : element.listFiles()) {
				deleteSubFolder(sub);
			}
		}
		element.delete();
	}

	public static void deleteEARFileEntries(File earLocation) {
		logger.debug("Deleting EARFile Entries ");
		String[] entries = earLocation.list();
		for (String entry : entries) {
			File currentFile = new File(earLocation.getPath(), entry);
			if (currentFile.getName().contains("META-INF")) {
				deleteSubFolder(currentFile);
			}
			if (currentFile.isDirectory()) {
				deleteEARFileEntries(currentFile);
			}
			if (!(currentFile.getName().contains(".ear")
					|| currentFile.getName().contains("configFile") || currentFile
					.getName().contains("pom"))) {
				currentFile.delete();
			}
		}
	}
}

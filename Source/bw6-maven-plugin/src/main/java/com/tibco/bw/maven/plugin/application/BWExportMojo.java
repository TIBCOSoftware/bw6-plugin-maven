package com.tibco.bw.maven.plugin.application;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.IOUtils;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.BuildPluginManager;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.descriptor.PluginDescriptor;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import com.google.common.io.Files;
import com.tibco.bw.maven.plugin.osgi.helpers.ManifestParser;
import com.tibco.bw.maven.plugin.utils.BWProjectUtils;
import com.tibco.security.AXSecurityException;
import com.tibco.security.ObfuscationEngine;

import static org.twdata.maven.mojoexecutor.MojoExecutor.*;

@Mojo(name = "bwexport")
public class BWExportMojo extends AbstractMojo {

	private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;
	private static final int EOF = -1;

	@Parameter(defaultValue="${session}", readonly=true)
	private MavenSession session;

	@Parameter(defaultValue="${project}", readonly=true)
	private MavenProject project;

	@Parameter(property="project.build.directory")
	private File outputDirectory;

	@Parameter(property="project.basedir")
	private File projectBasedir;

	@Parameter(property="project.type")
	private String projectType;	

	@Parameter(property="binary", defaultValue ="false")
	private boolean binary;

	@Parameter(property="exportPath")
	private String exportPath;

	@Parameter(property="name")
	private String name;

	@Component
	private BuildPluginManager pluginManager;

	@Component
	private PluginDescriptor pluginDescriptor;

	private String earLoc;
	private String earName;
	private String applicationName;
	private ZipOutputStream outputStream;
	private Manifest manifest;

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		try {
			getLog().info("BW Export Mojo started ...");
			if(project == null || !BWProjectUtils.isSharedModule(project)) {
				throw new Exception("Please select a BW shared module to run this goal.");
			}
			if(binary) {
				if(exportPath == null || exportPath.isEmpty()) {
					exportPath = BWProjectUtils.getDefaultMavenRepo();
					exportPath = exportPath.replace("\\", "/");
				}
				if(name == null || name.isEmpty()) {
					name = project.getName() + ".zip";
				}else {
					name = name + ".zip";
				}

				manifest = ManifestParser.parseManifest(projectBasedir);
				String zipPath = exportPath + "/" + name;
				outputStream = new ZipOutputStream(new FileOutputStream(zipPath));
				File[] members = projectBasedir.listFiles();
				for(File member: members) {
					export(member);
				}
				outputStream.close();
				File destDir = Files.createTempDir();
				destDir.deleteOnExit();
				unzip(zipPath, destDir);
				File jarDir = Files.createTempDir();
				File jarFile = new File(jarDir, project.getArtifactId() + "-" + project.getVersion() + ".jar");
				createJar(destDir, jarFile);
				installJar(jarFile);
			}
		}catch(Exception e) {
			getLog().error(e);
			throw new MojoExecutionException("Failed to export the shared module as BSM: ", e);
		}
	}

	private void unzip(String zipPath, File destDir) throws FileNotFoundException, IOException {
		File zip = new File(zipPath);
		try(ZipInputStream zis = new ZipInputStream(new FileInputStream(zip))) {
			ZipEntry entry;
			while((entry = zis.getNextEntry()) != null) {
				File file = new File(destDir, entry.getName());
				if(entry.isDirectory()) {
					file.mkdirs();
				}else {
					file.getParentFile().mkdirs();
					try(FileOutputStream fos = new FileOutputStream(file)) {
						IOUtils.copy(zis, fos);
					}
				}
				zis.closeEntry();
			}
		}
	}

	private void createJar(File sourceDir, File jarFile) throws FileNotFoundException, IOException {
		if(jarFile.exists()) {
			if(!jarFile.delete()) {
				throw new IOException("Failed to delete existing JAR file: " + jarFile.getAbsolutePath());
			}
		}
		try(OutputStream os = java.nio.file.Files.newOutputStream(jarFile.toPath(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING); JarOutputStream jos = new JarOutputStream(os)) {
			Path base = sourceDir.toPath();
			
			// 1. Add META-INF first (if exists)
	        Path metaInf = base.resolve("META-INF/");
	        if(java.nio.file.Files.exists(metaInf)) {
	        	JarEntry metaInfEntry = new JarEntry("META-INF/");
	        	metaInfEntry.setTime(java.nio.file.Files.getLastModifiedTime(metaInf).toMillis());
	        	jos.putNextEntry(metaInfEntry);
	        	jos.closeEntry();
	        }
			
			// 2. Add MANIFEST.MF second (if exists)
	        Path manifestPath = base.resolve("META-INF/MANIFEST.MF");
	        if(java.nio.file.Files.exists(manifestPath)) {
	        	JarEntry manifestEntry = new JarEntry("META-INF/MANIFEST.MF");
	        	manifestEntry.setTime(java.nio.file.Files.getLastModifiedTime(manifestPath).toMillis());
	        	jos.putNextEntry(manifestEntry);
	        	java.nio.file.Files.copy(manifestPath, jos);
	        	jos.closeEntry();
	        }
			
	        // 3. Add other files
			java.nio.file.Files.walk(base)
			.filter(java.nio.file.Files::isRegularFile)
			.filter(tmpPath -> !tmpPath.toString().contains("target"))
			.filter(tmpPath -> !base.relativize(tmpPath).toString().replace("\\", "/").equals("META-INF/MANIFEST.MF"))
			.forEach(path -> {
				try {
					String entryName = base.relativize(path).toString().replace("\\", "/");
					try(InputStream in = java.nio.file.Files.newInputStream(path)) {
						JarEntry entry = new JarEntry(entryName);
						jos.putNextEntry(entry);
						byte[] buffer = new byte[4096];
						int bytesRead;
						while((bytesRead = in.read(buffer)) != -1) {
							jos.write(buffer, 0, bytesRead);
						}
					}
					jos.closeEntry();
				}catch(IOException e) {
					throw new UncheckedIOException("Error adding file to JAR: " + path, e);
				}
			});
		}
	}

	private void installJar(File jarFile) throws MojoExecutionException {
		executeMojo(
				plugin(
						groupId("org.apache.maven.plugins"),
						artifactId("maven-install-plugin"),
						version("3.1.1")
						),
				goal("install-file"),
				configuration(
						element(name("file"), jarFile.getAbsolutePath()),
						element(name("groupId"), project.getGroupId()),
						element(name("artifactId"), project.getArtifactId()),
						element(name("version"), project.getVersion()),
						element(name("packaging"), "jar"),
						element(name("generatePom"), "true")
						),
				executionEnvironment(
						project,
						session,
						pluginManager
						)
				);
	}

	private void export(File member) throws IOException {
		export(member, 1);
	}

	private void export(File member, int leadupDepth) throws IOException {
		if(!member.exists() || !member.canRead()) {
			return;
		}
		if(member.isFile()) {
			String destinationName = getDestinationName(leadupDepth, Paths.get(member.getPath()));
			write(member, destinationName);
		}else {
			File[] children = member.listFiles();
			if(children.length == 0) {
				String destinationName = getDestinationName(leadupDepth, Paths.get(member.getPath()));
				write(member, destinationName + "/");
			}
			for(File child: children) {
				export(child, leadupDepth + 1);
			}
		}
	}

	private void write(File member, String destinationPath) throws IOException {
		ZipEntry newEntry = new ZipEntry(destinationPath);
		if(member.isFile()) {
			write(newEntry, member);
		}else {
			outputStream.putNextEntry(newEntry);
		}
	}

	private void write(ZipEntry entry, File member) {
		try {
			byte[] readBuffer = new byte[4096];
			long localTimeStamp = member.lastModified();
			if(localTimeStamp != 0L) {
				entry.setTime(localTimeStamp);
			}
			outputStream.putNextEntry(entry);
			InputStream contentStream = encrypt(member, false);
			try {
				int n;
				while((n = contentStream.read(readBuffer)) > 0) {
					outputStream.write(readBuffer, 0, n);
				}
			}finally {
				if(contentStream != null) {
					contentStream.close();
				}
			}
			outputStream.closeEntry();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private InputStream encrypt(final File member, final boolean force) {
		try {
			if(member == null) {
				return null;
			}
			if(needToEncrypt(member)) {
				final PipedOutputStream pos = new PipedOutputStream();
				final PipedInputStream pis = new PipedInputStream(pos);
				new Thread() {
					@Override
					public void run() {
						try {
							doEncrypt(member, pos, force);
						}catch(Exception e) {
							e.printStackTrace();
						}finally {
							closeQuietly(pos);
						}
					}
				}.start();
				return pis;
			}else {
				return new FileInputStream(member);
			}
		}catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private void doEncrypt(File member, PipedOutputStream output, boolean force) throws Exception {
		if(member == null || output == null) {
			return;
		}
		InputStream input = null;
		try {
			input = new FileInputStream(member);
			if(input != null) {
				byte[] bytes = toByteArray(input);
				if(bytes != null) {
					String stringContent = new String(bytes);
					if(member.getName().endsWith(".bwp")) {
						String encryptedContent = tibcoEncrypt(stringContent);
						if(encryptedContent != null) {
							byte[] encryptedBytes = encryptedContent.getBytes();
							write(encryptedBytes, output);
						}
					}else if(member.getName().endsWith(".xsd")) {
						String encryptedContent = tibcoEncrypt(stringContent);
						if(encryptedContent != null) {
							byte[] encryptedBytes = encryptedContent.getBytes();
							write(encryptedBytes, output);
						}
					}else if(isSharedResource(member)) {
						String encryptedContent = tibcoEncrypt(stringContent);
						if(encryptedContent != null) {
							byte[] encryptedBytes = encryptedContent.getBytes();
							write(encryptedBytes, output);
						}

					}else {
						if(member.getName().equalsIgnoreCase("MANIFEST.MF")) {
							byte[] morhpedBytes = morphManifestFile(member);
							write(morhpedBytes, output);
						}
					}
				}
			}
		}catch(Exception e) {
			e.printStackTrace();
			throw e;
		}finally {
			if(input != null) {
				closeQuietly(input);
			}
		}
	}

	private byte[] morphManifestFile(File member) throws FileNotFoundException, IOException {
		try(InputStream is = new FileInputStream(member)) {
			Manifest manifest = new Manifest(is);
			manifest.getMainAttributes().put(new Attributes.Name("TIBCO-BW-SharedModuleType"), "binary");
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			manifest.write(os);
			return os.toByteArray();
		}
	}

	private String tibcoEncrypt(String input) throws AXSecurityException {
		return ObfuscationEngine.encrypt(input.toCharArray());
	}

	private String tibcoDecrypt(String input) throws AXSecurityException {
		char decryptChars[] = ObfuscationEngine.decrypt(input);
		return new String(decryptChars);
	}

	private boolean isSharedResource(File member) {
		String resourcesFolder= null;
		File parentFile = member.getParentFile();
		while(parentFile != null && !projectBasedir.equals(parentFile)) {
			resourcesFolder = parentFile.getName();
			parentFile = parentFile.getParentFile();
		}
		if(resourcesFolder != null && resourcesFolder.equals("Resources")) {
			return true;
		}
		return false;
	}

	private static void write(byte[] data, OutputStream output) throws IOException {
		if(data != null) {
			output.write(data);
		}
	}

	private static byte[] toByteArray(InputStream input) throws IOException {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
		int n;
		while(EOF != (n = input.read(buffer))) {
			output.write(buffer, 0, n);
		}
		return output.toByteArray();
	}

	private static void closeQuietly(Closeable closeable) {
		try {
			if(closeable != null) {
				closeable.close();
			}
		}catch(IOException ioe) {

		}
	}

	private boolean needToEncrypt(File member) {
		if(member.getName().endsWith(".bwp") || member.getName().equals("MANIFEST.MF") || isSharedResource(member)) {
			return true;
		}
		return false;
	}

	private String getDestinationName(int leadupDepth, Path path) {
		int segmentCount = path.getNameCount();
		if(segmentCount <= leadupDepth) {
			return "";
		}
		return path.subpath(segmentCount - leadupDepth, segmentCount).toString();
	}

}
package com.tibco.bw.maven.plugin.osgi.helpers;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.execution.MavenSession;

import com.tibco.bw.maven.plugin.utils.Constants;

public class ManifestParser {

	/** Longest line a manifest may have, per the JAR File Specification. */
	private static final int MAX_LINE_BYTES = 72;

	public static Manifest parseManifest(File baseDir) {
		Manifest mf = null;
        File mfile = new File(baseDir , "META-INF/MANIFEST.MF");
        if(mfile.exists())
        {
	        try {
	            mf = readManifest(mfile);
	        } catch(FileNotFoundException f) {
	        	f.printStackTrace();
	        } catch(IOException e) {
	        	e.printStackTrace();
	        }
        }
        return mf;
	}

	/**
	 * Reads a manifest from a file, tolerating physical lines longer than the
	 * {@value #MAX_LINE_BYTES}-byte limit the JAR File Specification imposes.
	 *
	 * java.util.jar.Attributes.read parses a header into a fixed 512-byte buffer and
	 * throws "line too long (line N)" past that. The manifest kept in the project
	 * directory is deliberately written with one physical line per header, so a rename
	 * refactoring in BW Studio does not splice its edits into the middle of a header
	 * (AMBW-55624, see ManifestWriter#writeManifestUnfolded), and a shared module that
	 * exports a couple of dozen schemas runs well past 512 bytes. Without this, the
	 * first build would write such a manifest and the next one would fail to read it
	 * back - "Failed to parse MANIFEST.MF for project". The same applies to manifests
	 * already left unfolded on disk by an earlier 2.11.4 build.
	 */
	public static Manifest readManifest(File mfile) throws IOException {
		byte[] raw = readAllBytes(mfile);
		try {
			return new Manifest(new ByteArrayInputStream(raw));
		} catch(IOException tooLong) {
			// Folding is transparent to the reader - it strips the single leading space of
			// each continuation line and re-joins - so the parsed values are unchanged.
			return new Manifest(new ByteArrayInputStream(refold(raw)));
		}
	}

	private static byte[] readAllBytes(File file) throws IOException {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		try(InputStream is = new FileInputStream(file)) {
			byte[] chunk = new byte[8192];
			int n;
			while((n = is.read(chunk)) > 0) {
				buffer.write(chunk, 0, n);
			}
		}
		return buffer.toByteArray();
	}

	/**
	 * Re-folds every physical line onto continuation lines of at most
	 * {@value #MAX_LINE_BYTES} bytes, leaving lines already within the limit untouched.
	 * This works on physical lines only, so a manifest that is already partly folded is
	 * handled the same way: a continuation line keeps the leading space it arrived with,
	 * and any extra chunk it is split into gets a leading space of its own.
	 */
	private static byte[] refold(byte[] raw) {
		ByteArrayOutputStream out = new ByteArrayOutputStream(raw.length + (raw.length / MAX_LINE_BYTES) + 16);
		int i = 0;
		while(i < raw.length) {
			int end = i;
			while(end < raw.length && raw[end] != '\n' && raw[end] != '\r') {
				end++;
			}
			writeFolded(out, raw, i, end - i);
			if(end < raw.length && raw[end] == '\r') {
				end++;
			}
			if(end < raw.length && raw[end] == '\n') {
				end++;
			}
			i = end;
		}
		return out.toByteArray();
	}

	private static void writeFolded(ByteArrayOutputStream out, byte[] raw, int off, int len) {
		if(len > 0) {
			// The first line carries no leading space, so it holds one byte more than the
			// continuation lines that follow it.
			int chunk = Math.min(len, MAX_LINE_BYTES);
			out.write(raw, off, chunk);
			int pos = off + chunk;
			int remaining = len - chunk;
			while(remaining > 0) {
				out.write('\r');
				out.write('\n');
				out.write(' ');
				int n = Math.min(remaining, MAX_LINE_BYTES - 1);
				out.write(raw, pos, n);
				pos += n;
				remaining -= n;
			}
		}
		out.write('\r');
		out.write('\n');
	}


	public static Manifest parseManifestFromJAR(File jarFile) 
	{
		Manifest moduleManifest = null;
		
		try
		{
			JarInputStream jarStream = new JarInputStream( new FileInputStream( jarFile ));
			moduleManifest = jarStream.getManifest();
			jarStream.close();
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
		
        return moduleManifest;
	}

	public static String getUpdatedProvideCapabilities(Manifest manifest, String oldVersion){
		String updatedProvidesCapabilities = ""; //$NON-NLS-1$
		
		Version versionObject = VersionParser.parseVersion(oldVersion);		
		String newVersion = versionObject.getMajor() + "." + versionObject.getMinor() + "." + versionObject.getMicro();
		if(versionObject.getQualifier()!= null && !versionObject.getQualifier().isEmpty()) {
				newVersion = newVersion+"."+versionObject.getQualifier();
		}

		if(manifest != null){
			String capabilities = manifest.getMainAttributes().getValue(Constants.BUNDLE_PROVIDE_CAPABILITY);
			if(capabilities != null && !capabilities.isEmpty()){
				String[] entries = capabilities.split(",");
				boolean updated = false;
				for(int i = 0; i<entries.length; i++){
					String entry = entries[i];
					String[] filters = entry.split(";");
					if(filters[0].trim().equals("com.tibco.bw.module") ){
						filters[2] = "version:Version=\""+newVersion+"\"";
						updated = true;
					}
					if(updated){
						String newEntry = filters[0].trim() + "; " + filters[1].trim() + "; " + filters[2].trim();
						entries[i] = newEntry;
						break;
					}
				}
				
				if(updated){
					for(int i = 0; i<entries.length; i++){
						String entry = entries[i];
						updatedProvidesCapabilities += entry;
						if(i < entries.length -  1){
							updatedProvidesCapabilities+= ",";
						}
					}
				}
			}
		}
		
		return updatedProvidesCapabilities;
	}
	
	

	public static String getRequiredCapabilities(String reqCapbilitySource, Set<Artifact> listDep, MavenSession session) {

	    StringBuilder processedText = new StringBuilder();

	    if (reqCapbilitySource != null && !reqCapbilitySource.isEmpty()) {
	        processedText.append(reqCapbilitySource.trim());
	    }

	    for (Artifact artifact : listDep) {

	        String artifactId = artifact.getArtifactId();

	        if(artifactId.equals("com.tibco.bw.palette.shared")) {
	        	continue;
	        }
	        // Check if dependency already exists
	        if (reqCapbilitySource != null && reqCapbilitySource.contains("(name=" + artifactId + ")")) {
	            continue;
	        }

	        File file = artifact.getFile();
	        String newVersion = null;
	        boolean isSharedModule = false;

	        try (JarFile jar = new JarFile(file)) {
	            Manifest mf = jar.getManifest();
	            if (mf != null) {
	                newVersion = mf.getMainAttributes().getValue(Constants.BUNDLE_VERSION);
	                isSharedModule = mf.getMainAttributes().getValue(Constants.TIBCO_SHARED_MODULE) != null;
	            }
	        } catch (IOException e) {
	            e.printStackTrace();
	        }

	        // Only TIBCO BW Shared Modules contribute a "com.tibco.bw.module" Require-Capability.
	        // Plain library jars (e.g. zip4j) are placed on the Bundle-ClassPath and are not BW
	        // modules, so they must not be added as a module capability even though they carry a
	        // valid OSGi Bundle-Version.
	        if (!isSharedModule)
	            continue;

	        if (newVersion == null)
	            continue;

	        // Remove qualifier and snapshot
	        if (newVersion.contains(".qualifier")) {
	            newVersion = newVersion.split("\\.qualifier")[0];
	        }

	        if (newVersion.contains("-SNAPSHOT")) {
	            newVersion = newVersion.split("-SNAPSHOT")[0];
	        }

	        String newCapability = "com.tibco.bw.module; filter:=\"(&(name=" 
	                + artifactId + ")(version=" + newVersion + "))\"";

	        if (processedText.length() > 0 && processedText.charAt(processedText.length() - 1) != ',') {
	            processedText.append(",");
	        }

	        processedText.append(newCapability);
	    }

	    return processedText.toString();
	}
}
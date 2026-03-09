package com.tibco.bw.maven.plugin.osgi.helpers;

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

	public static Manifest parseManifest(File baseDir) {
		Manifest mf = null;
        File mfile = new File(baseDir , "META-INF/MANIFEST.MF");
        if(mfile.exists())
        {
	        InputStream is = null;
	        try {
	            is = new FileInputStream(mfile);
	            mf = new Manifest(is);
	        } catch(FileNotFoundException f) {
	        	f.printStackTrace();
	        } catch(IOException e) {
	        	e.printStackTrace();
	        } finally {
	            try {
	            	if(is != null) {
	            		is.close();	
	            	}
				} catch(IOException e) {
					e.printStackTrace();
				}
	        }
        }
        return mf;
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

	        try (JarFile jar = new JarFile(file)) {
	            Manifest mf = jar.getManifest();
	            newVersion = mf.getMainAttributes().getValue(Constants.BUNDLE_VERSION);
	        } catch (IOException e) {
	            e.printStackTrace();
	        }

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
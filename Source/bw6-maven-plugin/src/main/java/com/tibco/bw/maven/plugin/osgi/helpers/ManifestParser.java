package com.tibco.bw.maven.plugin.osgi.helpers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Dependency;

import com.tibco.bw.maven.plugin.utils.BWFileUtils;
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

public static String getRequiredCapabilities(String reqCapbilitySource, List<Dependency> listDep, MavenSession session) {
		
		String processedText = "";
		String listModulesBw="";
		for (Iterator<Dependency> iter = listDep.iterator(); iter.hasNext();) {
			Dependency dep = iter.next();
			Path path = null;
			if(session.getLocalRepository()!= null ) {
				path = Paths.get(session.getLocalRepository().getBasedir());
			}else {
				path = Paths.get(System.getProperty("user.home"), ".m2");
			}
			
			String fileName = dep.getArtifactId().concat("-" + dep.getVersion() + ".jar");
			System.out.println("Searching for jar "+fileName +" at local repo "+path.toString());
			List<Path> result = null;
			try {
				result = BWFileUtils.findByFileName(path, fileName);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			File file = result.get(0).toFile();
			Manifest mf = null;
			try (JarFile jar = new JarFile(file)) {
	             mf = jar.getManifest();
	        } catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
//			String newVersion = dep.getVersion();
			String newVersion = mf.getMainAttributes().getValue(Constants.BUNDLE_VERSION);
			if (newVersion != null && newVersion.contains(".qualifier")) {
				String vers[] = newVersion.split(".qualifier");
				newVersion = vers[0];
			}
			if (newVersion != null && newVersion.contains("-SNAPSHOT")) {
				String vers[] = newVersion.split("-SNAPSHOT");
				newVersion = vers[0];
			}

			String[] entries = reqCapbilitySource.split(",");
			for (int i = 0; i < entries.length; i++) {
				
				String entry = entries[i];
				String[] filters = entry.split(";");

				if (filters[0].trim().equals("com.tibco.bw.module")) {
					System.out.println("com.tibco.bw.module detected");
					if (filters[1].trim().startsWith("filter:=\"(&(name="+ dep.getArtifactId() +")")) {

						listModulesBw += filters[0] + ";" + "filter:=\"(&(name="+dep.getArtifactId()+")(version="+newVersion+"))\"" + ",";
						
					}
				} 
			}

		}
		if(reqCapbilitySource != null && !reqCapbilitySource.isEmpty() && reqCapbilitySource.contains(",")) {
			String[] entriesOthers = reqCapbilitySource.split(",");
			for (int i = 0; i < entriesOthers.length; i++) {
				
				String entry = entriesOthers[i];
				String[] filters = entry.split(";");
				String str = String.join(",", filters);
	
				if (!filters[0].trim().equals("com.tibco.bw.module")) {
					processedText+= entry+",";
				} 
			}
		}
		processedText= listModulesBw+processedText;
		processedText=processedText.replaceAll(",,",",");
		if(processedText.endsWith(","))
			processedText=processedText.substring(0, processedText.length() - 1);
		return processedText;
	}
}
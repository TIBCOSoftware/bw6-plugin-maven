package com.tibco.bw.maven.plugin.test.setuplocal;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.Manifest;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.lang3.StringUtils;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.project.DefaultDependencyResolutionRequest;
import org.apache.maven.project.DependencyResolutionException;
import org.apache.maven.project.DependencyResolutionResult;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.eclipse.aether.graph.Dependency;

import com.tibco.bw.maven.plugin.osgi.helpers.ManifestParser;
import com.tibco.bw.maven.plugin.test.helpers.BWTestConfig;
import com.tibco.bw.maven.plugin.utils.BWFileUtils;
import com.tibco.bw.maven.plugin.utils.Constants;

/**
 * @author sdarekar
 *
 */

public class ESMTestFile {
	List<MavenProject> projects = BWTestConfig.INSTANCE.getSession().getProjects();
	Map<File,String> projectDependancies = new HashMap<File, String>();
	
	/** This method is used to extract ESM at .m2 repo
	 * 
	 */
	public void extractESM(){
		for( MavenProject project : projects )
		{
			if( project.getPackaging().equals("bwmodule") )
			{
				projectDependancies = collectDependeciesFromProject( project);
				checkForSharedModule(projectDependancies);
			}
		}
	}
	
	
	private Map<File,String> collectDependeciesFromProject(MavenProject project){
		HashMap<File,String> artifactFiles = new HashMap<File,String>();
		DependencyResolutionResult resolutionResult = getDependencies(project,BWTestConfig.INSTANCE.getSession());
		if (resolutionResult != null) {
			for(Dependency dependency : resolutionResult.getDependencies()) {
				if(!dependency.getArtifact().getVersion().equals("0.0.0")) {
					artifactFiles.put(dependency.getArtifact().getFile(),dependency.getArtifact().getArtifactId());
				}
			}
		}
		return artifactFiles;
		
	}
	
	private Map<File,String> collectDependeciesFromESM(String project){
		HashMap<File,String> artifactFiles = new HashMap<File,String>();
		File file = new File(project.concat("/pom.xml"));
		if(file.exists()){
			MavenXpp3Reader reader = new MavenXpp3Reader();
			try {
				Model model = reader.read(new FileReader(project.concat("/pom.xml")));
				if(null != model){
					for(org.apache.maven.model.Dependency dependency : model.getDependencies()){
						if(!dependency.getVersion().equals("0.0.0")) {
							Path path = Paths.get(System.getProperty("user.home"), ".m2");
							String fileName=dependency.getArtifactId().concat("-"+dependency.getVersion()+".jar");
							List<Path> result = BWFileUtils.findByFileName(path, fileName);
							artifactFiles.put(result.get(0).toFile(),dependency.getArtifactId());
						}
					}

				}

			} catch (IOException | XmlPullParserException e1) {
				e1.printStackTrace();
			}
		}
		return artifactFiles;

	}
	


	/**This method will check for Shared Module dependency
	 * 
	 *
	 */
	private void checkForSharedModule( Map<File,String> artifactFiles) {
		
		for(File file : artifactFiles.keySet()) {
			if( file.getName().indexOf("com.tibco.bw.palette.shared") != -1 || file.getName().indexOf("com.tibco.xml.cxf.common") != -1 || file.getName().indexOf("tempbw") != -1){
				continue;
			}
			boolean isSharedModule = false;
			Manifest mf = ManifestParser.parseManifestFromJAR( file);
			if(mf == null){
				try {
					throw new Exception("Failed to get Manifest for - "+ file.getName() +". Please verify if jar file is valid, the MANIFEST.MF should be first or second entry in the jar file. Use Command - jar tf <Jar_File_Path> to verify.");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			for( Object str : mf.getMainAttributes().keySet())
			{
				if( Constants.TIBCO_SHARED_MODULE.equals(str.toString() ))
				{
					isSharedModule = true;
					break;
				}
			}
			if(isSharedModule){
				try {
					unzipESM(file, artifactFiles.get(file));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

	}
	
	
	private void unzipESM( File processFile , String module ) throws Exception
	{
		String zipFileName = processFile.getAbsolutePath();
		String dest = StringUtils.substringBefore(processFile.getName(), ".jar");
		String temp = System.getProperty( "java.io.tmpdir" );
		
		try (FileInputStream fis = new FileInputStream(zipFileName);
				BufferedInputStream bis = new BufferedInputStream(fis);
				ZipInputStream stream = new ZipInputStream(bis)) {
			byte[] buffer = new byte[1024];
			ZipEntry zipEntry;
			
			File destDir = new File(temp,dest);
			BWTestConfig.INSTANCE.getESMDirectories().add(destDir);
			while ((zipEntry = stream.getNextEntry()) != null) {  
				File newFile = newFile(destDir, zipEntry);
		     if (zipEntry.isDirectory()) {
		         if (!newFile.isDirectory() && !newFile.mkdirs()) {
		             throw new IOException("Failed to create directory " + newFile);
		         }
		     } else {
		         File parent = newFile.getParentFile();
		         if (!parent.isDirectory() && !parent.mkdirs()) {
		             throw new IOException("Failed to create directory " + parent);
		         }
		         
		         FileOutputStream fos = new FileOutputStream(newFile);
		         int len;
		         while ((len = stream.read(buffer)) > 0) {
		             fos.write(buffer, 0, len);
		         }
		         fos.close();
		     }
		    }
			//extractESMTransitiveDependency(destDir.getAbsolutePath());
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void extractESMTransitiveDependency(String destDirPath) {
		Map<File,String> esmDependancies = new HashMap<File, String>();
		
		esmDependancies = collectDependeciesFromESM(destDirPath);
		checkForSharedModule(esmDependancies);
		
	}


	public  File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {
		destinationDir.mkdir();
	    File destFile = new File(destinationDir, zipEntry.getName());
	    return destFile;
	}
	
	private DependencyResolutionResult getDependencies(MavenProject project, MavenSession session) {
		DependencyResolutionResult resolutionResult = null;

		try {
			DefaultDependencyResolutionRequest resolution = new DefaultDependencyResolutionRequest(project, session.getRepositorySession());
			resolutionResult = BWTestConfig.INSTANCE.getResolver().resolve(resolution);
		} catch (DependencyResolutionException e) {
			e.printStackTrace();
			resolutionResult = e.getResult();
		}
		return resolutionResult;
	}
}

package com.tibco.bw.maven.plugin.test.setuplocal;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Properties;
import java.util.jar.Manifest;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.project.DefaultDependencyResolutionRequest;
import org.apache.maven.project.DependencyResolutionException;
import org.apache.maven.project.DependencyResolutionResult;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectDependenciesResolver;
import org.eclipse.aether.graph.Dependency;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.w3c.dom.Element;

import com.tibco.bw.maven.plugin.osgi.helpers.ManifestParser;
import com.tibco.bw.maven.plugin.test.helpers.BWTestConfig;
import com.tibco.bw.maven.plugin.utils.BWProjectUtils;
import com.tibco.bw.maven.plugin.utils.Constants;

public class ConfigFileGenerator 
{
	ArrayList<String> bundleNamesList = new ArrayList<>();
    Boolean isBundleToStart = false;
    MavenSession session;
    ProjectDependenciesResolver resolver;
    
	public ConfigFileGenerator(MavenSession session, ProjectDependenciesResolver resolver) {
		this.session = session;
		this.resolver = resolver;
	}

	public void generateConfig()
	{
		
		try
		{


			File configIni = new File( BWTestConfig.INSTANCE.getConfigDir() , "config.ini");
			configIni.createNewFile();

			StringBuilder builder = new StringBuilder();
			List<File> targets = getTargetPlatform();
			for( File target : targets )
			{
				addPluginsFromDir(target, builder);
			}
			
			List<MavenProject> requiredDevPropertiesProjects = new ArrayList<MavenProject>();
			
			List<MavenProject> projects =  BWTestConfig.INSTANCE.getSession().getProjects();
	
			for( MavenProject project : projects ) {
				if (project.getPackaging().equals("bwmodule") || project.getPackaging().equals("bwear")) {

					if (isJavaProject(project)) {
						requiredDevPropertiesProjects.add(project);
					}
					
		  			// include all dependencies including transitive in module project
	    			DependencyResolutionResult resolutionResult = getDependenciesResolutionResult(project);
	    			if (resolutionResult != null) {
						    boolean isCXF = false;
	    		        	for(Dependency dependency : resolutionResult.getDependencies()) {
	    		    			if(dependency.getArtifact().getVersion().equals("0.0.0")) { //$NON-NLS-1$
	    		    				continue;
	    		    			}	    		    			
	    		    			
	    		    			org.eclipse.aether.artifact.Artifact aetherArtifact = dependency.getArtifact();
								if(!"provided".equals(dependency.getScope()) && 
										!(aetherArtifact.getFile().getName().contains("com.tibco.bw.palette.shared")) && 
										!(aetherArtifact.getFile().getName().contains("com.tibco.xml.cxf.common")) && 
										 !aetherArtifact.getGroupId().equalsIgnoreCase("tempbw")) {
									builder.append( "," );
									addReference(builder, aetherArtifact.getFile(), aetherArtifact.getArtifactId());
								}
								if(aetherArtifact.getFile().getName().contains("com.tibco.xml.cxf.common")){
									isCXF = true;
								}

	    		        	}
							if(isCXF){
								requiredDevPropertiesProjects.add(project);
							}
	    		      }  
	    			
	    		
				}
			}
			 

			for( MavenProject project : projects )
			{
				if( project.getPackaging().equals("bwmodule") || project.getPackaging().equals("bwear"))
				{
					builder.append( "," );
					addReference(builder,  project.getBasedir() ,project.getBasedir().getName());
				}
				
			}

			Properties properties = new Properties();
			properties.put( "osgi.bundles", builder.toString() );
			properties.put( "osgi.bundles.defaultStartLevel", "5" );
			properties.put( "osgi.install.area", "file:" + BWTestConfig.INSTANCE.getTibcoHome() + BWTestConfig.INSTANCE.getBwHome() + "/system/hotfix/lib/common");
			properties.put("osgi.framework", "file:" + BWTestConfig.INSTANCE.getTibcoHome() + BWTestConfig.INSTANCE.getBwHome() + "/system/lib/common/org.eclipse.osgi_3.16.200.v20210226-1447.jar");
			properties.put("osgi.configuration.cascaded", "false");
			
			FileOutputStream stream = new FileOutputStream(configIni);
			properties.store(stream, "Configuration File"); 
			stream.flush();
			stream.close();
		
			generateDevPropertiesFile(requiredDevPropertiesProjects);
		}
		
		catch(Exception e )
		{
			e.printStackTrace();
		}

	} 

	private DependencyResolutionResult getDependenciesResolutionResult(MavenProject project) {
		DependencyResolutionResult resolutionResult = null;
	    try {
	        DefaultDependencyResolutionRequest resolution = new DefaultDependencyResolutionRequest(project, session.getRepositorySession());
	        resolutionResult = resolver.resolve(resolution);
	    	System.out.println();
        } catch (DependencyResolutionException e) {
        	e.printStackTrace();
            resolutionResult = e.getResult();
        }
		return resolutionResult;
	}
	
	private boolean isJavaProject(MavenProject project) {
		File projectFile = new File(project.getBasedir(), Constants.DOT_PROJECT_FILE);
		NodeList nList = getNatureList(projectFile);  
		if (nList != null) {
			for(int i = 0; i < nList.getLength(); i++) {
				if (nList.item(i) != null) {
					Element node = (Element)nList.item(i);
					String nature = node.getTextContent();
					if (nature != null && nature.equals(Constants.NATURE_JAVE_PROJECT_PROPERTY))
						return true;
				}
			}
		}
		return false;
	}

	private NodeList getNatureList(File dotProject) {
		NodeList nList = null;
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			dbFactory.setNamespaceAware(true);
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(dotProject);
			nList = doc.getElementsByTagName(Constants.NATURE_PROPERTY);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return nList;
	}	
	
	private void generateDevPropertiesFile(List<MavenProject> requiredDevPropertiesProjects) throws IOException, DependencyResolutionRequiredException{
		File devProps = new File( BWTestConfig.INSTANCE.getConfigDir() , "dev.properties");
		devProps.createNewFile();
		
		Properties properties = new Properties();
		properties.put("@ignoredot@","true");
		for(MavenProject reqProject : requiredDevPropertiesProjects)
		{
			//find classpath
			Manifest projectManifest = ManifestParser.parseManifest( reqProject.getBasedir() );
			String bundleClassPath = projectManifest.getMainAttributes().getValue("Bundle-ClassPath");
			BWTestConfig.INSTANCE.getLogger().debug("Bundle-Classpath for project "+ reqProject.getName() +" -> "+bundleClassPath);
			String pathString = "";
			if(bundleClassPath != null)
			{
				String pathEntries[] = bundleClassPath.split(",");
				for(String path : pathEntries){
					if(!path.equals("."))
						pathString += "," + path;
				}
			}
			pathString = "bin,target/classes" + pathString;
			properties.put(reqProject.getName(), pathString);
			BWTestConfig.INSTANCE.getLogger().debug("Adding project entry to dev.properties -> "+ reqProject.getName()+ "="+ pathString);
		}
		
		FileOutputStream stream = new FileOutputStream(devProps);
		properties.store(stream, "dev properties"); 
		stream.flush();
		stream.close();

	}
	
	
	private void addPluginsFromDir( File target , StringBuilder builder )
	{
        
		File []files = target.listFiles();
		for( File file : files )
		{
			//If database drivers are not installed then don't load the lib folder
			File libFolder = new File(file.getAbsolutePath().concat("/lib"));
			if(libFolder.exists()){
				if(libFolder.isDirectory() && libFolder.list().length==0){
					continue;
				}
			}
			
			if( file.getName().contains( "DS_Store"))
			{
				continue;
			}

			String[] split = file.getName().split("_");
			if(bundleNamesList.isEmpty()){
				bundleNamesList.add(split[0]);
				isBundleToStart = true;
			}
			else{
				for(String bundleName : bundleNamesList){
					if(null !=bundleName && bundleName.equals(split[0])){
						isBundleToStart = false;
						break;
					}
					else{
						isBundleToStart = true;
					}
				}
				if(isBundleToStart){
					bundleNamesList.add(split[0]);
				}
				}
			
			if(isBundleToStart){
				if(builder.length() > 0 )
				{
					builder.append( ",");
				}
				addReference( builder , file , split[0]);
			}
		}
		
	}
	
	
	private void addReference(  StringBuilder builder , File file ,String key)
	{

		builder.append("reference:");
		builder.append( "file:");
		builder.append( new com.tibco.bw.maven.plugin.utils.Path(file.getAbsolutePath()).removeTrailingSeparator().toString() );
		builder.append(getStartValue(key));

	}
	
	
	
	
	private List<File> getTargetPlatform()
	{
		File pluginPalettes;
		List<File> list = new ArrayList<>();
		String [] platformDirs = { "system/hotfix/lib/common" , "system/lib/common" , "system/hotfix/palettes" , "system/palettes" , "system/hotfix/shared" , "system/shared","config/drivers/shells/jdbc.oracle.runtime/hotfix/runtime/plugins" ,"config/drivers/shells/jdbc.oracle.runtime/runtime/plugins","config/drivers/shells/jdbc.mysql.runtime/hotfix/runtime/plugins","config/drivers/shells/jdbc.mysql.runtime/runtime/plugins","config/drivers/shells/jdbc.mariadb.runtime/hotfix/runtime/plugins","config/drivers/shells/jdbc.mariadb.runtime/runtime/plugins","config/drivers/shells/jdbc.db2.runtime/hotfix/runtime/plugins","config/drivers/shells/jdbc.db2.runtime/runtime/plugins"};
	
		String bwHomeStr = BWTestConfig.INSTANCE.getTibcoHome() + BWTestConfig.INSTANCE.getBwHome();
		
		if(BWTestConfig.INSTANCE.getBwHome().contains("bwce")){
			pluginPalettes = new File(BWTestConfig.INSTANCE.getTibcoHome().concat("/bwce/palettes")); //If  plugin is installed load the jars for the same
		}
		else{
			pluginPalettes = new File(BWTestConfig.INSTANCE.getTibcoHome().concat("/bw/palettes")); //If plugin is installed load the jars for the same
		}

		if(pluginPalettes.exists() && pluginPalettes.isDirectory()){
			Path [] pluginPlatformDirs = getPluginPlatformDir(pluginPalettes.getPath());
			for(Path pluginFile : pluginPlatformDirs){
				File file = new File(pluginFile.toString(),"plugins");
				if(file.exists()){
					list.add(file); 
				}

			}
		}
		
		File bwHome = new File( bwHomeStr );
		
		for( String str : platformDirs )
		{
			File file = new File(bwHome, str );
			if( file.exists() )
			{
		
				list.add( file );
			}
		}
		
		return list;
		
	}
	
	
	
	private Path[] getPluginPlatformDir(String path) {
		try (Stream<Path> paths = Files.walk(Paths.get(path))) {

			Path[] pluginDirPath = paths.filter(Files::isDirectory)
					.filter(s -> s.endsWith("runtime")).toArray(Path[]::new);

			return pluginDirPath;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}


	private Map<String,String> getStartValuesMap()
	{
		Map<String,String> map = new HashMap<String,String>();
		
		map.put( "com.tibco.tpcl.javax.system.exports" , "" );
		map.put( "com.tibco.bw.extensions.logback" , ""  );
		map.put( "com.tibco.bw.thor.equinox.env" , "" );
		map.put( "com.tibco.neo.eclipse.support.osgi" , "@3:start" );
		map.put( "org.eclipse.osgi.compatibility.state" , "" );
		map.put( "com.tibco.bw.thor.runtime.tools" , "@2:start" );
		map.put( "com.tibco.tpcl.javax.osgi.factories" , "@1:start" );
		map.put( "org.eclipse.equinox.common" , "@2:start" );
		map.put( "com.tibco.tpcl.javax.system.exports.sun" , "" );
		map.put( "org.eclipse.equinox.console.jaas.fragment" , "" );
		
		return map;
		
	}
	
	private String getStartValue( String key )
	{
		Map<String,String> map = getStartValuesMap();
		if( map.containsKey( key ))
		{
			return map.get(key);
			
		}
		
		return "@start";
		
		
	}
}

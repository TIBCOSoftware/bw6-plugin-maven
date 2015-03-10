/*
 * Copyright (c) 2013-2014 TIBCO Software Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.tibco.bw.maven.gatherers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.osgi.util.ManifestElement;
import org.eclipse.pde.internal.core.project.PDEProject;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.tibco.bw.maven.Activator;
import com.tibco.bw.maven.utils.BWAppModuleInfo;
import com.tibco.bw.maven.utils.BWApplicationInfo;
import com.tibco.bw.maven.utils.BWMavenConstants;
import com.tibco.bw.maven.utils.BWModuleInfo;
import com.tibco.bw.maven.utils.BWOSGiModuleInfo;
import com.tibco.bw.maven.utils.BWProjectInfo;
import com.tibco.bw.maven.utils.BWSharedModuleInfo;

/**
 * 
 * Gathers the Project information.
 * 1. Application Projects
 * 2. AppModule Projects
 * 3. Shared Modules
 * 4. OSGi Modules
 * 5. TibcoHome
 * 6. BW Version
 * 
 * @author Ashutosh
 * 
 * @version 1.0
 *
 */
public class BWProjectInfoGatherer implements IBWProjectInfoGatherer

{

	private IProject project ;
	
	private String tibcoHome;
	
	private String bwVersion;
	
	private Map<String, List<String>> dependencies = new HashMap<String, List<String>>();

	
	public BWProjectInfoGatherer( IProject project )
	{
		this.project = project;
	}
	
	/**
	 * Gathers the complete Project Info.
	 */
	public BWProjectInfo gather() throws Exception
	{
		BWProjectInfo info = new BWProjectInfo();
		
		tibcoHome = getTibcoHome();
		bwVersion = getBWVersion();
		
		BWApplicationInfo appInfo = gatherApplicationinfo();
		info.setAppInfo(appInfo);
		appInfo.setProject(project);		
		
		appInfo.setDependencies(dependencies);
		
		
		info.setTibcoHome( tibcoHome);
		
		return info;
		
		
	}
	
	/**
	 * Gathers the Application Info.
	 * 
	 * @return the BWApplicationInfo for the Application project.
	 * 
	 * @throws Exception
	 */
	private BWApplicationInfo gatherApplicationinfo() throws Exception
	{
		BWApplicationInfo info = new BWApplicationInfo();
		
		IFile file = project.getFile("META-INF/TIBCO.xml");

		IFile manifest = PDEProject.getManifest(project);
//		BundleContext context = IDEWorkbenchPlugin.getDefault().getBundle().getBundleContext();
//		Bundle bundle = context.getBundle();
//		bundle.ge
		
		Map<String,String> headers = new HashMap<String,String>(); 
		ManifestElement.parseBundleManifest(new FileInputStream( manifest.getLocation().toFile()), headers);
		
		gatherCommonInfo(project, info, headers);
		
		getModulesForApplication(file.getRawLocation().toFile() , info);
		
		return info;

	}
	
	/**
	 * Gathers the Modules information for the Application project.
	 * The Module includes the AppModules, Shared Modules and OSGi modules.
	 * 
	 * @param file the TIBCO.xml file instance for the Application.
	 * 
	 * @param info the BWApplicationInfo
	 * 
	 * @throws Exception
	 */
	private void getModulesForApplication( File file , BWApplicationInfo info ) throws Exception
	{
		List<BWAppModuleInfo> list = new ArrayList<BWAppModuleInfo>();
		List<BWSharedModuleInfo> sharedlist = new ArrayList<BWSharedModuleInfo>();
		List<BWOSGiModuleInfo> osgiList = new ArrayList<BWOSGiModuleInfo>();

		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		dbFactory.setNamespaceAware(true);

		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(file);
		
		NodeList nList = doc.getElementsByTagNameNS("http://schemas.tibco.com/tra/model/core/PackagingModel" , "module");
		for ( int i = 0 ; i < nList.getLength(); i++)
		{
			Element node = (Element)nList.item(i);			
			NodeList childList = node.getElementsByTagNameNS("http://schemas.tibco.com/tra/model/core/PackagingModel", "symbolicName");
			NodeList techList = node.getElementsByTagNameNS("http://schemas.tibco.com/tra/model/core/PackagingModel", "technologyType");
			
			String module = childList.item(0).getTextContent();
			String technologyType = techList.item(0).getTextContent();
			
			if(technologyType.indexOf("bw-appmodule") != -1 )
			{
				IProject appModuleProject = ResourcesPlugin.getWorkspace().getRoot().getProject(module);
				BWAppModuleInfo appModuleInfo = gatherAppModuleInfo(appModuleProject);
				appModuleInfo.setProject(appModuleProject);
				list.add(appModuleInfo);				
			}
			else if( technologyType.indexOf("bw-sharedmodule") != -1 )
			{
				IProject sharedModuleProject = ResourcesPlugin.getWorkspace().getRoot().getProject(module);
				BWSharedModuleInfo sharedModuleInfo = gatherSharedModuleInfo(sharedModuleProject);
				sharedModuleInfo.setProject(sharedModuleProject);
				sharedlist.add(sharedModuleInfo);
			}
			else if( technologyType.equals(("osgi-bundle") ) )
			{
				IProject osgiModuleProject = ResourcesPlugin.getWorkspace().getRoot().getProject(module);
				BWOSGiModuleInfo osgiModuleInfo = gatherOSGiModuleInfo(osgiModuleProject);
				osgiModuleInfo.setProject(osgiModuleProject);
				osgiList.add(osgiModuleInfo);
			}
			
		}
		
		info.setAppModules(list);
		info.setSharedModules(sharedlist);
		info.setOsgiModules(osgiList);
		

		
	}
	
	/**
	 * Gathers the AppModule information.
	 *  
	 * @param project the IProject project for the App Module project
	 * 
	 * @return BWAppModuleInfo for the App module
	 * 
	 * @throws Exception
	 */
	private BWAppModuleInfo gatherAppModuleInfo( IProject project ) throws Exception
	{
		BWAppModuleInfo info = new BWAppModuleInfo();
		
		IFile manifest = PDEProject.getManifest(project);
		 
		Map<String,String> headers = new HashMap<String,String>(); 
		ManifestElement.parseBundleManifest(new FileInputStream( manifest.getLocation().toFile()), headers);

		gatherCommonInfo(project, info, headers );

		info.setCapabilities( processCapabilites( headers.get("Require-Capability") , info));

		info.setDepSharedModules(dependencies.get(info.getName()));

		return info;
	}
	

	/**
	 * Gathers the OSGi module information.
	 * 
	 * @param project the IProject project for the OSGi Module project
	 * 
	 * @return the  BWOSGiModuleInfo for the OSGi module project.
	 *  
	 * @throws Exception
	 */
	private BWOSGiModuleInfo gatherOSGiModuleInfo( IProject project ) throws Exception
	{
		BWOSGiModuleInfo info = new BWOSGiModuleInfo();
		
		IFile manifest = PDEProject.getManifest(project);
		 
		Map<String,String> headers = new HashMap<String,String>(); 
		ManifestElement.parseBundleManifest(new FileInputStream( manifest.getLocation().toFile()), headers);

		gatherCommonInfo(project, info, headers );

		info.setCapabilities( processCapabilites( headers.get("Require-Capability") , info ));

		
		info.setDepSharedModules(dependencies.get(info.getName()));


		return info;
	}

	/**
	 * Gathers the Shared Module Information.
	 * 
	 * @param project the IProject project for the Shared Module project
	 * 
	 * @return the BWSharedModuleInfo for the Shared Module project
	 * 
	 * @throws Exception
	 */
	private BWSharedModuleInfo gatherSharedModuleInfo( IProject project ) throws Exception
	{
		BWSharedModuleInfo info = new BWSharedModuleInfo();

		IFile manifest = PDEProject.getManifest(project);

		
		Map<String,String> headers = new HashMap<String,String>(); 
		ManifestElement.parseBundleManifest(new FileInputStream( manifest.getLocation().toFile()), headers);
		
		gatherCommonInfo(project, info, headers );

		info.setCapabilities( processCapabilites( headers.get("Require-Capability") , info ));

		
		info.setDepSharedModules(dependencies.get(info.getName()));

		
		return info;
	}
	
	/**
	 * Gathers the common information for the Modules.
	 * 
	 * @param project the Eclipse IProject
	 *  
	 * @param info ModuleInfo
	 * 
	 * @param headers the Manifest Headers
	 * 
	 * @return the updated ModuleInfo
	 * 
	 * @throws Exception
	 */
	private BWModuleInfo gatherCommonInfo( IProject project , BWModuleInfo info ,  Map<String,String> headers) throws Exception
	{
		info.setName( headers.get("Bundle-SymbolicName"));
		info.setVersion(replaceVersion (headers.get("Bundle-Version")) );
		info.setGroupId("com.tibco.bw");
		info.setArtifactId( project.getName() );
		
		IFile pomFile = project.getFile("/pom.xml");
		File pomFileAbs = pomFile.getRawLocation().toFile();
		if (!pomFileAbs.exists()) {
			pomFileAbs.createNewFile();
		}
		info.setPomfileLocation(pomFileAbs);
		info.setTibcoHome(tibcoHome);
		info.setBwVersion(bwVersion);
		return info;

	}

	/**
	 * Replaces the qualifier with Snapshot. This is due to different formats for Maven and OSGi.
	 *  
	 * @param version the OSGi version.
	 * 
	 * @return the Maven version.
	 */
	private String replaceVersion( String version)
	{
		return version.replaceAll(".qualifier", "-SNAPSHOT");
	}
	
	/**
	 * Create the list of Plugins which provides the capabilities. The list is now hardcoded.
	 * 
	 * @param caps the list of capabilities for the given module.
	 * 
	 * @return the List of plugins providing the capability.
	 */
	private List<String> processCapabilites( String caps , BWModuleInfo info )
	{
		List<String> list = new ArrayList<String>();
	
		if( caps == null || caps.equals("") )
		{
			return list;
		}
		String[] capArray = caps.split(",");
		for( String cap : capArray )
		{
			cap = cap.trim();
			
			if( cap.contains("com.tibco.bw.module") )
			{
				processSharedModuleCaps(cap , info );
				continue;
			}
					
			String plugin = BWMavenConstants.capabilities.get(cap );
		
			if(plugin == null || plugin.equals("")  )
			{
				Activator.log("Failed to find module for capability => " + cap , IStatus.WARNING );
				continue;
			}			
			
			list.add(plugin);
		}
		

		
		return list;
	}
	
	private void  processSharedModuleCaps( String cap , BWModuleInfo info )
	{
		List<String> list ;
		
		if (dependencies.containsKey( info.getName() ) )
		{
			dependencies.get( info.getName());
		}
		else
		{
			list = new ArrayList<String>();
			dependencies.put(info.getName(), list );
		}

		if(cap.contains("com.tibco.bw.module"))
		{
			int nameIndex = cap.indexOf("name=");
			int endIndex = cap.indexOf(")" , nameIndex);
			String pluginName = cap.substring( nameIndex + 5, endIndex);
		
			dependencies.get( info.getName()).add( pluginName );	
			
		}
		

	}
	
	
	/**
	 * Gets the Tibco Home. 
	 * Check if its a V-Build or a Dev Build.
	 * The TibcoHome is derived from the "eclipse.launcher" system property.
	 * 
	 * @return the TibcoHome value.
	 * 
	 */
	private String getTibcoHome()
	{
		String tibcoHome = "";
	
		//Check if the Eclipse Launcher contains the Studio property. 
		//That means that it is launched from the Studio.
		String launcher = System.getProperty("eclipse.launcher");
		if(launcher.indexOf("studio") > 0 )
		{
			tibcoHome = launcher.substring(0 , launcher.indexOf("studio") - 1);
		}
		// If not then its launched from the Dev Build.
		else if(launcher.indexOf("eclipse.exe") > 0 )			
		{
			tibcoHome = launcher.substring(0 , launcher.indexOf("eclipse") - 1) + "/tibco.home";
		}
		
		if(tibcoHome == null || tibcoHome.equals("" ))
		{
			tibcoHome = "c:/tibco";
		}
		return tibcoHome;
	}

	/**
	 * The BW Version is returned from the TibcoHome.
	 * It scans the BW Folder for the Version and returns the same.
	 * 
	 * @param tibcoHome the Tibco Home location.
	 * 
	 * @return the BW Version name.
	 */
	private String getBWVersion()
	{

		File file = new File(tibcoHome);
		
		//Find the folder named BW in teh Tibco Home.
		String[] first = file.list(new FilenameFilter() {
			
			@Override
			public boolean accept(File arg0, String arg1) {
				return arg1.equals("bw");				
			}
		});
		
		File bwhome = new File( file , first[0]);
		
		// Find all the folders with name like 6.x in the BW folder. Return the highest one.
		String[] second = bwhome.list( new FilenameFilter() {
			
			@Override
			public boolean accept(File dir, String name) {
				return ( name.indexOf("6") != -1 || name.indexOf("1") != -1);
			}
		});
		
		Arrays.sort(second);
		
		
		return second[ second.length - 1];
	}
	
}

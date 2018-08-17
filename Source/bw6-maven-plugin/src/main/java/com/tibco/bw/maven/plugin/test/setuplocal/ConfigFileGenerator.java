package com.tibco.bw.maven.plugin.test.setuplocal;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.maven.project.MavenProject;

import com.tibco.bw.maven.plugin.test.helpers.BWTestConfig;
import com.tibco.bw.maven.plugin.utils.Path;

public class ConfigFileGenerator 
{

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
			
			List<MavenProject> projects =  BWTestConfig.INSTANCE.getSession().getProjects();
			
			for( MavenProject project : projects )
			{
				if( project.getPackaging().equals("bwmodule") || project.getPackaging().equals("bwear"))
				{
					builder.append( "," );
					addReference(builder,  project.getBasedir() );
				}
				
			}
//			builder.append(",");
//			addReference(builder, new File("D:/Codebase/BW6/Trunk/platform/runtime/plugins/com.tibco.bw.core.runtime.bw.tests"));
//
//			builder.append(",");
//			addReference(builder, new File("D:/Codebase/BW6/Trunk/thor/management/plugins/com.tibco.bw.thor.management.bw.tests"));

			
			
			Properties properties = new Properties();
			properties.put( "osgi.bundles", builder.toString() );
			properties.put( "osgi.bundles.defaultStartLevel", "5" );
			properties.put( "osgi.install.area", "file:" + BWTestConfig.INSTANCE.getTibcoHome() + BWTestConfig.INSTANCE.getBwHome() + "/system/hotfix/lib/common");
			properties.put("osgi.framework", "file:" + BWTestConfig.INSTANCE.getTibcoHome() + BWTestConfig.INSTANCE.getBwHome() + "/system/lib/common/org.eclipse.osgi_3.10.1.v20140909-1633.jar");
			properties.put("osgi.configuration.cascaded", "false");
			
			FileOutputStream stream = new FileOutputStream(configIni);
			properties.store(stream, "Configuration File"); 
			stream.flush();
			stream.close();
		}
		
		catch(Exception e )
		{
			e.printStackTrace();
		}

	} 
	
	
	private void addPluginsFromDir( File target , StringBuilder builder )
	{

		File []files = target.listFiles();
		for( File file : files )
		{
			
			if( file.getName().contains( "DS_Store"))
			{
				continue;
			}
//			File devBundle = getDevBundle( file.getName() );
//			if( devBundle != null )
//			{
//				file = devBundle;
//				System.out.println( "### Found dev " +  file);
//			}
			
			if( builder.length() > 0 )
			{
				builder.append( ",");
			}
			
			
			addReference( builder , file );
		}
		
	}
	
	
	private void addReference(  StringBuilder builder , File file )
	{
		builder.append("reference:");
		builder.append( "file:");
		builder.append( new Path(file.getAbsolutePath()).removeTrailingSeparator().toString() );
		builder.append(getStartValue(file.getName()));

	}
	
	
	
	
	private List<File> getTargetPlatform()
	{
		
		List<File> list = new ArrayList<>();
		String [] platformDirs = { "system/hotfix/lib/common" , "system/lib/common" , "system/hotfix/palettes" , "system/palettes" , "system/hotfix/shared" , "system/shared"};
	
		String bwHomeStr = BWTestConfig.INSTANCE.getTibcoHome() + BWTestConfig.INSTANCE.getBwHome();
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
	
	private File getDevBundle( String name )
	{
		
		if( name.contains( "com.tibco.bx.core_" ))
		{
			return new File("D:/Codebase/BX/Trunk/runtime/engine/plugins/com.tibco.bx.core");
		}
//		if( name.contains("com.tibco.bw.core.runtime_"))
//		{
//			return new File("D:/Codebase/BW6/Trunk/platform/runtime/plugins/com.tibco.bw.core.runtime");
//		}
//		else if( name.contains("com.tibco.bw.core.runtime.api_"))
//		{
//			return new File("D:/Codebase/BW6/Trunk/platform/runtime.api/plugins/com.tibco.bw.core.runtime.api");	
//		}
//		else if( name.contains( "com.tibco.bw.frwk.api_" ))
//		{
//			return new File( "D:/Codebase/BW6/Trunk/thor/bw/plugins/com.tibco.bw.frwk.api");
//
//		}
//		else if( name.contains( "com.tibco.bw.frwk.engine_" ))
//		{
//			return new File("D:/Codebase/BW6/Trunk/thor/bw/plugins/com.tibco.bw.frwk.engine");
//		}
//			
//		else if( name.contains( "com.tibco.bw.runtime_" ))
//		{
//			return new File("D:/Codebase/BW6/Trunk/api/runtime/plugins/com.tibco.bw.runtime");
//		}
		
		
		
		else if( name.contains( "com.tibco.bw.thor.runtime_" ))
		{
			return new File("D:/Codebase/BW6/Trunk/thor/runtime/plugins/com.tibco.bw.thor.runtime");
		}

		else if( name.contains( "com.tibco.bw.frwk_" ))
		{
			return new File("D:/Codebase/BW6/Trunk/thor/bw/plugins/com.tibco.bw.frwk");
		}
		
		else if( name.contains( "com.tibco.bw.thor.management.bw.tests_" ))
		{
			return new File("D:/Codebase/BW6/Trunk/thor/management/plugins/com.tibco.bw.thor.management.bw.tests");
		}
		else if( name.contains( "com.tibco.bw.core.runtime.bw.tests_" ))
		{
			return new File("D:/Codebase/BW6/Trunk/platform/runtime/plugins/com.tibco.bw.core.runtime.bw.tests");
		}
		
		return null;
	}
	
	
	private Map<String,String> getStartValuesMap()
	{
		Map<String,String> map = new HashMap<String,String>();
		
		map.put( "com.tibco.tpcl.javax.system.exports_5.10.0.001" , "" );
		map.put( "com.tibco.bw.extensions.logback_6.3.100.007.jar" , "" );
		map.put( "com.tibco.bw.thor.equinox.env_1.2.900.008.jar" , "" );
		map.put( "com.tibco.neo.eclipse.support.osgi_1.2.0.001@3.jar" , "@3:start" );
		map.put( "org.eclipse.osgi.compatibility.state_1.0.1.v20140709-1414.jar" , "" );
		map.put( "com.tibco.bw.thor.runtime.tools_1.2.900.008@2.jar" , "@2:start" );
		map.put( "com.tibco.tpcl.javax.osgi.factories_1.10.0.001@1.jar" , "@1:start" );
		map.put( "org.eclipse.equinox.common_3.6.200.v20130402-1505.jar" , "@2:start" );
		map.put( "com.tibco.tpcl.javax.system.exports.sun_5.10.0.001" , "" );
		map.put( "org.eclipse.equinox.console.jaas.fragment_1.0.100.001" , "" );
		
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

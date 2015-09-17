package com.tibco.bw.studio.maven.helpers;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import com.tibco.bw.studio.maven.modules.BWModule;
import com.tibco.bw.studio.maven.modules.BWProject;

public class ModuleOrderBuilder 
{

	private List<String> dependencyList = new ArrayList<String>();
	
	public List<String> getDependencyOrder( BWProject project )
	{
		
		Map<String, List<String>> map = project.getDependencies();
		
		BWModule application = ModuleHelper.getApplication( project.getModules() ); 
		BWModule appModule = ModuleHelper.getAppModule( project.getModules() );
		
		computeDependency(map,  appModule.getArtifactId() );
		dependencyList.add( application.getArtifactId() );
		
		return dependencyList;
	}
	
	private void computeDependency(Map<String, List<String>> map, String start) 
	{
		Queue<String> queue = new LinkedList<String>();
		
		if(map.get(start)==null)
		{
			dependencyList.add( start );
			return;
		}

		queue.addAll(map.get(start));
		
		while(!queue.isEmpty())
		{
			String newStart = (String) queue.remove();
			

			if(!dependencyList.contains(newStart)) 
			{
				computeDependency(map, newStart);			
			}
			
		}
		dependencyList.add(start);
	}	

	
}

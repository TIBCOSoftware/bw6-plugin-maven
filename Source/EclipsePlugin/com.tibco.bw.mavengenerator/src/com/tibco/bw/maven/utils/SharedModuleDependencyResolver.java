package com.tibco.bw.maven.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class SharedModuleDependencyResolver 
{
	public static HashMap<String, List<String>> map = new HashMap();
	public static ArrayList<String> resolved = new ArrayList<String>();

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ArrayList<String> arrlist = new ArrayList<String>();
		arrlist.add("S1");
		arrlist.add("S3");
		arrlist.add("S4");
		arrlist.add("S7");
		map.put("AM", arrlist);
		ArrayList<String> arrlist1 = new ArrayList<String>();
		arrlist1.add("S3");
		arrlist1.add("S4");
		arrlist1.add("S7");
		map.put("S1", arrlist1);
		ArrayList<String> arrlist2 = new ArrayList<String>();
		arrlist2.add("S4");
		map.put("S3", arrlist2);
		ArrayList<String> arrlist3 = new ArrayList<String>();
		arrlist3.add("S2");
		map.put("S4", arrlist3);
		map.put("S2", null);
		map.put("S7", null);
		System.out.println(map);
		
		String startingNode = "AM";
		
		resolveDependency(map, startingNode);
		
		System.out.println("bundle resolve sequence "+resolved);
	}

	static void resolveDependency(HashMap<String, List<String>> map, String start) 
	{
		Queue<String> queue = new LinkedList<String>();
		
		if(map.get(start)==null)
		{
			resolved.add(start);
			return;
		}

		queue.addAll(map.get(start));
		
		while(!queue.isEmpty())
		{
			String newStart = (String) queue.remove();
			

			if(!resolved.contains(newStart)) 
			{
				resolveDependency(map, newStart);			
			}
			
		}
		resolved.add(start);
	}

}

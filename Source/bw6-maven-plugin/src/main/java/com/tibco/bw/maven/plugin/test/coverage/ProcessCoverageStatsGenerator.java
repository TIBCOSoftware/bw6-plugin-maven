package com.tibco.bw.maven.plugin.test.coverage;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ProcessCoverageStatsGenerator 
{
	 private static DecimalFormat format = new DecimalFormat("#.##");

	
	private OverAllStats stats = new OverAllStats();
	private List<ProcessStats> processStats = new ArrayList<ProcessCoverageStatsGenerator.ProcessStats>();
	
	public void generateStats ( Map<String,ProcessCoverage> map )
	{
	
		for( ProcessCoverage cov : map.values() )
		{
			stats.addModule( cov.getModuleName() );
			stats.addProcess( cov.getProcessName() );
			if( cov.isProcessExecuted() )
			{
				stats.addModuleCovered( cov.getModuleName() );
				stats.addProcessCovered( cov.getProcessName() );
				
			}
			stats.addActivities( cov.getActivities().size() );
			stats.addActivitiesCovered( cov.getActivitiesExec().size() );

			stats.addTransition( cov.getTransitions().size() );
			stats.addTransitionCovered( cov.getTransitionExec().size());

			ProcessStats pstat = new ProcessStats();
			pstat.setModuleName( cov.getModuleName() );
			pstat.setProcessName( cov.getProcessName() );
			pstat.setTotalActivities( cov.getActivities().size() );
			pstat.setCoveredActivities( cov.getActivitiesExec().size() );
			pstat.setTotalTransitions( cov.getTransitions().size() );
			pstat.setCoveredTransitions( cov.getTransitionExec().size());
			processStats.add(pstat);
		}
		
		
		
	}
	
	
	public OverAllStats getStats() {
		return stats;
	}

	public List<ProcessStats> getProcessStats() {
		return processStats;
	}

	
	
	
	public static class OverAllStats
	{
		private Set<String> totalModules = new HashSet<>();
		
		private Set<String> coveredModules = new HashSet<>();
		
		private Set<String> totalProcess = new HashSet<>();

		private Set<String> coveredProcess = new HashSet<>();
		
		private int totalTransitions;
		
		private int totalActivities;
		
		private int coveredTransitions;
		
		private int coveredActivities;
		
		public void addModule( String module )
		{
			totalModules.add(module);
		}
		
		public void addModuleCovered( String module )
		{
			coveredModules.add(module);
		}
		
		public void addProcess( String process )
		{
			totalProcess.add(process);
		}
		
		public void addProcessCovered( String process )
		{
			coveredProcess.add(process);
		}

		public void addActivities( int count )
		{
			totalActivities = totalActivities + count;
		}
		
		public void addActivitiesCovered( int count )
		{
			coveredActivities = coveredActivities + count;
		}

		public void addTransition( int count )
		{
			totalTransitions = totalTransitions + count;
		}
		
		public void addTransitionCovered( int count )
		{
			coveredTransitions = coveredTransitions + count;
		}		
		
		public String getModuleStat()
		{
			
			int total = totalModules.size();
			int covered  = coveredModules.size();
			
			float success = 0;
			
			if( total == covered )
			{
				success = 100;
			}
			else if ( covered == 0 )
			{
				success = 0;
			}else
			{
				success = ((float)covered  )/ ((float) total) * 100;
			}
			
			return  format.format(success) + "%  (" + covered + " / "  + total + ")";
		}
		
		public String getProcessStat()
		{
			
			int total = totalProcess.size();
			int covered  = coveredProcess.size();
			
			float success = 0;
			
			if( total == covered )
			{
				success = 100;
			}
			else if ( covered == 0 )
			{
				success = 0;
			}else
			{
				success = ((float)covered  )/ ((float) total) * 100;
			}
			

			return  format.format(success) + "%  (" + covered + " / "  + total + ")";
		}

		
		public String getActivityStat()
		{
			
			int total = totalActivities;
			int covered  = coveredActivities;
			
			float success = 0;
			
			if( total == covered )
			{
				success = 100;
			}
			else if ( covered == 0 )
			{
				success = 0;
			}else
			{
				success = ((float)covered  )/ ((float) total) * 100;
			}
			

			return  format.format(success) + "%  (" + covered + " / "  + total + ")";
		}

		public String getTransitionStat()
		{
			
			int total = totalTransitions;
			int covered  = coveredTransitions;
			
			float success = 0;
			
			if( total == covered )
			{
				success = 100;
			}
			else if ( covered == 0 )
			{
				success = 0;
			}else
			{
				success = ((float)covered  )/ ((float) total) * 100;
			}
			

			return  format.format(success) + "%  (" + covered + " / "  + total + ")";
		}

		
	}
	
	public static class ProcessStats
	{
		private String processName;
		
		private String moduleName;
		
		private int totalTransitions;
		
		private int totalActivities;
		
		private int coveredTransitions;
		
		private int coveredActivities;
		
		
		public String getActivityStat()
		{
			
			int total = totalActivities;
			int covered  = coveredActivities;
			
			float success = 0;
			
			if( total == covered )
			{
				success = 100;
			}
			else if ( covered == 0 )
			{
				success = 0;
			}else
			{
				success = ((float)covered  )/ ((float) total) * 100;
			}
			

			return  format.format(success) + "%  (" + covered + " / "  + total + ")";
		}

		public String getTransitionStat()
		{
			
			int total = totalTransitions;
			int covered  = coveredTransitions;
			
			float success = 0;
			
			if( total == covered )
			{
				success = 100;
			}
			else if ( covered == 0 )
			{
				success = 0;
			}else
			{
				success = ((float)covered  )/ ((float) total) * 100;
			}
			

			return  format.format(success) + "%  (" + covered + " / "  + total + ")";
		}


		public String getProcessName() {
			return processName;
		}

		public void setProcessName(String processName) {
			this.processName = processName;
		}

		public String getModuleName() {
			return moduleName;
		}

		public void setModuleName(String moduleName) {
			this.moduleName = moduleName;
		}

		public int getTotalTransitions() {
			return totalTransitions;
		}

		public void setTotalTransitions(int totalTransitions) {
			this.totalTransitions = totalTransitions;
		}

		public int getTotalActivities() {
			return totalActivities;
		}

		public void setTotalActivities(int totalActivities) {
			this.totalActivities = totalActivities;
		}

		public int getCoveredTransitions() {
			return coveredTransitions;
		}

		public void setCoveredTransitions(int coveredTransitions) {
			this.coveredTransitions = coveredTransitions;
		}

		public int getCoveredActivities() {
			return coveredActivities;
		}

		public void setCoveredActivities(int coveredActivities) {
			this.coveredActivities = coveredActivities;
		}
		
		
	}
	
}

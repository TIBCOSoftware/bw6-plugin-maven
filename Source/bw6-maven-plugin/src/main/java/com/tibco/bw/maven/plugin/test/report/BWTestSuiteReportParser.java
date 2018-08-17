package com.tibco.bw.maven.plugin.test.report;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.tibco.bw.maven.plugin.test.dto.AssertionResultDTO;
import com.tibco.bw.maven.plugin.test.dto.CompleteReportDTO;
import com.tibco.bw.maven.plugin.test.dto.TestCaseResultDTO;
import com.tibco.bw.maven.plugin.test.dto.TestSetResultDTO;
import com.tibco.bw.maven.plugin.test.dto.TestSuiteResultDTO;

public class BWTestSuiteReportParser 
{
	
	 private static DecimalFormat format = new DecimalFormat("#.##");


	private CompleteReportDTO completeResult ;
	private Summary summary;
	private Map<String, PackageTestDetails> packageMap = new HashMap<>();
	
	public BWTestSuiteReportParser( CompleteReportDTO result )
	{
		this.completeResult = result;
		summary = new Summary();
		parse();
	}
	
	public Map<String, PackageTestDetails> getPackageMap() 
	{
		return packageMap;
	}

	public void setPackageMap(Map<String, PackageTestDetails> packageMap) 
	{
		this.packageMap = packageMap;
	}

	private void parse()
	{
		
		for( int count = 0 ; count < completeResult.getModuleResult().size() ; count++ )
		{
			TestSuiteResultDTO result = (TestSuiteResultDTO) completeResult.getModuleResult().get( count );
		
		
			for( int i =0 ; i < result.getTestSetResult().size() ; i++ )
			{
				TestSetResultDTO testset = (TestSetResultDTO) result.getTestSetResult().get( i );
	
				String testPackage = testset.getPackageName();
	
				PackageTestDetails packageDetails = null;
				
				if( packageMap.containsKey(testPackage) )
				{
					packageDetails = packageMap.get(testPackage);
				}
				else
				{
					packageDetails = new PackageTestDetails();
					packageDetails.setModuleName( result.getModuleInfo().getModuleName() );
					packageDetails.setPackageName(testPackage);
					packageMap.put(testPackage, packageDetails);
				}
				
				
				ProcessTestDetails processDetails = null;
				
				for( ProcessTestDetails tempProcess : packageDetails.getProcessDetails() )
				{
					if( tempProcess.getProcessName().equals( testset.getProcessName() ))
					{
						processDetails = tempProcess;
						break;
					}
					
				}
					
				if( processDetails == null )
				{
					processDetails = new ProcessTestDetails();
					processDetails.setProcessName( testset.getProcessName() );
					packageDetails.getProcessDetails().add(processDetails);
				}
				
				
				
				for( int j = 0 ; j < testset.getTestCaseResult().size() ; j++ )
				{
					TestCaseResultDTO testcase = (TestCaseResultDTO) testset.getTestCaseResult().get( j );
					
					ProcessFileTestDetails fileDetails = new ProcessFileTestDetails();
					fileDetails.setFileName( testcase.getTestCaseFile() );
					fileDetails.setTotalAssertions( testcase.getAssertionsRun());
					
					processDetails.getFileTestDetails().add(fileDetails);
					
					for( int assercount = 0 ; assercount < testcase.getAssertionResult().size()  ; assercount++ )
					{
						AssertionResultDTO aresult = (AssertionResultDTO) testcase.getAssertionResult().get(  assercount );
						if( aresult.getAssertionStatus().equals("failed"))
						{
							fileDetails.addAssertionFailure(aresult.getActivityName() );
						}
					}
					
					processDetails.incrementTotalTests();
					packageDetails.incrementTotalTests();
					
					if( testcase.getAssertionFailure() > 0 )
					{
						processDetails.incrementFailures();
						packageDetails.incrementFailures();
						fileDetails.incrementFailures();
					}
				}
			}
		}
	}
	
	
	public Summary getSummary() 
	{
		return summary;
	}



	public void setSummary(Summary summary) 
	{
		this.summary = summary;
	}


	public class PackageTestDetails
	{
		private String moduleName;
		private String packageName;
		private int totalTests;
		private int errors;
		private int failures;
		private int skipped;
		
		private List<ProcessTestDetails> processDetails = new ArrayList<>();
		
		public String getPackageName() 
		{
			return packageName;
		}
		public String getModuleName() 
		{
			return moduleName;
		}
		public void setModuleName(String moduleName) 
		{
			this.moduleName = moduleName;
		}
		public void setPackageName(String packageName) 
		{
			this.packageName = packageName;
		}
		public int getTotalTests() 
		{
			return totalTests;
		}
		public void incrementTotalTests() 
		{
			this.totalTests++;
		}
		public int getErrors() 
		{
			return errors;
		}
		public void incrementErrors()
		{
			this.errors++;
		}
		public int getFailures()
		{
			return failures;
		}
		public void incrementFailures()
		{
			this.failures++;
		}
		public int getSkipped() {
			return skipped;
		}
		public void incrementSkipped()
		{
			this.skipped++ ;
		}
		public String getSuccessRate() 
		{
			float success = 0;
			
			if( failures == 0 )
			{
				success = 1 * 100;
			}
			else if( failures == totalTests )
			{
				success = 0;
			}
			else 
			{
				success = ((float)(totalTests - failures) /(float) totalTests) * 100;
			}
			return String.valueOf( format.format(success) );


		}
		public List<ProcessTestDetails> getProcessDetails() 
		{
			return processDetails;
		}
		public void setProcessDetails(List<ProcessTestDetails> map) 
		{
			this.processDetails = map;
		}
		
		
	}
	
	public class ProcessTestDetails
	{
		private String processName;
		private int totalTests;
		private int errors;
		private int failures;
		private int skipped;
		
		private List<ProcessFileTestDetails> fileTestDetails = new ArrayList<ProcessFileTestDetails>();
		
		public ProcessTestDetails()
		{
			
		}
		
		
		
		public ProcessTestDetails(String processName, int totalTests,int errors, int failures, int skipped) 
		{
			this.processName = processName;
			this.totalTests = totalTests;
			this.errors = errors;
			this.failures = failures;
			this.skipped = skipped;
		}



		public String getProcessName() 
		{
			return processName;
		}
		public void setProcessName(String processName)
		{
			this.processName = processName;
		}
		public int getTotalTests() 
		{
			return totalTests;
		}
		public void incrementTotalTests()
		{
			this.totalTests++;
		}
		public int getErrors() 
		{
			return errors;
		}
		public void incrementErrors() 
		{
			this.errors++;
		}
		public int getFailures() 
		{
			return failures;
		}
		public void incrementFailures() 
		{
			this.failures++;
		}
		public int getSkipped() 
		{
			return skipped;
		}
		public void incrementSkipped() 
		{
			this.skipped++;
		}
		public String getSuccessRate() 
		{
			float success = 0;
			
			if( failures == 0 )
			{
				success = 1 * 100;
			}
			else if( failures == totalTests )
			{
				success = 0;
			}
			else 
			{
				success = ((float)(totalTests - failures) / (float)totalTests) * 100;
			}
			return String.valueOf( format.format(success) );
			}



		public List<ProcessFileTestDetails> getFileTestDetails()
		{
			return fileTestDetails;
		}


		public void setFileTestDetails(List<ProcessFileTestDetails> fileTestDetails)
		{
			this.fileTestDetails = fileTestDetails;
		}
		
	}
	
	
	public class ProcessFileTestDetails
	{
		
		private String fileName;
		private int totalAssertions;
		private int failures;
		private List<String> assertionFailures = new ArrayList<>();
		
		public ProcessFileTestDetails()
		{
			
		}
		
		public ProcessFileTestDetails(String fileName, int totalAssertions) 
		{
			this.fileName = fileName;
			this.totalAssertions = totalAssertions;
		}

		public String getFileName() 
		{
			return fileName;
		}
		public void setFileName(String fileName) 
		{
			this.fileName = fileName;
		}
		public int getTotalAssertions() 
		{
			return totalAssertions;
		}
		public void setTotalAssertions(int totalAssertions) 
		{
			this.totalAssertions = totalAssertions;
		}

		public int getFailures() 
		{
			return failures;
		}
		
		public void incrementFailures()
		{
			this.failures++;
		}

		public List<String> getAssertionFailures() 
		{
			return assertionFailures;
		}

		public void setAssertionFailures(List<String> assertionFailures) {
			this.assertionFailures = assertionFailures;
		}
		
		public void addAssertionFailure( String str)
		{
			assertionFailures.add(str);
		}
		
	}
	

	public class Summary
	{
		
		public String getTotalTests()
		{
			int totaltests = 0;
			
			for( int count = 0 ; count < completeResult.getModuleResult().size() ; count++ )
			{
				TestSuiteResultDTO result = (TestSuiteResultDTO) completeResult.getModuleResult().get( count );
				for( int i = 0 ; i < result.getTestSetResult().size() ; i++ )
				{
					
					totaltests = totaltests + ((TestSetResultDTO)result.getTestSetResult().get(i)).getTestCaseResult().size();
				}

			}
			
			
			return String.valueOf(totaltests);
		}
		
		public String getErrors()
		{
			return "0";
		}
		
		public String getSkipped()
		{
			return "0";
		}
		
		public String getFailures()
		{
			
			int totalfailure = 0;
			
			for( int count = 0 ; count < completeResult.getModuleResult().size() ; count++ )
			{
				TestSuiteResultDTO result = (TestSuiteResultDTO) completeResult.getModuleResult().get( count );
				for( int i =0 ; i < result.getTestSetResult().size() ; i++ )
				{
					TestSetResultDTO testset = (TestSetResultDTO) result.getTestSetResult().get( i );

					
					for( int j = 0 ; j < testset.getTestCaseResult().size() ; j++ )
					{
						TestCaseResultDTO testcase = (TestCaseResultDTO) testset.getTestCaseResult().get( j );
						if( testcase.getAssertionFailure() > 0 )
						{
							totalfailure++;
						}
						else
						{

						}
					}
				}

			}
			return String.valueOf(totalfailure);
		}
		
		public String getPercentage()
		{
			float success = 0;

			if( Integer.valueOf(getFailures()) == 0 )
			{
				success = 100;
			}
			else if( Integer.valueOf(getFailures()) == Integer.valueOf(getTotalTests()) )
			{
				success = 0;
			}
			else 
			{
				success = (((float)(Integer.valueOf(getTotalTests()) - Integer.valueOf(getFailures()) )/ ((float)Integer.valueOf(getTotalTests())) ))* 100;
			}
			return String.valueOf( format.format(success) );
		}
		
	}
	
	
	public class Packages
	{
		
	}
	
	
}

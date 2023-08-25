package com.tibco.bw.maven.plugin.test.report;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.tibco.bw.maven.plugin.test.dto.AssertionResultDTO;
import com.tibco.bw.maven.plugin.test.dto.BWTestSuiteDTO;
import com.tibco.bw.maven.plugin.test.dto.CompleteReportDTO;
import com.tibco.bw.maven.plugin.test.dto.TestCaseResultDTO;
import com.tibco.bw.maven.plugin.test.dto.TestSetResultDTO;
import com.tibco.bw.maven.plugin.test.dto.TestSuiteResultDTO;
import com.tibco.bw.maven.plugin.test.helpers.BWTestConfig;
import com.tibco.bw.maven.plugin.test.helpers.TestFileParser;

public class BWTestSuiteReportParser 
{
	
	 private static DecimalFormat format = new DecimalFormat("#.##");


	private CompleteReportDTO completeResult ;
	private Summary summary;
	private boolean showFailureDetails = false;
	private Map<String, PackageTestDetails> packageMap = new HashMap<>();
	private Map<String, TestSuiteDetails> testSuiteMap = new HashMap<>();
	private Map<String, String> testCaseWithProcessMap = new HashMap<>();
	
	public BWTestSuiteReportParser( CompleteReportDTO result )
	{
		this.completeResult = result;
		summary = new Summary();
		if(null != BWTestConfig.INSTANCE.getTestSuiteName() && !BWTestConfig.INSTANCE.getTestSuiteName().isEmpty()){
			parseTestSuiteWise();
		}
		else{
			parse();
		}
		
	}
	
	public Map<String, String> getTestCaseWithProcessMap() {
		return testCaseWithProcessMap;
	}
	
	public void setTestCaseWithProcessMap(Map<String, String> testCaseWithProcessMap) {
		this.testCaseWithProcessMap = testCaseWithProcessMap;
	}
	
	
	public Map<String, PackageTestDetails> getPackageMap() 
	{
		return packageMap;
	}

	public void setPackageMap(Map<String, PackageTestDetails> packageMap) 
	{
		this.packageMap = packageMap;
	}

	
	public Map<String, TestSuiteDetails> getTestSuiteMap() 
	{
		return testSuiteMap;
	}

	public void setTestSuiteMap(Map<String, TestSuiteDetails> testSuiteMap) 
	{
		this.testSuiteMap = testSuiteMap;
	}
	
	private void parse()
	{
		
		for( int count = 0 ; count < completeResult.getModuleResult().size() ; count++ )
		{
			TestSuiteResultDTO result = (TestSuiteResultDTO) completeResult.getModuleResult().get( count );
			
			setShowFailureDetails( TestFileParser.INSTANCE.getshowFailureDetails());
		
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
							StringBuilder assertionFailureDataBuilder = new StringBuilder();
							String activityName = aresult.getActivityName();
							fileDetails.addAssertionFailure(activityName);
							if (!activityName.equals("N/A")) {
								assertionFailureDataBuilder.append(" Assertion Failed For Activity with name "+"["+activityName+"]");
								assertionFailureDataBuilder.append(" [Reason] - Validation failed against Gold file. Please compare Activity output against Gold output values");
								assertionFailureDataBuilder.append(" [Activity Output:  "+aresult.getActivityOutput()+"]");
								assertionFailureDataBuilder.append(" [Gold Output:  "+aresult.getGoldInput()+"]");
							}
							else {
								assertionFailureDataBuilder.append(" Assertion Failed For Process ");
								assertionFailureDataBuilder.append(" [Reason] - Could be a read input or write output error.  Please check error message on console");							
							}
							fileDetails.addFailureData(assertionFailureDataBuilder.toString());
							
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
					else if(testcase.getProcessFailures()>0){
						processDetails.incrementErrors();
						packageDetails.incrementErrors();
						fileDetails.incrementErrors();
					}
				}
			}
		}
	}
	
	
	private void parseTestSuiteWise()
	{
		
		for( int count = 0 ; count < completeResult.getModuleResult().size() ; count++ )
		{
			TestSuiteResultDTO result = (TestSuiteResultDTO) completeResult.getModuleResult().get( count );
			
			setShowFailureDetails( TestFileParser.INSTANCE.getshowFailureDetails());
		
			for( int i =0 ; i < result.getBWTestSuite().size() ; i++ )
			{
				BWTestSuiteDTO testsuite = (BWTestSuiteDTO) result.getBWTestSuite().get( i );
				
				if( testCaseWithProcessMap.isEmpty()){
					testCaseWithProcessMap.putAll(testsuite.getTestCaseWithProcessNameMap());
				}
				
				String suiteName = testsuite.getTestSuiteName();
				
				TestSuiteDetails testSuiteDetails = null;
				
				
				if( testSuiteMap.containsKey(suiteName) )
				{
					testSuiteDetails = testSuiteMap.get(suiteName);
				}
				else
				{
					testSuiteDetails = new TestSuiteDetails();
					testSuiteDetails.setModuleName( result.getModuleInfo().getModuleName() );
					testSuiteDetails.setTestSuiteName(suiteName);
					testSuiteMap.put(suiteName, testSuiteDetails);
					
				}
				
				
				ProcessTestDetails processDetails = null;

				for( ProcessTestDetails tempProcess : testSuiteDetails.getProcessDetails() )
				{
					if( tempProcess.getSuiteName().equals( testsuite.getTestSuiteName() ))
					{
						processDetails = tempProcess;
						break;
					}
					
				}
				if( processDetails == null )
				{
					processDetails = new ProcessTestDetails();
					processDetails.setSuiteName( testsuite.getTestSuiteName() );
					testSuiteDetails.getProcessDetails().add(processDetails);
				}
				@SuppressWarnings("unchecked")
				List<TestCaseResultDTO> testCaseList = testsuite
						.getTestCaseList();
				
				for (TestCaseResultDTO testCase :testCaseList) {
				{
					
					
					ProcessFileTestDetails fileDetails = new ProcessFileTestDetails();
					fileDetails.setFileName( testCase.getTestCaseFile() );
					fileDetails.setTotalAssertions( testCase.getAssertionsRun());
					
					processDetails.getFileTestDetails().add(fileDetails);
					
					for( int assercount = 0 ; assercount < testCase.getAssertionResult().size()  ; assercount++ )
					{
						AssertionResultDTO aresult = (AssertionResultDTO) testCase.getAssertionResult().get(  assercount );
						if( aresult.getAssertionStatus().equals("failed"))
						{
							StringBuilder assertionFailureDataBuilder = new StringBuilder();
							fileDetails.addAssertionFailure(aresult.getActivityName() );
							assertionFailureDataBuilder.append(" Assertion Failed For Activity with name "+"["+aresult.getActivityName()+"]");
							assertionFailureDataBuilder.append(" in Sub-Process ["+ testsuite.getTestCaseWithProcessNameMap().get(testCase.getTestCaseFile()) + "]");
							assertionFailureDataBuilder.append(" in Test Suite ["+ testsuite.getTestSuiteName() + "]");
							assertionFailureDataBuilder.append(" [Reason] - Validation failed against Gold file. Please compare Activity output against Gold output values");
							assertionFailureDataBuilder.append(" [Activity Output:  "+aresult.getActivityOutput()+"]");
							assertionFailureDataBuilder.append(" [Gold Output:  "+aresult.getGoldInput()+"]");
							fileDetails.addFailureData(assertionFailureDataBuilder.toString());
							
						}
					}
					
					processDetails.incrementTotalTests();
					testSuiteDetails.incrementTotalTests();
					
					if( testCase.getAssertionFailure() > 0 )
					{
						processDetails.incrementFailures();
						testSuiteDetails.incrementFailures();
						fileDetails.incrementFailures();
					}
					else if(testCase.getProcessFailures()>0){
						processDetails.incrementErrors();
						testSuiteDetails.incrementErrors();
						fileDetails.incrementErrors();
					}
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


	public boolean isShowFailureDetails() {
		return showFailureDetails;
	}

	public void setShowFailureDetails(boolean showFailureDetails) {
		this.showFailureDetails = showFailureDetails;
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
			
			if( failures == 0 && errors == 0)
			{
				success = 1 * 100;
			}
			else if(errors == totalTests){
				success = 0;
			}
			else if( failures == totalTests )
			{
				success = 0;
			}
			else 
			{
				success = ((float)(totalTests - failures - errors) /(float) totalTests) * 100;
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
	
	
	public class TestSuiteDetails
	{
		private String moduleName;
		private String testSuiteName;
		private int totalTests;
		private int errors;
		private int failures;
		private int skipped;
		
		private List<ProcessTestDetails> processDetails = new ArrayList<>();
		
		public String getTestSuiteName() {
			return testSuiteName;
		}
		public void setTestSuiteName(String testSuiteName) {
			this.testSuiteName = testSuiteName;
		}
		public String getModuleName() 
		{
			return moduleName;
		}
		public void setModuleName(String moduleName) 
		{
			this.moduleName = moduleName;
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
			
			if( failures == 0 && errors == 0)
			{
				success = 1 * 100;
			}
			else if(errors == totalTests){
				success = 0;
			}
			else if( failures == totalTests )
			{
				success = 0;
			}
			else 
			{
				success = ((float)(totalTests - failures - errors) /(float) totalTests) * 100;
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
		private String suiteName;
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
			
			if( failures == 0 && errors == 0)
			{
				success = 1 * 100;
			}
			else if(errors == totalTests){
				success = 0;
			}
			else if( failures == totalTests )
			{
				success = 0;
			}
			else 
			{
				success = ((float)(totalTests - failures - errors) / (float)totalTests) * 100;
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



		public String getSuiteName() {
			return suiteName;
		}



		public void setSuiteName(String suiteName) {
			this.suiteName = suiteName;
		}
		
	}
	
	
	public class ProcessFileTestDetails
	{
		
		private String fileName;
		private int totalAssertions;
		private int failures;
		private int errors;
		private List<String> assertionFailures = new ArrayList<>();
		private List<String> failureDataList = new ArrayList<>();
		
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
		
		public int getErrors() 
		{
			return errors;
		}
		
		public void incrementFailures()
		{
			this.failures++;
		}
		
		public void incrementErrors()
		{
			this.errors++;
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

		public List<String> getFailureData() {
			return failureDataList;
		}

		public void addFailureData(String failureData) {
			failureDataList.add(failureData);
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
			
			int totalErrors  = 0; 
			
			for( int count = 0 ; count < completeResult.getModuleResult().size() ; count++ )
			{
				TestSuiteResultDTO result = (TestSuiteResultDTO) completeResult.getModuleResult().get( count );
				for( int i =0 ; i < result.getTestSetResult().size() ; i++ )
				{
					TestSetResultDTO testset = (TestSetResultDTO) result.getTestSetResult().get( i );

					
					for( int j = 0 ; j < testset.getTestCaseResult().size() ; j++ )
					{
						TestCaseResultDTO testcase = (TestCaseResultDTO) testset.getTestCaseResult().get( j );
						if( testcase.getProcessFailures() > 0 )
						{
							totalErrors++;
						}
						
					}
				}

			}
			return String.valueOf(totalErrors);
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
					}
				}

			}
			return String.valueOf(totalfailure);
		}
		
		public String getPercentage()
		{
			float success = 0;

			if( Integer.valueOf(getFailures()) == 0 && Integer.valueOf(getErrors()) == 0 )
			{
				success = 100;
			}
			else if( Integer.valueOf(getFailures()) == Integer.valueOf(getTotalTests()) )
			{
				success = 0;
			}
			else if( Integer.valueOf(getErrors()) == Integer.valueOf(getTotalTests()) )
			{
				success = 0;
			}
			
			else 
			{
				success = (((float)(Integer.valueOf(getTotalTests()) - Integer.valueOf(getFailures()) - Integer.valueOf(getErrors()))/ ((float)Integer.valueOf(getTotalTests())) ))* 100;
			}
			return String.valueOf( format.format(success) );
		}
		
	}
	
	
	public class Packages
	{
		
	}
	
	
}

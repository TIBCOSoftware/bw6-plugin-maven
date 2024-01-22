package com.tibco.bw.maven.plugin.test.report;

import static org.apache.maven.doxia.markup.HtmlMarkup.A;
import static org.apache.maven.doxia.sink.Sink.JUSTIFY_LEFT;
import static org.apache.maven.doxia.sink.SinkEventAttributes.HREF;
import static org.apache.maven.doxia.sink.SinkEventAttributes.NAME;

import java.util.List;
import java.util.Map;

import org.apache.maven.doxia.markup.HtmlMarkup;
import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.doxia.sink.impl.SinkEventAttributeSet;
import org.apache.maven.doxia.util.DoxiaUtils;

import com.tibco.bw.maven.plugin.test.dto.CompleteReportDTO;
import com.tibco.bw.maven.plugin.test.dto.TestSuiteResultDTO;
import com.tibco.bw.maven.plugin.test.helpers.BWTestConfig;
import com.tibco.bw.maven.plugin.test.report.BWTestSuiteReportParser.PackageTestDetails;
import com.tibco.bw.maven.plugin.test.report.BWTestSuiteReportParser.ProcessFileTestDetails;
import com.tibco.bw.maven.plugin.test.report.BWTestSuiteReportParser.ProcessTestDetails;
import com.tibco.bw.maven.plugin.test.report.BWTestSuiteReportParser.TestSuiteDetails;

public class BWTestReportGenerator 
{

    private static final int LEFT = JUSTIFY_LEFT;

    private static final Object[] TAG_TYPE_START = { HtmlMarkup.TAG_TYPE_START };

    private static final Object[] TAG_TYPE_END = { HtmlMarkup.TAG_TYPE_END };

	
    private CompleteReportDTO result ;
    private BWTestSuiteReportParser bwTestParser;
    private static boolean  showFailureProcessname = true;
    
	public void generateReport( CompleteReportDTO result ,  Sink sink)
	{
		this.result = result;
		this.bwTestParser = new BWTestSuiteReportParser(result);
		
        sink.head();

        sink.title();
        sink.text( "BW Report" );
        sink.title_();

        sink.head_();

        sink.body();

        SinkEventAttributeSet atts = new SinkEventAttributeSet();
        atts.addAttribute( "type", "text/javascript" );
        sink.unknown( "script", new Object[]{ HtmlMarkup.TAG_TYPE_START }, atts );
        sink.unknown( "cdata", new Object[]{ HtmlMarkup.CDATA_TYPE, javascriptToggleDisplayCode() }, null );
        sink.unknown( "script", new Object[]{ HtmlMarkup.TAG_TYPE_END }, null );
        
        sink.section1();
        sink.sectionTitle1();
        sink.text(  "BW Test Report");
        sink.sectionTitle1_();
        sink.section1_();

        constructSummarySection(sink);
        
        
        
       
        
        if(null != BWTestConfig.INSTANCE.getTestSuiteName() && !BWTestConfig.INSTANCE.getTestSuiteName().isEmpty()){
        	
        	constructTestSuiteSummarySection(sink);
        	constructTestSuiteWiseSection(sink);
        	if(bwTestParser.isShowFailureDetails() && showFailureProcessname){
        		constructFailureDetailsSectionForTestSuite(sink);
        	}
        }
        else{
        	 constructPackagesSection(sink);
        	 constructTestCasesSection(sink);
        	 if(bwTestParser.isShowFailureDetails() && showFailureProcessname){
            	 constructFailureDetailsSection(sink);
            }
        }
        
        
        
       
       
        
        sink.body_();

        sink.flush();

        sink.close();
	}
	
	
    private void constructSummarySection( Sink sink )
    {
        

        sink.section1();
        sink.sectionTitle1();
        sink.text( "Summary" );
        sink.sectionTitle1_();

        sinkAnchor( sink, "Summary" );

        constructHotLinks( sink );

        sinkLineBreak( sink );

        sink.table();

        sink.tableRows( new int[]{ LEFT, LEFT, LEFT, LEFT, LEFT }, true );

        sink.tableRow();

        sinkHeader( sink, "Tests" );

        sinkHeader( sink, "Errors" );

        sinkHeader( sink, "Failures" );

        sinkHeader( sink, "Skipped" );

        sinkHeader( sink, "Success Rate" );

        
        sink.tableRow_();

        sink.tableRow();

        sinkCell( sink, bwTestParser.getSummary().getTotalTests() );

        sinkCell( sink, bwTestParser.getSummary().getErrors() );

        sinkCell( sink, bwTestParser.getSummary().getFailures() );

        sinkCell( sink, bwTestParser.getSummary().getSkipped() );

        sinkCell( sink, bwTestParser.getSummary().getPercentage() + "%" );


        sink.tableRow_();

        sink.tableRows_();

        sink.table_();        


        sinkLineBreak( sink );

        sink.section1_();
    }
    
    
    private void constructPackagesSection( Sink sink)
    {


    	sink.section1();
    	sink.sectionTitle1();
    	sink.text( "Package List" );
    	sink.sectionTitle1_();

    	sinkAnchor( sink, "Package_List" );

    	constructHotLinks( sink );

    	sinkLineBreak( sink );

    	sink.table();

    	sink.tableRows( new int[]{ LEFT , LEFT, LEFT, LEFT, LEFT, LEFT, LEFT }, true );

    	sink.tableRow();

    	sinkHeader( sink, "Module" );
    	
    	sinkHeader( sink, "Package" );

    	sinkHeader( sink, "Test");

    	sinkHeader( sink, "Errors" );

    	sinkHeader( sink, "Failures" );

    	sinkHeader( sink, "Skipped" );

    	sinkHeader( sink, "Success Rate");


    	sink.tableRow_();

    	
    	
    	for ( Map.Entry<String, PackageTestDetails> entry : bwTestParser.getPackageMap().entrySet() )
    	{
    		sink.tableRow();

    		String packageName = entry.getKey();

    		PackageTestDetails packageDetails = entry.getValue();

    		sinkCell( sink,  packageDetails.getModuleName() );
    		
    		sinkCellLink( sink, packageName, "#" + packageName );

    		sinkCell( sink, String.valueOf( packageDetails.getTotalTests() ) );

    		sinkCell( sink, String.valueOf( packageDetails.getErrors() ));

    		sinkCell( sink,  String.valueOf( packageDetails.getFailures()) );

    		sinkCell( sink, "0" );

    		sinkCell( sink, packageDetails.getSuccessRate() + "%" );


    		sink.tableRow_();
    	}

    	sink.tableRows_();

    	sink.table_();

    	sink.lineBreak();


    	for ( Map.Entry<String, PackageTestDetails> entry : bwTestParser.getPackageMap().entrySet() )
    	{
    		String packageName = entry.getKey();

    		List<ProcessTestDetails> testSuiteList = entry.getValue().getProcessDetails();

    		sink.section2();
    		sink.sectionTitle2();
    		sink.text( packageName );
    		sink.sectionTitle2_();

    		sinkAnchor( sink, packageName );

    		boolean showTable = false;

			sink.table();

			sink.tableRows( new int[]{ LEFT, LEFT, LEFT, LEFT, LEFT, LEFT, LEFT }, true );

			sink.tableRow();

			sinkHeader( sink, "" );

			sinkHeader( sink, "Class" );

			sinkHeader( sink,"Tests" );

			sinkHeader( sink, "Errors" );

			sinkHeader( sink, "Failures" );

			sinkHeader( sink, "Skipped");

			sinkHeader( sink, "Success Rate" );
			

			sink.tableRow_();

			for ( ProcessTestDetails suite : testSuiteList )
			{
				constructTestSuiteSection( sink, suite , packageName);
			}

			sink.tableRows_();

			sink.table_();
    		

    		sink.section2_();
    	}

    	sinkLineBreak( sink );

    	sink.section1_();
    }
    
    private void constructTestSuiteSummarySection( Sink sink)
    {


    	sink.section1();
    	sink.sectionTitle1();
    	sink.text( "Test Suite List" );
    	sink.sectionTitle1_();

    	sinkAnchor( sink, "TestSuite_List" );

    	constructHotLinks( sink );

    	sinkLineBreak( sink );

    	sink.table();

    	sink.tableRows( new int[]{ LEFT , LEFT, LEFT, LEFT, LEFT, LEFT, LEFT }, true );

    	sink.tableRow();

    	sinkHeader( sink, "Test Suite" );
    	
    	sinkHeader( sink, "Module" );

    	sinkHeader( sink, "Test");

    	sinkHeader( sink, "Errors" );

    	sinkHeader( sink, "Failures" );

    	sinkHeader( sink, "Skipped" );

    	sinkHeader( sink, "Success Rate");


    	sink.tableRow_();

    	
    	
    	for ( Map.Entry<String, TestSuiteDetails> entry : bwTestParser.getTestSuiteMap().entrySet() )
    	{
    		sink.tableRow();

    		String testSuiteName = entry.getKey();

    		TestSuiteDetails testSuiteDetails = entry.getValue();

    		sinkCellLink( sink, testSuiteName, "#" + testSuiteName );
    		
    		sinkCell( sink,  testSuiteDetails.getModuleName() );

    		sinkCell( sink, String.valueOf( testSuiteDetails.getTotalTests() ) );

    		sinkCell( sink, String.valueOf( testSuiteDetails.getErrors() ));

    		sinkCell( sink,  String.valueOf( testSuiteDetails.getFailures()) );

    		sinkCell( sink, "0" );

    		sinkCell( sink, testSuiteDetails.getSuccessRate() + "%" );


    		sink.tableRow_();
    	}

    	sink.tableRows_();

    	sink.table_();

    	
    	sinkLineBreak( sink );

    	sink.section1_();
    }


    
    private void constructTestSuiteSection( Sink sink , ProcessTestDetails suite , String packageName )
    {
        sink.tableRow();

        sink.tableCell();

        sink.link( "#" + packageName + '.' + suite.getProcessName());

        if ( suite.getFailures() > 0 )
        {
            sinkIcon( "error", sink );
        }
        else
        {
            sinkIcon( "success", sink );
        }

        sink.link_();

        sink.tableCell_();

        sinkCellLink( sink, suite.getProcessName(), "#" + packageName + '.' + suite.getProcessName() );

        sinkCell( sink, Integer.toString( suite.getTotalTests() ) );

        sinkCell( sink, Integer.toString( suite.getErrors() ));

        sinkCell( sink, Integer.toString( suite.getFailures() ) );

        sinkCell( sink, "0" );

        
        sinkCell( sink, suite.getSuccessRate() + "%" );


        sink.tableRow_();
    }

    
    private void constructTestSuiteTestCaseSection( Sink sink , ProcessFileTestDetails testCase, Map<String, String> testCaseWithProcessMap )
    {
        sink.tableRow();

        sink.tableCell();

        if ( testCase.getFailures() > 0 )
        {
        	showFailureProcessname = true;
        	
            sink.link( "#" + toHtmlId( testCase.getFileName() ) );

            sinkIcon( "error", sink );

            sink.link_();
        }
        else
        {
            sinkIcon( "success", sink );
        }

        sink.tableCell_();

        sinkCell(sink, testCase.getFileName() );
        
        String text = String.valueOf(testCase.getTotalAssertions() ) + " Assertions run. ";
        if( testCase.getAssertionFailures().size() > 0 )
        {
        	text = text + " Assertions failed for Activities " + testCase.getAssertionFailures().toString() ;
        }
       
		sinkCell( sink, text   );

        sink.tableRow_();

    }
    

    private void constructTestCasesSection( Sink sink )
    {
        

        sink.section1();
        sink.sectionTitle1();
        sink.text( "Process Test Cases " );
        sink.sectionTitle1_();

        sinkAnchor( sink, "Test_Cases" );

        constructHotLinks( sink );

        
        for ( Map.Entry<String, PackageTestDetails> entry : bwTestParser.getPackageMap().entrySet() )
    	{
    		String packageName = entry.getKey();

    		List<ProcessTestDetails> testSuiteList = entry.getValue().getProcessDetails();
    		
    		for( ProcessTestDetails suite : testSuiteList )
    		{
    			
                sink.section2();
                sink.sectionTitle2();
                sink.text( suite.getProcessName() );
                sink.sectionTitle2_();

                sinkAnchor( sink, packageName + '.' + suite.getProcessName() );

                sink.table();

                sink.tableRows( new int[]{ LEFT, LEFT, LEFT }, true );

                for ( ProcessFileTestDetails testCase : suite.getFileTestDetails() )
                {
                	
                	constructTestCaseSection( sink, testCase );
                	
                }

                sink.tableRows_();

                sink.table_();

                sink.section2_();
    		}
    	}

        sinkLineBreak( sink );

        sink.section1_();
    }

    
    private void constructTestSuiteWiseSection( Sink sink )
    {
        

        sink.section1();
        sink.sectionTitle1();
        sink.text( "Test Suite Test Cases " );
        sink.sectionTitle1_();

        sinkAnchor( sink, "Test_Cases" );

        constructHotLinks( sink );

        for ( Map.Entry<String, TestSuiteDetails> entry : bwTestParser.getTestSuiteMap().entrySet() )
    	{
    		String testSuiteName = entry.getKey();

    		List<ProcessTestDetails> testSuiteList = entry.getValue().getProcessDetails();
    		
    		for( ProcessTestDetails suite : testSuiteList )
    		{
    			
                sink.section2();
                sink.sectionTitle2();
                sink.text( suite.getSuiteName().replaceAll(".bwts", "") );
                sink.sectionTitle2_();

                sinkAnchor( sink, testSuiteName + '.' + suite.getProcessName() );

                sink.table();

                sink.tableRows( new int[]{ LEFT, LEFT, LEFT }, true );

                for ( ProcessFileTestDetails testCase : suite.getFileTestDetails() )
                {
                	
                	constructTestSuiteTestCaseSection( sink, testCase,bwTestParser.getTestCaseWithProcessMap() );
                	
                }

                sink.tableRows_();

                sink.table_();

                sink.section2_();
    		}
    	}

        sinkLineBreak( sink );

        sink.section1_();
    }

    private void constructFailureDetailsSection( Sink sink )
    {
        

        sink.section1();
        sink.sectionTitle1();
        sink.text( "Failure Details " );
        sink.sectionTitle1_();

        sinkAnchor( sink, "Failure_Details" );

        constructHotLinks( sink );

        
        for ( Map.Entry<String, PackageTestDetails> entry : bwTestParser.getPackageMap().entrySet() )
    	{
    		String packageName = entry.getKey();

    		List<ProcessTestDetails> testSuiteList = entry.getValue().getProcessDetails();
    		
    		for( ProcessTestDetails suite : testSuiteList )
    		{
    			
                sink.section2();
                sink.sectionTitle2();
                sink.text( suite.getProcessName() );
                sink.sectionTitle2_();

                sinkAnchor( sink, packageName + '.' + suite.getProcessName() );

                sink.table();

                sink.tableRows( new int[]{ LEFT, LEFT }, true );

                for ( ProcessFileTestDetails testCase : suite.getFileTestDetails() )
                {
                	
                	constructFailureDataSection( sink, testCase );
                	
                }

                sink.tableRows_();

                sink.table_();

                sink.section2_();
    		}
    	}

    sinkLineBreak( sink );

    sink.section1_();
}
    
    private void constructFailureDetailsSectionForTestSuite( Sink sink )
    {
        

        sink.section1();
        sink.sectionTitle1();
        sink.text( "Failure Details " );
        sink.sectionTitle1_();

        sinkAnchor( sink, "Failure_Details" );

        constructHotLinks( sink );

        
        for ( Map.Entry<String, TestSuiteDetails> entry : bwTestParser.getTestSuiteMap().entrySet() )
    	{
    		String testSuiteName = entry.getKey();

    		List<ProcessTestDetails> testSuiteList = entry.getValue().getProcessDetails();
    		
    		for( ProcessTestDetails suite : testSuiteList )
    		{
    			
                sink.section2();
                sink.sectionTitle2();
                sink.text( suite.getSuiteName() );
                sink.sectionTitle2_();

                sinkAnchor( sink, testSuiteName + '.' + suite.getSuiteName() );

                sink.table();

                sink.tableRows( new int[]{ LEFT, LEFT }, true );

                for ( ProcessFileTestDetails testCase : suite.getFileTestDetails() )
                {
                	
                	constructFailureDataSection( sink, testCase );
                	
                }

                sink.tableRows_();

                sink.table_();

                sink.section2_();
    		}
    	}

    sinkLineBreak( sink );

    sink.section1_();
}
    
   
    private static void constructTestCaseSection( Sink sink , ProcessFileTestDetails testCase )
    {
        sink.tableRow();

        sink.tableCell();

        if ( testCase.getFailures() > 0 )
        {
        	showFailureProcessname = true;
        	
            sink.link( "#" + toHtmlId( testCase.getFileName() ) );

            sinkIcon( "error", sink );

            sink.link_();
        }
        else
        {
            sinkIcon( "success", sink );
        }

        sink.tableCell_();

        sinkCell(sink, testCase.getFileName() );
        
        String text = String.valueOf(testCase.getTotalAssertions() ) + " Assertions run. ";
        if( testCase.getAssertionFailures().size() > 0 )
        {
        	text = text + " Assertions failed for Activities " + testCase.getAssertionFailures().toString();
        }
		sinkCell( sink, text   );

        sink.tableRow_();

    }
    
    private static void constructFailureDataSection( Sink sink , ProcessFileTestDetails testCase ){

        
        if( testCase.getAssertionFailures().size() > 0 )
        {
           
        	for(String faultData : testCase.getFailureData())
        	{
        		sink.tableRow();
                sink.tableCell();
        		sink.link( "#" + toHtmlId( testCase.getFileName() ) );
        		sinkIcon( "error", sink );
        		sink.link_();
        		sink.tableCell_();
        		sinkCell( sink,testCase.getFileName());
        		sinkCell( sink, faultData );
        		sink.tableRow_();
        	}
        }
        else{
        	sink.tableRow();
        	sink.tableCell();
    		sink.link( "#" + toHtmlId( testCase.getFileName() ) );
    		sinkIcon( "success", sink );
    		sink.link_();
    		sink.tableCell_();
    		sinkCell( sink,testCase.getFileName());
        	sinkCell(sink, "No failed assertions found.");
        	sink.tableRow_();
        }

    
    }
    
    private static String toHtmlId( String id )
    {
        return DoxiaUtils.isValidId( id ) ? id : DoxiaUtils.encodeId( id );
    }


    
    private static void sinkIcon( String type, Sink sink )
    {
        sink.figure();

        if ( type.startsWith( "junit.framework" ) || "skipped".equals( type ) )
        {
            sink.figureGraphics( "images/icon_warning_sml.gif" );
        }
        else if ( type.startsWith( "success" ) )
        {
            sink.figureGraphics( "images/icon_success_sml.gif" );
        }
        else
        {
            sink.figureGraphics( "images/icon_error_sml.gif" );
        }

        sink.figure_();
    }

    

    private void constructHotLinks( Sink sink )
    {
            sink.paragraph();

            sink.text( "[" );
            sinkLink( sink, "Summary", "#Summary" );
            sink.text( "]" );
           if( null != BWTestConfig.INSTANCE.getTestSuiteName() && !BWTestConfig.INSTANCE.getTestSuiteName().isEmpty()){
        	   sink.text( " [" );
               sinkLink( sink, "Test Suite List", "#TestSuite_List" );
               sink.text( "]" );
           }
           else{
        	   sink.text( " [" );
               sinkLink( sink, "Package List", "#Package_List" );
               sink.text( "]" );
           }
            
            sink.text( " [" );
            sinkLink( sink, "Test Cases", "#Test_Cases" );
            sink.text( "]" );

           if( bwTestParser.isShowFailureDetails() ){
        	   sink.text( " [" );
               sinkLink( sink, "Failure Details", "#Failure_Details" );
               sink.text( "]" );
           }
            
            
            
            
            sink.paragraph_();
    }

    
    private static void sinkLineBreak( Sink sink )
    {
        sink.lineBreak();
    }

    private static void sinkHeader( Sink sink, String header )
    {
        sink.tableHeaderCell();
        sink.text( header );
        sink.tableHeaderCell_();
    }

    private static void sinkCell( Sink sink, String text )
    {
        sink.tableCell();
        sink.text( text );
        sink.tableCell_();
    }

    private static void sinkLink( Sink sink, String text, String link )
    {
        sink.link( link );
        sink.text( text );
        sink.link_();
    }

    private static void sinkCellLink( Sink sink, String text, String link )
    {
        sink.tableCell();
        sinkLink( sink, text, link );
        sink.tableCell_();
    }

   /* private static void sinkCellAnchor( Sink sink, String text, String anchor )
    {
        sink.tableCell();
        sinkAnchor( sink, anchor );
        sink.text( text );
        sink.tableCell_();
    }*/

    private static void sinkAnchor( Sink sink, String anchor )
    {
        // Dollar '$' for nested classes is not valid character in sink.anchor() and therefore it is ignored
        // https://issues.apache.org/jira/browse/SUREFIRE-1443
        sink.unknown( A.toString(), TAG_TYPE_START, new SinkEventAttributeSet( NAME, anchor ) );
        sink.unknown( A.toString(), TAG_TYPE_END, null );
    }

    /*private static void sinkLink( Sink sink, String href )
    {
        // The "'" argument in this JavaScript function would be escaped to "&apos;"
        // sink.link( "javascript:toggleDisplay('" + toHtmlId( testCase.getFullName() ) + "');" );
        sink.unknown( A.toString(), TAG_TYPE_START, new SinkEventAttributeSet( HREF, href ) );
    }*/

    /*@SuppressWarnings( "checkstyle:methodname" )
    private static void sinkLink_( Sink sink )
    {
        sink.unknown( A.toString(), TAG_TYPE_END, null );
    }*/

	  private static String javascriptToggleDisplayCode()
	    {

	        // the javascript code is emitted within a commented CDATA section
	        // so we have to start with a newline and comment the CDATA closing in the end

	        return "\n" + "function toggleDisplay(elementId) {\n"
	                + " var elm = document.getElementById(elementId + '-error');\n"
	                + " if (elm == null) {\n"
	                + "  elm = document.getElementById(elementId + '-failure');\n"
	                + " }\n"
	                + " if (elm && typeof elm.style != \"undefined\") {\n"
	                + "  if (elm.style.display == \"none\") {\n"
	                + "   elm.style.display = \"\";\n"
	                + "   document.getElementById(elementId + '-off').style.display = \"none\";\n"
	                + "   document.getElementById(elementId + '-on').style.display = \"inline\";\n"
	                + "  } else if (elm.style.display == \"\") {"
	                + "   elm.style.display = \"none\";\n"
	                + "   document.getElementById(elementId + '-off').style.display = \"inline\";\n"
	                + "   document.getElementById(elementId + '-on').style.display = \"none\";\n"
	                + "  } \n"
	                + " } \n"
	                + " }\n"
	                + "//";
	    }
	
}

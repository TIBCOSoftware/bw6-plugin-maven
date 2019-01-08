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

import com.tibco.bw.maven.plugin.test.coverage.ProcessCoverage;
import com.tibco.bw.maven.plugin.test.coverage.ProcessCoverageParser;
import com.tibco.bw.maven.plugin.test.coverage.ProcessCoverageStatsGenerator;
import com.tibco.bw.maven.plugin.test.coverage.ProcessCoverageStatsGenerator.OverAllStats;
import com.tibco.bw.maven.plugin.test.coverage.ProcessCoverageStatsGenerator.ProcessStats;
import com.tibco.bw.maven.plugin.test.dto.CompleteReportDTO;

public class CodeCoverageReportGenerator 
{

	   private static final int LEFT = JUSTIFY_LEFT;

	    private static final Object[] TAG_TYPE_START = { HtmlMarkup.TAG_TYPE_START };

	    private static final Object[] TAG_TYPE_END = { HtmlMarkup.TAG_TYPE_END };

		
	    private CompleteReportDTO result ;
	    private BWTestSuiteReportParser bwTestParser;
	    
		public void generateReport( CompleteReportDTO result ,  Sink sink)
		{
			
			ProcessCoverageParser parser = new ProcessCoverageParser();
			Map<String,ProcessCoverage> map = parser.loadCoverage(result);
			
			ProcessCoverageStatsGenerator gen = new ProcessCoverageStatsGenerator();
			gen.generateStats(map);
			
			
			OverAllStats comStats = gen.getStats();
			List<ProcessStats> stats = gen.getProcessStats();
			
			sink.head();
			

	        sink.title();
	        sink.text( "BW Coverage" );
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
	        sink.text(  "BW Coverage Report");
	        sink.sectionTitle1_();
	        sink.section1_();

	        constructSummarySection(sink , comStats );
	        constructProcessSection(sink , stats);
		}
		
		private void constructSummarySection( Sink sink , OverAllStats stats )
		{
		        

		        sink.section1();
		        sink.sectionTitle1();
		        sink.text( "Summary" );
		        sink.sectionTitle1_();

		        sinkAnchor( sink, "Overall Coverage Summary" );


		        sinkLineBreak( sink );

		        sink.table();

		        sink.tableRows( new int[]{ LEFT, LEFT, LEFT, LEFT }, true );

		        sink.tableRow();

		        sinkHeader( sink, "Modules %" );

		        sinkHeader( sink, "Process %" );

		        sinkHeader( sink, "Activity %" );

		        sinkHeader( sink, "Transition %" );

		        
		        sink.tableRow_();

		        sink.tableRow();

		        sinkCell( sink, stats.getModuleStat() );

		        sinkCell( sink, stats.getProcessStat() );

		        sinkCell( sink, stats.getActivityStat() );

		        sinkCell( sink, stats.getTransitionStat() );


		        sink.tableRow_();

		        sink.tableRows_();

		        sink.table_();        


		        sinkLineBreak( sink );

		        sink.section1_();
		    }
		    
		
	    private void constructProcessSection( Sink sink , List<ProcessStats> stats )
	    {
	        

	        sink.section1();
	        sink.sectionTitle1();
	        sink.text( "Coverage BreakDown By Process" );
	        sink.sectionTitle1_();

	        sink.table();

            sink.tableRows( new int[]{ LEFT, LEFT, LEFT , LEFT }, true );
            
            sink.tableRow();

			sinkHeader( sink, "Module" );

			sinkHeader( sink, "Process" );

			sinkHeader( sink,"Activity %" );

			sinkHeader( sink, "Transition %" );


			sink.tableRow_();
            

			for( ProcessStats stat : stats  )
			{
				 	sink.tableRow();

			        sinkCell( sink, stat.getModuleName() );

			        sinkCell( sink, stat.getProcessName() );

			        sinkCell( sink, stat.getActivityStat() );

			        sinkCell( sink, stat.getTransitionStat() );


			        sink.tableRow_();
			}
			

            sink.tableRows_();
            

			sink.table_();
	        
            sinkLineBreak( sink );

	        sink.section1_();
	        
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

	    private static void sinkCellAnchor( Sink sink, String text, String anchor )
	    {
	        sink.tableCell();
	        sinkAnchor( sink, anchor );
	        sink.text( text );
	        sink.tableCell_();
	    }

	    private static void sinkAnchor( Sink sink, String anchor )
	    {
	        // Dollar '$' for nested classes is not valid character in sink.anchor() and therefore it is ignored
	        // https://issues.apache.org/jira/browse/SUREFIRE-1443
	        sink.unknown( A.toString(), TAG_TYPE_START, new SinkEventAttributeSet( NAME, anchor ) );
	        sink.unknown( A.toString(), TAG_TYPE_END, null );
	    }

	    private static void sinkLink( Sink sink, String href )
	    {
	        // The "'" argument in this JavaScript function would be escaped to "&apos;"
	        // sink.link( "javascript:toggleDisplay('" + toHtmlId( testCase.getFullName() ) + "');" );
	        sink.unknown( A.toString(), TAG_TYPE_START, new SinkEventAttributeSet( HREF, href ) );
	    }

	    @SuppressWarnings( "checkstyle:methodname" )
	    private static void sinkLink_( Sink sink )
	    {
	        sink.unknown( A.toString(), TAG_TYPE_END, null );
	    }

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

package com.tibco.bw.maven.plugin.test.report;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.doxia.sink.SinkFactory;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Execute;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.reporting.AbstractMavenReport;
import org.apache.maven.reporting.MavenReportException;

import com.tibco.bw.maven.plugin.test.dto.CompleteReportDTO;
import com.tibco.bw.maven.plugin.test.helpers.BWTestConfig;

@Mojo( name = "bwreport", inheritByDefault = false )
@Execute( lifecycle = "bwtestreport", phase = LifecyclePhase.TEST )
public class BWTestsReport extends AbstractMavenReport 
{
	
	 @Component
	  private MavenSession session;




	@Override
	public String getDescription(Locale arg0) 
	{
		return "BW Test Report";
	}

	@Override
	public String getName(Locale arg0) 
	{
	
		return "bwtest";
	}

	@Override
	public String getOutputName() 
	{
		return "bwtest";
	}

	
	
	
	@Override
	public void execute() throws MojoExecutionException {

		
		
		super.execute();
	}
	
	
	private void generateCodeCoverageReport( CompleteReportDTO result ) throws Exception
	{
        String fileName = "bwcoverage.html";
        
        SinkFactory factory = getSinkFactory();
        
        Sink renderSink = factory.createSink(outputDirectory, fileName);
     
        CodeCoverageReportGenerator cgen = new CodeCoverageReportGenerator();
        
        cgen.generateReport(result, renderSink);
        
        renderSink.close();
        
	}
//	
//	
//	SiteRenderingContext siteContext = new SiteRenderingContext();
//    siteContext.setDecoration( new DecorationModel() );
//    siteContext.setTemplateName( "org/apache/maven/doxia/siterenderer/resources/default-site.vm" );
//    siteContext.setLocale( getL );
//    siteContext.setTemplateProperties( getTemplateProperties() );
//
//    RenderingContext context = new RenderingContext( outputDirectory, filename );
//
//    SiteRendererSink sink = new SiteRendererSink( context );
//    
//    
//    Sink renderSink = factory.createSink(outputDirectory, fileName);

	@Override
	protected void executeReport(Locale arg0) throws MavenReportException 
	{
		BWTestConfig.INSTANCE.reset();
		
		String tibcoHome = "";
		String bwHome = "";
		Log logger =  getLog();
		
		try {
			BWTestConfig.INSTANCE.init(  tibcoHome , bwHome , session, getProject() , logger );
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		CompleteReportDTO result = loadTestSuite();
		
		try {
			generateCodeCoverageReport( result );
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		

		
		
		BWTestReportGenerator gen = new BWTestReportGenerator();
		gen.generateReport(result, getSink());
		
		

		
	}
	
	
	private CompleteReportDTO loadTestSuite()
	{
		File file = new File( BWTestConfig.INSTANCE.getProject().getBasedir() , "target/bwtest/bwtestreport.xml");
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(CompleteReportDTO.class);
			Unmarshaller marshaller = jaxbContext.createUnmarshaller();
			CompleteReportDTO result = (CompleteReportDTO) marshaller.unmarshal( file );
			return result;
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	
	}
	


}

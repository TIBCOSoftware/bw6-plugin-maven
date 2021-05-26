package com.tibco.bw.maven.plugin.test.coverage;

import java.io.File;
import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.maven.project.MavenProject;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import com.tibco.bw.maven.plugin.test.dto.CompleteReportDTO;
import com.tibco.bw.maven.plugin.test.dto.ProcessCoverageDTO;
import com.tibco.bw.maven.plugin.test.dto.TestSuiteResultDTO;
import com.tibco.bw.maven.plugin.test.helpers.BWTestConfig;
import com.tibco.bw.maven.plugin.utils.BWFileUtils;

public class ProcessCoverageParser
{
	
	Map<String,ProcessCoverage> processMap = new HashMap<>();

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Map<String,ProcessCoverage> loadCoverage( CompleteReportDTO complete)	
	{
		List<MavenProject> projects = BWTestConfig.INSTANCE.getSession().getProjects();
		
		
		for( MavenProject project : projects )
		{
			if( project.getPackaging().equals("bwmodule") )
			{
				loadProcesses( project);
			}
		}
		
		for( int count = 0 ; count < complete.getModuleResult().size() ; count++ )
		{
			TestSuiteResultDTO result = (TestSuiteResultDTO) complete.getModuleResult().get( count );
			List coverage = result.getCodeCoverage();
			
			for( int i = 0; i < coverage.size() ; i++ )
			{
				ProcessCoverageDTO dto = (ProcessCoverageDTO) coverage.get( i );
				ProcessCoverage pc = processMap.get( dto.getProcessName());
				pc.setProcessExecuted(true);
				pc.getActivitiesExec().addAll( dto.getActivityCoverage() );
				pc.getTransitionExec().addAll( dto.getTransitionCoverage()  );
			}
		}	
		
		return processMap;
	}
	
	
	private void loadProcesses( MavenProject project )
	{
		List<File> files =  getProcessFiles(project);
		
		for( File file : files )
		{
			try {
				parse(file , project.getArtifactId() );
			} 
			catch (Exception e) {
			
				e.printStackTrace();
			}
		}
	} 
	
	private void parse( File processFile , String module ) throws Exception
	{
		
		ProcessParser parser = new ProcessParser();
		
		XMLReader reader  = XMLReaderFactory.createXMLReader();			
		
		String xml  = FileUtils.readFileToString( processFile );

		
		reader.setContentHandler(parser );
		reader.parse(new InputSource(new StringReader( xml )));
		
		ProcessCoverage coverage = parser.getCoverage();
		
		if( coverage.isSubProcess()) 
		{
			coverage.setModuleName(module);
			processMap.put( coverage.getProcessName(),  coverage);	
		}
		
		
		
		
	}
	
	private List<File> getProcessFiles( MavenProject project )
	{
		File baseDir = project.getBasedir();
		List<File> files = BWFileUtils.getEntitiesfromLocation( baseDir.toString() , "bwp");
		
		return files;
		
		
		
	}
	

}

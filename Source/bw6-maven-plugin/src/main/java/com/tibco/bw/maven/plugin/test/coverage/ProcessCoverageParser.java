package com.tibco.bw.maven.plugin.test.coverage;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.project.DefaultDependencyResolutionRequest;
import org.apache.maven.project.DependencyResolutionException;
import org.apache.maven.project.DependencyResolutionResult;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectDependenciesResolver;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import com.tibco.bw.maven.plugin.osgi.helpers.ManifestParser;
import com.tibco.bw.maven.plugin.test.dto.AssertionResultDTO;
import com.tibco.bw.maven.plugin.test.dto.CompleteReportDTO;
import com.tibco.bw.maven.plugin.test.dto.ProcessCoverageDTO;
import com.tibco.bw.maven.plugin.test.dto.TestCaseResultDTO;
import com.tibco.bw.maven.plugin.test.dto.TestSetResultDTO;
import com.tibco.bw.maven.plugin.test.dto.TestSuiteResultDTO;
import com.tibco.bw.maven.plugin.test.helpers.BWTestConfig;
import com.tibco.bw.maven.plugin.test.helpers.TestFileParser;
import com.tibco.bw.maven.plugin.test.report.BWTestSuiteReportParser.PackageTestDetails;
import com.tibco.bw.maven.plugin.test.report.BWTestSuiteReportParser.ProcessFileTestDetails;
import com.tibco.bw.maven.plugin.test.report.BWTestSuiteReportParser.ProcessTestDetails;
import com.tibco.bw.maven.plugin.utils.BWFileUtils;
import com.tibco.bw.maven.plugin.utils.Constants;

import org.eclipse.aether.graph.Dependency;

public class ProcessCoverageParser
{
	
    private static final Map<String, String> ALWAYS_EXECUTED_ACTIVITIES = new HashMap<>();
    static {
    	ALWAYS_EXECUTED_ACTIVITIES.put("OnMessageStart", "");
    	ALWAYS_EXECUTED_ACTIVITIES.put("OnMessageEnd", "");
    }
	
	Map<String,ProcessCoverage> processMap = new HashMap<>();
	
	 @Component
	  ProjectDependenciesResolver resolver;
	 
		HashMap<File,String> artifactFiles = new HashMap<File,String>();

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Map<String,ProcessCoverage> loadCoverage( CompleteReportDTO complete)	
	{
		List<MavenProject> projects = BWTestConfig.INSTANCE.getSession().getProjects();
		resolver = BWTestConfig.INSTANCE.getResolver();
		
		
		for( MavenProject project : projects )
		{
			if( project.getPackaging().equals("bwmodule") )
			{
				loadProcesses( project);
				loadProcessesFromESM(project);
				
				
			}
		}
		
		
		
		for( int count = 0 ; count < complete.getModuleResult().size() ; count++ )
		{
			TestSuiteResultDTO result = (TestSuiteResultDTO) complete.getModuleResult().get( count );
			List coverage = result.getCodeCoverage();
			if (coverage != null && coverage.size() > 0) {
				for( int i = 0; i < coverage.size() ; i++ )
				{
					ProcessCoverageDTO dto = (ProcessCoverageDTO) coverage.get( i );
					if(processMap.get( dto.getProcessName())!=null){
						ProcessCoverage pc = processMap.get( dto.getProcessName());
						pc.setProcessExecuted(true);
						pc.getActivitiesExec().addAll( dto.getActivityCoverage() );
						pc.getTransitionExec().addAll( dto.getTransitionCoverage()  );
					}
				}
			}
			else {
				// code coverage not there from TestSuiteResultDTO
				// As far as I can tell, it was never set.
				collectCoverageFromTestResults(result);
			}
		}	
		
		return processMap;
	}
	
	private void collectCoverageFromTestResults(TestSuiteResultDTO result) {


		for( int i =0 ; i < result.getTestSetResult().size() ; i++ )
		{
			TestSetResultDTO testset = (TestSetResultDTO) result.getTestSetResult().get( i );

			String testPackage = testset.getPackageName();

			ProcessCoverage pc = processMap.get(testset.getProcessName());
			pc.setProcessExecuted(true);
			// always put starter activity as executed
		 	String startActivityName = pc.getActivities().get(0);
 	 	 	pc.getActivitiesExec().add(startActivityName);

			Set<String> transitionsExecuted =  new HashSet<String>();
			for( int j = 0 ; j < testset.getTestCaseResult().size() ; j++ )
			{
				TestCaseResultDTO testcase = (TestCaseResultDTO) testset.getTestCaseResult().get( j );

				for( int assercount = 0 ; assercount < testcase.getAssertionResult().size()  ; assercount++ )
				{
					AssertionResultDTO aresult = (AssertionResultDTO) testcase.getAssertionResult().get(  assercount );
					String activityName = aresult.getActivityName();
					// keep it simple
					if (!activityName.equals("N/A")) {
						pc.getActivitiesExec().add(aresult.getActivityName());
					}
					for (String transition: pc.getTransitions()) {
					 	if (transition.indexOf(aresult.getActivityName()) >= 0 || 
					 	 	 	transition.indexOf(startActivityName) >= 0
					 	 	) {
							if (!transitionsExecuted.contains(transition)) {
								pc.getTransitionExec().add(transition);
								transitionsExecuted.add(transition);
							}
						}
					}

				}
			}
			
			for (String transition: pc.getTransitionExec() ) {

				String[] activities = convertTransition(transition);
				for (String activity: activities) {
					if (!pc.getActivitiesExec().contains(activity)) {
						pc.getActivitiesExec().add(activity);
					}
				}
			}
			
			for (String activity: pc.getActivities() ) {

				if (ALWAYS_EXECUTED_ACTIVITIES.containsKey(activity)) {
					pc.getActivitiesExec().add(activity);
				}

			}
		}


	}


	private String[] convertTransition(String transition) {
		String[] activities = new String[2];
		int index = transition.indexOf("To");
		activities[0] = transition.substring(0, index);
		activities[1] = transition.substring(index+2);
		return activities;
	}

	private void loadProcessesFromESM(MavenProject project) {
		 DependencyResolutionResult resolutionResult = getDependencies(project,BWTestConfig.INSTANCE.getSession());
		 if (resolutionResult != null) {
	        	for(Dependency dependency : resolutionResult.getDependencies()) {
	    			if(!dependency.getArtifact().getVersion().equals("0.0.0")) {
	            		artifactFiles.put(dependency.getArtifact().getFile(),dependency.getArtifact().getArtifactId());
	    			}
	        	}
	        }
			for(File file : artifactFiles.keySet()) {
				if( file.getName().indexOf("com.tibco.bw.palette.shared") != -1 || file.getName().indexOf("com.tibco.xml.cxf.common") != -1 || file.getName().indexOf("tempbw") != -1){
					continue;
				}
				boolean isSharedModule = false;
				Manifest mf = ManifestParser.parseManifestFromJAR( file);
				if(mf == null){
					try {
						throw new Exception("Failed to get Manifest for - "+ file.getName() +". Please verify if jar file is valid, the MANIFEST.MF should be first or second entry in the jar file. Use Command - jar tf <Jar_File_Path> to verify.");
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				for( Object str : mf.getMainAttributes().keySet())
				{
					if( Constants.TIBCO_SHARED_MODULE.equals(str.toString() ))
					{
						isSharedModule = true;
						break;
					}
				}
				if(isSharedModule){
					try {
						parseESM(file, artifactFiles.get(file));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		
	}

	private DependencyResolutionResult getDependencies(MavenProject project, MavenSession session) {
		DependencyResolutionResult resolutionResult = null;

		try {
			DefaultDependencyResolutionRequest resolution = new DefaultDependencyResolutionRequest(project, session.getRepositorySession());
			resolutionResult = resolver.resolve(resolution);
		} catch (DependencyResolutionException e) {
			e.printStackTrace();
			resolutionResult = e.getResult();
		}
		return resolutionResult;
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
		
		    coverage.setModuleName(module);
			processMap.put( coverage.getProcessName(),  coverage);	
	}
	
	private void parseESM( File processFile , String module ) throws Exception
	{
		String xml=null;
		String zipFileName = processFile.getAbsolutePath();
		

		try (FileInputStream fis = new FileInputStream(zipFileName);
				BufferedInputStream bis = new BufferedInputStream(fis);
				ZipInputStream stream = new ZipInputStream(bis)) {

			ZipEntry entry;
			ZipFile zf = new ZipFile(zipFileName);
			while ((entry = stream.getNextEntry()) != null) {
				String name = entry.getName();
				if (name.endsWith(".bwp")) {
					InputStream in = zf.getInputStream(entry);
					xml = IOUtils.toString(in, StandardCharsets.UTF_8.name());
					if( null != xml ){
						ProcessParser parser = new ProcessParser();
						XMLReader reader  = XMLReaderFactory.createXMLReader();			
						reader.setContentHandler(parser );
						reader.parse(new InputSource(new StringReader( xml )));
						ProcessCoverage coverage = parser.getCoverage();

						//if( coverage.isSubProcess()) 
					//	{
							coverage.setModuleName(module);
							processMap.put( coverage.getProcessName(),  coverage);	
						//}
					}
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private List<File> getProcessFiles( MavenProject project )
	{
		File baseDir = project.getBasedir();
		List<File> files = BWFileUtils.getEntitiesfromLocation( baseDir.toString() , "bwp");
		
		return files;
		
	}
	

}

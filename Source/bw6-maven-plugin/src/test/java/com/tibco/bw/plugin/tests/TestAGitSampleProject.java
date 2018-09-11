package com.tibco.bw.plugin.tests;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.apache.maven.shared.utils.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;

import edu.emory.mathcs.backport.java.util.Collections;

public class TestAGitSampleProject extends AbstractMojoTestCase {

	public static File gitRepo = new File(System.getProperty("user.home"),"gitProject");

	protected void setUp() throws Exception {
		super.setUp();
		
		String gitupdateFlag = System.getProperty("git.forceupdate")==null? "N":System.getProperty("git.forceupdate");
		System.out.println( "Git Sample Project Force Update Flag::::"+gitupdateFlag);
		if (gitRepo.exists()) {
			if(gitupdateFlag.equals("Y")){
				System.out.println("Updating Sample Projects from Git....");
				deleteDirectory(gitRepo);
				createGitClone();
			}
			
		}else{
			createGitClone();
			
		}
			
		
	
	}
	
	private void deleteDirectory(File gitRepo) {
		
		if (gitRepo.isDirectory()) {
		    for (File c : gitRepo.listFiles())
		    	deleteDirectory(c);
		  }
		  if (!gitRepo.delete())
			try {
				throw new FileNotFoundException("Failed to delete file: " + gitRepo);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
	}
	
	private void createGitClone(){
		Git git = null;
		try {
			git = Git
					.cloneRepository()
					.setURI("https://github.com/TIBCOSoftware/bw6-plugin-maven.git")
					.setDirectory(
							new File(System.getProperty("user.home")
									+ "/gitProject/"))
					.setBranchesToClone(
							Collections.singleton("refs/heads/unittest"))
					.setBranch("refs/heads/unittest").call();
			

		} catch (GitAPIException e) {
			e.printStackTrace();
		}finally{
			git.getRepository().close();
		}
	
	}

	public void testexecute() {}
	protected void tearDown() throws Exception {
		super.tearDown();
		
}
}

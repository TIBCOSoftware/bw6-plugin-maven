package com.tibco.bw.maven.plugin.test.stub;

import java.io.File;

import org.apache.maven.MavenExecutionException;

public class BWMavenPluginEarStub extends BWMavenPluginProjectStub {
	private static SampleProjectProperties prop = new SampleProjectProperties();

	public BWMavenPluginEarStub() throws MavenExecutionException {
		super(prop.getApplicationpath());

	}
	/** {@inheritDoc} */
	 public File getBasedir()
	    {
	        return getFile().getParentFile() ;
	    }
}

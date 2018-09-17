package com.tibco.bw.maven.plugin.test.stub;

import java.io.File;

import org.apache.maven.MavenExecutionException;
import org.codehaus.plexus.PlexusTestCase;

public class BWMavenPluginJarStub extends BWMavenPluginProjectStub {
	private static SampleProjectProperties prop = new SampleProjectProperties();

	public BWMavenPluginJarStub() throws MavenExecutionException {
		super(prop.getModulepath());

	}

	/** {@inheritDoc} */
    public File getBasedir()
    {
        return getFile().getParentFile() ;
    }
}

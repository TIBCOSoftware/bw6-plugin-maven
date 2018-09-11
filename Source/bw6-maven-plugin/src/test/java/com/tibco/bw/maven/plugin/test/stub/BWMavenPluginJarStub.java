package com.tibco.bw.maven.plugin.test.stub;

import java.io.File;

import org.apache.maven.MavenExecutionException;

public class BWMavenPluginJarStub extends BWMavenPluginProjectStub {
	private static SampleProjectProperties prop = new SampleProjectProperties();

	public BWMavenPluginJarStub() throws MavenExecutionException {
		super(prop.getModulepath());

	}

	@Override
	public File getBasedir() {
		return prop.getModulepath();
	}

	
}

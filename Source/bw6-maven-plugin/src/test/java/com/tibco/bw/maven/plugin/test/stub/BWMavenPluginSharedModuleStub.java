package com.tibco.bw.maven.plugin.test.stub;

import java.io.File;

import org.apache.maven.MavenExecutionException;

public class BWMavenPluginSharedModuleStub extends BWMavenPluginProjectStub {
	private static SampleProjectProperties prop = new SampleProjectProperties();

	public BWMavenPluginSharedModuleStub() throws MavenExecutionException {
		super(prop.getSharedModulepath());

	}

	@Override
	public File getBasedir() {
		return prop.getSharedModulepath();
	}

}

package com.tibco.bw.maven.plugin.test.stub;

import org.apache.maven.MavenExecutionException;
import org.codehaus.plexus.PlexusContainerException;

public class MavenSessionforJarSubStub extends MavenSessionStub{

	public MavenSessionforJarSubStub() throws PlexusContainerException, MavenExecutionException {
		super(new BWMavenPluginJarStub());
	}

}

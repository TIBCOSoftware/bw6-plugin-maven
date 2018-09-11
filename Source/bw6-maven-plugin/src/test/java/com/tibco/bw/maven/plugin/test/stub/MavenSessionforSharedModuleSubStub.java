package com.tibco.bw.maven.plugin.test.stub;

import org.apache.maven.MavenExecutionException;
import org.codehaus.plexus.PlexusContainerException;

public class MavenSessionforSharedModuleSubStub extends MavenSessionStub{

	public MavenSessionforSharedModuleSubStub() throws PlexusContainerException, MavenExecutionException {
		super(new BWMavenPluginSharedModuleStub());
	}

}

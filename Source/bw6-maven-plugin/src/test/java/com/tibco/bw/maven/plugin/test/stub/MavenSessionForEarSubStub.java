package com.tibco.bw.maven.plugin.test.stub;

import org.apache.maven.MavenExecutionException;
import org.codehaus.plexus.PlexusContainerException;

public class MavenSessionForEarSubStub extends MavenSessionStub{

	public MavenSessionForEarSubStub() throws PlexusContainerException, MavenExecutionException {
		super(new BWMavenPluginEarStub());
		
	}

}

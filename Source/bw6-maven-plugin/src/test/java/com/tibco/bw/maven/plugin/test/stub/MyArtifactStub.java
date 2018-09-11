package com.tibco.bw.maven.plugin.test.stub;

import java.io.File;

import org.apache.maven.artifact.handler.ArtifactHandler;
import org.apache.maven.plugin.testing.stubs.ArtifactStub;

public class MyArtifactStub extends ArtifactStub{
	
	private ArtifactHandler artifactHandler;
	
	private String baseVersion;
	
	public MyArtifactStub(String groupId, String artifactId,String version, String scope, String type ){
		setGroupId(groupId);
		setArtifactId(artifactId);
		setVersion(version);
		setScope(scope);
		setType(type);
		
	}
	
	 /** {@inheritDoc} */
    public ArtifactHandler getArtifactHandler()
    {
        return artifactHandler;
    }

    /** {@inheritDoc} */
    public void setArtifactHandler( ArtifactHandler handler )
    {
        this.artifactHandler = handler;
    }

    /** {@inheritDoc} */
    public String getBaseVersion()
    {
        return baseVersion;
    }

    /** {@inheritDoc} */
    public void setBaseVersion( String version)
    {
        this.baseVersion = version;
    }
    
  
	
}

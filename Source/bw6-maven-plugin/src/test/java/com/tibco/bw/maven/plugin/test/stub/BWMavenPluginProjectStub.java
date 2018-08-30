package com.tibco.bw.maven.plugin.test.stub;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.apache.maven.MavenExecutionException;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Build;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.plugin.testing.stubs.ArtifactStub;
import org.apache.maven.plugin.testing.stubs.MavenProjectStub;
import org.apache.maven.plugins.annotations.Component;
import org.codehaus.plexus.util.ReaderFactory;

import com.tibco.bw.maven.plugin.lifecycle.BWProjectLifeCycleListener;

public class BWMavenPluginProjectStub
    extends MavenProjectStub
{
    /**
     * Default constructor
     */
	
    public BWMavenPluginProjectStub() throws MavenExecutionException
    {
        MavenXpp3Reader pomReader = new MavenXpp3Reader();
        Model model;
        try
        {
            model = pomReader.read( ReaderFactory.newXmlReader( new File( getBasedir(), "pom.xml" ) ) );
            setModel( model );
        }
        catch ( Exception e )
        {
            throw new RuntimeException( e );
        }

        setGroupId( model.getGroupId() );
        setArtifactId( model.getArtifactId() );
        setVersion( model.getParent().getVersion() );
        setName( model.getArtifactId());
        setUrl( model.getUrl() );
        setPackaging( model.getPackaging() );
        
        Set<Artifact> hSet = new HashSet<Artifact>();
        for (Dependency x : model.getDependencies()){
        	 Artifact artifact = new ArtifactStub();
             artifact.setArtifactId(x.getArtifactId());
             artifact.setGroupId(x.getGroupId());
             artifact.setVersion(x.getVersion());
             hSet.add(artifact);
        }
        
        setDependencyArtifacts(hSet);
        setVersion(model.getParent().getVersion());
        
        Build build = new Build();
        build.setFinalName( model.getArtifactId() );
        build.setDirectory( getBasedir() + "/target" );
        build.setSourceDirectory( getBasedir() + "/src/main/java" );
        build.setOutputDirectory( getBasedir() + "/target/classes" );
        build.setTestSourceDirectory( getBasedir() + "/src/test/java" );
        build.setTestOutputDirectory( getBasedir() + "/target/test-classes" );
        setBuild( build );

        List compileSourceRoots = new ArrayList();
        compileSourceRoots.add( getBasedir() + "/src/main/java" );
        setCompileSourceRoots( compileSourceRoots );

        List testCompileSourceRoots = new ArrayList();
        testCompileSourceRoots.add( getBasedir() + "/src/test/java" );
        setTestCompileSourceRoots( testCompileSourceRoots );
       /* BWProjectLifeCycleListener listener = new BWProjectLifeCycleListener();
        listener.afterProjectsRead(session);*/
       
    }

    /** {@inheritDoc} */
    public File getBasedir()
    {
        return new File("C:/workspace/Test/");
    }
}
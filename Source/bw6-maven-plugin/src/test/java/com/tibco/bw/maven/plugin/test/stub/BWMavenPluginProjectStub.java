package com.tibco.bw.maven.plugin.test.stub;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.maven.MavenExecutionException;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.repository.DefaultArtifactRepository;
import org.apache.maven.artifact.repository.layout.DefaultRepositoryLayout;
import org.apache.maven.model.Build;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.plugin.testing.stubs.ArtifactStub;
import org.apache.maven.plugin.testing.stubs.DefaultArtifactHandlerStub;
import org.apache.maven.plugin.testing.stubs.MavenProjectStub;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.ReaderFactory;
import org.eclipse.aether.repository.RemoteRepository;

public class BWMavenPluginProjectStub
    extends MavenProjectStub
{
	
    /**
     * Default constructor
     */
	
			
	public BWMavenPluginProjectStub(File file) throws MavenExecutionException
    {
        MavenXpp3Reader pomReader = new MavenXpp3Reader();
        Model model;
        try
        {
            model = pomReader.read( ReaderFactory.newXmlReader( new File( file, "pom.xml" ) ) );
            setModel( model );
        }
        catch ( Exception e )
        {
            throw new RuntimeException( e );
        }

        setGroupId( model.getGroupId()==null ? model.getParent().getGroupId() : model.getGroupId() );
        setArtifactId( model.getArtifactId() );
        setVersion( model.getParent().getVersion() );
        setName( model.getArtifactId());
        setUrl( model.getUrl() );
        setPackaging( model.getPackaging() );
        setFile(new File( file, "pom.xml" ));
        
        Artifact artifact = new MyArtifactStub(getGroupId(), getArtifactId(), getVersion(), null, getPackaging());
        artifact.setArtifactHandler(new DefaultArtifactHandlerStub(getPackaging()));
        artifact.setBaseVersion(getVersion());
        //artifact.setFile(destination);
        setArtifact(artifact);
        //setRemoteArtifactRepositories(getRemoteArtifactRepositories());
        
        Set<Artifact> hSet = new HashSet<Artifact>();
        for (Dependency x : model.getDependencies()){
        	ArtifactStub depArtifact = new MyArtifactStub(x.getGroupId(), x.getArtifactId(), x.getVersion(), x.getScope()==null?"compile":x.getScope(), x.getType());
        	depArtifact.setArtifactHandler(new DefaultArtifactHandlerStub(x.getType()));
        	depArtifact.setFile(new File(System.getProperty("user.home")+"/.m2/repository"+"/"+x.getGroupId()+"/"+x.getArtifactId()+"/"+x.getVersion()+"/"+x.getArtifactId()+"-"+x.getVersion()+".jar"));
        	
        	hSet.add(depArtifact);
        }
        
        setDependencyArtifacts(hSet);
        setVersion(model.getParent().getVersion());
       
        Build build = new Build();
        build.setFinalName( model.getArtifactId() );
        build.setDirectory( file + "/target" );
        build.setSourceDirectory( file + "/src/main/java" );
        build.setOutputDirectory( file + "/target/classes" );
        build.setTestSourceDirectory( file + "/src/test/java" );
        build.setTestOutputDirectory( file + "/target/test-classes" );
        setBuild( build );

        List<String> compileSourceRoots = new ArrayList<>();
        compileSourceRoots.add( file + "/src/main/java" );
        setCompileSourceRoots( compileSourceRoots );

        List<String> testCompileSourceRoots = new ArrayList<>();
        testCompileSourceRoots.add( file + "/src/test/java" );
        setTestCompileSourceRoots( testCompileSourceRoots );
        
    }
	
	/*@SuppressWarnings("deprecation")
	@Override
	public List<ArtifactRepository> getRemoteArtifactRepositories() {

		ArtifactRepository repository = new DefaultArtifactRepository(
				"central", "http://repo.maven.apache.org/maven2",
				new DefaultRepositoryLayout());
		return Collections.singletonList(repository);
	}
*/
	
}
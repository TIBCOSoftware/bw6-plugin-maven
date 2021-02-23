package com.tibco.bw.maven.plugin.swagger;

import com.tibco.bw.maven.plugin.test.helpers.BWTestConfig;
import com.tibco.bw.maven.plugin.test.setuplocal.*;
import com.tibco.bw.maven.plugin.utils.BWFileUtils;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.xml.sax.InputSource;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;


@Mojo(name = "bwdocs", defaultPhase = LifecyclePhase.PACKAGE)
public class SwaggerGenerator extends AbstractMojo {
    @Parameter(defaultValue="${session}", readonly=true)
    private MavenSession session;

    @Parameter(defaultValue="${project}", readonly=true)
    private MavenProject project;

    @Parameter( property = "engineStartupWaitTime" , defaultValue = "2" )
    private int engineStartupWaitTime;

    @Parameter( property = "engineDebugPort" , defaultValue = "8090" )
    private int engineDebugPort;

    //@Parameter( property = "osgiCommands" )
    private List<String> osgiCommands = Arrays.asList("lrestdoc");

    @Parameter( property = "restApiDocPort" , defaultValue = "7777" )
    private int restApiDocPort;

    @Parameter( property = "swaggerOutputFile" , defaultValue = "swagger.json" )
    private String swaggerOutputFile;

    @Override
    public void execute() throws MojoExecutionException {

        if(project.getPackaging().equals("bwear")) {
            try {
                if (!verifyParameters()) {
                    return;
                } else {
                    initialize();

                    runEngine();

                    Thread.sleep(2000);

                    OSGICommandExecutor cmdExecutor = new OSGICommandExecutor();

                    for(String command : osgiCommands)
                    {
                        BWTestConfig.INSTANCE.getLogger().info("------------------------------------------------------------------------");
                        BWTestConfig.INSTANCE.getLogger().info("## Executing OSGi command ("+ command +") ##");
                        BWTestConfig.INSTANCE.getLogger().info("------------------------------------------------------------------------");

                        cmdExecutor.executeCommand(command);
                    }

                    generateSwagger();
                }
            }
            catch(Exception e )
            {
                getLog().error(e);
                throw new MojoExecutionException("Failed to generate Swagger file", e);
            }
            finally
            {
                if( BWTestConfig.INSTANCE.getEngineProcess() != null )
                {
                    BWTestConfig.INSTANCE.getEngineProcess().destroyForcibly();
                }
                if( BWTestConfig.INSTANCE.getConfigDir() != null )
                {
                    BWTestConfig.INSTANCE.getConfigDir().delete();
                }

            }
        }
    }


    private boolean verifyParameters() throws XPathExpressionException
    {
        String tibcoHome = project.getProperties().getProperty("tibco.Home");
        String bwHome = project.getProperties().getProperty("bw.Home");

        if( tibcoHome == null || tibcoHome.isEmpty() || bwHome == null || bwHome.isEmpty() )
        {
            getLog().info( "------------------------------------------------------------------------" );
            getLog().info( "TIBCO Home or BW Home is not provided. Skipping Swagger Generation Phase.");
            getLog().info( "------------------------------------------------------------------------" );

            return false;
        }

        if(!checkForSwaggers()) {
            getLog().info( "-----------------------------------------------------------" );
            getLog().info( "No REST Project detected. Skipping Swagger Generation Phase.");
            getLog().info( "-----------------------------------------------------------" );

            return false;
        }

        return true;
    }

    private boolean checkForSwaggers() throws XPathExpressionException
    {
        List<MavenProject> projects = session.getProjects();

        for( MavenProject project : projects )
        {
            if( project.getPackaging().equals("bwmodule") )
            {
                List<File> files = BWFileUtils.getEntitiesfromLocation( project.getBasedir().toString() , "bwm");
                if( files.size() > 0 )
                {
                    final XPathFactory factory = XPathFactory.newInstance();
                    final XPath xpath = factory.newXPath();
                    XPathExpression expression = xpath.compile("boolean(//*[local-name()='binding'][@*[local-name()='type' and .='rest:RestServiceBinding']])");

                    for (File file:files) {
                        getLog().info("Check " + file + " for REST Services");
                        InputSource is = new InputSource(file.getAbsolutePath());
                        String eval = expression.evaluate(is);
                        if (Boolean.valueOf(eval)) {
                            getLog().info("   Found REST Service into file " + file);
                            return true;
                        }
                    }
                }
            }
        }
        getLog().info( "-------------------------------------------------------" );
        getLog().info( "No Swagger files exist. ");
        getLog().info( "-------------------------------------------------------" );

        return false;
    }


    private void initialize() throws Exception
    {
        String tibcoHome = project.getProperties().getProperty("tibco.Home");
        String bwHome = project.getProperties().getProperty("bw.Home");

        BWTestExecutor.INSTANCE.setEngineDebugPort(engineDebugPort);

        BWTestExecutor.INSTANCE.setRestApiDocPort(restApiDocPort);

        BWTestExecutor.INSTANCE.setOsgiCommands(osgiCommands);

        BWTestExecutor.INSTANCE.setSkipInitMainProcessActivities(true);

        BWTestExecutor.INSTANCE.setSkipInitAllNonTestProcessActivities(true);

        BWTestConfig.INSTANCE.reset();

        BWTestConfig.INSTANCE.init(  tibcoHome , bwHome , session, project , getLog() );

        getLog().info( "" );
        getLog().info( "-------------------------------------------------------" );
        getLog().info( " Running BW Instance " );
        getLog().info( "-------------------------------------------------------" );
    }

    private void runEngine() throws Exception
    {
        EngineLaunchConfigurator config = new EngineLaunchConfigurator();
        config.loadConfiguration();

        ConfigFileGenerator gen = new ConfigFileGenerator();
        gen.generateConfig();

        EngineRunner runner = new EngineRunner(engineStartupWaitTime, Arrays.asList());
        runner.run();
    }

    private void generateSwagger() throws Exception
    {
        String swaggerUrl="http://localhost:" + restApiDocPort + "/" + project.getArtifactId() + "/swagger.json";

        Files.copy(
                new URL(swaggerUrl).openStream(),
                Paths.get(swaggerOutputFile),
                StandardCopyOption.REPLACE_EXISTING);
    }
}

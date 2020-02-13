package com.tibco.bw.maven.plugin.test;

import java.io.File;
import java.util.List;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import com.tibco.bw.maven.plugin.test.helpers.BWTestConfig;
import com.tibco.bw.maven.plugin.test.helpers.TestFileParser;
import com.tibco.bw.maven.plugin.test.setuplocal.BWTestExecutor;
import com.tibco.bw.maven.plugin.utils.BWFileUtils;

@Mojo(name = "bwtest", defaultPhase = LifecyclePhase.TEST)
public class BWTestMojo extends AbstractMojo {
	@Parameter(defaultValue="${session}", readonly=true)
    private MavenSession session;

	@Parameter(defaultValue="${project}", readonly=true)
    private MavenProject project;

    @Parameter(property = "testFailureIgnore", defaultValue = "false")
    private boolean testFailureIgnore;

    @Parameter(property = "skipTests", defaultValue = "false")
    protected boolean skipTests;

    @Parameter(property = "failIfNoTests", defaultValue = "true")
    private boolean failIfNoTests;

    @Parameter(property = "disableMocking", defaultValue = "false")
    private boolean disableMocking;

    @Parameter(property = "disableAssertions", defaultValue = "false")
    private boolean disableAssertions;

    @Parameter(property = "engineDebugPort", defaultValue = "8090")
    private int engineDebugPort;

    private String eclipsePluginsPath = null;

    public void execute() throws MojoExecutionException, MojoFailureException {

        BWTestExecutor executor = new BWTestExecutor();
        try {

            session.getProjects();

            if (!verifyParameters()) {
                return;
            }

            initialize();

            executor.execute();
        } catch (Exception e) {

            if (e instanceof MojoFailureException) {
                if (!testFailureIgnore) {
                    throw (MojoFailureException) e;

                } else {
                    System.out.println("Ignoring the exception for generating the report");
                }
            } else {

                e.printStackTrace();
                throw new MojoExecutionException(e.getMessage(), e);
            }
        }

    }


    private boolean verifyParameters() throws Exception {


        if (isSkipTests()) {
            getLog().info("-------------------------------------------------------");
            getLog().info("Skipping Test phase.");
            getLog().info("-------------------------------------------------------");


            return false;
        }

        String tibcoHome = project.getProperties().getProperty("tibco.Home");
        String bwHome = project.getProperties().getProperty("bw.Home");

        if (tibcoHome == null || tibcoHome.isEmpty() || bwHome == null || bwHome.isEmpty()) {
            getLog().info("-------------------------------------------------------");
            getLog().info("Tibco Home or BW Home is not provided. Skipping Test Phase.");
            getLog().info("-------------------------------------------------------");

            return false;
        }

        File file = new File(tibcoHome + bwHome);
        if (!file.exists() || !file.isDirectory()) {
            getLog().info("-------------------------------------------------------");
            getLog().info("Provided TibcoHome directory is invalid. Skipping Test Phase.");
            getLog().info("-------------------------------------------------------");

            return false;
        }
        try {
            eclipsePluginsPath = project.getProperties().getProperty("eclipse.plugins");
        } catch (Exception ex) {
            getLog().info("-------------------------------------------------------");
            getLog().info("Provided eclipse.plugins directory is invalid - Trying the default one");
            getLog().info("-------------------------------------------------------");
        }
        eclipsePluginsPath = (eclipsePluginsPath != null) ? eclipsePluginsPath : tibcoHome + "/tools/p2director/eclipse/plugins";
        getLog().info("-------------------------------------------------------");
        getLog().info(" Searching in : " + eclipsePluginsPath);
        getLog().info("-------------------------------------------------------");
        File eclipseDependenciesDir = new File(eclipsePluginsPath);
        if (!eclipseDependenciesDir.exists()) {
            getLog().info("-------------------------------------------------------");
            getLog().info("No Eclipse Dependencies found. Skipping Test Phase.");
            getLog().info("-------------------------------------------------------");

            return false;
        }
        boolean exists = checkForTest();


        if (getFailIfNoTests()) {
            if (!exists) {
                throw new MojoFailureException("No Test files existing in any of the Module.");
            }
        } else {
            if (!exists) {
                getLog().info("-------------------------------------------------------");
                getLog().info("No Tests found in any Module. Skipping Test Phase.");
                getLog().info("-------------------------------------------------------");
                return false;
            }
        }


        return true;

    }


    private boolean checkForTest() {
        List<MavenProject> projects = session.getProjects();
        for (MavenProject project : projects) {
            if (project.getPackaging().equals("bwmodule")) {
                List<File> files = BWFileUtils.getEntitiesfromLocation(project.getBasedir().toString(), "bwt");
                if (files.size() > 0) {
                    return true;
                }
            }
        }
        getLog().info("-------------------------------------------------------");
        getLog().info("No BWT Test files exist. ");
        getLog().info("-------------------------------------------------------");

        return false;
    }

    private void initialize() throws Exception {
        String tibcoHome = project.getProperties().getProperty("tibco.Home");
        String bwHome = project.getProperties().getProperty("bw.Home");

        TestFileParser.INSTANCE.setdisbleMocking(disableMocking);

        TestFileParser.INSTANCE.setdisbleAssertions(disableAssertions);

        BWTestExecutor.INSTANCE.setEngineDebugPort(engineDebugPort);

        BWTestConfig.INSTANCE.reset();

        BWTestConfig.INSTANCE.init(tibcoHome, bwHome, eclipsePluginsPath, session, project, getLog());

        getLog().info("");
        getLog().info("-------------------------------------------------------");
        getLog().info(" Running BW Tests ");
        getLog().info("-------------------------------------------------------");
    }


    public boolean isSkipTests() {
        if (!skipTests) {
            if ("true".equals(project.getProperties().get("skipTests"))) {
                return true;
            }
        }
        return skipTests;
    }


    public void setSkipTests(boolean skipTests) {
        this.skipTests = skipTests;
    }


    public boolean isTestFailureIgnore() {
        return testFailureIgnore;
    }

    public void setTestFailureIgnore(boolean testFailureIgnore) {
        this.testFailureIgnore = testFailureIgnore;
    }


    public boolean getFailIfNoTests() {
        return failIfNoTests;
    }


    public void setFailIfNoTests(boolean failIfNoTests) {
        this.failIfNoTests = failIfNoTests;
    }
}

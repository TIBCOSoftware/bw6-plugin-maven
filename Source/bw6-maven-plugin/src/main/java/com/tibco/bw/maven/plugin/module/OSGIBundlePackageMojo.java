package com.tibco.bw.maven.plugin.module;

import com.tibco.bw.maven.plugin.utils.Constants;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;

/**
 * Created by e7nrlj4 on 7/18/2017.
 */

@Mojo(name = Constants.OSGI_BUNDLE, defaultPhase = LifecyclePhase.PACKAGE, requiresDependencyResolution = ResolutionScope.COMPILE)
public class OSGIBundlePackageMojo extends AbstractBWModulePackageMojo {

    public void execute() throws MojoExecutionException {
        classifier = Constants.OSGI_BUNDLE;
        super.executeInternal();
    }
}

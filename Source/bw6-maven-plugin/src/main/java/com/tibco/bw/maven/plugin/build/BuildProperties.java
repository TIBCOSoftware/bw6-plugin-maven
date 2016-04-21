package com.tibco.bw.maven.plugin.build;

import java.util.List;

public interface BuildProperties {


    List<String> getBinIncludes();

    List<String> getBinExcludes();

    List<String> getSourceIncludes();

    List<String> getSourceExcludes();

}

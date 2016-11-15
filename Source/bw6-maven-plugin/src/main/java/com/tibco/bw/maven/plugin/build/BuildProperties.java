package com.tibco.bw.maven.plugin.build;

import java.util.List;

public interface BuildProperties {
    public List<String> getBinIncludes();
    public List<String> getBinExcludes();
    public List<String> getSourceIncludes();
    public List<String> getSourceExcludes();
}

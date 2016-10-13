package com.tibco.bw.maven.plugin.build;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

public class BuildPropertiesImpl implements BuildProperties {
    private List<String> binIncludes;
    private List<String> binExcludes;
    private List<String> sourceIncludes;
    private List<String> sourceExcludes;

    public BuildPropertiesImpl( Properties properties ) {
    	init(properties);
    }

    public void init( Properties properties ) {
        sourceIncludes = splitAndTrimCommaSeparated(properties.getProperty("src.includes"));
        sourceExcludes = splitAndTrimCommaSeparated(properties.getProperty("src.excludes"));
        binIncludes = splitAndTrimCommaSeparated(properties.getProperty("bin.includes"));
        binExcludes = splitAndTrimCommaSeparated(properties.getProperty("bin.excludes"));
    }

    private static List<String> splitAndTrimCommaSeparated(String rawValue) {
        List<String> result = new ArrayList<String>();
        if (rawValue != null) {
            for (String element : rawValue.split(",")) {
                result.add(element.trim());
            }
        }
        if (result.isEmpty()) {
            return Collections.emptyList();
        }
        return result;
    }

	public List<String> getBinIncludes() {
		return binIncludes;
	}

	public void setBinIncludes(List<String> binIncludes) {
		this.binIncludes = binIncludes;
	}

	public List<String> getBinExcludes() {
		return binExcludes;
	}

	public void setBinExcludes(List<String> binExcludes) {
		this.binExcludes = binExcludes;
	}

	public List<String> getSourceIncludes() {
		return sourceIncludes;
	}

	public void setSourceIncludes(List<String> sourceIncludes) {
		this.sourceIncludes = sourceIncludes;
	}

	public List<String> getSourceExcludes() {
		return sourceExcludes;
	}

	public void setSourceExcludes(List<String> sourceExcludes) {
		this.sourceExcludes = sourceExcludes;
	}
}

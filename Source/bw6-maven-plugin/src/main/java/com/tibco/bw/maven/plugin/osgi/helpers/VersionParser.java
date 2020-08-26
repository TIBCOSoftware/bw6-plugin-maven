package com.tibco.bw.maven.plugin.osgi.helpers;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.tibco.bw.maven.plugin.utils.Constants;

public class VersionParser {
    protected static SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmm");
    public static final String QUALIFIER = "qualifier";

	public static Version parseVersion(String version) {
		if (version == null) {
				return Version.EMPTYVERSION;
		}
		return new Version(version);
	}

	public static String getcalculatedOSGiVersion(String versionStr, String qualifierReplacement) {
		Version version = parseVersion(versionStr);
		String calcQualifier = calculateQualifier(version.getQualifier());
		if(!qualifierReplacement.equalsIgnoreCase(Constants.TIMESTAMP))
			calcQualifier = qualifierReplacement;
		String fullVersion = version.getMajor() + "." + version.getMinor() + "." + version.getMicro();
		if(!calcQualifier.isEmpty()) {
			fullVersion += "." + calcQualifier;
		}
		return fullVersion;
	}

    private static String calculateQualifier(String qualifier) {
    	if( QUALIFIER.equals(qualifier)) {
            Date timestamp = new Date();
            return format.format(timestamp);
    	}
    	return qualifier;
    }
}

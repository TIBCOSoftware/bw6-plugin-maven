package com.tibco.bw.maven.plugin.osgi.helpers;

import java.util.NoSuchElementException;
import java.util.StringTokenizer;

public class Version {
	private final int major;
	private final int minor;
	private final int micro;
	private final String qualifier;
	private static final String	SEPARATOR = ".";
	public static final Version	EMPTYVERSION = new Version(0, 0, 0);

	public Version(int major, int minor, int micro) {
		this(major, minor, micro, null);
	}

	public Version(int major, int minor, int micro, String qualifier) {
		if (qualifier == null) {
			qualifier = "";
		}
		this.major = major;
		this.minor = minor;
		this.micro = micro;
		this.qualifier = qualifier;
		validate();
	}

	public Version(String version) {
		int maj = 0;
		int min = 0;
		int mic = 0;
		String qual = "";
		try {
			StringTokenizer st = new StringTokenizer(version, SEPARATOR, true);
			maj = parseInt(st.nextToken(), version);
			if (st.hasMoreTokens()) { 
				st.nextToken(); 
				min = parseInt(st.nextToken(), version);
				if (st.hasMoreTokens()) {
					st.nextToken(); 
					mic = parseInt(st.nextToken(), version);
					if (st.hasMoreTokens()) { 
						st.nextToken(); 
						qual = st.nextToken(""); 
						if (st.hasMoreTokens()) { 
							throw new IllegalArgumentException("invalid version \"" + version + "\": invalid format");
						}
					}
				}
			}
		} catch (NoSuchElementException e) {
			IllegalArgumentException iae = new IllegalArgumentException("invalid version \"" + version + "\": invalid format");
			iae.initCause(e);
			throw iae;
		}
		major = maj;
		minor = min;
		micro = mic;
		qualifier = qual;
		validate();
	}

	private static int parseInt(String value, String version) {
		try {
			return Integer.parseInt(value);
		} catch(NumberFormatException e) {
			IllegalArgumentException iae = new IllegalArgumentException("invalid version \"" + version + "\": non-numeric \"" + value + "\"");
			iae.initCause(e);
			throw iae;
		}
	}

	private void validate() {
		if (major < 0 || minor < 0 || micro < 0) {
			throw new IllegalArgumentException("Invalid version");
		}
		for (char ch : qualifier.toCharArray()) {
			if (('A' <= ch) && (ch <= 'Z')) {
				continue;
			}
			if (('a' <= ch) && (ch <= 'z')) {
				continue;
			}
			if (('0' <= ch) && (ch <= '9')) {
				continue;
			}
			if ((ch == '_') || (ch == '-')) {
				continue;
			}
			throw new IllegalArgumentException("invalid qulifier "  + qualifier );
		}
	}

	public int getMajor() {
		return major;
	}

	public int getMinor() {
		return minor;
	}

	public int getMicro() {
		return micro;
	}

	public String getQualifier() {
		return qualifier;
	}
}

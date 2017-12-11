package com.tibco.bw.studio.maven.action;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "com.tibco.bw.studio.maven.action.messages"; //$NON-NLS-1$
	public static String MavenPOMProcessor_FailureMessage;
	public static String MavenPOMProcessor_FailureMessage2;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}

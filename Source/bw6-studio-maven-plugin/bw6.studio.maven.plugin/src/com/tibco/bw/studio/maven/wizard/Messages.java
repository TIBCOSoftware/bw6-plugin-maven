package com.tibco.bw.studio.maven.wizard;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "com.tibco.bw.studio.maven.wizard.messages"; //$NON-NLS-1$
	public static String MavenWizard_Deployemnt_Config;
	public static String MavenWizard_Docker_Config;
	public static String MavenWizard_PCF_Config;
	public static String MavenWizard_POMConfig;
	public static String MavenWizard_WindowTitle;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}

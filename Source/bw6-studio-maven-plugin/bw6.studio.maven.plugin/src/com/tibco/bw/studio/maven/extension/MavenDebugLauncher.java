package com.tibco.bw.studio.maven.extension;

import java.util.List;

import com.tibco.bw.core.design.extension.IDebugLauncher;
import com.tibco.bw.studio.maven.action.ManifestProcessor;

public class MavenDebugLauncher implements IDebugLauncher {

	public MavenDebugLauncher()
	{
	}

	@Override
	public void execute( List<String> applications ) 
	{
		ManifestProcessor processor = new ManifestProcessor();
		processor.run( applications );
	}

}

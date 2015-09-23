package com.tibco.bw.studio.maven.extension;

import java.util.List;

import com.tibco.bw.studio.maven.action.ManifestProcessor;
import com.tibco.bwcore.design.extension.IDebugLauncher;

public class MavenDebugLauncher implements IDebugLauncher {

	public MavenDebugLauncher() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void execute( List<String> applications ) 
	{
		ManifestProcessor processor = new ManifestProcessor();
		processor.run( applications );
	}

}

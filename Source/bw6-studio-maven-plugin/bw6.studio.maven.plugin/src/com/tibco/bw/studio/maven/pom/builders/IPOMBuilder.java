package com.tibco.bw.studio.maven.pom.builders;

import com.tibco.bw.studio.maven.modules.model.BWModule;
import com.tibco.bw.studio.maven.modules.model.BWProject;

public interface IPOMBuilder 
{

	public void build( BWProject project ,  BWModule module) throws Exception;
	
}

package com.tibco.bw.maven.gatherers;

import com.tibco.bw.maven.utils.BWProjectInfo;

public interface IBWProjectInfoGatherer 
{
	public BWProjectInfo gather() throws Exception;

}

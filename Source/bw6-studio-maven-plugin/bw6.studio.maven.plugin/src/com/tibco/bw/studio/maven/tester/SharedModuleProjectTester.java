package com.tibco.bw.studio.maven.tester;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.resources.IProject;

import com.tibco.bw.studio.maven.validation.ProjectUtils;

public class SharedModuleProjectTester extends PropertyTester{

	@Override
	public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
		if(receiver instanceof IProject){
			IProject project = (IProject)receiver;
			if(ProjectUtils.INSTANCE.isBWSharedModule(project)){
				return true;
			}
		}
		return false;
	}
}

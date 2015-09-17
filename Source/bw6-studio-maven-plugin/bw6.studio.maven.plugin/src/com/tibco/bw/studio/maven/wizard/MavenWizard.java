/*
 * Copyright (c) 2013-2014 TIBCO Software Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.tibco.bw.studio.maven.wizard;

import org.eclipse.jface.wizard.Wizard;

import com.tibco.bw.studio.maven.modules.BWProject;

public class MavenWizard extends Wizard 
{

	  protected WizardPage1 one;
	  private BWProject project;
  
	  public MavenWizard( BWProject project ) 
	  {
		  super();
		  this.project = project;
		  setNeedsProgressMonitor(true);
	  }

	  @Override
	  public void addPages() 
	  {
	    one = new WizardPage1( "POM Configuration" , project );
	    addPage(one);
	  }

	  @Override
	  public boolean performFinish() 
	  {
		project = one.getUpdatedProject();
	    return true;
	  }

	public BWProject getProject() 
	{
		return project;
	}

	 
}



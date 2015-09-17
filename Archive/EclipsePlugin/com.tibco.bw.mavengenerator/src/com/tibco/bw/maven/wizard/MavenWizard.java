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

package com.tibco.bw.maven.wizard;

import org.eclipse.jface.wizard.Wizard;

import com.tibco.bw.maven.utils.BWProjectInfo;

public class MavenWizard extends Wizard 
{

	  protected WizardOne one;
	  
	  private BWProjectInfo bwProjectInfo;
	  
	  public MavenWizard() 
	  {
	    super();
	    setNeedsProgressMonitor(true);
	  }

	  @Override
	  public void addPages() 
	  {
	    one = new WizardOne( "POM Configuration");
	    one.setBwProjectInfo(bwProjectInfo);
	    addPage(one);
	  }

	  @Override
	  public boolean performFinish() 
	  {
		bwProjectInfo = one.getUpdatedBWInfo();  
	    return true;
	  }

	public BWProjectInfo getBwProjectInfo() 
	{
		return bwProjectInfo;
	}

	public void setBwProjectInfo(BWProjectInfo bwProjectInfo) 
	{
		this.bwProjectInfo = bwProjectInfo;
	}
	
	 
}



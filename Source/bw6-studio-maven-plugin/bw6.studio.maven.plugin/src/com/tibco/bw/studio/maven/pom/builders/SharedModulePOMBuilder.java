package com.tibco.bw.studio.maven.pom.builders;

import com.tibco.bw.studio.maven.modules.model.BWAppModule;
import com.tibco.bw.studio.maven.modules.model.BWModule;
import com.tibco.bw.studio.maven.modules.model.BWPluginModule;
import com.tibco.bw.studio.maven.modules.model.BWProject;
import com.tibco.bw.studio.maven.modules.model.BWProjectType;

public class SharedModulePOMBuilder extends ModulePOMBuilder {

	@Override
	public void build(BWProject project, BWModule module) throws Exception {
		
		if(project.getType() == BWProjectType.SharedModule){
			if (!module.isOverridePOM()) {
				return;
			}
			
			this.project = project;
			this.module = module;
			this.bwEdition = "bw6";
			
			initializeModel();
			addPrimaryTags();
			addSecondaryTags();
			addBuild();
			if (module instanceof BWAppModule) {
				addPaletteSharedDependency();
			}
			// addModuleDependencies();
			if (module instanceof BWPluginModule && ((BWPluginModule) module).isCustomXpath()) {
				addCustomXPathDependency();
			}
			// addProperties();
			generatePOMFile();
		}else{
			super.build(project, module);
		}
	}
	
	protected void addSecondaryTags(){
		model.setGroupId(module.getGroupId());
		model.setVersion(module.getVersion());
	}
}

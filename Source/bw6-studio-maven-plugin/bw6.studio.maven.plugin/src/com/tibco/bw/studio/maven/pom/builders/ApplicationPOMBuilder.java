package com.tibco.bw.studio.maven.pom.builders;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.maven.model.Build;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.ReportPlugin;
import org.apache.maven.model.Reporting;
import org.codehaus.plexus.util.xml.Xpp3Dom;

import com.tibco.bw.studio.maven.helpers.ManifestParser;
import com.tibco.bw.studio.maven.helpers.ModuleHelper;
import com.tibco.bw.studio.maven.modules.model.BWApplication;
import com.tibco.bw.studio.maven.modules.model.BWDeploymentInfo;
import com.tibco.bw.studio.maven.modules.model.BWModule;
import com.tibco.bw.studio.maven.modules.model.BWProject;
import com.tibco.bw.studio.maven.modules.model.BWTestInfo;
import com.tibco.bw.studio.maven.wizard.BWProjectTypes;
import com.tibco.bw.studio.maven.wizard.MavenWizardContext;

public class ApplicationPOMBuilder extends AbstractPOMBuilder implements IPOMBuilder {
	private String bwEdition;
	
	@Override
	public void build(BWProject project, BWModule module) throws Exception {
		if(!module.isOverridePOM()) {
			return;
		}

		this.project = project;
		this.module = module;
		
		Map<String, String> manifest = ManifestParser.parseManifest(module.getProject());
		if (manifest.containsKey("TIBCO-BW-Edition") )				
		{
			String editions = manifest.get( "TIBCO-BW-Edition" );				
	
				String[] editionList = editions.split(",");
				for( String str : editionList )
				{
					switch ( str )
					{
					case "bwe":
						bwEdition = "bw6";
						break;

					case "bwcf":
						
							switch ( MavenWizardContext.INSTANCE.getSelectedType() )
							{
							case PCF:
								bwEdition = "cf";
								break;
								
							case Docker:
								bwEdition = "docker";
								break;
							
							default:
								break;
							}
						
						break;
				default:
						break;
					}
					
				}
		}
		initializeModel();
		addPrimaryTags();
		//addParent(ModuleHelper.getParentModule(project.getModules()));

		if( model != null && model.getProperties() == null )
		{
			model.setProperties( new Properties());
		}
		model.getProperties().put("project.type", MavenWizardContext.INSTANCE.getSelectedType().toString());
		
		addParent(ModuleHelper.getParentModule(project.getModules()));

		if( MavenWizardContext.INSTANCE.getSelectedType() == BWProjectTypes.PCF )
		{
			addBWCloudFoundryProperties();
		} else if(MavenWizardContext.INSTANCE.getSelectedType() == BWProjectTypes.Docker )
		{
			String platform = module.getBwDockerModule().getPlatform();
			addBWDockerProperties(platform);
		}
		addBuild();
		addReporting();
		generatePOMFile();
	}

	@Override
	protected void addDeploymentDetails(Plugin plugin) {
		BWTestInfo testInfo = ((BWApplication)module).getTestInfo();
		
		if( testInfo.getSkipTests() != null && !testInfo.getSkipTests().isEmpty())
		{
			model.getProperties().put("skipTests", testInfo.getSkipTests() );
			model.getProperties().put("failIfNoTests", testInfo.getFailIfNoTests() );
			model.getProperties().put("tibco.Home", testInfo.getTibcoHome() );
			model.getProperties().put("bw.Home", testInfo.getBwHome() );
		}
		
		
		
		if( MavenWizardContext.INSTANCE.getSelectedType() != BWProjectTypes.AppSpace )
		{
			return;
		}
		Properties properties = new Properties();
		plugin.setConfiguration(null);
		BWDeploymentInfo info = ((BWApplication)module).getDeploymentInfo();
		
		if(info == null || ! info.isDeployToAdmin()) {
			Xpp3Dom config = new Xpp3Dom("configuration");
			plugin.setConfiguration(config);
			model.getProperties().remove("deployToAdmin");
			model.getProperties().remove("agentHost");
			model.getProperties().remove("agentPort");
			model.getProperties().remove("agentAuth");
			model.getProperties().remove("agentUsername");
			model.getProperties().remove("agentPassword");
			model.getProperties().remove("agentSSL");
			model.getProperties().remove("truststorePath");
			model.getProperties().remove("truststorePassword");
			model.getProperties().remove("keystorePath");
			model.getProperties().remove("keystorePassword");
			model.getProperties().remove("domain");
			model.getProperties().remove("domainDesc");
			model.getProperties().remove("appSpace");
			model.getProperties().remove("appSpaceDesc");
			model.getProperties().remove("appNode");
			model.getProperties().remove("appNodeDesc");
			model.getProperties().remove("httpPort");
			model.getProperties().remove("osgiPort");
			model.getProperties().remove("redeploy");
			model.getProperties().remove("backup");
			model.getProperties().remove("backupLocation");
			model.getProperties().remove("profile");
			return;
		}

		Xpp3Dom config = new Xpp3Dom("configuration");
		properties.put("deploymentConfig.file", "");

		Xpp3Dom deployToAdmin  = new Xpp3Dom("deployToAdmin");
		deployToAdmin.setValue("${deployToAdmin}");
		model.addProperty("deployToAdmin", Boolean.toString(info.isDeployToAdmin()));
		properties.put("deployToAdmin", Boolean.toString(info.isDeployToAdmin()));

		Xpp3Dom agentHost = new Xpp3Dom("agentHost");
		agentHost.setValue("${agentHost}");
		model.addProperty("agentHost", info.getAgentHost());
		properties.put("agentHost", info.getAgentHost());

		Xpp3Dom agentPort = new Xpp3Dom("agentPort");
		agentPort.setValue("${agentPort}");
		model.addProperty("agentPort", info.getAgentPort());
		properties.put("agentPort", info.getAgentPort());

		Xpp3Dom agentAuth = new Xpp3Dom("agentAuth");
		agentAuth.setValue("${agentAuth}");
		model.addProperty("agentAuth", info.getAgentAuth());
		properties.put("agentAuth", info.getAgentAuth());

		Xpp3Dom agentUsername  = new Xpp3Dom("agentUsername");
		agentUsername.setValue("${agentUsername}");
		model.addProperty("agentUsername", info.getAgentUsername());
		properties.put("agentUsername", info.getAgentUsername());

		Xpp3Dom agentPassword = new Xpp3Dom("agentPassword");
		agentPassword.setValue("${agentPassword}");
		model.addProperty("agentPassword", info.getAgentPassword());		
		properties.put("agentPassword", info.getAgentPassword());

		Xpp3Dom agentSSL = new Xpp3Dom("agentSSL");
		agentSSL.setValue("${agentSSL}");
		model.addProperty("agentSSL", Boolean.toString(info.isAgentSSL()));
		properties.put("agentSSL", Boolean.toString(info.isAgentSSL()));

		Xpp3Dom trustPath  = new Xpp3Dom("truststorePath");
		trustPath.setValue("${truststorePath}");
		model.addProperty("truststorePath", info.getTrustPath());
		properties.put("truststorePath", info.getTrustPath());

		Xpp3Dom trustPass  = new Xpp3Dom("truststorePassword");
		trustPass.setValue("${truststorePassword}");
		model.addProperty("truststorePassword", info.getTrustPassword());
		properties.put("truststorePassword", info.getTrustPassword());

		Xpp3Dom keyPath  = new Xpp3Dom("keystorePath");
		keyPath.setValue("${keystorePath}");
		model.addProperty("keystorePath", info.getKeyPath());
		properties.put("keystorePath", info.getKeyPath());

		Xpp3Dom keyPass  = new Xpp3Dom("keystorePassword");
		keyPass.setValue("${keystorePassword}");
		model.addProperty("keystorePassword", info.getKeyPassword());
		properties.put("keystorePassword", info.getKeyPassword());

		Xpp3Dom domain  = new Xpp3Dom("domain");
		domain.setValue("${domain}");
		model.addProperty("domain", info.getDomain());
		properties.put("domain", info.getDomain());

		Xpp3Dom domainDesc  = new Xpp3Dom("domainDesc");
		domainDesc.setValue("${domainDesc}");
		model.addProperty("domainDesc", info.getDomainDesc());
		properties.put("domainDesc", info.getDomainDesc());

		Xpp3Dom appspace  = new Xpp3Dom("appSpace");
		appspace.setValue("${appSpace}");
		model.addProperty("appSpace", info.getAppspace());
		properties.put("appSpace", info.getAppspace());

		Xpp3Dom appspaceDesc  = new Xpp3Dom("appSpaceDesc");
		appspaceDesc.setValue("${appSpaceDesc}");
		model.addProperty("appSpaceDesc", info.getAppspaceDesc());
		properties.put("appSpaceDesc", info.getAppspaceDesc());

		Xpp3Dom appnode  = new Xpp3Dom("appNode");
		appnode.setValue("${appNode}");
		model.addProperty("appNode", info.getAppNode());
		properties.put("appNode", info.getAppNode());

		Xpp3Dom appnodeDesc  = new Xpp3Dom("appNodeDesc");
		appnodeDesc.setValue("${appNodeDesc}");
		model.addProperty("appNodeDesc", info.getAppNodeDesc());
		properties.put("appNodeDesc", info.getAppNodeDesc());

		Xpp3Dom osgiport  = new Xpp3Dom("osgiPort");
		osgiport.setValue("${osgiPort}");
		model.addProperty("osgiPort", info.getOsgiPort());
		properties.put("osgiPort", info.getOsgiPort());

		Xpp3Dom httpPort  = new Xpp3Dom("httpPort");
		httpPort.setValue("${httpPort}");
		model.addProperty("httpPort", info.getHttpPort());
		properties.put("httpPort", info.getHttpPort());

		Xpp3Dom profile  = new Xpp3Dom("profile");
		profile.setValue("${profile}");
		model.addProperty("profile", info.getProfile());
		properties.put("profile", info.getProfile());

		Xpp3Dom redeploy  = new Xpp3Dom("redeploy");
		redeploy.setValue("${redeploy}");
		model.addProperty("redeploy", Boolean.toString(info.isRedeploy()));
		properties.put("redeploy", Boolean.toString(info.isRedeploy()));

		Xpp3Dom backup  = new Xpp3Dom("backup");
		backup.setValue("${backup}");
		model.addProperty("backup", Boolean.toString(info.isBackup()));
		properties.put("backup", Boolean.toString(info.isBackup()));

		Xpp3Dom backupLocation  = new Xpp3Dom("backupLocation");
		backupLocation.setValue("${backupLocation}");
		model.addProperty("backupLocation", info.getBackupLocation());
		properties.put("backupLocation", info.getBackupLocation());

		config.addChild(deployToAdmin);
		config.addChild(agentHost);
		config.addChild(agentPort);
		config.addChild(agentAuth);
		config.addChild(agentUsername);
		config.addChild(agentPassword);
		config.addChild(agentSSL);
		config.addChild(trustPath);
		config.addChild(trustPass);
		config.addChild(keyPath);
		config.addChild(keyPass);
		config.addChild(domain);
		config.addChild(domainDesc);
		config.addChild(appspace);
		config.addChild(appspaceDesc);
		config.addChild(appnode);
		config.addChild(appnodeDesc);
		config.addChild(osgiport);
		config.addChild(httpPort);
		config.addChild(redeploy);
		config.addChild(backup);
		config.addChild(backupLocation);
		config.addChild(profile);

		plugin.setConfiguration(config);

		if(ModuleHelper.getParentModule(project.getModules()).getProject() != null) {
			File deploymentProperties = new File(ModuleHelper.getParentModule(project.getModules()).getProject().getLocationURI().getRawPath() + File.separator + "deployment.properties");

			if(deploymentProperties.exists()) {
				deploymentProperties.delete();
			}

			FileOutputStream fileOut = null;
			try {
				deploymentProperties.createNewFile();
				fileOut = new FileOutputStream(deploymentProperties);
				String msg = "EAR Deployment Properties. Pass -DdeploymentConfig.file=<File Location/File Name> if you are running from Command Line. Otherwise add 'deploymentConfig.file' property to the POM File";
				properties.store(fileOut, msg);
				fileOut.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	protected void addReporting()
	{
		
		Reporting reporting = model.getReporting();
		if( reporting == null )
		{
			reporting = new Reporting();
			model.setReporting(reporting);
		}
		List<ReportPlugin> plugins = reporting.getPlugins();
		if( plugins == null )
		{
			reporting.setPlugins( new ArrayList<ReportPlugin>());
		}
		
		boolean isReporting = false;
		for( ReportPlugin plugin : plugins )
		{
			if( plugin.getArtifactId().equals("bw6-maven-plugin"))
			{
				isReporting = true;
				break;
			}
		}
		
		if( !isReporting )
		{
			ReportPlugin p = new ReportPlugin();
			p.setGroupId("com.tibco.plugins");
			p.setArtifactId("bw6-maven-plugin");
			p.setVersion("2.0.1");
			reporting.getPlugins().add(p);
		}
		
	}
	protected void addBuild() {
		Build build = model.getBuild();
		if(build == null) {
			build = new Build();
		}
		addBW6MavenPlugin(build);

		// Add <profiles> in pom
		addBW6MavenProfile(model);

		String platform = "";
		if("docker".equals(bwEdition)) {
			platform = module.getBwDockerModule().getPlatform();
		}
		//Add this plugin
		if("cf".equals(bwEdition) || "docker".equals(bwEdition)) {
			addBWCEPropertiesPlugin(build, bwEdition, platform);
    	}
		if("cf".equals(bwEdition)) {
    		//Delete existing plugin
			List<Plugin> plugins=build.getPlugins();
    		for(int i = 0; i < plugins.size(); i++) {
    			Plugin plg = plugins.get(i);
    			if(plg.getArtifactId().equals("cf-maven-plugin")) {
    				build.removePlugin(plg);
    			}
    		}
    		//Add Plugin
    		addPCFMavenPlugin(build);
    	} else if("docker".equals(bwEdition)) {
    		List<Plugin> plugins = build.getPlugins();
    		for(int i = 0; i < plugins.size(); i++) {
    			Plugin plg = plugins.get(i);
    			if(plg.getArtifactId().equals("docker-maven-plugin")) {
    				build.removePlugin(plg);
    			}
    		}
    		addDockerMavenPlugin(build);

    		if("K8S".equals(platform)) {
    			for(int i = 0; i < plugins.size(); i++) {
        			Plugin plg = plugins.get(i);
        			if(plg.getArtifactId().equals("fabric8-maven-plugin")) {
        				build.removePlugin(plg);
        			}
        		}
    			addDockerK8SMavenPlugin(build, false);
    		}
    	}
    	model.setBuild(build);
	}

	@Override
	protected String getPackaging() {
		return "bwear";
	}
}

package com.tibco.bw.maven.plugin.application;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;


@Mojo(name = "bwjson", defaultPhase = LifecyclePhase.INSTALL)
public class BWJsonMojo extends AbstractMojo{

	private String getWorkspacepath() {
	
		String workspacePath= System.getProperty("user.dir");
		
		String wsPath= workspacePath.substring(0, workspacePath.lastIndexOf(".parent"));
	
		return wsPath;
		
	}
	
	
	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		String file=(getWorkspacepath() + File.separator + "k8s-dev.properties");
		Properties prop = new Properties();
		InputStream input = null;

		try {

			input = new FileInputStream(file);

			// load the Kubernetes properties file
			prop.load(input);
		}
		catch(Exception e)
		{
			throw new MojoExecutionException("Could not load input from "+file+" due to exception: "+e);
		}


		String fileDocker=(getWorkspacepath() + File.separator + "docker-dev.properties");
		Properties propsDocker = new Properties();
		input = null;

		try {

			input = new FileInputStream(fileDocker);

			// load the Docker properties file
			propsDocker.load(input);
		}
		catch(Exception e)
		{
			throw new MojoExecutionException("Could not load input from "+fileDocker+" due to exception: "+e);
		}

		File directorySrc = new File(String.valueOf(getWorkspacepath() + File.separator+"src"));


		if(!directorySrc.exists()){

			directorySrc.mkdir();
		}

		File directoryMain = new File(String.valueOf(getWorkspacepath() + File.separator+"src/main"));


		if(!directoryMain.exists()){

			directoryMain.mkdir();
		}
		File directoryFabric8 = new File(String.valueOf(getWorkspacepath() + File.separator+"src/main/fabric8"));


		if(!directoryFabric8.exists()){

			directoryFabric8.mkdir();
		}



		//snakeYAML for writing maps nested objects to YAML file
		String locationService = getWorkspacepath() + File.separator + "src/main/fabric8/service.yml";
		File serviceFile = new File(Paths.get(locationService).toString());
		try {
			serviceFile.createNewFile();
		} catch (IOException e1) {
			throw new MojoExecutionException("Could not create file service.yml due to exception: "+e1);
		}
		Map<String, Object> dataService = new HashMap<String, Object>();
		dataService.put("kind", "Service");
		Map<String, Object> metadataService=new HashMap<String, Object>();
		metadataService.put("name", prop.getProperty("fabric8.service.name"));

		Map<String, String> appName=new HashMap<String, String>();
		String appModule=getWorkspacepath().toString(); 
		if(appModule.endsWith(File.separator)){
			appModule=appModule.substring(0,appModule.length()-1);
		}
		String appModuleName=appModule.substring(appModule.lastIndexOf(File.separator)+1);
		appName.put("app", appModuleName);

		metadataService.put("labels",appName);
		dataService.put("metadata", metadataService);
		Map<String, Object> specdataService=new HashMap<String, Object>();
		specdataService.put("type", prop.getProperty("fabric8.service.type"));

		List<Map<String, Object>> portsList=new ArrayList<Map<String, Object>>();
		Map<String, Object> portInfo=new HashMap<String, Object>();
		portInfo.put("port", Integer.parseInt(prop.getProperty("fabric8.service.port")));
		portInfo.put("targetPort", Integer.parseInt(prop.getProperty("fabric8.service.containerPort")));

		portsList.add(portInfo);
		specdataService.put("ports", portsList);

		Map<String, String> appInfo=new HashMap<String, String>();
		appInfo.put("app", prop.getProperty("fabric8.service.name"));
		specdataService.put("selector", appInfo);




		dataService.put("spec", specdataService);

		DumperOptions options = new DumperOptions(); 
		options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK); 
		Yaml yaml = new Yaml(options); 
		FileWriter writer=null;
		try {
			writer = new FileWriter(locationService);
		} catch (IOException e1) {
			
			throw new MojoExecutionException("Could not write to service.yml due to exception: "+e1);
		}
		if(writer!=null)
		yaml.dump(dataService, writer);








		String locationDeployment = getWorkspacepath() + File.separator + "src/main/fabric8/deployment.yml";
		File deploymentFile = new File(Paths.get(locationDeployment).toString());
		try {
			deploymentFile.createNewFile();
		} catch (IOException e1) {
			throw new MojoExecutionException("Could not create file service.yml due to exception: "+e1);
		}	

		Map<String, Object> data = new HashMap<String, Object>();
		data.put("kind", "Deployment");
		Map<String, Object> metadata=new HashMap<String, Object>();
		metadata.put("name", prop.getProperty("fabric8.replicationController.name"));
		data.put("metadata", metadata);
		Map<String, Object> specdata=new HashMap<String, Object>();
		specdata.put("replicas", Integer.parseInt(prop.getProperty("fabric8.replicas")));
		HashMap<String, String> appInfoDeployment=new HashMap<String, String>();
		String application=getWorkspacepath().toString(); 
		if(application.endsWith(File.separator)){
			application=application.substring(0,application.length()-1);
		}
		String applicationName=application.substring(application.lastIndexOf(File.separator)+1);

		appInfoDeployment.put("app", applicationName);
		Map<String, Object> matchLabels=new HashMap<String, Object>();
		matchLabels.put("matchLabels", appInfoDeployment);
		specdata.put("selector",matchLabels);
		Map<String,Object> template=new HashMap<String, Object>();
		metadata=new HashMap<String, Object>();
		metadata.put("name", prop.getProperty("fabric8.replicationController.name"));
		Map<String, Object> appInfoLabel=new HashMap<String, Object>();
		appInfoLabel.put("app", applicationName);
		metadata.put("labels", appInfoLabel);
		template.put("metadata", metadata);

		List<Map<String, Object>> containerData=new ArrayList<Map<String,Object>>();
		Map<String, Object> containerInfo= new HashMap<String, Object>();
		containerInfo.put("name", prop.getProperty("fabric8.container.name"));

		containerInfo.put("image", propsDocker.getProperty("docker.image"));

		containerInfo.put("imagePullPolicy", "Always");
		List<Map<String, Object>> envList=new ArrayList<Map<String, Object>>();


		Set<Object> envKeys = prop.keySet();
		List<String> envVars=new ArrayList<String>();

		if(envKeys!=null){
			for(Object key: envKeys){
				String keyVal= key.toString();
				if(keyVal!=null && keyVal.startsWith("fabric8.env"))
					envVars.add(keyVal);
			}


		}
		if(envVars!=null){
			for(int e=0;e< envVars.size(); e++){
				Map<String , Object> env = new HashMap<String, Object>();	
				String varName= envVars.get(e);
				if(("fabric8.env.BW_LOGLEVEL").equalsIgnoreCase(varName)){
					env.put("name", "BW_LOGLEVEL");
					env.put("value", prop.getProperty("fabric8.env.BW_LOGLEVEL"));

				}
				else if(("fabric8.env.APP_CONFIG_PROFILE").equalsIgnoreCase(varName)){
					env = new HashMap<String, Object>();
					env.put("name", "BW_PROFILE");
					env.put("value", prop.getProperty("fabric8.env.APP_CONFIG_PROFILE"));
				}

				else{
					String envName= varName.replace("fabric8.env.", "");
					env.put("name", envName);
					env.put("value",prop.getProperty(varName));
				}
				envList.add(env);
			}
		}
		Map<String , Object> envLogger = new HashMap<String, Object>();	
		if(prop.getProperty("fabric8.env.BW_LOGLEVEL")==null || prop.getProperty("fabric8.env.BW_LOGLEVEL").isEmpty()){
			envLogger.put("name", "BW_LOGLEVEL");
			envLogger.put("value", "ERROR");
			envList.add(envLogger);
		}


		containerInfo.put("env", envList);
		portsList=new ArrayList<Map<String, Object>>();
		portInfo=new HashMap<String, Object>();
		portInfo.put("containerPort", Integer.parseInt(prop.getProperty("fabric8.service.containerPort")));
		portsList.add(portInfo);
		containerInfo.put("ports", portsList);

		containerData.add(containerInfo);
		Map<String, Object> specdata1=new HashMap<String, Object>();
		specdata1.put("containers", containerData);
		template.put("spec", specdata1);

		specdata.put("template",template);



		data.put("spec", specdata);

		options = new DumperOptions(); 
		options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK); 
		yaml = new Yaml(options); 
		try {
			writer = new FileWriter(locationDeployment);
		} catch (IOException e) {
			throw new MojoExecutionException("Could not write to file deployment.yml due to exception: "+e);
		}
		yaml.dump(data, writer);
		
	}
	
	

}

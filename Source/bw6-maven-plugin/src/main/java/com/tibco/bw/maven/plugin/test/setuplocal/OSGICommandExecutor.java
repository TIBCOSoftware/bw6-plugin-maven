package com.tibco.bw.maven.plugin.test.setuplocal;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.media.multipart.MultiPartFeature;

import com.tibco.bw.maven.plugin.test.helpers.BWTestConfig;

public class OSGICommandExecutor {
	
	private static final String CONTEXT_ROOT = "/bw/framework.json/osgi";
	private Client jerseyClient;
	private WebTarget r;
	private String scheme = "http";
	private final String host = "localhost";
	
	private void init() 
	{
		if (this.jerseyClient == null) 
		{
			ClientConfig clientConfig = new ClientConfig();
			clientConfig.register(JacksonFeature.class).register(MultiPartFeature.class);
			this.jerseyClient = ClientBuilder.newClient(clientConfig);
		}
		this.r = this.jerseyClient.target(UriBuilder.fromPath(CONTEXT_ROOT).scheme(this.scheme).host(this.host).port(BWTestExecutor.INSTANCE.getEngineDebugPort()).build());
	
	}
	
	public void executeCommand(String command){
		init();
		
		try {
			Response resp = this.r.queryParam("command",command).request().get();
			BWTestConfig.INSTANCE.getLogger().info(resp.readEntity(String.class));
		} catch(Exception e){
			BWTestConfig.INSTANCE.getLogger().error(e);
		}
		
	}

}

package com.tibco.bw.maven.packager;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;


public class BWEarInstallerRest extends AbstractMojo
{

	public BWEarInstallerRest() 
	{
		
	}

	public void execute() throws MojoExecutionException, MojoFailureException 
	{
	
		Client client = Client.create();
		 
		WebResource webResource = client.resource("http://localhost:5555/api/browse/domains");
 
		ClientResponse response = webResource.accept("application/json").get(ClientResponse.class);
		if (response.getStatus() != 200) 
		{
			   throw new RuntimeException("Failed : HTTP error code : "
				+ response.getStatus());
		}
	 
		String output = response.getEntity(String.class);
		
		System.out.println( output );
	}
	
	
	
	public static void main( String [] args ) throws Exception
	{
		BWEarInstallerRest rest = new BWEarInstallerRest();
		rest.execute();
	}
	
}

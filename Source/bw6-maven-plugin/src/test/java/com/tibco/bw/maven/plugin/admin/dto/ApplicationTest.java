package com.tibco.bw.maven.plugin.admin.dto;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ApplicationTest {

	@Test
	public void testApplication() {
		ObjectMapper mapper = new ObjectMapper();
		//JSON from file to Object
		try {
			Application obj = mapper.readValue(new File("src/main/resources/Application.json"), Application.class);
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}

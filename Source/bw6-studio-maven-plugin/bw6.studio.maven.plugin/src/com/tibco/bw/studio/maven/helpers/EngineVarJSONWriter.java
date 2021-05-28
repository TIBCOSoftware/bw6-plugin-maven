package com.tibco.bw.studio.maven.helpers;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonWriter;
import javax.json.JsonWriterFactory;
import javax.json.stream.JsonGenerator;

public class EngineVarJSONWriter {
	
	public static void write(String manifestJsonLocation, String engineVarJSONFile) throws IOException {
		
		JsonArray props = getDefaultEngineVars();
		
		//write enginevar.json file
		OutputStream os = new FileOutputStream(engineVarJSONFile);
		Map<String, Object> properties = new HashMap<>(1);
        properties.put(JsonGenerator.PRETTY_PRINTING, true);
        
		JsonWriterFactory writerFactory = Json.createWriterFactory(properties);
		JsonWriter jsonWriter = writerFactory.createWriter(os);
		jsonWriter.writeArray(props);
		jsonWriter.close();
		
	}

	private static JsonArray getDefaultEngineVars() 
	{
		JsonArrayBuilder props = Json.createArrayBuilder();
		
		props.add(Json.createObjectBuilder().add("name", "BW_LOGGER_OVERRIDES").add("description", "Override the default values for different logger properties").add("type", "string").add("value", "ROOT=WARN"));
		props.add(Json.createObjectBuilder().add("name", "BW_ENGINE_THREADCOUNT").add("description", "Override the default value for bw.engine.threadcount property").add("type", "integer").add("value", "8"));
		props.add(Json.createObjectBuilder().add("name", "BW_ENGINE_STEPCOUNT").add("description", "Override the default value for bw.engine.stepcount property").add("type", "integer").add("value", "-1"));
		props.add(Json.createObjectBuilder().add("name", "BW_INSTRUMENTATION_ENABLED").add("description", "Override the default value for bw.frwk.event.subscriber.instrumentation.enabled property").add("type", "boolean").add("value", "true"));
		props.add(Json.createObjectBuilder().add("name", "CUSTOM_ENGINE_PROPERTY").add("description", "Set different java properties").add("type", "string").add("value", "na"));
		props.add(Json.createObjectBuilder().add("name", "BW_APP_CPU_ALERT_THRESHOLD").add("description", "The threshold of CPU %usage by the App to issue alert warning log").add("type", "number").add("value", "70"));
		props.add(Json.createObjectBuilder().add("name", "BW_APP_MEM_ALERT_THRESHOLD").add("description", "The threshold of memory %usage by the App to issue alert warning log").add("type", "number").add("value", "70"));
		
		return props.build();
	}

}

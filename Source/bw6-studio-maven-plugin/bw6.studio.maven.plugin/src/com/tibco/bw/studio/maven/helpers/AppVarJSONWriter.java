package com.tibco.bw.studio.maven.helpers;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.json.JsonValue;
import javax.json.JsonWriter;
import javax.json.JsonWriterFactory;
import javax.json.stream.JsonGenerator;

public class AppVarJSONWriter {

	public static void write(String manifestJsonLocation, String appVarJSONFile) throws IOException {
		//parse manifest.json file
		JsonReader jsonReader = Json.createReader(new FileInputStream(manifestJsonLocation));
		JsonObject jsonObject = jsonReader.readObject();
		JsonArray appProps = jsonObject.getJsonArray("properties");
		
		JsonArrayBuilder props = Json.createArrayBuilder();
		//transform to appvar format
		for (JsonValue prop : appProps) {
			JsonObject obj = (JsonObject)prop;
			JsonObjectBuilder propObj = Json.createObjectBuilder();
			propObj.add("name", obj.getString("name"));
			propObj.add("description", "");
			propObj.add("type", obj.getString("datatype"));
			propObj.add("value", obj.getString("default"));
			props.add(propObj);
		}
		
		//write appvar.json file
		
		OutputStream os = new FileOutputStream(appVarJSONFile);
		Map<String, Object> properties = new HashMap<>(1);
        properties.put(JsonGenerator.PRETTY_PRINTING, true);
        
		JsonWriterFactory writerFactory = Json.createWriterFactory(properties);
		JsonWriter jsonWriter = writerFactory.createWriter(os);
		jsonWriter.writeArray(props.build());
		jsonWriter.close();
	}

}

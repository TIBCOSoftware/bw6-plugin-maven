/*Copyright � 2018. TIBCO Software Inc. All Rights Reserved.*/

package com.tibco.bw.maven.plugin.testsuite;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Scanner;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;

public class YMLBWTSFileReader {

	
	public static BWTSModel getModelFrom(Path filePath) throws JsonParseException, JsonMappingException, IOException{
		YAMLMapper mapper = new YAMLMapper();
		String contents = contentsOf(filePath);
		BWTSModel bwtsModel = mapper.readValue(contents, BWTSModel.class);	
		return bwtsModel;
	}
	
	public static BWTSModel getModelFrom(InputStream inputStream) throws JsonParseException, JsonMappingException, IOException{
		String contents = getContentsFrom(inputStream);
		YAMLMapper mapper = new YAMLMapper();
		BWTSModel bwtsModel = mapper.readValue(contents, BWTSModel.class);
		return bwtsModel;
	}
	
	private static String contentsOf(Path filePath) throws IOException{
		File file = filePath.toFile();
		InputStream inputStream = new FileInputStream(file);
		String contents = getContentsFrom(inputStream);
		inputStream.close();
		
		return contents;
	}
	
	

	private static String getContentsFrom(InputStream inputStream) {
		Scanner scanner = new Scanner(inputStream);
		Scanner s = scanner.useDelimiter("\\A"); //$NON-NLS-1$
		String contents = s.hasNext() ? s.next() : ""; //$NON-NLS-1$
		
		s.close();
		scanner.close();
		return contents;
	}
}

/*Copyright � 2018. TIBCO Software Inc. All Rights Reserved.*/

package com.tibco.bw.maven.plugin.testsuite;

import java.util.LinkedHashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class BWTSModel {
	
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	private Map<String,Object> others;

	public BWTSModel() {
		others= new LinkedHashMap<String, Object>();
	}
	
	@JsonAnyGetter
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	public Map<String, Object> getOthers() {
		return others;
	}
	@JsonAnySetter
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	public void setOthers(String name, Object value) {
		others.put(name,value);
	}
}

package com.tibco.bw.maven.plugin.test.dto;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlEnumValue;
import jakarta.xml.bind.annotation.XmlType;

@XmlType
@XmlEnum(String.class)
public enum ConditionLanguageDTO {
	@XmlEnumValue("JSCRIPT")
	JSCRIPT, 
	@XmlEnumValue("XPATH")
	XPATH,
	@XmlEnumValue("XSLT10")
	XSLT10,
	@XmlEnumValue("XSLT20")
	XSLT20;
}

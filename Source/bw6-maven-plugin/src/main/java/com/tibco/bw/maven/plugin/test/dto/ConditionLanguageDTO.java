package com.tibco.bw.maven.plugin.test.dto;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

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

/*
 * Copyright(c) 2014 TIBCO Software Inc.
 * All rights reserved.
 *
 * This software is confidential and proprietary information of TIBCO Software Inc.
 *
 */

package com.tibco.bw.maven.plugin.admin.dto;

import java.util.Collection;
import java.util.Collections;

import javax.xml.bind.annotation.XmlElement;

/**
 * @author <a href="mailto:tdiekman@tibco.com">Tim Diekmann</a>
 *
 * @since 1.0.0
 */
public class Property {
	private String name;
	private Object value;
	private String description;
	private String type;
	private boolean scalable;
	private boolean editable;
	private String visibility;
	private String regex;
	private Object defaultValue;
	private Collection<String> tags;

	@SuppressWarnings("unchecked")
	public Property() {
		this.tags = Collections.EMPTY_LIST;
	}

	/**
	 * @return the name
	 */
	@XmlElement
	public String getName() {
		return this.name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(final String name) {
		this.name = name;
	}

	/**
	 * @return the value
	 */
	@XmlElement
	public Object getValue() {
		return this.value;
	}

	/**
	 * @param value
	 *            the value to set
	 */
	public void setValue(final Object value) {
		this.value = value;
	}

	/**
	 * @return the description
	 */
	@XmlElement
	public String getDescription() {
		return this.description;
	}

	/**
	 * @param description
	 *            the description to set
	 */
	public void setDescription(final String description) {
		this.description = description;
	}

	/**
	 * @return the type
	 */
	@XmlElement
	public String getType() {
		return this.type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(final String type) {
		this.type = type;
	}

	/**
	 * @return the scalable
	 */
	@XmlElement
	public boolean isScalable() {
		return this.scalable;
	}

	/**
	 * @param scalable
	 *            the scalable to set
	 */
	public void setScalable(final boolean scalable) {
		this.scalable = scalable;
	}

	/**
	 * @return the editable
	 */
	@XmlElement
	public boolean isEditable() {
		return this.editable;
	}

	/**
	 * @param editable
	 *            the editable to set
	 */
	public void setEditable(final boolean editable) {
		this.editable = editable;
	}

	/**
	 * @return the visibility
	 */
	@XmlElement
	public String getVisibility() {
		return this.visibility;
	}

	/**
	 * @param visibility
	 *            the visibility to set
	 */
	public void setVisibility(final String visibility) {
		this.visibility = visibility;
	}

	/**
	 * @return the defaultValue
	 */
	@XmlElement
	public Object getDefaultValue() {
		return this.defaultValue;
	}

	/**
	 * @param defaultValue
	 *            the defaultValue to set
	 */
	public void setDefaultValue(final Object defaultValue) {
		this.defaultValue = defaultValue;
	}

	/**
	 * @return the tags
	 */
	@XmlElement
	public Collection<String> getTags() {
		return this.tags;
	}

	/**
	 * @param tags
	 *            the tags to set
	 */
	public void setTags(final Collection<String> tags) {
		this.tags = tags;
	}

	/**
	 * Regular expression to use for validating input
	 *
	 * @return the regex
	 */
	@XmlElement
	public String getRegex() {
		return this.regex;
	}

	/**
	 * @param regex
	 *            the regex to set
	 */
	public void setRegex(final String regex) {
		this.regex = regex;
	}
}

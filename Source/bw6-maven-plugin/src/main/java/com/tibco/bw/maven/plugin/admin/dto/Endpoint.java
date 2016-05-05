/*
 * Copyright(c) 2014 TIBCO Software Inc.
 * All rights reserved.
 *
 * This software is confidential and proprietary information of TIBCO Software Inc.
 *
 */

package com.tibco.bw.maven.plugin.admin.dto;

import java.util.Map;

import javax.xml.bind.annotation.XmlElement;

/**
 * @author <a href="mailto:tdiekman@tibco.com">Tim Diekmann</a>
 *
 * @since 1.0.0
 */

public class Endpoint {
    private String              name;
    private String              url;
    private String              reverseProxyUrl;
    private Map<String, String> properties;
    private String              type;

    public Endpoint() {

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
     * @return the url
     */
    @XmlElement
    public String getUrl() {
        return this.url;
    }

    /**
     * @param url
     *            the url to set
     */
    public void setUrl(final String url) {
        this.url = url;
    }

    /**
     * @return the properties
     */
    @XmlElement
    public Map<String, String> getProperties() {
        return this.properties;
    }

    /**
     * @param properties
     *            the properties to set
     */
    public void setProperties(final Map<String, String> properties) {
        this.properties = properties;
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

    @XmlElement
    public String getReverseProxyUrl() {
        return this.reverseProxyUrl;
    }

    public void setReverseProxyUrl(final String reverseProxyUrl) {
        this.reverseProxyUrl = reverseProxyUrl;
    }
}

/*
 * Copyright(c) 2014 TIBCO Software Inc.
 * All rights reserved.
 *
 * This software is confidential and proprietary information of TIBCO Software Inc.
 *
 */

package com.tibco.bw.maven.plugin.admin.dto;

import javax.xml.bind.annotation.XmlElement;


/**
 * @author <a href="mailto:tdiekman@tibco.com">Tim Diekmann</a>
 *
 * @since 1.0.0
 */
public class ServiceBinding {
    private String name;
    @Deprecated
    private String uri;
    private String transportType;
    private String endpointURI;
    private String componentName;
    private String serviceName;
    private String resource;
    private String path;
    private String docBasePath;
    private String attachmentStyle;
    private String soapVersion;

    public ServiceBinding() {

    }

    /**
     * @return the path
     * @since 6.2.0
     */
    @XmlElement
    public String getPath() {
        return this.path;
    }

    /**
     * @param path
     *            the path to set
     */
    public void setPath(final String path) {
        this.path = path;
    }

    /**
     * @return the docBasePath
     * @since 6.2.0
     */
    @XmlElement
    public String getDocBasePath() {
        return this.docBasePath;
    }

    /**
     * @param docBasePath
     *            the docBasePath to set
     */
    public void setDocBasePath(final String docBasePath) {
        this.docBasePath = docBasePath;
    }

    /**
     * @return the componentName
     * @since 6.2.0
     */
    @XmlElement
    public String getComponentName() {
        return this.componentName;
    }

    /**
     * @param componentName
     *            the componentName to set
     */
    public void setComponentName(final String componentName) {
        this.componentName = componentName;
    }

    /**
     * @return the serviceName
     * @since 6.2.0
     */
    @XmlElement
    public String getServiceName() {
        return this.serviceName;
    }

    /**
     * @param serviceName
     *            the serviceName to set
     */
    public void setServiceName(final String serviceName) {
        this.serviceName = serviceName;
    }

    /**
     * @return the resource
     * @since 6.2.0
     */
    @XmlElement
    public String getResource() {
        return this.resource;
    }

    /**
     * @param resource
     *            the resource to set
     */
    public void setResource(final String resource) {
        this.resource = resource;
    }

    /**
     * @return the transportType
     */
    @XmlElement
    public String getTransportType() {
        return this.transportType;
    }

    /**
     * @param transportType
     *            the transportType to set
     */
    public void setTransportType(final String transportType) {
        this.transportType = transportType;
    }

    /**
     * @return the endpointURI
     */
    @XmlElement
    public String getEndpointURI() {
        return this.endpointURI;
    }

    /**
     * @param endpointURI
     *            the endpointURI to set
     */
    public void setEndpointURI(final String endpointURI) {
        this.endpointURI = endpointURI;
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
     * @return the uri
     */
    @Deprecated
    @XmlElement
    public String getUri() {
        return this.uri;
    }

    /**
     * @param uri
     *            the uri to set
     */
    @Deprecated
    public void setUri(final String uri) {
        this.uri = uri;
    }

    /**
     * @return the attachmentStyle
     * @since 6.2.0
     */
    @XmlElement
    public String getAttachmentStyle() {
        return this.attachmentStyle;
    }

    /**
     * @param attachmentStyle
     *            the attachmentStyle to set
     */
    public void setAttachmentStyle(final String attachmentStyle) {
        this.attachmentStyle = attachmentStyle;
    }

    /**
     * @return the soapVersion
     * @since 6.2.0
     */
    @XmlElement
    public String getSoapVersion() {
        return this.soapVersion;
    }

    /**
     * @param soapVersion
     *            the soapVersion to set
     */
    public void setSoapVersion(final String soapVersion) {
        this.soapVersion = soapVersion;
    }

}

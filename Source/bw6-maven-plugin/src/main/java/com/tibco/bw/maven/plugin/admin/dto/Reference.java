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
public class Reference {
    private String name;
    private String           wsdlInterface;
    private String             moduleName;
    private ReferenceBinding binding;

    /**
     * @return the wsdlInterface
     */
    @XmlElement
    public String getWsdlInterface() {
        return this.wsdlInterface;
    }

    /**
     * @param wsdlInterface
     *            the wsdlInterface to set
     */
    public void setWsdlInterface(final String wsdlInterface) {
        this.wsdlInterface = wsdlInterface;
    }

    /**
     *
     */
    public Reference() {
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
     * @return the binding
     */
    @XmlElement
    public ReferenceBinding getBinding() {
        return this.binding;
    }

    /**
     * @param binding
     *            the binding to set
     */
    public void setBinding(final ReferenceBinding binding) {
        this.binding = binding;
    }

    /**
     * @return the moduleName
     */
    @XmlElement
    public String getModuleName() {
        return this.moduleName;
    }

    /**
     * @param moduleName
     *            the moduleName to set
     */
    public void setModuleName(final String moduleName) {
        this.moduleName = moduleName;
    }
}

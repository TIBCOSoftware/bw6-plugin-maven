/*
 * Copyright(c) 2014 TIBCO Software Inc.
 * All rights reserved.
 *
 * This software is confidential and proprietary information of TIBCO Software Inc.
 *
 */

package com.tibco.bw.maven.plugin.admin.dto;

import java.util.ArrayList;
import java.util.Collection;

import javax.xml.bind.annotation.XmlElement;

/**
 * Model object for a BW Component
 *
 * @author <a href="mailto:tdiekman@tibco.com">Tim Diekmann</a>
 *
 * @since 1.0.0
 */

public class Component {
    private String             name;
    private String             processName;
    private String             moduleName;
    private Collection<String> services;
    private Collection<String> references;

    /**
     *
     */
    public Component() {
        this.services = new ArrayList<>();
        this.references = new ArrayList<>();
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
     * @return the processName
     */
    @XmlElement
    public String getProcessName() {
        return this.processName;
    }

    /**
     * @param processName
     *            the processName to set
     */
    public void setProcessName(final String processName) {
        this.processName = processName;
    }

    /**
     * @return the services
     */
    @XmlElement
    public Collection<String> getServices() {
        return this.services;
    }

    /**
     * @param services
     *            the services to set
     */
    public void setServices(final Collection<String> services) {
        this.services = services;
    }

    /**
     * @return the references
     */
    @XmlElement
    public Collection<String> getReferences() {
        return this.references;
    }

    /**
     * @param references
     *            the references to set
     */
    public void setReferences(final Collection<String> references) {
        this.references = references;
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

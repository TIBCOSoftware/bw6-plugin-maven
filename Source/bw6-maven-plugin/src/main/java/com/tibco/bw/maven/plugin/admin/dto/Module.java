/*
 * Copyright(c) 2014 TIBCO Software Inc.
 * All rights reserved.
 *
 * This software is confidential and proprietary information of TIBCO Software Inc.
 *
 */

package com.tibco.bw.maven.plugin.admin.dto;

import java.util.Collection;

import javax.xml.bind.annotation.XmlElement;

/**
 * Model class representing a Thor module, e.g. BW Application Module or BW Shared Module, or Adapter Module. Also supported is Bundle
 * Module.
 *
 * @author <a href="mailto:tdiekman@tibco.com">Tim Diekmann</a>
 *
 * @since 1.0.0
 */
public class Module {
    private String             description;
    private String             name;
    private String             version;
    private Collection<String> types;

    public Module() {

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
     * @return the version
     */
    @XmlElement
    public String getVersion() {
        return this.version;
    }

    /**
     * @param version
     *            the version to set
     */
    public void setVersion(final String version) {
        this.version = version;
    }

    /**
     * @return the types
     */
    @XmlElement
    public Collection<String> getTypes() {
        return this.types;
    }

    /**
     * @param types
     *            the types to set
     */
    public void setTypes(final Collection<String> types) {
        this.types = types;
    }
}

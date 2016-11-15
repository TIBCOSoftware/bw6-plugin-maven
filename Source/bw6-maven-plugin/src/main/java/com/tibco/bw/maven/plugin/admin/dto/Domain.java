/*
 * Copyright(c) 2014 TIBCO Software Inc.
 * All rights reserved.
 *
 * This software is confidential and proprietary information of TIBCO Software Inc.
 *
 */

package com.tibco.bw.maven.plugin.admin.dto;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;

/**
 * @author <a href="mailto:tdiekman@tibco.com">Tim Diekmann</a>
 *
 * @since 1.0.0
 */

public class Domain {

    private String                      name;
    private String                      description;
    private String                      date;
    private String                      owner;
    private boolean                  full;
    private List<HRef>         configRefs;
    private List<HRef>         appspaceRefs;
    private List<DomainConfig> configs;
    private List<AppSpace>     appspaces;

    public Domain() {
    }

    /**
     * @return the full
     */
    @XmlElement
    public boolean isFull() {
        return this.full;
    }

    /**
     * @param full
     *            the full to set
     */
    public void setFull(final boolean full) {
        this.full = full;
    }

    /**
     * @return the configRefs
     */
    @XmlElement
    public List<HRef> getConfigRefs() {
        return this.configRefs;
    }

    /**
     * @param configRefs
     *            the configRefs to set
     */
    public void setConfigRefs(final List<HRef> configRefs) {
        this.configRefs = configRefs;
    }

    /**
     * @return the appspaceRefs
     */
    @XmlElement
    public List<HRef> getAppspaceRefs() {
        return this.appspaceRefs;
    }

    /**
     * @param appspaceRefs
     *            the appspaceRefs to set
     */
    public void setAppspaceRefs(final List<HRef> appspaceRefs) {
        this.appspaceRefs = appspaceRefs;
    }

    /**
     * @return the configs
     */
    @XmlElement
    public List<DomainConfig> getConfigs() {
        return this.configs;
    }

    /**
     * @param configs
     *            the configs to set
     */
    public void setConfigs(final List<DomainConfig> configs) {
        this.configs = configs;
    }

    @XmlElement
    public String getName() {
        return this.name;
    }

    @XmlElement
    public String getOwner() {
        return this.owner;
    }

    @XmlElement
    public String getDate() {
        return this.date;
    }

    @XmlElement
    public String getDescription() {
        return this.description;
    }

    /**
     * @param name
     *            the name to set
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * @param description
     *            the description to set
     */
    public void setDescription(final String description) {
        this.description = description;
    }

    /**
     * @param dateTime
     *            and time at which this domain is created the date to set
     */
    public void setDate(final String dateTime) {
        this.date = dateTime;
    }

    /**
     * @param owner
     *            who created this domain the owner to set
     */
    public void setOwner(final String owner) {
        this.owner = owner;
    }


    /**
     * @return all the AppSpaces that are part of this domain
     */
    @XmlElement
    public List<AppSpace> getAppspaces() {
        return this.appspaces;
    }

    /**
     * @param appspaces
     *            the list of AppSpaces to set
     */
    public void setAppspaces(final List<AppSpace> appspaces) {
        this.appspaces = appspaces;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return String.format("com.tibco.bw.maven.plugin.admin.dto.Domain(%s)", this.name);
    }
}

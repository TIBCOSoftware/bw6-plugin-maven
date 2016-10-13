package com.tibco.bw.maven.plugin.admin.dto;

/**
 *
 * Copyright (c) 2014 TIBCO Software Inc.
 * All Rights Reserved.
 */

import java.util.Collection;
import java.util.Collections;

import javax.xml.bind.annotation.XmlElement;
/**
 * DTO of a TIBCO_HOME installation.
 * @author <a href="mailto:rpegalla@tibco.com">Rohit Pegallapati</a>
 *
 *
 */

public class Installation {
    private String                        name;
    private String                        location;
    private Collection<com.tibco.bw.maven.plugin.admin.dto.InstalledSoftware> installedSoftware;

    @SuppressWarnings("unchecked")
	public Installation() {
        this.installedSoftware = Collections.EMPTY_LIST;
    }

    @SuppressWarnings("unchecked")
	public Installation(final String name, final String location, final Collection<InstalledSoftware> software ) {
        this.name = name;
        this.location = location;
        this.installedSoftware = software != null ? software : Collections.EMPTY_LIST;
    }

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
     * @return the location
     */
    @XmlElement
    public String getLocation() {
        return this.location;
    }

    /**
     * @param location
     *            the location to set
     */
    public void setLocation(final String location) {
        this.location = location;
    }

    /**
     * @return the installedSoftware
     */

    @XmlElement
    public Collection<InstalledSoftware> getInstalledSoftware() {
        return this.installedSoftware;
    }

    /**
     * @param installedSoftware
     *            the installedSoftware to set
     */

    @SuppressWarnings("unchecked")
	public void setInstalledSoftware(final Collection<com.tibco.bw.maven.plugin.admin.dto.InstalledSoftware> installedSoftware) {
        this.installedSoftware = installedSoftware != null ? installedSoftware : Collections.EMPTY_LIST;
    }

}
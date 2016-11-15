/*
 * Copyright(c) 2014 TIBCO Software Inc. All rights reserved.
 *
 * This software is confidential and proprietary information of TIBCO Software Inc.
 */

package com.tibco.bw.maven.plugin.admin.dto;

import javax.xml.bind.annotation.XmlElement;

/**
 * @author <a href="mailto:tdiekman@tibco.com">Tim Diekmann</a>
 *
 * @since 1.0.0
 */

public class DomainConfig {
    private String domainName;
    private String agentName;
    private String domainHome;

    public DomainConfig() {

    }

    public DomainConfig(final String name, final String home, final String agent) {
        this.domainName = name;
        this.domainHome = home;
        this.agentName = agent;

    }

    @XmlElement
    public String getDomainName() {
        return this.domainName;
    }

    @XmlElement
    public String getHome() {
        return this.domainHome;
    }

    @XmlElement
    public String getAgentName() {
        return this.agentName;
    }

    /**
     * @param name
     *            the name to set
     */
    public void setDomainName(final String name) {
        this.domainName = name;
    }

    /**
     * @param agentName
     *            the agentName to set
     */
    public void setAgentName(final String agentName) {
        this.agentName = agentName;
    }

    /**
     * @param domainHome
     *            the domainHome to set
     */
    public void setHome(final String domainHome) {
        this.domainHome = domainHome;
    }

}

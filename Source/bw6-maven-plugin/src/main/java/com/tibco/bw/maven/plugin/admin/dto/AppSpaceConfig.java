/*
 * Copyright(c) 2014 TIBCO Software Inc. All rights reserved.
 *
 * This software is confidential and proprietary information of TIBCO Software Inc.
 */

package com.tibco.bw.maven.plugin.admin.dto;

import javax.xml.bind.annotation.XmlElement;

import com.tibco.bw.maven.plugin.admin.dto.AppSpace.AppSpaceRuntimeStatus;

/**
 * @author <a href="mailto:tdiekman@tibco.com">Tim Diekmann</a>
 *
 * @since 1.0.0
 */

public class AppSpaceConfig {

    public enum AppSpaceConfigStatus {
        InSync, OutOfSync
    }

    private String appSpaceName;
    private String agentName;
    private AppSpaceRuntimeStatus status;      // this can be in InSync or OutOfSync depending on the result of deploy command..
    private AppSpaceConfigStatus configStatus; // this can be in InSync or OutOfSync depending on the result of config command..

    public AppSpaceConfig() {
        this.status = AppSpaceRuntimeStatus.InSync;
        this.configStatus = AppSpaceConfigStatus.InSync;
    }

    @XmlElement
    public String getAppSpaceName() {
        return this.appSpaceName;
    }

    @XmlElement
    public String getAgentName() {
        return this.agentName;
    }

    @XmlElement
    public AppSpaceRuntimeStatus getStatus() {
        return this.status;
    }

    @XmlElement
    public AppSpaceConfigStatus getConfigStatus() {
        return this.configStatus;
    }

    /**
     * @param name
     *            the name to set
     */
    public void setAppSpaceName(final String name) {
        this.appSpaceName = name;
    }

    /**
     * @param agentName
     *            the agentName to set
     */
    public void setAgentName(final String agent) {
        this.agentName = agent;
    }

    /**
     * @param status
     *            the deployment status to set
     */
    public void setStatus(final AppSpaceRuntimeStatus status) {
        this.status = status;
    }

    /**
     * @param status
     *            the configuration status to set
     */
    public void setConfigStatus(final AppSpaceConfigStatus status) {
        this.configStatus = status;
    }
}

/*
 * Copyright� 2014 TIBCO Software Inc.
 * All rights reserved.
 *
 * This software is confidential and proprietary information of TIBCO Software Inc.
 *
 */

package com.tibco.bw.maven.plugin.admin.dto;

import javax.xml.bind.annotation.XmlElement;

/**
 * @author <a href="mailto:vnalawad@tibco.com">Vijay Nalawade</a>
 *
 * @since 1.0.0
 */

public class BWEngineInfo {
    private String   engineName;
    private String   engineThreadCount;
    private String   engineStepCount;
    private String   appNodeName;
    private String   appSpaceName;
    private String   domainName;
    private String   debuggerPort;
    private String   debuggerInterface;
    private String   persistenceMode;
    private String   groupName;
    private String   groupProviderTechnology;
    private String   engineState;
    private String[] errors;

    /**
     * @return the engineName
     */
    @XmlElement
    public String getEngineName() {
        return this.engineName;
    }

    /**
     * @param engineName
     *            the engineName to set
     */
    public void setEngineName(final String engineName) {
        this.engineName = engineName;
    }

    /**
     * @return the engineThreadCount
     */
    @XmlElement
    public String getEngineThreadCount() {
        return this.engineThreadCount;
    }

    /**
     * @param engineThreadCount
     *            the engineThreadCount to set
     */
    public void setEngineThreadCount(final String engineThreadCount) {
        this.engineThreadCount = engineThreadCount;
    }

    /**
     * @return the engineStepCount
     */
    @XmlElement
    public String getEngineStepCount() {
        return this.engineStepCount;
    }

    /**
     * @param engineStepCount
     *            the engineStepCount to set
     */
    public void setEngineStepCount(final String engineStepCount) {
        this.engineStepCount = engineStepCount;
    }

    /**
     * @return the appNodeName
     */
    @XmlElement
    public String getAppNodeName() {
        return this.appNodeName;
    }

    /**
     * @param appNodeName
     *            the appNodeName to set
     */
    public void setAppNodeName(final String appNodeName) {
        this.appNodeName = appNodeName;
    }

    /**
     * @return the appSpaceName
     */
    @XmlElement
    public String getAppSpaceName() {
        return this.appSpaceName;
    }

    /**
     * @param appSpaceName
     *            the appSpaceName to set
     */
    public void setAppSpaceName(final String appSpaceName) {
        this.appSpaceName = appSpaceName;
    }

    /**
     * @return the domainName
     */
    @XmlElement
    public String getDomainName() {
        return this.domainName;
    }

    /**
     * @param domainName
     *            the domainName to set
     */
    public void setDomainName(final String domainName) {
        this.domainName = domainName;
    }

    /**
     * @return the debuggerPort
     */
    @XmlElement
    public String getDebuggerPort() {
        return this.debuggerPort;
    }

    /**
     * @param debuggerPort
     *            the debuggerPort to set
     */
    public void setDebuggerPort(final String debuggerPort) {
        this.debuggerPort = debuggerPort;
    }

    /**
     * @return the persistenceMode
     */
    @XmlElement
    public String getPersistenceMode() {
        return this.persistenceMode;
    }

    /**
     * @param persistenceMode
     *            the persistenceMode to set
     */
    public void setPersistenceMode(final String persistenceMode) {
        this.persistenceMode = persistenceMode;
    }

    /**
     * @return the groupName
     */
    @XmlElement
    public String getGroupName() {
        return this.groupName;
    }

    /**
     * @param groupName
     *            the groupName to set
     */
    public void setGroupName(final String groupName) {
        this.groupName = groupName;
    }

    /**
     * @return the groupProviderTechnology
     */
    @XmlElement
    public String getGroupProviderTechnology() {
        return this.groupProviderTechnology;
    }

    /**
     * @param groupProviderTechnology
     *            the groupProviderTechnology to set
     */
    public void setGroupProviderTechnology(final String groupProviderTechnology) {
        this.groupProviderTechnology = groupProviderTechnology;
    }

    /**
     * @return the engineState
     */
    @XmlElement
    public String getEngineState() {
        return this.engineState;
    }

    /**
     * @param engineState
     *            the engineState to set
     */
    public void setEngineState(final String engineState) {
        this.engineState = engineState;
    }

    /**
     * @return the errors
     */
    @XmlElement
    public String[] getErrors() {
        return this.errors;
    }

    /**
     * @param errors
     *            the errors to set
     */
    public void setErrors(final String[] errors) {
        this.errors = errors;
    }

    /**
     * @return the debuggerInterface
     */
    @XmlElement
    public String getDebuggerInterface() {
        return this.debuggerInterface;
    }

    /**
     * @param debuggerInterface the debuggerInterface to set
     */
    public void setDebuggerInterface(final String debuggerInterface) {
        this.debuggerInterface = debuggerInterface;
    }
}

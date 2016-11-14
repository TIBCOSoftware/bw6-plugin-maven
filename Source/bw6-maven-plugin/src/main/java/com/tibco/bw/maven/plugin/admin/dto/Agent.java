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
 * DTO for Agent information.
 *
 * @author <a href="mailto:tdiekman@tibco.com">Tim Diekmann</a>
 *
 * @since 1.0.0
 */
public class Agent {
    public static enum AgentConfigStates {
        InSync, OutOfSync
    }

    public static enum AgentStates {
        Unreachable, Running
    }

    private String              name;
    private String              description;
    private String              httpHost;
    private Integer             httpPort;
    private Integer             internalPort;
    private String              pid;
    private String              adminMode;
    private String              machineName;
    private String              tibcoHome;
    private String              version;
    private AgentConfigStates   configState;
    private long                uptime;
    private AgentStates         state;
    private String              installationName;
    private Map<String, Object> configProps;

    public Agent() {
    }

    @XmlElement
    public String getName() {
        return this.name;
    }

    /**
     * @return the httpPort
     */
    @XmlElement
    public Integer getHttpPort() {
        return this.httpPort;
    }

    /**
     * @param httpPort
     *            the httpPort to set
     */
    public void setHttpPort(final Integer httpPort) {
        this.httpPort = httpPort;
    }

    /**
     * @return the httpHost
     */
    @XmlElement
    public String getHttpHost() {
        return this.httpHost;
    }

    /**
     * @param httpHost
     *            the httpHost to set
     */
    public void setHttpHost(final String httpHost) {
        this.httpHost = httpHost;
    }

    @XmlElement
    /**
     * @return the internalPort
     */
    public Integer getInternalPort() {
        return this.internalPort;
    }

    @XmlElement
    public String getDescription() {
        return this.description;
    }

    /**
     * @return
     */
    @XmlElement
    public String getTibcoHome() {
        return this.tibcoHome;
    }

    /**
     * @return
     */
    @XmlElement
    public String getInstallationName() {
        return this.installationName;
    }

    /**
     * @return
     */
    @XmlElement
    public Map<String, Object> getConfigMap() {
        return this.configProps;
    }

    /**
     * @param tibcoHome
     *            the tibcoHome to set
     */
    public void setTibcoHome(final String tibcoHome) {
        this.tibcoHome = tibcoHome;
    }

    /**
     * @return
     */
    @XmlElement
    public String getMachineName() {
        return this.machineName;
    }

    /**
     * @param machineName
     *            the machineName to set
     */
    public void setMachineName(final String machineName) {
        this.machineName = machineName;
    }

    @XmlElement
    public String getVersion() {
        return this.version;
    }

    /**
     * @param version
     *            the name to set
     */
    public void setVersion(final String version) {
        this.version = version;
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
     * @param internalPort
     *            the internalPort to set
     */
    public void setInternalPort(final Integer internalPort) {
        this.internalPort = internalPort;
    }

    /**
     * @return the pid
     */
    @XmlElement
    public String getPid() {
        return this.pid;
    }

    /**
     * @param pid
     *            the pid to set
     */
    public void setPid(final String pid) {
        this.pid = pid;
    }

    /**
     * @return the adminMode
     */
    @XmlElement
    public String getAdminMode() {
        return this.adminMode;
    }

    /**
     * @param adminMode
     *            the adminMode to set
     */
    public void setAdminMode(final String adminMode) {
        this.adminMode = adminMode;
    }

    /**
     * @return the configState
     */
    @XmlElement
    public AgentConfigStates getConfigState() {
        return this.configState;
    }

    /**
     * @param configState
     *            the configState to set
     */
    public void setConfigState(final AgentConfigStates configState) {
        this.configState = configState;
    }

    /**
     * @return the uptime
     */
    @XmlElement
    public long getUptime() {
        return this.uptime;
    }

    /**
     * @param uptime
     *            the uptime to set
     */
    public void setUptime(final long uptime) {
        this.uptime = uptime;
    }

    /**
     * @return the state
     */
    public AgentStates getState() {
        return this.state;
    }

    /**
     * @param state
     *            the state to set
     */
    public void setState(final AgentStates state) {
        this.state = state;
    }

    /**
     * @param installationName
     *            the installationName to set
     */
    public void setInstallationName(final String installationName) {
        this.installationName = installationName;
    }

    /**
     * @param config
     *            the config to set
     */
    public void setConfigMap(final Map<String, Object> config) {
        this.configProps = config;
    }
}

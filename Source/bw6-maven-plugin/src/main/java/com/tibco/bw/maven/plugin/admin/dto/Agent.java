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

    
    @XmlElement
    public Integer getHttpPort() {
        return this.httpPort;
    }

    
    public void setHttpPort(final Integer httpPort) {
        this.httpPort = httpPort;
    }

    
    @XmlElement
    public String getHttpHost() {
        return this.httpHost;
    }

    
    public void setHttpHost(final String httpHost) {
        this.httpHost = httpHost;
    }

    @XmlElement
   
    public Integer getInternalPort() {
        return this.internalPort;
    }

    @XmlElement
    public String getDescription() {
        return this.description;
    }

    
    @XmlElement
    public String getTibcoHome() {
        return this.tibcoHome;
    }

    
    @XmlElement
    public String getInstallationName() {
        return this.installationName;
    }

    
    @XmlElement
    public Map<String, Object> getConfigMap() {
        return this.configProps;
    }

    
    public void setTibcoHome(final String tibcoHome) {
        this.tibcoHome = tibcoHome;
    }

    
    @XmlElement
    public String getMachineName() {
        return this.machineName;
    }

    
    public void setMachineName(final String machineName) {
        this.machineName = machineName;
    }

    @XmlElement
    public String getVersion() {
        return this.version;
    }

    
    public void setVersion(final String version) {
        this.version = version;
    }

    
    public void setName(final String name) {
        this.name = name;
    }

    
    public void setDescription(final String description) {
        this.description = description;
    }

    
    public void setInternalPort(final Integer internalPort) {
        this.internalPort = internalPort;
    }

    
    @XmlElement
    public String getPid() {
        return this.pid;
    }

    
    public void setPid(final String pid) {
        this.pid = pid;
    }

    
    @XmlElement
    public String getAdminMode() {
        return this.adminMode;
    }

    
    public void setAdminMode(final String adminMode) {
        this.adminMode = adminMode;
    }

    
    @XmlElement
    public AgentConfigStates getConfigState() {
        return this.configState;
    }

    
    public void setConfigState(final AgentConfigStates configState) {
        this.configState = configState;
    }

    
    @XmlElement
    public long getUptime() {
        return this.uptime;
    }

    
    public void setUptime(final long uptime) {
        this.uptime = uptime;
    }

   
    public AgentStates getState() {
        return this.state;
    }

    
    public void setState(final AgentStates state) {
        this.state = state;
    }

    
    public void setInstallationName(final String installationName) {
        this.installationName = installationName;
    }

    
    public void setConfigMap(final Map<String, Object> config) {
        this.configProps = config;
    }
}

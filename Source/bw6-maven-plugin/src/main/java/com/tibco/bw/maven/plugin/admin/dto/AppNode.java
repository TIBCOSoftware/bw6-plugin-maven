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
 * @author <a href="mailto:tdiekman@tibco.com">Tim Diekmann</a>
 *
 * @since 1.0.0
 */

public class AppNode {

    public enum AppNodeRuntimeStates {
        Running, Stopped, Impaired, Starting, Stopping, StartFailed, Unreachable
    }

    public enum AppNodeRuntimeConfigStates {
        InSync, OutOfSync
    }

    private String               name;
    private String               agentName;
    private String               version;
    private String               appSpaceName;
    private String               description;
    private String               httpPort;
    private String               osgiPort;
    private AppNodeRuntimeStates state;
    private String               domainName;
    private Map<String, String>  properties;
    private String               login;
    private String   configState;
    private String   pid;
    private long     uptime;

    public AppNode() {
    }

    /**
     * @return the configState
     */
    @XmlElement
    public String getConfigState() {
        return this.configState;
    }

    /**
     * @param configState
     *            the configState to set
     */
    public void setConfigState(final String configState) {
        this.configState = configState;
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
    @XmlElement
    public AppNodeRuntimeStates getState() {
        return this.state;
    }

    /**
     * @param state
     *            the state to set
     */
    public void setState(final AppNodeRuntimeStates state) {
        this.state = state;
    }

    /**
     * @return the properties
     */
    @XmlElement
    public Map<String, String> getProperties() {
        return this.properties;
    }

    /**
     * @param properties
     *            the properties to set
     */
    public void setProperties(final Map<String, String> properties) {
        this.properties = properties;
    }

    /**
     * @param version
     *            the version to set
     */
    public void setVersion(final String version) {
        this.version = version;
    }

    @XmlElement
    public String getName() {
        return this.name;
    }

    @XmlElement
    public String getHttpPort() {
        return this.httpPort;
    }

    @XmlElement
    public String getOsgiPort() {
        return this.osgiPort;
    }

    @XmlElement
    public String getAgentName() {
        return this.agentName;
    }

    @XmlElement
    public String getVersion() {
        return this.version;
    }

    @XmlElement
    public String getAppSpaceName() {
        return this.appSpaceName;
    }

    @XmlElement
    public String getDescription() {
        return this.description;
    }

    @XmlElement
    public String getDomainName() {
        return this.domainName;
    }

    @XmlElement
    public String getLogin() {
        return this.login;
    }

    public void setLogin(final String login) {
        this.login = login;
    }

    /**
     * @param name
     *            , the name of the AppNode the name to set
     */

    public void setName(final String name) {
        this.name = name;
    }

    /**
     * @param name
     *            , the name of the AppSpace to be used by AppNode the appspace name to set
     */

    public void setAppSpaceName(final String name) {
        this.appSpaceName = name;
    }

    /**
     * @param name
     *            , the osgiPort of this AppNode the osgiPort to set
     */

    public void setOsgiPort(final String port) {
        this.osgiPort = port;
    }

    /**
     * @param name
     *            , the httpPort of this AppNode the httpPort to set
     */

    public void setHttpPort(final String port) {
        this.httpPort = port;
    }

    /**
     * @param name
     *            , the name of the host where this AppNode is to be created the name of the host to set
     */

    public void setAgentName(final String name) {
        this.agentName = name;
    }

    /**
     * @param descr
     *            , the description of the AppNode the descr to set
     */
    public void setDescription(final String descr) {
        this.description = descr;
    }

    /**
     * @param domainName
     *            , the domainName to set
     */
    public void setDomainName(final String domainName) {
        this.domainName = domainName;
    }
}

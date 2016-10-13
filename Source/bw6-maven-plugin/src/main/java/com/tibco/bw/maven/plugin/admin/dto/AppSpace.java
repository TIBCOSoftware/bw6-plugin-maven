/*
 * Copyright(c) 2014 TIBCO Software Inc.
 * All rights reserved.
 *
 * This software is confidential and proprietary information of TIBCO Software Inc.
 *
 */

package com.tibco.bw.maven.plugin.admin.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;

/**
 * DTO representing an AppSpace
 *
 * @author <a href="mailto:tdiekman@tibco.com">Tim Diekmann</a>
 *
 * @since 1.0.0
 */

public class AppSpace {

    public enum AppSpaceRuntimeStatus {
        Stopped, Running, Starting, Stopping, Degraded, Unreachable, InSync, OutOfSync
    }

    private String                      name;
    private String                      description;
    private String                      owner;
    private String                      date;
    private boolean                     elastic;
    private AppSpaceRuntimeStatus status;
    private int                         minNodes;
    private String                      version;
    private String                      domainName;
    private boolean              full;
    private List<HRef>           applicationRefs;
    private List<HRef>           appSpaceConfigRefs;
    private List<HRef>           appNodeRefs;
    private List<Application>    applications;
    private List<AppSpaceConfig>  appSpaceConfigs;
    private List<AppNode>        appnodes;
    private Map<String, Object>   properties;
    private Map<String, String>  traFileProperties;

    public AppSpace() {
        this.applications = new ArrayList<Application>();
        this.appSpaceConfigs = new ArrayList<>();
        this.appnodes = new ArrayList<>();
        this.properties = new HashMap<String, Object>();
        this.traFileProperties = new HashMap<String, String>();
    }

    /**
     * @return the appSpaceConfigs
     */
    @XmlElement
    public List<AppSpaceConfig> getAppSpaceConfigs() {
        return this.appSpaceConfigs;
    }

    /**
     * @param appSpaceConfigs
     *            the appSpaceConfigs to set
     */
    public void setAppSpaceConfigs(final List<AppSpaceConfig> appSpaceConfigs) {
        this.appSpaceConfigs = appSpaceConfigs;
    }

    /**
     * @return the properties
     */
    @XmlElement
    public Map<String, Object> getProperties() {
        return this.properties;
    }

    /**
     * @param properties
     *            the properties to set
     */
    public void setProperties(final Map<String, Object> properties) {
        this.properties = properties;
    }

    /**
     * @return the traFileProperties
     */
    @XmlElement
    public Map<String, String> getTraFileProperties() {
        return this.traFileProperties;
    }

    /**
     * @param traFileProperties
     *            the traFileProperties to set
     */
    public void setTraFileProperties(final Map<String, String> traFileProperties) {
        this.traFileProperties = traFileProperties;
    }

    /**
     * @param runtimeStatus
     *            the runtimeStatus to set
     */
    public void setStatus(final AppSpaceRuntimeStatus runtimeStatus) {
        this.status = runtimeStatus;
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
    public boolean isElastic() {
        return this.elastic;
    }

    @XmlElement
    public String getDescription() {
        return this.description;
    }

    @XmlElement
    public int getMinNodes() {
        return this.minNodes;
    }

    @XmlElement
    public String getVersion() {
        return this.version;
    }

    /**
     * @return the List of AppNodes attached to this AppSpace
     */
    @XmlElement
    public List<AppNode> getAppNodes() {
        return this.appnodes;
    }

    /**
     * @return the List of Applications deployed to this AppSpace
     */
    @XmlElement
    public List<Application> getApplications() {
        return this.applications;
    }

    /**
     * @return the runtimeStatus
     */
    @XmlElement
    public AppSpaceRuntimeStatus getStatus() {
        return this.status;
    }

    /**
     * If true, the lists are populated with the nested data for appnodes and configs. If false, the lists only contain references to the
     * nexted data
     *
     * @return full
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
     * @param appnodes
     *            the appnodes to set
     */
    public void setAppnodes(final List<AppNode> appnodes) {
        this.appnodes = appnodes;
    }

    /**
     * @param name
     *            the name to set
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * @param owner
     *            who created this AppSPace the machineName to set
     */
    public void setOwner(final String owner) {
        this.owner = owner;
    }

    /**
     * @param date
     *            and time when this AppSpace is created the date to set
     */
    public void setDate(final String date) {
        this.date = date;
    }

    /**
     * @param description
     *            the description to set
     */
    public void setDescription(final String description) {
        this.description = description;
    }

    /**
     * @param elastic
     *            the elastic to set
     */
    public void setElastic(final boolean elastic) {
        this.elastic = elastic;
    }

    /**
     * @param minNodes
     *            the minimum number of nodes in the AppSpace to put this AppSpace into Running state to set
     */
    public void setMinNodes(final int nodes) {
        this.minNodes = nodes;
    }

    /**
     * @param version
     *            the version of BW software that this AppSpace is handling, the version must be same across all agents attached to this
     *            AppSpace
     */
    public void setVersion(final String v) {
        this.version = v;
    }

    /**
     * @param configs
     *            , The list of AppSpace configurations , one for each machine that is added to AppSpace the configs to set
     */
    public void setAppspaceConfigs(final List<AppSpaceConfig> configs) {
        this.appSpaceConfigs = configs;
    }

    /**
     * @param nodes
     *            , The list of AppNodes attached to this AppSpace the nodes to set
     */
    public void setAppNodes(final List<AppNode> nodes) {
        this.appnodes = nodes;
    }

    /**
     * @param nodes
     *            , The list of Applications deployed to this AppSpace the nodes to set
     */
    public void setApplications(final List<Application> apps) {
        this.applications = apps;
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
     * @return the applicationRefs
     */
    @XmlElement
    public List<HRef> getApplicationRefs() {
        return this.applicationRefs;
    }

    /**
     * @param applicationRefs
     *            the applicationRefs to set
     */
    public void setApplicationRefs(final List<HRef> applicationRefs) {
        this.applicationRefs = applicationRefs;
    }

    /**
     * @return the configRefs
     */
    @XmlElement
    public List<HRef> getAppSpaceConfigRefs() {
        return this.appSpaceConfigRefs;
    }

    /**
     * @param configRefs
     *            the configRefs to set
     */
    public void setAppSpaceConfigRefs(final List<HRef> configRefs) {
        this.appSpaceConfigRefs = configRefs;
    }

    /**
     * @return the appNodeRefs
     */
    @XmlElement
    public List<HRef> getAppNodeRefs() {
        return this.appNodeRefs;
    }

    /**
     * @param appNodeRefs
     *            the appNodeRefs to set
     */
    public void setAppNodeRefs(final List<HRef> appNodeRefs) {
        this.appNodeRefs = appNodeRefs;
    }
}

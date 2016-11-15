/*
 * Copyright(c) 2014 TIBCO Software Inc.
 * All rights reserved.
 *
 * This software is confidential and proprietary information of TIBCO Software Inc.
 *
 */

package com.tibco.bw.maven.plugin.admin.dto;

import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;

/**
 * @author <a href="mailto:tdiekman@tibco.com">Tim Diekmann</a>
 *
 * @since 1.0.0
 */

public class AppInstance {
    private String         appNodeName;
    private String         state;
    private String         stateDetail;
    private String         profileName;
    private String         deploymentStatus;
    private String         deploymentStatusDetail;
    private String         configState;
    private String         docURL;
    private List<Endpoint> endpoints;
    private List<Property> configuration;
    private HRef           profileContentRef;     // href to get the profile configured for a given appInstance when user supplied one.

    @SuppressWarnings("unchecked")
	public AppInstance() {
        this.endpoints = Collections.EMPTY_LIST;
        this.configuration = Collections.EMPTY_LIST;
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
     * @return the state
     */
    @XmlElement
    public String getState() {
        return this.state;
    }

    /**
     * @param state
     *            the state to set
     */
    public void setState(final String state) {
        this.state = state;
    }

    /**
     * @return the stateDetail
     */
    @XmlElement
    public String getStateDetail() {
        return this.stateDetail;
    }

    /**
     * @param stateDetail
     *            the stateDetail to set
     */
    public void setStateDetail(final String stateDetail) {
        this.stateDetail = stateDetail;
    }

    /**
     * @return the profileName
     */
    @XmlElement
    public String getProfileName() {
        return this.profileName;
    }

    /**
     * @param profileName
     *            the profileName to set
     */
    public void setProfileName(final String profileName) {
        this.profileName = profileName;
    }

    /**
     * @return the profileContent
     */
    @XmlElement
    public HRef getProfileContentRef() {
        return this.profileContentRef;
    }

    /**
     * @param profileContent
     *            the profileContent to set
     */
    public void setProfileContentRef(final HRef profileContent) {
        this.profileContentRef = profileContent;
    }

    /**
     * @return the deploymentStatus
     */
    @XmlElement
    public String getDeploymentStatus() {
        return this.deploymentStatus;
    }

    /**
     * @param deploymentStatus
     *            the deploymentStatus to set
     */
    public void setDeploymentStatus(final String deploymentStatus) {
        this.deploymentStatus = deploymentStatus;
    }

    /**
     * @return the deploymentStatusDetail
     */

    @XmlElement
    public String getDeploymentStatusDetail() {
        return this.deploymentStatusDetail;
    }

    /**
     * @param deploymentStatusDetail
     *            the deploymentStatusDetail to set
     */
    public void setDeploymentStatusDetail(final String deploymentStatusDetail) {
        this.deploymentStatusDetail = deploymentStatusDetail;
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
     * @return the configuration
     */

    @XmlElement
    public List<Property> getConfiguration() {
        return this.configuration;
    }

    /**
     * @param configuration
     *            the configuration to set
     */
    public void setConfiguration(final List<Property> configuration) {
        this.configuration = configuration;
    }

    /**
     * @return the docURL
     */
    @XmlElement
    public String getDocURL() {
        return this.docURL;
    }

    /**
     * @param docURL
     *            the docURL to set
     */
    public void setDocURL(final String docURL) {
        this.docURL = docURL;
    }

    /**
     * @return the endpoints
     */
    @XmlElement
    public List<Endpoint> getEndpoints() {
        return this.endpoints;
    }

    /**
     * @param endpoints
     *            the endpoints to set
     */
    public void setEndpoints(final List<Endpoint> endpoints) {
        this.endpoints = endpoints;
    }
}

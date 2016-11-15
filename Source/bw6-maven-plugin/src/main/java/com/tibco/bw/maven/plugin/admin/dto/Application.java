/*
 * Copyright(c) 2014 TIBCO Software Inc.
 * All rights reserved.
 *
 * This software is confidential and proprietary information of TIBCO Software Inc.
 *
 */

package com.tibco.bw.maven.plugin.admin.dto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;

/**
 * DTO for Application
 *
 * @author <a href="mailto:tdiekman@tibco.com">Tim Diekmann</a>
 *
 * @since 1.0.0
 */

public class Application {
    public enum ApplicationDeploymentStates {
        Deployed, DeployFailed, UndeployFailed, OutOfSync
    }


    public enum ApplicationRuntimeStates {
        Running, Stopped, Impaired, FlowControlled, Paused, StartFailed, AppError, DeployFailed, Unreachable, Deploying, Stopping, Degraded
    }


    public enum ApplicationConfigurationStates {
        InSync, OutOfSync
    }

    private String                      name;
    private String                      appSpaceName;
    private String                      domainName;
    private String                      version;
    private String                      description;
    private HRef                        archiveRef;
    private List<Property>              configuration;
    private int                         revision;
    private ApplicationRuntimeStates    state;                 // this is the aggregate runtime status of the application
    private String                      archiveName;
    private String                      archivePath;
    private String                      profileName;
    private String                      docURL;
    private ApplicationDeploymentStates deploymentStatus;
    private List<String>                deploymentStatusDetail;
    private List<AppInstance>           instances;
    private HRef                        profileContentRef;  // href to get the profile configured for a given application
    private HRef                        href; // href to the application to be used to get/undeploy operations and as a baseurl for start/stop actions
    private Collection<Component>       components;
    private Collection<Process>         processes;
	private String 						message;
	private String 						code;

    public Application() {
        this.instances = new ArrayList<AppInstance>();
        this.deploymentStatusDetail = new ArrayList<String>();
        this.components = new ArrayList<>();
        this.processes = new ArrayList<>();
    }

    /**
     * @return the name
     */
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
     * @return the version
     */
    @XmlElement
    public String getVersion() {
        return this.version;
    }

    /**
     * @param version
     *            the version to set
     */
    public void setVersion(final String version) {
        this.version = version;
    }

    /**
     * @return the description
     */
    @XmlElement
    public String getDescription() {
        return this.description;
    }

    /**
     * @param description
     *            the description to set
     */
    public void setDescription(final String description) {
        this.description = description;
    }

    /**
     * @return the earFileRef
     */
    @XmlElement
    public HRef getArchiveRef() {
        return this.archiveRef;
    }

    /**
     * @param earFileRef
     *            the earFileRef to set
     */
    public void setArchiveRef(final HRef earFileRef) {
        this.archiveRef = earFileRef;
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
     * @return the revision
     */
    @XmlElement
    public int getRevision() {
        return this.revision;
    }

    /**
     * @param revision
     *            the revision to set
     */
    public void setRevision(final int revision) {
        this.revision = revision;
    }

    /**
     * @return the status
     */
    @XmlElement
    public ApplicationRuntimeStates getState() {
        return this.state;
    }

    /**
     * @param status
     *            the status to set
     */
    public void setState(final ApplicationRuntimeStates state) {
        this.state = state;
    }

    /**
     * @return the archiveName
     */
    @XmlElement
    public String getArchiveName() {
        return this.archiveName;
    }

    /**
     * @param earFileName
     *            the earFileName to set
     */
    public void setArchiveName(final String earFileName) {
        this.archiveName = earFileName;
    }

    /**
     * @return the archivePath
     */
    @XmlElement
    public String getArchivePath() {
        return this.archivePath;
    }

    /**
     * @param earFilePath
     *            the earFilePath to set
     */
    public void setArchivePath(final String earFilePath) {
        this.archivePath = earFilePath;
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
     * @return the deploymentStatus
     */
    @XmlElement
    public ApplicationDeploymentStates getDeploymentStatus() {
        return this.deploymentStatus;
    }

    /**
     * @param deploymentStatus
     *            the deploymentStatus to set
     */
    public void setDeploymentStatus(final ApplicationDeploymentStates deploymentStatus) {
        this.deploymentStatus = deploymentStatus;
    }

    /**
     * @return the deploymentStatusDetail
     */
    @XmlElement
    public List<String> getDeploymentStatusDetail() {
        return this.deploymentStatusDetail;
    }

    /**
     * @param deploymentStatusDetail
     *            the deploymentStatusDetail to set
     */
    public void setDeploymentStatusDetail(final List<String> deploymentStatusDetail) {
        this.deploymentStatusDetail = deploymentStatusDetail;
    }

    /**
     * @return the instances
     */
    @XmlElement
    public List<AppInstance> getInstances() {
        return this.instances;
    }

    /**
     * @param instances
     *            the instances to set
     */
    public void setAppInstances(final List<AppInstance> instances)
    {
        this.instances = instances;
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
     * @return the url to the application
     */
    @XmlElement
    public HRef getRef() {
        return this.href;
    }

    /**
     * @param href
     *            the href to set
     */
    public void setRef(final HRef href) {
        this.href = href;
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
     * @return the components
     */
    @XmlElement
    public Collection<Component> getComponents() {
        return this.components;
    }

    /**
     * @param components
     *            the components to set
     */
    public void setComponents(final Collection<Component> components) {
        this.components = components;
    }

    /**
     * @return the processes
     */
    @XmlElement
    public Collection<Process> getProcesses() {
        return this.processes;
    }

    /**
     * @param processes
     *            the processes to set
     */
    public void setProcesses(final Collection<Process> processes) {
        this.processes = processes;
    }

    /**
     * @return the message
     */
    @XmlElement
    public String getMessage() {
		return message;
	}

    /**
     * 
     * @param message
     * 			the message to set
     */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * 
	 * @return the cpde
	 */
	@XmlElement
	public String getCode() {
		return code;
	}

	/**
	 * 
	 * @param code
	 * 			the code to set
	 */
	public void setCode(String code) {
		this.code = code;
	}

}

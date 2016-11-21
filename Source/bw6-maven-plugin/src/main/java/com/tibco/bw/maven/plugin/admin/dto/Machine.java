package com.tibco.bw.maven.plugin.admin.dto;

import java.util.Collection;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;

/**
 *
 * Copyright (c) 2014 TIBCO Software Inc. All Rights Reserved.
 *
 * @author <a href="mailto:rpegalla@tibco.com">Rohit Pegallapati</a>
 *
 *
 */

public class Machine {

	private String name;
	// private String description;
	private String ipAddress;
	private MachineStatus status;
	private List<AppNode> appNodes;
	private List<Agent> agents;
	private List<HRef> installationRefs;
	private List<HRef> agentRefs;
	private List<HRef> appNodeRefs;
	private List<HRef> appSpaceRefs;
	private List<HRef> domainRefs;
	private List<HRef> archiveRefs;
	private String os;
	private String osPatchLevel;
	// private boolean full;

	public Machine() {
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
	 * @param ipAddress
	 *            the ipAddress to set
	 */
	public void setIpAddress(final String ipAddress) {
		this.ipAddress = ipAddress;
	}

	/**
	 * @return the ipAddress
	 */
	@XmlElement
	public String getIpAddress() {
		return this.ipAddress;
	}

	/**
	 * @return the installations
	 */

	/*
	 * @XmlElement public Collection<Installation> getInstallations() { return
	 * this.installations; }
	 * 
	 * 
	 * /**
	 * 
	 * @param installations the installations to set
	 */

	/*
	 * public void setInstallations(final Collection<Installation>
	 * installations) { this.installations = installations != null ?
	 * installations : Collections.EMPTY_LIST; }
	 * 
	 * public void addInstallation(final Installation installation) {
	 * this.installations.add(installation); }
	 */

	/**
	 *
	 * @param installationRefs
	 */
	public void setInstallationRefs(final List<HRef> installationRefs) {
		this.installationRefs = installationRefs;
	}

	@XmlElement
	public void setDomainRefs(final List<HRef> domainRefs) {
		this.domainRefs = domainRefs;
	}

	public void setAppSpaceRefs(final List<HRef> appSpaceRefs) {
		this.appSpaceRefs = appSpaceRefs;
	}

	@XmlElement
	public void setArchiveRefs(final List<HRef> archiveRefs) {
		this.archiveRefs = archiveRefs;
	}

	@XmlElement
	public List<HRef> getDomainRefs() {
		return this.domainRefs;
	}

	@XmlElement
	public List<HRef> getAppSpaceRefs() {
		return this.appSpaceRefs;
	}

	@XmlElement
	public List<HRef> getArchiveRefs() {
		return this.archiveRefs;
	}

	@XmlElement
	public List<HRef> getInstallationRefs() {
		return this.installationRefs;
	}

	@XmlElement
	public void setAgentRefs(final List<HRef> agentRefs) {
		this.agentRefs = agentRefs;
	}

	@XmlElement
	public List<HRef> getAgentRefs() {
		return this.agentRefs;
	}

	@XmlElement
	public void setAppNodeRefs(final List<HRef> appNodeRefs) {
		this.appNodeRefs = appNodeRefs;
	}

	@XmlElement
	public List<HRef> getAppNodeRefs(final List<HRef> appNodeRefs) {
		return this.appNodeRefs;
	}

	/**
	 * @return the agents
	 */
	@XmlElement
	public Collection<Agent> getAgents() {
		return this.agents;
	}

	/**
	 * @param agents
	 *            the agents to set
	 */
	public void setAgents(final List<Agent> agents) {
		this.agents = agents;
	}

	/**
	 * @return the appNodes on this machine
	 */
	@XmlElement
	public Collection<AppNode> getAppNodes() {
		return this.appNodes;
	}

	/**
	 * @param appNodes
	 *            the appNodes to set
	 */
	public void setAppNodes(final List<AppNode> appNodes) {
		this.appNodes = appNodes;
	}

	/**
	 * @return the status
	 */
	@XmlElement
	public MachineStatus getStatus() {
		return this.status;
	}

	/**
	 * @param status
	 *            the status to set
	 */
	public void setStatus(final MachineStatus status) {
		if (status != null) {
			this.status = status;
		}
	}

	/**
	 * @return the OS patch level of this machine
	 */
	@XmlElement
	public String getOSPatchLevel() {
		return this.osPatchLevel;
	}

	/**
	 * @param osPatchLevel
	 *            the osPatchLevel to set
	 */
	public void setOSPatchLevel(final String osPatchLevel) {
		if (osPatchLevel != null) {
			this.osPatchLevel = osPatchLevel;
		}
	}

	@XmlElement
	public String getOs() {
		return this.os;
	}

	public void setOs(final String os) {
		this.os = os;
	}

	@XmlElement
	public String getOsPatchLevel() {
		return this.osPatchLevel;
	}

	public void setOsPatchLevel(final String osPatchLevel) {
		this.osPatchLevel = osPatchLevel;
	}

	@XmlElement
	public List<HRef> getAppNodeRefs() {
		return this.appNodeRefs;
	}

	public static enum MachineStatus {
		Unreachable, Running
	};
}

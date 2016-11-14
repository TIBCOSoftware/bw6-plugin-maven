/*
 * Copyright� 2014 TIBCO Software Inc.
 * All rights reserved.
 *
 * This software is confidential and proprietary information of TIBCO Software Inc.
 *
 */

package com.tibco.bw.maven.plugin.admin.dto;

import java.util.Collection;
import java.util.Collections;

import javax.xml.bind.annotation.XmlElement;

/**
 * @author <a href="mailto:rduntulu@tibco.com">Raju Duntuluri</a>
 *
 * @since 1.0.0
 */
public class Archive {
	public enum EarFileEntryType {
		Folder, File
	};

	private String domainName;
	private String name;
	private String path;
	private String parent;
	private String type;
	private String applicationName;
	private String applicationVersion;
	private String version;
	private String description;
	// private String uploadedBy;
	private String geUserUploaded;
	private String uploadedOn;
	private String category; // apps or shared
	private long size; // size of the ear file in kb
	private Collection<Module> modules;
	private Collection<String> profileNames;
	private Collection<Component> components;
	private Collection<Process> processes;
	private Collection<Service> services;
	private Collection<Reference> references;
	private HRef archives; // href to get all the archives in a given folder, this is set when the entry type is a folder.

	public Archive() {
		this.modules = Collections.emptyList();
		this.profileNames = Collections.emptyList();
		this.components = Collections.emptyList();
		this.processes = Collections.emptyList();
		this.services = Collections.emptyList();
		this.references = Collections.emptyList();
	}

	@XmlElement
	public String getDomainName() {
		return this.domainName;
	}

	@XmlElement
	public String getName() {
		return this.name;
	}

	@XmlElement
	public String getPath() {
		return this.path;
	}

	@XmlElement
	public String getParent() {
		return this.parent;
	}

	@XmlElement
	public String getType() {
		return this.type;
	}

	@XmlElement
	public String getVersion() {
		return this.version;
	}

	@XmlElement
	public String getAppVersion() {
		return this.applicationVersion;
	}

	@XmlElement
	public String getDescription() {
		return this.description;
	}

	@XmlElement
	public String getAppName() {
		return this.applicationName;
	}

	@XmlElement
	public String getUploadedTime() {
		return this.uploadedOn;
	}

	@XmlElement
	public String geUserUploaded() {
		return this.geUserUploaded;
	}

	@XmlElement
	public String getCategory() {
		return this.category;
	}

	@XmlElement
	public long getSize() {
		return this.size;
	}

	@XmlElement
	public Collection<Module> getModules() {
		return this.modules;
	}

	@XmlElement
	public Collection<String> getProfileNames() {
		return this.profileNames;
	}

	@XmlElement
	public HRef getArchives() {
		return this.archives;
	}

	/**
	 * @return the components
	 */
	@XmlElement
	public Collection<Component> getComponents() {
		return this.components;
	}

	/**
	 * @return the processes
	 */
	@XmlElement
	public Collection<Process> getProcesses() {
		return this.processes;
	}

	/**
	 * @return the services
	 */
	@XmlElement
	public Collection<Service> getServices() {
		return this.services;
	}

	/**
	 * @return the references
	 */
	@XmlElement
	public Collection<Reference> getReferences() {
		return this.references;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(final String name) {
		this.name = name;
	}

	/**
	 * @param path
	 *            the relative path to set
	 */
	public void setPath(final String path) {
		this.path = path;
	}

	/**
	 * @param type
	 *            the entry type FOLDER or FILE to set
	 */
	public void setType(final String type) {
		this.type = type;
	}

	/**
	 * @param domain
	 *            the domain to set
	 */
	public void setDomainName(final String domain) {
		this.domainName = domain;
	}

	/**
	 * @param paparentth
	 *            the parent folder to set
	 */
	public void setParent(final String parent) {
		this.parent = parent;
	}

	/**
	 * @param version
	 *            the version to set
	 */
	public void setVersion(final String version) {
		this.version = version;
	}

	/**
	 * @param version
	 *            the version to set
	 */
	public void setAppVersion(final String version) {
		this.applicationVersion = version;
	}

	/**
	 * @param descr
	 *            the description to set
	 */
	public void setDescription(final String descr) {
		this.description = descr;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setAppName(final String name) {
		this.applicationName = name;
	}

	/**
	 * @param time
	 *            the time to set
	 */
	public void setUploadedTime(final String time) {
		this.uploadedOn = time;
	}

	/**
	 * @param user
	 *            the user to set
	 */
	public void setUserUploaded(final String user) {
		this.geUserUploaded = user;
	}

	/**
	 * @param category
	 *            the category to set
	 */
	public void setCategory(final String category) {
		this.category = category;
	}

	/**
	 * @param size
	 *            the size to set
	 */
	public void setSize(final long size) {
		this.size = size;
	}

	/**
	 * @param modules
	 *            the modules to set
	 */
	public void setModules(final Collection<Module> modules) {
		this.modules = modules;
	}

	/**
	 * @param profileNames
	 *            the profileNames to set
	 */
	public void setProfileNames(final Collection<String> profiles) {
		this.profileNames = profiles;
	}

	/**
	 * @param category
	 *            the category to set
	 */
	public void setArchivesRef(final HRef ref) {
		this.archives = ref;
	}

	/**
	 * @param components
	 *            the components to set
	 */
	public void setComponents(final Collection<Component> components) {
		this.components = components;
	}

	/**
	 * @param processes
	 *            the processes to set
	 */
	public void setProcesses(final Collection<Process> processes) {
		this.processes = processes;
	}

	/**
	 * @param services
	 *            the services to set
	 */
	public void setServices(final Collection<Service> services) {
		this.services = services;
	}

	/**
	 * @param references
	 *            the references to set
	 */
	public void setReferences(final Collection<Reference> references) {
		this.references = references;
	}
}

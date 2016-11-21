/*
 * Copyright(c) 2014 TIBCO Software Inc.
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
 * Model for a BW process
 *
 * @author <a href="mailto:tdiekman@tibco.com">Tim Diekmann</a>
 *
 * @since 1.0.0
 */
public class Process {
    private String name;
    private String source;
    private String                  moduleName;
    private byte[]                diagram;
    private Collection<Reference> references;
    private byte[] diagramConfig;

    /**
     *
     */
    public Process() {
        this.references = Collections.emptyList();
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
     * @return the process definition (content of .bwp file) as a String
     */
    @XmlElement
    public String getSource() {
        return this.source;
    }

    /**
     * @param source
     *            the source to set
     */
    public void setSource(final String source) {
        this.source = source;
    }

    /**
     * @return the references
     */
    @XmlElement
    public Collection<Reference> getReferences() {
        return this.references;
    }

    /**
     * @param references
     *            the references to set
     */
    public void setReferences(final Collection<Reference> references) {
        this.references = references;
    }

    /**
     * @return the moduleName
     */
    @XmlElement
    public String getModuleName() {
        return this.moduleName;
    }

    /**
     * @param moduleName
     *            the moduleName to set
     */
    public void setModuleName(final String moduleName) {
        this.moduleName = moduleName;
    }

    /**
     * @return the diagram
     */
    @XmlElement
    public byte[] getDiagram() {
        return this.diagram;
    }

    /**
     * @param diagram
     *            the diagram to set
     */
    public void setDiagram(final byte[] diagram) {
        this.diagram = diagram;
    }

    /**
     * @return the diagramConfig
     */
    @XmlElement
	public byte[] getDiagramConfig() {
		return diagramConfig;
	}

    /**
     * @param diagramConfig
     *            the diagramConfig to set
     */
	public void setDiagramConfig(byte[] diagramConfig) {
		this.diagramConfig = diagramConfig;
	}
    
}

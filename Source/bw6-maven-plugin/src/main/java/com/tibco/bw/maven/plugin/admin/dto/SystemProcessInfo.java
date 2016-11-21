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
 * DTO for Agent System Information
 *
 * @author <a href="mailto:vnalawad@tibco.com">Vijay Nalawade</a>
 *
 * @since 1.0.0
 */
public class SystemProcessInfo {

    private long   systemProcessId;
    private long   activeThreadCount;
    private long   totalMemoryInBytes;
    private long   freeMemoryInBytes;
    private long   usedMemoryInBytes;
    private double percentMemoryUsed;
    private double percentCpuUsed;
    private long   upSince;

    public SystemProcessInfo() {

    }

    @XmlElement
    public long getSystemProcessId() {
        return this.systemProcessId;
    }

    public void setSystemProcessId(final long systemProcessId) {
        this.systemProcessId = systemProcessId;
    }

    @XmlElement
    public long getActiveThreadCount() {
        return this.activeThreadCount;
    }

    public void setActiveThreadCount(final long activeThreadCount) {
        this.activeThreadCount = activeThreadCount;
    }

    @XmlElement
    public long getTotalMemoryInBytes() {
        return this.totalMemoryInBytes;
    }

    public void setTotalMemoryInBytes(final long totalMemoryInBytes) {
        this.totalMemoryInBytes = totalMemoryInBytes;
    }

    @XmlElement
    public long getFreeMemoryInBytes() {
        return this.freeMemoryInBytes;
    }

    public void setFreeMemoryInBytes(final long freeMemoryInBytes) {
        this.freeMemoryInBytes = freeMemoryInBytes;
    }

    @XmlElement
    public long getUsedMemoryInBytes() {
        return this.usedMemoryInBytes;
    }

    public void setUsedMemoryInBytes(final long usedMemoryInBytes) {
        this.usedMemoryInBytes = usedMemoryInBytes;
    }

    @XmlElement
    public double getPercentMemoryUsed() {
        return this.percentMemoryUsed;
    }

    public void setPercentMemoryUsed(final double percentMemoryUsed) {
        this.percentMemoryUsed = percentMemoryUsed;
    }

    @XmlElement
    public double getPercentCpuUsed() {
        return this.percentCpuUsed;
    }

    public void setPercentCpuUsed(final double percentCpuUsed) {
        this.percentCpuUsed = percentCpuUsed;
    }

    @XmlElement
    public long getUpSince() {
        return this.upSince;
    }

    public void setUpSince(final long upSince) {
        this.upSince = upSince;
    }

}

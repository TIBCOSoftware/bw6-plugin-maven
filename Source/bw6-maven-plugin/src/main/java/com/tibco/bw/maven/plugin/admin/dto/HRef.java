/*
 * Copyright(c) 2014 TIBCO Software Inc.
 * All rights reserved.
 *
 * This software is confidential and proprietary information of TIBCO Software Inc.
 *
 */

package com.tibco.bw.maven.plugin.admin.dto;

import javax.xml.bind.annotation.XmlElement;

/**
 * @author <a href="mailto:tdiekman@tibco.com">Tim Diekmann</a>
 *
 * @since 1.0.0
 */

public class HRef {
    private String href;

    public HRef() {
    }

    public HRef(final String href) {
        this.href = href;
    }

    @XmlElement(name = "href")
    public String getHref() {
        return this.href;
    }

    public void setHref(final String href) {
        this.href = href;
    }
}

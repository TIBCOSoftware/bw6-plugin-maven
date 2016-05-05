package com.tibco.bw.maven.plugin.admin.dto;

import java.util.Objects;

import javax.xml.bind.annotation.XmlElement;

/**
 * @author rduntulu
 *
 */
public class InstalledSoftware {

    private String productDisplayName;
    private String version;           // major/minor (x.y)
    private String build;
    private String productHome;
    //private String hofixVersion;
    private String producHFVersion;
    private String productType;
    private String spVersion;
    private String description;
    private String machineName;
    private String tibcoHome;
    private String bundle;
    private String extensionName;

    public InstalledSoftware(final String name, final String version, final String build, final String home, final String hfVersion,
            final String type, final String spVersion, final String machineName, final String tibcoHome, final String extensionName)
    {
        Objects.requireNonNull(type, "productType is a mandatory field");
        Objects.requireNonNull(machineName, "machineName is a mandatory field");
        Objects.requireNonNull(version, "version is a mandatory field");
        Objects.requireNonNull(tibcoHome, "tibcoHome is a mandatory field");

        this.productDisplayName = name;
        this.version = version;
        this.build = build;
        this.productHome = home;
        //this.hofixVersion = hfVersion;
        this.producHFVersion = hfVersion;
        this.productType = type;
        this.spVersion = spVersion;
        this.bundle = null;
        this.machineName = machineName;
        this.tibcoHome = tibcoHome;
        this.extensionName = extensionName;
    }

    public InstalledSoftware() {
    }

    @XmlElement
    public String getProductName()
    {
        return this.productDisplayName;
    }
    @XmlElement
    public String getProductVersion()
    {
        return this.version;
    }
    @XmlElement
    public String getProductType()
    {
        return this.productType;
    }
    @XmlElement
    public String getProductBuild()
    {
        return this.build;
    }
    @XmlElement
    public String getProductHome()
    {
        return this.productHome;
    }
    @XmlElement
    public String getProducHFVersion()
    {
        return this.producHFVersion;
    }

    public void setProductHFVersion(final String hofixVersion) {
        this.producHFVersion = hofixVersion;
    }
    
    public void setExtensionName(final String extensionName)
    {
    	this.extensionName = extensionName;
    }

    @XmlElement
    public String getExtensionName()
    {
    	return this.extensionName;
    }
    
    @XmlElement
    public String getProductSPVersion()
    {
        return this.spVersion;
    }
    
    public void setProductSPVersion(final String spVersion) {
        this.spVersion = spVersion;
    }

    @XmlElement
    public String getTibcoHome()
    {
        return this.tibcoHome;
    }
 
    public void setTibcoHome(final String home)
    {
        this.tibcoHome = home;
    }
    
    @XmlElement
    public String getMachineName()
    {
        return this.machineName;
    }
 
    public void setMachineName(final String machine)
    {
        this.machineName = machine;
    }
    
    @XmlElement
    public void setDescription(final String descr)
    {
        this.description = descr;
    }

    public String getDescription()
    {
        return this.description;
    }

    @XmlElement
    public String getBundle() {
        return this.bundle;
    }

    public void setBundle(final String bundle) {
        this.bundle = bundle;
    }

    @Override
    public String toString()
    {
        return this.productDisplayName + " " + this.version + "."+ this.spVersion+ " " + this.build + " " + this.productHome;
    }

    public void setProductType(final String product) {
        this.productType = product;
    }

    public void setProductVersion( final String version) {
        this.version = version;
    }

    public void setProductHome(final String home) {
        this.productHome = home;
    }

    public void setProductBuild(final String build) {
        this.build = build;
    }
    
    public void setProductName(final String name) {
        this.productDisplayName = name;
    }
}

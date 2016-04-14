# Plug-in Code for Apache Maven and TIBCO ActiveMatrix BusinessWorks™

This plug-in is subject to the license shared as part of the repository. Kindly review the license before using or downloading this plug-in.

It is provided as a sample plug-in to support use-cases of integrating TIBCO ActiveMatrix BusinessWorks™ 6.3.0 and higher with Apache Maven.

## Prerequisites

1. Java 1.8
2. Maven should be installed on the Machine. M2_HOME should be set. The Maven Executable should be available in the Path (version 3.3.3 was used for development).
This can be confirmed by running the command mvn -version from Terminal/Command Prompt.
3. TIBCO ActiveMatrix BusinessWorks™ 6.3.0 or higher should be installed.

## Installation of this maven plugin

1. Clone to https://github.com/TIBCOSoftware/bw6-plugin-maven

2. Navigate to the Source Folder.

3. run **mvn clean install** which will install bw6-maven-plugin to your local maven repository (you could also deploy this to your Nexus or Artifactory)


### First Steps to use this maven plugin to manage dependencies for  BW projects

Starting from scratch, go into Studio and Build a BW project and check 'Use Java configuration'.. 


This plugin is a Maven plugin only - there is no Studio plugin - 
**BW6 Studio will not be aware of maven, it will treat these as normal faceted projects** 

so you have to run maven externally or through a Maven run configuration in Eclipse..

When you run 'mvn clean install' This plugin will:
- sync the dependencies setup within each BW project pom.xml to the BW project lib folder
- add the lib/*.jar entries to the BW project META-INF/MANIFEST.MF
- propagate the mvn artifact version to the BW project META-INF/MANIFEST.MF and META-INF/TIBCO.xml (for packaging modules)
	- to do so it converts snapshot versions like 1.0.0-SNAPSHOT to 1.0.0.qualifier (OSGI friendly)
	- or, for release versioning keeps 1.0.0 as it is already OSGI friendly

Aside from Studio, this plugin will package BW modules and BW ears and deploy them to the maven repository. It will resolve third party jar and BW module jar depenencies via maven when building these ears for deployment (via bwadmin or TEA) and also generates the necessary META-INF/MANIFEST.mf information..

Create a BW Project pom.xml file manually based on the samples in the Samples folder..
Here is a quick guideline..

#### Shared Modules and Application Modules
Must have a packaging type of 'bwmodule'.

```xml
  <packaging>bwmodule</packaging>
```

#### Packaging Modules (.application modules) 
Must have a packaging type of 'bwear'.

```xml
  <packaging>bwear</packaging>
```

#### All Modules
You must include the plugin dependency as such (in a parent POM or individual BW project POM):.

```xml
  <build>
    <sourceDirectory>src</sourceDirectory>
    <outputDirectory>target/classes</outputDirectory>
    <plugins>
      <plugin>
        <groupId>com.tibco.plugins</groupId>
        <artifactId>bw6-maven-plugin</artifactId>
        <version>1.0.0-TC-SNAPSHOT</version>
        <extensions>true</extensions>
      </plugin>
    </plugins>
  </build>

```


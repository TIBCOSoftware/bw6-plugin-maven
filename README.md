# Plug-in Code for Apache Maven and TIBCO ActiveMatrix BusinessWorks™

This plug-in is subject to the license shared as part of the repository. Kindly review the license before using or downloading this plug-in.

It is provided as a sample plug-in to support use-cases of integrating TIBCO ActiveMatrix BusinessWorks™ 6.3.0 and higher with Apache Maven.

## Prerequisites

1. Java 1.8
2. Maven should be installed on the Machine. M2_HOME should be set. The Maven Executable should be available in the Path (version 3.3.3 was used for development).
This can be confirmed by running the command mvn -version from Terminal/Command Prompt.
3. TIBCO ActiveMatrix BusinessWorks™ 6.3.0 or higher should be installed.

## Installation of this maven plugin

1. Clone from https://github.com/tcosley/bw6-plugin-maven

2. Navigate to the Source Folder.

3. run **mvn clean install** which will install bw6-tc-maven-plugin to your local maven repository (you could also deploy this to your Nexus or Artifactory)


### Enables studio debugging
This plugin is a Maven plugin only - there is no Studio plugin - 
**BW6 Studio will not be aware of maven, it will treat these as normal BW6 projects**.

Basically it puts things where Studio expects them you have to run maven externally or through a Maven run configuration in Eclipse..

When you run 'mvn compile' or more involved goals this plugin will:
- sync the dependencies setup within each BW project pom.xml to the BW project lib folder
- add the lib/*.jar entries to the BW project META-INF/MANIFEST.MF
- propagate the mvn artifact version to the BW project META-INF/MANIFEST.MF and META-INF/TIBCO.xml (for packaging modules)
	- to do so it converts snapshot versions like 1.0.0-SNAPSHOT to 1.0.0.qualifier (OSGI friendly)
	- or, for release versioning keeps 1.0.0 as it is already OSGI friendly

*Unfortunately shared-module dependency resolution via maven does not work with Studio so you still have to manage module dependency and module.application includes via Studio*.

### Ear Packaging for deployment
Running 'mvn package' or other goals further into the maven build lifecycle (install, deploy etc) will get module jars and module.application ears built completely aside from Studio.

The plugin will resolve third party jar and bwmodule (bw-sharedmodule and bw-appmodule classifiers) dependencies via maven when building these artifacts for deployment (via bwadmin or TEA) and also generates the necessary META-INF/MANIFEST.mf and META-INF/TIBCO.xml information..

Create a BW Project pom.xml file(s) manually based on the samples in the Samples folder..

Here is a quick guideline..

#### Shared Modules
Must have a packaging type of 'bwmodule' with classifier 'bw-sharedmodule'.

```xml
  <packaging>bwmodule</packaging>
  <properties>
    <maven.jar.classifier>bw-sharedmodule</maven.jar.classifier>
  </properties>
```
Special property for excluding jars from studio lib - but allowing them to get packaged into deployable ear..
(ran into issues with stax wherein transitive stax dependency pulled in by a java dependency caused things to fail in studio - but was needed standalone..
```xml
  <packaging>bwmodule</packaging>
  <properties>
    <maven.jar.classifier>bw-sharedmodule</maven.jar.classifier>
	<studio.jar.excludelist>stax-api-1.0-2.jar|stax2-api-3.1.4.jar|woodstox-core-asl-4.4.1.jar</studio.jar.excludelist>
  </properties>
```

#### App Modules
Must have a packaging type of 'bwmodule' with classifier 'bw-appmodule'.

```xml
  <packaging>bwmodule</packaging>
  <properties>
    <maven.jar.classifier>bw-appmodule</maven.jar.classifier>
  </properties>
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
        <artifactId>bw6-tc-maven-plugin</artifactId>
        <version>1.1.0-SNAPSHOT</version>
        <extensions>true</extensions>
      </plugin>
    </plugins>
  </build>

```

## Sample BW Projects Setup to use the Plugin

See Samples/bw6.3.1/JWT for an example parent pom with shared-module and application module.


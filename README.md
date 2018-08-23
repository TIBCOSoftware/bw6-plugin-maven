[![Build Status](https://travis-ci.org/TIBCOSoftware/bw6-plugin-maven.svg?branch=master)](https://travis-ci.org/TIBCOSoftware/bw6-plugin-maven)

# Plug-in Code for Apache Maven and TIBCO ActiveMatrix BusinessWorks™ and TIBCO BusinessWorks™ Container Edition

This plug-in is subject to the license shared as part of the repository. Kindly review the license before using or downloading this plug-in.

It is provided as a sample plug-in to support use-cases of integrating TIBCO ActiveMatrix BusinessWorks™ with Apache Maven. It is also provided as a sample plug-in to support use-cases of integrating TIBCO BusinessWorks™ Container Edition 1.0.0 and higher with Apache Maven.


## Prerequisites

1. Maven should be installed on the Machine. M2_HOME should be set. The Maven Executable should be available in the Path.
This can be confirmed by running the command mvn -version from Terminal/Command Prompt.
2. Either of TIBCO ActiveMatrix BusinessWorks™ or TIBCO BusinessWorks™ Container Edition should be installed.

## Installation

a. Goto https://github.com/TIBCOSoftware/bw6-plugin-maven/releases

b. Download TIB_BW_Maven_Plugin_x.y.z.zip

c. Unzip the file in a folder on local drive.

d. Open the Terminal and run install.bat ( for Windows based OS ) or install.sh ( for Unix based OS)

e. The Installer will ask for TibcoHome location. Provide the TibcoHome to the Script. e.g. `/opt/tibco/bw_home`

This will install the Maven Plugin to the TibcoHome. 

## For TIBCO ActiveMatrix BusinessWorks™
Please follow the wiki page at 
[Steps for TIBCO ActiveMatrix BusinessWorks™](https://github.com/TIBCOSoftware/bw6-plugin-maven/wiki/Steps-For-TIBCO-ActiveMatrix-BusinessWorks)


## For TIBCO BusinessWorks™ Container Edition 

Please follow the wiki pages at [Building-applications-for-TIBCO-BusinessWorks-Container-Edition](
https://github.com/TIBCOSoftware/bw6-plugin-maven/wiki/Building-applications-for-TIBCO-BusinessWorks-Container-Edition)

## Building the code
Please follow the wiki page [Building the Maven Plugin](https://github.com/TIBCOSoftware/bw6-plugin-maven/wiki/Build)

## Contributing to the Plug-in

If you'd like to contribute to this plug-in, please reach out to integration-pm@tibco.com


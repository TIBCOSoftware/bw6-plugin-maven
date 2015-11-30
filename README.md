#Plugin Code for Apache Maven and TIBCO ActiveMatrix BusinessWorks™

This plug-in has been provided subject to the license shared as part of the repository. Kindly ensure to review the same in detail before using or downloading this plug-in.

It is provided as a sample Plug-in to support use-cases of integrating TIBCO ActiveMatrix BusinessWorks™ 6.3.0 and higher with Apache Maven.

##Prerequisites

1. Maven should be installed on the Machine. M2_HOME should be set. The Maven Executable should be available in the Path.
This can be confirmed by running the command mvn -version from Terminal/Command Prompt.
2. TIBCO ActiveMatrix BusinessWorks™ 6.3.0 or higher should be installed.

##Installation


a. Goto https://github.com/TIBCOSoftware/bw6-plugin-maven

b. Navigate to the Installer Folder.

c. Download TIB_BW_Maven_Plugin_1.0.0.zip

d. Unzip the file in a folder on local drive.

e. Open the Terminal and run install.bat ( for Windows based OS ) or install.sh ( for Unix based OS)

f. The Installer will ask for TibcoHome location. Provide the TibcoHome to the Script.

This will install the Maven Plugin to the TibcoHome. 

###First Steps

Running the Maven Build from Studio.

a. Open the Studio.

b. Right-Click on the Application project.

c. The Context Menu will show the option "Generate POM for Application"

d. Clicking it will launch the POM Generation UI.

e. Enter the Parent POM details here and Click Finish.

f. The Project will be converted to Maven nature. Note The Workspace will index after generating POM filesfor the first time and may take some time.

g. Open Run/Debug Configurations. Create a new Maven Build.

h. Under the "Base directory" choose add variable for Workspace location. The variable name is ${workspace_loc}

i. In the Goal enter the Goal to be executed. The goal can be any standard Maven goal like package, install, deploy etc.

j. Select the goal as "package" for creating the EAR file. This will create the EAR file in the target folder under the Application project.

k. Select the goal as "install" for creating the EAR file and installing it to local Maven Repository.


##Extending the Maven Plugin

1. The Maven Plugin Source code is available under the Folder "Source" in the Github Repository.

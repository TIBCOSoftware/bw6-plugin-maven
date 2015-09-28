Prerequisites

1. Maven should be installed on the Machine. M2_HOME should be set. The Maven Executable should be available in the Path.
This can be confirmed by running the command mvn -version from Terminal/Command Prompt.

Installation

a. Git clone https://github.com/TIBCOSoftware/bw6-plugin-maven
or
Download the bw6-plugin-maven as Zip File and unzip it to a folder.
 
b. Go to the Installer folder.

c. Open the Terminal and run command ant setup.

d. The Installer will ask for TibcoHome location. Provide the TibcoHome to the Installer.

This will install the Maven Plugin to the TibcoHome. 

First Steps

Running the Maven Build from Studio.

a. Open the Studio.

b. Right-Click on the Application project.

c. The Context Menu will show the option "Generate POM"

d. Clicking it will launch the POM Generation UI.

e. Enter the details here and Click Finish.

f. The Project will be converted to Maven nature. Note The Workspace will index on clicking for the first time and may take some time.

g. Open Run/Debug Configurations. Create a new Maven Build.

h. Under the Base directory choose add variable for Workspace location. The variable name is ${workspace_loc}

i. In the Goal enter the Goal to be executed. The goal can be package, install, deploy etc.

j. Select the goal as "package" for creating the EAR file.

k. Select te goal as "install" for creating the EAR file and installing it to local Maven Repository.




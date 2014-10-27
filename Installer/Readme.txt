Prerequisites

1. Maven Maven should be installed on the Machine. M2_HOME should be set. The Maven Executable should be available in the Path.
This can be confirmed by running the command mvn -version from Terminal/Command Prompt.

Installation

a. Go to the <TibcoHome>/bw/6.2/bin
b. Open the Terminal and run command bwinstall bw6-plugin-maven

This will install the Maven Plugin to the TibcoHome. 

First Steps

a. Open the Studio.

b. Right-Click on the Application project.

c. The Context Menu will show the option "Generate POM"

d. Clicking it will launch the POM Generation UI.

e. Enter the details here and Click Finish.

f. The Project will be converted to Maven nature. Note The Workspace will index on clicking for the first time and may take some time.

f. Right click on the Application Project, Go to Run options and Click "mvn build"

g. Select the goal as "package" for creating the EAR file.

h. Select te goal as "install" for creating the EAR file and installing it to the bwadmin.


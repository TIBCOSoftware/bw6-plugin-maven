Prerequisites
=============
1. Maven must be installed on the Machine. Configure M2_HOME environment variable and update
   PATH environment variable to have access to Maven executables. 
   
   This can be confirmed by running the command mvn -version from Terminal/Command Prompt.

2. Your machine needs to have Internet access.  During the installation, the install script connects to the Maven Repository.

Installation
============
1. From a Terminal command window, cd to <TIBCO_HOME>/bw/6.2/bin

2. Run command 

    bwinstall bw6-plugin-maven

This will install the Maven Plugin to your TIBCO_HOME. 


Using Maven BW6 Plugin from BusinessStudio
==========================================

1. Open the Studio.

2. Right-Click on the Application project.

3. The Context Menu will show the option "Generate POM"

4. Clicking it will launch the POM Generation UI.

5. Enter the details here and Click Finish.

6. The Project will be converted to Maven nature. Note The Workspace will index on clicking for the first time and may take some time.

7. Right click on the Application Project, Go to Run options and Click "mvn build"

8. Select the goal as "package" for creating the EAR file.

9. Select te goal as "install" for creating the EAR file and installing it to the bwadmin.


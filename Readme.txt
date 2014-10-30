Prerequisites
=============
1. Maven must be installed on the Machine. Configure M2_HOME environment variable and update
   PATH environment variable to have access to Maven executables. 
   
   You can verify this by running the command mvn -version from a terminal/command prompt.

2. Your machine must have Internet access.  During installation, the install script connects to the Maven Repository.

Installation
============
1. From a Terminal command window, cd to <TIBCO_HOME>/bw/6.2/bin

2. Run the following command to install the Maven plugin to your TIBCO_HOME:

    bwinstall bw6-plugin-maven
   
3. To verify that the Maven plugin is installed, check the your Maven repository. The Maven Repository is typically located at:

    <UserHome>\.m2\repository\com\tibco\bw\bw-archiver\1.0.0    

Using Maven BW6 Plugin from TIBCO Business Studio
=================================================

1. Open TIBCO Business Studio.

2. In the Project Explorer, right-click the application project and select 'Generate POM' from the context menu.
   The POM Generation UI is launched.
   
3. Enter the details on the UI and click 'Finish'. 
   During the install lifecycle the BW Maven Plugin will also install the EAR to the BWAdmin. 

   Note: Please refer to BW Administration guide for more information on the Admin deployment.

4. The project is converted to Maven nature. 
   Note: The Workspace is indexed when you convert the project to Maven nature for the first time, and this may take some time.

5. Right-click on the application project amd select Run > mvn build. 

6. Type the goal as "package" to create an EAR file.

7. Type the goal as "install" to create an EAR file and install it to the bwadmin specified in Step 3.

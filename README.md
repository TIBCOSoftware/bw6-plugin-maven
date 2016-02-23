#Plug-in Code for Apache Maven and TIBCO ActiveMatrix BusinessWorks™

This plug-in is subject to the license shared as part of the repository. Kindly review the license before using or downloading this plug-in.

It is provided as a sample plug-in to support use-cases of integrating TIBCO ActiveMatrix BusinessWorks™ 6.3.0 and higher with Apache Maven.

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

c. The Context Menu will show the option "Generate POM for Application".

d. Clicking it will launch the POM Generation UI.

e. Enter the Parent POM details here and Click Finish.

f. The Project will be converted to Maven (Eclipse Project) nature. Note the workspace will index after generating POM files for the first time and may take some time. You can continue with the steps below by allowing this indexing to run in the background.

g. Open Run/Debug Configurations. Create a new Maven Build.

h. Under the "Base directory" choose add variable for Workspace location. The variable name is ${workspace_loc}.

i. In the Goal enter the Goal to be executed. The goal can be any standard Maven goal like package, install, deploy etc.

j. Select the goal as "package" for creating the EAR file. This will create the EAR file in the target folder under the Application project - 'Refresh' your project using the right-click menu if this folder is not visible.

k. Select the goal as "install" for creating the EAR file and installing it to local Maven Repository.

##Contributing to the Plug-in

If you'd like to contribute to this plug-in, please reach out to integration-pm@tibco.com



***

***


#Plug-in Code for Apache Maven and TIBCO BusinessWorks™ Container Edition

This plug-in is subject to the license shared as part of the repository. Kindly review the license before using or downloading this plug-in.

It is provided as a sample plug-in to support use-cases of integrating TIBCO BusinessWorks™ Container Edition 1.0.0 and higher with Apache Maven.

##Prerequisites

1. Maven should be installed on the Machine. M2_HOME should be set. The Maven Executable should be available in the Path.
This can be confirmed by running the command mvn -version from Terminal/Command Prompt.
2. TIBCO BusinessWorks™ Container Edition 1.0.0 or higher should be installed.

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

c. The Context Menu will show the option "Generate POM for Application".

d. Clicking it will launch the POM Generation UI.

e. Enter your PCF instance configuration details here and Click Finish.

        1. PCF Target = https://api.run.pivotal.io
        
        2. PCF Server Name = PCF_UK_credential (Define this as <\server> inside your config/settings.xml of Apache Maven Home)        
                <server>
                        <id>PCF_UK_credential</id>
                        <username>admin</username>
                        <password>xxxxxxxxxxxx</password>
                </server> 
                
       3. PCF Org 
       4. PCF Space
       5. Number of App Instance 
       6. App Memory (Minimum should be 1024 MB)
       7. App Buildpack (BWCE buildpack which developer as pushed to PCF instance)
       8. Select Services (Button where you login to PCF and select required services you want to bind to your app)

f. The Project will be converted to Maven (Eclipse Project) nature. Note the workspace will index after generating POM files for the first time and may take some time. You can continue with the steps below by allowing this indexing to run in the background.

 - You will find at your workspace two properties files are created with the name - pcfdev.properties and pcfprod.properties. These properties files signifies two PCF instances ie. for PCF Dev and PCF Production, if you have more PCF environments you can manually just create copies of one of these properties file and rename it to your another pcf environment (eg. pcfqa.properties). By default, both pcfdev and pcfprod properties are same, and contains values which you have specified in step (e), so you can manually edit these values in properties file for your PCF environment (prod / dev).  

g. Open Run/Debug Configurations. Create a new Maven Build.

h. Under the "Base directory" choose add variable for pom location. 

 - Choose ${workspace_loc} for goals - package, cf:push, cf:delete, cf:scale, cf:start, cf:restart, cf:stop, cf:apps, cf:services (use 'initialize' before every cf goals eg. 'initialize cf:push', see step i,k)
 - Choose ${project_loc} for goals - cf:login , cf:logout (use 'initialize' before these goals eg. 'initialize cf:login'), we need to fire these goals from '.application' project of your workspace.

NOTE: Whenever you execute maven goals from terminal, you should point to your workspace. 

 - Incase authentication token expired, you can execute logout and login goals. From CI server you can call cf:login / cf:logout job only if cf:push job gives token expired error. See example below for login -
 Root POM : xxxxx.application/pom.xml (login / logout should be fired from '.application' project)
 Goal : initialize cf:login -Dproperty.file=../pcfprod.properties
 - 'Package' goal is standard Maven goal, which is independent of cf-maven-plugin and can be executed to create application 'EAR' 
 - Since all goals are fired from your workspace location (having parent/root pom.xml), so, make sure before executing any such goals like cf:push, you are aware about which ".application" project is getting pushed on PCF.  

i. In the 'Goal' enter the goal to be executed. The goal can be any standard PCF commands like cf:push, cf:start, cf:delete etc. for more goals you can view this link -
http://docs.run.pivotal.io/buildpacks/java/build-tool-int.html

 - Please make sure to prefix 'initialize' before all your cf goals. So in goal you should enter - 'initialize cf:push'
 - Also make sure to add parameter - 'property.file' as name and the value as path to your pcf instance properties file ie. '../pcfprod.properties'. If you don't specify any parameter then, by default it will pick from pcfdev.properties.

j. Goal "cf:push" will also create EAR file in the target folder under the Application project, along with pushing the same EAR on PCF - 'Refresh' your project using the right-click menu if this folder is not visible. 

k. You can do cf:push from your maven terminal or from CI Server(Jenkins etc), using maven commands like -
mvn initialize cf:push -Dproperty.file=../pcfprod.properties (you can specify any other pcf instance properties file as value to parameter property.file)

l. You can try other goals from studio, by creating new Maven Run Configurations for different goals , or from terminal pointing to your workspace using 'mvn initialize cf:<command> -Dproperty.file=../<pcfinstance>.properties'

NOTE: For all non-web application if you are using PCF Elastic Runtime 1.6 or above (Diego) then, it will give health-check error while cf:push, so you have to use PCF CLI (6.13 or above) to set health-check as 'none' after pushing your application and re-push after setting health-check as 'none'.  You can use below command on CLI -
cf set-health-check App_Name none 

##Contributing to the Plug-in

If you'd like to contribute to this plug-in, please reach out to integration-pm@tibco.com


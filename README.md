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

c. Download TIB_BW_Maven_Plugin_1.1.0.zip

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

c. Download TIB_BW_Maven_Plugin_1.0.0.zip (for BWCE 1.0.1 having on PCF) or TIB_BW_Maven_Plugin_1.1.0.zip (for BWCE 2.0 having both Docker and PCF)

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

e. There are two platforms supported by this plugin -
- CloudFoundry (follow step f)
- Docker + Kubernetes (follow step g)
NOTE - For Docker/Kubernetes you also need to download gCloud-sdk and kubectl (CLIs), and make sure you add them to your path environment-variable. 

f. Enter your PCF instance configuration details here and Click Finish.

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
       8. Env Var (add environment variabled as key=value with comma seperated) 
       9. Select Services (Button where you login to PCF and select required services you want to bind to your app)

g. Enter Docker + Kubernetes details 

       1. Docker Host 
       2. Docker Cert Path
       3. Image name (You can provide public/private repo as part of the image name like - gcr.io/<project_id>/<image-name>) 
       4. BWCE image (BWCE Runtime base image)
       5. Maintainer (your name/email)
       6. checkbox- if you want to run this image on Docker host. If yes, you will see Docker run configurations. 
       7. checkbox for Kubernetes (if checked step8 onwards)
       8. RC Name (Replication controller name of kubernetes)
       9. No. of replicas (how many pods/instances on kubernetes)
       10. Service name (by default we are exposing service on LoadBalancer)
       11. Container port
       12. Namespace (Kubernetes namespace)
       13. Env Variables (key=value comma seperated)

h. The Project will be converted to Maven (Eclipse Project) nature. Note the workspace will index after generating POM files for the first time and may take some time. You can continue with the steps below by allowing this indexing to run in the background.
 - You will find parent project will get created in your workspace with a pom file.
 - You will find in your application project properties files are getting created for dev and prod environments, but, if you have more environments you can manually just create copies of one of these properties file and rename it to your another environment (eg. pcfqa.properties, docker-qa.properties, k8s-qa.properties etc.). By default, both dev and prod properties are same, and contains values which you have specified from studio pop-up, so you can manually edit these values in properties file for your environment (prod / dev).  
 
Property files:
 - PCF - pcfdev.properties and pcfprod.properties and variable name as pcf.property.file
 - Docker - docker-dev.properties and docker-prod.properties and variable name as docker.property.file
 - K8S - k8s-dev.properties and k8s-prod.properties and variable name as k8s.property.file

i. Open Run/Debug Configurations. Create a new Maven Build.

j. Under the "Base directory" choose add variable for pom location choose ${project_loc} or browse parent project as workspace. 

k. In the 'Goal' enter the goal to be executed. 

- Make sure you add initialize before all maven goals.
- 'package' goal is standard Maven goal, which is independent of cf-maven-plugin and can be executed to create application 'EAR'. 
- Below are platform specific goals:

CloudFoundry - cf:push , cf:scale etc
http://docs.run.pivotal.io/buildpacks/java/build-tool-int.html

Docker and Kubernetes - 

 - clean package initialize docker:build 
 - initialize docker:start
 - initialize docker:push (before push make sure you generate token and authorize your docker host for GCP repo, follow step)
 - initialize fabric8:json 
 - initialize fabric8:apply

http://fabric8io.github.io/docker-maven-plugin/index.html
http://fabric8.io/guide/mavenPlugin.html

Docker authorize for GCP docker repo before trying docker:push

--- Windows ---
gcloud auth print-access-token
docker login -u _token -p "your token" https://gcr.io

--- Linux/OSX ----
docker login -u _token -p "$(gcloud auth print-access-token)" https://gcr.io


l. You can try other goals from studio, by creating new Maven Run Configurations for different goals , or from terminal pointing to your workspace using 'mvn initialize cf:command -Dpcf.property.file=pcfdev.properties'

NOTE: For all non-web application if you are using PCF Elastic Runtime 1.6 or above (Diego) then, it will give health-check error while cf:push, so you have to use PCF CLI (6.13 or above) to set health-check as 'none' after pushing your application and re-push after setting health-check as 'none'.  You can use below command on CLI -
cf set-health-check App_Name none 

##Contributing to the Plug-in

If you'd like to contribute to this plug-in, please reach out to integration-pm@tibco.com


# Sample Shared Module with Java dependencies, and an Application Module that uses it..

## Steps taken to build this sample

### Create JWT folder
Create parent pom.xml here..

Add SampleSharedModule, SampleApp and SampleApp.application to the modules list..

### Create JWT/SampleSharedModule
Create a new BW Shared module, check 'Use Java configuration'
- SampleSharedModule
- check 'Use Java configuration'

Uncheck 'Use default location' Specify the JWT folder.

Create JWT/SampleSharedModule/pom.xml
- reference parent pom
- include property maven.jar.classifier=bw-sharedmodule
- and add in JWT dependencies


### Create JWT/SampleApp
Create a new BW Application Module
- SampleApp
- check 'Use Java configuration'
Uncheck 'Use default location' Specify the JWT folder.

Create JWT/SampleApp/pom.xml
- include property maven.jar.classifier=bw-appmodule
- add in dependency for SampleSharedModule
Create JWT/SampleApp.application/pom.xml
- add in dependency for SampleApp
- reference parent pom in both


### Run mvn
mvn clean install

This will get the jars referenced by SampleSharedModule/pom.xml pulled into that module's lib folder (I .gitignore lib).
It will also:
- update the Bundle-Version, Bundle-Classpath and Provide-Capabilityin SampleSharedModule/META-INF/MANIFEST.MF
- update the Bundle-Version, Require-Capability and Requires-Bundle in SampleApp META-INF/MANIFEST.MF
- update the Bundle-Version in SampleApp.application META-INF/MANIFEST.MF
- update the modules/module(symbolicName, technologyType and technologyVersion) for the associated application SampleApp.application META-INF/MANIFEST.MF


### Refresh from studio
Select all three modules in studio, right-click and refresh (or F5)


### Update SampleApp and SampleApp.application to include SampleSharedModule from within Studio
The bw6-tc-maven-plugin does manage bw-sharedmodule and bw-appmodule dependencies via mvn..
It will update MANIFEST.MF and TIBCO.xml files throughout so that Studio has what it needs.

Studio is not maven-aware, however.. It relies on workspace resolution; so all referenced projects must be open in Studio.


### Now you're ready to build stuff..
Now you can build Sub-Processes that perform 'Java Invoke' activities that make use of the 3rd party jars in the Shared Module, 
and invoke these sub-processes in dependent application modules.


## To run this sample
Import and Open SampleSharedApp, SampleApp and SampleApp.application in Studio.

Run SampleApp from Studio debugger..
You can use the sample postman collection in this folder to perform a get with a Bearer token..


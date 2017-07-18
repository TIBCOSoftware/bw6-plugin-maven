# Sample Shared Module with Java dependencies, and an Application Module that uses it..

## Steps taken to build this sample

### Create JWT folder
Use Editor: Create parent pom.xml here..

Add SampleSharedModule, SampleApp and SampleApp.application to the modules list..

### Create JWT/SampleSharedModule
From Studio: Create a new BW Shared module, check 'Use Java configuration'
- SampleSharedModule
- check 'Use Java configuration'

Uncheck 'Use default location' Specify the JWT folder.

Use Editor: Create JWT/SampleSharedModule/pom.xml
- reference parent pom
- include packaging=bw-sharedmodule
- and add in JWT dependencies


### Create JWT/SampleApp
From Studio: Create a new BW Application Module
- SampleApp
- check 'Use Java configuration'
Uncheck 'Use default location' Specify the JWT folder.

Use Editor: Create JWT/SampleApp/pom.xml
- include packaging=bw-appmodule
- add in dependency for SampleSharedModule
Use Editor: Create JWT/SampleApp.application/pom.xml
- add in dependency for SampleApp
- reference parent pom in both


### Run mvn
From command line:
``` bash
mvn clean install
```

This will get the jars referenced by SampleSharedModule/pom.xml pulled into that module's lib folder (.gitignore lib).
It will also:
- update the Bundle-Version, Bundle-Classpath and Provide-Capabilityin SampleSharedModule/META-INF/MANIFEST.MF
- update the Bundle-Version, Require-Capability and Requires-Bundle in SampleApp META-INF/MANIFEST.MF
- update the Bundle-Version in SampleApp.application META-INF/MANIFEST.MF
- update the modules/module(symbolicName, technologyType and technologyVersion) for the associated application SampleApp.application META-INF/MANIFEST.MF


### Refresh from studio
Select all three modules in studio, right-click and refresh (or F5)


### Update SampleApp and SampleApp.application to include SampleSharedModule from within Studio
The bw6-maven-plugin does manage bw-sharedmodule, bw-appmodule and osgi-bundle dependencies via mvn..
It will update MANIFEST.MF and TIBCO.xml files throughout so that Studio has what it needs.

Studio is not maven-aware, however.. It relies on workspace resolution; so all referenced projects must be open in Studio.


### Now you're ready to build stuff in Studio..
Now you can build Sub-Processes that perform 'Java Invoke' activities that make use of the 3rd party jars in the Shared Module, 
and invoke these sub-processes in dependent application modules.


## To run this sample (after mvn install from commandline)
Import and Open SampleSharedModule, SampleApp and SampleApp.application in Studio.

Run SampleApp from Studio debugger..
You can use the sample postman collection in this folder to perform a get with a Bearer token..


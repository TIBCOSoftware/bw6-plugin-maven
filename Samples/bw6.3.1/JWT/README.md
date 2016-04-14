# Sample Shared Module with Java dependencies, and an Application Module that uses it..

## Steps

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
- and add in JWT dependencies


### Create JWT/SampleApp
Create a new BW Application Module
- SampleApp
- check 'Use Java configuration'
Uncheck 'Use default location' Specify the JWT folder.

Create JWT/SampleApp/pom.xml
Create JWT/SampleApp.application/pom.xml
- reference parent pom in both


### Run mvn
mvn clean install

This will get the jars referenced by SampleSharedModule/pom.xml pulled into that module's lib folder (I .gitignore lib).
It will also:
- update the Bundle-Classpath and Bundle-Version in SampleSharedModule/META-INF/MANIFEST.MF
- update the Bundle-Version in SampleApp META-INF/MANIFEST.MF
- update the Bundle-Version in SampleApp.application META-INF/MANIFEST.MF
- update the modules/module/technologyVersion for the associated application SampleApp.application META-INF/MANIFEST.MF


### Refresh from studio
Select all three modules in studio, right-click and refresh (or F5)


### Update SampleApp and SampleApp.application to include SampleSharedModule from within Studio
Unfortunately I have not (yet) been able to figure out to manage the dependency on 
shared modules via maven..

So you have to go into SampleApp and reference SampleSharedModule under 'Module Descriptors'->'Dependencies'..
Also SampleApp.application and reference SampleSharedModule under 'Package->Includes'.

### Now you're ready to build stuff..
Now you can build Sub-Processes that perform 'Java Invoke' activities that make use of the 3rd party jars in the Shared Module, 
and invoke these sub-processes in dependent application modules.


## To run this sample
Run SampleApp from Studio debugger..
You can use the sample postman collection in this folder to perform a get with a Bearer token..


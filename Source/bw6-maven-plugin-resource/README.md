# Plug-in Code for Apache Maven and TIBCO ActiveMatrix BusinessWorks™

***
USE
 `mvn com.tibco.plugins:bw6-maven-plugin-resource:help`
 
This plugin has 3 goals:

bw6mavenresource:bwexport
  Goal which bwexport file export properties. 
  
  `mvn com.tibco.plugins:bw6-maven-plugin-resource:bwexport -Dprofile=ProfileNameFile -Dpropertyfile=propertyFile`

bw6mavenresource:bwimport
  Display bwimport information on bw6-maven-plugin-resource. 
  
  Call `mvn com.tibco.plugins:bw6-maven-plugin-resource:bwimport -Dprofile=ProfileNameFile -Dpropertyfile=propertyFile`

bw6mavenresource:help
  Display help information on bw6-maven-plugin-resource.
  Call `mvn bw6mavenresource:help -Ddetail=true -Dgoal=<goal-name>` to display
  parameter details.

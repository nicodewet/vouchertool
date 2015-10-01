= Setup for Eclipse =

First complete the environment config, then the JRebel development cycle testing.

= Environment Config = 

0. install the "Maven Integration for Eclipse" plugin by Sonatype Inc. 
1. svn checkout of trunk
2. right click on project, select Properties, select Project Facets, select Convert to faceted form, Java should be selected, also
   select Dynamic Web Module 3.0
3. right click on the pom.xml file -> Run As... -> Run Configurations... Now create a new configuration, with an appropriate name and 
   the correct base directory, and in Goals... enter eclipse:eclipse
4. execute eclipse:eclipse (configured in 3.), note, after running the above and refreshing your project, you will notice a set of 
   unversioned files and directories that have been created, DON'T commit these to SVN since these are derived artifacts and we want 
   to keep the build portable (i.e. across IDEs and keep local environment changes out of the project).
5. in the Projects menu, make sure Build Automatically has been enabled
6. create two more Run Configurationd as you did in step 3, this time with the first Run Configuration having goal jetty:run and the 
   second having goal jetty:stop

= JRebel Development Cycle Testing = 

0. run the project with jetty:run
1. open your JRebel log file from within eclipse, the very first line should tell you where it is located e.g. C:\Users\Nico\.jrebel\jrebel.log
2. open the web application in your browser http://localhost:8080
3. open a Java file affecting the first page and make a change, be sure to save the file
4. you should see, in both the Console output and in the JRebel log output, evidence that JRebel has picked up the change,  
   e.g. [2013-04-16 17:33:52] JRebel: Reloading class 'com.mayloom.vt.VoucherTool'.
5. close your browser, open it again, go to http://localhost:8080 and see the change affected - note a refresh is not enough   
6. assuming you are done, run jetty:stop

= Jenkins =

/var/lib/jenkins/.m2/settings.xml


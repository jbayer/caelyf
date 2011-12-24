<% if (!params.nowrap) include '/WEB-INF/includes/header.gtpl?title=Setting up your project' %>

<h1>Deploying to Cloud Foundry</h1>

<a name="assemble"></a>
<h2>Assemble the application</h2>

<p>
Use gradle to build the project and make sure it is up-to-date:
</p>

<pre>
jbayer-mbpro:caelyf-template-project-0.1 jbayer> ./gradlew build
:compileJava UP-TO-DATE
:compileGroovy UP-TO-DATE
:processResources UP-TO-DATE
:classes UP-TO-DATE
:jar UP-TO-DATE
:assemble UP-TO-DATE
:compileTestJava UP-TO-DATE
:compileTestGroovy UP-TO-DATE
:processTestResources UP-TO-DATE
:testClasses UP-TO-DATE
:test UP-TO-DATE
:check UP-TO-DATE
:build UP-TO-DATE
</pre>

<a name="vmc"></a>
<h2>Deploy with vmc command line</h2>

<p>
Now use the vmc command line interface to deploy the application.  Note that you need to specify that the application is in the "war" project sub-directory and bind the application to a redis service (provision a new redis instance if you must).  If you do not bind the application to a redis service, the application may fail to start with an exception like: "java.lang.RuntimeException: Impossible to get redis credentials (Cannot invoke method getAt() on null object)".
</p>

<pre>
jbayer-mbpro:caelyf-template-project-0.1 jbayer> vmc push
Would you like to deploy from the current directory? [Yn]: n
Please enter in the deployment path: war
Application Name: jambaycaelyf
Application Deployed URL [jambaycaelyf.cloudfoundry.com]: 
Detected a Java Web Application, is this correct? [Yn]: 
Memory Reservation (64M, 128M, 256M, 512M, 1G) [512M]: 
Creating Application: OK
Would you like to bind any services to 'jambaycaelyf'? [yN]: y
Would you like to use an existing provisioned service? [yN]: n
The following system services are available
1: mongodb
2: mysql
3: postgresql
4: rabbitmq
5: redis
Please select one you wish to provision: 5
Specify the name of the service [redis-be2b2]: 
Creating Service: OK
Binding Service [redis-be2b2]: OK
Uploading Application:
  Checking for available resources: OK
  Processing resources: OK
  Packing application: OK
  Uploading (1K): OK   
Push Status: OK
Staging Application: OK                                                         
Starting Application: OK
</pre>

<p>
Now try accessing the application at the Application Deployed URL from the push command.
</p>


<% if (!params.nowrap) include '/WEB-INF/includes/footer.gtpl' %>
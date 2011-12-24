<% if (!params.nowrap) include '/WEB-INF/includes/header.gtpl?title=Setting up your project' %>

<h1>Setting up your project</h1>

<a name="layout"></a>
<h2>Directory layout</h2>

<p>
We'll follow the directory layout proposed by the <b>Caelyf</b> template project:
</p>

<pre>
/
+-- src
|   |
|   +-- main
|   |   |
|   |   +-- groovy
|   |   +-- java
|   |
|   +-- test
|       |
|       +-- groovy
|       +-- java
|
+-- war
    |
    +-- index.gtpl
    +-- css
    +-- images
    +-- js
    +-- WEB-INF
        |
        +-- web.xml
        +-- plugins.groovy      // if you use plugins
        +-- routes.groovy       // if you use the URL routing system
        +-- classes
        |
        +-- groovy
        |    |
        |    +-- controller.groovy
        |
        +-- pages
        |    |
        |    +-- view.gtpl
        |
        +-- includes
        |    |
        |    +-- footer.gtpl
        |
        +-- lib
            |
            +-- caelyf-x.y.z.jar
            +-- groovy-all-x.y.z.jar
            +-- jedis-x.y.z.jar

</pre>

<p>
At the root of your project, you'll find:
</p>

<ul>
    <li>
        <code>src</code>: If your project needs source files beyond the templates and groovlets,
        you can place both your Java and Groovy sources in that directory.
        Before running on Micro Cloud Foundry or before deploying your application to cloudfoundry.com,
        you should make sure to pre-compile your Groovy and Java classes so they are available in
        <code>WEB-INF/classes</code>.
    </li>
    <li>
        <code>war</code>: This directory will be what's going to be deployed on Cloud Foundry.
        It contains your groovlets, templates, images, JavaScript files, stylesheets, and more.
        It also contains the classical <code>WEB-INF</code> directory from typical Java web applications.
    </li>
</ul>

<p>
In the <code>WEB-INF</code> directory, you'll find:
</p>

<ul>
    <li>
        <code>web.xml</code>: The usual Java EE configuration file for web applications.
    </li>
    <li>
        <code>classes</code>: The compiled classes (compiled with <code>build.groovy</code>) will go in that directory.
    </li>
    <li>
        <code>groovy</code>: In that folder, you'll put your controller and service files written in Groovy in the form of Groovlets.
    </li>
    <li>
        <code>pages</code>: Here you can put all your template views, to which you'll point at from the URL routes configuration.
    </li>
    <li>
        <code>includes</code>: We propose to let you put included templates in that directory.
    </li>
    <li>
        <code>lib</code>: All the needed libraries will be put here, the Groovy, <b>Caelyf</b> and Jedis
        (the Java library to access Redis, for page caching purposes),
        as well as any third-party JARs you may need in your application.
    </li>
</ul>

<blockquote>
<b>Note: </b> You may decide to put the Groovy scripts and includes elsewhere,
but the other files and directories can't be changed,
as they are files the servlet container expects to find at that specific location.
</blockquote>

<a name="configuration"></a>
<h2>Configuration files</h2>

<p>
With the directory layout ready, let's have a closer look at the <code>web.xml</code> file.
</p>

<a name="web-xml"></a>
<h3>web.xml</h3>
<pre class="brush:xml">
    &lt;web-app xmlns="http://java.sun.com/xml/ns/javaee" version="2.5"&gt;
        &lt;!-- A servlet context listener to initialize the plugin system --&gt;
        &lt;listener&gt;
            &lt;listener-class&gt;groovyx.caelyf.ServletContextListener&lt;/listener-class&gt;
        &lt;/listener&gt;

        &lt;!-- The Caelyf Groovlet servlet --&gt;
        &lt;servlet&gt;
            &lt;servlet-name&gt;GroovletServlet&lt;/servlet-name&gt;
            &lt;servlet-class&gt;groovyx.caelyf.CaelyfServlet&lt;/servlet-class&gt;
        &lt;/servlet&gt;

        &lt;!-- The Caelyf template servlet --&gt;
        &lt;servlet&gt;
            &lt;servlet-name&gt;TemplateServlet&lt;/servlet-name&gt;
            &lt;servlet-class&gt;groovyx.caelyf.CaelyfTemplateServletlt;/servlet-class&gt;
        &lt;/servlet&gt;

        &lt;!-- The URL routing filter --&gt;
        &lt;filter&gt;
            &lt;filter-name&gt;RoutesFilter&lt;/filter-name&gt;
            &lt;filter-class&gt;groovyx.caelyf.routes.RoutesFilter&lt;/filter-class&gt;
        &lt;/filter&gt;

        &lt;!-- Specify a mapping between *.groovy URLs and Groovlets --&gt;
        &lt;servlet-mapping&gt;
            &lt;servlet-name&gt;GroovletServlet&lt;/servlet-name&gt;
            &lt;url-pattern&gt;*.groovy&lt;/url-pattern&gt;
        &lt;/servlet-mapping&gt;

        &lt;!-- Specify a mapping between *.gtpl URLs and templates --&gt;
        &lt;servlet-mapping&gt;
            &lt;servlet-name&gt;TemplateServlet&lt;/servlet-name&gt;
            &lt;url-pattern&gt;*.gtpl&lt;/url-pattern&gt;
        &lt;/servlet-mapping&gt;

        &lt;filter-mapping&gt;
            &lt;filter-name&gt;RoutesFilter&lt;/filter-name&gt;
            &lt;url-pattern&gt;/*&lt;/url-pattern&gt;
        &lt;/filter-mapping&gt;

        &lt;!-- Define index.gtpl as a welcome file --&gt;
        &lt;welcome-file-list&gt;
            &lt;welcome-file&gt;index.gtpl&lt;/welcome-file&gt;
        &lt;/welcome-file-list&gt;
    &lt;/web-app&gt;
</pre>

<p>
In <code>web.xml</code>, we first define a servlet context listener, to initialize the <a href="/tutorial/plugins">plugin system</a>.
We define the two Caelyf servlets for Groovlets and templates,
as well as their respective mappings to URLs ending with <code>.groovy</code> and <code>.gtpl</code>.
We setup a servlet filter for the <a href="/tutorial/url-routing">URL routing</a> to have nice and friendly URLs.
We then define a welcome file for <code>index.gtpl</code>, so that URLs looking like a directory search for and template with that default name.
</p>

<blockquote>
<b>Note</b>: You can update the filter definition as shown below,
when you attempt to forward to a route from another Groovlet to keep your request attributes.
Without the dispatcher directives below the container is issuing a 302 redirect
which will cause you to lose all of your request attributes.
<pre class="brush:xml">
    &lt;filter-mapping&gt;
        &lt;filter-name&gt;RoutesFilter&lt;/filter-name&gt;
        &lt;url-pattern&gt;/*&lt;/url-pattern&gt;
        &lt;dispatcher&gt;FORWARD&lt;/dispatcher&gt;
        &lt;dispatcher&gt;REQUEST&lt;/dispatcher&gt;
    &lt;/filter-mapping&gt;
</pre>
</blockquote>

<h1>Using the template project</h1>

<p>
You can use the <a href="/tutorial/template-project">template project</a> offered by <b>Caelyf</b>.
It uses <b>Gradle</b> for the build, and for running and deploying applications,
and <b>Spock</b> for testing your groovlets.
Please have a look at the <a href="/tutorial/template-project">template project section</a> to know more about it.
</p>

<% if (!params.nowrap) include '/WEB-INF/includes/footer.gtpl' %>
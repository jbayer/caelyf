<title>Tutorial</title>

<h1 style="page-break-before: avoid;">Tutorial</h1>

<% if (!request.requestURI.contains('print')) { %>
<div id="sidebox" class="roundPinkBorder">
    <table>
        <tr>
            <td><a href="/tutorial/print"><img src="/images/icon-printer.png" alt="Printer-friendly"></a></td>
            <td><a href="/tutorial/print">Single page documentation</a></td>
        </tr>
        <tr>
            <td><a href="/caelyf.pdf"><img src="/images/icon-pdf.png" alt="Documentation PDF"></a></td>
            <td><a href="/caelyf.pdf">PDF documentation</a></td>
        </tr>
    </table>
</div>
<% } %>

<p>
The goal of this tutorial is to quickly get you started with using <b>Caelyf</b> to write
and deploy Groovy applications on Cloud Foundry.
We'll assume you have already setup your computer with the VMC tooling for deploying your applications to Cloud Foundry.
If you haven't, please do so by reading the 
<a href="http://start.cloudfoundry.com/getting-started.html">instructions</a> from the Cloud Foundry site.
</p>

<p>
The easiest way to get setup quickly is to download the template project from the <a href="/download">download section</a>.
It provides a ready-to-go project with the right configuration files pre-filled and an appropriate directory layout:
</p>

<ul>
    <li><code>web.xml</code> preconfigured with the <b>Caelyf</b> servlets</li>
    <li>a sample Groovlet and template</li>
    <li>the needed JARs (Groovy, Caelyf, Jedis)</li>
</ul>

<p>
You can <a href="/api/index.html">browse the JavaDoc</a> of the classes composing <b>Caelyf</b>.
</p>

<h2>Table of Content</h2>

<ul>
    <li><a href="/tutorial/setup">Setting up your project</a></li>
    <ul>
        <li><a href="/tutorial/setup#layout">Directory layout</a></li>
        <li><a href="/tutorial/setup#configuration">Configuration files</a></li>
    </ul>

    <li><a href="/tutorial/template-project">The template project</a></li>
    <ul>
        <li><a href="/tutorial/template-project#build">Gradle build file</a></li>
    </ul>

    <li><a href="/tutorial/views-and-controllers">Views and controllers</a></li>
    <ul>
        <li><a href="/tutorial/views-and-controllers#variables">Variables available in the binding</a></li>
        <ul>
            <li><a href="/tutorial/views-and-controllers#eager">Eager variables</a></li>
            <li><a href="/tutorial/views-and-controllers#lazy">Lazy variables</a></li>
            <li><a href="/tutorial/views-and-controllers#caelyfBindings">Injecting services and variables in your classes</a></li>
        </ul>
        <li><a href="/tutorial/views-and-controllers#templates">Templates</a></li>
        <ul>
            <li><a href="/tutorial/views-and-controllers#includes">Includes</a></li>
            <li><a href="/tutorial/views-and-controllers#redirect-forward">Redirect and forward</a></li>
        </ul>
        <li><a href="/tutorial/views-and-controllers#groovlets">Groovlets</a></li>
        <ul>
            <li><a href="/tutorial/views-and-controllers#markup-builder">Using MarkupBuilder to render XML or HTML snippets</a></li>
            <li><a href="/tutorial/views-and-controllers#view-delegation">Delegating to a view template</a></li>
        </ul>
        <li><a href="/tutorial/views-and-controllers#logging">Logging messages</a></li>
    </ul>

    <li><a href="/tutorial/url-routing">Flexible URL routing system</a></li>
    <ul>
        <li><a href="/tutorial/url-routing#configuration">Configuring URL routing</a></li>
        <li><a href="/tutorial/url-routing#route-definition">Defining URL routes</a></li>
        <li><a href="/tutorial/url-routing#wildcards">Using wildcards</a></li>
        <li><a href="/tutorial/url-routing#path-variables">Using path variables</a></li>
        <ul>
            <li><a href="/tutorial/url-routing#path-variable-validation">Validating path variables</a></li>
        </ul>
        <li><a href="/tutorial/url-routing#ignore">Ignoring certain routes</a></li>
        <li><a href="/tutorial/url-routing#caching">Caching groovlet and template output</a></li>
    </ul>

    <li><a href="/tutorial/plugins">Simple plugin system</a></li>
    <ul>
        <li><a href="/tutorial/plugins#what">What a plugin can do for you</a></li>
        <li><a href="/tutorial/plugins#anatomy">Anatomy of a plugin</a></li>
        <ul>
            <li><a href="/tutorial/plugins#hierarchy">Hierarchy</a></li>
            <li><a href="/tutorial/plugins#descriptor">The plugin descriptor</a></li>
        </ul>
        <li><a href="/tutorial/plugins#using">Using a plugin</a></li>
        <li><a href="/tutorial/plugins#distribute">How to distribute and deploy a plugin</a></li>
    </ul>
</ul>

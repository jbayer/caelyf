<% include '/WEB-INF/includes/header.gtpl?title=Welcome' %>

<h1>About Caelyf</h1>

<div id="sidebox" class="roundPinkBorder">
    <table>
        <tr>
            <td><a href="/download"><img src="/images/icon-download.png" alt="Download"></a></td>
            <td><a href="/download">Download</a></td>
        </tr>
        <tr>
            <td><a href="/tutorial"><img src="/images/icon-learn.png" alt="Learn"></a></td>
            <td><a href="/tutorial">Learn more</a></td>
        </tr>
        <tr>
            <td><a href="/community"><img src="/images/icon-group.png" alt="Get involved"></a></td>
            <td><a href="/community">Get involved</a></td>
        </tr>
        <tr>
            <td><a href="/search"><img src="/images/icon-zoom.png" alt="Search"></a></td>
            <td><a href="/search">Search</a></td>
        </tr>
    </table>
</div>

<div>
    <p>
        <b>Caelyf</b> is a lightweight <a href="http://groovy.codehaus.org">Groovy</a> toolkit
    for <a href="http://www.cloudfoundry.com">Cloud Foundry</a>.
    </p>
    <ul>
        <li><b>Caelyf</b> lets you deploy applications on Cloud Foundry</li>
        <li><b>Caelyf</b> gives you the choice to use Groovy for developing your applications</li>
        <li>
            <b>Caelyf</b> builds upon <a href="http://groovy.codehaus.org/Groovlets">Groovlets</a>
            and the <a href="http://groovy.codehaus.org/Groovy+Templates">Groovy template servlet</a>
        </li>
        <li><b>Caelyf</b> allows you to cleanly separate your views with Groovy templates and your actions in Groovlets.
        </li>
        <li><b>Caelyf</b> lets you define friendly REST-ful URLs thanks to its <a
                href="/tutorial/url-routing">URL routing</a> system</li>
        <li><b>Caelyf</b> provides a simple <a
                href="/tutorial/plugins">plugin system</a> for improving code reuse and code sharing</li>
        <li>
            <b>Caelyf</b> is <b>Open Source</b> and is released under the <b>Apache License</b> (ASL 2).
        Its source code is hosted by <a href="http://github.com/glaforge/caelyf/tree/master">Github</a>
        </li>
    </ul>

    <p>
        <b>Caelyf</b>'s origins come from a fork of the <a href="http://gaelyk.appspot.com">Gaelyk</a>
        lightweight Groovy toolkit for Google App Engine.
    </p>
</div>

<% include '/WEB-INF/includes/footer.gtpl' %>
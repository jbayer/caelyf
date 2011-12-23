<% if (!params.nowrap) include '/WEB-INF/includes/header.gtpl?title=Views+and+controllers' %>

<h1>Views and controllers</h1>

<p>
Now that our project is all setup, it's time to dive into groovlets and templates.
</p>

<blockquote>
<b>Tip: </b>A good practice is to separate your views from your logic
(following the usual <a href="http://en.wikipedia.org/wiki/Model–view–controller">MVC pattern</a>).
Since <b>Caelyf</b> provides both view templates and Groovlet controllers, it's advised to use the former for the view and the later for the logic.
</blockquote>

<p>
<b>Caelyf</b> builds on Groovy's own <a href="http://groovy.codehaus.org/Groovlets">Groovlets</a> and
<a href="http://groovy.codehaus.org/Groovy+Templates">template servlet</a> to add a shorcuts to the Cloud Foundry services.
</p>

<blockquote>
<b>Note: </b> You can learn more about Groovy's Groovlets and templates from this <a href="http://www.ibm.com/developerworks/java/library/j-pg03155/">article on IBM developerWorks</a>.
<b>Caelyf</b>'s own Groovlets and templates are just an extension of Groovy's ones,
and simply decorate Groovy's Groovlets and templates by giving access to Cloud Foundry services
and add some additional methods to them via <a href="http://groovy.codehaus.org/Groovy+Categories">Groovy categories</a>.
</blockquote>

<a name="variables"></a>
<h2>Variables available in the binding</h2>

<p>
A special servlet binding gives you direct access to some implicit variables that you can use in your views and controllers:
</p>

<a name="eager"></a>
<h3>Eager variables</h3>
<ul>
    <li>
        <tt>request</tt> : the
        <a href="http://java.sun.com/javaee/5/docs/api/javax/servlet/http/HttpServletRequest.html"><code>HttpServletRequest</code></a>
        object</li>
    <li>
        <tt>response</tt> : the
        <a href="http://java.sun.com/javaee/5/docs/api/javax/servlet/http/HttpServletResponse.html"><code>HttpServletResponse</code></a>
        object</li>
    <li>
        <tt>context</tt> : the
        <a href="http://java.sun.com/javaee/5/docs/api/javax/servlet/ServletContext.html"><code>ServletContext</code></a>
        object</li>
    <li>
        <tt>application</tt> : same as <code>context</code>
    </li>
    <li>
        <tt>session</tt> : shorthand for <code>request.getSession(false)</code> (can be null) which returns an
        <a href="http://java.sun.com/javaee/5/docs/api/javax/servlet/http/HttpSession.html"><code>HttpSession</code></a>
    </li>
    <li>
        <tt>params</tt> : map of all form parameters (can be empty)
    </li>
    <li>
        <tt>headers</tt> : map of all <tt>request</tt> header fields
    </li>
    <li>
        <tt>log</tt> : a Groovy logger is available for logging messages through <code>java.util.logging</code>
    </li>
    <li>
        <tt>logger</tt> : a logger accessor can be used to get access to any logger (more on <a href="#logging">logging</a>)
    </li>
</ul>

<a name="lazy"></a>
<h3>Lazy variables</h3>
<ul>
    <li><tt>out</tt> : shorthand for <code>response.getWriter()</code> which returns a <a href="http://java.sun.com/j2se/1.5.0/docs/api/java/io/PrintWriter.html"><code>PrintWriter</code></a></li>
    <li><tt>sout</tt> : shorthand for <code>response.getOutputStream()</code> which returns a <a href="http://java.sun.com/javaee/5/docs/api/javax/servlet/ServletOutputStream.html"><code>ServletOutputStream</code></a></li>
    <li><tt>html</tt> : shorthand for <code>new MarkupBuilder(response.getWriter())</code> which returns a <a href="http://groovy.codehaus.org/api/groovy/xml/MarkupBuilder.html"><code>MarkupBuilder</code></a></li>
</ul>

<blockquote>
    <b>Note: </b>
    The <i>eager</i> variables are pre-populated in the binding of your Groovlets and templates.
    The <i>lazy</i> variables are instantiated and inserted in the binding only upon the first request.
</blockquote>

<p>
Beyond those standard Servlet variables provided by Groovy's servlet binding, <b>Caelyf</b> also adds ones of his own
by injecting the following:
</p>

<ul>
    <li>
        <tt>localMode</tt> : a boolean variable which is <code>true</code> when the application is running in local
        development mode, and <code>false</code> when deployed on Cloud Foundry.
    </li>
    <li>
        <tt>app</tt> : a map variable with the following keys and values:
        <ul>
            <li>
                <tt>caelyf</tt> : a map with the following keys and values:
                <ul>
                    <li><tt>version</tt> : the version of the <b>Caelyf</b> toolkit used (here: ${app.caelyf.version})</li>
                </ul>
            </li>
        </ul>
    </li>
</ul>

<blockquote>
    <b>Note: </b>
    Regarding the <code>app</code> variable, this means you can access those values with the following syntax
    in your groovlets and templates:
    <pre class="brush:groovy">
        app.caelyf.version
    </pre>
</blockquote>

<p>
Thanks to all these variables and services available, you'll be able to access the Cloud Foundry services and Servlet specific artifacts
with a short and concise syntax to further streamline the code of your application.
</p>

<a name="caelyfBindings"></a>
<h3>Injecting services and variables in your classes</h3>

<p>
All the variables and services listed in the previous sections are automatically injected into the binding
of Groovlets and templates, making their access transparent, as if they were implicit or global variables.
But what about classes? If you want to also inject the services and variables into your classes,
you can annotate them with the <code>@CaelyfBindings</code> annotation.
</p>

<pre class="brush:groovy">
    import groovyx.caelyf.CaelyfBindings

    // annotate your class with the transformation
    @CaelyfBindings
    class WeblogService {
        def numberOfComments(post) {
            // you can use Redis right away
            redis.get('someKey')
        }
    }
</pre>

<p>
The annotation instructs the compiler to create properties in your class for each of the services and variables.
</p>

<blockquote>
<b>Note: </b> Variables like <code>request</code>, <code>response</code>, <code>session</code>, <code>context</code>,
<code>params</code>, <code>headers</code>, <code>out</code>, <code>sout</code>, <code>html</code> are not bound
in your classes.
</blockquote>

<blockquote>
<b>Note: </b> If your class already has a property of the same name as the variables and services injected
by this AST transformation, they won't be overriden.
</blockquote>

<a name="templates"></a>
<h2>Templates</h2>

<p>
<b>Caelyf</b> templates are very similar to JSPs or PHP: they are pages containing scriptlets of code.
You can:
</p>

<ul>
    <li>put blocks of Groovy code inside <code>&lt;% /* some code */ %&gt;</code>,</li>
    <li>call <code>print</code> and <code>println</code> inside those scriptlets for writing to the servlet writer,</li>
    <li>use the <code>&lt;%= variable %&gt;</code> notation to insert a value in the output,</li>
    <li>or also the GString notation <code>\${variable}</code> to insert some text or value.</li>
</ul>

<p>
Let's have a closer look at an example of what a template may look like:
</p>

<pre class="brush:xml">
    &lt;html&gt;
        &lt;body&gt;
            &lt;p&gt;&lt;%
                def message = "Hello World!"
                print message %&gt;
            &lt;/p&gt;
            &lt;p&gt;&lt;%= message %&gt;&lt;/p&gt;
            &lt;p&gt;\${message}&lt;/p&gt;
            &lt;ul&gt;
            &lt;% 3.times { %&gt;
                &lt;li&gt;\${message}&lt;/li&gt;
            &lt;% } %&gt;
            &lt;/ul&gt;
        &lt;/body&gt;
    &lt;/html&gt;
</pre>

<p>
The resulting HTML produced by the template will look like this:
</p>

<pre class="brush:xml">
    &lt;html&gt;
        &lt;body&gt;
            &lt;p&gt;Hello World!&lt;/p&gt;
            &lt;p&gt;Hello World!&lt;/p&gt;
            &lt;p&gt;Hello World!&lt;/p&gt;
            &lt;ul&gt;
                &lt;li&gt;Hello World!&lt;/li&gt;
                &lt;li&gt;Hello World!&lt;/li&gt;
                &lt;li&gt;Hello World!&lt;/li&gt;
            &lt;/ul&gt;
        &lt;/body&gt;
    &lt;/html&gt;
</pre>

<p>
If you need to import classes, you can also define imports in a scriplet at the top of your template as the following snippet shows:
</p>

<pre class="brush:groovy">
    &lt;% import com.foo.Bar %&gt;
</pre>

<blockquote>
    <b>Note: </b> Of course, you can also use Groovy's type aliasing with <code>import com.foo.ClassWithALongName as CWALN</code>.
    Then, later on, you can instantiate such a class with <code>def cwaln = new CWALN()</code>.
</blockquote>
<blockquote>
    <b>Note: </b> Also please note that import directives don't look like JSP directives (as of this writing).
</blockquote>

<p>
As we detailed in the previous section, you can also access the Servlet objects (request, response, session, context),
as well as other services offered by Cloud Foundry, such as Redis.
For instance, the following template will display some page content surrounded by a header and footer retrieved from the Redis cache:
</p>

<pre class="brush:xml">
    &lt;html&gt;
        &lt;body&gt;
            &lt;%= redis.get('page-header') %&gt;
            &lt;div&gt;Some content&lt;/div&gt;
            &lt;%= redis.get('page-footer') %&gt;
        &lt;/body&gt;
    &lt;/html&gt;
</pre>

<a name="includes"></a>
<h3>Includes</h3>

<p>
Often, you'll need to reuse certain graphical elements across different pages.
For instance, you always have a header, a footer, a navigation menu, etc.
In that case, the include mechanism comes in handy.
As advised, you may store templates in <code>WEB-INF/includes</code>.
In your main page, you may include a template as follows:
</p>

<pre class="brush:xml">
    &lt;% include '/WEB-INF/includes/header.gtpl' %&gt;

    &lt;div&gt;My main content here.&lt;/div&gt;

    &lt;% include '/WEB-INF/includes/footer.gtpl' %&gt;
</pre>

<a name="redirect-forward"></a>
<h3>Redirect and forward</h3>

<p>
When you want to chain templates or Groovlets, you can use the Servlet redirect and forward capabilities.
To do a forward, simply do:
</p>

<pre class="brush:groovy">
    &lt;% forward 'index.gtpl' %&gt;
</pre>

<p>
For a redirect, you can do:
</p>

<pre class="brush:groovy">
    &lt;% redirect 'index.gtpl' %&gt;
</pre>

<a name="groovlets"></a>
<h2>Groovlets</h2>

<p>
In contrast to view templates, Groovlets are actually mere Groovy scripts.
But they can access the output stream or the writer of the servlet, to write directly into the output.
Or they can also use the markup builder to output HTML or XML content to the view.
</p>

<p>
Let's have a look at an example Groovlet:
</p>

<pre class="brush:groovy">
    println """
        &lt;html&gt;
            &lt;body&gt;
    """

    [1, 2, 3, 4].each { number -> println "<p>\${number}</p>" }

    def now = new Date()

    println """
                &lt;p&gt;
                    \${now}
                &lt;/p&gt;
            &lt;/body&gt;
        &lt;/html&gt;
    """
</pre>

<p>
You can use <code>print</code> and <code>println</code> to output some HTML or other plain-text content to the view.
Instead of writing to <code>System.out</code>, <code>print</code> and <code>println</code> write to the output of the servlet.
For outputing HTML or XML, for instance, it's better to use a template,
or to send fragments written with a Markup builder as we shall see in the next sessions.
Inside those Groovy scripts, you can use all the features and syntax constructs of Groovy
(lists, maps, control structures, loops, create methods, utility classes, etc.)
</p>

<a name="markup-builder"></a>
<h3>Using <code>MarkupBuilder</code> to render XML or HTML snippets</h3>

<p>
Groovy's <code>MarkupBuilder</code> is a utility class that lets you create markup content (HTML / XML) with a Groovy notation,
instead of having to use ugly <code>println</code>s.
Our previous Groovlet can be written more cleanly as follows:
</p>

<pre class="brush:groovy">
html.html {
    body {
        [1, 2, 3, 4].each { number -> p number }

        def now = new Date()

        p now
    }
}
</pre>

<blockquote>
<b>Note: </b> You may want to learn more about <code>MarkupBuilder</code> in the
<a href="http://groovy.codehaus.org/Creating+XML+using+Groovy%27s+MarkupBuilder">Groovy wiki documentation</a> or on this
<a href="http://www.ibm.com/developerworks/java/library/j-pg05199/index.html?S_TACT=105AGX02&S_CMP=EDU">article from IBM developerWorks</a>.
</blockquote>

<a name="view-delegation"></a>
<h3>Delegating to a view template</h3>

<p>
As we explained in the section about redirects and forwards, at the end of your Groovlet,
you may simply redirect or forward to a template.
This is particularly interesting if we want to properly decouple the logic from the view.
To continue improving our previous Groovlets, we may, for instance, have a Groovlet compute the data needed by a template to render.
We'll need a Groovlet and a template. The Groovlet <code>WEB-INF/groovy/controller.groovy</code> would be as follows:
</p>

<pre class="brush:groovy">
    request['list'] = [1, 2, 3, 4]
    request['date'] = new Date()

    forward 'display.gtpl'
</pre>

<blockquote>
<b>Note: </b> For accessing the request attributes, the following syntaxes are actually equivalent:
<pre class="brush:groovy">
    request.setAttribute('list', [1, 2, 3, 4])
    request.setAttribute 'list', [1, 2, 3, 4]
    request['list'] = [1, 2, 3, 4]
    request.list = [1, 2, 3, 4]
</pre>
</blockquote>

<p>
The Groovlet uses the request attributes as a means to transfer data to the template.
The last line of the Groovlet then forwards the data back to the template view <code>display.gtpl</code>:
</p>

<pre class="brush:xml">
    &lt;html&gt;
        &lt;body&gt;
        &lt;% request.list.each { number -&gt; %&gt;
            &lt;p&gt;\${number}&lt;/p&gt;
        &lt;% } %&gt;
            &lt;p&gt;\${request.date}&lt;/p&gt;
        &lt;/body&gt;
    &lt;/html&gt;
</pre>

<a name="logging"></a>
<h2>Logging messages</h2>

<p>
In your Groovlets and Templates, thanks to the <code>log</code> variable in the binding,
you can log messages through the <code>java.util.logging</code> infrastructure.
The <code>log</code> variable is an instance of <code>groovyx.caelyf.logging.GroovyLogger</code>
and provides the methods:
<code>severe(String)</code>, <code>warning(String)</code>, <code>info(String)</code>, <code>config(String)</code>,
<code>fine(String)</code>, <code>finer(String)</code>, and <code>finest(String)</code>.
</p>

<p>
The default loggers in your groovlets and templates follow a naming convention.
The groovlet loggers' name starts with the <code>caelyf.groovlet</code> prefix,
whereas the template loggers' name starts with <code>caelyf.template</code>.
The name also contains the internal URI of the groovlet and template but transformed:
the slashes are exchanged with dots, and the extension of the file is removed.
</p>

<blockquote>
    <b>Note: </b>
    The extension is dropped, as one may have configured a different extension name for groovlets and templates
    than the usual ones (ie. <code>.groovy</code> and <code>.gtpl</code>).
</blockquote>

<p>
A few examples to illustrate this:
</p>

<table cellspacing="10">
    <tr>
        <th>URI</th>
        <th>Logger name</th>
    </tr>
    <tr>
        <td><code>/myTemplate.gtpl</code></td>
        <td><code>caelyf.template.myTemplate</code></td>
    </tr>
    <tr>
        <td><code>/crud/scaffolding.gtpl</code></td>
        <td><code>caelyf.template.crud.scaffolding</code></td>
    </tr>
    <tr>
        <td><code>/WEB-INF/templates/aTemplate.gtpl</code></td>
        <td><code>caleyf.template.WEB-INF.templates.aTemplate</code></td>
    </tr>
    <tr>
        <td><code>/upload.groovy</code><br/>
            (ie. <code>/WEB-INF/groovy/upload.groovy</code>)</td>
        <td><code>caelyf.groovlet.upload</code></td>
    </tr>
    <tr>
        <td><code>/account/credit.groovy</code><br/>
            (ie. <code>/WEB-INF/groovy/account/credit.groovy</code>)</td>
        <td><code>caelyf.groovlet.account.credit</code></td>
    </tr>
</table>


<p>
This naming convention is particularly interesting as the <code>java.util.logging</code> infrastructure
follows a hierarchy of loggers depending on their names, using dot delimiters, where
<code>caelyf.template.crud.scaffolding</code> inherits from
<code>caelyf.template.crud</code> which inherits in turn from
<code>caelyf.template</code>, then from
<code>caelyf</code>. You get the idea!
For more information on this hierarchy aspect,
please refer to the <a href="http://download.oracle.com/javase/1.5.0/docs/api/java/util/logging/class-use/LogManager.html">Java documentation</a>.
</p>

<p>
Concretely, it means you'll be able to have a fine grained way of defining your loggers hierarchy
and how they should be configured, as a child inherits from its parent configuration,
and a child is able to override parent's configuration.
So in your <code>logging.properties</code> file, you can have something like:
</p>

<pre>
# Set default log level to INFO
.level = INFO

# Configure Caelyf's log level to WARNING, including groovlet's and template's
caelyf.level = WARNING

# Configure groovlet's log level to FINE
caelyf.groovlet.level = FINE

# Override a specific groovlet familty to FINER
caelyf.groovlet.crud.level = FINER

# Set a specific groovlet level to FINEST
caelyf.groovlet.crud.scaffoldingGroovlet.level = FINEST

# Set a specific template level to FINE
caelyf.template.crud.editView.level = FINE
</pre>

<p class="brush:groovy">
You can also use the <code>GroovyLogger</code> in your Groovy classes:
</p>

<pre class="brush:groovy">
    import groovyx.caelyf.logging.GroovyLogger
    // ...
    def log = new GroovyLogger("myLogger")
    log.info "This is a logging message with level INFO"
</pre>

<p>
It is possible to access any logger thanks to the logger accessor,
which is available in the binding under the name <code>logger</code>.
From a Groovlet or a Template, you can do:
</p>

<pre class="brush:groovy">
    // access a logger by its name, as a property access
    logger.myNamedLogger.info "logging an info message"

    // when the logger has a complex name (like a package name with dots), prefer the subscript operator:
    logger['com.foo.Bar'].info "logging an info message"
</pre>

<p>
Additionally, there are two other loggers for tracing the routes filter and plugins handler, with
<code>caelyf.routesfilter</code> and <code>caelyf.pluginshandler</code>.
The last two log their messages with the <code>CONFIG</code> level,
so be sure to adapt the logging level in your logging configuration file
if you wish to troubleshoot how routes and plugins are handled.   
</p>

<% if (!params.nowrap) include '/WEB-INF/includes/footer.gtpl' %>
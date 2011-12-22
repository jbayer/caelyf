<title>Flexible URL routing</title>

<h1>Flexible URL routing</h1>

<p>
<b>Caelyf</b> provides a flexible and powerful URL routing system:
you can use a small Groovy Domain-Specific Language for defining routes for nicer and friendlier URLs.
</p>

<a name="configuration"></a>
<h2>Configuring URL routing</h2>

<p>
To enable the URL routing system, you should configure the <code>RoutesFilter</code> servlet filter in <code>web.xml</code>:
</p>

<pre class="brush:xml">
    ...
    &lt;filter&gt;
        &lt;filter-name&gt;RoutesFilter&lt;/filter-name&gt;
        &lt;filter-class&gt;groovyx.caelyf.routes.RoutesFilter&lt;/filter-class&gt;
    &lt;/filter&gt;
    ...
    &lt;filter-mapping&gt;
        &lt;filter-name&gt;RoutesFilter&lt;/filter-name&gt;
        &lt;url-pattern&gt;/*&lt;/url-pattern&gt;
    &lt;/filter-mapping&gt;
    ...
</pre>

<blockquote>
<b>Note: </b> We advise to setup only one route filter, but it is certainly possible to define several ones
for different areas of your site.
By default, the filter is looking for the file <code>/WEB-INF/routes.groovy</code> for the routes definitions,
but it is possible to override this setting by specifying a different route DSL file with a servlet filter configuration parameter:
<pre class="brush:xml">
    &lt;filter&gt;
        &lt;filter-name&gt;RoutesFilter&lt;/filter-name&gt;
        &lt;filter-class&gt;groovyx.caelyf.routes.RoutesFilter&lt;/filter-class&gt;
        &lt;init-param&gt;
            &lt;param-name&gt;routes.location&lt;/param-name&gt;
            &lt;param-value&gt;/WEB-INF/blogRoutes.groovy&lt;/param-value&gt;
        &lt;/init-param&gt;
    &lt;/filter&gt;
</pre>
</blockquote>

<blockquote>
<b>Warning: </b> The filter is stopping the chain filter once a route is found.
So you should ideally put the route filter as the last element of the chain.
</blockquote>

<a name="route-definition"></a>
<h2>Defining URL routes</h2>

<p>
By default, once the filter is configured, URL routes are defined in <code>WEB-INF/routes.groovy</code>,
in the form of a simple Groovy scripts, defining routes in the form of a lightweight DSL.
The capabilities of the routing system are as follow, you can:
</p>

<ul>
    <li>match requests made with a certain method (GET, POST, PUT, DELETE), or all</li>
    <li>define the final destination of the request</li>
    <li>chose whether you want to forward or redirect to the destination URL (i.e. URL rewriting through forward vs. redirection)</li>
    <li>express variables in the route definition and reuse them as variables in the final destination of the request</li>
    <li>validate the variables according to some boolean expression, or regular expression matching</li>
    <li>cache the output of groovlets and templates pointed by that route for a specified period of time</li>
</ul>

<p>
Let's see those various capabilities in action.
Imagine we want to define friendly URLs for our blog application.
Let's configure a first route in <code>WEB-INF/routes.groovy</code>.
Say you want to provide a shorthand URL <code>/about</code> that would redirect to your first blog post.
You could configure the <code>/about</code> route for all GET requests calling the <code>get</code> method.
You would then redirect those requests to the final destination with the <code>redirect</code> named argument:
</p>

<pre class="brush:groovy">
    get "/about", redirect: "/blog/2008/10/20/welcome-to-my-blog"
</pre>

<p>If you prefer to do a forward, so as to do URL rewriting to keep the nice short URL,
you would just replace <code>redirect</code> with <code>forward</code> as follows:</p>

<pre class="brush:groovy">
    get "/about", forward: "/blog/2008/10/20/welcome-to-my-blog"
</pre>

<p>
If you have different routes for different HTTP methods, you can use the <code>get</code>, <code>post</code>,
<code>put</code> and <code>delete</code> methods.
If you want to catch all the requests independently of the HTTP method used, you can use the <code>all</code> function.
Another example, if you want to post only to a URL to create a new blog article,
and want to delegate the work to a <code>post.groovy</code> Groovlet, you would create a route like this one:
</p>

<pre class="brush:groovy">
    post "/new-article", forward: "/post.groovy"     // shortcut for "/WEB-INF/groovy/post.groovy"
</pre>

<blockquote>
<b>Note: </b> When running your applications in development mode, <b>Caelyf</b> is configured to take into accounts
any changes made to the <code>routes.groovy</code> definition file.
Each time a request is made, which goes through the route servlet filter, <b>Caelyf</b> checks whether a more
recent route definition file exists.
However, once deployed on Cloud Foundry, the routes are set in stone and are not reloaded.
The sole cost of the routing system is the regular expression mapping to match request URIs against route patterns.
</blockquote>

<a name="wildcards"></a>
<h2>Using wildcards</h2>

<p>
You can use a single and a double star as wildcards in your routes, similarly to the Ant globing patterns.
A single star matches a word (<code>/\\w+/</code>), where as a double start matches an arbitrary path.
For instance, if you want to show information about the blog authors,
you may forward all URLs starting with <code>/author</code> to the same Groovlet:
</p>

<pre class="brush:groovy">
    get "/author/*", forward: "/authorsInformation.groovy"
</pre>

<p>
This route would match requests made to <code>/author/johnny</code> as well as to <code>/author/begood</code>.
</p>

<p>
In the same vein, using the double star to forward all requests starting with <code>/author</code> to the same Groovlet:
</p>

<pre class="brush:groovy">
    get "/author/**", forward: "/authorsInformation.groovy"
</pre>

<p>
This route would match requests made to <code>/author/johnny</code>, as well as <code>/author/johnny/begood</code>,
or even <code>/author/johnny/begood/and/anyone/else</code>.
</p>

<blockquote>
<b>Warning: </b> Beware of the abuse of too many wildcards in your routes,
as they may be time consuming to compute when matching a request URI to a route pattern.
Better prefer several explicit routes than a too complicated single route.
</blockquote>

<a name="path-variables"></a>
<h2>Using path variables</h2>

<p>
<b>Caelyf</b> provides a more convenient way to retrieve the various parts of a request URI, thanks to path variables.
</p>

<p>
In a blog application, you want your article to have friendly URLs.
For example, a blog post announcing the release of Groovy 1.7-RC-1 could be located at:
<code>/article/2009/11/27/groovy-17-RC-1-released</code>.
And you want to be able to reuse the various elements of that URL to pass them in the query string of the Groovlet
which is responsible for displaying the article.
You can then define a route with path variables as shown in the example below:
</p>

<pre class="brush:groovy">
    get "/article/@year/@month/@day/@title", forward: "/article.groovy?year=@year&month=@month&day=@day&title=@title"
</pre>

<p>
The path variables are of the form <code>@something</code>, where something is a word (in terms of regular expressions).
Here, with our original request URI, the variables will contains the string <code>'2009'</code> for the
<code>year</code> variable, <code>'11'</code> for <code>month</code>, <code>'27'</code> for <code>day</code>,
and <code>'groovy-17-RC-1-released</code> for the <code>title</code> variable.
And the final Groovlet URI which will get the request will be
<code>/WEB-INF/groovy/article.groovy?year=2009&month=11&day=27&title=groovy-17-RC-1-released</code>,
once the path variable matching is done.
</p>

<blockquote>
<b>Note: </b> If you want to have optional path variables, you should define as many routes as options.
So you would define the following routes to display all the articles published on some year, month, or day:
<pre class="brush:groovy">
    get "/article/@year/@month/@day/@title", forward: "/article.groovy?year=@year&month=@month&day=@day&title=@title"
    get "/article/@year/@month/@day",        forward: "/article.groovy?year=@year&month=@month&day=@day"
    get "/article/@year/@month",             forward: "/article.groovy?year=@year&month=@month"
    get "/article/@year",                    forward: "/article.groovy?year=@year"
    get "/article",                          forward: "/article.groovy"
</pre>
Also, note that routes are matched in order of appearance.
So if you have several routes which map an incoming request URI, the first one encountered in the route definition file will win.
</blockquote>

<a name="path-variable-validation"></a>
<h3>Validating path variables</h3>

<p>
The routing system also allows you to validate path variables thanks to the usage of a closure.
So if you use path variable validation, a request URI will match a route if the route path matches,
but also if the closure returns a boolean, or a value which is coercible to a boolean
through to the usual <i>Groovy Truth</i> rules.
Still using our article route, we would like the year to be 4 digits, the month and day 2 digits,
and impose no particular constraints on the title path variable,
we could define our route as follows:
</p>

<pre class="brush:groovy">
    get "/article/@year/@month/@day/@title",
        forward: "/article.groovy?year=@year&month=@month&day=@day&title=@title",
        validate: { year ==~ /\\d{4}/ && month ==~ /\\d{2}/ && day ==~ /\\d{2}/ }
</pre>

<blockquote>
<b>Note: </b> Just as the path variables found in the request URI are replaced in the rewritten URL,
the path variables are also available inside the body of the closure,
so you can apply your validation logic.
Here in our closure, we used Groovy's regular expression matching support,
but you can use boolean logic that you want, like <code>year.isNumber()</code>, etc.
</blockquote>

<p>
In addition to the path variables, you also have access to the <code>request</code> from within the validation closure.
For example, if you wanted to check that a particular attribute is present in the request,
like checking a user is registered to access a message board, you could do:
</p>

<pre class="brush:groovy">
    get "/message-board",
        forward: "/msgBoard.groovy",
        validate: { request.registered == true }
</pre>

<a name="ignore"></a>
<h2>Ignoring certain routes</h2>

<p>
As a fast path to bypass certain URL patterns, you can use the <code>ignore: true</code> parameter in your route definition:
</p>

<pre class="brush:groovy">
    all "/_ah/**", ignore: true
</pre>

<a name="caching"></a>
<h2>Caching groovlet and template output</h2>

<p>
<b>Caelyf</b> provides support for caching groovlet and template output,
and this be defined through the URL routing system.
This caching capability obviously leverages the Redis data structure service of Cloud Foundry.
In the definition of your routes, you simply have to add a new named parameter: <code>cache</code>,
indicating the number of seconds, minutes or hours you want the page to be cached.
Here are a few examples:
</p>

<pre class="brush:groovy">
    get "/news",     forward: "/new.groovy",     cache: 10.minutes
    get "/tickers",  forward: "/tickers.groovy", cache: 1.second
    get "/download", forward: "/download.gtpl",  cache: 2.hours
</pre>

<p>
The duration can be any number (an int) of second(s), minute(s) or hour(s):
both plural and singular forms are supported.
</p>

<a name="cacheclear"></a>
<p>
It is possible to clear the cache for a given URI if you want to provide a fresher page to your users:
</p>

<pre class="brush:groovy">
    memcache.clearCacheForUri('/breaking-news')
</pre>

<blockquote>
<b>Note: </b> There are as many cache entries as URIs with query strings.
So if you have <code>/breaking-news</code> and <code>/breaking-news?category=politics</code>,
you will have to clear the cache for both, as <b>Caelyf</b> doesn't track all the query parameters.
</blockquote>


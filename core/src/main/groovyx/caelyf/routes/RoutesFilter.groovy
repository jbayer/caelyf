/*
 * Copyright 2009-2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package groovyx.caelyf.routes

import javax.servlet.Filter
import javax.servlet.FilterChain
import javax.servlet.ServletResponse
import javax.servlet.ServletRequest
import javax.servlet.FilterConfig
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import groovyx.caelyf.BindingEnhancer
import groovyx.caelyf.plugins.PluginsHandler
import groovyx.caelyf.ExpirationTimeCategory
import groovyx.caelyf.cache.CacheHandler
import groovyx.caelyf.logging.GroovyLogger

import org.codehaus.groovy.control.CompilerConfiguration

/**
 * <code>RoutesFilter</code> is a Servlet Filter whose responsability is to define URL mappings for your
 * Caelyf application. When the servlet filter is configured, a file named <code>routes.groovy</code>
 * will be loaded by the filter, defining the various routes a web request may follow.
 * <p>
 * It is possible to customize the location of the routes definition file by using the
 * <code>routes.location</code> init parameter in the declaration of the filter in <code>web.xml</code>.
 * <p>
 * In development mode, routes will be reloaded automatically on each request, but when the application
 * is deployed on Cloud Foundry, all the routes will be set in stone.
 *
 * @author Guillaume Laforge
 */
class RoutesFilter implements Filter {

    /**
     * Location of the routes file definition
     */
    private String routesFileLocation
    private long lastRoutesFileModification = 0
    private List<Route> routes = []
    private FilterConfig filterConfig
    private GroovyLogger log = new GroovyLogger('caelyf.routesfilter')

    void init(FilterConfig filterConfig) {

        log.config "Routes filter being initialized"

        this.filterConfig = filterConfig
        this.routesFileLocation = filterConfig.getInitParameter("routes.location") ?: "/WEB-INF/routes.groovy"
        loadRoutes()
    }

    /**
     * Load the routes configuration
     */
    synchronized void loadRoutes() {
        log.config "Loading routes configuration"

        def routesResource = filterConfig.servletContext.getResource(this.routesFileLocation)

        def connection = null

        try {
            connection = routesResource.openConnection()

            def lastModified = connection.getLastModified()

            // if the file has changed since the last check, reload the routes
            if (lastModified > lastRoutesFileModification) {
                def config = new CompilerConfiguration()
                config.scriptBaseClass = RoutesBaseScript.class.name

                // define a binding for the routes definition,
                def binding = new Binding()
                BindingEnhancer.bind(binding)

                connection.inputStream.withReader('UTF-8') { Reader reader ->
                    // evaluate the route definitions
                    def routesConfigText = reader.text
                    RoutesBaseScript script = (RoutesBaseScript) new GroovyShell(binding, config).parse(routesConfigText)

                    use(ExpirationTimeCategory) {
                        script.run()
                    }
                    routes = script.routes

                    // add the routes defined by the plugins
                    routes.addAll PluginsHandler.instance.routes
                }

                // update the last modified flag
                lastRoutesFileModification = lastModified
            }
        } catch (Throwable t) {
            log.config "Impossible to read or parse routes.groovy (${t.message})"

            t.printStackTrace()
        } finally {
            if (connection?.inputStream) {
                connection.inputStream.close()
            }
        }

    }

    /**
     * Forward or redirects requests to another URL if a matching route is defined.
     * Otherwise, the normal filter chain and routing applies.
     */
    void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) {
        // reload the routes in local dev mode in case the routes definition has changed since the last request
        if (!System.getenv('VCAP_SERVICES')) {
            log.config "Realoding routes..."
            loadRoutes()
        }

        HttpServletRequest request = (HttpServletRequest)servletRequest
        HttpServletResponse response = (HttpServletResponse)servletResponse

        def method = request.method

        boolean foundRoute = false
        for (Route route in routes) {
            // first, check that the HTTP methods are compatible
            if (route.method == HttpMethod.ALL || route.method.toString() == method) {
                def result = route.forUri(request)
                if (result.matches) {
                    if (route.ignore) {
                        // skip out completely
                        break
                    }
                    if (route.redirectionType == RedirectionType.FORWARD) {
                        CacheHandler.serve(route, request, response)
                    } else {
                        response.sendRedirect result.destination
                    }
                    foundRoute = true
                    break
                }
            }
        }

        if (!foundRoute) {
            filterChain.doFilter servletRequest, servletResponse
        }
    }

    void destroy() { }
}

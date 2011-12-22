package groovyx.caelyf.routes

import javax.servlet.FilterConfig
import javax.servlet.ServletContext
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.servlet.FilterChain
import javax.servlet.RequestDispatcher
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import groovyx.caelyf.BindingEnhancer

/**
 * 
 * @author Guillaume Laforge
 */
class RoutesFilterTest extends GroovyTestCase {

    Binding binding

    def filter = new RoutesFilter()

    protected void setUp() {
        super.setUp()

        binding = new Binding()
        BindingEnhancer.bind(binding)

        filter.init(new FilterConfig() {
            String getFilterName() { "RoutesFilter" }

            String getInitParameter(String s) {
                if (s == 'routes.location') {
                    'src/test/groovyx/caelyf/routes/routes.sample'
                }
            }

            Enumeration getInitParameterNames() {
                ['routes.location'] as Enumeration
            }

            ServletContext getServletContext() {
                [getResource: { String name ->
                    new File('src/test/groovyx/caelyf/routes/routes.sample').toURL()
                }] as ServletContext
            }
        })

        filter.loadRoutes()

        assert filter.routes
    }

    void testNoRouteFound() {
        def request = [
                getRequestURI: { -> "/nowhere" },
                getMethod: { -> "GET" }
        ] as HttpServletRequest

        def response = [:] as HttpServletResponse

        def chained = false
        def chain = [
                doFilter: { HttpServletRequest req, HttpServletResponse resp -> chained = true }
        ] as FilterChain

        filter.doFilter(request, response, chain)

        assert chained
    }

    void testIgnoredRoute() {
        def request = [
                getRequestURI: { -> "/ignore" },
                getMethod: { -> "GET" },
                setAttribute: { String name, val -> }
        ] as HttpServletRequest

        def response = [:] as HttpServletResponse

        def chained = false
        def chain = [
                doFilter: { HttpServletRequest req, HttpServletResponse resp -> chained = true }
        ] as FilterChain

        filter.doFilter(request, response, chain)

        assert chained
    }

    void testRedirectRoute() {
        def request = [
                getRequestURI: { -> "/redirect" },
                getMethod: { -> "GET" },
                setAttribute: { String name, val -> }
        ] as HttpServletRequest

        def redirected = ""

        def response = [
                sendRedirect: { String where -> redirected = where }
        ] as HttpServletResponse

        def chain = [:] as FilterChain

        filter.doFilter(request, response, chain)

        assert redirected == "/elsewhere.gtpl"
    }

    void testNormalRoute() {
        def forwarded = false
        def dispatched = ""

        def dispatcher = [
                forward: { ServletRequest req, ServletResponse resp -> forwarded = true }
        ] as RequestDispatcher

        def request = [
                getRequestURI: { -> "/somewhere" },
                getQueryString: { -> "" },
                getMethod: { -> "GET" },
                getRequestDispatcher: { String s -> dispatched = s; return dispatcher },
                setAttribute: { String name, val -> }
        ] as HttpServletRequest

        def response = [:] as HttpServletResponse

        def chain = [:] as FilterChain

        filter.doFilter(request, response, chain)

        assert forwarded
        assert dispatched == "/somewhere.groovy"
    }

}

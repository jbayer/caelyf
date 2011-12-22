package groovyx.caelyf

import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpServletRequest
import javax.servlet.RequestDispatcher
import javax.servlet.ServletResponse
import javax.servlet.ServletRequest
import javax.servlet.http.HttpSession
import javax.servlet.ServletContext
import javax.servlet.ServletConfig

/**
 * @author Guillaume Laforge
 */
class CaelyfTemplateServletTest extends GroovyTestCase {

    void testGet() {
        def tempFile = File.createTempFile("template", ".gtpl")
        tempFile.createNewFile()
        tempFile << '''
            Hello World
            <%= params.foo %>
            ${request.getAttribute('bar')}
            <% include 'other.gtpl' %>
        '''

        def writer = new StringWriter()
        def printWriter = new PrintWriter(writer)

        def ctxt = [
                log: { String msg -> println "log $msg" },
                getRealPath: { String p ->
                    println "getRealPath($p)"
                    tempFile.absolutePath
                },
                getResource: { String p -> println "getResource($p)"; tempFile.toURL() }
        ] as ServletContext

        def config = [
                getServletContext: {-> ctxt },
                getInitParameter: { String p -> println "getInitParameter($p)" },
                getServletName: {-> "groovyx.caelyf.CaelyfTemplateServlet" }
        ] as ServletConfig

        def session = [:] as HttpSession

        def request = [
                toString: {-> "mock request" },
                getProtocol: {-> println "getProtocol()"; "HTTP/1.1" },
                getAttribute: { String attr -> println "getAttribute($attr)"; return attr },
                getServletPath: {-> println "getServletPath()"; "/template.gtpl" },
                getPathInfo: {-> println "getPathInfo()"; null },
                getSession: { boolean b -> println "getSession($b)"; session },
                getParameterNames: {->
                    println "getParameterNames()"; new Enumeration() {
                        boolean hasMore = false
                        boolean hasMoreElements() { hasMore = !hasMore; return hasMore }
                        Object nextElement() { return "foo" }
                    }
                },
                getParameterValues: { String param -> [param] as String[] },
                getHeaderNames: {->
                    println "getHeaderNames()"; new Enumeration() {
                        boolean hasMoreElements() { return false }
                        Object nextElement() { return null }
                    }
                },
                getRequestDispatcher: { String p -> [
                        include: { ServletRequest request, ServletResponse response ->
                            println "include $p"
                            response.writer << "bye"
                        },
                        forward: { ServletRequest request, ServletResponse response ->
                            println "forward $p"
                        }
                ] as RequestDispatcher }
        ] as HttpServletRequest

        def response = [
                toString: {-> "mock response" },
                setContentType: { String ct -> println "setContentType($ct)" },
                sendError: { int err, String msg = null -> println "sendError($err, $msg)" },
                getWriter: {-> println "getWriter()"; printWriter },
                setStatus: { int i -> println "setStatus($i)" },
                flushBuffer: {-> println "flushBuffer()" }
        ] as HttpServletResponse

        try {
            def servlet = new CaelyfTemplateServlet()
            servlet.init(config)
            servlet.service(request, response)

            def result = writer.toString()
            println result

            assert result.contains('Hello World')
            assert result.contains('foo')
            assert result.contains('bar')
            assert result.contains('bye')
        } finally {
            tempFile.delete()
        }
    }
}

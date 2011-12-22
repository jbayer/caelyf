package groovyx.caelyf

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpSession
import javax.servlet.ServletConfig
import javax.servlet.ServletContext
import javax.servlet.RequestDispatcher
import javax.servlet.ServletResponse
import javax.servlet.ServletRequest

/**
 * @author Guillaume Laforge
 */
class CaelyfServletTest extends GroovyTestCase {

    void testGet() {
        def tempFile = File.createTempFile("groovlet", ".groovy")
        tempFile.createNewFile()
        tempFile << """
            log.info 'start'
            out << 'hello'
            include 'bye.gtpl'
        """

        def writer = new StringWriter()
        def printWriter = new PrintWriter(writer)

        def ctxt = [
                log: { String msg -> println "log $msg" },
                getRealPath: { String p ->
                    println "getRealPath($p)"
                    if (p.contains('index'))
                        tempFile.absolutePath
                    else
                        "."
                },
                getResource: { String p -> println "getResource($p)"; tempFile.toURL() }
        ] as ServletContext

        def config = [
                getServletContext: {-> ctxt },
                getInitParameter: { String p -> println "getInitParameter($p)" }
        ] as ServletConfig

        def session = [:] as HttpSession

        def request = [
                toString: {-> "mock request" },
                getProtocol: {-> println "getProtocol()"; "HTTP/1.1" },
                getAttribute: { String attr -> println "getAttribute($attr)" },
                getServletPath: {-> println "getServletPath()"; "/index.groovy" },
                getPathInfo: {-> println "getPathInfo()"; null },
                getSession: { boolean b -> println "getSession($b)"; session },
                getParameterNames: {->
                    println "getParameterNames()"; new Enumeration() {
                        boolean hasMoreElements() { return false }
                        Object nextElement() { return null }
                    }
                },
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
                getWriter: { -> println "getWriter()"; printWriter }
        ] as HttpServletResponse

        try {
            def servlet = new CaelyfServlet()
            servlet.init(config)
            servlet.service(request, response)

            assert writer.toString() == 'hellobye'

        } finally {
            tempFile.delete()
        }
    }
}

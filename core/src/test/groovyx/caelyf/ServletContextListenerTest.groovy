package groovyx.caelyf

import javax.servlet.ServletContextEvent
import javax.servlet.ServletContext
import groovyx.caelyf.plugins.PluginsHandler

/**
 * @author Guillaume Laforge
 */
class ServletContextListenerTest extends GroovyTestCase {

    void testContextListener() {
        boolean called = false
        PluginsHandler.instance.scriptContent = { called = true }

        def context = [:] as ServletContext
        def event = new ServletContextEvent(context)

        def listener = new CaelyfServletContextListener()

        listener.contextInitialized event

        assert called
    }
}

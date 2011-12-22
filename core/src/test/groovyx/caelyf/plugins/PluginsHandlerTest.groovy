package groovyx.caelyf.plugins

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import groovy.servlet.ServletCategory
import groovyx.caelyf.BindingEnhancer

/**
 * 
 * @author Guillaume Laforge
 */
class PluginsHandlerTest extends GroovyTestCase {

    private Binding binding

    protected void setUp() {
        super.setUp()

        binding = new Binding()
        BindingEnhancer.bind(binding)

        PluginsHandler.instance.reinit()
    }

    void testStandardPluginScriptReadingRoutine() {
        def content = PluginsHandler.instance.scriptContent("src/test/groovyx/caelyf/plugins/pluginScript.sample")
        assert content == """\
            binding {
                version = '1.2.3'
            }""".stripIndent()
    }

    void testEnrichExistingBindingWithPluginBindingDefinition() {
        PluginsHandler.instance.with {
            scriptContent = { String path ->
                if (path == "WEB-INF/plugins.groovy") {
                    "install myPlugin"
                } else if (path == "WEB-INF/plugins/myPlugin.groovy") {
                    """
                    binding {
                        version = '1.2.3'
                    }
                    """
                } else ""
            }

            initPlugins()

            def binding = new Binding(version: '0.5.6')
            enrich binding

            assert binding.getVariable('version') == '1.2.3'
        }
    }

    void testNoPlugins() {
        PluginsHandler.instance.with {
            scriptContent = { String path -> "" }
            initPlugins()

            assert !bindingVariables
            assert !routes
            assert !categories
            assert !beforeActions
            assert !afterActions
        }
    }

    void testOnePlugin() {
        PluginsHandler.instance.with {
            scriptContent = { String path ->
                if (path == "WEB-INF/plugins.groovy") {
                    "install myPlugin"
                } else if (path == "WEB-INF/plugins/myPlugin.groovy") {
                    """
                    binding {
                        version = "1.2.3"
                    }

                    routes {
                        get "/index", forward: "/index.groovy"
                        post "/upload", forward: "/upload.groovy"
                    }

                    categories MyCat

                    before { 'before' }

                    after  { 'after' }

                    class MyCat {}
                    """
                } else ""
            }

            initPlugins()

            assert bindingVariables.version == "1.2.3"
            assert routes.size() == 2
            assert categories*.name == ['MyCat']
            assert beforeActions.size() == 1
            assert beforeActions[0]() == 'before'
            assert afterActions.size() == 1
            assert afterActions[0]() == 'after'
        }
    }

    void testTwoPluginsToCheckOrderOfActions() {
        def output = new StringBuilder()

        def request  = [
                getAttribute: { String key -> output }
        ] as HttpServletRequest

        def response = [:] as HttpServletResponse

        PluginsHandler.instance.with {
            scriptContent = { String path ->
                if (path == "WEB-INF/plugins.groovy") {
                    """
                    install pluginOne
                    install pluginTwo
                    """
                } else if (path == "WEB-INF/plugins/pluginOne.groovy") {
                    """
                    before { request.sample << '1' }
                    after  { request.sample << '2' }
                    """
                } else if (path == "WEB-INF/plugins/pluginTwo.groovy") {
                    """
                    before { request.sample << '3' }
                    after  { request.sample << '4' }
                    """
                }
            }

            initPlugins()

            assert beforeActions.size() == 2
            assert afterActions.size()  == 2

            executeBeforeActions request, response
            executeAfterActions  request, response

            use(ServletCategory) {
                assert request.sample.toString() == '1342'
            }
        }
    }

    void testAccessOriginalBindingVars() {
        PluginsHandler.instance.with {
            scriptContent = { String path ->
                if (path == "WEB-INF/plugins.groovy") {
                    "install myPlugin"
                } else if (path == "WEB-INF/plugins/myPlugin.groovy") {
                    """
                    binding {
                        book = "Harry Potter"
                    }

                    before {
                        request.fromBindingBlock = binding
                    }
                    """
                } else ""
            }

            initPlugins()

            def values = [:]
            def request = [setAttribute: { String name, obj -> values[name] = obj }] as HttpServletRequest
            executeBeforeActions(request, [:] as HttpServletResponse)

            assert values.fromBindingBlock.book == "Harry Potter"
        }

    }
}



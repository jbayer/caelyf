package groovyx.caelyf.plugins

import org.codehaus.groovy.control.CompilerConfiguration

import groovyx.caelyf.BindingEnhancer

/**
 * 
 * @author Guillaume Laforge
 */
class PluginBaseScriptTest extends GroovyTestCase {

    private Binding binding

    protected void setUp() {
        binding = new Binding()
        BindingEnhancer.bind(binding)

        PluginsHandler.instance.reinit()
    }

    protected void tearDown() {
        super.tearDown()
    }


    void testLoadPluginDescriptor() {
        def config = new CompilerConfiguration()
        config.scriptBaseClass = PluginBaseScript.class.name

        def binding = new Binding()

        PluginBaseScript script = (PluginBaseScript) new GroovyShell(binding, config).parse("""
            binding {
                version = "1.2"
            }

            categories MyCategory, MyOtherCategory

            routes {
                get "/crud", forward: "/crud.groovy"
            }

            before {
                "before"
            }

            after {
                "after"
            }

            class MyCategory {}
            class MyOtherCategory {}

            return "initialized"
        """)

        assert script.run() == "initialized"

        assert script.getBindingVariables()['version'] == "1.2"
        assert script.getCategories()*.name == ['MyCategory', 'MyOtherCategory']
        assert script.getBeforeAction()() == "before"
        assert script.getAfterAction()() == "after"
    }
}

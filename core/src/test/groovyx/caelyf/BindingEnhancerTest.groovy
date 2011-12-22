package groovyx.caelyf

/**
 * Test the binding enhancer binds the services and other utilities in the binding.
 *
 * @author Guillaume Laforge
 */
class BindingEnhancerTest extends GroovyTestCase {

    private Binding binding

    protected void setUp() {
        super.setUp()

        binding = new Binding()
        BindingEnhancer.bind(binding)
    }

    /**
     * Check the various services variables are available in the binding
     */
    void testVariablesPresent() {
        ["localMode", "logger", "redis"].each {
            assert binding.variables.containsKey(it)
        }
    }

    void testCaelyfVersionPresent() {
        assert binding.app.caelyf.version ==~ /\d+\.\d+(\.\d+)?/
    }
}

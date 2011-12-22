package groovyx.caelyf

import groovyx.caelyf.logging.LoggerAccessor

/**
 * @author Vladimir Orany
 * @author Guillaume Laforge
 */
class BindingsInjectionInClassesTest extends GroovyTestCase {

    private Binding binding

    protected void setUp() {
        super.setUp()
        binding = new Binding()
        BindingEnhancer.bind(binding)
    }

    void testInjection() {
        def obj = new GroovyShell().evaluate '''
			import groovyx.caelyf.CaelyfBindings

			@CaelyfBindings
			class TestEnhanced {}
			new TestEnhanced()
		'''

        [
                localMode		  : Boolean,
                app 			  : Map,
                logger  		  : LoggerAccessor,
        ].each { property, clazz ->
            assert obj.metaClass.getMetaProperty(property)?.type == clazz
        }
    }

    void testExistingPropertyShouldntBeOverriden() {
        def obj = new GroovyShell().evaluate '''
            import groovyx.caelyf.CaelyfBindings

			@CaelyfBindings
			class TestEnhanced {
				Map datastore
			}
			new TestEnhanced()
		'''

		assert obj.hasProperty('datastore')
		assert obj.metaClass.getMetaProperty('datastore').getType() == Map
    }
}

package groovyx.caelyf

import groovyx.caelyf.logging.GroovyLogger

/**
 * @author Guillaume Laforge
 */
class LoggerNamesTest extends GroovyTestCase {
    void testTemplateName() {
        def expected = [
                '/index.gtpl':                  'caelyf.template.index',
                '/tutorial/chap1.gtpl':         'caelyf.template.tutorial.chap1',
                '/WEB-INF/pages/index.gtpl':    'caelyf.template.WEB-INF.pages.index',
                '/WEB-INF/includes/a.gtpl':     'caelyf.template.WEB-INF.includes.a',
                '/WEB-INF/includes/dir/b.gtpl': 'caelyf.template.WEB-INF.includes.dir.b'
        ]

        expected.each { String uri, String loggerName ->
            assert GroovyLogger.forTemplateUri(uri).name == loggerName
        }
    }

    void testGroovletName() {
        def expected = [
                '/upload.groovy':                   'caelyf.groovlet.upload',
                '/WEB-INF/groovy/upload.groovy':    'caelyf.groovlet.upload',
                '/WEB-INF/groovy/ctrl/edit.groovy': 'caelyf.groovlet.ctrl.edit'
        ]

        expected.each { String uri, String loggerName ->
            assert GroovyLogger.forGroovletUri(uri).name == loggerName
        }
    }
}

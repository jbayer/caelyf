package groovyx.caelyf.cache

import groovyx.caelyf.CaelyfCategory
import groovyx.caelyf.routes.Route

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.servlet.RequestDispatcher
import javax.servlet.ServletResponse
import javax.servlet.ServletRequest
import groovyx.caelyf.routes.HttpMethod
import groovyx.caelyf.routes.RedirectionType

import groovyx.caelyf.cache.CachedResponse.CustomServletOutputStream

/**
 * Cache handler tests
 *
 * @author Guillaume Laforge
 */
class CacheHandlerTest extends GroovyTestCase {

    private recorder = []

    private String dateBefore = "Sat, 29 Oct 1994 19:43:31 GMT"
    private String dateAfter  = "Fri, 29 Oct 2094 19:43:31 GMT"

    private String uri = "/index"

    private request = [
            getRequestURI: { -> recorder << "req.getRequestURI"; uri },
            getQueryString: { -> recorder << "req.getQueryString"; "" },
            getRequestDispatcher: { String s -> recorder << "req.getRequestDispatcher"; requestDispatcher },
            getHeader: { String h -> recorder << "req.getHeader"; dateAfter },
            toString: { -> "mock request" },
            setAttribute: { String name, val -> }
    ] as HttpServletRequest

    private requestDispatcher = [
            forward: { ServletRequest servletRequest, ServletResponse servletResponse -> recorder << "reqDisp.forward" }
    ] as RequestDispatcher

    private stream = new OutputStream() {
        void write(byte[] bytes) { }
        void write(byte[] bytes, int i, int i1) { }
        void write(int i) { }
    }
    private output = new CustomServletOutputStream(output: stream)

    private response = [
            addHeader: { String h, String v -> recorder << "resp.addHeader" },
            getContentType: { -> recorder << "resp.getContentType"; "text/html" },
            setContentType: { String ct -> recorder << "resp.setContentType" },
            getOutputStream: { -> recorder << "resp.getOutputStream"; output },
            sendError: { int errCode -> recorder << "resp.sendError" },
            setHeader: { k, v -> recorder << "resp.setHeader" }
    ] as HttpServletResponse

    // TODO fix test with Redis caching
    void _testClearCacheForUri() {
        def memcache = MemcacheServiceFactory.memcacheService
        use(CaelyfCategory) {
            memcache['content-for-/photos'] = '1234'
            memcache['content-type-for-/photos'] = 'text/html'
            memcache['last-modified-/photos'] = new Date().time.toString()

            memcache.clearCacheForUri('/photos')

            assert memcache['content-for-/photos'] == null
            assert memcache['content-type-for-/photos'] == null
            assert memcache['last-modified-/photos'] == null
        }
    }

    void testCacheServingWithoutCaching() {
        def route = new Route(uri, "/index.groovy")
        CacheHandler.serve route, request, response

        assert recorder == ['req.getRequestURI', 'req.getQueryString', 'req.getRequestURI', 'req.getRequestDispatcher', 'reqDisp.forward']
    }

    void testCacheServingWithCaching() {
        def route = new Route(uri, "/index.groovy", HttpMethod.ALL, RedirectionType.FORWARD, null, null, 100)
        CacheHandler.serve route, request, response

        assert recorder == ['req.getRequestURI', 'req.getQueryString', 'req.getRequestURI', 'req.getHeader', 'resp.addHeader',
                'resp.addHeader', 'resp.addHeader', 'req.getRequestDispatcher', 'reqDisp.forward',
                'resp.getContentType', 'resp.getContentType', 'resp.setContentType', 'resp.getOutputStream']
    }

    // TODO fix test with Redis caching
    void _testCacheServingWithLastModified() {
        def memcache = MemcacheServiceFactory.memcacheService
        memcache.put("last-modified-$uri".toString(), dateBefore)
        def route = new Route(uri, "/index.groovy", HttpMethod.ALL, RedirectionType.FORWARD, null, null, 100)
        CacheHandler.serve route, request, response

        assert recorder == ['req.getRequestURI', 'req.getQueryString', 'req.getRequestURI', 'req.getHeader', 'resp.sendError', 'resp.setHeader']
    }

    // TODO fix test with Redis caching
    void _testCacheServingWithCachingAndInCache() {
        def memcache = MemcacheServiceFactory.memcacheService
        memcache.put("content-for-$uri".toString(), "Hello")
        memcache.put("content-type-for-$uri".toString(), "text/html")
        def route = new Route(uri, "/index.groovy", HttpMethod.ALL, RedirectionType.FORWARD, null, null, 100)
        CacheHandler.serve route, request, response

        assert recorder == ['req.getRequestURI', 'req.getQueryString', 'req.getRequestURI', 'req.getHeader', 'resp.setContentType', 'resp.getOutputStream']
    }
}

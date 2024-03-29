/*
 * Copyright 2009-2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package groovyx.caelyf.cache

import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpServletRequest
import java.text.SimpleDateFormat
import groovyx.caelyf.routes.Route
import groovyx.caelyf.logging.GroovyLogger
import static groovyx.caelyf.cache.RedisHolder.getRedis

/**
 * Class handling the caching of the pages
 *
 * @author Guillaume Laforge
 */
class CacheHandler {

    // Date formatter for caching headers date creation
    private static final SimpleDateFormat httpDateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US)

    static {
        httpDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"))
    }

    private static final GroovyLogger log = new GroovyLogger("caelyf.cache")

    static void clearCacheForUri(String uri) {
        redis.del(uri)
    }

    static void serve(Route route, HttpServletRequest request, HttpServletResponse response) {
        log.config "Serving for route $route"

        def requestURI = request.requestURI
        def uri = requestURI + (request.queryString ? "?$request.queryString" : "")

        log.config "Request URI to cache: $uri"

        def result = route.forUri(request)

        if (route.cacheExpiration > 0) {
            log.config "Route cacheable"

            def ifModifiedSince = request.getHeader("If-Modified-Since")
            // if an "If-Modified-Since" header is present in the incoming request
            if (ifModifiedSince) {
                log.config "If-Modified-Since header present"

                def sinceDate = httpDateFormat.parse(ifModifiedSince)
                String lastModifiedString = redis.hget(uri, 'last-modified')
                if (lastModifiedString && httpDateFormat?.parse(lastModifiedString).before(sinceDate)) {
                    log.config "Sending NOT_MODIFIED"

                    response.sendError HttpServletResponse.SC_NOT_MODIFIED
                    response.setHeader("Last-Modified", ifModifiedSince)
                    return
                }
            }
            serveAndCacheOrServeFromCache(request, response, result.destination, uri, route.cacheExpiration)
        } else {
            log.config "Route not cacheable"

            request.getRequestDispatcher(result.destination).forward request, response
        }
    }

    static private serveAndCacheOrServeFromCache(HttpServletRequest request, HttpServletResponse response, String destination, String uri, int cacheExpiration) {
        log.config "Serve and/or cache for URI $uri"

        def uriBytes = uri.getBytes('UTF-8')
        def contentKeyBytes = 'content'.getBytes('UTF-8')

        def type = redis.hget(uri, 'content-type')
        def content = redis.hget(uriBytes, contentKeyBytes)

        // the resource is still present in the cache
        if (content && type) {
            log.config "Content present in the cache, outputing content-type and content"

            // if it's in the cache, return the page from the cache
            response.contentType = type
            response.outputStream << content
        } else { // serve and cache
            log.config "Not in the cache"

            def now = new Date()
            def lastModifiedString = httpDateFormat.format(now)

            // specify caching durations
            response.addHeader "Cache-Control", "max-age=${cacheExpiration}"
            response.addHeader "Last-Modified", lastModifiedString
            response.addHeader "Expires", httpDateFormat.format(new Date(now.time + cacheExpiration * 1000))
            //response.addHeader "ETag", "\"\""

            log.config "Wrapping a response for caching and forwarding to resource to be cached"
            def cachedResponse = new CachedResponse(response)
            request.getRequestDispatcher(destination).forward request, cachedResponse
            def byteArray = cachedResponse.output.toByteArray()

            log.config "Byte array of wrapped response will be put in cache: ${new String(byteArray)}"

            redis.hset(uri, 'content-type', cachedResponse.contentType)
            redis.hset(uri, 'last-modified', lastModifiedString)
            redis.hset(uriBytes, contentKeyBytes, byteArray)
            redis.expire(uri, cacheExpiration)

            log.config "Serving content-type and byte array"

            // output back to the screen
            response.contentType = cachedResponse.contentType
            response.outputStream << byteArray
        }
    }
}

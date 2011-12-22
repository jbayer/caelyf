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
package groovyx.caelyf

import groovyx.caelyf.logging.LoggerAccessor
import groovyx.caelyf.cache.RedisHolder

/**
 * Class responsible for adding adding various services and variables into the binding of Groovlets and Templates.
 *
 * @author Marcel Overdijk
 * @author Guillaume Laforge
 * @author Benjamin Muschko
 */
class BindingEnhancer {

    /**
     * Bind the various services and variables
     *
     * @param binding Binding in which to bind the various services and variables
     */
    static void bind(Binding binding) {
        // Tells whether the application is running in local development mode
        // or is deployed on CloudFoundry
        binding.setVariable("localMode", System.getenv('VCAP_SERVICES') == null)

        binding.setVariable("app", getApp())

        // Add a logger variable to easily access any logger
        binding.setVariable("logger", getLogger())

        // Add the Redis Jedis service
        binding.setVariable("redis", RedisHolder.getRedis())
    }

    static Map getApp() {
        [
            caelyf: [
                version: '0.1'
            ]
        ]
    }

    static LoggerAccessor getLogger() {
        new LoggerAccessor()
    }
}

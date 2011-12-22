package groovyx.caelyf.cache

import redis.clients.jedis.Jedis
import groovy.json.JsonSlurper

/**
 * 
 * @author Guillaume Laforge
 */
class RedisHolder {
    private static Jedis redis = null
    
    private static Closure redisCredentials = { ->
        def vcapEnvVariable = System.getenv('VCAP_SERVICES')

        if (vcapEnvVariable) {
            try {
                def json = new JsonSlurper().parseText(vcapEnvVariable)
                def redisServiceCred = json.'redis-2.2'[0].credentials

                return [
                        host:       redisServiceCred.hostname,
                        port:       redisServiceCred.port,
                        password:   redisServiceCred.password
                ]
            } catch (any) {
                throw new RuntimeException("Impossible to get redis credentials (${any.message})")
            }
        }
        return [:]
    }.memoize()

    static Jedis getRedis() {
        if (this.redis) {
            this.redis.client.ping()
        }

        def vcapEnvVariable = System.getenv('VCAP_SERVICES')

        if (vcapEnvVariable) {
            def creds = redisCredentials()

            this.redis = new Jedis(creds.host, creds.port)
            this.redis.auth(creds.password)
        } else {
            this.redis = new Jedis("localhost")
        }

        return this.redis
    }
}

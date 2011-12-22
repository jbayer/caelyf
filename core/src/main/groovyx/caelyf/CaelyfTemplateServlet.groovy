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

import groovy.servlet.ServletBinding
import groovy.servlet.TemplateServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import groovyx.caelyf.plugins.PluginsHandler
import javax.servlet.ServletConfig
import groovyx.caelyf.logging.GroovyLogger

/**
 * The Caelyf template servlet extends Groovy's own template servlet
 *
 * @author Marcel Overdijk
 * @author Guillaume Laforge
 *
 * @see groovy.servlet.TemplateServlet
 */
class CaelyfTemplateServlet extends TemplateServlet {

    @Override
    void init(ServletConfig config) {
        super.init(config)
    }

    /**
     * Injects the default variables and services in the binding of templates
     * as well as the variables contributed by plugins, and a logger.
     *
     * @param binding the binding to enhance
     */
    @Override
    protected void setVariables(ServletBinding binding) {
        BindingEnhancer.bind(binding)
        PluginsHandler.instance.enrich(binding)
        binding.setVariable("log", GroovyLogger.forTemplateUri(super.getScriptUri(binding.request)))
    }

    /**
     * Service incoming requests applying the <code>CaelyfCategory</code>
     * and the other categories defined by the installed plugins.
     *
     * @param request the request
     * @param response the response
     * @throws IOException when anything goes wrong
     */
    @Override
    void service(HttpServletRequest request, HttpServletResponse response) throws IOException {
        use([CaelyfCategory, * PluginsHandler.instance.categories]) {
            PluginsHandler.instance.executeBeforeActions(request, response)
            super.service(request, response)
            PluginsHandler.instance.executeAfterActions(request, response)
        }
    }
}
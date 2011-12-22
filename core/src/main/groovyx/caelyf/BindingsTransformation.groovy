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
import org.codehaus.groovy.ast.ASTNode
import org.codehaus.groovy.ast.AnnotationNode
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.MethodNode
import org.codehaus.groovy.control.CompilePhase
import org.codehaus.groovy.control.SourceUnit
import org.codehaus.groovy.transform.ASTTransformation
import org.codehaus.groovy.transform.GroovyASTTransformation
import java.lang.reflect.Modifier
import org.codehaus.groovy.ast.ClassHelper
import org.codehaus.groovy.ast.Parameter
import org.codehaus.groovy.ast.stmt.BlockStatement
import org.codehaus.groovy.ast.stmt.ReturnStatement
import org.codehaus.groovy.ast.expr.MethodCallExpression
import org.codehaus.groovy.ast.expr.ClassExpression
import org.codehaus.groovy.ast.expr.TupleExpression
import redis.clients.jedis.Jedis
import groovyx.caelyf.cache.RedisHolder

/**
 * This Groovy AST Transformation is a local transformation which is triggered by the Groovy compiler
 * when developers annotate their classes with the <code>@CaelyfBindings</code> annotation.
 * The transformation will inject the various variables and services usually injected in Groovlets and templates.
 *
 * @author Vladimir Orany
 * @author Guillaume Laforge
 */
@GroovyASTTransformation(phase = CompilePhase.SEMANTIC_ANALYSIS)
class BindingsTransformation implements ASTTransformation {
	
	void visit(ASTNode[] nodes, SourceUnit unit) {
		if (nodes.length != 2 || !(nodes[0] instanceof AnnotationNode) || !(nodes[1] instanceof ClassNode)) {
			println "Internal error: expecting [AnnotationNode, ClassNode] but got: ${Arrays.asList(nodes)}"
		}

		ClassNode parent = (ClassNode) nodes[1]

		addGetterIfNotExists(parent, Boolean,                  "getLocalMode",         BindingEnhancer,      "getLocalMode")
		addGetterIfNotExists(parent, Map,                      "getApp",               BindingEnhancer,      "getApp")
		addGetterIfNotExists(parent, LoggerAccessor,           "getLogger",            BindingEnhancer,      "getLogger")
		addGetterIfNotExists(parent, Jedis,                    "getRedis",             RedisHolder,          "getRedis")
	}

	private void addGetterIfNotExists(ClassNode parent, Class serviceClass, String getterName, Class factoryClass, String factoryMethodName) {
		if(!parent.getGetterMethod(getterName)) {
			parent.addMethod makeServiceGetter(serviceClass, getterName, factoryClass, factoryMethodName)
		}
	}
	
	private MethodNode makeServiceGetter(Class serviceClass, String accessorName, Class factoryClass, String factoryMethodName) {
        def returnType  = ClassHelper.make(serviceClass).plainNodeReference
        def factoryType = ClassHelper.make(factoryClass).plainNodeReference

        BlockStatement block = new BlockStatement()
        block.addStatement(new ReturnStatement(new MethodCallExpression(
                new ClassExpression(factoryType), factoryMethodName, new TupleExpression()
        )))
        
        new MethodNode(
                accessorName, 
                Modifier.PRIVATE | Modifier.STATIC, 
                returnType, 
                Parameter.EMPTY_ARRAY,
                ClassNode.EMPTY_ARRAY,
                block
        )
	}
}

/*
 * Copyright 2012 Shared Learning Collaborative, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.slc.sli.api.security.context.traversal.graph;

import org.slc.sli.api.security.context.resolver.EntityContextResolver;

import java.util.List;

/**
 * Simple container for graph objects.
 */
public final class SecurityNodeBuilder {
    
    private SecurityNode node;
    
    public static SecurityNodeBuilder buildNode(String nodeName) {
        return new SecurityNodeBuilder(nodeName, nodeName);
    }
    
    public static SecurityNodeBuilder buildNode(String nodeName, String nodeType) {
        return new SecurityNodeBuilder(nodeName, nodeType);
    }
    
    private SecurityNodeBuilder(String name, String type) {
        node = new SecurityNode(name, type);
    }
    
    /**
     * Looks for field in toEntity unless associationNode is not empty
     * 
     * @param toEntity
     * @param withField
     * @param associationNode
     * @return
     */
    public SecurityNodeBuilder addConnection(String toEntity, String withField, String associationNode) {
        SecurityNodeConnection connection = new SecurityNodeConnection(toEntity, withField, associationNode);
        node.addConnection(connection);
        return this;
    }
    
    public SecurityNodeBuilder addConnection(String toEntity, String withField, String associationNode,
            NodeFilter filter) {
        SecurityNodeConnection connection = new SecurityNodeConnection(toEntity, withField, associationNode, filter);
        node.addConnection(connection);
        return this;
    }
    public SecurityNodeBuilder addConnection(String toEntity, String withField, String associationNode,
                                             NodeFilter filter, NodeAggregator aggregator) {
        SecurityNodeConnection connection = new SecurityNodeConnection(toEntity, withField, associationNode, filter, aggregator);
        node.addConnection(connection);
        return this;
    }

    public SecurityNodeBuilder addConnection(String toEntity, String withField) {
        SecurityNodeConnection connection = new SecurityNodeConnection(toEntity, withField);
        node.addConnection(connection);
        return this;
    }
    
    public SecurityNode construct() {
        return node;
    }
    
    public SecurityNodeBuilder addLocalReference(String toEntity, String withField) {
        node.addConnection(new SecurityNodeConnection(toEntity, withField, true));
        return this;
    }
    public SecurityNodeBuilder addConnection(String toEntity, String withField, String associationNode,
                                             List<NodeFilter> filter) {
        SecurityNodeConnection connection = new SecurityNodeConnection(toEntity, withField, associationNode, filter);
        node.addConnection(connection);
        return this;
    }

    public SecurityNodeBuilder addConnection(String toEntity, EntityContextResolver resolver) {
        node.addConnection(new SecurityNodeConnection(toEntity, resolver));
        return this;
    }
}

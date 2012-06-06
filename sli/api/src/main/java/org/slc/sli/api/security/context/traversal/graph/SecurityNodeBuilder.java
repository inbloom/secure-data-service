package org.slc.sli.api.security.context.traversal.graph;

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
}

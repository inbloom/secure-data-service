package org.slc.sli.api.security.context.traversal.graph;


import java.util.HashMap;
import java.util.Map;

/**
 * Simple container for graph objects.
 */
public final class SecurityNodeBuilder {

    private SecurityNode node;

    public static SecurityNodeBuilder buildNode(String nodeName) {
        return new SecurityNodeBuilder(nodeName);
    }

    private SecurityNodeBuilder(String name) {
        node = new SecurityNode(name);
    }



    public SecurityNodeBuilder addConnection(String toEntity, String withField, String associationNode) {
        Map<String, String> connection = new HashMap<String, String>();
        connection.put(SecurityNode.CONNECTION_ENTITY, toEntity);
        connection.put(SecurityNode.CONNECTION_FIELD_NAME, withField);
        connection.put(SecurityNode.CONNECTION_ASSOCIATION, associationNode);
        node.addConnection(connection);
        return this;
    }

    public SecurityNode construct() {
        return node;
    }



}

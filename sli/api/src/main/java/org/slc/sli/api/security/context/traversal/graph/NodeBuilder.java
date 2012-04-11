package org.slc.sli.api.security.context.traversal.graph;


import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;

/**
 * Simple container for graph objects.
 */
public final class NodeBuilder {

    private Node node;

    public static NodeBuilder buildNode(String nodeName) {
        return new NodeBuilder(nodeName);
    }

    private NodeBuilder(String name) {
        node = new Node(name);
    }



    public NodeBuilder addConnection(String toEntity, String withField, String associationNode) {
        Map<String, String> connection = new HashMap<String, String>();
        connection.put("entity", toEntity);
        connection.put("fieldName", withField);
        connection.put("associationNode", associationNode);
        node.addConnection(connection);
        return this;
    }

    public Node construct() {
        return node;
    }



}

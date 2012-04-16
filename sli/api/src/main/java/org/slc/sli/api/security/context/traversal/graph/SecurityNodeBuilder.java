package org.slc.sli.api.security.context.traversal.graph;



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
        SecurityNodeConnection connection = new SecurityNodeConnection(toEntity, withField, associationNode);
        node.addConnection(connection);
        return this;
    }

    public SecurityNode construct() {
        return node;
    }



}

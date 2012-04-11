package org.slc.sli.api.security.context.traversal.graph;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Node representation
 */
public class Node {
    private Set<Map<String, String>> connections;
    private String name;

    public Node(String name) {
        this.name = name;
        connections = new HashSet<Map<String, String>>();
    }

    public Set<Map<String, String>> getConnections() {
        return connections;
    }

    public boolean isConnectedTo(String entityName) {
        for (Map<String, String> connection : connections) {
            if (connection.get("entity").equals(entityName))
                return true;
        }
        return false;
    }

    public Map<String, String> getConnectionForEntity(String entityName) {
        for (Map<String, String> connection : connections) {
            if (connection.get("entity").equals(entityName))
                return connection;
        }
        return null;
    }

    public void addConnection (Map<String, String> connection) {
        connections.add(connection);
    }

    public String getName() {
        return name;
    }

    public void setConnections(Set<Map<String, String>> connections) {
        this.connections = connections;
    }

    public void setName(String name) {
        this.name = name;
    }
}

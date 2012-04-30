package org.slc.sli.api.security.context.traversal.graph;

import java.util.HashSet;
import java.util.Set;

/**
 * Node representation
 */
public class SecurityNode {
    Set<SecurityNodeConnection> connections;
    private String name;
    private String type;

    public SecurityNode(String name) {
        this.name = name;
        this.type = name;
        this.connections = new HashSet<SecurityNodeConnection>();
    }

    public SecurityNode(String name, String type) {
        this.name = name;
        this.type = type;
        this.connections = new HashSet<SecurityNodeConnection>();
    }

    public Set<SecurityNodeConnection> getConnections() {
        return this.connections;
    }

    public boolean isConnectedTo(String entityName) {
        for (SecurityNodeConnection connection : connections) {
            if (connection.getConnectionTo().equals(entityName)) {
                return true;
            }
        }
        return false;
    }

    public SecurityNodeConnection getConnectionForEntity(String entityName) {
        for (SecurityNodeConnection connection : connections) {
            if (connection.getConnectionTo().equals(entityName)) {
                return connection;
            }
        }
        return null;
    }

    public void addConnection(SecurityNodeConnection connection) {
        connections.add(connection);
    }

    public String getName() {
        return name;
    }

    public void setConnections(Set<SecurityNodeConnection> connections) {
        this.connections = connections;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}

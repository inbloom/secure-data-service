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

import java.util.ArrayList;
import java.util.List;

/**
 * Node representation
 */
public class SecurityNode {
    List<SecurityNodeConnection> connections;
    private String name;
    private String type;

    public SecurityNode(String name) {
        this.name = name;
        this.type = name;
        this.connections = new ArrayList<SecurityNodeConnection>();
    }

    public SecurityNode(String name, String type) {
        this.name = name;
        this.type = type;
        this.connections = new ArrayList<SecurityNodeConnection>();
    }

    public List<SecurityNodeConnection> getConnections() {
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

    public void setConnections(List<SecurityNodeConnection> connections) {
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

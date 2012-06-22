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

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Basic Node test.
 */
public class SecurityNodeTest {

    private SecurityNode node;

    @Before
    public void setUp() throws Exception {
        node = SecurityNodeBuilder.buildNode("simple").addConnection("SomeWhere", "SomeWhereId", null).construct();
    }

    @After
    public void tearDown() throws Exception {
        node = null;
    }

    @Test
    public void testGetConnections() throws Exception {
        List<SecurityNodeConnection> connections = node.getConnections();
        assertTrue(connections.size() == 1);

    }

    @Test
    public void testIsConnectedTo() throws Exception {
        assertFalse(node.isConnectedTo(":KDSLJF"));
        assertTrue(node.isConnectedTo("SomeWhere"));
        assertFalse(node.isConnectedTo(null));

    }

    @Test
    public void testGetConnectionForEntity() throws Exception {
        SecurityNodeConnection connection = null;
        connection = node.getConnectionForEntity("asdf");
        assertTrue(connection == null);
        connection = node.getConnectionForEntity(null);
        assertTrue(connection == null);
        connection = node.getConnectionForEntity("SomeWhere");
        assertTrue(connection != null);

    }
}

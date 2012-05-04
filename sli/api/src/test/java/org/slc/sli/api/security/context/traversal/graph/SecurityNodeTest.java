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

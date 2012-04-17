package org.slc.sli.api.security.context.traversal.graph;

import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for security node connection
 * 
 * @author rlatta
 * 
 */
public class SecurityNodeConnectionTest {
    
    private SecurityNodeConnection connection;
    
    @Before
    public void setUp() throws Exception {
        connection = new SecurityNodeConnection("Test Entity", "Test Field", "Test Association");
    }
    
    @After
    public void tearDown() throws Exception {
        connection = null;
    }

    @Test
    public void testGetFieldName() {
        assertTrue(connection.getFieldName().equals("Test Field"));
    }
    
    @Test
    public void testSetFieldName() {
        connection.setFieldName("Waffles");
        assertTrue(connection.getFieldName().equals("Waffles"));
    }
    
    @Test
    public void testGetConnectionTo() {
        assertTrue(connection.getConnectionTo().equals("Test Entity"));
    }
    
    @Test
    public void testSetConnectionTo() {
        connection.setConnectionTo("Waffles");
        assertTrue(connection.getConnectionTo().equals("Waffles"));
    }
    
    @Test
    public void testGetAssociationNode() {
        assertTrue(connection.getAssociationNode().equals("Test Association"));
    }
    
    @Test
    public void testSetAssociationNode() {
        connection.setAssociationNode("Waffles");
        assertTrue(connection.getAssociationNode().equals("Waffles"));
    }
    
    @Test
    public void testSecurityNodeConnection() {
        connection = new SecurityNodeConnection();
        assertTrue(connection.getConnectionTo() != null);
        connection = new SecurityNodeConnection("Waffles", "Waffles", "waffle");
        assertTrue(connection.getConnectionTo() != null);
    }

    
}

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
    //   connection = new SecurityNodeConnection();
    //    assertTrue(connection.getConnectionTo() != null);
    //    connection = new SecurityNodeConnection("Waffles", "Waffles", "waffle");
    //    assertTrue(connection.getConnectionTo() != null);
    }


}

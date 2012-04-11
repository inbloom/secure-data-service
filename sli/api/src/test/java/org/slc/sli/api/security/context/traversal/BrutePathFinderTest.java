package org.slc.sli.api.security.context.traversal;

import org.junit.Before;
import org.junit.Test;
import org.slc.sli.api.security.context.traversal.graph.Node;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static junit.framework.Assert.assertTrue;

/**
 * Test to see if we can get from one node to another.
 */
public class BrutePathFinderTest {

    @Autowired
    private BrutePathFinder pathFinder;

    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void testGetSimplePath() throws Exception {
        List<Node> path = null;
        path = pathFinder.find("teacher", "student");
        assertTrue(path.get(0).getName().equals("teacher"));
        assertTrue(path.get(1).getName().equals("section"));
        assertTrue(path.get(2).getName().equals("student"));
    }
}

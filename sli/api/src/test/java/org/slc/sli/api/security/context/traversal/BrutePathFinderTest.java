package org.slc.sli.api.security.context.traversal;

import static junit.framework.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slc.sli.api.security.context.traversal.graph.SecurityNode;
import org.slc.sli.api.test.WebContextTestExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

/**
 * Test to see if we can get from one node to another.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class })
public class BrutePathFinderTest {

    @Autowired
    private BrutePathFinder pathFinder;

    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void testGetSimplePath() throws Exception {
        List<SecurityNode> path = null;
        path = pathFinder.find("teacher", "student");
        assertTrue(path.size() == 3);
        assertTrue(path.get(0).getName().equals("teacher"));
        assertTrue(path.get(1).getName().equals("section"));
        assertTrue(path.get(2).getName().equals("student"));
    }

    @Test
    public void testGet2PartPath() throws Exception {
        List<SecurityNode> path = null;
        path = pathFinder.find("teacher", "section");
        assertTrue(path.size() == 2);
        assertTrue(path.get(0).getName().equals("teacher"));
        assertTrue(path.get(1).getName().equals("section"));
    }

    @Test
    public void testReverseFind() throws Exception {
        List<SecurityNode> path = null;
        path = pathFinder.find("student", "teacher");
        assertTrue(path.size() == 3);
        assertTrue(path.get(0).getName().equals("student"));
        assertTrue(path.get(1).getName().equals("section"));
        assertTrue(path.get(2).getName().equals("teacher"));
    }
}

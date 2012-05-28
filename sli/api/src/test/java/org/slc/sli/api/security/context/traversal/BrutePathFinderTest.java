package org.slc.sli.api.security.context.traversal;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slc.sli.api.security.context.traversal.graph.SecurityNode;
import org.slc.sli.api.test.WebContextTestExecutionListener;
import org.slc.sli.common.constants.EntityNames;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import java.util.List;

import static junit.framework.Assert.assertTrue;

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
    private List<SecurityNode> path;

    @Before
    public void setUp() throws Exception {
        path = null;
    }
    
    @After
    public void tearDown() throws Exception {
        path = null;
    }

    @Test
    public void testGetSimplePath() throws Exception {
        path = pathFinder.find("teacher", "student");
        assertTrue(path.size() == 3);
        assertTrue(path.get(0).getName().equals("teacher"));
        assertTrue(path.get(1).getName().equals("section"));
        assertTrue(path.get(2).getName().equals("student"));
    }

    @Test
    public void testGetSimplePath3() throws Exception {
        path = pathFinder.getPreDefinedPath("teacher", "school");
        assertTrue(path.size() == 2);
        assertTrue(path.get(0).getName().equals("teacher"));
        assertTrue(path.get(1).getName().equals("school"));
    }


    @Test
    public void testGet2PartPath() throws Exception {
        path = pathFinder.find("teacher", "section");
        assertTrue(path.size() == 2);
        assertTrue(path.get(0).getName().equals("teacher"));
        assertTrue(path.get(1).getName().equals("section"));
    }
    
    
    @Test
    public void testReverseFind() throws Exception {
        path = pathFinder.find("student", "teacher");
        assertTrue(path.size() == 3);
        assertTrue(path.get(0).getName().equals("student"));
        assertTrue(path.get(1).getName().equals("section"));
        assertTrue(path.get(2).getName().equals("teacher"));
    }


    @Test
    public void testGetPredefinedTest() throws Exception {
        path = pathFinder.getPreDefinedPath("teacher", "teacher");
        assertTrue(path.size() == 3);
        path = pathFinder.getPreDefinedPath("waffles", "pancakes");
        assertTrue(path.size() == 0);
        path = null;
        path = pathFinder.getPreDefinedPath("teacher", "course");
        assertTrue(path.size() == 5);
        assertTrue(path.get(4).getName().equals(EntityNames.COURSE));
    }
    
}

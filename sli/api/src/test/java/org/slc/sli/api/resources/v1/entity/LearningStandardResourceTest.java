package org.slc.sli.api.resources.v1.entity;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * Unit tests for LearningStandardResource
 * 
 * @author Ryan Farris <rfarris@wgen.net>
 * 
 */
@RunWith(JUnit4.class)
public class LearningStandardResourceTest {
    
    @Test(expected = java.lang.UnsupportedOperationException.class)
    public void testReadAll() {
        new LearningStandardResource().readAll(0, 0, null, null);
    }
    
    @Test(expected = java.lang.UnsupportedOperationException.class)
    public void testRead() {
        new LearningStandardResource().read(null, null, null);
    }
    
    @Test(expected = java.lang.UnsupportedOperationException.class)
    public void testCreate() {
        new LearningStandardResource().create(null, null, null);
    }
    
    @Test(expected = java.lang.UnsupportedOperationException.class)
    public void testDelete() {
        new LearningStandardResource().delete(null, null, null);
    }
}

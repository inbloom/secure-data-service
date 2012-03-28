package org.slc.sli.api.resources.v1.entity;

import static org.junit.Assert.assertEquals;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

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
    
    @Test
    public void testReadAll() {
        Response res = new LearningStandardResource().readAll(0, 0, null, null);
        assertEquals(Status.NOT_FOUND.getStatusCode(), res.getStatus());
    }
    
    @Test
    public void testRead() {
        Response res = new LearningStandardResource().read(null, null, null);
        assertEquals(Status.NOT_FOUND.getStatusCode(), res.getStatus());
    }
    
    @Test
    public void testCreate() {
        Response res = new LearningStandardResource().create(null, null, null);
        assertEquals(Status.NOT_FOUND.getStatusCode(), res.getStatus());
    }
    
    @Test
    public void testDelete() {
        Response res = new LearningStandardResource().delete(null, null, null);
        assertEquals(Status.NOT_FOUND.getStatusCode(), res.getStatus());
    }
}

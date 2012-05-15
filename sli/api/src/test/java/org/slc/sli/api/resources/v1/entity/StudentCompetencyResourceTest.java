package org.slc.sli.api.resources.v1.entity;

import static org.junit.Assert.assertEquals;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import org.slc.sli.api.config.BasicDefinitionStore;

/**
 * Unit tests for StudentCompetencyResource
 *
 * @author chung
 *
 */
@RunWith(JUnit4.class)
public class StudentCompetencyResourceTest {
    StudentCompetencyResource studentCompetencyResource = new StudentCompetencyResource(new BasicDefinitionStore());

    @Test
    public void testReadAll() {
        Response res = studentCompetencyResource.readAll(0, 0, null, null);
        assertEquals(Status.NOT_FOUND.getStatusCode(), res.getStatus());
    }

    @Test
    public void testRead() {
        Response res = studentCompetencyResource.read(null, null, null);
        assertEquals(Status.NOT_FOUND.getStatusCode(), res.getStatus());
    }

    @Test
    public void testCreate() {
        Response res = studentCompetencyResource.create(null, null, null);
        assertEquals(Status.NOT_FOUND.getStatusCode(), res.getStatus());
    }

    @Test
    public void testDelete() {
        Response res = studentCompetencyResource.delete(null, null, null);
        assertEquals(Status.NOT_FOUND.getStatusCode(), res.getStatus());
    }

    @Test
    public void testUpdate() {
        Response res = studentCompetencyResource.update(null, null, null, null);
        assertEquals(Status.NOT_FOUND.getStatusCode(), res.getStatus());
    }
}

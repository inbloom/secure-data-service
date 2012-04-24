package org.slc.sli.api.resources.v1.entity;

import static org.junit.Assert.assertEquals;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import org.slc.sli.api.config.BasicDefinitionStore;

/**
 * Unit tests for StudentCompetencyObjectiveResource
 *
 * @author chung
 *
 */
@RunWith(JUnit4.class)
public class StudentCompetencyObjectiveResourceTest {
    StudentCompetencyObjectiveResource studentCompetencyObjectiveResource = new StudentCompetencyObjectiveResource(new BasicDefinitionStore());

    @Test
    public void testReadAll() {
        Response res = studentCompetencyObjectiveResource.readAll(0, 0, null, null);
        assertEquals(Status.NOT_FOUND.getStatusCode(), res.getStatus());
    }

    @Test
    public void testRead() {
        Response res = studentCompetencyObjectiveResource.read(null, null, null);
        assertEquals(Status.NOT_FOUND.getStatusCode(), res.getStatus());
    }

    @Test
    public void testCreate() {
        Response res = studentCompetencyObjectiveResource.create(null, null, null);
        assertEquals(Status.NOT_FOUND.getStatusCode(), res.getStatus());
    }

    @Test
    public void testDelete() {
        Response res = studentCompetencyObjectiveResource.delete(null, null, null);
        assertEquals(Status.NOT_FOUND.getStatusCode(), res.getStatus());
    }

    @Test
    public void testUpdate() {
        Response res = studentCompetencyObjectiveResource.update(null, null, null, null);
        assertEquals(Status.NOT_FOUND.getStatusCode(), res.getStatus());
    }
}

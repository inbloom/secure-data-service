package org.slc.sli.api.resources.v1.entity;

import static org.junit.Assert.assertEquals;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import org.slc.sli.api.config.BasicDefinitionStore;

/**
 * Unit tests for CompetencyLevelDescriptorTypeResource
 *
 * @author chung
 *
 */
@RunWith(JUnit4.class)
public class CompetencyLevelDescriptorTypeResourceTest {
    CompetencyLevelDescriptorTypeResource competencyLevelDescriptorTypeResource = new CompetencyLevelDescriptorTypeResource(new BasicDefinitionStore());

    @Test
    public void testReadAll() {
        Response res = competencyLevelDescriptorTypeResource.readAll(0, 0, null, null);
        assertEquals(Status.NOT_FOUND.getStatusCode(), res.getStatus());
    }

    @Test
    public void testRead() {
        Response res = competencyLevelDescriptorTypeResource.read(null, null, null);
        assertEquals(Status.NOT_FOUND.getStatusCode(), res.getStatus());
    }

    @Test
    public void testCreate() {
        Response res = competencyLevelDescriptorTypeResource.create(null, null, null);
        assertEquals(Status.NOT_FOUND.getStatusCode(), res.getStatus());
    }

    @Test
    public void testDelete() {
        Response res = competencyLevelDescriptorTypeResource.delete(null, null, null);
        assertEquals(Status.NOT_FOUND.getStatusCode(), res.getStatus());
    }

    @Test
    public void testUpdate() {
        Response res = competencyLevelDescriptorTypeResource.update(null, null, null, null);
        assertEquals(Status.NOT_FOUND.getStatusCode(), res.getStatus());
    }
}

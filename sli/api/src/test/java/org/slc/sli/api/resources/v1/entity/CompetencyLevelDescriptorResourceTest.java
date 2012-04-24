package org.slc.sli.api.resources.v1.entity;

import static org.junit.Assert.assertEquals;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import org.slc.sli.api.config.BasicDefinitionStore;

/**
 * Unit tests for CompetencyLevelDescriptorResource
 *
 * @author chung
 *
 */
@RunWith(JUnit4.class)
public class CompetencyLevelDescriptorResourceTest {
    CompetencyLevelDescriptorResource competencyLevelDescriptorResource = new CompetencyLevelDescriptorResource(new BasicDefinitionStore());

    @Test
    public void testReadAll() {
        Response res = competencyLevelDescriptorResource.readAll(0, 0, null, null);
        assertEquals(Status.NOT_FOUND.getStatusCode(), res.getStatus());
    }

    @Test
    public void testRead() {
        Response res = competencyLevelDescriptorResource.read(null, null, null);
        assertEquals(Status.NOT_FOUND.getStatusCode(), res.getStatus());
    }

    @Test
    public void testCreate() {
        Response res = competencyLevelDescriptorResource.create(null, null, null);
        assertEquals(Status.NOT_FOUND.getStatusCode(), res.getStatus());
    }

    @Test
    public void testDelete() {
        Response res = competencyLevelDescriptorResource.delete(null, null, null);
        assertEquals(Status.NOT_FOUND.getStatusCode(), res.getStatus());
    }

    @Test
    public void testUpdate() {
        Response res = competencyLevelDescriptorResource.update(null, null, null, null);
        assertEquals(Status.NOT_FOUND.getStatusCode(), res.getStatus());
    }
}

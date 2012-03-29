package org.slc.sli.api.resources.v1;

import static org.junit.Assert.assertEquals;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;

import org.slc.sli.api.config.EntityDefinition;

/**
 * Tests for CustomEntityResouce
 * 
 * @author Ryan Farris <rfarris@wgen.net>
 * 
 */
@RunWith(JUnit4.class)
public class CustomEntityResourceTest {
    
    CustomEntityResource resource;
    
    @Before
    public void init() {
        String entityId = "TEST-ID";
        EntityDefinition entityDef = Mockito.mock(EntityDefinition.class);
        resource = new CustomEntityResource(entityId, entityDef);
    }
    
    @Test
    public void testRead() {
        Response res = resource.read();
        assertEquals(Status.NOT_FOUND.getStatusCode(), res.getStatus());
    }
    
    @Test
    public void testCreateOrUpdate() {
        Response res = resource.createOrUpdate();
        assertEquals(Status.NOT_FOUND.getStatusCode(), res.getStatus());
    }
    
    @Test
    public void testDelete() {
        Response res = resource.delete();
        assertEquals(Status.NOT_FOUND.getStatusCode(), res.getStatus());
    }
}

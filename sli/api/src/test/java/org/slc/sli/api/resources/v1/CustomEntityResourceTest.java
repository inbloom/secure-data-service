package org.slc.sli.api.resources.v1;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;

import org.slc.sli.api.config.EntityDefinition;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.service.EntityService;

/**
 * Tests for CustomEntityResouce
 * 
 * @author Ryan Farris <rfarris@wgen.net>
 * 
 */
@RunWith(JUnit4.class)
public class CustomEntityResourceTest {
    
    CustomEntityResource resource;
    EntityService service;
    
    @Before
    public void init() {
        String entityId = "TEST-ID";
        EntityDefinition entityDef = Mockito.mock(EntityDefinition.class);
        service = Mockito.mock(EntityService.class);
        Mockito.when(entityDef.getService()).thenReturn(service);
        resource = new CustomEntityResource(entityId, entityDef);
    }
    
    @Test
    public void testRead() {
        Response res = resource.read();
        assertNotNull(res);
        assertEquals(Status.OK.getStatusCode(), res.getStatus());
        Mockito.verify(service).getCustom("TEST-ID");
    }
    
    @Test
    public void testCreateOrUpdate() {
        EntityBody test = new EntityBody();
        Response res = resource.createOrUpdate(test);
        assertNotNull(res);
        assertEquals(Status.NO_CONTENT.getStatusCode(), res.getStatus());
        Mockito.verify(service).createOrUpdateCustom("TEST-ID", test);
    }
    
    @Test
    public void testDelete() {
        Response res = resource.delete();
        assertNotNull(res);
        assertEquals(Status.NO_CONTENT.getStatusCode(), res.getStatus());
        Mockito.verify(service).deleteCustom("TEST-ID");
    }
}

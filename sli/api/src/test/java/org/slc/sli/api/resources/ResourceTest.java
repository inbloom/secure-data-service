package org.slc.sli.api.resources;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import org.slc.sli.api.WebContextTestExecutionListener;
import org.slc.sli.api.representation.CollectionResponse;
import org.slc.sli.api.representation.CollectionResponse.EntityReference;
import org.slc.sli.api.representation.EntityBody;

/**
 * Provides common annotations for any tests that are testing resources that are exposed in a
 * RESTful manner
 * 
 * @author Sean Melody <smelody@wgen.net>
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class })
public class ResourceTest {
    
    @Autowired
    Resource api;
    
    public Map<String, Object> createTestObject() {
        Map<String, Object> entity = new HashMap<String, Object>();
        entity.put("field1", 1);
        entity.put("field2", 2);
        return entity;
    }
    
    @Test
    public void testResorceMethods() {
        // post some data
        Map<String, String> ids = new HashMap<String, String>();
        
        Response createResponse = api.createEntity("students", new EntityBody(createTestObject()));
        assertNotNull(createResponse);
        assertEquals(Status.CREATED.getStatusCode(), createResponse.getStatus());
        List<Object> locationHeaders = createResponse.getMetadata().get("Location");
        assertNotNull(locationHeaders);
        assertEquals(1, locationHeaders.size());
        Pattern regex = Pattern.compile(".+/(\\d+)$");
        Matcher matcher = regex.matcher((String) locationHeaders.get(0));
        matcher.find();
        assertEquals(1, matcher.groupCount());
        ids.put(matcher.group(1), (String) locationHeaders.get(0));
        
        Response createResponse2 = api.createEntity("students", new EntityBody(createTestObject()));
        assertNotNull(createResponse2);
        assertEquals(Status.CREATED.getStatusCode(), createResponse2.getStatus());
        List<Object> locationHeaders2 = createResponse2.getMetadata().get("Location");
        assertNotNull(locationHeaders2);
        assertEquals(1, locationHeaders2.size());
        matcher = regex.matcher((String) locationHeaders2.get(0));
        matcher.find();
        assertEquals(1, matcher.groupCount());
        ids.put(matcher.group(1), (String) locationHeaders2.get(0));
        
        Response createResponse3 = api.createEntity("schools", new EntityBody(createTestObject()));
        assertNotNull(createResponse3);
        assertEquals(Status.CREATED.getStatusCode(), createResponse3.getStatus());
        
        // test the get collection method
        Response studentEntities = api.getCollection("students", 0, 100);
        CollectionResponse response = (CollectionResponse) studentEntities.getEntity();
        assertNotNull(response);
        assertNotNull(response.getEntities());
        assertEquals(2, response.getEntities().size());
        for (EntityReference er : response.getEntities()) {
            assertNotNull(er.getId());
            assertNotNull(er.getLink());
            assertEquals("student", er.getLink().getType());
            assertEquals("self", er.getLink().getRel());
            assertTrue(ids.containsKey(er.getId()));
            assertNotNull(ids.get(er.getId()), er.getLink().getHref());
        }
        
        assertEquals(1, ((CollectionResponse) api.getCollection("students", 0, 1).getEntity()).getEntities().size());
        assertEquals(1, ((CollectionResponse) api.getCollection("students", 1, 1).getEntity()).getEntities().size());
        
        // test get
        for (String id : ids.keySet()) {
            Response r = api.getEntityOrAssociations("students", id);
            EntityBody body = (EntityBody) r.getEntity();
            assertNotNull(body);
            // TODO: enable assertEquals(id, body.get("id"));
            assertEquals(1, body.get("field1"));
            assertEquals(2, body.get("field2"));
        }
        
        // test update/get/delete
        for (String id : ids.keySet()) {
            Response r = api.getEntityOrAssociations("students", id);
            EntityBody body = (EntityBody) r.getEntity();
            body.put("field1", 99);
            Response r2 = api.updateEntity("students", id, body);
            assertEquals(Status.NO_CONTENT.getStatusCode(), r2.getStatus());
            
            Response r3 = api.getEntityOrAssociations("students", id);
            EntityBody body3 = (EntityBody) r3.getEntity();
            assertNotNull(body3);
            assertEquals(body, body3);
            
            Response d = api.deleteEntity("students", id);
            assertNull(d.getEntity());
            assertEquals(Status.NO_CONTENT.getStatusCode(), d.getStatus());
            
            Response r4 = api.getEntityOrAssociations("students", id);
            assertEquals(Status.NOT_FOUND.getStatusCode(), r4.getStatus());
        }
        
        Response r5 = api.getCollection("students", 0, 100);
        CollectionResponse empty = (CollectionResponse) r5.getEntity();
        assertNotNull(empty);
        assertEquals(0, empty.getEntities().size());
    }
}

package org.slc.sli.api.resources;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import com.sun.jersey.api.uri.UriBuilderImpl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import org.slc.sli.api.representation.CollectionResponse;
import org.slc.sli.api.representation.EmbeddedLink;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.test.WebContextTestExecutionListener;

/**
 * Unit tests for the generic Resource class.
 * 
 * @author Ryan Farris <rfarris@wgen.net>
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class })
public class ResourceTest {
    
    @Autowired
    Resource api;
    
    public Map<String, Object> createTestEntity() {
        Map<String, Object> entity = new HashMap<String, Object>();
        entity.put("field1", 1);
        entity.put("field2", 2);
        return entity;
    }
    
    public Map<String, Object> createTestAssoication(String studentId, String schoolId) {
        Map<String, Object> entity = new HashMap<String, Object>();
        entity.put("studentId", studentId);
        entity.put("schoolId", schoolId);
        entity.put("entryGradeLevel", "First grade");
        return entity;
    }
    
    @Test
    public void testResourceMethods() throws Exception {
        UriInfo info = buildMockUriInfo();

        // post some data
        Map<String, String> ids = new HashMap<String, String>();
        
        Response createResponse = api.createEntity("students", new EntityBody(createTestEntity()));
        assertNotNull(createResponse);
        assertEquals(Status.CREATED.getStatusCode(), createResponse.getStatus());
        String studentId1 = parseIdFromLocation(createResponse);
        ids.put(studentId1, (String) createResponse.getMetadata().get("Location").get(0));
        
        Response createResponse2 = api.createEntity("students", new EntityBody(createTestEntity()));
        assertNotNull(createResponse2);
        assertEquals(Status.CREATED.getStatusCode(), createResponse2.getStatus());
        String studentId2 = parseIdFromLocation(createResponse2);
        ids.put(studentId2, (String) createResponse2.getMetadata().get("Location").get(0));
        
        Response createResponse3 = api.createEntity("schools", new EntityBody(createTestEntity()));
        assertNotNull(createResponse3);
        assertEquals(Status.CREATED.getStatusCode(), createResponse3.getStatus());
        String schoolId = parseIdFromLocation(createResponse3);
        
        Response createResponse4 = api.createEntity("student-enrollments",
                new EntityBody(createTestAssoication(studentId1, schoolId)));
        assertNotNull(createResponse4);
        String assocId1 = parseIdFromLocation(createResponse4);
        
        Response createResponse5 = api.createEntity("student-enrollments",
                new EntityBody(createTestAssoication(studentId2, schoolId)));
        assertNotNull(createResponse5);
        String assocId2 = parseIdFromLocation(createResponse5);

        // test get
        for (String id : ids.keySet()) {
            Response r = api.getEntityOrAssociations("students", id, 0, 100, info);
            EntityBody body = (EntityBody) r.getEntity();
            assertNotNull(body);
            assertEquals(id, body.get("id"));
            assertEquals(1, body.get("field1"));
            assertEquals(2, body.get("field2"));
            List<?> links = (List<?>) body.get("links");
            assertTrue(links.contains(new EmbeddedLink("self", "student", "base/students/" + id)));
            assertTrue(links.contains(new EmbeddedLink("getStudentEnrollments", "student-enrollment",
                    "base/student-enrollments/" + id)));
        }
        
        // test associations
        for (String id : new String[] { assocId1, assocId2 }) {
            Response r = api.getEntityOrAssociations("student-enrollments", id, 0, 10, info);
            EntityBody assoc = (EntityBody) r.getEntity();
            assertNotNull(assoc);
            assertEquals(id, assoc.get("id"));
            assertEquals("First grade", assoc.get("entryGradeLevel"));
            assertEquals(schoolId, assoc.get("schoolId"));
            assertNotNull(assoc.get("studentId"));
            if (!(assoc.get("studentId").equals(studentId1) || assoc.get("studentId").equals(studentId2))) {
                fail();
            }
        }
        
        // test freaky association uri
        for (String id : new String[] { studentId1, studentId2 }) {
            Response r = api.getEntityOrAssociations("student-enrollments", id, 0, 10, info);
            CollectionResponse cr = (CollectionResponse) r.getEntity();
            assertNotNull(cr);
            assertEquals(1, cr.size());
            assertNotNull(cr.get(0).getId());
            if (!(cr.get(0).getId().equals(assocId1) || cr.get(0).getId().equals(assocId2))) {
                fail();
            }
            assertNotNull(cr.get(0).getLink());
            assertNotNull("self", cr.get(0).getLink().getRel());
            assertNotNull(cr.get(0).getLink().getHref());
            assertTrue(cr.get(0).getLink().getHref().contains(cr.get(0).getId()));
        }
        
        // test update/get/delete
        for (String id : ids.keySet()) {
            Response r = api.getEntityOrAssociations("students", id, 0, 100, info);
            EntityBody body = (EntityBody) r.getEntity();
            body.put("field1", 99);
            Response r2 = api.updateEntity("students", id, body);
            assertEquals(Status.NO_CONTENT.getStatusCode(), r2.getStatus());
            
            Response r3 = api.getEntityOrAssociations("students", id, 0, 100, info);
            EntityBody body3 = (EntityBody) r3.getEntity();
            assertNotNull(body3);
            assertEquals(body, body3);
            
            Response d = api.deleteEntity("students", id);
            assertNull(d.getEntity());
            assertEquals(Status.NO_CONTENT.getStatusCode(), d.getStatus());
            
            Response r4 = api.getEntityOrAssociations("students", id, 0, 100, info);
            assertEquals(Status.NOT_FOUND.getStatusCode(), r4.getStatus());
        }
        
    }
    
    private static String parseIdFromLocation(Response response) {
        List<Object> locationHeaders = response.getMetadata().get("Location");
        assertNotNull(locationHeaders);
        assertEquals(1, locationHeaders.size());
        Pattern regex = Pattern.compile(".+/(\\d+)$");
        Matcher matcher = regex.matcher((String) locationHeaders.get(0));
        matcher.find();
        assertEquals(1, matcher.groupCount());
        return matcher.group(1);
    }
    
    public UriInfo buildMockUriInfo() throws Exception {
        UriInfo mock = mock(UriInfo.class);
        when(mock.getAbsolutePathBuilder()).thenAnswer(new Answer<UriBuilder>() {
            
            @Override
            public UriBuilder answer(InvocationOnMock invocation) throws Throwable {
                return new UriBuilderImpl().path("absolute");
            }
        });
        when(mock.getBaseUriBuilder()).thenAnswer(new Answer<UriBuilder>() {
            
            @Override
            public UriBuilder answer(InvocationOnMock invocation) throws Throwable {
                return new UriBuilderImpl().path("base");
            }
        });
        when(mock.getRequestUriBuilder()).thenAnswer(new Answer<UriBuilder>() {
            
            @Override
            public UriBuilder answer(InvocationOnMock invocation) throws Throwable {
                return new UriBuilderImpl().path("request");
            }
        });
        
        return mock;
    }
}

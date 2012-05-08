package org.slc.sli.api.resources.v1.associations;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.slc.sli.api.config.ResourceNames;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.resources.SecurityContextInjector;
import org.slc.sli.api.resources.util.ResourceConstants;
import org.slc.sli.api.resources.v1.HypermediaType;
import org.slc.sli.api.resources.v1.entity.StudentResource;
import org.slc.sli.api.resources.v1.entity.DisciplineIncidentResource;
import org.slc.sli.api.resources.v1.association.StudentDisciplineIncidentAssociationResource;
import org.slc.sli.api.service.EntityNotFoundException;
import org.slc.sli.api.test.WebContextTestExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import com.sun.jersey.api.uri.UriBuilderImpl;
import com.sun.jersey.core.util.MultivaluedMapImpl;

/**
 * Unit tests for the resource representing a Student
 * @author slee
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class })
public class StudentDisciplineIncidentAssociationTest {

    @Autowired
    StudentResource studentResource; //class under test 
    
    @Autowired
    DisciplineIncidentResource disciplineIncidentResource;

    @Autowired
    StudentDisciplineIncidentAssociationResource studentDisciplineIncidentAssociationResource;

    @Autowired
    private SecurityContextInjector injector;
    
    private UriInfo uriInfo;
    private HttpHeaders httpHeaders;
    private final String disciplineIncidentId = "disciplineIncidentId";
    private final String studentParticipationCode = "studentParticipationCode";
    
    private final String firstDisciplineIncidentId = "1001";
    private final String firstParticipationCode = "Perpetrator";
    private final String updatedParticipationCode = "Reporter";
    private final String secondDisciplineIncidentId = "1002";
    private final String secondParticipationCode = "Perpetrator";
    
    @Before
    public void setup() throws Exception {
        uriInfo = buildMockUriInfo(null);
        
        // inject administrator security context for unit testing
        injector.setAdminContextWithElevatedRights();
        
        List<String> acceptRequestHeaders = new ArrayList<String>();
        acceptRequestHeaders.add(HypermediaType.VENDOR_SLC_JSON);
        
        httpHeaders = mock(HttpHeaders.class);
        when(httpHeaders.getRequestHeader("accept")).thenReturn(acceptRequestHeaders);
        when(httpHeaders.getRequestHeaders()).thenReturn(new MultivaluedMapImpl());
    }
    
    private Map<String, Object> createTestEntity() {
        Map<String, Object> entity = new HashMap<String, Object>();
        entity.put(disciplineIncidentId, firstDisciplineIncidentId);
        entity.put(studentParticipationCode, firstParticipationCode);
        return entity;
    }
    
    private Map<String, Object> createTestUpdateEntity() {
        Map<String, Object> entity = new HashMap<String, Object>();
        entity.put(disciplineIncidentId, firstDisciplineIncidentId);
        entity.put(studentParticipationCode, updatedParticipationCode);
        return entity;
    }
    
    private Map<String, Object> createTestSecondaryEntity() {
        Map<String, Object> entity = new HashMap<String, Object>();
        entity.put(disciplineIncidentId, secondDisciplineIncidentId);
        entity.put(studentParticipationCode, secondParticipationCode);
        return entity;
    }
    
    @Test
    public void testCreateDisciplineIncident() {
        Response response = studentDisciplineIncidentAssociationResource.create(new EntityBody(createTestEntity()), httpHeaders, uriInfo);
        assertEquals("Status code should be 201", Status.CREATED.getStatusCode(), response.getStatus());
            
        String id = parseIdFromLocation(response);
        assertNotNull("ID should not be null", id);
    }
    
    @Test
    public void testReadDisciplineIncident() {
        //create one entity
        Response createResponse = studentDisciplineIncidentAssociationResource.create(new EntityBody(createTestEntity()), httpHeaders, uriInfo);
        String id = parseIdFromLocation(createResponse);
        Response response = studentDisciplineIncidentAssociationResource.read(id, httpHeaders, uriInfo);
        
        Object responseEntityObj = response.getEntity();
        
        if (responseEntityObj instanceof EntityBody) {
            assertNotNull(responseEntityObj);
        } else if (responseEntityObj instanceof List<?>) {
            @SuppressWarnings("unchecked")
            List<EntityBody> results = (List<EntityBody>) responseEntityObj;
            assertTrue("Should have one entity", results.size() == 1);
        } else {
            fail("Response entity not recognized: " + response);
        } 
    }
    
    @Test
    public void testDeleteDisciplineIncident() {
        //create one entity
        Response createResponse = studentDisciplineIncidentAssociationResource.create(new EntityBody(createTestEntity()), httpHeaders, uriInfo);
        String id = parseIdFromLocation(createResponse);
        
        //delete it
        Response response = studentDisciplineIncidentAssociationResource.delete(id, httpHeaders, uriInfo);
        assertEquals("Status code should be NO_CONTENT", Status.NO_CONTENT.getStatusCode(), response.getStatus());
        
        try {
            @SuppressWarnings("unused")
            Response getResponse = studentDisciplineIncidentAssociationResource.read(id, httpHeaders, uriInfo);
            fail("should have thrown EntityNotFoundException");
        } catch (EntityNotFoundException e) {
            return;
        } catch (Exception e) {
            fail("threw wrong exception: " + e);
        }
    }
    
    @Test
    public void testUpdateDisciplineIncident() {
        //create one entity
        Response createResponse = studentDisciplineIncidentAssociationResource.create(new EntityBody(createTestEntity()), httpHeaders, uriInfo);
        String id = parseIdFromLocation(createResponse);
        
        //update it
        Response response = studentDisciplineIncidentAssociationResource.update(id, new EntityBody(createTestUpdateEntity()), httpHeaders, uriInfo);
        assertEquals("Status code should be NO_CONTENT", Status.NO_CONTENT.getStatusCode(), response.getStatus());
          
        //try to get it
        Response getResponse = studentDisciplineIncidentAssociationResource.read(id, httpHeaders, uriInfo);
        assertEquals("Status code should be OK", Status.OK.getStatusCode(), getResponse.getStatus());            
        EntityBody body = (EntityBody) getResponse.getEntity();
        assertNotNull("Should return an entity", body);            
        assertEquals(studentParticipationCode + " should be " + updatedParticipationCode, updatedParticipationCode, body.get(studentParticipationCode));
        assertNotNull("Should include links", body.get(ResourceConstants.LINKS));
    }
    
    @Test
    public void testReadAllDisciplineIncidents() {
        //create two entities
        studentDisciplineIncidentAssociationResource.create(new EntityBody(createTestEntity()), httpHeaders, uriInfo);
        studentDisciplineIncidentAssociationResource.create(new EntityBody(createTestSecondaryEntity()), httpHeaders, uriInfo);
        
        //read everything
        Response response = studentDisciplineIncidentAssociationResource.readAll(0, 100, httpHeaders, uriInfo);
        assertEquals("Status code should be OK", Status.OK.getStatusCode(), response.getStatus());
        
        @SuppressWarnings("unchecked")
        List<EntityBody> results = (List<EntityBody>) response.getEntity();
        assertNotNull("Should return entities", results);
        assertTrue("Should have at least two entities", results.size() >= 2);
    }
    
    @Test
    public void testReadCommaSeparatedResources() {
        Response response = studentDisciplineIncidentAssociationResource.read(getIDList(ResourceNames.STUDENT_DISCIPLINE_INCIDENT_ASSOCIATIONS), httpHeaders, uriInfo);
        assertEquals("Status code should be 200", Status.OK.getStatusCode(), response.getStatus());
        
        @SuppressWarnings("unchecked")
        List<EntityBody> results = (List<EntityBody>) response.getEntity();
        assertEquals("Should get 2 entities", 2, results.size());

        EntityBody body1 = results.get(0);
        assertNotNull("Should not be null", body1);
        assertEquals(studentParticipationCode + " should be " + firstParticipationCode, firstParticipationCode, body1.get(studentParticipationCode));
        assertNotNull("Should include links", body1.get(ResourceConstants.LINKS));
        
        EntityBody body2 = results.get(1);
        assertNotNull("Should not be null", body2);
        assertEquals(studentParticipationCode + " should be " + secondParticipationCode, secondParticipationCode, body2.get(studentParticipationCode));
        assertNotNull("Should include links", body2.get(ResourceConstants.LINKS));
    }
    
//    @Test
//    public void testGetStudents() {
//        //create one entity
//        Response createResponse = studentDisciplineIncidentAssociationResource.create(new EntityBody(createTestEntity()), httpHeaders, uriInfo);
//        String id = parseIdFromLocation(createResponse);
//        
//        Response response = studentDisciplineIncidentAssociationResource.getStudents(id, 0, 0, httpHeaders, uriInfo);
//        assertEquals("Status code should be OK", Status.OK.getStatusCode(), response.getStatus());            
//
//        Object responseEntityObj = response.getEntity();
//
//        EntityBody body = null;
//        if (responseEntityObj instanceof EntityBody) {
//            assertNotNull(responseEntityObj);
//            body = (EntityBody) responseEntityObj;
//        } else if (responseEntityObj instanceof List<?>) {
//            @SuppressWarnings("unchecked")
//            List<EntityBody> results = (List<EntityBody>) responseEntityObj;
//            assertTrue("Should have one entity", results.size() == 1);
//            body = results.get(0);
//        } else {
//            fail("Response entity not recognized: " + response);
//            return;
//        }
//
//        assertNotNull("Should return an entity", body);            
//        assertEquals("disciplineIncidentId should be 1001", "1001", body.get("disciplineIncidentId"));
//        assertNotNull("Should include links", body.get(ResourceConstants.LINKS));
//    }
//    

    private UriInfo buildMockUriInfo(final String queryString) throws Exception {
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
        
        when(mock.getQueryParameters(true)).thenAnswer(new Answer<MultivaluedMap<String, String>>() {
            @Override
            public MultivaluedMap<String, String> answer(InvocationOnMock invocationOnMock) throws Throwable {
                return new MultivaluedMapImpl();
            }
        });
        
        when(mock.getRequestUri()).thenReturn(new UriBuilderImpl().replaceQuery(queryString).build(new Object[] {}));
        return mock;
    }
    
    private String getIDList(String resource) {
        //create more resources
        Response createResponse1 = studentDisciplineIncidentAssociationResource.create(new EntityBody(createTestEntity()), httpHeaders, uriInfo);
        Response createResponse2 = studentDisciplineIncidentAssociationResource.create(new EntityBody(createTestSecondaryEntity()), httpHeaders, uriInfo);
        
        return parseIdFromLocation(createResponse1) + "," + parseIdFromLocation(createResponse2);
    }
    
    private static String parseIdFromLocation(Response response) {
        List<Object> locationHeaders = response.getMetadata().get("Location");
        assertNotNull(locationHeaders);
        assertEquals(1, locationHeaders.size());
        Pattern regex = Pattern.compile(".+/([\\w-]+)$");
        Matcher matcher = regex.matcher((String) locationHeaders.get(0));
        matcher.find();
        assertEquals(1, matcher.groupCount());
        return matcher.group(1);
    }
}

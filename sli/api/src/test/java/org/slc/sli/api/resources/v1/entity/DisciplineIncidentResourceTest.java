package org.slc.sli.api.resources.v1.entity;

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

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.representation.EntityResponse;
import org.slc.sli.api.resources.SecurityContextInjector;
import org.slc.sli.api.resources.util.ResourceTestUtil;
import org.slc.sli.api.resources.v1.HypermediaType;
import org.slc.sli.api.resources.v1.association.StudentDisciplineIncidentAssociationResource;
import org.slc.sli.api.service.EntityNotFoundException;
import org.slc.sli.api.test.WebContextTestExecutionListener;
import org.slc.sli.common.constants.ResourceConstants;
import org.slc.sli.common.constants.ResourceNames;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

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
public class DisciplineIncidentResourceTest {

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
    private final String incidentIdentifier = "disciplineIncident";
    private final String incidentDate = "incidentDate";
    private final String incidentTime = "incidentTime";

    private final String firstIncidentIdentifier = "1";
    private final String firstIncidentDate = "2012-02-24";
    private final String firstIncidentTime = "09:00:00";
    private final String updatedIncidentDate = "2012-02-23";
    private final String updatedIncidentTime = "09:09:09";
    private final String secondIncidentIdentifier = "2";
    private final String secondIncidentDate = "2012-02-25";
    private final String secondIncidentTime = "19:09:09";

    @Before
    public void setup() throws Exception {
        uriInfo = ResourceTestUtil.buildMockUriInfo(null);

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
        entity.put(incidentIdentifier, firstIncidentIdentifier);
        entity.put(incidentDate, firstIncidentDate);
        entity.put(incidentTime, firstIncidentTime);
        return entity;
    }

    private Map<String, Object> createTestUpdateEntity() {
        Map<String, Object> entity = new HashMap<String, Object>();
        entity.put(incidentIdentifier, firstIncidentIdentifier);
        entity.put(incidentDate, updatedIncidentDate);
        entity.put(incidentTime, updatedIncidentTime);
        return entity;
    }

    private Map<String, Object> createTestSecondaryEntity() {
        Map<String, Object> entity = new HashMap<String, Object>();
        entity.put(incidentIdentifier, secondIncidentIdentifier);
        entity.put(incidentDate, secondIncidentDate);
        entity.put(incidentTime, secondIncidentTime);
        return entity;
    }

    private Map<String, Object> createTestStudentEntity() {
        Map<String, Object> entity = new HashMap<String, Object>();
        entity.put("studentUniqueStateId", "1001");
       return entity;
    }

    private Map<String, Object> createTestStudentDisciplineIncidentAssociationEntity() {
        Map<String, Object> entity = new HashMap<String, Object>();
        entity.put("studentParticipationCode", "Perpetrator");
        return entity;
    }

    @Test
    public void testCreate() {
        Response response = disciplineIncidentResource.create(new EntityBody(createTestEntity()), httpHeaders, uriInfo);
        assertEquals("Status code should be 201", Status.CREATED.getStatusCode(), response.getStatus());

        String id = ResourceTestUtil.parseIdFromLocation(response);
        assertNotNull("ID should not be null", id);
    }

    @Test
    public void testRead() {
        //create one entity
        Response createResponse = disciplineIncidentResource.create(new EntityBody(createTestEntity()), httpHeaders, uriInfo);
        String id = ResourceTestUtil.parseIdFromLocation(createResponse);
        Response response = disciplineIncidentResource.read(id, httpHeaders, uriInfo);

        Object responseEntityObj = null;

        if (response.getEntity() instanceof EntityResponse) {
            EntityResponse resp = (EntityResponse) response.getEntity();
            responseEntityObj = resp.getEntity();
        } else {
            fail("Should always return EntityResponse: " + response);
        }

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
    public void testDelete() {
        //create one entity
        Response createResponse = disciplineIncidentResource.create(new EntityBody(createTestEntity()), httpHeaders, uriInfo);
        String id = ResourceTestUtil.parseIdFromLocation(createResponse);

        //delete it
        Response response = disciplineIncidentResource.delete(id, httpHeaders, uriInfo);
        assertEquals("Status code should be NO_CONTENT", Status.NO_CONTENT.getStatusCode(), response.getStatus());

        try {
            @SuppressWarnings("unused")
            Response getResponse = disciplineIncidentResource.read(id, httpHeaders, uriInfo);
            fail("should have thrown EntityNotFoundException");
        } catch (EntityNotFoundException e) {
            return;
        } catch (Exception e) {
            fail("threw wrong exception: " + e);
        }
    }

    @Test
    public void testUpdate() {
        //create one entity
        Response createResponse = disciplineIncidentResource.create(new EntityBody(createTestEntity()), httpHeaders, uriInfo);
        String id = ResourceTestUtil.parseIdFromLocation(createResponse);

        //update it
        Response response = disciplineIncidentResource.update(id, new EntityBody(createTestUpdateEntity()), httpHeaders, uriInfo);
        assertEquals("Status code should be NO_CONTENT", Status.NO_CONTENT.getStatusCode(), response.getStatus());

        //try to get it
        Response getResponse = disciplineIncidentResource.read(id, httpHeaders, uriInfo);
        assertEquals("Status code should be OK", Status.OK.getStatusCode(), getResponse.getStatus());
        EntityResponse entityResponse = (EntityResponse) getResponse.getEntity();
        EntityBody body = (EntityBody) entityResponse.getEntity();
        assertNotNull("Should return an entity", body);
        assertEquals(incidentDate + " should be " + updatedIncidentDate, updatedIncidentDate, body.get(incidentDate));
        assertEquals(incidentTime + " should be " + updatedIncidentTime, updatedIncidentTime, body.get(incidentTime));
        assertNotNull("Should include links", body.get(ResourceConstants.LINKS));
    }

    @Test
    public void testReadAll() {
        //create two entities
        disciplineIncidentResource.create(new EntityBody(createTestEntity()), httpHeaders, uriInfo);
        disciplineIncidentResource.create(new EntityBody(createTestSecondaryEntity()), httpHeaders, uriInfo);

        //read everything
        Response response = disciplineIncidentResource.readAll(0, 100, httpHeaders, uriInfo);
        assertEquals("Status code should be OK", Status.OK.getStatusCode(), response.getStatus());

        @SuppressWarnings("unchecked")
        EntityResponse entityResponse = (EntityResponse) response.getEntity();
        List<EntityBody> results = (List<EntityBody>) entityResponse.getEntity();
        assertNotNull("Should return entities", results);
        assertTrue("Should have at least two entities", results.size() >= 2);
    }

    @Test
    public void testReadCommaSeparatedResources() {
        Response response = disciplineIncidentResource.read(getIDList(ResourceNames.DISCIPLINE_INCIDENTS), httpHeaders, uriInfo);
        assertEquals("Status code should be 200", Status.OK.getStatusCode(), response.getStatus());

        @SuppressWarnings("unchecked")
        EntityResponse entityResponse = (EntityResponse) response.getEntity();
        List<EntityBody> results = (List<EntityBody>) entityResponse.getEntity();
        assertEquals("Should get 2 entities", 2, results.size());

        EntityBody body1 = results.get(0);
        assertNotNull("Should not be null", body1);
        assertEquals(incidentDate + " should be " + firstIncidentDate, firstIncidentDate, body1.get(incidentDate));
        assertEquals(incidentTime + " should be " + firstIncidentTime, firstIncidentTime, body1.get(incidentTime));
        assertNotNull("Should include links", body1.get(ResourceConstants.LINKS));

        EntityBody body2 = results.get(1);
        assertNotNull("Should not be null", body2);
        assertEquals(incidentDate + " should be " + secondIncidentDate, secondIncidentDate, body2.get(incidentDate));
        assertEquals(incidentTime + " should be " + secondIncidentTime, secondIncidentTime, body2.get(incidentTime));
        assertNotNull("Should include links", body2.get(ResourceConstants.LINKS));
    }

    @Test
    public void testGetStudentDisciplineIncidentAssociations() {
        //create one entity
        Response createResponse = disciplineIncidentResource.create(new EntityBody(createTestEntity()), httpHeaders, uriInfo);
        String disciplineIncidentId = ResourceTestUtil.parseIdFromLocation(createResponse);

        createResponse = studentResource.create(new EntityBody(createTestStudentEntity()), httpHeaders, uriInfo);
        String studentId = ResourceTestUtil.parseIdFromLocation(createResponse);

        Map<String, Object> map = createTestStudentDisciplineIncidentAssociationEntity();
        map.put("disciplineIncidentId", disciplineIncidentId);
        map.put("studentId", studentId);

        createResponse = studentDisciplineIncidentAssociationResource.create(new EntityBody(map), httpHeaders, uriInfo);
        Response response = disciplineIncidentResource.getStudentDisciplineIncidentAssociations(disciplineIncidentId, httpHeaders, uriInfo);
        assertEquals("Status code should be OK", Status.OK.getStatusCode(), response.getStatus());

        Object responseEntityObj = null;

        if (response.getEntity() instanceof EntityResponse) {
            EntityResponse resp = (EntityResponse) response.getEntity();
            responseEntityObj = resp.getEntity();
        } else {
            fail("Should always return EntityResponse: " + response);
        }

        EntityBody body = null;
        if (responseEntityObj instanceof EntityBody) {
            assertNotNull(responseEntityObj);
            body = (EntityBody) responseEntityObj;
        } else if (responseEntityObj instanceof List<?>) {
            @SuppressWarnings("unchecked")
            List<EntityBody> results = (List<EntityBody>) responseEntityObj;
            assertTrue("Should have one entity", results.size() == 1);
            body = results.get(0);
        } else {
            fail("Response entity not recognized: " + response);
            return;
        }

        assertNotNull("Should return an entity", body);
        assertEquals("studentParticipationCode should be Perpetrator", "Perpetrator", body.get("studentParticipationCode"));
        assertNotNull("Should include links", body.get(ResourceConstants.LINKS));

        response = studentResource.getStudentDisciplineIncidentAssociations(studentId, httpHeaders, uriInfo);
        assertEquals("Status code should be OK", Status.OK.getStatusCode(), response.getStatus());

        responseEntityObj = null;

        if (response.getEntity() instanceof EntityResponse) {
            EntityResponse resp = (EntityResponse) response.getEntity();
            responseEntityObj = resp.getEntity();
        } else {
            fail("Should always return EntityResponse: " + response);
        }

        body = null;
        if (responseEntityObj instanceof EntityBody) {
            assertNotNull(responseEntityObj);
            body = (EntityBody) responseEntityObj;
        } else if (responseEntityObj instanceof List<?>) {
            @SuppressWarnings("unchecked")
            List<EntityBody> results = (List<EntityBody>) responseEntityObj;
            assertTrue("Should have one entity", results.size() == 1);
            body = results.get(0);
        } else {
            fail("Response entity not recognized: " + response);
            return;
        }

        assertNotNull("Should return an entity", body);
        assertEquals("studentParticipationCode should be Perpetrator", "Perpetrator", body.get("studentParticipationCode"));
        assertNotNull("Should include links", body.get(ResourceConstants.LINKS));
    }

    @Test
    public void testGetStudentDisciplineIncidentAssociationStudents() {
        //create one entity
        Response createResponse = disciplineIncidentResource.create(new EntityBody(createTestEntity()), httpHeaders, uriInfo);
        String disciplineIncidentId = ResourceTestUtil.parseIdFromLocation(createResponse);

        createResponse = studentResource.create(new EntityBody(createTestStudentEntity()), httpHeaders, uriInfo);
        String studentId = ResourceTestUtil.parseIdFromLocation(createResponse);

        Map<String, Object> map = createTestStudentDisciplineIncidentAssociationEntity();
        map.put("disciplineIncidentId", disciplineIncidentId);
        map.put("studentId", studentId);

        createResponse = studentDisciplineIncidentAssociationResource.create(new EntityBody(map), httpHeaders, uriInfo);
        Response response = disciplineIncidentResource.getStudentDisciplineIncidentAssociationStudents(disciplineIncidentId, httpHeaders, uriInfo);
        assertEquals("Status code should be OK", Status.OK.getStatusCode(), response.getStatus());

        Object responseEntityObj = null;

        if (response.getEntity() instanceof EntityResponse) {
            EntityResponse resp = (EntityResponse) response.getEntity();
            responseEntityObj = resp.getEntity();
        } else {
            fail("Should always return EntityResponse: " + response);
        }

        EntityBody body = null;
        if (responseEntityObj instanceof EntityBody) {
            assertNotNull(responseEntityObj);
            body = (EntityBody) responseEntityObj;
        } else if (responseEntityObj instanceof List<?>) {
            @SuppressWarnings("unchecked")
            List<EntityBody> results = (List<EntityBody>) responseEntityObj;
            assertTrue("Should have one entity", results.size() == 1);
            body = results.get(0);
        } else {
            fail("Response entity not recognized: " + response);
            return;
        }

        assertNotNull("Should return an entity", body);
        assertEquals("studentUniqueStateId should be 1001", "1001", body.get("studentUniqueStateId"));
        assertNotNull("Should include links", body.get(ResourceConstants.LINKS));
    }

    @Test
    public void testGetStudentDisciplineIncidentAssociationDisciplineIncidents() {
        //create one entity
        Response createResponse = disciplineIncidentResource.create(new EntityBody(createTestEntity()), httpHeaders, uriInfo);
        String disciplineIncidentId = ResourceTestUtil.parseIdFromLocation(createResponse);

        createResponse = studentResource.create(new EntityBody(createTestStudentEntity()), httpHeaders, uriInfo);
        String studentId = ResourceTestUtil.parseIdFromLocation(createResponse);

        Map<String, Object> map = createTestStudentDisciplineIncidentAssociationEntity();
        map.put("disciplineIncidentId", disciplineIncidentId);
        map.put("studentId", studentId);

        createResponse = studentDisciplineIncidentAssociationResource.create(new EntityBody(map), httpHeaders, uriInfo);
        Response response = studentResource.getStudentDisciplineIncidentAssociationDisciplineIncidents(studentId, httpHeaders, uriInfo);
        assertEquals("Status code should be OK", Status.OK.getStatusCode(), response.getStatus());

        Object responseEntityObj = null;

        if (response.getEntity() instanceof EntityResponse) {
            EntityResponse resp = (EntityResponse) response.getEntity();
            responseEntityObj = resp.getEntity();
        } else {
            fail("Should always return EntityResponse: " + response);
        }

        EntityBody body = null;
        if (responseEntityObj instanceof EntityBody) {
            assertNotNull(responseEntityObj);
            body = (EntityBody) responseEntityObj;
        } else if (responseEntityObj instanceof List<?>) {
            @SuppressWarnings("unchecked")
            List<EntityBody> results = (List<EntityBody>) responseEntityObj;
            assertTrue("Should have one entity", results.size() == 1);
            body = results.get(0);
        } else {
            fail("Response entity not recognized: " + response);
            return;
        }

        assertNotNull("Should return an entity", body);
        assertEquals(incidentDate + " should be " + firstIncidentDate, firstIncidentDate, body.get(incidentDate));
        assertEquals(incidentTime + " should be " + firstIncidentTime, firstIncidentTime, body.get(incidentTime));
        assertNotNull("Should include links", body.get(ResourceConstants.LINKS));
    }

    private String getIDList(String resource) {
        //create more resources
        Response createResponse1 = disciplineIncidentResource.create(new EntityBody(createTestEntity()), httpHeaders, uriInfo);
        Response createResponse2 = disciplineIncidentResource.create(new EntityBody(createTestSecondaryEntity()), httpHeaders, uriInfo);

        return ResourceTestUtil.parseIdFromLocation(createResponse1) + "," + ResourceTestUtil.parseIdFromLocation(createResponse2);
    }
}

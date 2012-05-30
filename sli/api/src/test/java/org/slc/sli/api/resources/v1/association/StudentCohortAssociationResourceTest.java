package org.slc.sli.api.resources.v1.association;

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

import com.sun.jersey.core.util.MultivaluedMapImpl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slc.sli.api.client.constants.ResourceConstants;
import org.slc.sli.api.client.constants.ResourceNames;
import org.slc.sli.api.client.constants.v1.ParameterConstants;
import org.slc.sli.api.representation.EntityResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.resources.SecurityContextInjector;
import org.slc.sli.api.resources.util.ResourceTestUtil;
import org.slc.sli.api.resources.v1.HypermediaType;
import org.slc.sli.api.resources.v1.entity.CohortResource;
import org.slc.sli.api.resources.v1.entity.StudentResource;
import org.slc.sli.api.service.EntityNotFoundException;
import org.slc.sli.api.test.WebContextTestExecutionListener;

/**
 * Unit tests for the resource representing a cohort
 * @author srichards
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class })
public class StudentCohortAssociationResourceTest {

    @Autowired
    StudentResource studentResource;
    @Autowired
    CohortResource cohortResource;
    @Autowired
    StudentCohortAssociationResource studentCohortAssn; //class under test

    @Autowired
    private SecurityContextInjector injector;

    private UriInfo uriInfo;
    private HttpHeaders httpHeaders;

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

/*    private Map<String, Object> createTestCohortEntity() {
        Map<String, Object> entity = new HashMap<String, Object>();
        entity.put(ChortResource.COHORT_IDENTIFIER, firstCohortId);
        entity.put(CohortResource.COHORT_TYPE, "Unua Type");
        entity.put(CohortResource.EDUCATION_ORGANIZATION_ID, edOrgId);
        return entity;
    }

    private Map<String, Object> createTestStudentEntity() {
        Map<String, Object> entity = new HashMap<String, Object>();
        entity.put(ParameterConstants.STUDENT_ID, firstStudentId);
        entity.put(StudentResource.UNIQUE_STATE_ID, uniqueStateId);
        entity.put(StudentResource.NAME, "Unua");
        entity.put(StudentResource.SEX, "Female");
        entity.put(StudentResource.BIRTH_DATA, "01/01/1999");
        entity.put(StudentResource.HISPANIC_LATINO_ETHNICITY, "true");
        return entity;
    }
*/
    private final String assnId = "1234";
    private final String firstBeginDate = "2012-01-01";
    private final String secondBeginDate = "2012-06-06";
    private final String updatedBeginDate = "2012-12-31";
    private final String studentId = "2345";
    private final String cohortId = "3456";

    private Map<String, Object> createTestAssociation() {
        Map<String, Object> entity = new HashMap<String, Object>();
        entity.put(ParameterConstants.STUDENT_COHORT_ASSOCIATION_ID, assnId);
        entity.put(StudentCohortAssociationResource.BEGIN_DATE, firstBeginDate);
        entity.put(ParameterConstants.STUDENT_ID, studentId);
        entity.put(ParameterConstants.COHORT_ID, cohortId);
        return entity;
    }

    private Map<String, Object> createTestUpdateAssociation() {
        Map<String, Object> entity = new HashMap<String, Object>();
        entity.put(ParameterConstants.STUDENT_COHORT_ASSOCIATION_ID, assnId);
        entity.put(StudentCohortAssociationResource.BEGIN_DATE, updatedBeginDate);
        entity.put(ParameterConstants.STUDENT_ID, studentId);
        entity.put(ParameterConstants.COHORT_ID, cohortId);
        return entity;
    }

    private Map<String, Object> createTestSecondaryAssociation() {
        Map<String, Object> entity = new HashMap<String, Object>();
        entity.put(ParameterConstants.STUDENT_COHORT_ASSOCIATION_ID, "4567");
        entity.put(StudentCohortAssociationResource.BEGIN_DATE, secondBeginDate);
        entity.put(ParameterConstants.STUDENT_ID, "5678");
        entity.put(ParameterConstants.COHORT_ID, "6789");
        return entity;
    }

    @Test
    public void testCreate() {
        Response response = studentCohortAssn.create(new EntityBody(createTestAssociation()), httpHeaders, uriInfo);
        assertEquals("Status code should be 201", Status.CREATED.getStatusCode(), response.getStatus());

        String id = ResourceTestUtil.parseIdFromLocation(response);
        assertNotNull("ID should not be null", id);
    }

    @Test
    public void testRead() {
        //create one entity
        Response createResponse = studentCohortAssn.create(new EntityBody(createTestAssociation()), httpHeaders, uriInfo);
        String id = ResourceTestUtil.parseIdFromLocation(createResponse);
        Response response = studentCohortAssn.read(id, httpHeaders, uriInfo);

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
        Response createResponse = studentCohortAssn.create(new EntityBody(createTestAssociation()), httpHeaders, uriInfo);
        String id = ResourceTestUtil.parseIdFromLocation(createResponse);

        //delete it
        Response response = studentCohortAssn.delete(id, httpHeaders, uriInfo);
        assertEquals("Status code should be NO_CONTENT", Status.NO_CONTENT.getStatusCode(), response.getStatus());

        try {
            @SuppressWarnings("unused")
            Response getResponse = studentCohortAssn.read(id, httpHeaders, uriInfo);
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
        Response createResponse = studentCohortAssn.create(new EntityBody(createTestAssociation()), httpHeaders, uriInfo);
        String id = ResourceTestUtil.parseIdFromLocation(createResponse);

        //update it
        Response response = studentCohortAssn.update(id, new EntityBody(createTestUpdateAssociation()), httpHeaders, uriInfo);
        assertEquals("Status code should be NO_CONTENT", Status.NO_CONTENT.getStatusCode(), response.getStatus());

        //try to get it
        Response getResponse = studentCohortAssn.read(id, httpHeaders, uriInfo);
        assertEquals("Status code should be OK", Status.OK.getStatusCode(), getResponse.getStatus());
        EntityResponse entityResponse  = (EntityResponse) getResponse.getEntity();
        EntityBody body = (EntityBody) entityResponse.getEntity();
        assertNotNull("Should return an entity", body);
        assertEquals(StudentCohortAssociationResource.BEGIN_DATE + " should be " + updatedBeginDate, updatedBeginDate, body.get(StudentCohortAssociationResource.BEGIN_DATE));
        assertNotNull("Should include links", body.get(ResourceConstants.LINKS));
    }

    @Test
    public void testReadAll() {
        //create two entities
        studentCohortAssn.create(new EntityBody(createTestAssociation()), httpHeaders, uriInfo);
        studentCohortAssn.create(new EntityBody(createTestSecondaryAssociation()), httpHeaders, uriInfo);

        //read everything
        Response response = studentCohortAssn.readAll(0, 100, httpHeaders, uriInfo);
        assertEquals("Status code should be OK", Status.OK.getStatusCode(), response.getStatus());

        @SuppressWarnings("unchecked")
        EntityResponse entityResponse  = (EntityResponse) response.getEntity();
        List<EntityBody> results = (List<EntityBody>) entityResponse.getEntity();
        assertNotNull("Should return entities", results);
        assertTrue("Should have at least two entities", results.size() >= 2);
    }

    @Test
    public void testReadCommaSeparatedResources() {
        Response response = studentCohortAssn.read(getIDList(ResourceNames.STUDENT_COHORT_ASSOCIATIONS), httpHeaders, uriInfo);
        assertEquals("Status code should be 200", Status.OK.getStatusCode(), response.getStatus());

        @SuppressWarnings("unchecked")
        EntityResponse entityResponse  = (EntityResponse) response.getEntity();
        List<EntityBody> results = (List<EntityBody>) entityResponse.getEntity();
        assertEquals("Should get 2 entities", 2, results.size());

        EntityBody body1 = results.get(0);
        assertNotNull("Should not be null", body1);
        assertEquals(StudentCohortAssociationResource.BEGIN_DATE + " should be " + firstBeginDate, firstBeginDate, body1.get(StudentCohortAssociationResource.BEGIN_DATE));
        assertNotNull("Should include links", body1.get(ResourceConstants.LINKS));

        EntityBody body2 = results.get(1);
        assertNotNull("Should not be null", body2);
        assertEquals(StudentCohortAssociationResource.BEGIN_DATE + " should be " + secondBeginDate, secondBeginDate, body2.get(StudentCohortAssociationResource.BEGIN_DATE));
        assertNotNull("Should include links", body2.get(ResourceConstants.LINKS));
    }

    @Test
    public void testGetStudentCohortAssocationStudents() {
        Response createResponse = studentResource.create(new EntityBody(
                ResourceTestUtil.createTestEntity("StudentResource")), httpHeaders, uriInfo);
        String studentId = ResourceTestUtil.parseIdFromLocation(createResponse);
        createResponse = cohortResource.create(new EntityBody(
                ResourceTestUtil.createTestEntity("CohortResource")), httpHeaders, uriInfo);
        String cohortId = ResourceTestUtil.parseIdFromLocation(createResponse);

        Map<String, Object> map = ResourceTestUtil.createTestAssociationEntity(
                "StudentCohortAssociation", "StudentResource", studentId, "CohortResource", cohortId);
        createResponse = studentCohortAssn.create(new EntityBody(map), httpHeaders, uriInfo);
        String id = ResourceTestUtil.parseIdFromLocation(createResponse);

        Response response = studentCohortAssn.getStudentCohortAssocationStudents(id, 0, 100, httpHeaders, uriInfo);
        EntityBody body = ResourceTestUtil.assertions(response);
        assertEquals("Entity type should match", "student", body.get("entityType"));
        assertEquals("ID should match", studentId, body.get("id"));
    }

    @Test
    public void testGetStudentCohortAssocationCohorts() {
        Response createResponse = studentResource.create(new EntityBody(
                ResourceTestUtil.createTestEntity("StudentResource")), httpHeaders, uriInfo);
        String studentId = ResourceTestUtil.parseIdFromLocation(createResponse);
        createResponse = cohortResource.create(new EntityBody(
                ResourceTestUtil.createTestEntity("CohortResource")), httpHeaders, uriInfo);
        String cohortId = ResourceTestUtil.parseIdFromLocation(createResponse);

        Map<String, Object> map = ResourceTestUtil.createTestAssociationEntity(
                "StudentCohortAssociation", "StudentResource", studentId, "CohortResource", cohortId);
        createResponse = studentCohortAssn.create(new EntityBody(map), httpHeaders, uriInfo);
        String id = ResourceTestUtil.parseIdFromLocation(createResponse);

        Response response = studentCohortAssn.getStudentCohortAssocationCohorts(id, 0, 100, httpHeaders, uriInfo);
        EntityBody body = ResourceTestUtil.assertions(response);
        assertEquals("Entity type should match", "cohort", body.get("entityType"));
        assertEquals("ID should match", cohortId, body.get("id"));
    }

    private String getIDList(String resource) {
        //create more resources
        Response createResponse1 = studentCohortAssn.create(new EntityBody(createTestAssociation()), httpHeaders, uriInfo);
        Response createResponse2 = studentCohortAssn.create(new EntityBody(createTestSecondaryAssociation()), httpHeaders, uriInfo);

        return ResourceTestUtil.parseIdFromLocation(createResponse1) + "," + ResourceTestUtil.parseIdFromLocation(createResponse2);
    }
}

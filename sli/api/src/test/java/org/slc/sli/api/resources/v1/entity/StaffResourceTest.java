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
import org.slc.sli.api.resources.v1.association.StaffCohortAssociationResource;
import org.slc.sli.api.resources.v1.association.StaffEducationOrganizationAssociationResource;
import org.slc.sli.api.resources.v1.association.StaffProgramAssociationResource;
import org.slc.sli.api.service.EntityNotFoundException;
import org.slc.sli.api.test.WebContextTestExecutionListener;

/**
 * Unit tests for the resource representing a Staff
 * @author srichards
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class })
public class StaffResourceTest {

    @Autowired
    StaffResource staffResource; //class under test

    @Autowired
    EducationOrganizationResource edOrgResource;

    @Autowired
    StaffEducationOrganizationAssociationResource staffEdOrgAssn;

    @Autowired
    CohortResource cohortResource;

    @Autowired
    StaffCohortAssociationResource staffCohortAssn;

    @Autowired
    ProgramResource programResource;

    @Autowired
    StaffProgramAssociationResource staffProgramAssociationResource;

    @Autowired
    private SecurityContextInjector injector;

    private UriInfo uriInfo;
    private HttpHeaders httpHeaders;

    private final String firstStaffId = "1234";
    private final String secondStaffId = "5678";
    private final String edOrgId = "2345";
    private final String edOrgAssociationId = "3456";
    private final String cohortId = "4567";
    private final String cohortAssociationId = "5678";
    private final String cohortAssnBeginDate = "2012-02-02";
    private final String secondName = "Dua";
    private final String uniqueStateId = "9876";
    private final String staffClassification = "orange";
    private final String cohortType = "Unua Type";

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
        entity.put(ParameterConstants.STAFF_ID, firstStaffId);
        entity.put(StaffResource.UNIQUE_STATE_ID, uniqueStateId);
        entity.put(StaffResource.NAME, "Unua");
        entity.put(StaffResource.SEX, "Female");
        entity.put(StaffResource.HISPANIC_LATINO_ETHNICITY, "true");
        entity.put(StaffResource.EDUCATION_LEVEL, "PhD");
        return entity;
    }

    private Map<String, Object> createTestUpdateEntity() {
        Map<String, Object> entity = new HashMap<String, Object>();
        entity.put(ParameterConstants.STAFF_ID, firstStaffId);
        entity.put(StaffResource.UNIQUE_STATE_ID, uniqueStateId);
        entity.put(StaffResource.NAME, secondName);
        entity.put(StaffResource.SEX, "Female");
        entity.put(StaffResource.HISPANIC_LATINO_ETHNICITY, "true");
        entity.put(StaffResource.EDUCATION_LEVEL, "PhD");
        return entity;
    }

    private Map<String, Object> createTestSecondaryEntity() {
        Map<String, Object> entity = new HashMap<String, Object>();
        entity.put(ParameterConstants.STAFF_ID, secondStaffId);
        entity.put(StaffResource.UNIQUE_STATE_ID, uniqueStateId);
        entity.put(StaffResource.NAME, "Tria");
        entity.put(StaffResource.SEX, "Male");
        entity.put(StaffResource.HISPANIC_LATINO_ETHNICITY, "false");
        entity.put(StaffResource.EDUCATION_LEVEL, "HS Diploma");
        return entity;
    }

    private Map<String, Object> createTestEdOrgEntity() {
        Map<String, Object> entity = new HashMap<String, Object>();
        entity.put(ParameterConstants.EDUCATION_ORGANIZATION_ID, edOrgId);
        entity.put("organizationCategories", "State Education Agency");
        entity.put("nameOfInstitution", "Primero Test Institution");
       return entity;
    }

    private Map<String, Object> createTestEdOrgAssociationEntity() {
        Map<String, Object> entity = new HashMap<String, Object>();
        entity.put(ParameterConstants.STAFF_EDUCATION_ORGANIZATION_ID, edOrgAssociationId);
        entity.put(StaffEducationOrganizationAssociationResource.STAFF_CLASSIFICATION, staffClassification);
        entity.put(StaffEducationOrganizationAssociationResource.BEGIN_DATE, "2012-01-01");
        return entity;
    }

    private Map<String, Object> createTestCohortEntity() {
        Map<String, Object> entity = new HashMap<String, Object>();
        entity.put(CohortResource.COHORT_IDENTIFIER, cohortId);
        entity.put(CohortResource.COHORT_TYPE, cohortType);
        entity.put(CohortResource.EDUCATION_ORGANIZATION_ID, edOrgId);
        return entity;
    }

    private Map<String, Object> createTestCohortAssociationEntity() {
        Map<String, Object> entity = new HashMap<String, Object>();
        entity.put(ParameterConstants.STAFF_COHORT_ASSOCIATION_ID, cohortAssociationId);
        entity.put(StaffCohortAssociationResource.BEGIN_DATE, cohortAssnBeginDate);
        return entity;
    }

    @Test
    public void testCreate() {
        Response response = staffResource.create(new EntityBody(createTestEntity()), httpHeaders, uriInfo);
        assertEquals("Status code should be 201", Status.CREATED.getStatusCode(), response.getStatus());

        String id = ResourceTestUtil.parseIdFromLocation(response);
        assertNotNull("ID should not be null", id);
    }

    @Test
    public void testRead() {
        //create one entity
        Response createResponse = staffResource.create(new EntityBody(createTestEntity()), httpHeaders, uriInfo);
        String id = ResourceTestUtil.parseIdFromLocation(createResponse);
        Response response = staffResource.read(id, httpHeaders, uriInfo);

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
        Response createResponse = staffResource.create(new EntityBody(createTestEntity()), httpHeaders, uriInfo);
        String id = ResourceTestUtil.parseIdFromLocation(createResponse);

        //delete it
        Response response = staffResource.delete(id, httpHeaders, uriInfo);
        assertEquals("Status code should be NO_CONTENT", Status.NO_CONTENT.getStatusCode(), response.getStatus());

        try {
            @SuppressWarnings("unused")
            Response getResponse = staffResource.read(id, httpHeaders, uriInfo);
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
        Response createResponse = staffResource.create(new EntityBody(createTestEntity()), httpHeaders, uriInfo);
        String id = ResourceTestUtil.parseIdFromLocation(createResponse);

        //update it
        Response response = staffResource.update(id, new EntityBody(createTestUpdateEntity()), httpHeaders, uriInfo);
        assertEquals("Status code should be NO_CONTENT", Status.NO_CONTENT.getStatusCode(), response.getStatus());

        //try to get it
        Response getResponse = staffResource.read(id, httpHeaders, uriInfo);
        assertEquals("Status code should be OK", Status.OK.getStatusCode(), getResponse.getStatus());
        EntityResponse entityResponse = (EntityResponse) getResponse.getEntity();
        EntityBody body = (EntityBody) entityResponse.getEntity();
        assertNotNull("Should return an entity", body);
        assertEquals(ParameterConstants.STAFF_ID + " should be " + firstStaffId, firstStaffId, body.get(ParameterConstants.STAFF_ID));
        assertEquals(StaffResource.NAME + " should be " + secondName, secondName, body.get(StaffResource.NAME));
        assertNotNull("Should include links", body.get(ResourceConstants.LINKS));
    }

    @Test
    public void testReadAll() {
        //create two entities
        staffResource.create(new EntityBody(createTestEntity()), httpHeaders, uriInfo);
        staffResource.create(new EntityBody(createTestSecondaryEntity()), httpHeaders, uriInfo);

        //read everything
        Response response = staffResource.readAll(0, 100, httpHeaders, uriInfo);
        assertEquals("Status code should be OK", Status.OK.getStatusCode(), response.getStatus());

        @SuppressWarnings("unchecked")
        EntityResponse entityResponse = (EntityResponse) response.getEntity();
        List<EntityBody> results = (List<EntityBody>) entityResponse.getEntity();
        assertNotNull("Should return entities", results);
        assertTrue("Should have at least two entities", results.size() >= 2);
    }

    @Test
    public void testReadCommaSeparatedResources() {
        Response response = staffResource.read(getIDList(ResourceNames.STAFF), httpHeaders, uriInfo);
        assertEquals("Status code should be 200", Status.OK.getStatusCode(), response.getStatus());

        @SuppressWarnings("unchecked")
        EntityResponse entityResponse = (EntityResponse) response.getEntity();
        List<EntityBody> results = (List<EntityBody>) entityResponse.getEntity();
        assertEquals("Should get 2 entities", 2, results.size());

        EntityBody body1 = results.get(0);
        assertNotNull("Should not be null", body1);
        assertEquals(ParameterConstants.STAFF_ID + " should be " + firstStaffId, firstStaffId, body1.get(ParameterConstants.STAFF_ID));
        assertNotNull("Should include links", body1.get(ResourceConstants.LINKS));

        EntityBody body2 = results.get(1);
        assertNotNull("Should not be null", body2);
        assertEquals(ParameterConstants.STAFF_ID + " should be " + secondStaffId, secondStaffId, body2.get(ParameterConstants.STAFF_ID));
        assertNotNull("Should include links", body2.get(ResourceConstants.LINKS));
    }

    @Test
    public void testGetEdOrgAssociations() {
        //create one entity
        Response createResponse = staffResource.create(new EntityBody(createTestEntity()), httpHeaders, uriInfo);
        String staffId = ResourceTestUtil.parseIdFromLocation(createResponse);

        createResponse = edOrgResource.create(new EntityBody(createTestEdOrgEntity()), httpHeaders, uriInfo);
        String targetId = ResourceTestUtil.parseIdFromLocation(createResponse);

        Map<String, Object> map = createTestEdOrgAssociationEntity();
        map.put(StaffEducationOrganizationAssociationResource.EDUCATION_ORGANIZATION_REFERENCE, targetId);
        map.put(StaffEducationOrganizationAssociationResource.STAFF_REFERENCE, staffId);

        createResponse = staffEdOrgAssn.create(new EntityBody(map), httpHeaders, uriInfo);
        //String associationId = parseIdFromLocation(createResponse);

        Response response = staffResource.getStaffEducationOrganizationAssociations(staffId, httpHeaders, uriInfo);

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
            assertTrue("Should have one entity; actually have " + results.size(), results.size() == 1);
            body = results.get(0);
        } else {
            fail("Response entity not recognized: " + response);
            return;
        }

        assertNotNull("Should return an entity", body);
        assertEquals(StaffEducationOrganizationAssociationResource.STAFF_CLASSIFICATION + " should be " + staffClassification, staffClassification, body.get(StaffEducationOrganizationAssociationResource.STAFF_CLASSIFICATION));
        assertNotNull("Should include links", body.get(ResourceConstants.LINKS));
    }

    @Test
    public void testGetAssociatedEdOrgs() {
        //create one entity
        Response createResponse = staffResource.create(new EntityBody(createTestEntity()), httpHeaders, uriInfo);
        String staffId = ResourceTestUtil.parseIdFromLocation(createResponse);

        createResponse = edOrgResource.create(new EntityBody(createTestEdOrgEntity()), httpHeaders, uriInfo);
        String targetId = ResourceTestUtil.parseIdFromLocation(createResponse);

        Map<String, Object> map = createTestEdOrgAssociationEntity();
        map.put(StaffEducationOrganizationAssociationResource.EDUCATION_ORGANIZATION_REFERENCE, targetId);
        map.put(StaffEducationOrganizationAssociationResource.STAFF_REFERENCE, staffId);

        createResponse = staffEdOrgAssn.create(new EntityBody(map), httpHeaders, uriInfo);
        //String associationId = parseIdFromLocation(createResponse);

        Response response = staffResource.getStaffEducationOrganizationAssociationEducationOrganizations(staffId, httpHeaders, uriInfo);

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
            assertTrue("Should have one entity; actually have " + results.size(), results.size() == 1);
            body = results.get(0);
        } else {
            fail("Response entity not recognized: " + response);
            return;
        }

        assertNotNull("Should return an entity", body);
        assertEquals("organizationCategories should be State Education Agency", "State Education Agency", body.get("organizationCategories"));
        assertNotNull("Should include links", body.get(ResourceConstants.LINKS));
    }

    @Test
    public void testGetCohortAssociations() {
        //create one entity
        Response createResponse = staffResource.create(new EntityBody(createTestEntity()), httpHeaders, uriInfo);
        String staffId = ResourceTestUtil.parseIdFromLocation(createResponse);

        createResponse = cohortResource.create(new EntityBody(createTestCohortEntity()), httpHeaders, uriInfo);
        String targetId = ResourceTestUtil.parseIdFromLocation(createResponse);

        Map<String, Object> map = createTestCohortAssociationEntity();
        map.put(ParameterConstants.COHORT_ID, targetId);
        map.put(ParameterConstants.STAFF_ID, staffId);

        createResponse = staffCohortAssn.create(new EntityBody(map), httpHeaders, uriInfo);
        //String associationId = parseIdFromLocation(createResponse);

        Response response = staffResource.getStaffCohortAssociations(staffId, httpHeaders, uriInfo);

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
            assertTrue("Should have one entity; actually have " + results.size(), results.size() == 1);
            body = results.get(0);
        } else {
            fail("Response entity not recognized: " + response);
            return;
        }

        assertNotNull("Should return an entity", body);
        assertEquals(StaffCohortAssociationResource.BEGIN_DATE + " should be " + cohortAssnBeginDate, cohortAssnBeginDate, body.get(StaffCohortAssociationResource.BEGIN_DATE));
        assertNotNull("Should include links", body.get(ResourceConstants.LINKS));
    }

    @Test
    public void testGetAssociatedCohorts() {
        //create one entity
        Response createResponse = staffResource.create(new EntityBody(createTestEntity()), httpHeaders, uriInfo);
        String staffId = ResourceTestUtil.parseIdFromLocation(createResponse);

        createResponse = cohortResource.create(new EntityBody(createTestCohortEntity()), httpHeaders, uriInfo);
        String cohortId = ResourceTestUtil.parseIdFromLocation(createResponse);

        Map<String, Object> map = createTestCohortAssociationEntity();
        map.put(ParameterConstants.STAFF_ID, staffId);
        map.put(ParameterConstants.COHORT_ID, cohortId);

        createResponse = staffCohortAssn.create(new EntityBody(map), httpHeaders, uriInfo);

        Response response = staffResource.getStaffCohortAssociationCohorts(staffId, httpHeaders, uriInfo);

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
            assertTrue("Should have one entity; actually have " + results.size(), results.size() == 1);
            body = results.get(0);
        } else {
            fail("Response entity not recognized: " + response);
            return;
        }

        assertNotNull("Should return an entity", body);
        assertEquals(CohortResource.COHORT_TYPE + " should be " + cohortType, cohortType, body.get(CohortResource.COHORT_TYPE));
        assertNotNull("Should include links", body.get(ResourceConstants.LINKS));
    }

    private Map<String, Object> createTestProgramEntity() {
        Map<String, Object> entity = new HashMap<String, Object>();
        entity.put("programId", "1001");
       return entity;
    }

    private Map<String, Object> createTestStaffProgramAssociationEntity() {
        Map<String, Object> entity = new HashMap<String, Object>();
        entity.put("beginDate", "2012-01-01");
        return entity;
    }

    @Test
    public void testGetStaffProgramAssociations() {
        //create one entity
        Response createResponse = programResource.create(new EntityBody(createTestProgramEntity()), httpHeaders, uriInfo);
        String programId = ResourceTestUtil.parseIdFromLocation(createResponse);

        createResponse = staffResource.create(new EntityBody(createTestEntity()), httpHeaders, uriInfo);
        String staffId = ResourceTestUtil.parseIdFromLocation(createResponse);

        Map<String, Object> map = createTestStaffProgramAssociationEntity();
        map.put("programId", programId);
        map.put("staffId", staffId);

        createResponse = staffProgramAssociationResource.create(new EntityBody(map), httpHeaders, uriInfo);

        Response response = staffResource.getStaffProgramAssociations(staffId, httpHeaders, uriInfo);

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
        assertEquals("beginDate should be 2012-01-01", "2012-01-01", body.get("beginDate"));
        assertNotNull("Should include links", body.get(ResourceConstants.LINKS));
    }

    @Test
    public void testGetStaffProgramAssociationProgram() {
        //create one entity
        Response createResponse = programResource.create(new EntityBody(createTestProgramEntity()), httpHeaders, uriInfo);
        String programId = ResourceTestUtil.parseIdFromLocation(createResponse);

        createResponse = staffResource.create(new EntityBody(createTestEntity()), httpHeaders, uriInfo);
        String staffId = ResourceTestUtil.parseIdFromLocation(createResponse);

        Map<String, Object> map = createTestStaffProgramAssociationEntity();
        map.put("programId", programId);
        map.put("staffId", staffId);

        createResponse = staffProgramAssociationResource.create(new EntityBody(map), httpHeaders, uriInfo);

        Response response = staffResource.getStaffProgramAssociationPrograms(staffId, httpHeaders, uriInfo);

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
        assertEquals("studentUniqueStateId should be 1001", "1001", body.get("programId"));
        assertNotNull("Should include links", body.get(ResourceConstants.LINKS));
    }

    private String getIDList(String resource) {
        //create more resources
        Response createResponse1 = staffResource.create(new EntityBody(createTestEntity()), httpHeaders, uriInfo);
        Response createResponse2 = staffResource.create(new EntityBody(createTestSecondaryEntity()), httpHeaders, uriInfo);

        return ResourceTestUtil.parseIdFromLocation(createResponse1) + "," + ResourceTestUtil.parseIdFromLocation(createResponse2);
    }
}

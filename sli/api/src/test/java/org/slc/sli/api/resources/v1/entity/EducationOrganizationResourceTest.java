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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import org.slc.sli.api.client.constants.ResourceConstants;
import org.slc.sli.api.client.constants.ResourceNames;
import org.slc.sli.api.client.constants.v1.ParameterConstants;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.representation.EntityResponse;
import org.slc.sli.api.resources.SecurityContextInjector;
import org.slc.sli.api.resources.util.ResourceTestUtil;
import org.slc.sli.api.resources.v1.HypermediaType;
import org.slc.sli.api.resources.v1.association.StaffEducationOrganizationAssociationResource;
import org.slc.sli.api.service.EntityNotFoundException;
import org.slc.sli.api.test.WebContextTestExecutionListener;

/**
 * Unit tests for the resource representing an education organization
 * @author vmcglaughlin
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class })
public class EducationOrganizationResourceTest {

    @Autowired
    EducationOrganizationResource edOrgResource; //class under test

    @Autowired
    StaffResource staffResource;

    @Autowired
    StaffEducationOrganizationAssociationResource staffEducationOrganizationAssociationResource;

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

    private Map<String, Object> createTestEntity() {
        Map<String, Object> entity = new HashMap<String, Object>();
        entity.put("organizationCategories", "State Education Agency");
        entity.put("nameOfInstitution", "Primero Test Institution");
        entity.put(ParameterConstants.EDUCATION_ORGANIZATION_ID, 1234);
        return entity;
    }

    private Map<String, Object> createTestUpdateEntity() {
        Map<String, Object> entity = new HashMap<String, Object>();
        entity.put("organizationCategories", "Local Education Agency");
        entity.put("nameOfInstitution", "Primero Test Institution");
        entity.put(ParameterConstants.EDUCATION_ORGANIZATION_ID, 1234);
        return entity;
    }

    private Map<String, Object> createTestSecondaryEntity() {
        Map<String, Object> entity = new HashMap<String, Object>();
        entity.put("organizationCategories", "State Education Agency");
        entity.put("nameOfInstitution", "Segundo Test Institution");
        entity.put(ParameterConstants.EDUCATION_ORGANIZATION_ID, 5678);
        return entity;
    }

    private Map<String, Object> createStaffTestEntity() {
        Map<String, Object> entity = new HashMap<String, Object>();
        entity.put(StaffResource.NAME, "Dua");
        entity.put(StaffResource.SEX, "Female");
        entity.put(StaffResource.HISPANIC_LATINO_ETHNICITY, "true");
        entity.put(StaffResource.EDUCATION_LEVEL, "PhD");
        entity.put(ParameterConstants.STAFF_ID, 1234);
        return entity;
    }

    private Map<String, Object> createStaffEdOrgAssocTestEntity(String staffId, String edOrgId) {
        Map<String, Object> entity = new HashMap<String, Object>();
        entity.put(ParameterConstants.STAFF_EDUCATION_ORGANIZATION_ID, 1234);
        entity.put(StaffEducationOrganizationAssociationResource.STAFF_REFERENCE, staffId);
        entity.put(StaffEducationOrganizationAssociationResource.EDUCATION_ORGANIZATION_REFERENCE, edOrgId);
        return entity;
    }

    @Test
    public void testCreate() {
        Response response = edOrgResource.create(new EntityBody(createTestEntity()), httpHeaders, uriInfo);
        assertEquals("Status code should be 201", Status.CREATED.getStatusCode(), response.getStatus());

        String id = ResourceTestUtil.parseIdFromLocation(response);
        assertNotNull("ID should not be null", id);
    }

    @Test
    public void testRead() {
        //create one entity
        Response createResponse = edOrgResource.create(new EntityBody(createTestEntity()), httpHeaders, uriInfo);
        String id = ResourceTestUtil.parseIdFromLocation(createResponse);
        Response response = edOrgResource.read(id, httpHeaders, uriInfo);

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

    @Test(expected = EntityNotFoundException.class)
    public void testDelete() {
        //create one entity
        Response createResponse = edOrgResource.create(new EntityBody(createTestEntity()), httpHeaders, uriInfo);
        String id = ResourceTestUtil.parseIdFromLocation(createResponse);

        //delete it
        Response response = edOrgResource.delete(id, httpHeaders, uriInfo);
        assertEquals("Status code should be NO_CONTENT", Status.NO_CONTENT.getStatusCode(), response.getStatus());

        @SuppressWarnings("unused")
        Response getResponse = edOrgResource.read(id, httpHeaders, uriInfo);
    }

    @Test
    public void testUpdate() {
        //create one entity
        Response createResponse = edOrgResource.create(new EntityBody(createTestEntity()), httpHeaders, uriInfo);
        String id = ResourceTestUtil.parseIdFromLocation(createResponse);

        //update it
        Response response = edOrgResource.update(id, new EntityBody(createTestUpdateEntity()), httpHeaders, uriInfo);
        assertEquals("Status code should be NO_CONTENT", Status.NO_CONTENT.getStatusCode(), response.getStatus());

        //try to get it
        Response getResponse = edOrgResource.read(id, httpHeaders, uriInfo);
        assertEquals("Status code should be OK", Status.OK.getStatusCode(), getResponse.getStatus());
        EntityResponse entityResponse = (EntityResponse) getResponse.getEntity();
        EntityBody body = (EntityBody) entityResponse.getEntity();
        assertNotNull("Should return an entity", body);
        assertEquals(ParameterConstants.EDUCATION_ORGANIZATION_ID + " should be 1234", body.get(ParameterConstants.EDUCATION_ORGANIZATION_ID), 1234);
        assertEquals("organizationCategories should be 8", body.get("organizationCategories"), "Local Education Agency");
        assertNotNull("Should include links", body.get(ResourceConstants.LINKS));
    }

    @Test
    public void testReadAll() {
        //create two entities
        edOrgResource.create(new EntityBody(createTestEntity()), httpHeaders, uriInfo);
        edOrgResource.create(new EntityBody(createTestSecondaryEntity()), httpHeaders, uriInfo);

        //read everything
        Response response = edOrgResource.readAll(0, 100, httpHeaders, uriInfo);
        assertEquals("Status code should be OK", Status.OK.getStatusCode(), response.getStatus());

        @SuppressWarnings("unchecked")
        EntityResponse entityResponse = (EntityResponse) response.getEntity();
        List<EntityBody> results = (List<EntityBody>) entityResponse.getEntity();
        assertNotNull("Should return entities", results);
        assertTrue("Should have at least two entities", results.size() >= 2);
    }

    @Test
    public void testGetStaffEducationOrganizationAssociations() {
        //create ed org entity
        Response createResponse = edOrgResource.create(new EntityBody(createTestEntity()), httpHeaders, uriInfo);
        String edOrgId = ResourceTestUtil.parseIdFromLocation(createResponse);
        //create staff entity
        createResponse = staffResource.create(new EntityBody(createStaffTestEntity()), httpHeaders, uriInfo);
        String staffId = ResourceTestUtil.parseIdFromLocation(createResponse);
        // create association entity
        staffEducationOrganizationAssociationResource.create(new EntityBody(
                createStaffEdOrgAssocTestEntity(staffId, edOrgId)), httpHeaders, uriInfo);

        //read the association
        Response response = edOrgResource.getStaffEducationOrganizationAssociations(edOrgId, httpHeaders, uriInfo);
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
        assertEquals("Staff IDs should equal", staffId, body.get(StaffEducationOrganizationAssociationResource.STAFF_REFERENCE));
        assertEquals("EdOrg IDs should equal", edOrgId, body.get(StaffEducationOrganizationAssociationResource.EDUCATION_ORGANIZATION_REFERENCE));
        assertNotNull("Should include links", body.get(ResourceConstants.LINKS));
    }

    @Test
    public void testGetStaffEducationOrganizationAssociationStaff() {
        //create ed org entity
        Response createResponse = edOrgResource.create(new EntityBody(createTestEntity()), httpHeaders, uriInfo);
        String edOrgId = ResourceTestUtil.parseIdFromLocation(createResponse);
        //create staff entity
        createResponse = staffResource.create(new EntityBody(createStaffTestEntity()), httpHeaders, uriInfo);
        String staffId = ResourceTestUtil.parseIdFromLocation(createResponse);
        // create association entity
        staffEducationOrganizationAssociationResource.create(new EntityBody(
                createStaffEdOrgAssocTestEntity(staffId, edOrgId)), httpHeaders, uriInfo);

        //read the target
        Response response = edOrgResource.getStaffEducationOrganizationAssociationStaff(edOrgId, httpHeaders, uriInfo);
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
        assertEquals("Education level should equal", "PhD", body.get(StaffResource.EDUCATION_LEVEL));
        assertNotNull("Should include links", body.get(ResourceConstants.LINKS));
    }

    @Test
    public void testReadCommaSeparatedResources() {
        Response response = edOrgResource.read(getIDList(ResourceNames.EDUCATION_ORGANIZATIONS), httpHeaders, uriInfo);
        assertEquals("Status code should be 200", Status.OK.getStatusCode(), response.getStatus());

        @SuppressWarnings("unchecked")
        EntityResponse entityResponse = (EntityResponse) response.getEntity();
        List<EntityBody> results = (List<EntityBody>) entityResponse.getEntity();
        assertEquals("Should get 2 entities", results.size(), 2);

        EntityBody body1 = results.get(0);
        assertNotNull("Should not be null", body1);
        assertEquals(ParameterConstants.EDUCATION_ORGANIZATION_ID + " should be 1234", body1.get(ParameterConstants.EDUCATION_ORGANIZATION_ID), 1234);
        assertNotNull("Should include links", body1.get(ResourceConstants.LINKS));

        EntityBody body2 = results.get(1);
        assertNotNull("Should not be null", body2);
        assertEquals(ParameterConstants.EDUCATION_ORGANIZATION_ID + " should be 5678", body2.get(ParameterConstants.EDUCATION_ORGANIZATION_ID), 5678);
        assertNotNull("Should include links", body2.get(ResourceConstants.LINKS));
    }

    private String getIDList(String resource) {
        //create more resources
        Response createResponse1 = edOrgResource.create(new EntityBody(createTestEntity()), httpHeaders, uriInfo);
        Response createResponse2 = edOrgResource.create(new EntityBody(createTestSecondaryEntity()), httpHeaders, uriInfo);

        return ResourceTestUtil.parseIdFromLocation(createResponse1) + "," + ResourceTestUtil.parseIdFromLocation(createResponse2);
    }
}

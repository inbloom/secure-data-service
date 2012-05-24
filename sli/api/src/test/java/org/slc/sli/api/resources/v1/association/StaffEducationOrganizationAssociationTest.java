package org.slc.sli.api.resources.v1.association;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.resources.SecurityContextInjector;
import org.slc.sli.api.resources.util.ResourceTestUtil;
import org.slc.sli.api.resources.v1.HypermediaType;
import org.slc.sli.api.resources.v1.entity.EducationOrganizationResource;
import org.slc.sli.api.resources.v1.entity.StaffResource;
import org.slc.sli.api.test.WebContextTestExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import com.sun.jersey.core.util.MultivaluedMapImpl;

/**
 * JUnit tests for StaffEducationOrganizationAssociation
 * @author chung
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class })
public class StaffEducationOrganizationAssociationTest {

    private final String staffResourceName = "StaffResource";
    private final String educationOrganizationResourceName = "EducationOrganizationResource";
    private final String staffEducationOrganizationAssociationResourceName = "StaffEducationOrganizationAssociation";

    @Autowired
    private SecurityContextInjector injector;
    @Autowired
    private StaffResource staffResource;
    @Autowired
    private EducationOrganizationResource educationOrganizationResource;
    @Autowired
    private StaffEducationOrganizationAssociationResource staffEducationOrganizationAssociationResource;

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

    @Test
    public void testGetStaffEducationOrganizationAssocationStaff() {
        Response createResponse = staffResource.create(new EntityBody(
                ResourceTestUtil.createTestEntity(staffResourceName)), httpHeaders, uriInfo);
        String staffId = ResourceTestUtil.parseIdFromLocation(createResponse);
        createResponse = educationOrganizationResource.create(new EntityBody(
                ResourceTestUtil.createTestEntity(educationOrganizationResourceName)), httpHeaders, uriInfo);
        String edOrgId = ResourceTestUtil.parseIdFromLocation(createResponse);

        Map<String, Object> map = ResourceTestUtil.createTestAssociationEntity(
                staffEducationOrganizationAssociationResourceName, staffResourceName, staffId, educationOrganizationResourceName, edOrgId);
        map.put(StaffEducationOrganizationAssociationResource.STAFF_REFERENCE, staffId);
        map.put(StaffEducationOrganizationAssociationResource.EDUCATION_ORGANIZATION_REFERENCE, edOrgId);
        createResponse = staffEducationOrganizationAssociationResource.create(new EntityBody(map), httpHeaders, uriInfo);
        String id = ResourceTestUtil.parseIdFromLocation(createResponse);

        Response response = staffEducationOrganizationAssociationResource.getStaffEducationOrganizationAssocationStaff(id, 0, 100, httpHeaders, uriInfo);
        EntityBody body = ResourceTestUtil.assertions(response);
        assertEquals("Entity type should match", "staff", body.get("entityType"));
        assertEquals("ID should match", staffId, body.get("id"));
    }

    @Test
    public void testGetStaffEducationOrganizationAssocationEducationOrganizations() {
        Response createResponse = staffResource.create(new EntityBody(
                ResourceTestUtil.createTestEntity(staffResourceName)), httpHeaders, uriInfo);
        String staffId = ResourceTestUtil.parseIdFromLocation(createResponse);
        createResponse = educationOrganizationResource.create(new EntityBody(
                ResourceTestUtil.createTestEntity(educationOrganizationResourceName)), httpHeaders, uriInfo);
        String edOrgId = ResourceTestUtil.parseIdFromLocation(createResponse);

        Map<String, Object> map = ResourceTestUtil.createTestAssociationEntity(
                staffEducationOrganizationAssociationResourceName, staffResourceName, staffId, educationOrganizationResourceName, edOrgId);
        map.put(StaffEducationOrganizationAssociationResource.STAFF_REFERENCE, staffId);
        map.put(StaffEducationOrganizationAssociationResource.EDUCATION_ORGANIZATION_REFERENCE, edOrgId);
        createResponse = staffEducationOrganizationAssociationResource.create(new EntityBody(map), httpHeaders, uriInfo);
        String id = ResourceTestUtil.parseIdFromLocation(createResponse);

        Response response = staffEducationOrganizationAssociationResource.getStaffEducationOrganizationAssocationEducationOrganizations(id, 0, 100, httpHeaders, uriInfo);
        EntityBody body = ResourceTestUtil.assertions(response);
        assertEquals("Entity type should match", "educationOrganization", body.get("entityType"));
        assertEquals("ID should match", edOrgId, body.get("id"));
    }
}

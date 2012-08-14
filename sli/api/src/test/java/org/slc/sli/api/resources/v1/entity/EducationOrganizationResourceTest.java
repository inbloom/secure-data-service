/*
 * Copyright 2012 Shared Learning Collaborative, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


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

import org.slc.sli.api.constants.ParameterConstants;
import org.slc.sli.api.constants.ResourceConstants;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.representation.EntityResponse;
import org.slc.sli.api.resources.SecurityContextInjector;
import org.slc.sli.api.resources.util.ResourceTestUtil;
import org.slc.sli.api.resources.v1.HypermediaType;
import org.slc.sli.api.resources.v1.association.StaffEducationOrganizationAssociationResource;
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
        injector.setAccessAllAdminContextWithElevatedRights();

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
}

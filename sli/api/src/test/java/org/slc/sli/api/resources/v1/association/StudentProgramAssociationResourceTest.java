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


package org.slc.sli.api.resources.v1.association;

import com.sun.jersey.core.util.MultivaluedMapImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slc.sli.api.constants.ParameterConstants;
import org.slc.sli.api.constants.ResourceConstants;
import org.slc.sli.api.constants.ResourceNames;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.representation.EntityResponse;
import org.slc.sli.api.resources.SecurityContextInjector;
import org.slc.sli.api.resources.util.ResourceTestUtil;
import org.slc.sli.api.resources.v1.HypermediaType;
import org.slc.sli.api.resources.v1.entity.ProgramResource;
import org.slc.sli.api.resources.v1.entity.StudentResource;
import org.slc.sli.api.service.EntityNotFoundException;
import org.slc.sli.api.test.WebContextTestExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit tests for the resource representing a staffProgramAssociation
 *
 * @author jtully
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class })
public class StudentProgramAssociationResourceTest {
    @Autowired
    StudentResource studentResource;
    @Autowired
    ProgramResource programResource;
    @Autowired
    StudentProgramAssociationResource studentProgramAssociationResource; //class under test

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

    private Map<String, Object> createTestEntity(String studentId, String programId) {
        Map<String, Object> entity = new HashMap<String, Object>();
        entity.put("studentId", studentId);
        entity.put("programId", programId);
        entity.put(ParameterConstants.STUDENT_PROGRAM_ASSOCIATION_ID, 1234);
        return entity;
    }

    private Map<String, Object> createTestUpdateEntity(String studentId) {
        Map<String, Object> entity = new HashMap<String, Object>();
        entity.put("studentId", studentId);
        entity.put("programId", ResourceTestUtil.parseIdFromLocation(programResource.create(new EntityBody(), httpHeaders, uriInfo)));
        entity.put(ParameterConstants.STUDENT_PROGRAM_ASSOCIATION_ID, 1234);
        return entity;
    }

    private Map<String, Object> createTestSecondaryEntity() {
        Map<String, Object> entity = new HashMap<String, Object>();

        entity.put("studentId", ResourceTestUtil.parseIdFromLocation(studentResource.create(new EntityBody(), httpHeaders, uriInfo)));
        entity.put("programId", ResourceTestUtil.parseIdFromLocation(programResource.create(new EntityBody(), httpHeaders, uriInfo)));
        entity.put(ParameterConstants.STUDENT_PROGRAM_ASSOCIATION_ID, 5678);
        return entity;
    }

    @Test
    public void testCreate() {
        Response res = studentResource.create(new EntityBody(), httpHeaders, uriInfo);
        String studentId = ResourceTestUtil.parseIdFromLocation(res);

        Response res1 = programResource.create(new EntityBody(), httpHeaders, uriInfo);
        String programId = ResourceTestUtil.parseIdFromLocation(res1);

        Response response = studentProgramAssociationResource.create(new EntityBody(createTestEntity(studentId, programId)), httpHeaders, uriInfo);
        assertEquals("Status code should be 201", Status.CREATED.getStatusCode(), response.getStatus());

        String id = ResourceTestUtil.parseIdFromLocation(response);
        assertNotNull("ID should not be null", id);
    }

    @Test
    public void testRead() {
        Response res = studentResource.create(new EntityBody(), httpHeaders, uriInfo);
        String studentId = ResourceTestUtil.parseIdFromLocation(res);

        Response res1 = programResource.create(new EntityBody(), httpHeaders, uriInfo);
        String programId = ResourceTestUtil.parseIdFromLocation(res1);

        //create one entity
        Response createResponse = studentProgramAssociationResource.create(new EntityBody(createTestEntity(studentId, programId)), httpHeaders, uriInfo);
        String id = ResourceTestUtil.parseIdFromLocation(createResponse);
        Response response = studentProgramAssociationResource.read(id, httpHeaders, uriInfo);

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
        Response res = studentResource.create(new EntityBody(), httpHeaders, uriInfo);
        String studentId = ResourceTestUtil.parseIdFromLocation(res);

        Response res1 = programResource.create(new EntityBody(), httpHeaders, uriInfo);
        String programId = ResourceTestUtil.parseIdFromLocation(res1);

        //create one entity
        Response createResponse = studentProgramAssociationResource.create(new EntityBody(createTestEntity(studentId, programId)), httpHeaders, uriInfo);
        String id = ResourceTestUtil.parseIdFromLocation(createResponse);

        //delete it
        Response response = studentProgramAssociationResource.delete(id, httpHeaders, uriInfo);
        assertEquals("Status code should be NO_CONTENT", Status.NO_CONTENT.getStatusCode(), response.getStatus());

        try {
            @SuppressWarnings("unused")
            Response getResponse = studentProgramAssociationResource.read(id, httpHeaders, uriInfo);
            fail("should have thrown EntityNotFoundException");
        } catch (EntityNotFoundException e) {
            return;
        } catch (Exception e) {
            fail("threw wrong exception: " + e);
        }
    }

    @Test
    public void testUpdate() {
        Response res = studentResource.create(new EntityBody(), httpHeaders, uriInfo);
        String studentId = ResourceTestUtil.parseIdFromLocation(res);

        Response res1 = programResource.create(new EntityBody(), httpHeaders, uriInfo);
        String programId = ResourceTestUtil.parseIdFromLocation(res1);

        //create one entity
        Response createResponse = studentProgramAssociationResource.create(new EntityBody(createTestEntity(studentId, programId)), httpHeaders, uriInfo);
        String id = ResourceTestUtil.parseIdFromLocation(createResponse);

        //update it
        Response response = studentProgramAssociationResource.update(id, new EntityBody(createTestUpdateEntity(studentId)), httpHeaders, uriInfo);
        assertEquals("Status code should be NO_CONTENT", Status.NO_CONTENT.getStatusCode(), response.getStatus());

        //try to get it
        Response getResponse = studentProgramAssociationResource.read(id, httpHeaders, uriInfo);
        assertEquals("Status code should be OK", Status.OK.getStatusCode(), getResponse.getStatus());
        EntityResponse entityResponse = (EntityResponse) getResponse.getEntity();
        EntityBody body = (EntityBody) entityResponse.getEntity();
        assertNotNull("Should return an entity", body);
        assertEquals(ParameterConstants.STUDENT_PROGRAM_ASSOCIATION_ID + " should be 1234", body.get(ParameterConstants.STUDENT_PROGRAM_ASSOCIATION_ID), 1234);
        assertNotNull("Should include links", body.get(ResourceConstants.LINKS));
    }

    @Test
    public void testReadAll() {
        Response res = studentResource.create(new EntityBody(), httpHeaders, uriInfo);
        String studentId = ResourceTestUtil.parseIdFromLocation(res);

        Response res1 = programResource.create(new EntityBody(), httpHeaders, uriInfo);
        String programId = ResourceTestUtil.parseIdFromLocation(res1);

        //create two entities
        studentProgramAssociationResource.create(new EntityBody(createTestEntity(studentId, programId)), httpHeaders, uriInfo);
        studentProgramAssociationResource.create(new EntityBody(createTestSecondaryEntity()), httpHeaders, uriInfo);

        //read everything
        Response response = studentProgramAssociationResource.readAll(0, 100, httpHeaders, uriInfo);
        assertEquals("Status code should be OK", Status.OK.getStatusCode(), response.getStatus());

        @SuppressWarnings("unchecked")
        EntityResponse entityResponse = (EntityResponse) response.getEntity();
        List<EntityBody> results = (List<EntityBody>) entityResponse.getEntity();
        assertNotNull("Should return entities", results);
        assertTrue("Should have at least two entities", results.size() >= 2);
    }

    @Test
    public void testReadCommaSeparatedResources() {
        Response response = studentProgramAssociationResource.read(getIDList(ResourceNames.STUDENT_PROGRAM_ASSOCIATIONS), httpHeaders, uriInfo);
        assertEquals("Status code should be 200", Status.OK.getStatusCode(), response.getStatus());

        @SuppressWarnings("unchecked")
        EntityResponse entityResponse = (EntityResponse) response.getEntity();
        List<EntityBody> results = (List<EntityBody>) entityResponse.getEntity();
        assertEquals("Should get 2 entities", results.size(), 2);

        EntityBody body1 = results.get(0);
        assertNotNull("Should not be null", body1);
        assertEquals(ParameterConstants.STUDENT_PROGRAM_ASSOCIATION_ID + " should be 1234", body1.get(ParameterConstants.STUDENT_PROGRAM_ASSOCIATION_ID), 1234);
        assertNotNull("Should include links", body1.get(ResourceConstants.LINKS));

        EntityBody body2 = results.get(1);
        assertNotNull("Should not be null", body2);
        assertEquals(ParameterConstants.STUDENT_PROGRAM_ASSOCIATION_ID + " should be 5678", body2.get(ParameterConstants.STUDENT_PROGRAM_ASSOCIATION_ID), 5678);
        assertNotNull("Should include links", body2.get(ResourceConstants.LINKS));
    }

    @Test
    public void testGetStudentProgramAssociationStudents() {
        Response createResponse = studentResource.create(new EntityBody(
                ResourceTestUtil.createTestEntity("StudentResource")), httpHeaders, uriInfo);
        String studentId = ResourceTestUtil.parseIdFromLocation(createResponse);
        createResponse = programResource.create(new EntityBody(
                ResourceTestUtil.createTestEntity("ProgramResource")), httpHeaders, uriInfo);
        String programId = ResourceTestUtil.parseIdFromLocation(createResponse);

        Map<String, Object> map = ResourceTestUtil.createTestAssociationEntity(
                "StudentProgramAssociationResource", "StudentResource", studentId, "ProgramResource", programId);
        createResponse = studentProgramAssociationResource.create(new EntityBody(map), httpHeaders, uriInfo);
        String id = ResourceTestUtil.parseIdFromLocation(createResponse);

        Response response = studentProgramAssociationResource.getStudentProgramAssociationStudents(id, 0, 100, httpHeaders, uriInfo);
        EntityBody body = ResourceTestUtil.assertions(response);
        assertEquals("Entity type should match", "student", body.get("entityType"));
        assertEquals("ID should match", studentId, body.get("id"));
    }

    @Test
    public void testGetStudentProgramAssociationPrograms() {
        Response createResponse = studentResource.create(new EntityBody(
                ResourceTestUtil.createTestEntity("StudentResource")), httpHeaders, uriInfo);
        String studentId = ResourceTestUtil.parseIdFromLocation(createResponse);
        createResponse = programResource.create(new EntityBody(
                ResourceTestUtil.createTestEntity("ProgramResource")), httpHeaders, uriInfo);
        String programId = ResourceTestUtil.parseIdFromLocation(createResponse);

        Map<String, Object> map = ResourceTestUtil.createTestAssociationEntity(
                "StudentProgramAssociationResource", "StudentResource", studentId, "ProgramResource", programId);
        createResponse = studentProgramAssociationResource.create(new EntityBody(map), httpHeaders, uriInfo);
        String id = ResourceTestUtil.parseIdFromLocation(createResponse);

        Response response = studentProgramAssociationResource.getStudentProgramAssociationPrograms(id, 0, 100, httpHeaders, uriInfo);
        EntityBody body = ResourceTestUtil.assertions(response);
        assertEquals("Entity type should match", "program", body.get("entityType"));
        assertEquals("ID should match", programId, body.get("id"));
    }

    private String getIDList(String resource) {
        Response res = studentResource.create(new EntityBody(), httpHeaders, uriInfo);
        String studentId = ResourceTestUtil.parseIdFromLocation(res);

        Response res1 = programResource.create(new EntityBody(), httpHeaders, uriInfo);
        String programId = ResourceTestUtil.parseIdFromLocation(res1);

        //create more resources
        Response createResponse1 = studentProgramAssociationResource.create(new EntityBody(createTestEntity(studentId, programId)), httpHeaders, uriInfo);
        Response createResponse2 = studentProgramAssociationResource.create(new EntityBody(createTestSecondaryEntity()), httpHeaders, uriInfo);

        return ResourceTestUtil.parseIdFromLocation(createResponse1) + "," + ResourceTestUtil.parseIdFromLocation(createResponse2);
    }
}

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
import org.slc.sli.api.resources.v1.entity.SchoolResource;
import org.slc.sli.api.resources.v1.entity.TeacherResource;
import org.slc.sli.api.test.WebContextTestExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import com.sun.jersey.core.util.MultivaluedMapImpl;

/**
 * JUnit tests for TeacherSchoolAssociationResource
 * @author chung
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class })
public class TeacherSchoolAssociationResourceTest {

    private final String teacherResourceName = "TeacherResource";
    private final String schoolResourceName = "SchoolResource";
    private final String teacherSchoolAssociationResourceName = "TeacherSchoolAssociationResource";

    @Autowired
    private SecurityContextInjector injector;
    @Autowired
    private TeacherResource teacherResource;
    @Autowired
    private SchoolResource schoolResource;
    @Autowired
    private TeacherSchoolAssociationResource teacherSchoolAssociationResource;

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

    @Test
    public void testGetTeachersForAssociation() {
        Response createResponse = teacherResource.create(new EntityBody(
                ResourceTestUtil.createTestEntity(teacherResourceName)), httpHeaders, uriInfo);
        String teacherId = ResourceTestUtil.parseIdFromLocation(createResponse);
        createResponse = schoolResource.create(new EntityBody(
                ResourceTestUtil.createTestEntity(schoolResourceName)), httpHeaders, uriInfo);
        String schoolId = ResourceTestUtil.parseIdFromLocation(createResponse);

        Map<String, Object> map = ResourceTestUtil.createTestAssociationEntity(
                teacherSchoolAssociationResourceName, teacherResourceName, teacherId, schoolResourceName, schoolId);
        createResponse = teacherSchoolAssociationResource.create(new EntityBody(map), httpHeaders, uriInfo);
        String id = ResourceTestUtil.parseIdFromLocation(createResponse);

        Response response = teacherSchoolAssociationResource.getTeachersForAssociation(id, 0, 100, httpHeaders, uriInfo);
        EntityBody body = ResourceTestUtil.assertions(response);
        assertEquals("Entity type should match", "teacher", body.get("entityType"));
        assertEquals("ID should match", teacherId, body.get("id"));
    }

    @Test
    public void testGetSchoolsForAssociation() {
        Response createResponse = teacherResource.create(new EntityBody(
                ResourceTestUtil.createTestEntity(teacherResourceName)), httpHeaders, uriInfo);
        String teacherId = ResourceTestUtil.parseIdFromLocation(createResponse);
        createResponse = schoolResource.create(new EntityBody(
                ResourceTestUtil.createTestEntity(schoolResourceName)), httpHeaders, uriInfo);
        String schoolId = ResourceTestUtil.parseIdFromLocation(createResponse);

        Map<String, Object> map = ResourceTestUtil.createTestAssociationEntity(
                teacherSchoolAssociationResourceName, teacherResourceName, teacherId, schoolResourceName, schoolId);
        createResponse = teacherSchoolAssociationResource.create(new EntityBody(map), httpHeaders, uriInfo);
        String id = ResourceTestUtil.parseIdFromLocation(createResponse);

        Response response = teacherSchoolAssociationResource.getSchoolsForAssociation(id, 0, 100, httpHeaders, uriInfo);
        EntityBody body = ResourceTestUtil.assertions(response);
        assertEquals("Entity type should match", "school", body.get("entityType"));
        assertEquals("ID should match", schoolId, body.get("id"));
    }
}

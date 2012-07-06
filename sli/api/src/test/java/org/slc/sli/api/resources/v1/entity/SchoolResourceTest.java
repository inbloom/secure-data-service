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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
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
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.resources.SecurityContextInjector;
import org.slc.sli.api.resources.util.ResourceTestUtil;
import org.slc.sli.api.resources.v1.HypermediaType;
import org.slc.sli.api.resources.v1.association.StudentSchoolAssociationResource;
import org.slc.sli.api.resources.v1.association.TeacherSchoolAssociationResource;
import org.slc.sli.api.test.WebContextTestExecutionListener;

/**
 * JUnit for school resources
 *
 * @author chung
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class })
public class SchoolResourceTest {

    private final String schoolResourceName = "SchoolResource";
    private final String teacherResourceName = "TeacherResource";
    private final String sessionResourceName = "SessionResource";
    private final String studentResourceName = "StudentResource";
    private final String sectionResourceName = "SectionResource";
    private final String teacherSchoolAssociationResourceName = "TeacherSchoolAssociationResource";
    private final String studentSchoolAssociationResourceName = "StudentSchoolAssociationResource";

    @Autowired
    private SecurityContextInjector injector;
    @Autowired
    private SchoolResource schoolResource;
    @Autowired
    private TeacherResource teacherResource;
    @Autowired
    private SessionResource sessionResource;
    @Autowired
    private StudentResource studentResource;
    @Autowired
    private SectionResource sectionResource;
    @Autowired
    private TeacherSchoolAssociationResource teacherSchoolAssociationResource;

    @Autowired
    private StudentSchoolAssociationResource studentSchoolAssociationResource;

    HttpHeaders httpHeaders;
    UriInfo uriInfo;

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
    public void testGetTeacherSchoolAssociations() {
        Response createResponse = schoolResource.create(new EntityBody(
                ResourceTestUtil.createTestEntity(schoolResourceName)), httpHeaders, uriInfo);
        String schoolId = ResourceTestUtil.parseIdFromLocation(createResponse);
        createResponse = teacherResource.create(new EntityBody(
                ResourceTestUtil.createTestEntity(teacherResourceName)), httpHeaders, uriInfo);
        String teacherId = ResourceTestUtil.parseIdFromLocation(createResponse);

        Map<String, Object> map = ResourceTestUtil.createTestAssociationEntity(
                teacherSchoolAssociationResourceName, schoolResourceName, schoolId, teacherResourceName, teacherId);
        teacherSchoolAssociationResource.create(new EntityBody(map), httpHeaders, uriInfo);

        Response response = schoolResource.getTeacherSchoolAssociations(schoolId, httpHeaders, uriInfo);
        ResourceTestUtil.assertions(response);
    }

    @Test
    public void testGetTeacherSchoolAssociationTeachers() {
        Response createResponse = schoolResource.create(new EntityBody(
                ResourceTestUtil.createTestEntity(schoolResourceName)), httpHeaders, uriInfo);
        String schoolId = ResourceTestUtil.parseIdFromLocation(createResponse);
        createResponse = teacherResource.create(new EntityBody(
                ResourceTestUtil.createTestEntity(teacherResourceName)), httpHeaders, uriInfo);
        String teacherId = ResourceTestUtil.parseIdFromLocation(createResponse);

        Map<String, Object> map = ResourceTestUtil.createTestAssociationEntity(
                teacherSchoolAssociationResourceName, schoolResourceName, schoolId, teacherResourceName, teacherId);
        teacherSchoolAssociationResource.create(new EntityBody(map), httpHeaders, uriInfo);

        Response response = schoolResource.getTeacherSchoolAssociationTeachers(schoolId, httpHeaders, uriInfo);
        ResourceTestUtil.assertions(response);
    }



    @Test
    public void testGetStudentSchoolAssociations() {
        Response createResponse = schoolResource.create(new EntityBody(
                ResourceTestUtil.createTestEntity(schoolResourceName)), httpHeaders, uriInfo);
        String schoolId = ResourceTestUtil.parseIdFromLocation(createResponse);
        createResponse = studentResource.create(new EntityBody(
                ResourceTestUtil.createTestEntity(studentResourceName)), httpHeaders, uriInfo);
        String studentId = ResourceTestUtil.parseIdFromLocation(createResponse);

        Map<String, Object> map = ResourceTestUtil.createTestAssociationEntity(
                studentSchoolAssociationResourceName, schoolResourceName, schoolId, studentResourceName, studentId);
        studentSchoolAssociationResource.create(new EntityBody(map), httpHeaders, uriInfo);

        Response response = schoolResource.getStudentSchoolAssociations(schoolId, httpHeaders, uriInfo);
        ResourceTestUtil.assertions(response);
    }

    @Test
    public void testGetStudentSchoolAssociationStudents() {
        Response createResponse = schoolResource.create(new EntityBody(
                ResourceTestUtil.createTestEntity(schoolResourceName)), httpHeaders, uriInfo);
        String schoolId = ResourceTestUtil.parseIdFromLocation(createResponse);
        createResponse = studentResource.create(new EntityBody(
                ResourceTestUtil.createTestEntity(studentResourceName)), httpHeaders, uriInfo);
        String studentId = ResourceTestUtil.parseIdFromLocation(createResponse);

        Map<String, Object> map = ResourceTestUtil.createTestAssociationEntity(
                studentSchoolAssociationResourceName, schoolResourceName, schoolId, studentResourceName, studentId);
        studentSchoolAssociationResource.create(new EntityBody(map), httpHeaders, uriInfo);

        Response response = schoolResource.getStudentSchoolAssociationStudents(schoolId, httpHeaders, uriInfo);
        ResourceTestUtil.assertions(response);
    }

    @Test
    public void testGetSectionsForSchool() {
        Response createResponse = schoolResource.create(new EntityBody(
                ResourceTestUtil.createTestEntity(schoolResourceName)), httpHeaders, uriInfo);
        String schoolId = ResourceTestUtil.parseIdFromLocation(createResponse);
        Map<String, Object> map = ResourceTestUtil.createTestEntity(sectionResourceName);
        map.put(ParameterConstants.SCHOOL_ID, schoolId);
        sectionResource.create(new EntityBody(map), httpHeaders, uriInfo);

        Response response = schoolResource.getSectionsForSchool(schoolId, httpHeaders, uriInfo);
        EntityBody body = ResourceTestUtil.assertions(response);
        assertEquals("School IDs should equal", schoolId, body.get(ParameterConstants.SCHOOL_ID));
    }
}

package org.slc.sli.api.resources.v1.entity;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import com.sun.jersey.api.uri.UriBuilderImpl;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import org.junit.Before;
import org.junit.Test;

import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.resources.SecurityContextInjector;
import org.slc.sli.api.resources.util.ResourceTestUtil;
import org.slc.sli.api.resources.v1.HypermediaType;
import org.slc.sli.api.resources.v1.ParameterConstants;
import org.slc.sli.api.resources.v1.association.SchoolSessionAssociationResource;
import org.slc.sli.api.resources.v1.association.StudentSchoolAssociationResource;
import org.slc.sli.api.resources.v1.association.TeacherSchoolAssociationResource;
import org.slc.sli.api.test.WebContextTestExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    private final String schoolSessionAssociationResourceName = "SchoolSessionAssociationResource";
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
    private SchoolSessionAssociationResource schoolSessionAssociationResource;
    @Autowired
    private StudentSchoolAssociationResource studentSchoolAssociationResource;

    HttpHeaders httpHeaders;
    UriInfo uriInfo;

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

    @Test
    public void testGetTeacherSchoolAssociations() {
        Response createResponse = schoolResource.create(new EntityBody(
                ResourceTestUtil.createTestEntity(schoolResourceName)), httpHeaders, uriInfo);
        String schoolId = parseIdFromLocation(createResponse);
        createResponse = teacherResource.create(new EntityBody(
                ResourceTestUtil.createTestEntity(teacherResourceName)), httpHeaders, uriInfo);
        String teacherId = parseIdFromLocation(createResponse);

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
        String schoolId = parseIdFromLocation(createResponse);
        createResponse = teacherResource.create(new EntityBody(
                ResourceTestUtil.createTestEntity(teacherResourceName)), httpHeaders, uriInfo);
        String teacherId = parseIdFromLocation(createResponse);

        Map<String, Object> map = ResourceTestUtil.createTestAssociationEntity(
                teacherSchoolAssociationResourceName, schoolResourceName, schoolId, teacherResourceName, teacherId);
        teacherSchoolAssociationResource.create(new EntityBody(map), httpHeaders, uriInfo);

        Response response = schoolResource.getTeacherSchoolAssociationTeachers(schoolId, httpHeaders, uriInfo);
        ResourceTestUtil.assertions(response);
    }

    @Test
    public void testGetSchoolSessionAssociations() {
        Response createResponse = schoolResource.create(new EntityBody(
                ResourceTestUtil.createTestEntity(schoolResourceName)), httpHeaders, uriInfo);
        String schoolId = parseIdFromLocation(createResponse);
        createResponse = sessionResource.create(new EntityBody(
                ResourceTestUtil.createTestEntity(sessionResourceName)), httpHeaders, uriInfo);
        String sessionId = parseIdFromLocation(createResponse);

        Map<String, Object> map = ResourceTestUtil.createTestAssociationEntity(
                schoolSessionAssociationResourceName, schoolResourceName, schoolId, sessionResourceName, sessionId);
        schoolSessionAssociationResource.create(new EntityBody(map), httpHeaders, uriInfo);

        Response response = schoolResource.getSchoolSessionAssociations(schoolId, httpHeaders, uriInfo);
        ResourceTestUtil.assertions(response);
    }

    @Test
    public void testGetSchoolSessionAssociationSessions() {
        Response createResponse = schoolResource.create(new EntityBody(
                ResourceTestUtil.createTestEntity(schoolResourceName)), httpHeaders, uriInfo);
        String schoolId = parseIdFromLocation(createResponse);
        createResponse = sessionResource.create(new EntityBody(
                ResourceTestUtil.createTestEntity(sessionResourceName)), httpHeaders, uriInfo);
        String sessionId = parseIdFromLocation(createResponse);

        Map<String, Object> map = ResourceTestUtil.createTestAssociationEntity(
                schoolSessionAssociationResourceName, schoolResourceName, schoolId, sessionResourceName, sessionId);
        schoolSessionAssociationResource.create(new EntityBody(map), httpHeaders, uriInfo);

        Response response = schoolResource.getSchoolSessionAssociationSessions(schoolId, httpHeaders, uriInfo);
        ResourceTestUtil.assertions(response);
    }

    @Test
    public void testGetStudentSchoolAssociations() {
        Response createResponse = schoolResource.create(new EntityBody(
                ResourceTestUtil.createTestEntity(schoolResourceName)), httpHeaders, uriInfo);
        String schoolId = parseIdFromLocation(createResponse);
        createResponse = studentResource.create(new EntityBody(
                ResourceTestUtil.createTestEntity(studentResourceName)), httpHeaders, uriInfo);
        String studentId = parseIdFromLocation(createResponse);

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
        String schoolId = parseIdFromLocation(createResponse);
        createResponse = studentResource.create(new EntityBody(
                ResourceTestUtil.createTestEntity(studentResourceName)), httpHeaders, uriInfo);
        String studentId = parseIdFromLocation(createResponse);

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
        String schoolId = parseIdFromLocation(createResponse);
        Map<String, Object> map = ResourceTestUtil.createTestEntity(sectionResourceName);
        map.put(ParameterConstants.SCHOOL_ID, schoolId);
        sectionResource.create(new EntityBody(map), httpHeaders, uriInfo);

        Response response = schoolResource.getSectionsForSchool(schoolId, httpHeaders, uriInfo);
        EntityBody body = ResourceTestUtil.assertions(response);
        assertEquals("School IDs should equal", schoolId, body.get(ParameterConstants.SCHOOL_ID));
    }

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

        when(mock.getQueryParameters(true)).thenAnswer(new Answer<MultivaluedMapImpl>() {
            @Override
            public MultivaluedMapImpl answer(InvocationOnMock invocationOnMock) throws Throwable {
                return new MultivaluedMapImpl();
            }
        });

        when(mock.getRequestUri()).thenReturn(new UriBuilderImpl().replaceQuery(queryString).build(new Object[] {}));
        return mock;
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

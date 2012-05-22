package org.slc.sli.api.resources.v1.entity;

import com.sun.jersey.core.util.MultivaluedMapImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.resources.SecurityContextInjector;
import org.slc.sli.api.resources.util.ResourceTestUtil;
import org.slc.sli.api.resources.v1.HypermediaType;
import org.slc.sli.api.resources.v1.association.CourseOfferingResource;
import org.slc.sli.api.resources.v1.association.StudentParentAssociationResource;
import org.slc.sli.api.resources.v1.association.StudentTranscriptAssociationResource;
import org.slc.sli.api.test.WebContextTestExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * JUnit for course resources
 *
 * @author chung
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class })
public class CourseResourceTest {

    private final String courseResourceName = "CourseResource";
    private final String studentResourceName = "StudentResource";
    private final String sessionResourceName = "SessionResource";
    private final String courseOfferingResourceName = "CourseOfferingResource";
    private final String studentTranscriptAssociationResourceName = "StudentTranscriptAssociationResource";
    private final String studentParentAssociationResourceName = "StudentParentAssociationResource";

    @Autowired
    private SecurityContextInjector injector;
    @Autowired
    private CourseResource courseResource;
    @Autowired
    private StudentResource studentResource;
    @Autowired
    private SessionResource sessionResource;
    @Autowired
    private StudentTranscriptAssociationResource studentTranscriptAssociationResource;
    @Autowired
    private CourseOfferingResource courseOfferingResource;
    @Autowired
    private StudentParentAssociationResource studentParentAssociationResource;

    HttpHeaders httpHeaders;
    UriInfo uriInfo;

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
    public void testGetCourseOffering() {
        Response createResponse = courseResource.create(new EntityBody(
                ResourceTestUtil.createTestEntity(courseResourceName)), httpHeaders, uriInfo);
        String courseId = ResourceTestUtil.parseIdFromLocation(createResponse);
        createResponse = sessionResource.create(new EntityBody(
                ResourceTestUtil.createTestEntity(sessionResourceName)), httpHeaders, uriInfo);
        String sessionId = ResourceTestUtil.parseIdFromLocation(createResponse);

        Map<String, Object> map = ResourceTestUtil.createTestAssociationEntity(
                courseOfferingResourceName, courseResourceName, courseId, sessionResourceName, sessionId);
        courseOfferingResource.create(new EntityBody(map), httpHeaders, uriInfo);

        Response response = courseResource.getCourseOfferings(courseId, httpHeaders, uriInfo);
        ResourceTestUtil.assertions(response);
    }

    @Test
    public void testGetCourseOfferingsCourses() {
        Response createResponse = courseResource.create(new EntityBody(
                ResourceTestUtil.createTestEntity(courseResourceName)), httpHeaders, uriInfo);
        String courseId = ResourceTestUtil.parseIdFromLocation(createResponse);
        createResponse = sessionResource.create(new EntityBody(
                ResourceTestUtil.createTestEntity(sessionResourceName)), httpHeaders, uriInfo);
        String sessionId = ResourceTestUtil.parseIdFromLocation(createResponse);

        Map<String, Object> map = ResourceTestUtil.createTestAssociationEntity(
                courseOfferingResourceName, courseResourceName, courseId, sessionResourceName, sessionId);
        courseOfferingResource.create(new EntityBody(map), httpHeaders, uriInfo);

        Response response = courseResource.getCourseOfferingCourses(courseId, httpHeaders, uriInfo);
        ResourceTestUtil.assertions(response);
    }

    @Test
    public void testGetStudentTranscriptAssociations() {
        Response createResponse = courseResource.create(new EntityBody(
                ResourceTestUtil.createTestEntity(courseResourceName)), httpHeaders, uriInfo);
        String courseId = ResourceTestUtil.parseIdFromLocation(createResponse);
        createResponse = studentResource.create(new EntityBody(
                ResourceTestUtil.createTestEntity(studentResourceName)), httpHeaders, uriInfo);
        String studentId = ResourceTestUtil.parseIdFromLocation(createResponse);

        Map<String, Object> map = ResourceTestUtil.createTestAssociationEntity(
                studentTranscriptAssociationResourceName, courseResourceName, courseId, studentResourceName, studentId);
        studentTranscriptAssociationResource.create(new EntityBody(map), httpHeaders, uriInfo);

        Response response = courseResource.getStudentTranscriptAssociations(courseId, httpHeaders, uriInfo);
        ResourceTestUtil.assertions(response);
    }

    @Test
    public void testGetStudentTranscriptAssociationsStudents() {
        Response createResponse = courseResource.create(new EntityBody(
                ResourceTestUtil.createTestEntity(courseResourceName)), httpHeaders, uriInfo);
        String courseId = ResourceTestUtil.parseIdFromLocation(createResponse);
        createResponse = studentResource.create(new EntityBody(
                ResourceTestUtil.createTestEntity(studentResourceName)), httpHeaders, uriInfo);
        String studentId = ResourceTestUtil.parseIdFromLocation(createResponse);

        Map<String, Object> map = ResourceTestUtil.createTestAssociationEntity(
                studentTranscriptAssociationResourceName, courseResourceName, courseId, studentResourceName, studentId);
        studentTranscriptAssociationResource.create(new EntityBody(map), httpHeaders, uriInfo);

        Response response = courseResource.getStudentTranscriptAssociationStudents(courseId, httpHeaders, uriInfo);
        ResourceTestUtil.assertions(response);
    }

    @Test
    public void testGetStudentParentAssociations() {
        Response createResponse = courseResource.create(new EntityBody(
                ResourceTestUtil.createTestEntity(courseResourceName)), httpHeaders, uriInfo);
        String courseId = ResourceTestUtil.parseIdFromLocation(createResponse);
        createResponse = studentResource.create(new EntityBody(
                ResourceTestUtil.createTestEntity(studentResourceName)), httpHeaders, uriInfo);
        String studentId = ResourceTestUtil.parseIdFromLocation(createResponse);

        Map<String, Object> map = ResourceTestUtil.createTestAssociationEntity(
                studentParentAssociationResourceName, courseResourceName, courseId, studentResourceName, studentId);
        studentParentAssociationResource.create(new EntityBody(map), httpHeaders, uriInfo);

        Response response = courseResource.getStudentParentAssociations(courseId, httpHeaders, uriInfo);
        ResourceTestUtil.assertions(response);
    }

    @Test
    public void testGetStudentParentAssociationsStudents() {
        Response createResponse = courseResource.create(new EntityBody(
                ResourceTestUtil.createTestEntity(courseResourceName)), httpHeaders, uriInfo);
        String courseId = ResourceTestUtil.parseIdFromLocation(createResponse);
        createResponse = studentResource.create(new EntityBody(
                ResourceTestUtil.createTestEntity(studentResourceName)), httpHeaders, uriInfo);
        String studentId = ResourceTestUtil.parseIdFromLocation(createResponse);

        Map<String, Object> map = ResourceTestUtil.createTestAssociationEntity(
                studentParentAssociationResourceName, courseResourceName, courseId, studentResourceName, studentId);
        studentParentAssociationResource.create(new EntityBody(map), httpHeaders, uriInfo);

        Response response = courseResource.getStudentParentAssociationStudents(courseId, httpHeaders, uriInfo);
        ResourceTestUtil.assertions(response);
    }
}

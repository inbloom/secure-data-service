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
import org.slc.sli.api.resources.v1.entity.CourseResource;
import org.slc.sli.api.resources.v1.entity.StudentResource;
import org.slc.sli.api.test.WebContextTestExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import com.sun.jersey.core.util.MultivaluedMapImpl;

/**
 * JUnit tests for StudentTranscriptAssociationResource
 * @author chung
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class })
public class StudentTranscriptAssociationResourceTest {

    private final String studentResourceName = "StudentResource";
    private final String courseResourceName = "CourseResource";
    private final String studentTranscriptAssociationResourceName = "StudentTranscriptAssociationResource";

    @Autowired
    private SecurityContextInjector injector;
    @Autowired
    private StudentResource studentResource;
    @Autowired
    private CourseResource courseResource;
    @Autowired
    private StudentTranscriptAssociationResource studentTranscriptAssociationResource;

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
    public void testGetCourses() {
        Response createResponse = studentResource.create(new EntityBody(
                ResourceTestUtil.createTestEntity(studentResourceName)), httpHeaders, uriInfo);
        String studentId = ResourceTestUtil.parseIdFromLocation(createResponse);
        createResponse = courseResource.create(new EntityBody(
                ResourceTestUtil.createTestEntity(courseResourceName)), httpHeaders, uriInfo);
        String courseId = ResourceTestUtil.parseIdFromLocation(createResponse);

        Map<String, Object> map = ResourceTestUtil.createTestAssociationEntity(
                studentTranscriptAssociationResourceName, studentResourceName, studentId, courseResourceName, courseId);
        createResponse = studentTranscriptAssociationResource.create(new EntityBody(map), httpHeaders, uriInfo);
        String id = ResourceTestUtil.parseIdFromLocation(createResponse);

        Response response = studentTranscriptAssociationResource.getCourses(id, 0, 100, httpHeaders, uriInfo);
        EntityBody body = ResourceTestUtil.assertions(response);
        assertEquals("Entity type should match", "course", body.get("entityType"));
        assertEquals("ID should match", courseId, body.get("id"));
    }

    @Test
    public void testGetStudents() {
        Response createResponse = studentResource.create(new EntityBody(
                ResourceTestUtil.createTestEntity(studentResourceName)), httpHeaders, uriInfo);
        String studentId = ResourceTestUtil.parseIdFromLocation(createResponse);
        createResponse = courseResource.create(new EntityBody(
                ResourceTestUtil.createTestEntity(courseResourceName)), httpHeaders, uriInfo);
        String courseId = ResourceTestUtil.parseIdFromLocation(createResponse);

        Map<String, Object> map = ResourceTestUtil.createTestAssociationEntity(
                studentTranscriptAssociationResourceName, studentResourceName, studentId, courseResourceName, courseId);
        createResponse = studentTranscriptAssociationResource.create(new EntityBody(map), httpHeaders, uriInfo);
        String id = ResourceTestUtil.parseIdFromLocation(createResponse);

        Response response = studentTranscriptAssociationResource.getStudents(id, 0, 100, httpHeaders, uriInfo);
        EntityBody body = ResourceTestUtil.assertions(response);
        assertEquals("Entity type should match", "student", body.get("entityType"));
        assertEquals("ID should match", studentId, body.get("id"));
    }
}

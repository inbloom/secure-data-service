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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.resources.SecurityContextInjector;
import org.slc.sli.api.resources.util.ResourceTestUtil;
import org.slc.sli.api.resources.v1.HypermediaType;
import org.slc.sli.api.resources.v1.association.CourseOfferingResource;
import org.slc.sli.api.test.WebContextTestExecutionListener;
import org.slc.sli.common.constants.v1.ParameterConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import com.sun.jersey.core.util.MultivaluedMapImpl;

/**
 * JUnit for session resources
 *
 * @author chung
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class })
public class SessionResourceTest {

    private final String sessionResourceName = "SessionResource";
    private final String schoolResourceName = "SchoolResource";
    private final String courseResourceName = "CourseResource";
    private final String courseOfferingResourceName = "courseOfferingResource";

    @Autowired
    private SecurityContextInjector injector;
    @Autowired
    private SessionResource sessionResource;
    @Autowired
    private SchoolResource schoolResource;
    @Autowired
    private CourseResource courseResource;
    @Autowired
    private CourseOfferingResource courseOfferingResource;

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
    public void testGetSchoolSessions() {
        Response createResponse = schoolResource.create(new EntityBody(
                ResourceTestUtil.createTestEntity(schoolResourceName)), httpHeaders, uriInfo);
        String schoolId = ResourceTestUtil.parseIdFromLocation(createResponse);

        Map<String, Object> map = ResourceTestUtil.createTestEntity(sessionResourceName);
        map.put(ParameterConstants.SCHOOL_ID, schoolId);
        sessionResource.create(new EntityBody(map), httpHeaders, uriInfo);
        
        Response response = sessionResource.getSchoolSessions(schoolId, httpHeaders, uriInfo);
        EntityBody body = ResourceTestUtil.assertions(response);
        assertEquals("School IDs should equal", schoolId, body.get(ParameterConstants.SCHOOL_ID));
    }

    @Test
    public void testGetSchoolSessionSchools() {
        Response createResponse = schoolResource.create(new EntityBody(
                ResourceTestUtil.createTestEntity(schoolResourceName)), httpHeaders, uriInfo);
        String schoolId = ResourceTestUtil.parseIdFromLocation(createResponse);

        Map<String, Object> map = ResourceTestUtil.createTestEntity(sessionResourceName);
        map.put(ParameterConstants.SCHOOL_ID, schoolId);
        sessionResource.create(new EntityBody(map), httpHeaders, uriInfo);        
        
        Response response = sessionResource.getSchoolSessionSchools(schoolId, httpHeaders, uriInfo);
        ResourceTestUtil.assertions(response);
    }

    @Test
    public void testGetCourseOfferings() {
        Response createResponse = sessionResource.create(new EntityBody(
                ResourceTestUtil.createTestEntity(sessionResourceName)), httpHeaders, uriInfo);
        String sessionId = ResourceTestUtil.parseIdFromLocation(createResponse);
        createResponse = courseResource.create(new EntityBody(
                ResourceTestUtil.createTestEntity(courseResourceName)), httpHeaders, uriInfo);
        String courseId = ResourceTestUtil.parseIdFromLocation(createResponse);

        Map<String, Object> map = ResourceTestUtil.createTestAssociationEntity(
                courseOfferingResourceName, sessionResourceName, sessionId, courseResourceName, courseId);
        courseOfferingResource.create(new EntityBody(map), httpHeaders, uriInfo);

        Response response = sessionResource.getCourseOfferings(sessionId, httpHeaders, uriInfo);
        ResourceTestUtil.assertions(response);
    }

    @Test
    public void testGetCourseOfferingCourses() {
        Response createResponse = sessionResource.create(new EntityBody(
                ResourceTestUtil.createTestEntity(sessionResourceName)), httpHeaders, uriInfo);
        String sessionId = ResourceTestUtil.parseIdFromLocation(createResponse);
        createResponse = courseResource.create(new EntityBody(
                ResourceTestUtil.createTestEntity(courseResourceName)), httpHeaders, uriInfo);
        String courseId = ResourceTestUtil.parseIdFromLocation(createResponse);

        Map<String, Object> map = ResourceTestUtil.createTestAssociationEntity(
                courseOfferingResourceName, sessionResourceName, sessionId, courseResourceName, courseId);
        courseOfferingResource.create(new EntityBody(map), httpHeaders, uriInfo);

        Response response = sessionResource.getCourseOfferingCourses(sessionId, httpHeaders, uriInfo);
        ResourceTestUtil.assertions(response);
    }
}

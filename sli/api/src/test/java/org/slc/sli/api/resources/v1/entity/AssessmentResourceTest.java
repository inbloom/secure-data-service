package org.slc.sli.api.resources.v1.entity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
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
import org.slc.sli.api.resources.v1.association.SectionAssessmentAssociationResource;
import org.slc.sli.api.resources.v1.association.StudentAssessmentAssociationResource;
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
 * JUnit for assessment resources
 * 
 * @author nbrown
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class })
public class AssessmentResourceTest {

    private final String assessmentResourceName = "AssessmentResource";
    private final String studentResourceName = "StudentResource";
    private final String sectionResourceName = "SectionResource";
    private final String studentAssessmentAssociationResourceName = "StudentAssessmentAssociationResource";
    private final String sectionAssessmentAssociationResourceName = "SectionAssessmentAssociationResource";

    @Autowired
    private SecurityContextInjector injector;
    @Autowired
    private AssessmentResource assessmentResource;
    @Autowired
    private StudentResource studentResource;
    @Autowired
    private SectionResource sectionResource;
    @Autowired
    private StudentAssessmentAssociationResource studentAssessmentAssociationResource;
    @Autowired
    private SectionAssessmentAssociationResource sectionAssessmentAssociationResource;

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
    public void testGetStudentAssessmentAssociations() {
        Response createResponse = assessmentResource.create(new EntityBody(
                ResourceTestUtil.createTestEntity(assessmentResourceName)), httpHeaders, uriInfo);
        String assessmentId = parseIdFromLocation(createResponse);
        createResponse = studentResource.create(new EntityBody(
                ResourceTestUtil.createTestEntity(studentResourceName)), httpHeaders, uriInfo);
        String studentId = parseIdFromLocation(createResponse);

        Map<String, Object> map = ResourceTestUtil.createTestAssociationEntity(
                studentAssessmentAssociationResourceName, assessmentResourceName, assessmentId, studentResourceName, studentId);
        studentAssessmentAssociationResource.create(new EntityBody(map), httpHeaders, uriInfo);

        Response response = assessmentResource.getStudentAssessmentAssociations(assessmentId, httpHeaders, uriInfo);
        ResourceTestUtil.assertions(response);
    }

    @Test
    public void testGetStudentAssessmentAssociationsStudents() {
        Response createResponse = assessmentResource.create(new EntityBody(
                ResourceTestUtil.createTestEntity(assessmentResourceName)), httpHeaders, uriInfo);
        String assessmentId = parseIdFromLocation(createResponse);
        createResponse = studentResource.create(new EntityBody(
                ResourceTestUtil.createTestEntity(studentResourceName)), httpHeaders, uriInfo);
        String studentId = parseIdFromLocation(createResponse);

        Map<String, Object> map = ResourceTestUtil.createTestAssociationEntity(
                studentAssessmentAssociationResourceName, assessmentResourceName, assessmentId, studentResourceName, studentId);
        studentAssessmentAssociationResource.create(new EntityBody(map), httpHeaders, uriInfo);

        Response response = assessmentResource.getStudentAssessmentAssociationsStudents(assessmentId, httpHeaders, uriInfo);
        ResourceTestUtil.assertions(response);
    }

    @Test
    public void testGetSectionAssessmentAssociations() {
        Response createResponse = assessmentResource.create(new EntityBody(
                ResourceTestUtil.createTestEntity(assessmentResourceName)), httpHeaders, uriInfo);
        String assessmentId = parseIdFromLocation(createResponse);
        createResponse = sectionResource.create(new EntityBody(
                ResourceTestUtil.createTestEntity(sectionResourceName)), httpHeaders, uriInfo);
        String sectionId = parseIdFromLocation(createResponse);

        Map<String, Object> map = ResourceTestUtil.createTestAssociationEntity(
                sectionAssessmentAssociationResourceName, assessmentResourceName, assessmentId, sectionResourceName, sectionId);
        sectionAssessmentAssociationResource.create(new EntityBody(map), httpHeaders, uriInfo);

        Response response = assessmentResource.getSectionAssessmentAssociations(assessmentId, httpHeaders, uriInfo);
        ResourceTestUtil.assertions(response);
    }

    @Test
    public void testGetSectionAssessmentAssociationsStudents() {
        Response createResponse = assessmentResource.create(new EntityBody(
                ResourceTestUtil.createTestEntity(assessmentResourceName)), httpHeaders, uriInfo);
        String assessmentId = parseIdFromLocation(createResponse);
        createResponse = sectionResource.create(new EntityBody(
                ResourceTestUtil.createTestEntity(sectionResourceName)), httpHeaders, uriInfo);
        String sectionId = parseIdFromLocation(createResponse);

        Map<String, Object> map = ResourceTestUtil.createTestAssociationEntity(
                sectionAssessmentAssociationResourceName, assessmentResourceName, assessmentId, sectionResourceName, sectionId);
        sectionAssessmentAssociationResource.create(new EntityBody(map), httpHeaders, uriInfo);

        Response response = assessmentResource.getSectionAssessmentAssociationsSections(assessmentId, httpHeaders, uriInfo);
        ResourceTestUtil.assertions(response);
    }
    
    @Test
    public void testGetLearningStandards() {
        Response response = assessmentResource.getLearningStandards("any");
        assertEquals(Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }
    
    @Test
    public void testGetLearningObjectives() {
        Response response = assessmentResource.getLearningObjectives("any");
        assertEquals(Status.NOT_FOUND.getStatusCode(), response.getStatus());
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

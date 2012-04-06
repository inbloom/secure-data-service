package org.slc.sli.api.resources.v1.entity;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
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

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.slc.sli.api.config.EntityDefinitionStore;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.resources.util.ResourceConstants;
import org.slc.sli.api.resources.util.ResourceTestUtil;
import org.slc.sli.api.resources.v1.HypermediaType;
import org.slc.sli.api.resources.v1.association.StudentAssessmentAssociationResource;

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
public class AssessmentResourceTest {

    private final String assessmentResourceName = "AssessmentResource";
    private final String studentResourceName = "StudentResource";
    private final String studentAssessmentAssociationResourceName = "StudentAssessmentAssociationResource";

    private EntityDefinitionStore defs = mock(EntityDefinitionStore.class);
    private AssessmentResource assessmentResource = new AssessmentResource(defs);
    private StudentResource studentResource = new StudentResource(defs);
    private StudentAssessmentAssociationResource studentAssessmentAssociationResource = new StudentAssessmentAssociationResource(defs);

    HttpHeaders httpHeaders;
    UriInfo uriInfo;

    @Before
    public void setup() throws Exception {
        uriInfo = buildMockUriInfo(null);

        List<String> acceptRequestHeaders = new ArrayList<String>();
        acceptRequestHeaders.add(HypermediaType.VENDOR_SLC_JSON);

        httpHeaders = mock(HttpHeaders.class);
        when(httpHeaders.getRequestHeader("accept")).thenReturn(acceptRequestHeaders);
        when(httpHeaders.getRequestHeaders()).thenReturn(new MultivaluedMapImpl());
    }

//    @Test
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
        assertEquals("Status code should be OK", Status.OK.getStatusCode(), response.getStatus());

        Object responseEntityObj = response.getEntity();

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
        assertNotNull("Should include links", body.get(ResourceConstants.LINKS));
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

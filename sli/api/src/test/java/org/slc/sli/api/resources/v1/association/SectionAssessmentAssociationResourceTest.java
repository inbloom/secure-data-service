package org.slc.sli.api.resources.v1.association;

import com.sun.jersey.core.util.MultivaluedMapImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.resources.SecurityContextInjector;
import org.slc.sli.api.resources.util.ResourceTestUtil;
import org.slc.sli.api.resources.v1.HypermediaType;
import org.slc.sli.api.resources.v1.entity.AssessmentResource;
import org.slc.sli.api.resources.v1.entity.SectionResource;
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

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * JUnit tests for SectionAssessmentAssociationResource
 * @author chung
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class })
public class SectionAssessmentAssociationResourceTest {

    private final String sectionResourceName = "SectionResource";
    private final String assessmentResourceName = "AssessmentResource";
    private final String sectionAssessmentAssociationResourceName = "SectionAssessmentAssociationResource";

    @Autowired
    private SecurityContextInjector injector;
    @Autowired
    private SectionResource sectionResource;
    @Autowired
    private AssessmentResource assessmentResource;
    @Autowired
    private SectionAssessmentAssociationResource sessionAssessmentAssociationResource;

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
    public void testGetSectionsForAssociation() {
        Response createResponse = sectionResource.create(new EntityBody(
                ResourceTestUtil.createTestEntity(sectionResourceName)), httpHeaders, uriInfo);
        String sectionId = ResourceTestUtil.parseIdFromLocation(createResponse);
        createResponse = assessmentResource.create(new EntityBody(
                ResourceTestUtil.createTestEntity(assessmentResourceName)), httpHeaders, uriInfo);
        String assessmentId = ResourceTestUtil.parseIdFromLocation(createResponse);

        Map<String, Object> map = ResourceTestUtil.createTestAssociationEntity(
                sectionAssessmentAssociationResourceName, sectionResourceName, sectionId, assessmentResourceName, assessmentId);
        createResponse = sessionAssessmentAssociationResource.create(new EntityBody(map), httpHeaders, uriInfo);
        String id = ResourceTestUtil.parseIdFromLocation(createResponse);

        Response response = sessionAssessmentAssociationResource.getSectionsForAssociation(id, 0, 100, httpHeaders, uriInfo);
        EntityBody body = ResourceTestUtil.assertions(response);
        assertEquals("Entity type should match", "section", body.get("entityType"));
        assertEquals("ID should match", sectionId, body.get("id"));
    }

    @Test
    public void testGetAssessmentForAssociation() {
        Response createResponse = sectionResource.create(new EntityBody(
                ResourceTestUtil.createTestEntity(sectionResourceName)), httpHeaders, uriInfo);
        String sectionId = ResourceTestUtil.parseIdFromLocation(createResponse);
        createResponse = assessmentResource.create(new EntityBody(
                ResourceTestUtil.createTestEntity(assessmentResourceName)), httpHeaders, uriInfo);
        String assessmentId = ResourceTestUtil.parseIdFromLocation(createResponse);

        Map<String, Object> map = ResourceTestUtil.createTestAssociationEntity(
                sectionAssessmentAssociationResourceName, sectionResourceName, sectionId, assessmentResourceName, assessmentId);
        createResponse = sessionAssessmentAssociationResource.create(new EntityBody(map), httpHeaders, uriInfo);
        String id = ResourceTestUtil.parseIdFromLocation(createResponse);

        Response response = sessionAssessmentAssociationResource.getAssessmentsForAssociation(id, 0, 100, httpHeaders, uriInfo);
        EntityBody body = ResourceTestUtil.assertions(response);
        assertEquals("Entity type should match", "assessment", body.get("entityType"));
        assertEquals("ID should match", assessmentId, body.get("id"));
    }
}

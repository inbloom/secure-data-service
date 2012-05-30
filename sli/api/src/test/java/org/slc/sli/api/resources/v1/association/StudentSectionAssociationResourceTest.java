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
import org.slc.sli.api.client.constants.v1.ParameterConstants;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.resources.SecurityContextInjector;
import org.slc.sli.api.resources.util.ResourceTestUtil;
import org.slc.sli.api.resources.v1.HypermediaType;
import org.slc.sli.api.resources.v1.entity.SectionResource;
import org.slc.sli.api.resources.v1.entity.StudentCompetencyResource;
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
 * JUnit tests for StudentSectionAssociationResource
 * @author chung
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class })
public class StudentSectionAssociationResourceTest {

    private final String studentResourceName = "StudentResource";
    private final String sectionResourceName = "SectionResource";
    private final String studentSectionAssociationResourceName = "StudentSectionAssociationResource";
    private final String studentCompetencyResourceName = "StudentCompetencyResource";

    @Autowired
    private SecurityContextInjector injector;
    @Autowired
    private StudentResource studentResource;
    @Autowired
    private SectionResource sectionResource;
    @Autowired
    private StudentSectionAssociationResource studentSectionAssociationResource;
    @Autowired
    private StudentCompetencyResource studentCompetencyResource;

    private UriInfo uriInfo;
    private HttpHeaders httpHeaders;

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
    public void testGetSections() {
        Response createResponse = studentResource.create(new EntityBody(
                ResourceTestUtil.createTestEntity(studentResourceName)), httpHeaders, uriInfo);
        String studentId = ResourceTestUtil.parseIdFromLocation(createResponse);
        createResponse = sectionResource.create(new EntityBody(
                ResourceTestUtil.createTestEntity(sectionResourceName)), httpHeaders, uriInfo);
        String sectionId = ResourceTestUtil.parseIdFromLocation(createResponse);

        Map<String, Object> map = ResourceTestUtil.createTestAssociationEntity(
                studentSectionAssociationResourceName, studentResourceName, studentId, sectionResourceName, sectionId);
        createResponse = studentSectionAssociationResource.create(new EntityBody(map), httpHeaders, uriInfo);
        String id = ResourceTestUtil.parseIdFromLocation(createResponse);

        Response response = studentSectionAssociationResource.getSections(id, 0, 100, httpHeaders, uriInfo);
        EntityBody body = ResourceTestUtil.assertions(response);
        assertEquals("Entity type should match", "section", body.get("entityType"));
        assertEquals("ID should match", sectionId, body.get("id"));
    }

    @Test
    public void testGetStudents() {
        Response createResponse = studentResource.create(new EntityBody(
                ResourceTestUtil.createTestEntity(studentResourceName)), httpHeaders, uriInfo);
        String studentId = ResourceTestUtil.parseIdFromLocation(createResponse);
        createResponse = sectionResource.create(new EntityBody(
                ResourceTestUtil.createTestEntity(sectionResourceName)), httpHeaders, uriInfo);
        String sectionId = ResourceTestUtil.parseIdFromLocation(createResponse);

        Map<String, Object> map = ResourceTestUtil.createTestAssociationEntity(
                studentSectionAssociationResourceName, studentResourceName, studentId, sectionResourceName, sectionId);
        createResponse = studentSectionAssociationResource.create(new EntityBody(map), httpHeaders, uriInfo);
        String id = ResourceTestUtil.parseIdFromLocation(createResponse);

        Response response = studentSectionAssociationResource.getStudents(id, 0, 100, httpHeaders, uriInfo);
        EntityBody body = ResourceTestUtil.assertions(response);
        assertEquals("Entity type should match", "student", body.get("entityType"));
        assertEquals("ID should match", studentId, body.get("id"));
    }

    @Test
    public void testGetStudentCompetencies() {
        Response createResponse = studentSectionAssociationResource.create(new EntityBody(
                ResourceTestUtil.createTestEntity(studentSectionAssociationResourceName)), httpHeaders, uriInfo);
        String studentSecId = ResourceTestUtil.parseIdFromLocation(createResponse);
        Map<String, Object> map = ResourceTestUtil.createTestEntity(studentCompetencyResourceName);
        map.put(ParameterConstants.STUDENT_SECTION_ASSOCIATION_ID, studentSecId);

        studentCompetencyResource.create(new EntityBody(map), httpHeaders, uriInfo);

        Response response = studentSectionAssociationResource.getStudentCompetencies(studentSecId, httpHeaders, uriInfo);
        EntityBody body = ResourceTestUtil.assertions(response);
        assertEquals(studentSecId, body.get(ParameterConstants.STUDENT_SECTION_ASSOCIATION_ID));
    }

}

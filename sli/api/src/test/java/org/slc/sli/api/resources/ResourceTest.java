package org.slc.sli.api.resources;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import com.sun.jersey.api.uri.UriBuilderImpl;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.slc.sli.api.representation.CollectionResponse;
import org.slc.sli.api.representation.CollectionResponse.EntityReference;
import org.slc.sli.api.representation.EmbeddedLink;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.test.WebContextTestExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

/**
 * Unit tests for the generic Resource class.
 * 
 * @author Ryan Farris <rfarris@wgen.net>
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class })
public class ResourceTest {

    @Autowired
    private SecurityContextInjector injector;
    // post some data
    // Map of <type, id> pair to entity location.
    /**
     * Track an object type/id.
     */
    final class TypeIdPair {
        protected TypeIdPair(Object type, String id) {
            this.type = (String) type;
            this.id = id;
        }
        
        String type;
        String id;
    }
    
    private static final String STUDENT_SCHOOL_ASSOCIATION_URI = "student-school-associations";
    private static final String STUDENT_SECTION_ASSOCIATION_URI = "student-section-associations";
    private static final String STUDENT_ASSESSMENT_ASSOCIATION_URI = "student-assessment-associations";
    private static final String TEACHER_SCHOOL_ASSOCIATION_URI = "teacher-school-associations";
    private static final String EDUCATIONORGANIZATION_SCHOOL_ASSOCIATION_URI = "educationOrganization-school-associations";
    private static final String STAFF_EDUCATIONORGANIZATION_ASSOCIATION_URI = "staff-educationOrganization-associations";
    private static final String SECTION_ASSESSMENT_ASSOCIATION_URI = "section-assessment-associations";
    private static final String SECTION_SCHOOL_ASSOCIATION_URI = "section-school-associations";
    @Autowired
    Resource api;
    
    public Map<String, Object> createTestEntity() {
        Map<String, Object> entity = new HashMap<String, Object>();
        entity.put("field1", 1);
        entity.put("field2", 2);
        return entity;
    }
    
    public Map<String, Object> createTestAssoication(String studentId, String schoolId) {
        Map<String, Object> entity = new HashMap<String, Object>();
        entity.put("studentId", studentId);
        entity.put("schoolId", schoolId);
        entity.put("entryGradeLevel", "First grade");
        return entity;
    }
    
    public Map<String, Object> createTestStudentSectionAssociation(String studentId, String sectionId) {
        Map<String, Object> assoc = new HashMap<String, Object>();
        assoc.put("studentId", studentId);
        assoc.put("sectionId", sectionId);
        assoc.put("repeatIdentifier", "NOT_REPEATED");
        return assoc;
    }
    
    public Map<String, Object> createTestStudentAssessmentAssociation(String studentId, String assessmentId) {
        Map<String, Object> entity = new HashMap<String, Object>();
        entity.put("studentId", studentId);
        entity.put("assessmentId", assessmentId);
        entity.put("administrationLanguage", "ENGLISH");
        entity.put("administrationDate", "2011-01-01");
        return entity;
    }
    
    public Map<String, Object> createTestTeacherSchoolAssociation(String teacherId, String schoolId) {
        Map<String, Object> entity = new HashMap<String, Object>();
        entity.put("teacherId", teacherId);
        entity.put("schoolId", schoolId);
        return entity;
    }

    public Map<String, Object> createTestEducationOrganizationSchoolAssociation(String educationOrganizationId, String schoolId) {
        Map<String, Object> entity = new HashMap<String, Object>();
        entity.put("educationOrganizationId", educationOrganizationId);
        entity.put("schoolId", schoolId);
        return entity;
    }

    public Map<String, Object> createTestEducationOrganizationStaffAssociation(String staffId, String educationOrganizationId) {
        Map<String, Object> entity = new HashMap<String, Object>();
        entity.put("staffId", staffId);
        entity.put("educationOrganizationId", educationOrganizationId);
        return entity;
    }

    public Map<String, Object> createTestSectionAssessmentAssociation(String sectionId, String assessmentId) {
        Map<String, Object> entity = new HashMap<String, Object>();
        entity.put("sectionId", sectionId);
        entity.put("assessmentId", assessmentId);
        return entity;
    }
    
    public Map<String, Object> createTestSectionSchoolAssociation(String sectionId, String schoolId) {
        Map<String, Object> entity = new HashMap<String, Object>();
        entity.put("sectionId", sectionId);
        entity.put("schoolId", schoolId);
        return entity;
    }

    @Before
    public void setUp() {
        // inject administrator security context for unit testing
        injector.setAdminContext();
    }
    
    @After
    public void tearDown() {
        SecurityContextHolder.clearContext();
    }
    
    @Test
    public void testResourceMethods() throws Exception {
        UriInfo info = buildMockUriInfo(null);
        
        HashMap<TypeIdPair, String> ids = new HashMap<TypeIdPair, String>();
        
        Response createResponse = api.createEntity("students", new EntityBody(createTestEntity()), info);
        assertNotNull(createResponse);
        assertEquals(Status.CREATED.getStatusCode(), createResponse.getStatus());
        String studentId1 = parseIdFromLocation(createResponse);
        ids.put(new TypeIdPair("students", studentId1), (String) createResponse.getMetadata().get("Location").get(0));
        
        Response createResponse2 = api.createEntity("students", new EntityBody(createTestEntity()), info);
        assertNotNull(createResponse2);
        assertEquals(Status.CREATED.getStatusCode(), createResponse2.getStatus());
        String studentId2 = parseIdFromLocation(createResponse2);
        ids.put(new TypeIdPair("students", studentId2), (String) createResponse2.getMetadata().get("Location").get(0));
        
        Response createResponse3 = api.createEntity("schools", new EntityBody(createTestEntity()), info);
        assertNotNull(createResponse3);
        assertEquals(Status.CREATED.getStatusCode(), createResponse3.getStatus());
        String schoolId = parseIdFromLocation(createResponse3);
        ids.put(new TypeIdPair("schools", schoolId), (String) createResponse3.getMetadata().get("Location").get(0));

        Response createResponse4 = api.createEntity(STUDENT_SCHOOL_ASSOCIATION_URI, new EntityBody(
                createTestAssoication(studentId1, schoolId)), info);
        assertNotNull(createResponse4);
        String assocId1 = parseIdFromLocation(createResponse4);
        
        Response createResponse5 = api.createEntity(STUDENT_SCHOOL_ASSOCIATION_URI, new EntityBody(
                createTestAssoication(studentId2, schoolId)), info);
        assertNotNull(createResponse5);
        String assocId2 = parseIdFromLocation(createResponse5);
        
        Response createResponse6 = api.createEntity("teachers", new EntityBody(createTestEntity()), info);
        assertNotNull(createResponse6);
        String teacherId1 = parseIdFromLocation(createResponse6);
        ids.put(new TypeIdPair("teachers", teacherId1), (String) createResponse6.getMetadata().get("Location").get(0));
        
        // test section
        Response createResponse7 = api.createEntity("sections", new EntityBody(createTestEntity()), info);
        assertNotNull(createResponse7);
        String sectionId1 = parseIdFromLocation(createResponse7);
        ids.put(new TypeIdPair("sections", sectionId1), (String) createResponse7.getMetadata().get("Location").get(0));
        
        Response createResponse8 = api.createEntity("assessments", new EntityBody(createTestEntity()), info);
        assertNotNull(createResponse8);
        String assessmentId1 = parseIdFromLocation(createResponse8);
        ids.put(new TypeIdPair("assessments", assessmentId1), (String) createResponse8.getMetadata().get("Location")
                .get(0));
        
        Response createResponse9 = api.createEntity(STUDENT_ASSESSMENT_ASSOCIATION_URI, new EntityBody(
                createTestStudentAssessmentAssociation(studentId1, assessmentId1)), info);
        assertNotNull(createResponse9);
        String studentAssessmentAssocId = parseIdFromLocation(createResponse9);
        
        Response createResponseSSA = api.createEntity(STUDENT_SECTION_ASSOCIATION_URI, new EntityBody(
                createTestStudentSectionAssociation(studentId1, sectionId1)), info);
        assertNotNull(createResponseSSA);
        String studentSectionAssocId = parseIdFromLocation(createResponseSSA);
        
        Response createResponse11 = api.createEntity(TEACHER_SCHOOL_ASSOCIATION_URI, new EntityBody(
                createTestTeacherSchoolAssociation(teacherId1, schoolId)), info);
        assertNotNull(createResponse11);
        String teacherSchoolAssocId = parseIdFromLocation(createResponse11);

        Response createResponseStaff = api.createEntity("staff", new EntityBody(createTestEntity()), info);
        assertNotNull(createResponseStaff);
        assertEquals(Status.CREATED.getStatusCode(), createResponseStaff.getStatus());
        String staffId = parseIdFromLocation(createResponseStaff);
        ids.put(new TypeIdPair("staff", staffId), (String) createResponseStaff.getMetadata().get("Location").get(0));

        // test staff entity
        // assertEntityResponse("staff", info, ids);
        
        // test educationOrganization entity
        Response createEdOrgResponse = api.createEntity("educationOrganizations", new EntityBody(createTestEntity()), info);
        assertNotNull(createEdOrgResponse);
        String educationOrganizationId = parseIdFromLocation(createEdOrgResponse);
        ids.put(new TypeIdPair("educationOrganizations", educationOrganizationId), (String) createEdOrgResponse.getMetadata().get("Location").get(0));
        
        Response createResponseEOSA = api.createEntity(EDUCATIONORGANIZATION_SCHOOL_ASSOCIATION_URI, new EntityBody(
                createTestEducationOrganizationSchoolAssociation(educationOrganizationId, schoolId)), info);
        assertNotNull(createResponseEOSA);
        String educationOrganizationSchoolAssocId = parseIdFromLocation(createResponseEOSA);
        
        Response createResponseStaffEOA = api.createEntity(STAFF_EDUCATIONORGANIZATION_ASSOCIATION_URI, new EntityBody(
                createTestEducationOrganizationStaffAssociation(staffId, educationOrganizationId)), info);
        assertNotNull(createResponseEOSA);
        String educationOrganizationStaffAssocId = parseIdFromLocation(createResponseStaffEOA);
        
        Response createResponseSAA = api.createEntity(SECTION_ASSESSMENT_ASSOCIATION_URI, new EntityBody(
                createTestSectionAssessmentAssociation(sectionId1, assessmentId1)), info);
        assertNotNull(createResponseSAA);
        String sectionAssessmentAssocId = parseIdFromLocation(createResponseSAA);

        Response createResponseSSchA = api.createEntity(SECTION_SCHOOL_ASSOCIATION_URI, new EntityBody(
                createTestSectionSchoolAssociation(sectionId1, schoolId)), info);
        assertNotNull(createResponseSSchA);
        String sectionSchoolAssocId = parseIdFromLocation(createResponseSSchA);

        // test get
        for (TypeIdPair typeId : ids.keySet()) {
            assertStudentCorrect(info, typeId);
        }
        
        // test associations
        for (String id : new String[] { assocId1, assocId2 }) {
            Response r = api.getEntity(STUDENT_SCHOOL_ASSOCIATION_URI, id, 0, 10, info);
            EntityBody assoc = (EntityBody) r.getEntity();
            assertNotNull(assoc);
            assertEquals(id, assoc.get("id"));
            assertEquals("First grade", assoc.get("entryGradeLevel"));
            assertEquals(schoolId, assoc.get("schoolId"));
            assertNotNull(assoc.get("studentId"));
            if (!(assoc.get("studentId").equals(studentId1) || assoc.get("studentId").equals(studentId2))) {
                fail();
            }
        }
        
        // test student assessment associaiton
        Response response = api.getEntity(STUDENT_ASSESSMENT_ASSOCIATION_URI, studentAssessmentAssocId, 0, 10, info);
        EntityBody assocBody = (EntityBody) response.getEntity();
        assertNotNull(assocBody);
        assertEquals(studentAssessmentAssocId, assocBody.get("id"));
        assertEquals(assocBody.get("administrationLanguage"), "ENGLISH");
        assertEquals(studentId1, assocBody.get("studentId"));
        assertEquals(assessmentId1, assocBody.get("assessmentId"));
        
        // test query on student assessment association
        UriInfo queryInfo = buildMockUriInfo("administrationLanguage=ENGLISH");
        Response queryResponse = api.getEntity(STUDENT_ASSESSMENT_ASSOCIATION_URI, studentId1, 0, 10, queryInfo);
        CollectionResponse queryCollectionResponse = (CollectionResponse) queryResponse.getEntity();
        assertNotNull(queryCollectionResponse);
        queryInfo = buildMockUriInfo("administrationLanguage=FRENCH");
        queryResponse = api.getEntity(STUDENT_ASSESSMENT_ASSOCIATION_URI, studentId1, 0, 10, queryInfo);
        queryCollectionResponse = (CollectionResponse) queryResponse.getEntity();
        assertNull(queryCollectionResponse);
        queryInfo = buildMockUriInfo("administrationDate>=2011-12-01");
        queryResponse = api.getEntity(STUDENT_ASSESSMENT_ASSOCIATION_URI, studentId1, 0, 10, queryInfo);
        queryCollectionResponse = (CollectionResponse) queryResponse.getEntity();
        assertNull(queryCollectionResponse);
        queryInfo = buildMockUriInfo("administrationDate<=2011-12-01");
        queryResponse = api.getEntity(STUDENT_ASSESSMENT_ASSOCIATION_URI, studentId1, 0, 10, queryInfo);
        queryCollectionResponse = (CollectionResponse) queryResponse.getEntity();
        assertNotNull(queryCollectionResponse);
        
        // test student section association
        Response ssaResponse = api.getEntity(STUDENT_SECTION_ASSOCIATION_URI, studentSectionAssocId, 0, 10, info);
        EntityBody ssaAssocBody = (EntityBody) ssaResponse.getEntity();
        assertNotNull(ssaAssocBody);
        assertEquals(studentSectionAssocId, ssaAssocBody.get("id"));
        assertEquals(studentId1, ssaAssocBody.get("studentId"));
        assertEquals(sectionId1, ssaAssocBody.get("sectionId"));
        
        // test teacher school association
        Response tsaResponse = api.getEntity(TEACHER_SCHOOL_ASSOCIATION_URI, teacherSchoolAssocId, 0, 10, info);
        EntityBody tsaAssocBody = (EntityBody) tsaResponse.getEntity();
        assertNotNull(tsaAssocBody);
        assertEquals(teacherSchoolAssocId, tsaAssocBody.get("id"));
        assertEquals(teacherId1, tsaAssocBody.get("teacherId"));
        assertEquals(schoolId, tsaAssocBody.get("schoolId"));
        
        // test freaky association uri
        for (String id : new String[] { studentId1, studentId2 }) {
            Response r = api.getEntity(STUDENT_SCHOOL_ASSOCIATION_URI, id, 0, 10, info);
            CollectionResponse cr = (CollectionResponse) r.getEntity();
            assertNotNull(cr);
            assertEquals(1, cr.size());
            assertNotNull(cr.get(0).getId());
            if (!(cr.get(0).getId().equals(assocId1) || cr.get(0).getId().equals(assocId2))) {
                fail();
            }
            assertNotNull(cr.get(0).getLink());
            assertEquals("self", cr.get(0).getLink().getRel());
            assertNotNull(cr.get(0).getLink().getHref());
            assertTrue(cr.get(0).getLink().getHref().contains(cr.get(0).getId()));
        }
        
        testHops(info, studentId1, studentId2, schoolId);
        
        // test query on hops
        testHopQuery(studentId1, studentId2, schoolId);

        // test update/get/delete
        for (TypeIdPair typeId : ids.keySet()) {
            Response r = api.getEntity(typeId.type, typeId.id, 0, 100, info);
            EntityBody body = (EntityBody) r.getEntity();
            body.put("field1", 99);
            Response r2 = api.updateEntity(typeId.type, typeId.id, body);
            assertEquals(Status.NO_CONTENT.getStatusCode(), r2.getStatus());
            
            Response r3 = api.getEntity(typeId.type, typeId.id, 0, 100, info);
            EntityBody body3 = (EntityBody) r3.getEntity();
            assertNotNull(body3);
            assertEquals(body, body3);
            
            Response d = api.deleteEntity(typeId.type, typeId.id);
            assertNull(d.getEntity());
            assertEquals(Status.NO_CONTENT.getStatusCode(), d.getStatus());
            
            Response r4 = api.getEntity(typeId.type, typeId.id, 0, 100, info);
            assertEquals(Status.NOT_FOUND.getStatusCode(), r4.getStatus());
        }
        
    }
    
    /**
     * Helper method for testing entity responses
     * 
     * @param entityName
     */
    private void assertEntityResponse(String entityName, UriInfo info, Map<TypeIdPair, String> typeIds) {
        Response createResponse = api.createEntity(entityName, new EntityBody(createTestEntity()), info);
        assertNotNull(createResponse);
        
        String assessmentId = parseIdFromLocation(createResponse);
        typeIds.put(new TypeIdPair(entityName, assessmentId), (String) createResponse.getMetadata().get("Location")
                .get(0));
    }
    
    private void assertStudentCorrect(UriInfo info, TypeIdPair typeId) {
        Response r = api.getEntity(typeId.type, typeId.id, 0, 100, info);
        EntityBody body = (EntityBody) r.getEntity();
        assertNotNull(body);
        assertEquals(typeId.id, body.get("id"));
        assertEquals(1, body.get("field1"));
        assertEquals(2, body.get("field2"));
        
        if (typeId.type.equals("students")) {
            List<?> links = (List<?>) body.get("links");
            assertTrue(links.contains(new EmbeddedLink("self", "student", "base/students/" + typeId.id)));
            assertTrue(links.contains(new EmbeddedLink("getStudentSchoolAssociations", "studentSchoolAssociation",
                    "base/student-school-associations/" + typeId.id)));
            assertTrue(links.contains(new EmbeddedLink("getSchools", "school", "base/student-school-associations/"
                    + typeId.id + "/targets")));
        }
    }
    
    // The .../targets links, has nothing to do with beer
    private void testHops(UriInfo info, String studentId1, String studentId2, String schoolId) {
        Response hopResponse = api.getHoppedRelatives(STUDENT_SCHOOL_ASSOCIATION_URI, schoolId, 0, 10, info);
        CollectionResponse hopCollection = (CollectionResponse) hopResponse.getEntity();
        assertNotNull(hopCollection);
        assertEquals(2, hopCollection.size());
        Set<String> hoppedRelatives = new HashSet<String>();
        for (EntityReference entity : hopCollection) {
            hoppedRelatives.add(entity.getId());
        }
        assertEquals(new HashSet<String>(Arrays.asList(studentId1, studentId2)), hoppedRelatives);
    }
    
    // test query on hop
    private void testHopQuery(String studentId1, String studentId2, String schoolId) throws Exception {
        UriInfo queryInfo = buildMockUriInfo("field1=1");
        Response hopResponse = api.getHoppedRelatives(STUDENT_SCHOOL_ASSOCIATION_URI, schoolId, 0, 10, queryInfo);
        CollectionResponse hopCollection = (CollectionResponse) hopResponse.getEntity();
        assertNotNull(hopCollection);
        assertEquals(2, hopCollection.size());
        Set<String> hoppedRelatives = new HashSet<String>();
        for (EntityReference entity : hopCollection) {
            hoppedRelatives.add(entity.getId());
        }
        assertEquals(new HashSet<String>(Arrays.asList(studentId1, studentId2)), hoppedRelatives);
        
        queryInfo = buildMockUriInfo("field1=10");
        hopResponse = api.getHoppedRelatives(STUDENT_SCHOOL_ASSOCIATION_URI, schoolId, 0, 10, queryInfo);
        hopCollection = (CollectionResponse) hopResponse.getEntity();
        assertNull(hopCollection);
        
        queryInfo = buildMockUriInfo("field1>10");
        hopResponse = api.getHoppedRelatives(STUDENT_SCHOOL_ASSOCIATION_URI, schoolId, 0, 10, queryInfo);
        hopCollection = (CollectionResponse) hopResponse.getEntity();
        assertNull(hopCollection);
        
        queryInfo = buildMockUriInfo("field1<10");
        hopResponse = api.getHoppedRelatives(STUDENT_SCHOOL_ASSOCIATION_URI, schoolId, 0, 10, queryInfo);
        hopCollection = (CollectionResponse) hopResponse.getEntity();
        assertNotNull(hopCollection);
        assertEquals(2, hopCollection.size());
    }

    private static String parseIdFromLocation(Response response) {
        List<Object> locationHeaders = response.getMetadata().get("Location");
        assertNotNull(locationHeaders);
        assertEquals(1, locationHeaders.size());
        Pattern regex = Pattern.compile(".+/(\\d+)$");
        Matcher matcher = regex.matcher((String) locationHeaders.get(0));
        matcher.find();
        assertEquals(1, matcher.groupCount());
        return matcher.group(1);
    }
    
    public UriInfo buildMockUriInfo(final String queryString) throws Exception {
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
        
        when(mock.getRequestUri()).thenReturn(new UriBuilderImpl().replaceQuery(queryString).build(null));
        return mock;
    }
}

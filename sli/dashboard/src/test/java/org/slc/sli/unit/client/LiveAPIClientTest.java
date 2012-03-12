package org.slc.sli.unit.client;

import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slc.sli.client.LiveAPIClient;
import org.slc.sli.client.RESTClient;
import org.slc.sli.entity.GenericEntity;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Unit test for the Live API client.
 */
public class LiveAPIClientTest {

    private LiveAPIClient client;
    private RESTClient mockRest;
    private ApplicationContext appContext;

    @Before
    public void setUp() throws Exception {
        // Get the initalized bean from spring config
        appContext = new ClassPathXmlApplicationContext("application-context.xml");
        client = (LiveAPIClient) appContext.getBean("apiClient");
        mockRest = mock(RESTClient.class);
        
        client.setRestClient(mockRest);
    }

    @After
    public void tearDown() throws Exception {
        client = null;
        mockRest = null;
    }

    @Test
    public void testGetStudentAttendance() throws Exception {
        List<GenericEntity> attendance;
        attendance = client.getStudentAttendance(null, null);
        assertNotNull(attendance);
        assert (attendance.size() == 0);
        
        String url = client.getApiUrl() + "v1/students/1000/attendances";
        
        String json = "[{attendance: \"yes\"},{attendance:\"no\"}]";
        when(mockRest.makeJsonRequestWHeaders(url, null, true)).thenReturn(json);
        attendance = null;
        attendance = client.getStudentAttendance(null, "1000");
        assertNotNull(attendance);
        assert (attendance.size() == 2);
        assert (attendance.get(0).get("attendance").equals("yes"));
    }
    
    @Test
    public void testGetCourses() {
        String url = client.getApiUrl() + "/v1/students/56789/studentTranscriptAssociations/courses?subjectArea=math&includeFields=courseId,courseTitle";
        String token = "token";
        
        //build the params
        Map<String, String> params = new HashMap<String, String>();
        params.put("subjectArea", "math");
        params.put("includeFields", "courseId,courseTitle");
        
        String json = "[{courseId: \"123456\",courseTitle:\"Math 1\"},{courseId: \"987654\",courseTitle:\"French 1\"}]";
        when(mockRest.makeJsonRequestWHeaders(url, token, false)).thenReturn(json);
        
        List<GenericEntity> courses = client.getCourses(token, "56789", params);
        assertEquals("Size should match", 2, courses.size());
        assertEquals("course id should match", "123456", courses.get(0).get("courseId"));
        assertEquals("course title should match", "Math 1", courses.get(0).get("courseTitle"));
    }
    
    @Test
    public void testGetStudentTranscriptAssociations() {
        String url = client.getApiUrl() + "/v1/students/56789/studentTranscriptAssociations?courseId=123456&includeFields=finalLetterGradeEarned,studentId";
        String token = "token";
        
        //build the params
        Map<String, String> params = new HashMap<String, String>();
        params.put("courseId", "123456");
        params.put("includeFields", "finalLetterGradeEarned,studentId");
        
        String json = "[{finalLetterGradeEarned: \"A\",studentId:\"56789\"},{finalLetterGradeEarned: \"C\",studentId:\"56789\"}]";
        when(mockRest.makeJsonRequestWHeaders(url, token, false)).thenReturn(json);
        
        List<GenericEntity> assocs = client.getStudentTranscriptAssociations(token, "56789", params);
        assertEquals("Size should match", 2, assocs.size());
        assertEquals("student id should match", "56789", assocs.get(0).get("studentId"));
        assertEquals("finalLetterGradeEarned should match", "A", assocs.get(0).get("finalLetterGradeEarned"));
    }
    
    @Test
    public void testGetSections() {
        String url = client.getApiUrl() + "/v1/students/56789/studentSectionAssociations/sections?courseId=123456&includeFields=sessionId";
        String token = "token";
        
        //build the params
        Map<String, String> params = new HashMap<String, String>();
        params.put("courseId", "123456");
        params.put("includeFields", "sessionId");
        
        String json = "[{sessionId:\"98765\"},{sessionId:\"99999\"}]";
        when(mockRest.makeJsonRequestWHeaders(url, token, false)).thenReturn(json);
        
        List<GenericEntity> assocs = client.getSections(token, "56789", params);
        assertEquals("Size should match", 2, assocs.size());
        assertEquals("student id should match", "98765", assocs.get(0).get("sessionId"));
    }
    
    @Test
    public void testGetEntity() {
        String url = client.getApiUrl() + "/v1/sessions/56789?includeFields=schoolYear,term";
        String token = "token";
        
        //build the params
        Map<String, String> params = new HashMap<String, String>();
        params.put("includeFields", "schoolYear,term");
        
        String json = "{schoolYear:\"2008-2009\",term:\"Fall Semester\"}";
        when(mockRest.makeJsonRequestWHeaders(url, token, false)).thenReturn(json);
        
        GenericEntity entity = client.getEntity(token, "sessions", "56789", params);
        assertNotNull("should not be null", entity);
        assertEquals("school year should match", "2008-2009", entity.get("schoolYear"));
        assertEquals("term should match", "Fall Semester", entity.get("term"));
    }
    
    @Test
    public void testBuildQueryString() {
        //build the params
        Map<String, String> params = new HashMap<String, String>();
        params.put("courseId", "123456");
        params.put("includeFields", "finalLetterGradeEarned,studentId");
        
        //String query = client.b
    }
}

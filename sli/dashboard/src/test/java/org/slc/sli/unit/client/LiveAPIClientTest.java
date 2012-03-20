package org.slc.sli.unit.client;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
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

/**
 * Unit test for the Live API client.
 */
public class LiveAPIClientTest {
    
    private LiveAPIClient client;
    private RESTClient mockRest;
    
    @Before
    public void setUp() throws Exception {
        if (System.getProperty("env") == null)
            System.setProperty("env", "dev");
        // Get the initalized bean from spring config
        client = new LiveAPIClient();
        mockRest = mock(RESTClient.class);
        
        client.setRestClient(mockRest);
    }
    
    @After
    public void tearDown() throws Exception {
        client = null;
        mockRest = null;
    }
    
    @Test
    public void testGetSessionsByYear() throws Exception {
        List<GenericEntity> sessions;
        String url = client.getApiUrl() + "/sessions/";
        when(mockRest.makeJsonRequestWHeaders(url, null, false)).thenReturn("[]");
        sessions = client.getSessionsByYear(null, null);
        assertNull(sessions);
        
        url = client.getApiUrl() + "/sessions/?schoolYear=2011-2012";
        String json = "[{session: \"Yes\"}, {session: \"No\"}]";
        when(mockRest.makeJsonRequestWHeaders(url, null, false)).thenReturn(json);
        sessions = client.getSessionsByYear(null, "2011-2012");
        assertNotNull(sessions);
        assertTrue(sessions.size() == 2);
    }
    
    @Test
    public void testGetStudentAttendance() throws Exception {
        List<GenericEntity> attendance;
        attendance = client.getStudentAttendance(null, null, null, null);
        assertNotNull(attendance);
        assert (attendance.size() == 0);
        String url = client.getApiUrl() + "/v1/students/1000/attendances";
        
        String json = "[{attendance: \"yes\"},{attendance:\"no\"}]";
        when(mockRest.makeJsonRequestWHeaders(url, null, false)).thenReturn(json);
        attendance = null;
        attendance = client.getStudentAttendance(null, "1000", null, null);
        assertNotNull(attendance);
        assert (attendance.size() == 2);
        assert (attendance.get(0).get("attendance").equals("yes"));
    }
    
    @Test
    public void testGetStudentAttendanceWithDates() throws Exception {
        List<GenericEntity> attendance;
        attendance = client.getStudentAttendance(null, null, null, null);
        assertNotNull(attendance);
        assert (attendance.size() == 0);
        
        String url = client.getApiUrl() + "/v1/students/1000/attendances?eventDate>=2011-07-13&eventDate<=2012-07-13";
        
        String json = "[{attendance: \"yes\"},{attendance:\"no\"}]";
        when(mockRest.makeJsonRequestWHeaders(url, null, false)).thenReturn(json);
        attendance = null;
        attendance = client.getStudentAttendance(null, "1000", "2011-07-13", "2012-07-13");
        assertNotNull(attendance);
        assert (attendance.size() == 2);
        assert (attendance.get(0).get("attendance").equals("yes"));
    }
    
    @Test
    public void testGetCourses() {
        String url = client.getApiUrl()
                + "/v1/students/56789/studentTranscriptAssociations/courses?subjectArea=math&includeFields=courseId,courseTitle";
        String token = "token";
        
        // build the params
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
        String url = client.getApiUrl()
                + "/v1/students/56789/studentTranscriptAssociations?courseId=123456&includeFields=finalLetterGradeEarned,studentId";
        String token = "token";
        
        // build the params
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
        String url = client.getApiUrl()
                + "/v1/students/56789/studentSectionAssociations/sections?courseId=123456&includeFields=sessionId";
        String token = "token";
        
        // build the params
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
        
        // build the params
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
    public void testGetStudentSectionGradebookEntries() {
        String url = client.getApiUrl()
                + "/v1/studentSectionGradebookEntries?sectionId=1234&studentId=5678&includeFields=numericGradeEarned,dateFulfilled";
        String token = "token";
        
        // build the params
        Map<String, String> params = new HashMap<String, String>();
        params.put("studentId", "5678");
        params.put("sectionId", "1234");
        params.put("includeFields", "numericGradeEarned,dateFulfilled");
        
        String json = "[{numericGradeEarned:\"84.0\",dateFulfilled:\"2011-10-30\"},{numericGradeEarned:\"98.0\",dateFulfilled:\"2011-11-20\"}]";
        when(mockRest.makeJsonRequestWHeaders(url, token, false)).thenReturn(json);
        
        List<GenericEntity> gradebookEntries = client.getStudentSectionGradebookEntries(token, "5678", params);
        assertEquals("Size should match", 2, gradebookEntries.size());
        assertEquals("numeric grade should match", "84.0", gradebookEntries.get(0).get("numericGradeEarned"));
        assertEquals("dateFulfilled should match", "2011-10-30", gradebookEntries.get(0).get("dateFulfilled"));
    }
    
    @Test
    public void testBuildQueryString() {
        // build the params
        Map<String, String> params = new HashMap<String, String>();
        params.put("courseId", "123456");
        params.put("includeFields", "finalLetterGradeEarned,studentId");
        
        // String query = client.b
    }
}

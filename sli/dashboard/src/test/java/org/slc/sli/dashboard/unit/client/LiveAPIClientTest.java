/*
 * Copyright 2012-2013 inBloom, Inc. and its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.slc.sli.dashboard.unit.client;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.slc.sli.dashboard.client.LiveAPIClient;
import org.slc.sli.dashboard.client.RESTClient;
import org.slc.sli.dashboard.entity.ConfigMap;
import org.slc.sli.dashboard.entity.GenericEntity;
import org.slc.sli.dashboard.util.Constants;
import org.slc.sli.dashboard.util.JsonConverter;

/**
 * Unit test for the Live API client.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/application-context-test.xml" })
public class LiveAPIClientTest {

    private static final String EDORGS_URL = "/v1/educationOrganizations/";
    private static final String CUSTOM_DATA = "/custom";

    private LiveAPIClient client;
    private RESTClient mockRest;

    @Value("${api.server.url}")
    private String apiUrl;

    @Before
    public void setUp() throws Exception {
        if (System.getProperty("env") == null) {
            System.setProperty("env", "dev");
        }
        // Get the initalized bean from spring config
        client = new LiveAPIClient();
        client.setApiUrl(apiUrl);
        mockRest = mock(RESTClient.class);

        client.setRestClient(mockRest);
    }

    @After
    public void tearDown() throws Exception {
        client = null;
        mockRest = null;
    }

    @Test
    public void testGetEdOrgCustomData() throws Exception {

        String token = "token";
        String id = "id";
        String url = client.getApiUrl() + EDORGS_URL + id + CUSTOM_DATA;
        String json = "{config: {" + "\"component_1\": " + "{" + "\"id\" : \"component_1\", "
                + "\"name\" : \"Component 1\", " + "\"type\" : \"LAYOUT\", " + "\"items\": ["
                + "{\"id\" : \"component_1_1\", \"name\": \"First Child Component\", \"type\": \"PANEL\"}, "
                + "{\"id\" : \"component_1_2\", \"name\": \"Second Child Component\", \"type\": \"PANEL\"}" + "]" + "}"
                + "}}";

        when(mockRest.makeJsonRequestWHeaders(url, token)).thenReturn(
                JsonConverter.toJson(new LiveAPIClient.CustomEntityWrapper(json)));
        ConfigMap customConfig = client.getEdOrgCustomData(token, id);
        assertNotNull(customConfig);
        assertEquals(1, customConfig.size());
        assertEquals("component_1", customConfig.getComponentConfig("component_1").getId());

    }

    @Test
    public void testPutEdOrgCustomData() throws Exception {

        String token = "token";
        String id = "id";
        String url = client.getApiUrl() + EDORGS_URL + id + CUSTOM_DATA;
        String json = "{config:{" + "\"component_1\": " + "{" + "\"id\" : \"component_1\", "
                + "\"name\" : \"Component 1\", " + "\"type\" : \"LAYOUT\", " + "\"items\": ["
                + "{\"id\" : \"component_1_1\", \"name\": \"First Child Component\", \"type\": \"PANEL\"}, "
                + "{\"id\" : \"component_1_2\", \"name\": \"Second Child Component\", \"type\": \"PANEL\"}" + "]" + "}"
                + "}}";
        Gson gson = new GsonBuilder().create();
        ConfigMap customConfig = gson.fromJson(json, ConfigMap.class);
        RestClientAnswer restClientAnswer = new RestClientAnswer();
        Mockito.doAnswer(restClientAnswer).when(mockRest)
                .putJsonRequestWHeaders(Mockito.anyString(), Mockito.anyString(), Mockito.anyObject());
        client.putEdOrgCustomData(token, id, customConfig);
        String customJson = restClientAnswer.getJson();

        when(mockRest.makeJsonRequestWHeaders(url, token)).thenReturn(customJson);
        customConfig = client.getEdOrgCustomData(token, id);
        assertNotNull(customConfig);
        assertEquals(1, customConfig.size());
        assertEquals("component_1", customConfig.getComponentConfig("component_1").getId());

    }

    private static class RestClientAnswer implements Answer {

        private String json;

        @Override
        public Object answer(InvocationOnMock invocation) throws Throwable {
            json = (String) invocation.getArguments()[2];
            return null;
        }

        public String getJson() {
            return json;
        }
    }

    @Test
    public void testGetSessionsByYear() throws Exception {
        List<GenericEntity> sessions;
        String url = client.getApiUrl() + "/v1/sessions/";
        when(mockRest.makeJsonRequestWHeaders(url, null)).thenReturn("[]");
        sessions = client.getSessionsByYear(null, null);
        assertEquals(sessions.size(), 0);

        url = client.getApiUrl() + "/v1/sessions/?schoolYear=2011-2012";
        String json = "[{session: \"Yes\"}, {session: \"No\"}]";
        when(mockRest.makeJsonRequestWHeaders(url, null)).thenReturn(json);
        sessions = client.getSessionsByYear(null, "2011-2012");
        assertNotNull(sessions);
        assertTrue(sessions.size() == 2);
    }

    @Test
    public void testGetStudentAttendance() throws Exception {
        List<GenericEntity> attendance;
        attendance = client.getStudentAttendance(null, null, null, null);
        assertNotNull(attendance);
        assertEquals(0, attendance.size());
        String url = client.getApiUrl() + "/v1/students/1000/attendances";

        String json = "[{attendance: \"yes\"},{attendance:\"no\"}]";
        when(mockRest.makeJsonRequestWHeaders(url, null)).thenReturn(json);
        attendance = null;
        attendance = client.getStudentAttendance(null, "1000", null, null);
        assertNotNull(attendance);
        assertEquals(2, attendance.size());
        assertEquals("yes", attendance.get(0).get("attendance"));

        url = client.getApiUrl() + "/v1/students/1000/attendances?eventDate>=2011-07-13&eventDate<=2012-07-13";

        json = "[{attendance: \"yes\"},{attendance:\"no\"}]";
        when(mockRest.makeJsonRequestWHeaders(url, null)).thenReturn(json);
        attendance = null;
        attendance = client.getStudentAttendance(null, "1000", "2011-07-13", "2012-07-13");
        assertNotNull(attendance);
        assertEquals(2, attendance.size());
        assertEquals("yes", attendance.get(0).get("attendance"));
    }

    @Test
    public void testGetCourses() {
        String url = client.getApiUrl()
                + "/v1/sections/56789/studentSectionAssociations/students?views=transcript";
        String token = "token";

        // build the params
        Map<String, String> params = new HashMap<String, String>();
        params.put("subjectArea", "math");
        params.put("includeFields", "courseId,courseTitle");

        String json = "[{courseId: \"123456\",courseTitle:\"Math 1\"},{courseId: \"987654\",courseTitle:\"French 1\"}]";
        when(mockRest.makeJsonRequestWHeaders(url, token)).thenReturn(json);

        List<GenericEntity> courses = client.getCourses(token, "56789", params);
        assertEquals("Size should match", 2, courses.size());
        assertEquals("course id should match", "123456", courses.get(0).get("courseId"));
        assertEquals("course title should match", "Math 1", courses.get(0).get("courseTitle"));
    }

    @Test
    public void testGetCourseTranscripts() {
        String url = client.getApiUrl()
                + "/v1/students/56789/courseTranscripts?courseId=123456&includeFields=finalLetterGradeEarned,studentId";
        String token = "token";

        // build the params
        Map<String, String> params = new HashMap<String, String>();
        params.put("courseId", "123456");
        params.put("includeFields", "finalLetterGradeEarned,studentId");

        String json = "[{finalLetterGradeEarned: \"A\",studentId:\"56789\"},{finalLetterGradeEarned: \"C\",studentId:\"56789\"}]";
        when(mockRest.makeJsonRequestWHeaders(url, token)).thenReturn(json);

        List<GenericEntity> assocs = client.getCourseTranscripts(token, "56789", params);
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
        when(mockRest.makeJsonRequestWHeaders(url, token)).thenReturn(json);

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
        when(mockRest.makeJsonRequestWHeaders(url, token)).thenReturn(json);

        GenericEntity entity = client.getEntity(token, "sessions", "56789", params);
        assertNotNull("should not be null", entity);
        assertEquals("school year should match", "2008-2009", entity.get("schoolYear"));
        assertEquals("term should match", "Fall Semester", entity.get("term"));
    }

    @Test
    public void testGetStudentGradebookEntries() {
        String url = client.getApiUrl()
                + "/v1/studentGradebookEntries?sectionId=1234&studentId=5678&includeFields=numericGradeEarned,dateFulfilled";
        String token = "token";

        // build the params
        Map<String, String> params = new HashMap<String, String>();
        params.put("studentId", "5678");
        params.put("sectionId", "1234");
        params.put("includeFields", "numericGradeEarned,dateFulfilled");

        String json = "[{numericGradeEarned:\"84.0\",dateFulfilled:\"2011-10-30\"},{numericGradeEarned:\"98.0\",dateFulfilled:\"2011-11-20\"}]";
        when(mockRest.makeJsonRequestWHeaders(url, token)).thenReturn(json);

        List<GenericEntity> gradebookEntries = client.getStudentGradebookEntries(token, "5678", params);
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

    @Ignore
    @Test
    public void testGetSchools() {
        LiveAPIClient liveClient = new LiveAPIClient() {
            @Override
            public String getId(String token) {
                return null;
            }

            @Override
            public List<GenericEntity> getSectionsForTeacher(String teacherId, String token, String schoolId) {
                return null;
            }

        };
        SecurityContextHolder.getContext().setAuthentication(new Authentication() {
            @Override
            public String getName() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
                // TODO Auto-generated method stub

            }

            @Override
            public boolean isAuthenticated() {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public Object getPrincipal() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public Object getDetails() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public Object getCredentials() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public Collection<GrantedAuthority> getAuthorities() {
                // TODO Auto-generated method stub
                return Collections.emptyList();
            }
        });
        String token = "fakeToken";
        String[] ids = { "1", "2" };

        List<GenericEntity> result = liveClient.getSchools(token, Arrays.asList(ids));
        assertEquals(result.size(), 1);
    }

    @Test
    public void testGetStudents() {
        LiveAPIClient liveClient = new LiveAPIClient() {

            @Override
            public GenericEntity getStudent(String token, String id) {
                return new GenericEntity();
            }
        };
        String[] ids = { "1", "2" };
        String token = "fakeToken";
        List<GenericEntity> result = liveClient.getStudents(token, Arrays.asList(ids));
        assertEquals(result.size(), 2);
    }

    @Test
    public void testGetStudentAssessments() {
        String token = "fakeToken";
        String id = "1";
        LiveAPIClient liveClient = new LiveAPIClient() {
            @Override
            public List<GenericEntity> createEntitiesFromAPI(String url, String token) {
                GenericEntity ge = new GenericEntity();
                List<GenericEntity> list = new LinkedList<GenericEntity>();
                list.add(ge);
                return list;
            }
        };

        List<GenericEntity> result = liveClient.getStudentAssessments(token, id);
        assertEquals(result.size(), 1);
    }

    @Test
    public void testGetSection() {
        LiveAPIClient liveClient = new LiveAPIClient() {
            @Override
            public List<GenericEntity> createEntitiesFromAPI(String url, String token) {
                GenericEntity ge = new GenericEntity();
                ge.put(Constants.ATTR_STUDENT_ID, "1");
                List<GenericEntity> list = new LinkedList<GenericEntity>();
                list.add(ge);
                return list;
            }

            @Override
            public GenericEntity createEntityFromAPI(String url, String token) {
                GenericEntity ge = new GenericEntity();
                ge.put(Constants.ATTR_UNIQUE_SECTION_CODE, "section");
                return ge;
            }
        };
        GenericEntity section = liveClient.getSection("1", "fakeToken");
        assertEquals(section.get("uniqueSectionCode"), "section");
        assertEquals(section.get("sectionName"), "section");

    }

    @Test
    public void testGetSession() {
        client.getSession(null, null);
    }

    @Test
    public void testGetId() {
        LiveAPIClient liveClient = new LiveAPIClient() {
            @Override
            public GenericEntity createEntityFromAPI(String url, String token) {
                GenericEntity ge = new GenericEntity();
                List<Map> list = new LinkedList<Map>();
                Map selfMap = new HashMap();
                selfMap.put(Constants.ATTR_REL, Constants.ATTR_SELF);
                selfMap.put(Constants.ATTR_HREF,
                        "https://local.slidev.org/api/rest/v1/teachers/9d32fcd6-4148-4b6c-8c8b-b87ae9ef5a4b");
                list.add(selfMap);
                ge.put(Constants.ATTR_LINKS, list);
                return ge;
            }
        };
        assertEquals(liveClient.getId("token"), "9d32fcd6-4148-4b6c-8c8b-b87ae9ef5a4b");
    }

    @Test
    public void testGetSectionsForTeacher() {
        LiveAPIClient liveClient = new LiveAPIClient() {
            @Override
            public List<GenericEntity> createEntitiesFromAPI(String url, String token) {
                GenericEntity ge = new GenericEntity();
                ge.put(Constants.ATTR_SECTION_ID, "1");
                List<GenericEntity> list = new LinkedList<GenericEntity>();
                list.add(ge);
                return list;
            }

            @Override
            public GenericEntity getSection(String id, String token) {
                return new GenericEntity();
            }

        };

        List<GenericEntity> result = liveClient.getSectionsForTeacher(null, null, null);
        assertEquals(result.size(), 1);
    }

    @Test
    public void testGetHomeroomForStudent() {

        LiveAPIClient liveClient = new LiveAPIClient() {
            @Override
            public List<GenericEntity> createEntitiesFromAPI(String url, String token) {
                List<GenericEntity> list = new LinkedList<GenericEntity>();
                if (token.equals("empty")) {
                    return list;
                }
                GenericEntity ge = new GenericEntity();
                ge.put(Constants.ATTR_HOMEROOM_INDICATOR, true);
                list.add(ge);
                if (token.equals("two")) {
                    list.add(new GenericEntity());
                }
                return list;
            }

            @Override
            public GenericEntity getSection(String sectionId, String token) {
                GenericEntity section = new GenericEntity();
                section.put(Constants.ATTR_NAME, "section");
                return section;
            }
        };

        GenericEntity ge = liveClient.getHomeRoomForStudent(null, "one");
        assertEquals(ge.get(Constants.ATTR_NAME), "section");
        ge = liveClient.getHomeRoomForStudent(null, "two");
        assertEquals(ge.get(Constants.ATTR_NAME), "section");
        ge = liveClient.getHomeRoomForStudent(null, "empty");
        assertEquals(ge, null);
    }

    @Test
    public void testGetTeacherForSection() {
        LiveAPIClient liveClient = new LiveAPIClient() {
            @Override
            public List<GenericEntity> createEntitiesFromAPI(String url, String token) {
                GenericEntity ge = new GenericEntity();
                ge.put(Constants.ATTR_CLASSROOM_POSITION, Constants.TEACHER_OF_RECORD);
                List<GenericEntity> list = new LinkedList<GenericEntity>();
                if (token.equals("empty")) {
                    return list;
                }
                list.add(ge);
                return list;
            }

            @Override
            public GenericEntity createEntityFromAPI(String url, String token) {
                GenericEntity teacher = new GenericEntity();
                teacher.put("name", "teacher");
                return teacher;
            }
        };

        GenericEntity ge = liveClient.getTeacherForSection(null, "fakeToken");
        assertEquals(ge.get(Constants.ATTR_NAME), "teacher");
        ge = liveClient.getTeacherForSection(null, "empty");
        assertEquals(ge, null);
    }

    // Test student enrollment, by mocking out calls to API
    @Test
    public void testGetStudentEnrollment() {
        GenericEntity student = new GenericEntity();
        ArrayList<Map<String, String>> links = new ArrayList<Map<String, String>>();
        HashMap<String, String> link = new HashMap<String, String>();
        link.put(Constants.ATTR_REL, "getStudentSchoolAssociations");
        link.put(Constants.ATTR_HREF, "getStudentSchoolAssociationLink");
        links.add(link);
        student.put(Constants.ATTR_LINKS, links);
        LiveAPIClient liveClient = new LiveAPIClient() {

            /*
             * Mock out call to studentSchoolAssociations to return a fake association when a call
             * is made.
             */
            @Override
            public List<GenericEntity> createEntitiesFromAPI(String url, String token) {
                GenericEntity studentSchoolAssociation = new GenericEntity();
                ArrayList<Map<String, String>> links = new ArrayList<Map<String, String>>();
                HashMap<String, String> link = new HashMap<String, String>();
                link.put(Constants.ATTR_REL, "getSchool");
                link.put(Constants.ATTR_HREF, "getSchoolLink");
                links.add(link);
                studentSchoolAssociation.put(Constants.ATTR_LINKS, links);
                List<GenericEntity> associations = new LinkedList<GenericEntity>();
                associations.add(studentSchoolAssociation);
                return associations;
            }

            /*
             * Mock out call to schools to return a fake school when a call is made via the api.
             */
            @Override
            public GenericEntity createEntityFromAPI(String url, String token) {
                GenericEntity school = new GenericEntity();
                school.put(Constants.ATTR_NAME, "schoolName");
                return school;
            }

        };

        // Make the getStudentEnrollment call and assert that the school returned in the call is the
        // faked out school in the earlier call
        List<GenericEntity> enrollment = liveClient.getStudentEnrollment("fakeToken", student);
        GenericEntity firstEnrollment = enrollment.get(0);
        Map<String, String> school = (Map<String, String>) firstEnrollment.get(Constants.ATTR_SCHOOL);
        assertEquals(school.get(Constants.ATTR_NAME), "schoolName");
    }

    // Test student enrollment, by mocking out calls to API
    @Test
    public void testGetParentsForStudent() {
        LiveAPIClient client = new LiveAPIClient() {
            @Override
            public List<GenericEntity> createEntitiesFromAPI(String url, String token) {
                List<GenericEntity> entityList = new ArrayList<GenericEntity>();
                return entityList;
            }
        };
        Assert.assertTrue(client.getParentsForStudent(null, null).isEmpty());
    }
}

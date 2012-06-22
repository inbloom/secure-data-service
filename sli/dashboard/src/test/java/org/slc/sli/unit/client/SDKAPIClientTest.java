package org.slc.sli.unit.client;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.slc.sli.api.client.Entity;
import org.slc.sli.api.client.SLIClient;
import org.slc.sli.api.client.impl.BasicClient;
import org.slc.sli.client.SDKAPIClient;
import org.slc.sli.entity.Config;
import org.slc.sli.entity.ConfigMap;
import org.slc.sli.entity.GenericEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Unit test for the Live API client.
 *
 * @author iivanisevic
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/application-context-test.xml" })
public class SDKAPIClientTest {

    private static Logger log = LoggerFactory.getLogger(SDKAPIClientTest.class);

    private static final String CUSTOM_CONFIG_JSON = "{config:{" + "\"component_1\": " + "{"
            + "\"id\" : \"component_1\", " + "\"name\" : \"Component 1\", " + "\"type\" : \"LAYOUT\", "
            + "\"items\": ["
            + "{\"id\" : \"component_1_1\", \"name\": \"First Child Component\", \"type\": \"PANEL\"}, "
            + "{\"id\" : \"component_1_2\", \"name\": \"Second Child Component\", \"type\": \"PANEL\"}" + "]" + "}"
            + "}}";

    // Mock Data Files
    private static final String MOCK_DATA_DIRECTORY = "mock_data/";
    private static final String MOCK_SCHOOL_FILE = "school.json";
    private static final String MOCK_STAFF_FILE = "staffInfo.json";
    private static final String MOCK_SESSIONS_FILE = "session.json";
    private static final String MOCK_SECTIONS_FILE = "sections.json";
    private static final String MOCK_ENROLLMENT_FILE = "school.json";
    private static final String MOCK_STUDENTS_FILE = "student.json";
    private static final String MOCK_PROGRAMS_FILE = "student_program_association.json";
    private static final String MOCK_ASSESSMENT_METADATA_FILE = "assessment_meta_data.json";
    private static final String MOCK_ASSESSMENTS_FILE = "assessment.json";
    private static final String MOCK_ATTENDANCE_FILE = "attendance.json";
    private static final String MOCK_ED_ORG_FILE = "educational_organization.json";
    private static final String MOCK_ED_ORG_ASSOC_FILE = "educational_organization_association.json";
    private static final String MOCK_SCHOOL_ED_ORG_ASSOC_FILE = "school_educational_organization_association.json";

    private SDKAPIClient client;

    private SLIClient mockSdk;

    private ClassLoader classLoader;

    @Value("${api.server.url}")
    private String apiUrl;

    @Before
    public void setUp() throws Exception {
        client = new SDKAPIClient();
        mockSdk = mock(BasicClient.class);
        client.setSdkClient(mockSdk);
        this.classLoader = Thread.currentThread().getContextClassLoader();
    }

    @After
    public void tearDown() throws Exception {
        client = null;
        mockSdk = null;
    }

   /* @Test
    public void testGetEdOrgCustomData() throws Exception {

        String token = "token";
        String id = "id";
        String componentId = "component_1";
        String componentName = "Component 1";
        String componentType = "LAYOUT";

        SdkClientReadAnswer sdkClientReadAnswer = new SdkClientReadAnswer(CUSTOM_CONFIG_JSON);
        Mockito.doAnswer(sdkClientReadAnswer).when(mockSdk)
                .read(Mockito.anyString(), Mockito.any(List.class), Mockito.anyString(), Mockito.any(Class.class));

        ConfigMap configMap = client.getEdOrgCustomData(token, id);

        assertNotNull(configMap);
        assertEquals(1, configMap.size());
        assertEquals(componentId, configMap.getComponentConfig(componentId).getId());
        assertEquals(componentName, configMap.getComponentConfig(componentId).getName());
        assertEquals(componentType, configMap.getComponentConfig(componentId).getType().name());

    }*/

    /*@Test
    public void testPutEdOrgCustomData() throws Exception {

        String token = "token";
        String id = "id";
        String componentId = "component_1";
        String componentName = "Component 1";
        String componentType = "LAYOUT";

        Gson gson = new GsonBuilder().create();
        ConfigMap configMap = gson.fromJson(CUSTOM_CONFIG_JSON, ConfigMap.class);

        SdkClientCreateAnswer sdkClientCreateAnswer = new SdkClientCreateAnswer();
        Mockito.doAnswer(sdkClientCreateAnswer).when(mockSdk)
                .create(Mockito.anyString(), Mockito.anyString(), Mockito.any(Entity.class));

        client.putEdOrgCustomData(token, id, configMap);

        ConfigMap verifyConfigMap = sdkClientCreateAnswer.getConfigMap();

        assertNotNull(verifyConfigMap);
        assertEquals(1, verifyConfigMap.size());
        assertEquals(componentId, verifyConfigMap.getComponentConfig(componentId).getId());
        assertEquals(componentName, verifyConfigMap.getComponentConfig(componentId).getName());
        assertEquals(componentType, verifyConfigMap.getComponentConfig(componentId).getType().name());

    }*/

    @Test
    public void testGetStudent() throws MalformedURLException, URISyntaxException, IOException {
        String token = "token";
        String key = "uid";
        String studentId = "541397175";

        String filename = getFilename(MOCK_DATA_DIRECTORY + "common/" + MOCK_STUDENTS_FILE);
        SdkClientReadAnswerFromFile sdkClientReadAnswerFromFile = new SdkClientReadAnswerFromFile(filename, studentId,
                key);
        Mockito.doAnswer(sdkClientReadAnswerFromFile).when(mockSdk)
                .read(Mockito.anyString(), Mockito.any(List.class), Mockito.anyString(), Mockito.any(Class.class));

        GenericEntity studentEntity = client.getStudent(token, studentId);

        assertNotNull(studentEntity);
        assertEquals(12, studentEntity.size());
        assertEquals(studentEntity.getString(key), studentId);
    }

    public void testGetStudentsWithIdsAndParams() {
        // this test is not implemented because the underlying method is not called by anything at
        // this time
    }

    public void testGetStudents() {
        // this test is not implemented because the underlying method is only used by
        // getStudentsWithSearch and that method has its own associated test
    }

    @Test
    public void testGetStudentsForSection() throws MalformedURLException, URISyntaxException, IOException  {
        String token = "token";
        String key = "sectionId";

        // lookup of valid section
        String value = "1";
        String filename = getFilename(MOCK_DATA_DIRECTORY + "common/" + MOCK_SECTIONS_FILE);
        SdkClientReadAnswerFromFile sdkClientReadAnswerFromFile = new SdkClientReadAnswerFromFile(filename, value, key);
        Mockito.doAnswer(sdkClientReadAnswerFromFile).when(mockSdk)
                .read(Mockito.anyString(), Mockito.any(List.class), Mockito.anyString(), Mockito.any(Class.class));

        List<GenericEntity> students = client.getStudentsForSection(token, value);

        assertNotNull(students);
        // one section returned
        assertEquals(1, students.size());
        // containing 12 students
        GenericEntity ge = students.get(0);
        Object o = ge.get("studentUIDs");
        assertNotNull(o);
        assertEquals(12, ((List) o).size());

        // lookup of invalid section
        value = "3124";
        sdkClientReadAnswerFromFile = new SdkClientReadAnswerFromFile(filename, value, key);
        Mockito.doAnswer(sdkClientReadAnswerFromFile).when(mockSdk)
                .read(Mockito.anyString(), Mockito.any(List.class), Mockito.anyString(), Mockito.any(Class.class));

        students = client.getStudentsForSection(token, value);
        assertEquals(0, students.size());
    }

    @Test
    public void testGetStudentsWithSearch() throws MalformedURLException, URISyntaxException, IOException  {
        String token = "token";
        String filename = getFilename(MOCK_DATA_DIRECTORY + "common/" + MOCK_STUDENTS_FILE);

        // search by first name only
        String firstName = "Isaiah";
        String lastName = "";
        String[] searchStr = { firstName, lastName };

        SdkClientReadAnswerFromFile sdkClientReadAnswerFromFile = new SdkClientReadAnswerFromFile(filename, searchStr);
        Mockito.doAnswer(sdkClientReadAnswerFromFile).when(mockSdk)
                .read(Mockito.anyString(), Mockito.any(List.class), Mockito.anyString(), Mockito.any(Class.class));

        List<GenericEntity> students = client.getStudentsWithSearch(token, firstName, lastName);

        assertNotNull(students);
        assertEquals(2, students.size());
        assertEquals(students.get(0).getString("firstName"), firstName);

        // search by last name only
        firstName = "";
        lastName = "Glass";
        searchStr[0] = firstName;
        searchStr[1] = lastName;

        sdkClientReadAnswerFromFile = new SdkClientReadAnswerFromFile(filename, searchStr);
        Mockito.doAnswer(sdkClientReadAnswerFromFile).when(mockSdk)
                .read(Mockito.anyString(), Mockito.any(List.class), Mockito.anyString(), Mockito.any(Class.class));

        students = client.getStudentsWithSearch(token, firstName, lastName);

        assertNotNull(students);
        assertEquals(2, students.size());
        assertEquals(students.get(0).getString("lastName"), lastName);

        // search by first and last name
        firstName = "Isaiah";
        lastName = "Glass";
        searchStr[0] = firstName;
        searchStr[1] = lastName;

        sdkClientReadAnswerFromFile = new SdkClientReadAnswerFromFile(filename, searchStr);
        Mockito.doAnswer(sdkClientReadAnswerFromFile).when(mockSdk)
                .read(Mockito.anyString(), Mockito.any(List.class), Mockito.anyString(), Mockito.any(Class.class));

        students = client.getStudentsWithSearch(token, firstName, lastName);

        assertNotNull(students);
        assertEquals(1, students.size());
        assertEquals(students.get(0).getString("firstName"), firstName);
        assertEquals(students.get(0).getString("lastName"), lastName);
    }

    @Test
    public void testGetAssessmentsForStudent() throws MalformedURLException, URISyntaxException, IOException  {
        String token = "token";
        String key = "studentId";
        String studentId = "288598192";

        String filename = getFilename(MOCK_DATA_DIRECTORY + "common/" + MOCK_ASSESSMENTS_FILE);
        SdkClientReadAnswerFromFile sdkClientReadAnswerFromFile = new SdkClientReadAnswerFromFile(filename, studentId,
                key);
        Mockito.doAnswer(sdkClientReadAnswerFromFile).when(mockSdk)
                .read(Mockito.anyString(), Mockito.any(List.class), Mockito.anyString(), Mockito.any(Class.class));

        List<GenericEntity> assessments = client.getAssessmentsForStudent(token, studentId);

        assertNotNull(assessments);
        assertEquals(18, assessments.size());
        ListIterator<GenericEntity> li = assessments.listIterator();
        int count = 0;
        while (li.hasNext()) {
            GenericEntity ge = li.next();
            assertEquals(ge.getString(key), studentId);
            if (ge.getString("assessmentName").equals("StateTest_READING")) {
                count++;
            }
        }
        assertEquals(count, 6);
    }

    public void testGetAssessment() throws MalformedURLException, URISyntaxException, IOException  {
        // this test is not implemented because the underlying method is not called by anything at
        // this time
    }

    public void testGetAssessments() throws MalformedURLException, URISyntaxException, IOException {
        // this test is not implemented because the underlying method is not called by anything at
        // this time
    }

    @Test
    public void testGetSchool() throws MalformedURLException, URISyntaxException, IOException  {
        String token = "token";
        String key = "schoolId";
        String schoolId = "Illinois PS145";

        String filename = getFilename(MOCK_DATA_DIRECTORY + "common/" + MOCK_SCHOOL_FILE);
        SdkClientReadAnswerFromFile sdkClientReadAnswerFromFile = new SdkClientReadAnswerFromFile(filename, schoolId,
                key);
        Mockito.doAnswer(sdkClientReadAnswerFromFile).when(mockSdk)
                .read(Mockito.anyString(), Mockito.any(List.class), Mockito.anyString(), Mockito.any(Class.class));

        GenericEntity schoolEntity = client.getSchool(token, schoolId);

        assertNotNull(schoolEntity);
        assertEquals(3, schoolEntity.size());
        assertEquals(schoolEntity.getString(key), schoolId);
    }

    public void testGetSchoolsWithParams() throws MalformedURLException, URISyntaxException, IOException  {
        // this test is not implemented because the underlying method is not called by anything at
        // this time
    }

    @Test
    public void testGetSchools() throws MalformedURLException, URISyntaxException, IOException    {
        SDKAPIClient client = new SDKAPIClient() {
            @Override
            public String getId(String token) {
                return null;
            }

            @Override
            public List<GenericEntity> getSectionsForTeacher(String teacherId, String token, Map<String, String> params) {
                return null;
            }

            @Override
            public List<GenericEntity> getSectionsForNonEducator(String token, Map<String, String> params) {
                return null;
            }
        };
        client.setSdkClient(mockSdk);

        SecurityContextHolder.getContext().setAuthentication(new Authentication() {
            @Override
            public String getName() {
                return null;
            }

            @Override
            public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
            }

            @Override
            public boolean isAuthenticated() {
                return false;
            }

            @Override
            public Object getPrincipal() {
                return null;
            }

            @Override
            public Object getDetails() {
                return null;
            }

            @Override
            public Object getCredentials() {
                return null;
            }

            @Override
            public Collection<GrantedAuthority> getAuthorities() {
                return Collections.emptyList();
            }
        });

        String token = "token";
        String key = "schoolId";
        String[] idArr = { "Illinois PS145", "Illinois PS200" };
        List<String> schoolIds = Arrays.asList(idArr);

        String filename = getFilename(MOCK_DATA_DIRECTORY + "common/" + MOCK_SCHOOL_FILE);
        SdkClientReadAnswerFromFile sdkClientReadAnswerFromFile = new SdkClientReadAnswerFromFile(filename, schoolIds,
                key);
        Mockito.doAnswer(sdkClientReadAnswerFromFile).when(mockSdk)
                .read(Mockito.anyString(), Mockito.any(List.class), Mockito.anyString(), Mockito.any(Class.class));

        List<GenericEntity> schoolList = client.getSchools(token, schoolIds);

        assertNotNull(schoolList);
        assertEquals(2, schoolList.size());
        ListIterator<GenericEntity> li = schoolList.listIterator();
        while (li.hasNext()) {
            assertTrue(schoolIds.contains(li.next().getString(key)));
        }
    }

    @Test
    public void testGetSession() throws MalformedURLException, URISyntaxException, IOException   {
        String token = "token";
        String key = "sessionId";

        // lookup of valid session
        String value = "123456789";
        String filename = getFilename(MOCK_DATA_DIRECTORY + "common/" + MOCK_SESSIONS_FILE);
        SdkClientReadAnswerFromFile sdkClientReadAnswerFromFile = new SdkClientReadAnswerFromFile(filename, value, key);
        Mockito.doAnswer(sdkClientReadAnswerFromFile).when(mockSdk)
                .read(Mockito.anyString(), Mockito.any(List.class), Mockito.anyString(), Mockito.any(Class.class));

        GenericEntity entity = client.getSession(token, value);

        assertNotNull(entity);
        assertEquals(3, entity.size());
        assertEquals(entity.getString(key), value);

        // lookup of invalid session
        value = "3124";
        sdkClientReadAnswerFromFile = new SdkClientReadAnswerFromFile(filename, value, key);
        Mockito.doAnswer(sdkClientReadAnswerFromFile).when(mockSdk)
                .read(Mockito.anyString(), Mockito.any(List.class), Mockito.anyString(), Mockito.any(Class.class));
        entity = client.getSession(token, value);

        assertNull(entity);
    }

    @Test
    public void testGetSessions() throws MalformedURLException, URISyntaxException, IOException   {
        String token = "token";
        String filename = getFilename(MOCK_DATA_DIRECTORY + "common/" + MOCK_SESSIONS_FILE);

        // note that this test currently does not test for optional parameters because they are
        // never used by any call at this time

        SdkClientReadAnswerFromFile sdkClientReadAnswerFromFile = new SdkClientReadAnswerFromFile(filename);
        Mockito.doAnswer(sdkClientReadAnswerFromFile).when(mockSdk)
                .read(Mockito.anyString(), Mockito.any(List.class), Mockito.anyString(), Mockito.any(Class.class));
        List<GenericEntity> sessions = client.getSessions(token, null);

        assertNotNull(sessions);
        assertEquals(5, sessions.size());
    }

    public void testGetSessionsWithIdsAndParams() {
        // this test is not implemented because the underlying method is not called by anything at
        // this time
    }

    @Test
    public void testGetSessionsForYear() throws MalformedURLException, URISyntaxException, IOException   {
        String token = "token";
        String key = "schoolYear";
        String value = "2011";
        String filename = getFilename(MOCK_DATA_DIRECTORY + "common/" + MOCK_SESSIONS_FILE);

        // 2 sessions expected
        SdkClientReadAnswerFromFile sdkClientReadAnswerFromFile = new SdkClientReadAnswerFromFile(filename, value, key);
        Mockito.doAnswer(sdkClientReadAnswerFromFile).when(mockSdk)
                .read(Mockito.anyString(), Mockito.any(List.class), Mockito.anyString(), Mockito.any(Class.class));

        List<GenericEntity> sessions = client.getSessionsForYear(token, value);

        assertNotNull(sessions);
        assertEquals(2, sessions.size());
        for (GenericEntity ge : sessions) {
            assertEquals(ge.getString(key), value);
        }

        // no sessions expected
        value = "2005";
        sdkClientReadAnswerFromFile = new SdkClientReadAnswerFromFile(filename, value, key);
        Mockito.doAnswer(sdkClientReadAnswerFromFile).when(mockSdk)
                .read(Mockito.anyString(), Mockito.any(List.class), Mockito.anyString(), Mockito.any(Class.class));
        sessions = client.getSessionsForYear(token, value);

        assertNotNull(sessions);
        assertEquals(0, sessions.size());
    }

    @Test
    public void testGetSections() throws MalformedURLException, URISyntaxException, IOException   {
        String token = "token";
        String filename = getFilename(MOCK_DATA_DIRECTORY + "common/" + MOCK_SECTIONS_FILE);

        // note that this test currently does not test for optional parameters because they are
        // never used by any call at this time

        SdkClientReadAnswerFromFile sdkClientReadAnswerFromFile = new SdkClientReadAnswerFromFile(filename);
        Mockito.doAnswer(sdkClientReadAnswerFromFile).when(mockSdk)
                .read(Mockito.anyString(), Mockito.any(List.class), Mockito.anyString(), Mockito.any(Class.class));
        List<GenericEntity> sections = client.getSections(token, null);

        assertNotNull(sections);
        assertEquals(3, sections.size());
    }

    @Test
    public void testGetSectionsForStudent() throws MalformedURLException, URISyntaxException, IOException   {
        String token = "token";
        String key = "studentUIDs";
        String filename = getFilename(MOCK_DATA_DIRECTORY + "common/" + MOCK_SECTIONS_FILE);

        // note that this test currently does not test for optional parameters because they are
        // never used by any call at this time

        // testing with a student id present in two sections
        String value = "288598192";
        SdkClientReadAnswerFromFile sdkClientReadAnswerFromFile = new SdkClientReadAnswerFromFile(filename, value, key);
        Mockito.doAnswer(sdkClientReadAnswerFromFile).when(mockSdk)
                .read(Mockito.anyString(), Mockito.any(List.class), Mockito.anyString(), Mockito.any(Class.class));
        List<GenericEntity> sections = client.getSectionsForStudent(token, value, null);

        assertNotNull(sections);
        assertEquals(2, sections.size());

        // testing with a student id not present in any sections
        value = "288598193";
        sdkClientReadAnswerFromFile = new SdkClientReadAnswerFromFile(filename, value, key);
        Mockito.doAnswer(sdkClientReadAnswerFromFile).when(mockSdk)
                .read(Mockito.anyString(), Mockito.any(List.class), Mockito.anyString(), Mockito.any(Class.class));
        sections = client.getSectionsForStudent(token, value, null);

        assertNotNull(sections);
        assertEquals(0, sections.size());
    }

    @Test
    public void testGetSectionsForTeacher() throws MalformedURLException, URISyntaxException, IOException   {
        String token = "token";
        String key = "teacherId";
        String filename = getFilename(MOCK_DATA_DIRECTORY + "common/" + MOCK_SECTIONS_FILE);

        // note that this test currently does not test for optional parameters because they are
        // never used by any call at this time

        // testing with a teacher id present in a single section
        String value = "12399";
        SdkClientReadAnswerFromFile sdkClientReadAnswerFromFile = new SdkClientReadAnswerFromFile(filename, value, key);
        Mockito.doAnswer(sdkClientReadAnswerFromFile).when(mockSdk)
                .read(Mockito.anyString(), Mockito.any(List.class), Mockito.anyString(), Mockito.any(Class.class));
        List<GenericEntity> sections = client.getSectionsForTeacher(token, value, null);

        assertNotNull(sections);
        assertEquals(1, sections.size());

        // testing with a teacher id not present in any sections
        value = "28859";
        sdkClientReadAnswerFromFile = new SdkClientReadAnswerFromFile(filename, value, key);
        Mockito.doAnswer(sdkClientReadAnswerFromFile).when(mockSdk)
                .read(Mockito.anyString(), Mockito.any(List.class), Mockito.anyString(), Mockito.any(Class.class));
        sections = client.getSectionsForTeacher(token, value, null);

        assertNotNull(sections);
        assertEquals(0, sections.size());
    }

    public void testGetSectionsNonEducator() throws MalformedURLException, URISyntaxException, IOException  {
        // this test is not implemented because the underlying method does not appear to be used
        // even though it is potentially called in getSchools
    }

    public void testGetSectionsWithIdsAndParams() {
        // this test is not implemented because the underlying method is not called by anything at
        // this time
    }

    public void testGetSectionHomeForStudent() throws MalformedURLException, URISyntaxException, IOException   {
        // this test is not implemented because the mock json data does not conform to the proper
        // format
        // once it is updated, this test can be implemented
        /*
         * String token = "token";
         * String key = "studentUIDs";
         * String mapKey = "studentSectionAssociation";
         * String filename = getFilename(MOCK_DATA_DIRECTORY + "common/" + MOCK_SECTIONS_FILE);
         * String value = "288598192";
         *
         * SdkClientReadAnswerFromFile sdkClientReadAnswerFromFile = new
         * SdkClientReadAnswerFromFile(filename, value, key);
         * Mockito.doAnswer(sdkClientReadAnswerFromFile).when(mockSdk)
         * .read(Mockito.anyString(), Mockito.any(List.class), Mockito.anyString(),
         * Mockito.any(Class.class));
         * GenericEntity section = client.getSectionHomeForStudent(token, value);
         *
         * assertNotNull(section);
         * assertEquals("True", section.getString("homeroomIndicator"));
         */
    }

    @Test
    public void testGetSection() throws MalformedURLException, URISyntaxException, IOException   {
        String token = "token";
        String key = "sectionId";

        // lookup of valid section
        String value = "1";
        String filename = getFilename(MOCK_DATA_DIRECTORY + "common/" + MOCK_SECTIONS_FILE);
        SdkClientReadAnswerFromFile sdkClientReadAnswerFromFile = new SdkClientReadAnswerFromFile(filename, value, key);
        Mockito.doAnswer(sdkClientReadAnswerFromFile).when(mockSdk)
                .read(Mockito.anyString(), Mockito.any(List.class), Mockito.anyString(), Mockito.any(Class.class));

        GenericEntity entity = client.getSection(token, value);

        assertNotNull(entity);
        assertEquals(5, entity.size());
        assertEquals(entity.getString(key), value);

        // lookup of invalid section
        value = "3124";
        sdkClientReadAnswerFromFile = new SdkClientReadAnswerFromFile(filename, value, key);
        Mockito.doAnswer(sdkClientReadAnswerFromFile).when(mockSdk)
                .read(Mockito.anyString(), Mockito.any(List.class), Mockito.anyString(), Mockito.any(Class.class));
        entity = client.getSection(token, value);

        assertNull(entity);
    }

    public String getFilename(String filename) {
        URL url = classLoader.getResource(filename);
        return url == null ? null : url.getFile();
    }

    private static class SdkClientReadAnswer implements Answer {

        private String json;
        private ConfigMap configMap;
        private List entityList;

        public SdkClientReadAnswer(String json) {
            this.json = json;
            Gson gson = new GsonBuilder().create();
            this.configMap = gson.fromJson(getJson(), ConfigMap.class);
        }

        @Override
        public Object answer(InvocationOnMock invocation) throws Throwable {
            entityList = (List) invocation.getArguments()[1];

            entityList.add(getConfigMap());

            return null;
        }

        public String getJson() {
            return json;
        }

        public ConfigMap getConfigMap() {
            return configMap;
        }

        public List getEntityList() {
            return entityList;
        }
    }

    private static class SdkClientCreateAnswer implements Answer {

        private ConfigMap configMap;

        @Override
        public Object answer(InvocationOnMock invocation) throws Throwable {
            Entity configMapEntity = (Entity) invocation.getArguments()[2];
            Map<String, Config> config = (Map<String, Config>) configMapEntity.getData().get("config");
            this.configMap = new ConfigMap();
            this.configMap.setConfig(config);
            return null;
        }

        public ConfigMap getConfigMap() {
            return configMap;
        }

    }

    private static class SdkClientReadAnswerFromFile implements Answer {

        private String json;
        private List<GenericEntity> genericEntities;
        private List entityList;
        private String url;
        private String id;
        private String key;
        private String[] searchStr;
        private List<String> idList;

        /** get the json from a specified filename */
        public SdkClientReadAnswerFromFile(String filename) {
            genericEntities = fromFile(filename);
        }

        /** get the json from a specified filename, with the selected argument */
        public SdkClientReadAnswerFromFile(String filename, String entityid, String entityKey) {
            this(filename);
            this.id = entityid;
            this.key = entityKey;
        }

        /** get the json from a specified filename, with the selected search parameters */
        public SdkClientReadAnswerFromFile(String filename, String[] searchStr) {
            this(filename);
            this.searchStr = searchStr;
        }

        /** get the json from a specified filename, with the List of arguments */
        public SdkClientReadAnswerFromFile(String filename, List<String> entityIds, String entityKey) {
            this(filename);
            this.idList = entityIds;
            this.key = entityKey;
        }

        @Override
        public Object answer(InvocationOnMock invocation) throws Throwable {
            entityList = (List) invocation.getArguments()[1];

            if (key != null && id != null) {
                url = (String) invocation.getArguments()[2];
                List<GenericEntity> genericEntities = getGenericEntities();
                for (GenericEntity ge : genericEntities) {
                    Object entity = ge.get(key);
                    // entity can be a single value or an ArrayList of values
                    if (entity instanceof ArrayList) {
                        if (((ArrayList) entity).contains(id)) {
                            entityList.add(ge);
                        }
                    } else if (entity.equals(id)) {
                        entityList.add(ge);
                    }
                }
            } else if (searchStr != null) {
                url = (String) invocation.getArguments()[2];
                List<GenericEntity> genericEntities = getGenericEntities();
                for (GenericEntity ge : genericEntities) {
                    if ((searchStr[0].equals("") || ge.getString("firstName").equals(searchStr[0]))
                            && (searchStr[1].equals("") || ge.getString("lastName").equals(searchStr[1]))) {
                        entityList.add(ge);
                    }
                }
            } else if (idList != null && key != null) {
                url = (String) invocation.getArguments()[2];
                List<GenericEntity> genericEntities = getGenericEntities();
                for (GenericEntity ge : genericEntities) {
                    if (idList.contains(ge.getString(key))) {
                        entityList.add(ge);
                    }
                }
            } else {
                entityList.addAll(getGenericEntities());
            }
            return null;
        }

        public String getJson() {
            return json;
        }

        public List getEntityList() {
            return entityList;
        }

        public List<GenericEntity> getGenericEntities() {
            return genericEntities;
        }

        /**
         * Retrieves an entity list from the specified file
         * and instantiates from its JSON representation
         *
         * @param filePath
         *            - the file path to persist the view component XML string representation
         * @return entityList
         *         - the generic entity list
         */
        private List<GenericEntity> fromFile(String filePath) {
            List<GenericEntity> entityList = new ArrayList<GenericEntity>();
            BufferedReader reader = null;

            try {
                // Read JSON file
                reader = new BufferedReader(new FileReader(filePath));
                StringBuffer jsonBuffer = new StringBuffer();
                String line;
                while ((line = reader.readLine()) != null) {
                    jsonBuffer.append(line);
                }
                json = jsonBuffer.toString();

                // Parse JSON
                Gson gson = new Gson();
                List<Map> maps = gson.fromJson(getJson(), new ArrayList<Map>().getClass());
                for (Map<String, Object> map : maps) {
                    entityList.add(new GenericEntity(map));
                }
            } catch (IOException e) {
                log.error(e.getMessage());
            } catch (NullPointerException e) {
                log.error(e.getMessage());
            } catch (Exception e) {
                log.error(e.getMessage());
            } finally {
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (Exception e) {
                    log.error(e.getMessage());
                }
            }
            return entityList;
        }
    }

}

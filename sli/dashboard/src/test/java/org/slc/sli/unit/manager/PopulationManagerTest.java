package org.slc.sli.unit.manager;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import org.slc.sli.entity.GenericEntity;
import org.slc.sli.manager.EntityManager;
import org.slc.sli.manager.PopulationManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * 
 * Tests for the population manager.
 *
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(EntityManager.class)
public class PopulationManagerTest {
    
    private PopulationManager manager;
    private EntityManager mockEntity;

    @Before
    public void setUp() throws Exception {
        manager = new PopulationManager();
        mockEntity = mock(EntityManager.class);
        manager.setEntityManager(mockEntity);

    }

    @After
    public void tearDown() throws Exception {
        manager = null;
        mockEntity = null;
    }

    /*
    * Test that the assessmentFamilyMap is populated from values in entityManager.
    */
    /*
    @Test
    public void testAssessmentFamilyMapFromInit() throws Exception {
        
        EntityManager mockedEntityManager = PowerMockito.spy(new EntityManager());
        
        HashMap<String, String> assessment = new HashMap<String, String>();
        assessment.put("name", "Dibels");
        LinkedList<Map> assessments = new LinkedList<Map>();
        assessments.add(assessment);
        HashMap<String, Object> genericEntityMap = new HashMap<String, Object>();
        genericEntityMap.put("children", assessments);
        GenericEntity genericEntity = new GenericEntity(genericEntityMap);
        LinkedList<GenericEntity> assessmentMetaDataList = new LinkedList<GenericEntity>();
        assessmentMetaDataList.add(genericEntity);
        
        PowerMockito.doReturn(assessmentMetaDataList).when(mockedEntityManager, "getAssessmentMetaData");
        PopulationManager popManager = new PopulationManager();
        assertTrue(popManager.getAssessmentFamilyMap().size() == 0);
        popManager.setEntityManager(mockedEntityManager);
        popManager.init();
        assertTrue(popManager.getAssessmentFamilyMap().containsKey("Dibels"));
    }
    */
    
    
    /*
     *Test that student summaries are being populated from programs, assessments, and students 
     */
    @Test
    public void testGetStudentSummaries() throws Exception {
       /* String studentId = "student_id";
        String token = "token";
        String assessmentName = "Dibels";
        String assessmentFamily = "Reading";
        List<String> studentIds = new LinkedList<String>(); 
        studentIds.add(studentId);
        List<String> programs = new LinkedList<String>();
        programs.add("ELL");
       
        EntityManager mockedEntityManager = PowerMockito.spy(new EntityManager());
       
        // Setup studentPrograms
        GenericEntity studentProgram = new GenericEntity();
        studentProgram.put("studentId", studentId);
        studentProgram.put("programs", programs);
        List<GenericEntity> studentPrograms = new LinkedList<GenericEntity>();
        studentPrograms.add(studentProgram);
       

        PowerMockito.doReturn(studentPrograms).when(mockedEntityManager, "getPrograms", token, studentIds);
       
        // Setup studentAssessments
        GenericEntity studentAssessment = new GenericEntity();
        studentAssessment.put("assessmentName", assessmentName);
        studentAssessment.put("studentId", studentId);
        List<GenericEntity> studentAssessments = new LinkedList<GenericEntity>();
        studentAssessments.add(studentAssessment);
       
        PowerMockito.doReturn(studentAssessments).when(mockedEntityManager, "getAssessments", token, studentIds);
        
        
        //Setup studentSummaries
        GenericEntity studentSummary = new GenericEntity();
        studentSummary.put("id", studentId);
        List<GenericEntity> studentSummaries = new LinkedList<GenericEntity>();
        studentSummaries.add(studentSummary);
        
        PowerMockito.doReturn(studentSummaries).when(mockedEntityManager, "getStudents", token, studentIds);
        
        
        PopulationManager popMan = new PopulationManager();
        popMan.setEntityManager(mockedEntityManager);
        
        List<GenericEntity> studentSumamries = popMan.getStudentSummaries(token, studentIds); 
        assertTrue(studentSummaries.size() == 1);
        
        GenericEntity result = studentSummaries.get(0);
        assertTrue(result.get("id").equals(studentId));
        assertTrue(programs.equals(result.get("programs")));
        
        GenericEntity assessments = (GenericEntity) result.get("assessments");
        assertTrue(assessments.get("assessmentName").equals(assessmentName));
        assertTrue(assessments.get("assessmentFamily") == null);/*
    }
    
    
    //TODO: Unskip test after debugging
    @Test
    public void testGetStudentInfo() {
/*
        String[] studentIdArray = {"453827070", "943715230"};
        List<String> studentIds = Arrays.asList(studentIdArray);

        MockAPIClient mockClient = new MockAPIClient();
        EntityManager entityManager = new EntityManager();
        entityManager.setApiClient(mockClient);
        
        ConfigManager configManager = new ConfigManager();
        configManager.setApiClient(mockClient);
        configManager.setEntityManager(entityManager);
        ViewConfig config = configManager.getConfig("lkim", "IL_3-8_ELA");

        StudentManager studentManager = new StudentManager();
        studentManager.setApiClient(mockClient);
        studentManager.setEntityManager(entityManager);
        List<GenericEntity> studentInfo = studentManager.getStudentInfo("lkim", studentIds, config, "NONE");
        assertEquals(2, studentInfo.size());
        */
    }
    
    @Test
    public void testGetAssessments() throws Exception {
        /*
        String[] studentIdArray = {"453827070", "943715230"};
        List<String> studentIds = Arrays.asList(studentIdArray);
        MockAPIClient mockClient = PowerMockito.spy(new MockAPIClient());
        EntityManager entityManager = new EntityManager();
        entityManager.setApiClient(mockClient);
        ConfigManager configManager = new ConfigManager();
        configManager.setApiClient(mockClient);
        configManager.setEntityManager(entityManager);
        ViewConfig config = configManager.getConfig("lkim", "IL_3-8_ELA"); // this view has ISAT Reading and ISAT Writing

        PopulationManager aManager = new PopulationManager();
        when(mockClient.getFilename("mock_data/lkim/school.json")).thenReturn("src/test/resources/mock_data/lkim/school.json");
        when(mockClient.getFilename("mock_data/lkim/custom_view_config.json")).thenReturn("src/test/resources/mock_data/lkim/custom_view_config.json");
        aManager.setEntityManager(entityManager);
        List<GenericEntity> assmts = aManager.getAssessments("lkim", studentIds, config);
        
        assertEquals(111, assmts.size());
        */
    }

/*
    @Test
    public void testGetAssessmentMetaData() throws Exception {
        PopulationManager aManager = new PopulationManager();
        MockAPIClient mockClient = PowerMockito.spy(new MockAPIClient());
        when(mockClient.getFilename("mock_data/assessment_meta_data.json")).thenReturn("src/test/resources/mock_data/assessment_meta_data.json");
        List<AssessmentMetaData> metaData = aManager.getAssessmentMetaData("lkim");
        assertEquals(8, metaData.size()); // mock data has now 8 families: ISAT Reading, ISAT Writing, DIBELS Next, TRC, AP English, ACT, SAT, and PSAT
    }
  */

    @Test
    public void testGetAttendance() throws Exception {
        List<String> studentIds = new ArrayList<String>();
        studentIds.add("0");
        studentIds.add("1");
        List<GenericEntity> attendance = new ArrayList<GenericEntity>();
        attendance.add(new GenericEntity());
        attendance.add(new GenericEntity());
        when(mockEntity.getAttendance(null, "0")).thenReturn(attendance);
        when(mockEntity.getAttendance(null, "1")).thenReturn(attendance);

        Map<String, Object> studentAttendance = manager.createStudentAttendanceMap(null, studentIds);
        assertNotNull(studentAttendance);
    }

    @Test
    public void testGetAttendanceWithBadStudent() throws Exception {
        List<String> studentIds = new ArrayList<String>();
        studentIds.add("0");
        studentIds.add("1");
        List<GenericEntity> attendance = new ArrayList<GenericEntity>();
        attendance.add(new GenericEntity());
        attendance.add(new GenericEntity());
        when(mockEntity.getAttendance(null, "0")).thenReturn(new ArrayList<GenericEntity>());
        when(mockEntity.getAttendance(null, "1")).thenReturn(attendance);


        Map<String, Object> studentAttendance = manager.createStudentAttendanceMap(null, studentIds);
        assertNotNull(studentAttendance);
    }
    
    @Test
    public void testGetStudentHistoricalAssessments() throws Exception {
        String token = "token", subjectArea = "Math";
        final String STUDENT_ID = "123456";
        final String COURSE_ID = "56789";
        final String SESSION_ID = "9999";
        
        //create the course
        GenericEntity courseEntity = new GenericEntity();
        courseEntity.put("courseId", COURSE_ID);
        courseEntity.put("courseTitle", "Math 1");
        //create the accociation
        GenericEntity assocEntity = new GenericEntity();
        assocEntity.put("finalLettergrade", "A");
        assocEntity.put("studentId", STUDENT_ID);
        //create the section
        GenericEntity sectionEntity = new GenericEntity();
        sectionEntity.put("sessionId", SESSION_ID);
        //create the session
        GenericEntity sessionEntity = new GenericEntity();
        sessionEntity.put("schoolYear", "2009-2010");
        
        //add the courses
        List<GenericEntity> courses = new ArrayList<GenericEntity>();
        courses.add(courseEntity);
        //add the associations
        List<GenericEntity> studentCourseAssocs = new ArrayList<GenericEntity>();
        studentCourseAssocs.add(assocEntity);
        //add the sections
        List<GenericEntity> sections = new ArrayList<GenericEntity>();
        sections.add(sectionEntity);
        //add the students
        List<String> students = new ArrayList<String>();
        students.add(STUDENT_ID);
        
        Map<String, String> params = new HashMap<String, String>();
        params.put("subjectArea", subjectArea);
        params.put("includeFields", "courseId,courseTitle");
        
        Map<String, String> params1 = new HashMap<String, String>();
        params1.put("courseId", COURSE_ID);
        params1.put("includeFields", "finalLetterGradeEarned,studentId");
        
        Map<String, String> params2 = new HashMap<String, String>();
        params2.put("courseId", COURSE_ID);
        params2.put("includeFields", "sessionId");
        
        Map<String, String> params3 = new HashMap<String, String>();
        params3.put("includeFields", "schoolYear");
        
        when(mockEntity.getCourses(token, STUDENT_ID, params)).thenReturn(courses);
        when(mockEntity.getStudentTranscriptAssociations(token, STUDENT_ID, params1)).thenReturn(studentCourseAssocs);
        when(mockEntity.getSections(token, STUDENT_ID, params2)).thenReturn(sections);
        when(mockEntity.getEntity(token, "sessions",SESSION_ID, params3)).thenReturn(sessionEntity);
        
        Map<String, List<GenericEntity>> results = manager.getStudentHistoricalAssessments(token, students, subjectArea);
        
        assertEquals("Should have one result", 1, results.size());
        assertTrue("", results.keySet().contains(STUDENT_ID));
        assertEquals("Letter grade should be A", "A", results.get(STUDENT_ID).get(0).get("finalLettergrade"));
        assertEquals("Student Id should be 123456", STUDENT_ID, results.get(STUDENT_ID).get(0).get("studentId"));
        assertEquals("Course title should match", "Math 1", results.get(STUDENT_ID).get(0).get("courseTitle"));
        assertEquals("subject area should match", "Math", results.get(STUDENT_ID).get(0).get("subjectArea"));
        assertEquals("school year should match", "2009-2010", results.get(STUDENT_ID).get(0).get("schoolYear"));
    }
}

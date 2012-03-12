package org.slc.sli.unit.manager;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import org.slc.sli.entity.GenericEntity;
import org.slc.sli.manager.EntityManager;
import org.slc.sli.manager.PopulationManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertNotNull;
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
        List<String> studentIds = getStudentIds();
        List<GenericEntity> attendance = new ArrayList<GenericEntity>();
        attendance.add(new GenericEntity());
        attendance.add(new GenericEntity());
        when(mockEntity.getAttendance(null, "0", null, null)).thenReturn(attendance);
        when(mockEntity.getAttendance(null, "1", null, null)).thenReturn(attendance);
        when(mockEntity.getSession(null, "")).thenReturn(new GenericEntity());

        Map<String, Object> studentAttendance = manager.createStudentAttendanceMap(null, studentIds, "");
        assertNotNull(studentAttendance);
    }

    @Test
    public void testGetAttendanceWithBadStudent() throws Exception {
        List<String> studentIds = getStudentIds();
        List<GenericEntity> attendance = new ArrayList<GenericEntity>();
        attendance.add(new GenericEntity());
        attendance.add(new GenericEntity());
        when(mockEntity.getAttendance(null, "0", null, null)).thenReturn(new ArrayList<GenericEntity>());
        when(mockEntity.getAttendance(null, "1", null, null)).thenReturn(attendance);
        when(mockEntity.getSession(null, "")).thenReturn(new GenericEntity());


        Map<String, Object> studentAttendance = manager.createStudentAttendanceMap(null, studentIds, "");
        assertNotNull(studentAttendance);
    }

    private List<String> getStudentIds() {
        List<String> studentIds = new ArrayList<String>();
        studentIds.add("0");
        studentIds.add("1");
        return studentIds;
    }

    @Test
    public void testGetAttendancesWithCourse() throws Exception {
        GenericEntity session = new GenericEntity();
        session.put("startDate", "2012-03-07");
        session.put("endDate", "2013-03-07");
        when(mockEntity.getSession(null, "")).thenReturn(session);
        when(mockEntity.getAttendance(null, "0", "2012-03-07", "2013-03-07")).thenReturn(new ArrayList<GenericEntity>());
        when(mockEntity.getAttendance(null, "1", "2012-03-07", "2013-03-07")).thenReturn(new ArrayList<GenericEntity>());
        Assert.assertNotNull(manager.createStudentAttendanceMap(null, getStudentIds(), ""));
    }

    @Test
    public void testGetAttendancesWithoutCourse() throws Exception {
        when(mockEntity.getSession(null, "")).thenReturn(new GenericEntity());
        when(mockEntity.getAttendance(null, "0", null, null)).thenReturn(new ArrayList<GenericEntity>());
        when(mockEntity.getAttendance(null, "1", null, null)).thenReturn(new ArrayList<GenericEntity>());
        Assert.assertNotNull(manager.createStudentAttendanceMap(null, getStudentIds(), ""));
    }


    @Test
    public void testGetStudentHistoricalAssessments() throws Exception {
        String token = "token", subjectArea = "Math";
        String studentId = "123456";
        String courseId = "56789";
        
        //create the course
        GenericEntity courseEntity = new GenericEntity();
        courseEntity.put("id", courseId);
        courseEntity.put("courseTitle", "Math 1");
        //create the accociation
        GenericEntity assocEntity = new GenericEntity();
        assocEntity.put("finalLettergrade", "A");
        assocEntity.put("studentId", studentId);
        
        //add the courses
        List<GenericEntity> courses = new ArrayList<GenericEntity>();
        courses.add(courseEntity);
        //add the associations
        List<GenericEntity> studentCourseAssocs = new ArrayList<GenericEntity>();
        studentCourseAssocs.add(assocEntity);
        //add the students
        List<String> students = new ArrayList<String>();
        students.add(studentId);
        
        Map<String, String> params = new HashMap<String, String>();
        params.put("subjectArea", subjectArea);
        params.put("includeFields", "courseTitle");
        
        Map<String, String> params1 = new HashMap<String, String>();
        params1.put("courseId", courseId);
        params1.put("includeFields", "finalLetterGradeEarned");
        
        when(mockEntity.getCourses(token, studentId, params)).thenReturn(courses);
        when(mockEntity.getStudentTranscriptAssociations(token, studentId, params1)).thenReturn(studentCourseAssocs);
        
        Map<String, List<GenericEntity>> results = manager.getStudentHistoricalAssessments(token, students, subjectArea);
        
        assertEquals("Should have one result", 1, results.size());
        assertTrue("Should have a key with the student Id", results.keySet().contains(studentId));
        assertEquals("Letter grade should be A", "A", results.get(studentId).get(0).get("finalLettergrade"));
        assertEquals("Student Id should be 123456", studentId, results.get(studentId).get(0).get("studentId"));
        assertEquals("Course title should match", "Math 1", results.get(studentId).get(0).get("courseTitle"));
        assertEquals("subject area should match", "Math", results.get(studentId).get(0).get("subjectArea"));
    }
    
    @Test
    public void testApplyShoolYear() {
        String token = "token";
        String sessionId1 = "9999", sessionId2 = "9998", sessionId3 = "9997";
        
        //create the sections
        GenericEntity sectionEntity1 = new GenericEntity();
        sectionEntity1.put("sessionId", sessionId1);
        sectionEntity1.put("courseId", COURSEID1);
        GenericEntity sectionEntity2 = new GenericEntity();
        sectionEntity2.put("sessionId", sessionId2);
        sectionEntity2.put("courseId", COURSEID2);
        GenericEntity sectionEntity3 = new GenericEntity();
        sectionEntity3.put("sessionId", sessionId3);
        sectionEntity3.put("courseId", COURSEID3);
        
        //create the sessions
        GenericEntity sessionEntity1 = new GenericEntity();
        sessionEntity1.put("schoolYear", YEAR_1998_1999);
        GenericEntity sessionEntity2 = new GenericEntity();
        sessionEntity2.put("schoolYear", YEAR_2009_2010);
        GenericEntity sessionEntity3 = new GenericEntity();
        sessionEntity3.put("schoolYear", YEAR_2006_2007);
    }
        
}

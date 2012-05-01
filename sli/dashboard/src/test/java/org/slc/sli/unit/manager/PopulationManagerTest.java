package org.slc.sli.unit.manager;

import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slc.sli.entity.Config;
import org.slc.sli.entity.GenericEntity;
import org.slc.sli.manager.EntityManager;
import org.slc.sli.manager.PopulationManager;
import org.slc.sli.manager.impl.PopulationManagerImpl;
import org.slc.sli.util.Constants;

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
        manager = new PopulationManagerImpl();
        mockEntity = mock(EntityManager.class);
        manager.setEntityManager(mockEntity);

    }

    @After
    public void tearDown() throws Exception {
        manager = null;
        mockEntity = null;
    }

    /*
     *Test that student summaries are being populated from programs, assessments, and students
     */
    @Test
    public void testGetStudentSummaries() throws Exception {

        String studentId = "0";
        String sectionId = "sectionId";
        String token = "token";
        String assessmentName = "Dibels";
        List<String> studentIds = new ArrayList<String>();
        studentIds.add(studentId);
        List<String> programs = new ArrayList<String>();
        programs.add("ELL");

        EntityManager mockedEntityManager = PowerMockito.spy(new EntityManager());

        // Setup studentPrograms
        GenericEntity studentProgram = new GenericEntity();
        studentProgram.put("studentId", studentId);
        studentProgram.put("programs", programs);
        List<GenericEntity> studentPrograms = new ArrayList<GenericEntity>();
        studentPrograms.add(studentProgram);


        // Setup studentAssessments
        GenericEntity studentAssessment = new GenericEntity();
        studentAssessment.put("assessmentName", assessmentName);
        studentAssessment.put("studentId", studentId);
        List<GenericEntity> studentAssessments = new ArrayList<GenericEntity>();
        studentAssessments.add(studentAssessment);

        PowerMockito.doReturn(studentAssessments).when(mockedEntityManager, "getStudentAssessments", token, studentId);


        //Setup studentSummaries
        GenericEntity studentSummary = new GenericEntity();
        studentSummary.put("id", studentId);
        List<GenericEntity> students = new ArrayList<GenericEntity>();
        students.add(studentSummary);

        PowerMockito.doReturn(students).when(mockedEntityManager, "getStudents", token, sectionId, studentIds);

        // setup attendance
        PowerMockito.doReturn(new ArrayList<GenericEntity>()).when(mockedEntityManager, "getAttendance", token, studentId, null, null);

        // setup session
        //GenericEntity baseSession = generateSession("2010-2011", "2010-12-31", "2011-01-31");
        PowerMockito.doReturn(new GenericEntity()).when(mockedEntityManager, "getSession", token, null);
        PowerMockito.doReturn(new ArrayList<GenericEntity>()).when(mockedEntityManager, "getSessionsByYear", token, null);

        // run it
        PopulationManager popMan = new PopulationManagerImpl();
        popMan.setEntityManager(mockedEntityManager);

        List<GenericEntity> studentSummaries = popMan.getStudentSummaries(token, studentIds, null, null, sectionId);
        assertTrue(studentSummaries.size() == 1);

        GenericEntity result = studentSummaries.get(0);
        assertTrue(result.get("id").equals(studentId));
    }

    private List<String> getStudentIds() {
        List<String> studentIds = new ArrayList<String>();
        studentIds.add("0");
        studentIds.add("1");
        return studentIds;
    }

    @Test
    public void testGetSessionDates() throws Exception {
        String sessionId = "1";
        GenericEntity baseSession = generateSession("2010-2011", "2010-12-31", "2011-01-31");
        when(mockEntity.getSession(null, sessionId)).thenReturn(baseSession);
        when(mockEntity.getSessionsByYear(null, "2010-2011")).thenReturn(Arrays.asList(baseSession));

        // See that we have the same beginning and end date
        List<String> dates = manager.getSessionDates(null, sessionId);
        assertTrue(dates.size() == 2);
        assertTrue(dates.get(0).compareTo("2010-12-31") == 0);
        assertTrue(dates.get(1).compareTo("2011-01-31") == 0);

        //See that we compare dates correctly
        GenericEntity lateSession = generateSession("2010-2011", "2011-02-1", "2011-03-14");
        when(mockEntity.getSessionsByYear(null, "2010-2011")).thenReturn(Arrays.asList(baseSession, lateSession));
        dates = manager.getSessionDates(null, sessionId);
        assertTrue(dates.size() == 2);
        assertTrue(dates.get(0).compareTo("2010-12-31") == 0);
        assertTrue(dates.get(1).compareTo("2011-03-14") == 0);

        //Try starting with the middle of a 3 semester setup.
        GenericEntity earlySession = generateSession("2010-2011", "2010-01-01", "2010-12-30");
        when(mockEntity.getSessionsByYear(null, "2010-2011")).thenReturn(Arrays.asList(baseSession, lateSession, earlySession));
        dates = manager.getSessionDates(null, sessionId);
        assertTrue(dates.size() == 2);
        assertTrue(dates.get(0).compareTo("2010-01-01") == 0);
        assertTrue(dates.get(1).compareTo("2011-03-14") == 0);

    }

    private GenericEntity generateSession(String schoolYear, String beginDate, String endDate) {
        GenericEntity startingSession = new GenericEntity();
        startingSession.put("schoolYear", schoolYear);
        startingSession.put("beginDate", beginDate);
        startingSession.put("endDate", endDate);
        return startingSession;
    }

    @Test
    public void testGetAttendanceForOneStudent() throws Exception {

        // start with a token, a student id, and a data config
        String studentId = "0";
        Config.Data config = new Config.Data();

        // set up mock attendance data
        GenericEntity attend1 = new GenericEntity();
        attend1.put(Constants.ATTR_ATTENDANCE_DATE, "2011-09-01");
        attend1.put(Constants.ATTR_ATTENDANCE_EVENT_CATEGORY, "Tardy");
        GenericEntity attend2 = new GenericEntity();
        attend2.put(Constants.ATTR_ATTENDANCE_DATE, "2011-10-01");
        attend2.put(Constants.ATTR_ATTENDANCE_EVENT_CATEGORY, "Excused Absence");

        List<GenericEntity> attendList = new ArrayList<GenericEntity>();
        attendList.add(attend1);
        attendList.add(attend2);

        when(mockEntity.getAttendance(null, "0", null, null)).thenReturn(attendList);

        // make the call
        GenericEntity a = manager.getAttendance(null, studentId, config);

        Assert.assertNotNull(a);
        Assert.assertNotNull(a.getList("attendance"));
        Assert.assertEquals(1, ((Integer) (((Map) (a.getList("attendance").get(0))).get("totalCount"))).intValue());

    }

    @Test
    public void testApplyAssessmentFilters() throws Exception {

        // set up studentSummaries
        List<GenericEntity> studentSummaries = createSomeStudentSummaries();

        // set up config
        Map<String, Object> params = new HashMap<String, Object>();
        Map<String, String> assmtFilter = new HashMap<String, String>();
        assmtFilter.put("ISAT Reading", "MOST_RECENT_RESULT");
        params.put(Constants.CONFIG_ASSESSMENT_FILTER, assmtFilter);
        Config.Data config = new Config.Data("entity", "cacheKey", false, params);

        // make the call
        PopulationManagerImpl pm = new PopulationManagerImpl();
        pm.applyAssessmentFilters(studentSummaries, config);

        // check that two of the three assmts got filtered out
        GenericEntity student = studentSummaries.get(0);
        Map filteredAssmts = (Map) student.get("assessments");
        Map filteredAssmt = (Map) filteredAssmts.get("ISAT Reading");
        Assert.assertEquals(1, filteredAssmts.size());
        Assert.assertEquals("2011-05-01", filteredAssmt.get("administrationDate"));
    }

    @Test
    public void testEnhanceListOfStudents() {

        // set up studentSummaries
        List<GenericEntity> studentSummaries = createSomeStudentSummaries();

        // set up config
        Map<String, Object> params = new HashMap<String, Object>();
        Map<String, String> assmtFilter = new HashMap<String, String>();
        assmtFilter.put("ISAT Reading", "MOST_RECENT_RESULT");
        params.put(Constants.CONFIG_ASSESSMENT_FILTER, assmtFilter);
        Config.Data config = new Config.Data("entity", "cacheKey", false, params);

        // make the call
        PopulationManagerImpl pm = new PopulationManagerImpl();
        pm.applyAssessmentFilters(studentSummaries, config);
        //TODO - Pass "real" data, update createSomeStudentSummaries to use the same section ID that you pass here.
        pm.enhanceListOfStudents(studentSummaries, "");

        // check for full name
        GenericEntity student = studentSummaries.get(0);
        Map name = (Map) student.get("name");
        Assert.assertEquals("John Doe", name.get("fullName"));

        // check for modified score attributes
        Map enhancedAssmts = (Map) student.get("assessments");
        Map enhancedAssmt = (Map) enhancedAssmts.get("ISAT Reading");
        Assert.assertEquals("50", enhancedAssmt.get("Scale score"));

        // check for attendance tallies
        Map attendances = (Map) student.get(Constants.ATTR_STUDENT_ATTENDANCES);
        Assert.assertEquals(1, attendances.get(Constants.ATTR_ABSENCE_COUNT));
        Assert.assertEquals(1, attendances.get(Constants.ATTR_TARDY_COUNT));
    }

    private List<GenericEntity> createSomeStudentSummaries() {

        List<GenericEntity> studentSummaries = new ArrayList<GenericEntity>();
        GenericEntity student = new GenericEntity();
        Map name = new HashMap();
        name.put("firstName", "John");
        name.put("lastSurname", "Doe");
        student.put("name", name);
        student.put("id", "dummyId");
        studentSummaries.add(student);

        // create assmt data
        List<Map> assmtResults = new ArrayList<Map>();
        Map assmtResult1 = new HashMap();
        Map assmts1 = new HashMap();
        assmts1.put("assessmentFamilyHierarchyName", "ISAT.ISAT Reading for Grades 3-8.ISAT Reading for Grade 8");
        assmtResult1.put("assessments", assmts1);
        assmtResult1.put("administrationDate", "2011-05-01");
        List<Map> scores = new ArrayList<Map>();
        Map scaleScore = new HashMap();
        scaleScore.put("assessmentReportingMethod", "Scale score");
        scaleScore.put("result", "50");
        scores.add(scaleScore);
        assmtResult1.put("scoreResults", scores);
        assmtResults.add(assmtResult1);

        Map assmtResult2 = new HashMap();
        Map assmts2 = new HashMap();
        assmts2.put("assessmentFamilyHierarchyName", "ISAT.ISAT Reading for Grades 3-8.ISAT Reading for Grade 8");
        assmtResult2.put("assessments", assmts2);
        assmtResult2.put("administrationDate", "2011-03-01");
        assmtResults.add(assmtResult2);

        Map assmtResult3 = new HashMap();
        Map assmts3 = new HashMap();
        assmts3.put("assessmentFamilyHierarchyName", "Dummy Assessment Family");
        assmtResult3.put("assessments", assmts3);
        assmtResult3.put("administrationDate", "2011-02-01");
        assmtResults.add(assmtResult3);

        student.put(Constants.ATTR_STUDENT_ASSESSMENTS, assmtResults);

        // create attendance data
        Map<String, Object> attendanceBody = new HashMap<String, Object>();
        List<Map<String, Object>> attendances = new ArrayList<Map<String, Object>>();
        attendanceBody.put(Constants.ATTR_STUDENT_ATTENDANCES, attendances);
        student.put(Constants.ATTR_STUDENT_ATTENDANCES, attendanceBody);

        Map<String, Object> attendance1 = new HashMap<String, Object>();
        attendance1.put(Constants.ATTR_ATTENDANCE_EVENT_CATEGORY, "Unexcused Absence");
        attendances.add(attendance1);

        Map<String, Object> attendance2 = new HashMap<String, Object>();
        attendance2.put(Constants.ATTR_ATTENDANCE_EVENT_CATEGORY, "Tardy");
        attendances.add(attendance2);
        
        
        //StudentTranscripts
        List<Map<String, Object>> studentTranscriptAssociations = new ArrayList<Map<String, Object>>();
        //7th Grade English Transcript
        Map<String, Object> studentTranscript = new LinkedHashMap<String, Object>();
        studentTranscript.put("id", "dc41d13b-9f8c-486d-b720-6b3c8a761f06");
        studentTranscript.put("finalLetterGradeEarned", "F");
        studentTranscript.put("courseId", "5fac98fc-62a4-49d1-9417-11b0823a2e7a");
        studentTranscriptAssociations.add(studentTranscript);
        //8th Grade Composition Transcript
        studentTranscript = new LinkedHashMap<String, Object>();
        studentTranscript.put("id", "b66f4677-c250-4ad9-a318-cf77ccefc651");
        studentTranscript.put("finalLetterGradeEarned", "D-");
        studentTranscript.put("courseId", "9eb8fe49-a08a-4722-a6d4-fe1d2ec69ddd");
        studentTranscriptAssociations.add(studentTranscript);
        
        //StudentSections
        List<Map<String, Object>> studentSectionAssociations = new ArrayList<Map<String, Object>>();
        //7th Grade Enlish Section 6       
        Map<String, Object> sessions = new LinkedHashMap<String, Object>();
        sessions.put("id", "432f3ed2-f413-4ed0-82f3-d2ba00c5b61a");
        sessions.put("schoolYear", "2010-2011");
        sessions.put("sessionName", "Fall 2010 East Daybreak Junior High");
        sessions.put("term", "Fall Semester");
        sessions.put("endDate", "2010-12-16");
        sessions.put("beginDate", "2010-09-06");
        Map<String, Object> courses = new LinkedHashMap<String, Object>();
        courses.put("id", "5fac98fc-62a4-49d1-9417-11b0823a2e7a");
        courses.put("subjectArea", "English Language and Literature");
        courses.put("courseTitle", "7th Grade English");
        Map<String, Object> sections = new LinkedHashMap<String, Object>();
        sections.put("sessions", sessions);
        sections.put("courses", courses);
        sections.put("courseId", "5fac98fc-62a4-49d1-9417-11b0823a2e7a");
        sections.put("sessionId", "432f3ed2-f413-4ed0-82f3-d2ba00c5b61a");
        Map<String, Object> sectionAssoc = new LinkedHashMap<String, Object>();
        sectionAssoc.put("id", "a38a9d24-26c8-4f56-9af8-bacac6fc445d");
        sectionAssoc.put("sections", sections);
        sectionAssoc.put("sectionId", "3bca9b7d-03b5-4bdd-8e5e-b225a78b9e4e");
        studentSectionAssociations.add(sectionAssoc);
        //8th Grade English
        sessions = new LinkedHashMap<String, Object>();
        sessions.put("id", "c689b232-b075-4bae-a579-8c7dc0f471cb");
        sessions.put("schoolYear", "2011-2012");
        sessions.put("sessionName", "Fall 2011 East Daybreak Junior High");
        sessions.put("term", "Fall Semester");
        sessions.put("endDate", "2011-12-16");
        sessions.put("beginDate", "2011-09-06");
        courses = new LinkedHashMap<String, Object>();
        courses.put("id", "48e8e237-1039-455f-a5ee-d3cc188ceac8");
        courses.put("subjectArea", "English Language and Literature");
        courses.put("courseTitle", "8th Grade English");
        sections = new LinkedHashMap<String, Object>();
        sections.put("sessions", sessions);
        sections.put("courses", courses);
        sections.put("courseId", "48e8e237-1039-455f-a5ee-d3cc188ceac8");
        sections.put("sessionId", "c689b232-b075-4bae-a579-8c7dc0f471cb");
        sectionAssoc = new LinkedHashMap<String, Object>();
        sectionAssoc.put("id", "db7836e8-97b4-4079-9971-63a741403e43");
        sectionAssoc.put("sections", sections);
        sectionAssoc.put("sectionId", "fc4de89d-534e-4ae7-ae3c-b4a536e1a4ac");
        studentSectionAssociations.add(sectionAssoc);
        //9th Grade English
        sessions = new LinkedHashMap<String, Object>();
        sessions.put("id", "bf67a1cb-c7df-40b2-bfa8-99a0691e8c09");
        sessions.put("schoolYear", "2010-2011");
        sessions.put("sessionName", "Spring 2011 East Daybreak Junior High");
        sessions.put("term", "Spring Semester");
        sessions.put("endDate", "2011-05-16");
        sessions.put("beginDate", "2011-01-06");
        courses = new LinkedHashMap<String, Object>();
        courses.put("id", "9eb8fe49-a08a-4722-a6d4-fe1d2ec69ddd");
        courses.put("subjectArea", "English Language and Literature");
        courses.put("courseTitle", "7th Grade Composition");
        sections = new LinkedHashMap<String, Object>();
        sections.put("sessions", sessions);
        sections.put("courses", courses);
        sections.put("courseId", "9eb8fe49-a08a-4722-a6d4-fe1d2ec69ddd,");
        sections.put("sessionId", "bf67a1cb-c7df-40b2-bfa8-99a0691e8c09");
        sectionAssoc = new LinkedHashMap<String, Object>();
        sectionAssoc.put("id", "d720837a-ee94-4add-bd4b-7d4176b38d05");
        sectionAssoc.put("sections", sections);
        sectionAssoc.put("sectionId", "bcdc582d-149d-449f-a06a-9a3181e6bb97");
        studentSectionAssociations.add(sectionAssoc);
        
        Map<String, Object> transcripts = new LinkedHashMap<String, Object>();
        transcripts.put("courseTranscripts", studentTranscriptAssociations);
        transcripts.put("studentSectionAssociations", studentSectionAssociations);
        student.put("transcript", transcripts);
        


        return studentSummaries;
    }

}

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


package org.slc.sli.dashboard.manager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.slc.sli.dashboard.entity.Config;
import org.slc.sli.dashboard.entity.GenericEntity;
import org.slc.sli.dashboard.manager.impl.StudentProgressManagerImpl;
import org.slc.sli.dashboard.util.Constants;

/**
 * Tests for student progress manager
 * @author srupasinghe
 *
 */
public class StudentProgressManagerTest {
    private StudentProgressManagerImpl manager;
    private EntityManager mockEntity;

    private static final String STUDENTID = "123456";
    private static final String YEAR_1998_1999 = "1998-1999";
    private static final String YEAR_2006_2007 = "2006-2007";
    private static final String YEAR_2009_2010 = "2009-2010";
    private static final String COURSEID1 = "5678";
    private static final String COURSEID2 = "1234";
    private static final String COURSEID3 = "9876";

    @Before
    public void setUp() throws Exception {
        manager = new StudentProgressManagerImpl();
        mockEntity = mock(EntityManager.class);
        manager.setEntityManager(mockEntity);

    }

    @After
    public void tearDown() throws Exception {
        manager = null;
        mockEntity = null;
    }

    @Test
    public void testGetStudentHistoricalAssessments() throws Exception {
        final String token = "token";
        final List<String> studentIds = new ArrayList<String>();
        final String subjectArea = "math";
        final String course = "algebra";
        final String section = "algebra I";
        final Map<String, String> params = new HashMap<String, String>();
        GenericEntity subjArea = new GenericEntity();
        subjArea.put(Constants.ATTR_SUBJECTAREA, subjectArea);

        when(mockEntity.getCourses(token, section, params)).thenReturn(buildHistoricalData());

        when(mockEntity.getEntity(token, Constants.ATTR_COURSES, course,
                new HashMap<String, String>())).thenReturn(subjArea);

        Map<String, List<GenericEntity>> historicalData =
                manager.getStudentHistoricalAssessments(token, studentIds, course, section);

        assertEquals("D-", historicalData.get(STUDENTID).get(0).get(Constants.ATTR_FINAL_LETTER_GRADE).toString());
        assertEquals("2001-2002", historicalData.get(STUDENTID).get(0).get(Constants.ATTR_SCHOOL_YEAR).toString());
        assertEquals("Algebra I", historicalData.get(STUDENTID).get(0).get(Constants.ATTR_COURSE_TITLE).toString());
        assertEquals("Fall Semester", historicalData.get(STUDENTID).get(0).get(Constants.ATTR_TERM).toString());

        SortedSet<String> schoolYears = manager.getSchoolYears("", historicalData);

        assertEquals("2001-2002 Fall Semester", schoolYears.first());

    }

    private List<GenericEntity> buildHistoricalData() {
        List<GenericEntity> historicalData = new ArrayList<GenericEntity>();

        GenericEntity student1 = new GenericEntity();
        student1.put("id", STUDENTID);
        student1.put("transcript", buildTranscript());
        historicalData.add(student1);

        return historicalData;
    }

    private Map<String, Object> buildTranscript() {
        Map<String, Object> transcript = new HashMap<String, Object>();
        transcript.put(Constants.ATTR_COURSE_TRANSCRIPTS, buildStudentTranscripts());
        transcript.put(Constants.ATTR_STUDENT_SECTION_ASSOC, buildStudentSectionAssocs());
        return transcript;
    }

    private List<Map<String, Object>> buildStudentSectionAssocs() {
        List<Map<String, Object>> studentSectionAssocs = new ArrayList<Map<String, Object>>();
        Map<String, Object> studentSectionAssoc = new HashMap<String, Object>();
        studentSectionAssoc.put(Constants.ATTR_SECTIONS, buildSection());
        studentSectionAssocs.add(studentSectionAssoc);
        return studentSectionAssocs;
    }

    private Map<String, Object> buildSection() {
        Map<String, Object> section = new HashMap<String, Object>();
        section.put(Constants.ATTR_SESSIONS, buildSession());
        section.put(Constants.ATTR_COURSES, buildCourse());
        return section;
    }

    private Map<String, Object> buildCourse() {
        Map<String, Object> course = new HashMap<String, Object>();
        course.put(Constants.ATTR_COURSE_TITLE, "Algebra I");
        course.put(Constants.ATTR_ID, "1234Course");
        course.put(Constants.ATTR_SUBJECTAREA, "math");
        return course;
    }

    private Map<String, Object> buildSession() {
        Map<String, Object> session = new HashMap<String, Object>();
        session.put(Constants.ATTR_SCHOOL_YEAR, "2001-2002");
        session.put(Constants.ATTR_TERM, "Fall Semester");
        return session;
    }

    private List<Map<String, Object>> buildStudentTranscripts() {
        List<Map<String, Object>> studentTranscripts = new ArrayList<Map<String, Object>>();
        Map<String, Object> studentTranscript = new HashMap<String, Object>();
        studentTranscript.put(Constants.ATTR_FINAL_LETTER_GRADE, "D-");
        studentTranscript.put(Constants.ATTR_COURSE_ID, "1234Course");
        studentTranscripts.add(studentTranscript);
        return studentTranscripts;
    }


    @Test
    public void testGetCurrentProgressForStudents() {
        String token = "token";
        String selectedSection = "5678";

        List<GenericEntity> studentInfos = new ArrayList<GenericEntity>();
        GenericEntity studentInfo = new GenericEntity();

        GenericEntity gradebookEntry1 = new GenericEntity();
        gradebookEntry1.put("id", "5555");
        gradebookEntry1.put("gradebookEntryType", "Unit Test");
        GenericEntity gradebookEntry2 = new GenericEntity();
        gradebookEntry2.put("id", "5556");
        gradebookEntry2.put("gradebookEntryType", "Unit Test");
        GenericEntity gradebookEntry3 = new GenericEntity();
        gradebookEntry3.put("id", "5557");
        gradebookEntry3.put("gradebookEntryType", "Unit Test");

        GenericEntity gradebook1 = new GenericEntity();
        gradebook1.put("id", "9999");
        gradebook1.put("numericGradeEarned", 84.0);
        gradebook1.put("dateFulfilled", "2011-05-15");
        gradebook1.put("gradebookEntryId", "5555");
        gradebook1.put(Constants.ATTR_GRADEBOOK_ENTRIES, gradebookEntry1);
        GenericEntity gradebook2 = new GenericEntity();
        gradebook2.put("id", "9998");
        gradebook2.put("numericGradeEarned", 88.0);
        gradebook2.put("dateFulfilled", "2011-06-30");
        gradebook2.put("gradebookEntryId", "5556");
        gradebook2.put(Constants.ATTR_GRADEBOOK_ENTRIES, gradebookEntry2);
        GenericEntity gradebook3 = new GenericEntity();
        gradebook3.put("id", "9997");
        gradebook3.put("numericGradeEarned", 81.0);
        gradebook3.put("dateFulfilled", "2010-05-02");
        gradebook3.put("gradebookEntryId", "5557");
        gradebook3.put(Constants.ATTR_GRADEBOOK_ENTRIES, gradebookEntry3);

        List<GenericEntity> gradebooks = new ArrayList<GenericEntity>();
        gradebooks.add(gradebook1);
        gradebooks.add(gradebook2);
        gradebooks.add(gradebook3);

        studentInfo.put(Constants.ATTR_STUDENT_GRADEBOOK_ENTRIES, gradebooks);
        studentInfo.put("id", STUDENTID);
        studentInfos.add(studentInfo);

        when(mockEntity.getStudentsWithGradebookEntries(token, selectedSection)).thenReturn(studentInfos);

        //call the method
        Map<String, Map<String, GenericEntity>> results = manager.getCurrentProgressForStudents(token, new ArrayList<String>(), selectedSection);
        assertEquals("Size should be 1", 1, results.size());

        Map<String, GenericEntity> tests = results.get(STUDENTID);
        assertNotNull("Should have an entry", tests.get("5555"));
        assertEquals("numeric grade should match", 84.0, tests.get("5555").get("numericGradeEarned"));
        assertEquals("numeric grade should match", "2011-05-15", tests.get("5555").getString("dateFulfilled"));
        assertEquals("numeric grade should match", "Unit Test", tests.get("5555").getString("gradebookEntryType"));
    }

    @Test
    public void testRetrieveSortedGradebookEntryList() {
        SortedSet<GenericEntity> tests = manager.retrieveSortedGradebookEntryList(buildUnitTestDataMap());

        assertEquals("Should have 3 entries", 3, tests.size());
        assertEquals("First element should match", "2010-05-02", tests.first().getString("dateFulfilled"));
        assertEquals("First element should match", "2011-06-30", tests.last().getString("dateFulfilled"));
    }


    private Map<String, Map<String, GenericEntity>> buildUnitTestDataMap() {
        Map<String, Map<String, GenericEntity>> data = new HashMap<String, Map<String, GenericEntity>>();

        GenericEntity gradebook1 = new GenericEntity();
        gradebook1.put("id", "9999");
        gradebook1.put("numericGradeEarned", "84.0");
        gradebook1.put("dateFulfilled", "2011-05-15");
        GenericEntity gradebook2 = new GenericEntity();
        gradebook2.put("id", "9998");
        gradebook2.put("numericGradeEarned", "88.0");
        gradebook2.put("dateFulfilled", "2011-06-30");
        GenericEntity gradebook3 = new GenericEntity();
        gradebook3.put("id", "9997");
        gradebook3.put("numericGradeEarned", "81.0");
        gradebook3.put("dateFulfilled", "2010-05-02");

        Map<String, GenericEntity> map = new HashMap<String, GenericEntity>();
        map.put("9999", gradebook1);
        map.put("9998", gradebook2);
        map.put("9997", gradebook3);

        data.put(STUDENTID, map);

        return data;
    }

    @Test
    public void testGetTranscript() {

        String token = "1234";
        String studentId = "STUDENT-1234";
        Config.Data config = new Config.Data();
        List<String> optionalFields = new ArrayList<String>();
        optionalFields.add(Constants.ATTR_TRANSCRIPT);

        when(mockEntity.getStudentWithOptionalFields(token, studentId, optionalFields)).
                thenReturn(new MockAPIClient().getStudentWithOptionalFields(token, studentId, optionalFields));

        GenericEntity actual = manager.getTranscript(token, studentId, config);

        @SuppressWarnings("unchecked")
        List<GenericEntity> transcriptHistory = (List<GenericEntity>) actual.get(StudentProgressManagerImpl.TRANSCRIPT_HISTORY);
        assertEquals(2, transcriptHistory.size());
        GenericEntity termOne = transcriptHistory.get(0);
        GenericEntity termTwo = transcriptHistory.get(1);

        assertEquals("Spring Semester", termOne.getString(Constants.ATTR_TERM));
        assertEquals("Fall Semester", termTwo.getString(Constants.ATTR_TERM));

        assertEquals("Seventh grade", termOne.getString(Constants.ATTR_GRADE_LEVEL));
        assertEquals("Seventh grade", termTwo.getString(Constants.ATTR_GRADE_LEVEL));

        @SuppressWarnings("unchecked")
        List<GenericEntity> termOneCourses = (List<GenericEntity>) termOne.get(Constants.ATTR_COURSES);

        assertEquals(1, termOneCourses.size());
        assertEquals("85.0", termOneCourses.get(0).getString("grade"));
        assertEquals("7th Grade Composition", termOneCourses.get(0).getString(StudentProgressManagerImpl.COURSE));

        @SuppressWarnings("unchecked")
        List<GenericEntity> termTwoCourses = (List<GenericEntity>) termTwo.get(Constants.ATTR_COURSES);

        assertEquals(1, termTwoCourses.size());
        assertEquals("87.0", termTwoCourses.get(0).getString(StudentProgressManagerImpl.GRADE));
        assertEquals("7th Grade English", termTwoCourses.get(0).getString(StudentProgressManagerImpl.COURSE));

    }
}

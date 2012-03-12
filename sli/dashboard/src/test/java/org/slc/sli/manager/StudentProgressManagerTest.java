package org.slc.sli.manager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
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

import org.slc.sli.entity.GenericEntity;

/**
 * Tests for student progress manager
 * @author srupasinghe
 *
 */
public class StudentProgressManagerTest {
    private StudentProgressManager manager;
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
        manager = new StudentProgressManager();
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
        String token = "token";
        String courseId = "56789";
        String selectedCourseId = "5555";
        String sessionId1 = "9999", sessionId2 = "9998";
        
        //create the course
        GenericEntity courseEntity = new GenericEntity();
        courseEntity.put("id", courseId);
        courseEntity.put("courseTitle", "Math 1");
        //create the accociation
        GenericEntity assocEntity = new GenericEntity();
        assocEntity.put("finalLettergrade", "A");
        assocEntity.put("studentId", STUDENTID);
        //
        GenericEntity subjectAreaEntity = new GenericEntity();
        courseEntity.put("subjectArea", "Math");
        //create the sections
        GenericEntity sectionEntity1 = new GenericEntity();
        sectionEntity1.put("sessionId", sessionId1);
        sectionEntity1.put("courseId", COURSEID1);
        sectionEntity1.put("uniqueSectionCode", "Math 1 A");
        GenericEntity sectionEntity2 = new GenericEntity();
        sectionEntity2.put("sessionId", sessionId2);
        sectionEntity2.put("courseId", COURSEID2);
        sectionEntity1.put("uniqueSectionCode", "Math 1 B");
        
        //add the courses
        List<GenericEntity> courses = new ArrayList<GenericEntity>();
        courses.add(courseEntity);
        //add the associations
        List<GenericEntity> studentCourseAssocs = new ArrayList<GenericEntity>();
        studentCourseAssocs.add(assocEntity);
        //add the students
        List<String> students = new ArrayList<String>();
        students.add(STUDENTID);
        //add the sections
        List<GenericEntity> sections = new ArrayList<GenericEntity>();
        sections.add(sectionEntity1);
        sections.add(sectionEntity2);
        
        Map<String, String> params = new HashMap<String, String>();
        params.put("includeFields", "courseTitle");
        
        Map<String, String> params1 = new HashMap<String, String>();
        params1.put("courseId", courseId);
        params1.put("includeFields", "finalLetterGradeEarned");
        
        Map<String, String> params2 = new HashMap<String, String>();
        params2.put("courseId", courseId);
        
        when(mockEntity.getCourses(token, STUDENTID, params)).thenReturn(courses);
        when(mockEntity.getStudentTranscriptAssociations(token, STUDENTID, params1)).thenReturn(studentCourseAssocs);
        when(mockEntity.getEntity(token, "courses", selectedCourseId, new HashMap<String, String>())).thenReturn(subjectAreaEntity);
        when(mockEntity.getSections(token, STUDENTID, params2)).thenReturn(sections);
        
        Map<String, List<GenericEntity>> results = manager.getStudentHistoricalAssessments(token, students, selectedCourseId);
        
        assertEquals("Should have one result", 1, results.size());
        assertTrue("Should have a key with the student Id", results.keySet().contains(STUDENTID));
        assertEquals("Student Id should be 123456", COURSEID1, results.get(STUDENTID).get(0).get("courseId"));
        assertEquals("Course title should match", "Math 1 B", results.get(STUDENTID).get(0).get("courseTitle"));
        assertEquals("Course title should match", "9999", results.get(STUDENTID).get(0).get("sessionId"));
    }
    
    @Test
    public void testApplySessionAndTranscriptInformation() {
        String token = "token";
        String sessionId1 = "9999", sessionId2 = "9998", sessionId3 = "9997";
        
        //create the sessions
        GenericEntity sessionEntity1 = new GenericEntity();
        sessionEntity1.put("schoolYear", YEAR_1998_1999);
        sessionEntity1.put("term", "Fall Semester");
        GenericEntity sessionEntity2 = new GenericEntity();
        sessionEntity2.put("schoolYear", YEAR_2009_2010);
        sessionEntity2.put("term", "Fall Semester");
        GenericEntity sessionEntity3 = new GenericEntity();
        sessionEntity3.put("schoolYear", YEAR_2006_2007);
        sessionEntity3.put("term", "Spring Semester");
        
        //create the accociation
        GenericEntity assocEntity = new GenericEntity();
        assocEntity.put("finalLetterGradeEarned", "A");
               
        //add the associations
        List<GenericEntity> studentCourseAssocs = new ArrayList<GenericEntity>();
        studentCourseAssocs.add(assocEntity);
        
        //create the params maps
        Map<String, String> params = new HashMap<String, String>();
        params.put("includeFields", "schoolYear,term");
        
        Map<String, String> params1 = new HashMap<String, String>();
        params1.put("courseId", COURSEID1);
        params1.put("includeFields", "finalLetterGradeEarned");
        
        Map<String, String> params2 = new HashMap<String, String>();
        params2.put("courseId", COURSEID2);
        params2.put("includeFields", "finalLetterGradeEarned");
        
        Map<String, String> params3 = new HashMap<String, String>();
        params3.put("courseId", COURSEID3);
        params3.put("includeFields", "finalLetterGradeEarned");
        
        //setup the mocks
        when(mockEntity.getEntity(token, "sessions", sessionId1, params)).thenReturn(sessionEntity1);
        when(mockEntity.getEntity(token, "sessions", sessionId2, params)).thenReturn(sessionEntity2);
        when(mockEntity.getEntity(token, "sessions", sessionId3, params)).thenReturn(sessionEntity3);
        
        when(mockEntity.getStudentTranscriptAssociations(token, STUDENTID, params1)).thenReturn(studentCourseAssocs);
        when(mockEntity.getStudentTranscriptAssociations(token, STUDENTID, params2)).thenReturn(studentCourseAssocs);
        when(mockEntity.getStudentTranscriptAssociations(token, STUDENTID, params3)).thenReturn(studentCourseAssocs);
        
        Map<String, List<GenericEntity>> data = buildHistoricalDataMap();
        
        SortedSet<String> results = manager.applySessionAndTranscriptInformation(token, data);
        
        assertEquals("Size should be 3", 3, results.size());
        assertEquals("First element should match", YEAR_2009_2010 + " Fall Semester", results.first());
        assertEquals("Third element should match", YEAR_1998_1999 + " Fall Semester", results.last());
        
        assertEquals("School year should match", YEAR_2009_2010 + " Fall Semester", data.get(STUDENTID).get(0).getString("schoolYear"));
        assertEquals("course title should match", "Math 1 B", data.get(STUDENTID).get(0).getString("courseTitle"));
        assertEquals("grade should match", "A", data.get(STUDENTID).get(0).getString("finalLetterGradeEarned"));
    }
    
    private Map<String, List<GenericEntity>> buildHistoricalDataMap() {
        Map<String, List<GenericEntity>> data = new HashMap<String, List<GenericEntity>>();
        
        GenericEntity entity1 = new GenericEntity();
        entity1.put("courseId", COURSEID1);
        entity1.put("courseTitle", "Math 1 A");
        entity1.put("sessionId", "9999");
        
        GenericEntity entity2 = new GenericEntity();
        entity2.put("courseId", COURSEID2);
        entity2.put("courseTitle", "Math 1 B");
        entity2.put("sessionId", "9998");
        
        GenericEntity entity3 = new GenericEntity();
        entity3.put("courseId", COURSEID3);
        entity3.put("courseTitle", "Math 2 A");
        entity3.put("sessionId", "9997");

        List<GenericEntity> list = new ArrayList<GenericEntity>();
        list.add(entity1);
        list.add(entity2);
        list.add(entity3);
        
        data.put(STUDENTID, list);
        
        return data;
    }

}

package org.slc.sli.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.slc.sli.entity.GenericEntity;
import org.slc.sli.util.Constants;

/**
 * Gathers and provides information needed for the student progress view
 * @author srupasinghe
 *
 */
public class StudentProgressManager {
    
    private static Logger log = LoggerFactory.getLogger(StudentProgressManager.class);
    
    @Autowired
    private EntityManager entityManager;
    
    /**
     * Returns a list of historical data for a given subject area
     * @param token Security token
     * @param studentIds List of student ids
     * @param selectedCourse The course to get information for
     * @return
     */
    public Map<String, List<GenericEntity>> getStudentHistoricalAssessments(final String token, List<String> studentIds, 
            String selectedCourse) {
        Map<String, List<GenericEntity>> results = new HashMap<String, List<GenericEntity>>();
        
        //get the subject area
        String subjectArea = getSubjectArea(token, selectedCourse);
        
        //build the params
        Map<String, String> params = new HashMap<String, String>();
        if (subjectArea != null)
            params.put(Constants.ATTR_SUBJECTAREA, subjectArea);
        params.put(Constants.PARAM_INCLUDE_FIELDS, Constants.ATTR_COURSE_TITLE);
        
        for (String studentId : studentIds) {
            log.debug("Historical data [studentId] " + studentId);
            
            //get the courses in the subject area for the given student
            List<GenericEntity> courses = entityManager.getCourses(token, studentId, params);
            log.debug("Historical data [courses] " + courses);
            
            for (GenericEntity course : courses) {
                log.debug("Historical data [course] " + course);
                
                //get the sections for the course
                List<GenericEntity> sections = getSectionsForCourse(token, studentId, course.getString(Constants.ATTR_ID));
                
                for (GenericEntity section : sections) {
                    section.remove("entityType");
                    section.remove("links");
                    
                    //add the new data
                    section.put(Constants.ATTR_COURSE_TITLE, course.getString(Constants.ATTR_COURSE_TITLE));
                    section.put(Constants.ATTR_SUBJECTAREA, subjectArea);
                    section.put(Constants.ATTR_COURSE_TITLE, section.getString(Constants.ATTR_UNIQUE_SECTION_CODE));
                    
                    //add the section to the map
                    if (results.get(studentId) != null) {
                        results.get(studentId).add(section);
                    } else {
                        List<GenericEntity> list = new ArrayList<GenericEntity>();
                        list.add(section);
                        
                        results.put(studentId, list);
                    }
                }                
            }
        }
        
        return results;
    }
    
    /**
     * Get the subject area for the selected  course
     * @param token Security token
     * @param selectedCourse The id for the selected course
     * @return
     */
    protected String getSubjectArea(final String token, String selectedCourse) {
        String subjectArea = null;
        Map<String, String> params = new HashMap<String, String>();
        
        GenericEntity entity = entityManager.getEntity(token, Constants.ATTR_COURSES, selectedCourse, params);
        
        if (entity != null) {
            subjectArea = entity.getString(Constants.ATTR_SUBJECTAREA);
        }
        
        return subjectArea;
    }
    
    /**
     * Set the transcript and session information for the given sections
     * @param token Security token
     * @param historicalData The historical data
     * @return
     */
    public SortedSet<String> applySessionAndTranscriptInformation(final String token, Map<String, 
            List<GenericEntity>> historicalData) {
        SortedSet<String> results = new TreeSet<String>();
        
        //build the params
        Map<String, String> params = new HashMap<String, String>();
        params.put(Constants.PARAM_INCLUDE_FIELDS, Constants.ATTR_SCHOOL_YEAR + "," + Constants.ATTR_TERM);
        
        for (Map.Entry<String, List<GenericEntity>> sectionData : historicalData.entrySet()) {
            String studentId = sectionData.getKey();
            List<GenericEntity> list = sectionData.getValue();
            
            //get the assessment list
            for (GenericEntity section : list) {
                //get the session
                GenericEntity session = entityManager.getEntity(token, Constants.ATTR_SESSIONS, section.getString(Constants.ATTR_SESSION_ID), params);
                //add the school year
                results.add(session.getString(Constants.ATTR_SCHOOL_YEAR) + " " + session.getString(Constants.ATTR_TERM));
                
                //get the studentCourseAssociation for the given student and course
                List<GenericEntity> transcripts = getStudentCourseAssociations(token, studentId, section.getString(Constants.ATTR_COURSE_ID));
                
                for (GenericEntity transcript : transcripts) {                    
                    log.debug("Historical data [studentTranscriptAssociations] " + transcript);
                    
                    section.put(Constants.ATTR_FINAL_LETTER_GRADE, transcript.getString(Constants.ATTR_FINAL_LETTER_GRADE));
                    section.put(Constants.ATTR_SCHOOL_YEAR, session.getString(Constants.ATTR_SCHOOL_YEAR) 
                            + " " + session.getString(Constants.ATTR_TERM));
                }
            }
            
            //sort by the school year
            Collections.sort(list, new SchoolYearComparator());
        }
        
        return results;
    }
        
    /**
     * Returns a list of studentCourseAssociations for a given student and course Id
     * @param token Security token
     * @param studentId The student Id
     * @param courseId The course Id
     * @return
     */
    protected List<GenericEntity> getStudentCourseAssociations(final String token, final String studentId, String courseId) {
        
        //build the params
        Map<String, String> params = new HashMap<String, String>();
        params.put(Constants.ATTR_COURSE_ID, courseId);
        params.put(Constants.PARAM_INCLUDE_FIELDS, Constants.ATTR_FINAL_LETTER_GRADE);
        
        return entityManager.getStudentTranscriptAssociations(token, studentId, params);
    }
    
    /**
     * Get the sections for the given student and course
     * @param token Security token
     * @param studentId The student Id
     * @param courseId The course Id
     * @return
     */
    protected List<GenericEntity> getSectionsForCourse(final String token, final String studentId, final String courseId) {
        Map<String, String> params = new HashMap<String, String>();
        params.put(Constants.ATTR_COURSE_ID, courseId);
        
        return entityManager.getSections(token, studentId, params);
    }
    
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
    
    
    /**
     * Compare two GenericEntities by the school year
     * @author srupasinghe
     *
     */
    class SchoolYearComparator implements Comparator<GenericEntity> {

        public int compare(GenericEntity e1, GenericEntity e2) {
            if (e1.getString(Constants.ATTR_SCHOOL_YEAR) == null 
                    || e2.getString(Constants.ATTR_SCHOOL_YEAR) == null) {
                return 0;
            }
            
            return e2.getString(Constants.ATTR_SCHOOL_YEAR).compareTo(e1.getString(Constants.ATTR_SCHOOL_YEAR));
        }
        
    }

}

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

package org.slc.sli.dashboard.manager.impl;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.slc.sli.dashboard.client.SDKConstants;
import org.slc.sli.dashboard.entity.Config;
import org.slc.sli.dashboard.entity.GenericEntity;
import org.slc.sli.dashboard.entity.util.GenericEntityEnhancer;
import org.slc.sli.dashboard.manager.EntityManager;
import org.slc.sli.dashboard.manager.StudentProgressManager;
import org.slc.sli.dashboard.util.Constants;
import org.slc.sli.dashboard.util.ExecutionTimeLogger.LogExecutionTime;

/**
 * Gathers and provides information needed for the student progress view
 * 
 * @author srupasinghe
 * 
 */
public class StudentProgressManagerImpl implements StudentProgressManager {
    private static final Logger LOG = LoggerFactory.getLogger(StudentProgressManagerImpl.class);
    public static final String TRANSCRIPT_HISTORY = "transcriptHistory";
    public static final String GRADE = "grade";
    public static final String SUBJECT = "subject";
    public static final String COURSE = "course";
    private static final DecimalFormat GRADE_FORMATTER = new DecimalFormat("0.0");
    
    @Autowired
    private EntityManager entityManager;
    
    @Override
    @SuppressWarnings("unchecked")
    @LogExecutionTime
    public GenericEntity getTranscript(String token, Object studentIdObj, Config.Data config) {
        
        SortedMap<GenericEntity, List<GenericEntity>> transcripts = new TreeMap<GenericEntity, List<GenericEntity>>(
                new SessionComparator());
        
        String studentId = studentIdObj.toString();
        List<String> optionalFields = new LinkedList<String>();
        optionalFields.add(Constants.ATTR_TRANSCRIPT);
        
        GenericEntity studentWithTranscript = entityManager.getStudentWithOptionalFields(token, studentId,
                optionalFields);
        if (studentWithTranscript == null) {
            return new GenericEntity();
        }
        
        Map<String, Object> studentTranscript = (Map<String, Object>) studentWithTranscript
                .get(Constants.ATTR_TRANSCRIPT);
        if (studentTranscript == null) {
            return new GenericEntity();
        }
        
        List<Map<String, Object>> courseTranscripts = (List<Map<String, Object>>) studentTranscript
                .get(Constants.ATTR_COURSE_TRANSCRIPTS);
        List<Map<String, Object>> studentSectionAssociations = (List<Map<String, Object>>) studentTranscript
                .get(Constants.ATTR_STUDENT_SECTION_ASSOC);
        
        if (studentSectionAssociations == null || courseTranscripts == null) {
            return new GenericEntity();
        }
        
        Map<String, GenericEntity> cache = new HashMap<String, GenericEntity>();
        cacheStudent(studentId, token, studentSectionAssociations, cache, courseTranscripts);
        
        for (Map<String, Object> studentSectionAssociation : studentSectionAssociations) {
            Map<String, Object> courseTranscript = getCourseTranscriptForSection(studentSectionAssociation,
                    courseTranscripts);
            
            // skip this course if we can't find previous info
            if (courseTranscript == null) {
                continue;
            }
            
            Map<String, Object> section = getGenericEntity(studentSectionAssociation, Constants.ATTR_SECTIONS);
            Map<String, Object> course = getGenericEntity(section, Constants.ATTR_COURSES);
            Map<String, Object> session = getGenericEntity(section, Constants.ATTR_SESSIONS);
            
            GenericEntity term = new GenericEntity();
            
            term.put(Constants.ATTR_TERM, getValue(session, Constants.ATTR_TERM));
            term.put(Constants.ATTR_GRADE_LEVEL, getValue(courseTranscript, Constants.ATTR_GRADE_LEVEL_WHEN_TAKEN));
            term.put(Constants.ATTR_SCHOOL, getSchoolName(section, token, cache));
            term.put(Constants.ATTR_SCHOOL_YEAR, getValue(session, Constants.ATTR_SCHOOL_YEAR));
            term.put(Constants.ATTR_CUMULATIVE_GPA, getGPA(session, studentId, token, cache));
            term.put(Constants.ATTR_SESSION_BEGIN_DATE, getValue(session, Constants.ATTR_SESSION_BEGIN_DATE));
            
            GenericEntityEnhancer.convertGradeLevel(term, Constants.ATTR_GRADE_LEVEL);
            
            // This isn't a new term
            if (transcripts.containsKey(term)) {
                List<GenericEntity> courses = transcripts.get(term);
                GenericEntity courseData = getCourseData(courseTranscript, course);
                courses.add(courseData);
            } else { // this is the first time the term has been encountered
                List<GenericEntity> courses = new ArrayList<GenericEntity>();
                GenericEntity courseData = getCourseData(courseTranscript, course);
                courses.add(courseData);
                transcripts.put(term, courses);
            }
        }
        
        List<GenericEntity> transcriptData = new ArrayList<GenericEntity>();
        
        for (Map.Entry<GenericEntity, List<GenericEntity>> entry : transcripts.entrySet()) {
            GenericEntity term = new GenericEntity();
            term.putAll(entry.getKey());
            term.put(Constants.ATTR_COURSES, entry.getValue());
            transcriptData.add(term);
        }
        
        GenericEntity ret = new GenericEntity();
        ret.put(TRANSCRIPT_HISTORY, transcriptData);
        return ret;
    }
    
    private void cacheStudent(String studentId, String token, List<Map<String, Object>> studentSectionAssociations,
            Map<String, GenericEntity> cache, List<Map<String, Object>> courseTranscripts) {
        
        List<String> entityIds = new ArrayList<String>();
        List<String> sessionIds = new ArrayList<String>();
        
        for (Map<String, Object> studentSectionAssociation : studentSectionAssociations) {
            Map<String, Object> courseTranscript = getCourseTranscriptForSection(studentSectionAssociation,
                    courseTranscripts);
            
            // skip this course if we can't find previous info
            if (courseTranscript == null) {
                continue;
            }
            
            Map<String, Object> section = getGenericEntity(studentSectionAssociation, Constants.ATTR_SECTIONS);
            Map<String, Object> session = getGenericEntity(section, Constants.ATTR_SESSIONS);
            sessionIds.add(getValue(session, Constants.ATTR_ID));
            entityIds.add(getValue(section, Constants.ATTR_SCHOOL_ID));
        }
        
        getSchools(entityIds, token, cache);
        getAcademicRecords(sessionIds, studentId, token, cache);
    }
    
    private String getGPA(Map<String, Object> session, String studentId, String token, Map<String, GenericEntity> cache) {
        String sessionId = getValue(session, Constants.ATTR_ID);
        
        GenericEntity academicRecord = cache.get(sessionId);
        
        if (academicRecord == null) {
            Map<String, String> params = new HashMap<String, String>();
            
            params.put(Constants.ATTR_SESSION_ID, sessionId);
            params.put(Constants.ATTR_STUDENT_ID, studentId);
            
            academicRecord = entityManager.getAcademicRecord(token, studentId, params);
        }
        
        String gpa = "";
        if (academicRecord != null) {
            gpa = GRADE_FORMATTER.format(academicRecord.get(Constants.ATTR_CUMULATIVE_GPA));
        }
        return gpa;
    }
    
    private void getAcademicRecords(List<String> sessions, String studentId, String token,
            Map<String, GenericEntity> cache) {
        List<GenericEntity> result = new ArrayList<GenericEntity>(sessions.size());
        for (int i = 0; i <= sessions.size() / Constants.MAX_IDS_PER_API_CALL; i++) {
            List<String> subList = sessions.subList(i * Constants.MAX_IDS_PER_API_CALL,
                    Math.min((i + 1) * Constants.MAX_IDS_PER_API_CALL, sessions.size()));
            String sessionId = "~";
            sessionId = sessionId + StringUtils.join(subList, "%7C");
            
            Map<String, String> params = new HashMap<String, String>();
            params.put(Constants.ATTR_SESSION_ID, sessionId);
            params.put(Constants.ATTR_STUDENT_ID, studentId);
            
            List<GenericEntity> academicRecords = entityManager.getAcademicRecords(token, studentId, params);
            if (academicRecords != null) {
                result.addAll(academicRecords);
            }
        }
        
        for (GenericEntity entity : result) {
            cache.put(entity.get("sessionId").toString(), entity);
        }
    }
    
    private GenericEntity getCourseData(Map<String, Object> courseTranscript, Map<String, Object> course) {
        GenericEntity courseData = new GenericEntity();
        courseData.put(GRADE, getGrade(courseTranscript));
        courseData.put(SUBJECT, getValue(course, Constants.ATTR_SUBJECTAREA));
        courseData.put(COURSE, getValue(course, Constants.ATTR_COURSE_TITLE));
        return courseData;
    }
    
    private String getGrade(Map<String, Object> courseTranscript) {
        String grade = "";
        // Try to get numeric grade first
        grade = getValue(courseTranscript, Constants.ATTR_FINAL_NUMERIC_GRADE);
        if (grade.equals("")) {
            grade = getValue(courseTranscript, Constants.ATTR_FINAL_LETTER_GRADE);
        }
        return grade;
    }
    
    private String getSchoolName(Map<String, Object> section, String token, Map<String, GenericEntity> cache) {
        String schoolId = getValue(section, Constants.ATTR_SCHOOL_ID);
        
        GenericEntity school = null;
        if (cache != null) {
            school = cache.get(schoolId);
        }
        if (cache == null || school == null) {
            school = entityManager.getEntity(token, Constants.ATTR_SCHOOLS, schoolId, new HashMap<String, String>());
        }
        String schoolName = "";
        if (school != null) {
            schoolName = school.getString(Constants.ATTR_NAME_OF_INST);
        }
        return schoolName;
    }
    
    private void getSchools(List<String> schoolIds, String token, Map<String, GenericEntity> cache) {
        
        List<GenericEntity> schools = entityManager.getEntities(token, SDKConstants.SCHOOLS_ENTITY, schoolIds, new HashMap<String, String>());
        
        for (GenericEntity school : schools) {
            cache.put(school.getId(), school);
        }
    }
    
    /**
     * Returns a list of historical data for a given subject area
     * 
     * @param token
     *            Security token
     * @param studentIds
     *            List of student ids
     * @param selectedCourse
     *            The course to get information for
     * @return
     */
    @Override
    @SuppressWarnings("unchecked")
    public Map<String, List<GenericEntity>> getStudentHistoricalAssessments(final String token,
            List<String> studentIds, String selectedCourse, String selectedSection) {
        Map<String, List<GenericEntity>> results = new HashMap<String, List<GenericEntity>>();
        
        // get the subject area
        String subjectArea = getSubjectArea(token, selectedCourse);
        Map<String, String> params = new HashMap<String, String>();
        
        List<GenericEntity> students = entityManager.getCourses(token, selectedSection, params);
        
        if (students == null || students.isEmpty()) {
            return results;
        }
        for (GenericEntity student : students) {
            String studentId = student.getString(Constants.ATTR_ID);
            List<GenericEntity> transcriptData = new ArrayList<GenericEntity>();
            
            Map<String, Object> transcript = (Map<String, Object>) student.get(Constants.ATTR_TRANSCRIPT);
            List<Map<String, Object>> courseTranscripts = (List<Map<String, Object>>) transcript
                    .get(Constants.ATTR_COURSE_TRANSCRIPTS);
            List<Map<String, Object>> studentSectionAssociations = (List<Map<String, Object>>) transcript
                    .get(Constants.ATTR_STUDENT_SECTION_ASSOC);
            
            // skip if we have no associations or we have no previous transcripts
            if (studentSectionAssociations == null || courseTranscripts == null) {
                continue;
            }
            
            studentSectionAssociations = filterBySubjectArea(studentSectionAssociations, subjectArea);
            
            for (Map<String, Object> studentSectionAssoc : studentSectionAssociations) {
                // Get the course transcript for a particular section
                Map<String, Object> courseTranscript = getCourseTranscriptForSection(studentSectionAssoc,
                        courseTranscripts);
                // skip this course if we can't find previous info
                if (courseTranscript == null) {
                    continue;
                }
                
                Map<String, Object> section = getGenericEntity(studentSectionAssoc, Constants.ATTR_SECTIONS);
                Map<String, Object> course = getGenericEntity(section, Constants.ATTR_COURSES);
                Map<String, Object> session = getGenericEntity(section, Constants.ATTR_SESSIONS);
                
                GenericEntity data = new GenericEntity();
                data.put(Constants.ATTR_FINAL_LETTER_GRADE, courseTranscript.get(Constants.ATTR_FINAL_LETTER_GRADE)
                        .toString());
                data.put(Constants.ATTR_COURSE_TITLE, getValue(course, Constants.ATTR_COURSE_TITLE));
                data.put(Constants.ATTR_SCHOOL_YEAR, getValue(session, Constants.ATTR_SCHOOL_YEAR));
                data.put(Constants.ATTR_TERM, getValue(session, Constants.ATTR_TERM));
                
                transcriptData.add(data);
            }
            
            results.put(studentId, transcriptData);
        }
        
        return results;
    }
    
    @SuppressWarnings("unchecked")
    private Map<String, Object> getGenericEntity(Map<String, Object> map, String field) {
        
        if (map == null) {
            return null;
        }
        
        return (Map<String, Object>) map.get(field);
    }
    
    private String getValue(Map<String, Object> map, String field) {
        String ret = "";
        
        if (map.containsKey(field)) {
            ret = map.get(field).toString();
        }
        
        return ret;
    }
    
    /**
     * Get the subject area for the selected course
     * 
     * @param token
     *            Security token
     * @param selectedCourse
     *            The id for the selected course
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
     * Return the course transcript for a given section
     * 
     * @param studentSectionAssoc
     *            The student section association we are looking at
     * @param courseTranscripts
     *            a set of transcripts for a given student
     * @return The transcript that applies to a given section
     */
    private Map<String, Object> getCourseTranscriptForSection(Map<String, Object> studentSectionAssoc,
            List<Map<String, Object>> courseTranscripts) {
        String courseId = "";
        Map<String, Object> section = getGenericEntity(studentSectionAssoc, Constants.ATTR_SECTIONS);
        Map<String, Object> course = getGenericEntity(section, Constants.ATTR_COURSES);
        if (course != null) {
            courseId = course.get(Constants.ATTR_ID).toString();
        }
        
        for (Map<String, Object> courseTranscript : courseTranscripts) {
            if (courseId.equals(courseTranscript.get(Constants.ATTR_COURSE_ID).toString())) {
                return courseTranscript;
            }
        }
        
        return null;
    }
    
    /**
     * Filters a list of student section associations down by a subject area
     * 
     * @param studentSectionAssociations
     *            The list of student section associations
     * @param subjectArea
     *            The filter to look at
     * @return Filtered list of student section associations
     */
    private List<Map<String, Object>> filterBySubjectArea(List<Map<String, Object>> studentSectionAssociations,
            String subjectArea) {
        if (subjectArea == null) {
            LOG.warn("Subject Area to match is null!");
            return studentSectionAssociations;
        }
        
        List<Map<String, Object>> filteredAssociations = new ArrayList<Map<String, Object>>();
        
        for (Map<String, Object> studentSectionAssociation : studentSectionAssociations) {
            Map<String, Object> section = getGenericEntity(studentSectionAssociation, Constants.ATTR_SECTIONS);
            Map<String, Object> course = getGenericEntity(section, Constants.ATTR_COURSES);
            if (course != null) {
                Object ssaSubjectArea = course.get(Constants.ATTR_SUBJECTAREA);
                if (ssaSubjectArea != null && ssaSubjectArea.toString().equals(subjectArea)) {
                    filteredAssociations.add(studentSectionAssociation);
                }
            }
        }
        
        return filteredAssociations;
    }
    
    /**
     * Set the transcript and session information for the given sections
     * 
     * @param token
     *            Security token
     * @param historicalData
     *            The historical data
     * @return
     */
    @Override
    public SortedSet<String> getSchoolYears(final String token, Map<String, List<GenericEntity>> historicalData) {
        SortedSet<String> results = new TreeSet<String>();
        
        for (List<GenericEntity> studentTranscripts : historicalData.values()) {
            for (GenericEntity transcript : studentTranscripts) {
                String schoolYear = transcript.getString(Constants.ATTR_SCHOOL_YEAR) + " "
                        + transcript.get(Constants.ATTR_TERM);
                results.add(schoolYear);
            }
        }
        
        return results;
    }
    
    /**
     * Returns all the gradebook entries for all the students in the given section
     * 
     * @param token
     *            Security token
     * @param studentIds
     *            The students in the section
     * @param selectedSection
     *            The section to look for
     * @return
     */
    @Override
    public Map<String, Map<String, GenericEntity>> getCurrentProgressForStudents(final String token,
            List<String> studentIds, String selectedSection) {
        Map<String, Map<String, GenericEntity>> results = new HashMap<String, Map<String, GenericEntity>>();
        
        List<GenericEntity> students = entityManager.getStudentsWithGradebookEntries(token, selectedSection);
        
        for (GenericEntity student : students) {
            String studentId = student.getString(Constants.ATTR_ID);
            
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> studentGradebookEntries = (List<Map<String, Object>>) student
                    .get(Constants.ATTR_STUDENT_GRADEBOOK_ENTRIES);
            if (studentGradebookEntries == null) {
                continue;
            }
            
            Map<String, GenericEntity> gradebookEntries = new HashMap<String, GenericEntity>();
            
            for (Map<String, Object> studentGradebookEntry : studentGradebookEntries) {
                
                // This doesn't cast well - have to manually create Generic Entity from Map<String,
                // Object>
                GenericEntity geStudentGradebookEntry = new GenericEntity();
                geStudentGradebookEntry.putAll(studentGradebookEntry);
                
                @SuppressWarnings("unchecked")
                Map<String, Object> entries = (Map<String, Object>) geStudentGradebookEntry
                        .get(Constants.ATTR_GRADEBOOK_ENTRIES);
                if (entries == null) {
                    continue;
                }

                geStudentGradebookEntry.put(Constants.ATTR_GRADEBOOK_ENTRY_TYPE, entries.get(Constants.ATTR_GRADEBOOK_ENTRY_TYPE));

                gradebookEntries.put(studentGradebookEntry.get(Constants.ATTR_GRADEBOOK_ENTRY_ID).toString(), geStudentGradebookEntry);
                LOG.debug("Progress data [studentGradebookEntry] {}", studentGradebookEntry);
            }
            
            results.put(studentId, gradebookEntries);
        }
        
        return results;
    }
    
    /**
     * Parses a numeric grade to a double
     * 
     * @param numericGrade
     *            The numeric grade as a string
     * @return
     */
    protected double parseNumericGrade(Object numericGrade) {
        if (numericGrade != null && numericGrade instanceof Double) {
            return ((Double) numericGrade).doubleValue();
        } else {
            return 0;
        }
    }
    
    /**
     * Returns a sorted unique set of gradebook entries(tests)
     * 
     * @param gradebookEntryData
     *            The gradebook entry data for a section
     * @return
     */
    @Override
    public SortedSet<GenericEntity> retrieveSortedGradebookEntryList(
            Map<String, Map<String, GenericEntity>> gradebookEntryData) {
        SortedSet<GenericEntity> list = new TreeSet<GenericEntity>(new DateFulFilledComparator());
        
        // Sorting by entity to be able to handle the introduction of GradebookEntry/type in the
        // future
        // Can be sorted by the keyset if GradebookEntry/type will not be used
        
        // go through and add the tests into one list
        for (Map<String, GenericEntity> map : gradebookEntryData.values()) {
            list.addAll(map.values());
        }
        
        return list;
    }
    
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
    
    /**
     * Compare two GenericEntities by the dateFulFilled
     * 
     * @author srupasinghe
     * 
     */
    public class DateFulFilledComparator implements Comparator<GenericEntity> {
        
        @Override
        public int compare(GenericEntity o1, GenericEntity o2) {
            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            
            if (o1.getString(Constants.ATTR_DATE_FULFILLED) != null
                    && o2.getString(Constants.ATTR_DATE_FULFILLED) != null) {
                try {
                    Date date1 = formatter.parse(o1.getString(Constants.ATTR_DATE_FULFILLED));
                    Date date2 = formatter.parse(o2.getString(Constants.ATTR_DATE_FULFILLED));
                    
                    return date1.compareTo(date2);
                    
                } catch (ParseException e) {
                    return 0;
                }
            }
            
            return 0;
        }
    }
    
    /**
     * Compare two GenericEntities by the Session
     * 
     * @author jshort
     * 
     */
    public class SessionComparator implements Comparator<GenericEntity> {
        
        @Override
        public int compare(GenericEntity ge0, GenericEntity ge1) {
            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            
            if (ge0.getString(Constants.ATTR_SESSION_BEGIN_DATE) != null
                    && ge1.getString(Constants.ATTR_SESSION_BEGIN_DATE) != null) {
                try {
                    Date date1 = formatter.parse(ge0.getString(Constants.ATTR_SESSION_BEGIN_DATE));
                    Date date2 = formatter.parse(ge1.getString(Constants.ATTR_SESSION_BEGIN_DATE));
                    
                    return date2.compareTo(date1);
                    
                } catch (ParseException e) {
                    return 0;
                }
            }
            
            return 0;
        }
    }
}

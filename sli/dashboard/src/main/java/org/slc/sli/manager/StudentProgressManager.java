package org.slc.sli.manager;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
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
public class StudentProgressManager implements Manager {
    
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
    @SuppressWarnings("unchecked")
    public Map<String, List<GenericEntity>> getStudentHistoricalAssessments(final String token, List<String> studentIds, 
            String selectedCourse, String selectedSection) {
        Map<String, List<GenericEntity>> results = new HashMap<String, List<GenericEntity>>();
        
        //get the subject area
        String subjectArea = getSubjectArea(token, selectedCourse);
        Map<String, String> params = new HashMap<String, String>();

        List<GenericEntity> students = entityManager.getCourses(token, selectedSection, params);

        if (students == null) return results;
        for (GenericEntity student : students) {
            String studentId = student.getString(Constants.ATTR_ID);
            List<GenericEntity> transcriptData = new ArrayList<GenericEntity>();

            Map<String, Object> transcript = (Map<String, Object>) student.get(Constants.ATTR_TRANSCRIPT);
            List<Map<String, Object>> studentTranscriptAssociations = (List<Map<String, Object>>) transcript.get(Constants.ATTR_STUDENT_TRANSCRIPT_ASSOC);
            List<Map<String, Object>> studentSectionAssociations = (List<Map<String, Object>>) transcript.get(Constants.ATTR_STUDENT_SECTION_ASSOC);

            // skip if we have no associations or we have no previous transcripts
            if (studentSectionAssociations == null || studentTranscriptAssociations == null) continue;

            studentSectionAssociations = filterBySubjectArea(studentSectionAssociations, subjectArea);

            for (Map<String, Object> studentSectionAssoc : studentSectionAssociations) {
                // Get the course transcript for a particular section
                Map<String, Object> courseTranscript = getCourseTranscriptForSection(studentSectionAssoc, studentTranscriptAssociations);
                // skip this course if we can't find previous info
                if (courseTranscript == null) continue;

                Map<String, Object> section = getSection(studentSectionAssoc);
                Map<String, Object> course = getCourse(section);
                Map<String, Object> session = getSession(section);

                GenericEntity data = new GenericEntity();
                data.put(Constants.ATTR_FINAL_LETTER_GRADE, courseTranscript.get(Constants.ATTR_FINAL_LETTER_GRADE).toString());
                data.put(Constants.ATTR_COURSE_TITLE, getCourseTitle(course));
                data.put(Constants.ATTR_SCHOOL_YEAR, getSchoolYear(session));
                data.put(Constants.ATTR_TERM, getTerm(session));

                transcriptData.add(data);
            }

            results.put(studentId, transcriptData);
        }

        return results;
    }

    /**
     * Get the session for a given section
     * @param section The section to pull from
     * @return the session
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> getSession(Map<String, Object> section) {
        if (section == null) return null;
        return (Map<String, Object>) section.get(Constants.ATTR_SESSIONS);
    }

    /**
     * Gets the course that a given section is tied to
     * @param section The given section
     * @return the course associated with the section
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> getCourse(Map<String, Object> section) {
        if (section == null) return null;
        return (Map<String, Object>) section.get(Constants.ATTR_COURSES);
    }

    /**
     * Get the section from a student section association
     * @param studentSectionAssoc the student section association we are looking at
     * @return the section properties for the given student section association
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> getSection(Map<String, Object> studentSectionAssoc) {
        if (studentSectionAssoc == null) return null;
        return (Map<String, Object>) studentSectionAssoc.get(Constants.ATTR_SECTIONS);
    }

    /**
     * Return the current term based on a given session
     * @param session The session to pull term from
     * @return the term as a string
     */
    private String getTerm(Map<String, Object> session) {
        String term = "";
        try {
            term = session.get(Constants.ATTR_TERM).toString();
        } catch (NullPointerException npe) {
            log.warn(npe.getMessage());
        }
        return term;
    }

    /**
     * Get the school year for a given session
     * @param session the session to pull school year from
     * @return the school year as a string
     */
    private String getSchoolYear(Map<String, Object> session) {
        String schoolYear = "";
        try {
            schoolYear = session.get(Constants.ATTR_SCHOOL_YEAR).toString();
        } catch (NullPointerException npe) {
            log.warn(npe.getMessage());
        }
        return schoolYear;
    }

    /**
     * Get the course title for a given course
     * @param course the course to pull the title from
     * @return the course title as a string
     */
    private String getCourseTitle(Map<String, Object> course) {
        String courseTitle = "";

        try {
            courseTitle = course.get(Constants.ATTR_COURSE_TITLE).toString();
        } catch (NullPointerException npe) {
            log.warn(npe.getMessage());
        }
        return courseTitle;
    }


    /**
     * Return the course transcript for a given section
     * @param studentSectionAssoc The student section association we are looking at
     * @param studentTranscriptAssociations a set of transcripts for a given student
     * @return The transcript that applies to a given section
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> getCourseTranscriptForSection(Map<String, Object> studentSectionAssoc,
                                                              List<Map<String, Object>> studentTranscriptAssociations) {
        String courseId = "";
        Map<String, Object> section = getSection(studentSectionAssoc);
        Map<String, Object> course = getCourse(section);
        if (course != null) {
            courseId = course.get(Constants.ATTR_ID).toString();
        }

        for (Map<String, Object> studentTranscriptAssociation : studentTranscriptAssociations) {
            if (courseId.equals(studentTranscriptAssociation.get(Constants.ATTR_COURSE_ID).toString()))
                return studentTranscriptAssociation;
        }

        return null;
    }

    /**
     * Filters a list of student section associations down by a subject area
     * @param studentSectionAssociations The list of student section associations
     * @param subjectArea The filter to look at
     * @return Filtered list of student section associations
     */
    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> filterBySubjectArea(List<Map<String, Object>> studentSectionAssociations, String subjectArea) {
        if (subjectArea == null) {
            log.warn("Subject Area to match is null!");
            return studentSectionAssociations;
        }

        List<Map<String, Object>> filteredAssociations = new ArrayList<Map<String, Object>>();

        for (Map<String, Object> studentSectionAssociation : studentSectionAssociations) {
            Map<String, Object> section = getSection(studentSectionAssociation);
            Map<String, Object> course = getCourse(section);
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
    public SortedSet<String> getSchoolYears(final String token, Map<String,
            List<GenericEntity>> historicalData) {
        SortedSet<String> results = new TreeSet<String>();

        for (List<GenericEntity> studentTranscripts : historicalData.values()) {
            for (GenericEntity transcript : studentTranscripts) {
                String schoolYear = transcript.getString(Constants.ATTR_SCHOOL_YEAR) + " " + transcript.get(Constants.ATTR_TERM);
                results.add(schoolYear);
            }
        }

        return results;
    }
    
    /**
     * Returns all the gradebook entries for all the students in the given section
     * @param token Security token
     * @param studentIds The students in the section
     * @param selectedSection The section to look for
     * @return
     */
    public Map<String, Map<String, GenericEntity>> getCurrentProgressForStudents(final String token, List<String> studentIds, 
            String selectedSection) {
        Map<String, Map<String, GenericEntity>> results = new HashMap<String, Map<String, GenericEntity>>();

        List<GenericEntity> students = entityManager.getStudentsWithGradebookEntries(token, selectedSection);

        for (GenericEntity student : students) {
            String studentId = student.getString(Constants.ATTR_ID);

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> studentGradebookEntries = (List<Map<String, Object>>) student.get(Constants.ATTR_STUDENT_SECTION_GRADEBOOK);
            if (studentGradebookEntries == null) continue;

            Map<String, GenericEntity> gradebookEntries = new HashMap<String, GenericEntity>();

            for (Map<String, Object> studentGradebookEntry : studentGradebookEntries) {

                // This doesn't cast well - have to manually create Generic Entity from Map<String, Object>
                GenericEntity geStudentGradebookEntry = new GenericEntity();
                geStudentGradebookEntry.putAll(studentGradebookEntry);

                @SuppressWarnings("unchecked")
                Map<String, Object> entries = (Map<String, Object>) geStudentGradebookEntry.get(Constants.ATTR_GRADEBOOK_ENTRIES);
                if (entries == null) continue;

                geStudentGradebookEntry.put(Constants.ATTR_GRADEBOOK_ENTRY_TYPE, entries.get(Constants.ATTR_GRADEBOOK_ENTRY_TYPE));

                gradebookEntries.put(studentGradebookEntry.get(Constants.ATTR_GRADEBOOK_ENTRY_ID).toString(), geStudentGradebookEntry);
                log.debug("Progress data [studentGradebookEntry] {}", studentGradebookEntry);
            }

            results.put(studentId, gradebookEntries);
        }

        return results;
    }

    /**
     * Parses a numeric grade to a double
     * @param numericGrade The numeric grade as a string
     * @return
     */
    protected double parseNumericGrade(Object numericGrade) {
        if (numericGrade != null && numericGrade instanceof Double) {
            return ((Double) numericGrade).doubleValue();
        } else
            return 0;
    }
    
    /**
     * Returns a sorted unique set of gradebook entries(tests)
     * @param gradebookEntryData The gradebook entry data for a section
     * @return
     */
    public SortedSet<GenericEntity> retrieveSortedGradebookEntryList(Map<String, Map<String, GenericEntity>> gradebookEntryData) {
        SortedSet<GenericEntity> list = new TreeSet<GenericEntity>(new DateFulFilledComparator());
        
        //Sorting by entity to be able to handle the introduction of GradebookEntry/type in the future
        //Can be sorted by the keyset if GradebookEntry/type will not be used
        
        //go through and add the tests into one list
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
     * @author srupasinghe
     *
     */
    class DateFulFilledComparator implements Comparator<GenericEntity> {

        @Override
        public int compare(GenericEntity o1, GenericEntity o2) {
            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            
            if (o1.getString(Constants.ATTR_DATE_FULFILLED) != null && o2.getString(Constants.ATTR_DATE_FULFILLED) != null) {
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

}

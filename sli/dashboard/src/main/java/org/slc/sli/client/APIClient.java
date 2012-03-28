package org.slc.sli.client;

import java.util.List;
import java.util.Map;

import org.slc.sli.entity.GenericEntity;

/**
 *
 * An interface to the SLI API.
 * This is meant to be a thin wrapper around API calls. It groups together multiple API calls
 * in some cases, hopefully in a way useful to the rest of the dashboard application.
 *
 */
public interface APIClient {

    public List<GenericEntity> getSchools(final String token, List<String> schoolIds);

    public List<GenericEntity> getStudents(final String token, List<String> studentIds);
    
    public GenericEntity getStudent(String token, String id);

    public List<GenericEntity> getStudentAssessments(final String token, String studentId);

    public List<GenericEntity> getAssessments(final String token, List<String> assessmentIds);

    public List<GenericEntity> getCustomData(final String token, String key);

    public List<GenericEntity> getPrograms(final String token, List<String> studentIds);

    public List<GenericEntity> getStudentAttendance(final String token, String studentId, String start, String end);

    public GenericEntity getParentEducationalOrganization(final String token, GenericEntity educationalOrganization);
    
    /**
     * Returns a list of courses for a given student and query params
     * i.e students/{studentId}/studentCourseAssociations/courses?subejctArea="math"&includeFields=courseId,name
     * 
     * @param token Security token
     * @param studentId The student Id
     * @param params Query params
     * @return
     */
    public List<GenericEntity> getCourses(final String token, final String studentId, Map<String, String> params);
    
    /**
     * Returns a list of studentCourseAssociations for a
     * given student and query params
     * i.e students/{studentId}/studentCourseAssociations?courseId={courseId}&includeFields=finalGrade
     * 
     * @param token Security token
     * @param studentId The student Id
     * @param params Query params
     * @return
     */
    public List<GenericEntity> getStudentTranscriptAssociations(final String token, final String studentId, Map<String, String> params);
    
    /**
     * Returns a list of sections for the given student and params
     * @param token Security token
     * @param studentId The student Id
     * @param params Query params
     * @return
     */
    public List<GenericEntity> getSections(final String token, final String studentId, Map<String, String> params);
    
    /**
     * Returns an entity for the given type, id and params
     * @param token Security token
     * @param type Type of the entity 
     * @param id The id of the entity
     * @param params param map
     * @return
     */
    public GenericEntity getEntity(final String token, final String type, final String id, Map<String, String> params);
    
    /**
     * Returns a list of student grade book entries for a given student and params
     * @param token Security token
     * @param studentId The student Id
     * @param params param map
     * @return
     */
    public List<GenericEntity> getStudentSectionGradebookEntries(final String token, final String studentId, Map<String, String> params);

    public GenericEntity getTeacherForSection(String sectionId, String token);
    
    public GenericEntity getHomeRoomForStudent(String studentId, String token);

    public GenericEntity getSession(String token, String sessionId);

    public List<GenericEntity> getSessionsByYear(String token, String schoolYear);
}

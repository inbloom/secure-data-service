package org.slc.sli.client;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.slc.sli.entity.ConfigMap;
import org.slc.sli.entity.GenericEntity;

/**
 *
 * An interface to the SLI API.
 * This is meant to be a thin wrapper around API calls. It groups together multiple API calls
 * in some cases, hopefully in a way useful to the rest of the dashboard application.
 *
 */
public interface APIClient {

    public GenericEntity getStaffInfo(String token);

    public ConfigMap getEdOrgCustomData(String token, String id);

    public void putEdOrgCustomData(String token, String id, ConfigMap configMap);

    public List<GenericEntity> getSchools(final String token, List<String> schoolIds);

    public List<GenericEntity> getStudents(final String token, Collection<String> studentIds);

    public GenericEntity getStudent(String token, String id);

    public List<GenericEntity> getStudentAssessments(final String token, String studentId);

    public List<GenericEntity> getAssessments(final String token, List<String> assessmentIds);

    public List<GenericEntity> getStudentAttendance(final String token, String studentId, String start, String end);

    public GenericEntity getParentEducationalOrganization(final String token, GenericEntity educationalOrganization);

    public List<GenericEntity> getParentEducationalOrganizations(final String token,
            List<GenericEntity> educationalOrganizations);

    public List<GenericEntity> getStudentEnrollment(final String token, GenericEntity student);

    public List<GenericEntity> getStudentsWithGradebookEntries(final String token, final String sectionId);

    public List<GenericEntity> getStudentsWithSearch(final String token, String firstName, String lastName);

    public GenericEntity getStudentWithOptionalFields(final String token, final String studentId,
            List<String> optionalFields);

    /**
     * Returns a list of courses for a given student and query params
     * i.e students/{studentId}/studentCourseAssociations/courses?subejctArea="math"&includeFields=
     * courseId,name
     *
     * @param token
     *            Security token
     * @param studentId
     *            The student Id
     * @param params
     *            Query params
     * @return
     */
    public List<GenericEntity> getCourses(final String token, final String studentId, Map<String, String> params);

    /**
     * Returns a list of studentCourseAssociations for a
     * given student and query params
     * i.e
     * students/{studentId}/studentCourseAssociations?courseId={courseId}&includeFields=finalGrade
     *
     * @param token
     *            Security token
     * @param studentId
     *            The student Id
     * @param params
     *            Query params
     * @return
     */
    public List<GenericEntity> getStudentTranscriptAssociations(final String token, final String studentId,
            Map<String, String> params);

    /**
     * Returns a list of sections for the given student and params
     *
     * @param token
     *            Security token
     * @param studentId
     *            The student Id
     * @param params
     *            Query params
     * @return
     */
    public List<GenericEntity> getSections(final String token, final String studentId, Map<String, String> params);

    /**
     * Returns an entity for the given type, id and params
     *
     * @param token
     *            Security token
     * @param type
     *            Type of the entity
     * @param id
     *            The id of the entity
     * @param params
     *            param map
     * @return
     */
    public GenericEntity getEntity(final String token, final String type, final String id, Map<String, String> params);

    /**
     * Returns entity for the given type, id and params
     *
     * @param token
     *            Security token
     * @param type
     *            Type of the entity
     * @param id
     *            The id of the entity
     * @param params
     *            param map
     * @return
     */
    public List<GenericEntity> getEntities(final String token, final String type, final String id,
            Map<String, String> params);

    /**
     * Returns a list of student grade book entries for a given student and params
     *
     * @param token
     *            Security token
     * @param studentId
     *            The student Id
     * @param params
     *            param map
     * @return
     */
    public List<GenericEntity> getStudentSectionGradebookEntries(final String token, final String studentId,
            Map<String, String> params);

    /**
     * Return a list of students for a section with the optional fields
     *
     * @param token
     *            Security token
     * @param sectionId
     *            The sectionId
     * @param studentIds
     *            The studentIds (this is only here to get MockClient working)
     * @return
     */
    public List<GenericEntity> getStudents(String token, String sectionId, List<String> studentIds);

    public GenericEntity getTeacherForSection(String sectionId, String token);

    public GenericEntity getHomeRoomForStudent(String studentId, String token);

    public GenericEntity getSession(String token, String sessionId);

    public List<GenericEntity> getSessions(String token);

    public List<GenericEntity> getSessionsByYear(String token, String schoolYear);

    /**
     * Return a url with the sortBy parameter
     *
     * @param url
     * @param sortBy
     * @return
     */
    public String sortBy(String url, String sortBy);

    /**
     * Return a url with the sortBy and sortOrder parameter
     *
     * @param url
     * @param sortBy
     * @param sortOrder
     * @return
     */
    public String sortBy(String url, String sortBy, String sortOrder);

    public GenericEntity getAcademicRecord(String token, Map<String, String> params);

    /**
     * Return parent(s) entity that is associated to a student
     * @param token
     * @param studentId
     * @return
     */
    public List<GenericEntity> getParentsForStudent(String token, String studentId);

    /**
     * Return courses and sections for a school
     * @param token
     * @param schoolId
     * @return
     */
    List<GenericEntity> getCoursesSectionsForSchool(String token, String schoolId);
}

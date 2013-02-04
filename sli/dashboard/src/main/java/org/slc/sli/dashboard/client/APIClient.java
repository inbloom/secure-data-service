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


package org.slc.sli.dashboard.client;

import java.util.List;
import java.util.Map;

import org.slc.sli.dashboard.entity.ConfigMap;
import org.slc.sli.dashboard.entity.GenericEntity;

/**
 *
 * The application SDK interface to the SLI API.
 * This is meant to be a thin wrapper around API calls. It groups together multiple API calls
 * in some cases, hopefully in a way useful to the rest of the application.
 *
 */
public interface APIClient {

    /**
     * Get the SLI configured grace period for historical access
     *
     * @return
     */
//    public String getGracePeriod();

    /**
     * Get a resource entity of a specified type which is identified by id and enriched using
     * optional parameters
     *
     * @param token
     * @param type
     * @param id
     * @param params
     * @return
     */
    public GenericEntity getEntity(String token, String type, String id, Map<String, String> params);

    /**
     * Get a list of resource entities of a specified type which are identified by a list of ids and
     * enriched using optional parameters
     *
     * @param token
     * @param type
     * @param ids
     * @param params
     * @return
     */
    public List<GenericEntity> getEntities(String token, String type, String ids, Map<String, String> params);

    /**
     * Get user's home entity
     *
     * @param token
     * @return
     */
    public GenericEntity getHome(String token);

    /**
     * Get the user's unique identifier
     *
     * @param token
     * @return
     */
    public String getId(String token);

    /**
     * Get EdOrg custom data
     *
     * @param token
     * @param id
     * @return
     */
    public ConfigMap getEdOrgCustomData(String token, String id);

    /**
     * Store EdOrg custom data
     *
     * @param token
     * @param id
     * @param configMap
     */
    public void putEdOrgCustomData(String token, String id, ConfigMap configMap);

    /**
     * Get a list of educational organizations using a list of ids
     *
     * @param token
     * @param ids
     * @param params
     * @return
     */
    public List<GenericEntity> getEducationalOrganizations(String token, List<String> ids, Map<String, String> params);

    /**
     * Get education organizations for staff member identified by id
     *
     * @param token
     * @param staffId
     * @return
     */
    public List<GenericEntity> getEducationOrganizationsForStaff(String token, String staffId);

    /**
     * Get an educational organization identified by id
     *
     * @param token
     * @param id
     * @return
     */
    public GenericEntity getEducationalOrganization(String token, String id);

    /**
     * Get education organizations for staff member identified by id and matching organization
     * category or first if not specified
     *
     * @param token
     * @param staffId
     * @param organizationCategory
     * @return
     */
    public GenericEntity getEducationOrganizationForStaff(String token, String staffId, String organizationCategory);

    /**
     * Get parent educational organizations for the supplied edOrgs
     *
     * @param token
     * @param educationalOrganizations
     * @return
     */
    public List<GenericEntity> getParentEducationalOrganizations(String token,
            List<GenericEntity> educationalOrganizations);

    /**
     * Get parent educational organization for the supplied edOrg
     *
     * @param token
     * @param educationalOrganization
     * @return
     */
    public GenericEntity getParentEducationalOrganization(String token, GenericEntity educationalOrganization);

    /**
     * Get a list of all schools depending upon user role
     *
     * @param token
     * @param ids
     * @return
     */
    public List<GenericEntity> getSchools(String token, List<String> ids);

    /**
     * Get a list of all associated schools
     *
     * @param token
     * @return
     */
    public List<GenericEntity> getMySchools(String token);

    /**
     * Get a list of schools using a list of ids
     *
     * @param token
     * @param ids
     * @param params
     * @return
     */
    public List<GenericEntity> getSchools(String token, List<String> ids, Map<String, String> params);

    /**
     * Get a school identified by id
     *
     * @param token
     * @param id
     * @return
     */
    public GenericEntity getSchool(String token, String id);

    /**
     * Get a list of all sessions
     *
     * @param token
     * @param params
     * @return
     */
    public List<GenericEntity> getSessions(String token, String schoolId, Map<String, String> params);

    /**
     * Get a list of sessions using a list of ids
     *
     * @param token
     * @param ids
     * @param params
     * @return
     */
    public List<GenericEntity> getSessions(String token, List<String> ids, Map<String, String> params);

    /**
     * Get a list of sessions for the specified school year
     *
     * @param token
     * @param schoolYear
     * @return
     */
    public List<GenericEntity> getSessionsForYear(String token, String schoolYear);

    /**
     * Get a session identified by id
     *
     * @param token
     * @param id
     * @return
     */
    public GenericEntity getSession(String token, String id);

    /**
     * Get a list of all sections
     *
     * @param token
     * @param params
     * @return
     */
    public List<GenericEntity> getSections(String token, Map<String, String> params);

    /**
     * Get a list of sections using a list of ids
     *
     * @param token
     * @param ids
     * @param params
     * @return
     */
    public List<GenericEntity> getSections(String token, List<String> ids, Map<String, String> params);

    /**
     * Get all sections for a non-Educator
     *
     * @param token
     * @param params
     * @return
     */
    public List<GenericEntity> getSectionsForNonEducator(String token, Map<String, String> params);

    /**
     * Get all sections for a Teacher
     *
     * @param token
     * @param teacherId
     * @param params
     * @return
     */
    public List<GenericEntity> getSectionsForTeacher(String teacherId, String token, Map<String, String> params);

    /**
     * Get a list of sections for the given student id
     *
     * @param token
     * @param studentId
     * @param params
     * @return
     */
    public List<GenericEntity> getSectionsForStudent(final String token, final String studentId,
            Map<String, String> params);

    /**
     * Get a section identified by id
     *
     * @param token
     * @param id
     * @return
     */
    public GenericEntity getSection(String token, String id);

    /**
     * Get student home room information
     *
     * @param token
     *            Security token
     * @param studentId
     * @return
     */
    public GenericEntity getSectionHomeForStudent(String token, String studentId);

    /**
     * Get a list of courses using a list of ids
     *
     * @param token
     * @param ids
     * @param params
     *            Query params
     * @return
     */
    //public List<GenericEntity> getCourses(final String token, final String studentId, Map<String, String> params);

    /**
     * Returns a list of studentCourseAssociations for a
     * given student and query params
     * i.e
     * students/{studentId}/studentCourseAssociations?courseId={courseId}&includeFields=finalGrade
     *
     */
    public List<GenericEntity> getCourses(String token, List<String> ids, Map<String, String> params);

    /**
     * Get a list of courses for the given student id
     *
     * @param token
     *            Security token
     * @param studentId
     *            The student Id
     * @param params
     *            Query params
     * @return
     */
    //public List<GenericEntity> getCourseTranscripts(final String token, final String studentId,
    //        Map<String, String> params);


    /**
     * Returns a list of sections for the given student and params
     *
     */
    public List<GenericEntity> getCoursesForStudent(String token, String studentId, Map<String, String> params);

    /**
     * Get a list of transcripts for the given student id
     *
     * @param token
     *            Security token
     * @param studentId
     *            The student Id
     * @param params
     *            Query params
     * @return
     */
    //public List<GenericEntity> getSections(final String token, final String studentId, Map<String, String> params);

    /**
     * Returns an entity for the given type, id and params
     *
     */
    public List<GenericEntity> getTranscriptsForStudent(String token, String studentId, Map<String, String> params);

    /**
     * Get a course identified by id
     *
     * @param token
     * @param id
     * @return
     */
    public GenericEntity getCourse(String token, String id);

    /**
     * Get a list of staff members using a list of ids
     *
     * @param token
     * @param ids
     * @param params
     * @return
     */
    public List<GenericEntity> getStaff(String token, List<String> ids, Map<String, String> params);

    /**
     * Get staff member information identified by id
     *
     * @param token
     * @param id
     * @return
     */
    public GenericEntity getStaff(String token, String id);

    /**
     * Get staff member information identified by id along with specified education organization of
     * category
     *
     * @param token
     *            Security token
     * @param type
     *            Type of the entity
     * @param id
     *            The id of the entity
     * @param organizationCategory
     * @return
     */
    public GenericEntity getStaffWithEducationOrganization(String token, String id, String organizationCategory);

    /**
     * Get a list of parents for the given student id
     *
     * @param token
     * @param studentId
     * @param params
     * @return
     */
    public List<GenericEntity> getParentsForStudent(String token, String studentId, Map<String, String> params);


    /**
     * Returns entity for the given type, id and params
     *
     */
    public List<GenericEntity> getTeachers(String token, List<String> ids, Map<String, String> params);

    /**
     * Get a teacher identified by id
     *
     * @param token
     *            Security token
     * @param type
     *            Type of the entity
     * @param id
     *            The id of the entity
     * @return
     */
    public GenericEntity getTeacher(String token, String id);


    /**
     * Similar to getTeacher, but also returns all the sections associated with the teacher.
     * @param token
     * @param id
     * @return
     */
    public GenericEntity getTeacherWithSections(String token, String id);

    /**
     * Get the teacher for a specified section
     *
     * @param token
     * @param sectionId
     * @return
     */
    public GenericEntity getTeacherForSection(String token, String sectionId);

    /**
     * Get a list of teachers for a specific school
     *
     * @param token
     * @param schoolId
     * @return
     */
    public List<GenericEntity> getTeachersForSchool(String schoolId, String token);

    /**
     * Returns a list of student grade book entries for a given student and params
     *
     */
    public List<GenericEntity> getStudents(String token, Map<String, String> params);

    /**
     * Get a list of students specified by a list of ids
     *
     * @param token
     *            Security token.
     * @param ids
     *            Theids to search for.
     * @param params
     *            param map
     * @return
     */
    public List<GenericEntity> getStudents(String token, List<String> ids, Map<String, String> params);

    /**
     * Get a list of students assigned to the specified section
     *
     * @param token
     *            Security token
     * @param sectionId
     *            The sectionId
     * @return
     */
    public List<GenericEntity> getStudentsForSection(String token, String sectionId);

    /**
     * Get a list of students assigned to the specified school for a given params
     * @param token
     * @param schoolId
     * @param params
     * @return
     */
    public List<GenericEntity> getStudentsForSchool(String token, String schoolId, Map<String, String> params);

    /**
     * Get a list of students using name search
     *
     * @param token
     * @param firstName
     * @param lastName
     * @return
     */
    public List<GenericEntity> getStudentsWithSearch(String token, String firstName, String lastName, String schoolId);

    /**
     * Get a list of students in the specified section along with gradebook entries
     *
     * @param token
     * @param sectionId
     * @return
     */
    public List<GenericEntity> getStudentsForSectionWithGradebookEntries(String token, String sectionId);

    /**
     * Get a student identified by id
     *
     * @param token
     * @param id
     * @return
     */
    public GenericEntity getStudent(String token, String id);

    /**
     * Get a student identified by id including specified optional information
     *
     * @param token
     * @param id
     * @param optionalFields
     * @return
     */
    public GenericEntity getStudentWithOptionalFields(String token, String id, List<String> optionalFields);

    /**
     * Get a list of school enrollments for the given student id
     *
     * @param token
     * @param student
     * @return
     */
    public List<GenericEntity> getEnrollmentForStudent(String token, String studentId);

    /**
     * Get a list of attendances for the given student id
     *
     * @param token
     * @param studentId
     * @param params
     * @return
     */
    public List<GenericEntity> getAttendanceForStudent(String token, String studentId, Map<String, String> params);

    /**
     * Get a list of academic records for the given student id
     *
     * @param token
     * @param studentId
     * @param params
     * @return
     */
    public List<GenericEntity> getAcademicRecordsForStudent(String token, String studentId, Map<String, String> params);

    /**
     * Get a list of assessments using a list of ids
     *
     * @param token
     * @param ids
     * @param params
     * @return
     */
    public List<GenericEntity> getAssessments(String token, List<String> ids, Map<String, String> params);

    /**
     * Get a list of assessments for the given student id
     *
     * @param token
     * @param studentId
     * @return
     */
    public List<GenericEntity> getAssessmentsForStudent(String token, String studentId);

    /**
     * Get an assessment identified by id
     *
     * @param token
     * @param id
     * @return
     */
    public GenericEntity getAssessment(String token, String id);

    /**
     * Return courses and sections for a school
     * @param token
     * @param schoolId
     * @return
     */
    List<GenericEntity> getCoursesSectionsForSchool(String token, String schoolId);

    public List<GenericEntity> getCourseSectionMappings(List<GenericEntity> sections, String schoolId, String token);

    public List<GenericEntity> readEntityList(String token, String url);

    public GenericEntity readEntity(String token, String url);

    public List<GenericEntity> readEntityList(String token, List<String> ids, Map<String, String> params,
            String sections);

    public String getTeacherIdForSection(String token, String sectionId);

    public void getTeacherIdForSections(String token, List<String> sectionId, Map<String, String> teacherIdCache);

    /**
     * Search students based on query string
     * @param token
     * @param query
     * @param params
     * @return list of matching student entities
     */
    public List<GenericEntity> searchStudents(String token, String query, Map<String, String> params);
}

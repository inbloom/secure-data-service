/*
 * Copyright 2012 Shared Learning Collaborative, LLC
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slc.sli.dashboard.client.SDKConstants;
import org.slc.sli.dashboard.entity.GenericEntity;
import org.slc.sli.dashboard.entity.util.ContactSorter;
import org.slc.sli.dashboard.entity.util.GenericEntityEnhancer;
import org.slc.sli.dashboard.entity.util.ParentsSorter;
import org.slc.sli.dashboard.util.Constants;
import org.slc.sli.dashboard.util.DashboardException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * EntityManager which engages with the API client to build "logical" entity graphs to be leveraged
 * by the MVC framework.
 *
 * @author Sravan Vankina
 * @author David Wu
 * @author Robert Bloh
 *
 */
@Component
public class EntityManager extends ApiClientManager {

    private static Logger log = LoggerFactory.getLogger(EntityManager.class);

    public EntityManager() {

    }

    /**
     * Get the list of school entities identified by the school id list and authorized for the
     * security token
     *
     * @param token
     *            - the principle authentication token
     * @param schoolIds
     *            - the school id list
     * @return schoolList
     *           - the school entity list
     */
    public List<GenericEntity> getSchools(final String token, List<String> schoolIds) {
        return getApiClient().getSchools(token, schoolIds);
    }

    /**
<<<<<<< HEAD
     * Get a single student entity with optional fields embedded.
=======
     * Returns a single student entity with optional fields embedded.
>>>>>>> master
     *
     * @param token
     *            - authentication token
     * @param studentId
     *            - the student we are looking for
     * @param optionalFields
     *            - a list of "optional views" that we are appending to the student, which could be
     *            related to attendance, assessments, etc..
     * @return a single student entity with selected optional views embedded
     */
    public GenericEntity getStudentWithOptionalFields(final String token, String studentId, List<String> optionalFields) {
        return getApiClient().getStudentWithOptionalFields(token, studentId, optionalFields);
    }

    /**
     * Get the list of student entities identified by the student id list and authorized for the
     * security token
     *
     * @param token
     *            - the principle authentication token
     * @param studentIds
     *            - the student id list
     * @return studentList
     *            - the student entity list
     */
    public List<GenericEntity> getStudents(final String token, String sectionId) {
        return getApiClient().getStudentsForSection(token, sectionId);
    }

    /**
     * Get the student entity identified by the student id and authorized for the security token
     *
     * @param token
     *            - the principle authentication token
     * @param studentId
     *            - the student id
     * @return student
     *         - the student entity
     */
    public GenericEntity getStudent(final String token, String studentId) {
        return getApiClient().getStudent(token, studentId);
    }

    /**
     * Get the student entity along with additional info needed for CSI panel
     *
     * @param token
     *            - the principle authentication token
     * @param studentId
     *            - the student id
     * @return student
     *            - the student entity
     */
    public GenericEntity getStudentForCSIPanel(final String token, String studentId) {
        GenericEntity student = getStudent(token, studentId);
        if (student == null) {
            throw new DashboardException("Unable to retrieve data for the requested ID");
        }
        student = ContactSorter.sort(student);
        student = GenericEntityEnhancer.enhanceStudent(student);

        GenericEntity section = getApiClient().getSectionHomeForStudent(token, studentId);

        if (section != null) {
            student.put(Constants.ATTR_SECTION_ID, section.get(Constants.ATTR_UNIQUE_SECTION_CODE));
            GenericEntity teacher = getApiClient().getTeacherForSection(token, section.getString(Constants.ATTR_ID));

            if (teacher != null) {
                @SuppressWarnings("unchecked")
                Map<String, Object> teacherName = (Map<String, Object>) teacher.get(Constants.ATTR_NAME);
                if (teacherName != null) {
                    student.put(Constants.ATTR_TEACHER_NAME, teacherName);
                }
            }
        }

        List<GenericEntity> studentEnrollment = getApiClient().getEnrollmentForStudent(token, student.getId());
        student.put(Constants.ATTR_STUDENT_ENROLLMENT, studentEnrollment);

        List<GenericEntity> parents = getApiClient().getParentsForStudent(token, studentId, null);

        //sort parent Contact if there are more than 2.
        if (parents != null && parents.size() > 1) {
            ParentsSorter.sort(parents);
        }

        for (GenericEntity parentsContact : parents) {
            ContactSorter.sort(parentsContact);
        }

        student.put(Constants.ATTR_PARENTS, parents);
        return student;
    }

    /**
     * Retrieves a list of attendance objects for a single student.
     *
     * @param token
     *            - the current authentication token
     * @param studentId
     *            - the student that you want to get your attendance objects for
     * @return a list of attendance objects
     */
    public List<GenericEntity> getAttendance(final String token, final String studentId, final String start,
            final String end) {
        Map<String, String> params = new HashMap<String, String>();
        if (start != null && start.length() > 0) {
            params.put(SDKConstants.PARAM_EVENT_DATE + ">", "" + start);
            params.put(SDKConstants.PARAM_EVENT_DATE + "<", "" + end);
        }
        return getApiClient().getAttendanceForStudent(token, studentId, params);
    }

    /**
     * Returns a list of courses for a given student and params
     *
     * @param token
     *            - the security token
     * @param studentId
     *            - the student id
     * @param params
     *            - param map
     * @return
     */
    public List<GenericEntity> getCourses(final String token, final String studentId, Map<String, String> params) {
        return getApiClient().getCoursesForStudent(token, studentId, params);
    }

    /**
     * Returns a list of student grade book entries for a given student and params
     *
     * @param token
     *            - the security token
     * @param studentId
     *            - the student id
     * @param params
     *            - param map
     * @return
     */
    public List<GenericEntity> getStudentSectionGradebookEntries(final String token, final String studentId,
            Map<String, String> params) {
        return getApiClient().getCoursesForStudent(token, studentId, params);
    }

    public List<GenericEntity> getStudentsWithGradebookEntries(final String token, final String sectionId) {
        return getApiClient().getStudentsForSectionWithGradebookEntries(token, sectionId);
    }

    /**
     * Returns an entity for the given type, id and params
     *
     * @param token
     *            - security token
     * @param type
     *            - type of the entity
     * @param id
     *            - the id of the entity
     * @param params
     *            - param map
     * @return
     */
    public GenericEntity getEntity(final String token, final String type, final String id, Map<String, String> params) {
        return getApiClient().getEntity(token, type, id, params);
    }


    /**
     * Returns a list of students, which match the search parameters
     *
     * @param token
     * @param firstName
     * @param lastName
     * @return
     */
    public List<GenericEntity> getStudentsFromSearch(String token, String firstName, String lastName) {
        return getApiClient().getStudentsWithSearch(token, firstName, lastName);
    }

    public GenericEntity getSession(String token, String sessionId) {
        return getApiClient().getSession(token, sessionId);
    }

    // TODO Change body of created methods use File | Settings | File Templates.
    public List<GenericEntity> getSessionsByYear(String token, String schoolYear) {
        return getApiClient().getSessionsForYear(token, schoolYear);
    }

    public GenericEntity getAcademicRecord(String token, String studentId, Map<String, String> params) {
        List<GenericEntity> entities = getApiClient().getAcademicRecordsForStudent(token, studentId, params);

        if (entities == null || entities.size() <= 0) {
            return null;
        }

        return entities.get(0);
    }
    
    
    /**
     * Retrieve section, and populate with additional data for teacher and course, required for section profile panel
     * @param token
     * @param sectionId
     * @return
     */
    public GenericEntity getSectionForProfile(String token, String sectionId) {
    	GenericEntity section =  getApiClient().getSection(token, sectionId);
    	
    	
    	//Retrieve teacher of record for the section, and add the teacher's name to the section entity.
    	GenericEntity teacher = getApiClient().getTeacherForSection(token, section.getString(Constants.ATTR_ID));
        if (teacher != null) {
            @SuppressWarnings("unchecked")
            Map<String, Object> teacherName = (Map<String, Object>) teacher.get(Constants.ATTR_NAME);
            if (teacherName != null) {
                section.put(Constants.ATTR_TEACHER_NAME, teacherName);
            }
        }
    	
    
       List<GenericEntity> sections = new ArrayList<GenericEntity>();
       sections.add(section);
       
       //Retrieve courses for the section, and add the course name and subject area to the section entity.
       List<GenericEntity> courses = getApiClient().getCourseSectionMappings(sections, token);

       if (courses != null && courses.size() > 0) {
    	   GenericEntity course = courses.get(0);
    	   section.put(Constants.ATTR_COURSE_TITLE, course.get(Constants.ATTR_COURSE_TITLE));
    	   section.put(Constants.ATTR_SUBJECTAREA, course.get(Constants.ATTR_SUBJECTAREA));
       }
       
        return section;
    }
    
}

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import org.slc.sli.api.client.Link;
import org.slc.sli.dashboard.client.SDKConstants;
import org.slc.sli.dashboard.entity.GenericEntity;
import org.slc.sli.dashboard.entity.util.ContactSorter;
import org.slc.sli.dashboard.entity.util.GenericEntityEnhancer;
import org.slc.sli.dashboard.entity.util.ParentsSorter;
import org.slc.sli.dashboard.util.Constants;
import org.slc.sli.dashboard.util.DashboardException;
import org.slc.sli.dashboard.util.ExecutionTimeLogger;

/**
 * EntityManager which engages with the API client to build "logical" entity
 * graphs to be leveraged by the MVC framework.
 * 
 * @author Sravan Vankina
 * @author David Wu
 * @author Robert Bloh
 * 
 */
@Component
public class EntityManager extends ApiClientManager {
    
    private static final Logger LOG = LoggerFactory.getLogger(EntityManager.class);
    
    /**
     * Get the list of school entities identified by the school id list and
     * authorized for the security token
     * 
     * @param token
     *            - the principle authentication token
     * @param schoolIds
     *            - the school id list
     * @return schoolList - the school entity list
     */
    public List<GenericEntity> getSchools(final String token, List<String> schoolIds) {
        return getApiClient().getSchools(token, schoolIds);
    }
    
    /**
     * Get a single student entity with optional fields embedded.
     * 
     * @param token
     *            - authentication token
     * @param studentId
     *            - the student we are looking for
     * @param optionalFields
     *            - a list of "optional views" that we are appending to the
     *            student, which could be related to attendance, assessments,
     *            etc..
     * @return a single student entity with selected optional views embedded
     */
    public GenericEntity getStudentWithOptionalFields(final String token, String studentId, List<String> optionalFields) {
        return getApiClient().getStudentWithOptionalFields(token, studentId, optionalFields);
    }
    
    /**
     * Get the list of student entities identified by the student id list and
     * authorized for the security token
     * 
     * @param token
     *            - the principle authentication token
     * @param studentIds
     *            - the student id list
     * @return studentList - the student entity list
     */
    public List<GenericEntity> getStudents(final String token, String sectionId) {
        return getApiClient().getStudentsForSection(token, sectionId);
    }
    
    /**
     * Get the student entity identified by the student id and authorized for
     * the security token
     * 
     * @param token
     *            - the principle authentication token
     * @param studentId
     *            - the student id
     * @return student - the student entity
     */
    public GenericEntity getStudent(final String token, String studentId) {
        return getApiClient().getStudent(token, studentId);
    }
    
    /**
     * Get the teacher entity identified by the teacher id and authorized for
     * the security token
     * 
     * @param token
     *            - the principle authentication token
     * @param tacherId
     *            - the teacher id
     * @return teacher - the teacher entity
     */
    public GenericEntity getTeacher(final String token, String teacherId) {
        return getApiClient().getTeacher(token, teacherId);
    }
    
    /**
     * Get the student entity along with additional info needed for CSI panel
     * 
     * @param token
     *            - the principle authentication token
     * @param studentId
     *            - the student id
     * @return student - the student entity
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
        
        List<GenericEntity> parents = getApiClient().getParentsForStudent(token, studentId,
                new HashMap<String, String>());
        
        // sort parent Contact if there are more than 2.
        if (parents != null && parents.size() > 1) {
            ParentsSorter.sort(parents);
        }
        
        if (parents != null) {
            for (GenericEntity parentsContact : parents) {
                ContactSorter.sort(parentsContact);
            }
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
     * Retrieves a list of attendance objects for a single student.
     *
     * @param token
     *            - the current authentication token
     * @param studentId
     *            - the student that you want to get your attendance objects for
     * @return a list of attendance objects
     */
    public List<GenericEntity> getAttendanceForSchool(final String token, final String studentId, String schoolId, final String start,
            final String end) {
        Map<String, String> params = new HashMap<String, String>();
        if (start != null && start.length() > 0) {
            params.put(SDKConstants.PARAM_EVENT_DATE + ">", "" + start);
            params.put(SDKConstants.PARAM_EVENT_DATE + "<", "" + end);
        }
        params.put(SDKConstants.PARAM_SCHOOL_ID, schoolId);
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
     * Returns a list of student grade book entries for a given student and
     * params
     * 
     * @param token
     *            - the security token
     * @param studentId
     *            - the student id
     * @param params
     *            - param map
     * @return
     */
    public List<GenericEntity> getStudentGradebookEntries(final String token, final String studentId,
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
    public List<GenericEntity> getEntities(final String token, final String type, final List<String> ids,
            Map<String, String> params) {
        return getApiClient().readEntityList(token, ids, params, type);
    }
    
    /**
     * Returns a list of students, which match the search parameters
     * 
     * @param token
     * @param firstName
     * @param lastName
     * @return
     */
    public List<GenericEntity> getStudentsFromSearch(String token, String firstName, String lastName, String schoolId) {
        return getApiClient().getStudentsWithSearch(token, firstName, lastName, schoolId);
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
    
    public List<GenericEntity> getAcademicRecords(String token, String studentId, Map<String, String> params) {
        List<GenericEntity> entities = getApiClient().getAcademicRecordsForStudent(token, studentId, params);
        
        if (entities == null || entities.size() <= 0) {
            return null;
        }
        
        return entities;
    }
    
    /**
     * Retrieve section, and populate with additional data for teacher and
     * course, required for section profile panel
     * 
     * @param token
     * @param sectionId
     * @return
     */
    public GenericEntity getSectionForProfile(String token, String sectionId, Map<String, GenericEntity> cache,
            Map<String, String> teacherIdCache) {
        
        Map<String, GenericEntity> localCache = cache;
        Map<String, String> localTeacherIdCache = teacherIdCache;
        
        if (localCache == null || localTeacherIdCache == null) {
            localCache = new HashMap<String, GenericEntity>();
            localTeacherIdCache = new HashMap<String, String>();
        }
        
        GenericEntity section = localCache.get(sectionId);
        if (section == null) {
            section = getApiClient().getSection(token, sectionId);
            localCache.put(sectionId, section);
        }
        
        if (section == null) {
            return null;
        }
        
        // Retrieve teacher of record for the section, and add the teacher's
        // name to the section
        // entity.
        String teacherId = localTeacherIdCache.get(sectionId);
        if (teacherId == null) {
            teacherId = getApiClient().getTeacherIdForSection(token, sectionId);
            if (teacherId == null) {
                teacherId = "empty";
            }
            localTeacherIdCache.put(sectionId, teacherId);
        }
        
        GenericEntity teacher = null;
        if (teacherId != null && !teacherId.equals("empty")) {
            teacher = localCache.get(teacherId);
            
            if (teacher == null) {
                teacher = getApiClient().getTeacher(token, teacherId);
                localCache.put(teacherId, teacher);
            }
        }
        
        if (teacher != null) {
            @SuppressWarnings("unchecked")
            Map<String, Object> teacherName = (Map<String, Object>) teacher.get(Constants.ATTR_NAME);
            if (teacherName != null) {
                section.put(Constants.ATTR_TEACHER_NAME, teacherName);
            }
        }
        
        List<Link> links = section.getLinks();
        
        List<String> entitieKeys = new ArrayList<String>(links.size());
        for (Link link : links) {
            String key = link.getResourceURL().toString();
            GenericEntity entity = localCache.get(key);
            if (entity == null) {
                // getApiClient().readEntity(token, key);
                // cache.put(key, entity);
                entitieKeys.add(key);
            }
        }
        
        String courseOfferingId = section.get("courseOfferingId").toString();
        GenericEntity courseOffering = localCache.get(courseOfferingId);
        if (courseOffering == null) {
            courseOffering = getCourseOffering(token, courseOfferingId);
            localCache.put(courseOfferingId, courseOffering);
        }
        
        if (courseOffering != null) {
            String courseId = courseOffering.getString(Constants.ATTR_COURSE_ID);
            if (courseId != null) {
                GenericEntity course;
                if (localCache.containsKey(courseId)) {
                    course = localCache.get(courseId);
                } else {
                    course = getApiClient().getCourse(token, courseId);
                    localCache.put(courseId, course);
                }
                
                if (course != null) {
                    section.put(Constants.ATTR_COURSE_TITLE, course.get(Constants.ATTR_COURSE_TITLE));
                    section.put(Constants.ATTR_SUBJECTAREA, course.get(Constants.ATTR_SUBJECTAREA));
                }
            }
        }
        
        return section;
    }
    
    private GenericEntity getCourseOffering(String token, String id) {
        // get Teacher information
        return getApiClient().readEntity(token, "/" + Constants.ATTR_COURSE_OFFERINGS + "/" + id);
    }
    
    private List<GenericEntity> cacheStudent(String token, List<Link> links, Map<String, GenericEntity> cache,
            Map<String, List<GenericEntity>> entityListCache, Map<String, String> teacherIdCache) {
        
        List<GenericEntity> studentSectionAssociations = null;
        // create a method that receive links and return List of generic
        // entities for the link specified
        for (Link link : links) {
            
            // If link is getStudentSectionAssociations.
            if (link.getLinkName().equals(Constants.GET_STUDENT_SECTION_ASSOCIATIONS)) {
                
                studentSectionAssociations = entityListCache.get(link.getResourceURL().toString());
                if (studentSectionAssociations == null) {
                    studentSectionAssociations = getApiClient().readEntityList(token,
                            link.getResourceURL().toString() + "?limit=0");
                    entityListCache.put(link.getResourceURL().toString(), studentSectionAssociations);
                }
                
                break;
            }
        }
        
        // Navigate links to retrieve course and subject.
        List<String> sectionIds = new ArrayList<String>();
        
        List<GenericEntity> entities;
        
        if (studentSectionAssociations != null) {
            // collect section ids
            for (GenericEntity studentSectionAssociation : studentSectionAssociations) {
                sectionIds.add(studentSectionAssociation.getString(Constants.ATTR_SECTION_ID));
            }
            
            entities = getApiClient().readEntityList(token, sectionIds, null, SDKConstants.SECTIONS_ENTITY);
            for (GenericEntity entity : entities) {
                cache.put(entity.getId(), entity);
            }
        }
        
        List<String> teacherIds = new ArrayList<String>(sectionIds.size());
        List<String> courseOfferingIds = new ArrayList<String>();
        // Iterate over associations
        getApiClient().getTeacherIdForSections(token, sectionIds, teacherIdCache);
        for (GenericEntity studentSectionAssociation : studentSectionAssociations) {
            GenericEntity section = cache.get(studentSectionAssociation.getString(Constants.ATTR_SECTION_ID));
            
            String teacherId = teacherIdCache.get(section.getString(Constants.ATTR_ID));
            if (teacherId == null) {
                teacherId = getApiClient().getTeacherIdForSection(token, section.getString(Constants.ATTR_ID));
                if (teacherId == null) {
                    teacherId = "empty";
                }
                teacherIdCache.put(section.getString(Constants.ATTR_ID), teacherId);
            }
            
            if (teacherId != null && !teacherId.equals("empty")) {
                teacherIds.add(teacherId);
            }
            courseOfferingIds.add(section.get("courseOfferingId").toString());
        }
        
        if (teacherIds.size() > 0) {
            entities = getApiClient().getTeachers(token, teacherIds, null);
            for (GenericEntity entity : entities) {
                cache.put(entity.getId(), entity);
            }
        }
        
        if (courseOfferingIds.size() > 0) {
            entities = getApiClient().readEntityList(token, courseOfferingIds, null, SDKConstants.COURSE_OFFERINGS);
            List<String> courseIds = new ArrayList<String>(entities.size());
            for (GenericEntity entity : entities) {
                cache.put(entity.getId(), entity);
                
                courseIds.add(entity.getString(Constants.ATTR_COURSE_ID));
            }
            
            entities = getApiClient().readEntityList(token, courseIds, null, SDKConstants.COURSES_ENTITY);
            for (GenericEntity entity : entities) {
                cache.put(entity.getId(), entity);
            }
        }
        
        return studentSectionAssociations;
    }
    
    //
    /**
     * Get all schools for the district, and pass them to the ed-org profile.
     * 
     * @param token
     * @param id
     * @return
     */
    public GenericEntity getEdorgProfile(String token, String id) {
        // TODO Auto-generated method stub
        GenericEntity edorg = getApiClient().getEducationalOrganization(token, id);
        GenericEntity ge = new GenericEntity();
        
        if (edorg != null) {
            ge.put(Constants.ATTR_NAME_OF_INST, edorg.getString(Constants.ATTR_NAME_OF_INST));
            Link link = edorg.getLink(Constants.ATTR_GET_FEEDER_SCHOOLS);
            List<GenericEntity> schools = getApiClient().readEntityList(token, link.getResourceURL().toString());
            LinkedList<GenericEntity> schoolList = new LinkedList<GenericEntity>();
            
            for (GenericEntity school : schools) {
                GenericEntity tempSchool = new GenericEntity();
                tempSchool.put(Constants.ATTR_ID, school.getString(Constants.ATTR_ID));
                tempSchool.put(Constants.ATTR_NAME_OF_INST, school.getString(Constants.ATTR_NAME_OF_INST));
                schoolList.add(tempSchool);
            }
            
            ge.put(Constants.ATTR_SCHOOL_LIST, schoolList);
        }
        
        return ge;
    }
    
    /**
     * Get all ed-orgs for state and pass them to the state Ed-org profile.
     * 
     * @param token
     * @param edorgId
     * @return
     */
    public GenericEntity getStateEdorgProfile(String token, String edorgId) {
        GenericEntity edorg = getApiClient().getEducationalOrganization(token, edorgId);
        GenericEntity ge = new GenericEntity();
        
        if (edorg != null) {
            ge.put(Constants.ATTR_NAME_OF_INST, edorg.getString(Constants.ATTR_NAME_OF_INST));
            Link link = edorg.getLink(Constants.ATTR_GET_FEEDER_EDORGS);
            List<GenericEntity> districts = getApiClient().readEntityList(token, link.getResourceURL().toString());
            LinkedList<GenericEntity> districtList = new LinkedList<GenericEntity>();
            
            for (GenericEntity district : districts) {
                GenericEntity tempSchool = new GenericEntity();
                tempSchool.put(Constants.ATTR_ID, district.getString(Constants.ATTR_ID));
                tempSchool.put(Constants.ATTR_NAME_OF_INST, district.getString(Constants.ATTR_NAME_OF_INST));
                districtList.add(tempSchool);
            }
            
            ge.put(Constants.ATTR_DISTRICT_LIST, districtList);
        }
        
        return ge;
    }
    
    /**
     * Returns the grades and associated courses, by traversing student section
     * asssociations.
     * 
     * @param token
     * @param studentId
     * @return
     */
    @ExecutionTimeLogger.LogExecutionTime
    public GenericEntity getCurrentCoursesAndGrades(String token, String studentId) {
        
        LOG.info("starting grades and courses");
        List<GenericEntity> toReturn = new LinkedList<GenericEntity>();
        
        Map<String, GenericEntity> cache = new HashMap<String, GenericEntity>();
        Map<String, String> teacherIdCache = new HashMap<String, String>();
        Map<String, List<GenericEntity>> entityListCache = new HashMap<String, List<GenericEntity>>();
        
        // Get the student by ID.
        GenericEntity student = getStudent(token, studentId);
        
        List<Link> links = student.getLinks();
        
        List<GenericEntity> studentSectionAssociations = cacheStudent(token, links, cache, entityListCache,
                teacherIdCache);
        
        // Iterate over associations
        for (GenericEntity studentSectionAssociation : studentSectionAssociations) {
            
            GenericEntity toAdd = new GenericEntity();
            
            // Retrieve, course, teacher, and subject for the
            // studentSectionAssociation.
            GenericEntity section = getSectionForProfile(token,
                    studentSectionAssociation.getString(Constants.ATTR_SECTION_ID), cache, teacherIdCache);
            
            // section may be null if teacher has no access to it
            if (section != null) {
                toAdd.put(Constants.ATTR_SECTION_NAME, section.get(Constants.ATTR_UNIQUE_SECTION_CODE));
                Map teacher = (Map) section.get(Constants.ATTR_TEACHER_NAME);
                
                StringBuilder teacherName = new StringBuilder();
                if (teacher != null) {
                    if (teacher.containsKey(Constants.ATTR_PERSONAL_TITLE_PREFIX)) {
                        teacherName = teacherName.append(teacher.get(Constants.ATTR_PERSONAL_TITLE_PREFIX)).append(
                                ". ");
                    }
                    if (teacher.containsKey(Constants.ATTR_FIRST_NAME)) {
                        teacherName = teacherName.append(teacher.get(Constants.ATTR_FIRST_NAME)).append(" ");
                    }
                    if (teacher.containsKey(Constants.ATTR_LAST_SURNAME)) {
                        teacherName = teacherName.append(teacher.get(Constants.ATTR_LAST_SURNAME));
                    }
                }
                toAdd.put(Constants.ATTR_TEACHER_NAME, teacherName.toString());
                toAdd.put(Constants.ATTR_COURSE_TITLE, section.get(Constants.ATTR_COURSE_TITLE));
                toAdd.put(Constants.ATTR_SUBJECTAREA, section.get(Constants.ATTR_SUBJECTAREA));
            }
            
            // Iterate the link and retrieve grades for the
            // studentSectionAssociation.
            for (Link stuSecLinks : studentSectionAssociation.getLinks()) {
                if (stuSecLinks.getLinkName().equals(Constants.GET_GRADES)) {
                    String key = stuSecLinks.getResourceURL().toString();
                    List<GenericEntity> grades;
                    
                    grades = getApiClient().readEntityList(token, key);
                    
                    for (GenericEntity grade : grades) {
                        toAdd.put(grade.getString(Constants.GRADE_TYPE),
                                grade.getString(Constants.ATTR_LETTER_GRADE_EARNED));
                    }
                }
            }
            // Add aggregated data for studentSectionAssociation to
            // return list.
            toReturn.add(toAdd);
        }
        
        GenericEntity ge = new GenericEntity();
        ge.put(Constants.COURSES_AND_GRADES, toReturn);
        
        LOG.info("ending grades and courses");
        
        return ge;
    }
}

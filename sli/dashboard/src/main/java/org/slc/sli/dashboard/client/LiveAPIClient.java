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


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.slc.sli.dashboard.entity.ConfigMap;
import org.slc.sli.dashboard.entity.GenericEntity;
import org.slc.sli.dashboard.entity.util.GenericEntityEnhancer;
import org.slc.sli.dashboard.util.Constants;
import org.slc.sli.dashboard.util.ExecutionTimeLogger;
import org.slc.sli.dashboard.util.ExecutionTimeLogger.LogExecutionTime;
import org.slc.sli.dashboard.util.JsonConverter;
import org.slc.sli.dashboard.util.SecurityUtil;


/**
 *
 * API Client class used by the Dashboard to make calls to the API service.
 * TODO: Refactor public methods to private and mock with PowerMockito in unit
 * tests
 *
 * @author svankina
 *
 */
public class LiveAPIClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(LiveAPIClient.class);

    // base urls
    private static final String STAFF_URL = "/v1/staff/";
    private static final String EDORGS_URL = "/v1/educationOrganizations/";
    private static final String SCHOOLS_URL = "/v1/schools";
    private static final String COURSES_URL = "/v1/courses/";
    private static final String COURSE_OFFERINGS_URL = "/v1/courseOfferings/";
    private static final String SECTIONS_URL = "/v1/sections/";
    private static final String STUDENTS_URL = "/v1/students/";
    private static final String TEACHERS_URL = "/v1/teachers/";
    private static final String HOME_URL = "/v1/home/";
    private static final String ASSMT_URL = "/v1/assessments/";
    private static final String SESSION_URL = "/v1/sessions/";
    private static final String STUDENT_ASSESSMENTS_URL = "/v1/studentAssessments/";
    private static final String STUDENT_GRADEBOOK_ENTR_URL = "/v1/studentGradebookEntries";
    private static final String STUDENT_ACADEMIC_RECORD_URL = "/v1/studentAcademicRecords";

    // resources to append to base urls
    private static final String STAFF_EDORG_ASSOC = "/staffEducationOrgAssignmentAssociations/educationOrganizations";
    private static final String ATTENDANCES = "/attendances";
    private static final String STUDENT_SECTION_ASSOC = "/studentSectionAssociations";
    private static final String TEACHER_SECTION_ASSOC = "/teacherSectionAssociations";
    private static final String STUDENT_ASSESSMENTS = "/studentAssessments";
    private static final String SECTIONS = "/sections";
    private static final String STUDENTS = "/students";
    private static final String COURSE_TRANSCRIPTS = "/courseTranscripts";
    private static final String CUSTOM_DATA = "/custom";
    private static final String STUDENT_PARENT_ASSOC = "/studentParentAssociations";
    private static final String PARENTS = "/parents";

    // link names
    private static final String ED_ORG_LINK = "getParentEducationOrganization";
    private static final String SCHOOL_LINK = "getSchool";
    private static final String STUDENT_SCHOOL_ASSOCIATIONS_LINK = "getStudentSchoolAssociations";

    // attributes
    private static final String EDORG_SLI_ID_ATTRIBUTE = "edOrgSliId";
    private static final String EDORG_ATTRIBUTE = "edOrg";
    private static final String API_VERSION = "v1";

    /**
     * Wrapper for value for the custom store - value is expected json object vs primitive
     *
     */
    public static class CustomEntityWrapper {
        String value;

        public CustomEntityWrapper(String value) {
            this.value = value;
        }
    }

    private String apiUrl;

    private RESTClient restClient;
    private Gson gson;

    private String gracePeriod;

    public void setGracePeriod(String gracePeriod) {
        this.gracePeriod = gracePeriod;
    }

    public String getGracePeriod() {
        return this.gracePeriod;
    }

    public LiveAPIClient() {
        gson = new Gson();
    }

    /**
     * Get staff information (credentials, etc.)
     */
    //@Override
    public GenericEntity getStaffInfo(String token) {
        String id = getId(token);
        GenericEntity staffEntity = createEntityFromAPI(getApiUrl() + STAFF_URL + id, token);
        List<GenericEntity> edOrgsList = createEntitiesFromAPI(getApiUrl() + STAFF_URL + id + STAFF_EDORG_ASSOC, token);
        if ((edOrgsList != null) && (edOrgsList.size() > 0)) {

            // Processing only first EdOrg for now
            GenericEntity edOrgEntity = edOrgsList.get(0);
            if (edOrgEntity != null) {
                String edOrgSliId = edOrgEntity.getId();
                staffEntity.put(EDORG_SLI_ID_ATTRIBUTE, edOrgSliId);
                staffEntity.put(EDORG_ATTRIBUTE, edOrgEntity);
            }
        }
        return staffEntity;
    }

    /**
     * Get educational organization custom data
     */
    //@Override
    public ConfigMap getEdOrgCustomData(String token, String id) {
        CustomEntityWrapper jsonConfig = (CustomEntityWrapper) createEntityFromAPI(getApiUrl() + EDORGS_URL + id + CUSTOM_DATA, token, CustomEntityWrapper.class);
        return JsonConverter.fromJson(jsonConfig.value, ConfigMap.class);
    }

    /**
     * Put or save educational organization custom data
     */
    //@Override
    public void putEdOrgCustomData(String token, String id, ConfigMap configMap) {
        putEntityToAPI(getApiUrl() + EDORGS_URL + id + CUSTOM_DATA, token, new CustomEntityWrapper(JsonConverter.toJson(configMap)));
    }

    /**
     * Creates a generic entity from an API call
     *
     * @param url
     * @param token
     * @param entityClass
     * @return the entity
     */
    @LogExecutionTime
    public Object createEntityFromAPI(String url, String token, Class entityClass) {
        // DE260 - Logging of possibly sensitive data
        // LOGGER.info("Querying API: {}", url);
        String response = restClient.makeJsonRequestWHeaders(url, token);
        if (response == null) {
            return null;
        }
        Object e = gson.fromJson(response, entityClass);
        return e;
    }

    @LogExecutionTime
    private <T> void putEntityToAPI(String url, String token, T entity) {
        restClient.putJsonRequestWHeaders(url, token, gson.toJson(entity));
    }

    //@Override
    public List<GenericEntity> getSchools(String token, List<String> schoolIds) {

        List<GenericEntity> schools = null;

        // get schools
        schools = createEntitiesFromAPI(getApiUrl() + SCHOOLS_URL + "?" + Constants.LIMIT + "=" + Constants.MAX_RESULTS, token);

        return schools;
    }

    //@Override
    public List<GenericEntity> getCoursesSectionsForSchool(String token, String schoolId) {

        // get sections
        List<GenericEntity> sections = null;
        if (SecurityUtil.isNotEducator()) {

            sections = createEntitiesFromAPI(getApiUrl() + SCHOOLS_URL + "/" + schoolId
                    + SECTIONS + "?" + Constants.LIMIT + "=" + Constants.MAX_RESULTS, token);

            enrichSectionsWithSessionDetails(token, sections);

            sections = filterCurrentSections(sections, true);

        } else {
            // TODO: (sivan) check if a simple /section will work for teachers as well
            String teacherId = getId(token);
            sections = getSectionsForTeacher(teacherId, token, schoolId);
        }

        // get courses
        List<GenericEntity> courses = new ArrayList<GenericEntity>();
        if (sections != null && !sections.isEmpty()) {
            courses = getCourseSectionMappings(sections, token);
        }

        return courses;
    }


    /**
     * Get the associations between courses and sections
     */
    private List<GenericEntity> getCourseSectionMappings(List<GenericEntity> sections, String token) {

        Map<String, GenericEntity> courseMap = new HashMap<String, GenericEntity>();
        Map<String, String> sectionIDToCourseIDMap = new HashMap<String, String>();

        // this temporary sectionLookup will be used for cross reference between
        // courseId and
        // section.
        Map<String, Set<GenericEntity>> sectionLookup = new HashMap<String, Set<GenericEntity>>();

        // iterate each section
        if (sections != null) {

            Map<String, String> courseOfferingToCourseIDMap = new HashMap<String, String>();

            // find the course for each course offering
            List<GenericEntity> courseOfferings = createEntitiesFromAPI(getApiUrl() + COURSE_OFFERINGS_URL + "?"
                    + Constants.LIMIT + "=" + Constants.MAX_RESULTS, token);
            if (courseOfferings != null) {
                for (GenericEntity courseOffering : courseOfferings) {
                    // Get course using courseId reference in section
                    String courseOfferingId = (String) courseOffering.get(Constants.ATTR_ID);
                    String courseId = (String) courseOffering.get(Constants.ATTR_COURSE_ID);
                    courseOfferingToCourseIDMap.put(courseOfferingId, courseId);
                }
            }

            for (GenericEntity section : sections) {
                // Get course using courseId reference in section
                String courseOfferingId = (String) section.get(Constants.ATTR_COURSE_OFFERING_ID);
                String courseId = courseOfferingToCourseIDMap.get(courseOfferingId);
                if (!sectionLookup.containsKey(courseId)) {
                    sectionLookup.put(courseId, new HashSet<GenericEntity>());
                }
                sectionLookup.get(courseId).add(section);
            }


            // get course Entity
            List<GenericEntity> courses = createEntitiesFromAPI(getApiUrl() + COURSES_URL + "?" + Constants.LIMIT + "=" + Constants.MAX_RESULTS, token);

            // update courseMap with courseId. "id" for this entity
            for (GenericEntity course : courses) {
                // Add course to courseMap
                //courseMap.put(course.getId(), course);
                Set<GenericEntity> matchedSections = sectionLookup.get(course.getId());
                if (matchedSections != null) {
                    // Add course to courseMap
                    courseMap.put(course.getId(), course);
                    Iterator<GenericEntity> sectionEntities = matchedSections.iterator();
                    while (sectionEntities.hasNext()) {
                        GenericEntity sectionEntity = sectionEntities.next();
                        course.appendToList(Constants.ATTR_SECTIONS, sectionEntity);
                        // update sectionIdToCourseIdMap
                        sectionIDToCourseIDMap.put(sectionEntity.getId(), course.getId());
                    }
                }
            }

        }

        return new ArrayList<GenericEntity>(courseMap.values());
    }


    /**
     * Get a list of student objects, given the student ids
     */
    //@Override
    public List<GenericEntity> getStudents(final String token, Collection<String> ids) {
        if (ids == null || ids.size() == 0) {
            return null;
        }

        List<GenericEntity> students = new ArrayList<GenericEntity>();

        for (String id : ids) {
            GenericEntity student = getStudent(token, id);
            if (student != null) {
                students.add(student);
            }
        }

        return students;
    }

    /**
     * Get a list of student assessment results, given a student id
     */
    //@Override
    public List<GenericEntity> getStudentAssessments(final String token, String studentId) {
        // make a call to student-assessments, with the student id
        List<GenericEntity> responses = createEntitiesFromAPI(getApiUrl() + STUDENTS_URL + studentId
                + STUDENT_ASSESSMENTS, token);

        // for each link in the returned list, make the student-assessment call
        // for the result data
        List<GenericEntity> studentAssmts = new ArrayList<GenericEntity>();
        if (responses != null) {
            for (GenericEntity studentAssmt : responses) {
                studentAssmts.add(studentAssmt);
            }
        }
        return studentAssmts;
    }

    /**
     * Get assessment info, given a list of assessment ids
     */
    //@Override
    public List<GenericEntity> getAssessments(final String token, List<String> assessmentIds) {

        List<GenericEntity> assmts = new ArrayList<GenericEntity>();
        for (String assmtId : assessmentIds) {
            assmts.add(getAssessment(assmtId, token));
        }
        return assmts;
    }

    /**
     * To retrieve Parent Educational Organizations
     */
    //@Override
    public List<GenericEntity> getParentEducationalOrganizations(final String token, List<GenericEntity> edOrgs) {

        StringBuilder parentEducationAgencyReferences = new StringBuilder();
        Set<String> edOrgParentIdLookup = new HashSet<String>();

        for (GenericEntity edOrg : edOrgs) {
            String parentEducationAgencyReferenceId = (String) edOrg.get(Constants.ATTR_PARENT_EDORG);

            // if educationOrganizationParentId returns null, it means edOrg
            // reaches the top of the
            // hierarchy. Also check educationOrganizationParentId because we do
            // not want to send
            // identical educationOrganizationParentId multiple times.
            if (parentEducationAgencyReferenceId != null && !parentEducationAgencyReferenceId.isEmpty()
                    && !edOrgParentIdLookup.contains(parentEducationAgencyReferenceId)) {
                if (parentEducationAgencyReferences.length() != 0) {
                    parentEducationAgencyReferences.append(",");
                }
                parentEducationAgencyReferences.append(parentEducationAgencyReferenceId);
                edOrgParentIdLookup.add(parentEducationAgencyReferenceId);
            }
        }

        // if there is any reference to query, access to API and return
        // entities.
        if (parentEducationAgencyReferences.length() != 0) {
            List<GenericEntity> returnedEdOrgsFromAPI = getEntities(token, Constants.ATTR_ED_ORGS,
                    parentEducationAgencyReferences.toString(), Collections.<String, String>emptyMap());
            if (returnedEdOrgsFromAPI != null) {
                return returnedEdOrgsFromAPI;
            }
        }
        return Collections.emptyList();
    }

    //@Override
    public GenericEntity getParentEducationalOrganization(final String token, GenericEntity edOrg) {
        List<String> hrefs = extractLinksFromEntity(edOrg, ED_ORG_LINK);
        for (String href : hrefs) {

            // API provides *both* parent and children edOrgs in the
            // "educationOrganization" link.
            // So this hack needs be made because api doesn't distinguish
            // between
            // parent or children edOrgs. DE105 has been logged to resolve it.
            // TODO: Remove the if statement once DE105 is resolved.
//            if (href.contains("?" + Constants.ATTR_PARENT_EDORG + "=")) {
//                // Links to children edOrgs are queries; ignore them.
//                continue;
//            }

            GenericEntity responses = createEntityFromAPI(href, token);
            return responses; // there should only be one parent.
        }
        // if we reached here the edOrg or school doesn't have a parent
        return null;
    }


    /**
     * Get one student
     */
    //@Override
    public GenericEntity getStudent(String token, String id) {
        return createEntityFromAPI(getApiUrl() + STUDENTS_URL + id, token);
    }

    //@Override
    public List<GenericEntity> getStudents(String token, String sectionId, List<String> studentIds) {
        return createEntitiesFromAPI(getApiUrl() + SECTIONS_URL + sectionId + STUDENT_SECTION_ASSOC + STUDENTS
                + "?views=assessments,attendances.1," + Constants.ATTR_TRANSCRIPT + ",gradebook"
                + "&" + Constants.LIMIT + "=" + Constants.MAX_RESULTS, token);
    }

    //@Override
    public List<GenericEntity> getStudentsWithGradebookEntries(final String token, final String sectionId) {
        return createEntitiesFromAPI(getApiUrl() + SECTIONS_URL + sectionId + STUDENT_SECTION_ASSOC + STUDENTS
                + "?views=gradebook" + "&" + Constants.LIMIT + "=" + Constants.MAX_RESULTS, token);
    }

    //@Override
    public GenericEntity getStudentWithOptionalFields(String token, String studentId, List<String> optionalFields) {
        String optFields = StringUtils.join(optionalFields, ',');

        String url = getApiUrl() + STUDENTS_URL + studentId + "?views=" + optFields;
        return createEntityFromAPI(url, token);
    }

    /**
     * Get one section
     */
    public GenericEntity getSection(String id, String token) {
        if (id == null) {
            return null;
        }
        GenericEntity section = createEntityFromAPI(getApiUrl() + SECTIONS_URL + id, token);
        if (section == null) {
            return null;
        }

        // if no section name, fill in with section code
        if (section.get(Constants.ATTR_SECTION_NAME) == null) {
            section.put(Constants.ATTR_SECTION_NAME, section.get(Constants.ATTR_UNIQUE_SECTION_CODE));
        }

        return section;
    }

    /**
     * 
     * @param token
     * @param id
     * @return
     */
    public GenericEntity getSession(String token, String id) {
        try {
            return this.createEntityFromAPI(getApiUrl() + SESSION_URL + id, token);
        } catch (JsonSyntaxException e) {
            LOGGER.warn("Error occured while getting session", e);
            return new GenericEntity();
        }
    }

    /**
     * Get one student-assessment association
     */
    private GenericEntity getStudentAssessment(String id, String token) {
        return createEntityFromAPI(getApiUrl() + STUDENT_ASSESSMENTS_URL + id, token);
    }

    /**
     * Get one assessment
     */
    private GenericEntity getAssessment(String id, String token) {
        return createEntityFromAPI(getApiUrl() + ASSMT_URL + id, token);
    }

    /**
     * Get the user's unique identifier
     *
     * @param token
     * @return
     */
    public String getId(String token) {

        // Make a call to the /home uri and retrieve id from there
        String returnValue = "";
        GenericEntity response = createEntityFromAPI(getApiUrl() + HOME_URL, token);

        if (response == null) {
            return null;
        }
        for (Map link : (List<Map>) (response.get(Constants.ATTR_LINKS))) {
            if (link.get(Constants.ATTR_REL).equals(Constants.ATTR_SELF)) {
                returnValue = parseId(link);
            }
        }

        return returnValue;
    }

    /**
     * Given a link in the API response, extract the entity's unique id
     *
     * @param link
     * @return
     */
    private String parseId(Map link) {
        String returnValue;
        int index = ((String) (link.get(Constants.ATTR_HREF))).lastIndexOf("/");
        returnValue = ((String) (link.get(Constants.ATTR_HREF))).substring(index + 1);
        return returnValue;
    }

    /**
     * retrieve all sections for tokenId
     *
     * @param token
     * @return
     */
    public List<GenericEntity> getSectionsForNonEducator(String token) {

        // call https://<IP address>/api/rest/<version>/sections
        List<GenericEntity> sections = createEntitiesFromAPI(getApiUrl() + SECTIONS_URL + "?" + Constants.LIMIT + "=" + Constants.MAX_RESULTS, token);

        // Enrich sections with session details
        enrichSectionsWithSessionDetails(token, sections);

        sections = filterCurrentSections(sections, true);

        return sections;
    }

    /**
     * Get a list of sections, given a teacher id
     */
    public List<GenericEntity> getSectionsForTeacher(String id, String token, String schoolId) {

        List<GenericEntity> sections = createEntitiesFromAPI(getApiUrl() + TEACHERS_URL + id + TEACHER_SECTION_ASSOC
                + SECTIONS, token);

        // filter by school id
        if (schoolId != null) {
            List<GenericEntity> filteredSections = new ArrayList<GenericEntity>();
            for (GenericEntity section : sections) {
                if (section.getString(Constants.ATTR_SCHOOL_ID) != null
                        && section.getString(Constants.ATTR_SCHOOL_ID).equals(schoolId)) {
                    filteredSections.add(section);
                }
            }
            sections = filteredSections;
        }

        // This isn't really filtering, rather just adding section codes to sections with no name
        sections = filterCurrentSections(sections, false);
        return sections;
    }

    /**
     * Enrich section entities with session details to be leveraged during filtering
     *
     * @param token
     * @param sections
     */
    private void enrichSectionsWithSessionDetails(String token, List<GenericEntity> sections) {

        List<GenericEntity> sessions = this.getSessions(token);
        if ((sessions != null) && (sections != null)) {

            // Setup sessions lookup map
            Map<String, GenericEntity> sessionMap = new HashMap<String, GenericEntity>();
            for (GenericEntity session : sessions) {
                sessionMap.put(session.getId(), session);
            }

            // Enrich each section with session entity
            for (GenericEntity section : sections) {
                String sessionIdAttribute = (String) section.get(Constants.ATTR_SESSION_ID);
                if (sessionIdAttribute != null) {
                    GenericEntity session = sessionMap.get(sessionIdAttribute);
                    section.put(Constants.ATTR_SESSION, session);
                }
            }
        }
    }

    /**
     * Process sections to ensure section name and filter historical data if specified
     *
     * @param sections
     * @param filterHistoricalData
     * @return
     */
    private List<GenericEntity> filterCurrentSections(List<GenericEntity> sections, boolean filterHistoricalData) {
        List<GenericEntity> filteredSections = sections;

        if (filterHistoricalData) {
            filteredSections = new ArrayList<GenericEntity>();
        }

        if (sections != null) {

            // Setup grace period date
            Calendar gracePeriodCalendar = Calendar.getInstance();
            gracePeriodCalendar.setTimeInMillis(System.currentTimeMillis());

            try {
                if (gracePeriod != null && !gracePeriod.equals("")) {
                    int daysToSubtract = Integer.parseInt(gracePeriod) * -1;
                    gracePeriodCalendar.add(Calendar.DATE, daysToSubtract);
                }
            } catch (NumberFormatException exception) {
                LOGGER.warn("Invalid grace period: {}", exception.getMessage());
            }

            for (GenericEntity section : sections) {

                // if no section name, fill in with section code
                if (section.get(Constants.ATTR_SECTION_NAME) == null) {
                    section.put(Constants.ATTR_SECTION_NAME, section.get(Constants.ATTR_UNIQUE_SECTION_CODE));
                }

                // Filter historical sections/sessions if necessary
                if (filterHistoricalData) {
                    Map<String, Object> session = (Map<String, Object>) section.get(Constants.ATTR_SESSION);

                    // Verify section has been enriched with session details
                    if (session != null) {
                        try {
                            // Setup session end date
                            String endDateAttribute = (String) session.get(Constants.ATTR_SESSION_END_DATE);
                            DateFormat formatter = new SimpleDateFormat(Constants.ATTR_DATE_FORMAT);
                            Date sessionEndDate = formatter.parse(endDateAttribute);
                            Calendar sessionEndCalendar = Calendar.getInstance();
                            sessionEndCalendar.setTimeInMillis(sessionEndDate.getTime());

                            // Add filtered section if grace period adjusted date is before
                            // or equal to session end date
                            if (gracePeriodCalendar.compareTo(sessionEndCalendar) <= 0) {
                                filteredSections.add(section);
                            }

                        } catch (IllegalArgumentException exception) {
                            LOGGER.warn("Invalid session date formatter configuration: {}", exception.getMessage());
                        } catch (ParseException exception) {
                            LOGGER.warn("Invalid session date format: {}", exception.getMessage());
                        }
                    }
                }
            }
        }

        return filteredSections;
    }


    //@Override
    public List<GenericEntity> getSessions(String token) {
        String url = getApiUrl() + SESSION_URL + "?" + Constants.LIMIT + "=" + Constants.MAX_RESULTS;
        try {
            return createEntitiesFromAPI(url, token);
        } catch (JsonSyntaxException e) {
            LOGGER.error(e.toString());
            return new ArrayList<GenericEntity>();
        }
    }

    //@Override
    public List<GenericEntity> getSessionsByYear(String token, String schoolYear) {
        String url = getApiUrl() + SESSION_URL + "?schoolYear=" + schoolYear;
        try {
            return createEntitiesFromAPI(url, token);
        } catch (JsonSyntaxException e) {
            LOGGER.error(e.toString());
            return new ArrayList<GenericEntity>();
        }
    }

    //@Override
    public GenericEntity getAcademicRecord(String token, Map<String, String> params) {
        String url = getApiUrl() + STUDENT_ACADEMIC_RECORD_URL;
        if (params != null && params.size() != 0) {
            url += "?" + buildQueryString(params);
        }

        List<GenericEntity> entities = createEntitiesFromAPI(url, token);

        if (entities == null || entities.size() == 0) {
            return null;
        }

        return entities.get(0);
    }

    /**
     * Returns the homeroom section for the student
     *
     * @param studentId
     * @param token
     * @return
     */
    //@Override
    public GenericEntity getHomeRoomForStudent(String studentId, String token) {
        String url = getApiUrl() + STUDENTS_URL + studentId + STUDENT_SECTION_ASSOC + "?" + Constants.LIMIT + "=" + Constants.MAX_RESULTS;
        List<GenericEntity> sectionStudentAssociations = createEntitiesFromAPI(url, token);

        // If only one section association exists for the student, return the
        // section as home room
        if (sectionStudentAssociations.size() == 1) {
            String sectionId = sectionStudentAssociations.get(0).getString(Constants.ATTR_SECTION_ID);
            return getSection(sectionId, token);
        }

        // If multiple section associations exist for the student, return the
        // section with
        // homeroomIndicator set to true
        for (GenericEntity secStudentAssociation : sectionStudentAssociations) {
            if ((secStudentAssociation.get(Constants.ATTR_HOMEROOM_INDICATOR) != null)
                    && ((Boolean) secStudentAssociation.get(Constants.ATTR_HOMEROOM_INDICATOR))) {
                String sectionId = secStudentAssociation.getString(Constants.ATTR_SECTION_ID);
                return getSection(sectionId, token);
            }
        }

        return null;
    }

    /**
     * Returns the primary staff associated with the section.
     *
     * @param sectionId
     * @param token
     * @return
     */
    //@Override
    public GenericEntity getTeacherForSection(String sectionId, String token) {
        String url = getApiUrl() + SECTIONS_URL + sectionId + TEACHER_SECTION_ASSOC;
        List<GenericEntity> teacherSectionAssociations = createEntitiesFromAPI(url, token);
        if (teacherSectionAssociations != null) {

            for (GenericEntity teacherSectionAssociation : teacherSectionAssociations) {

                if (teacherSectionAssociation.getString(Constants.ATTR_CLASSROOM_POSITION).equals(
                        Constants.TEACHER_OF_RECORD)) {
                    String teacherUrl = getApiUrl() + TEACHERS_URL
                            + teacherSectionAssociation.getString(Constants.ATTR_TEACHER_ID);
                    GenericEntity teacher = createEntityFromAPI(teacherUrl, token);
                    return teacher;
                }
            }
        }

        return null;
    }

    /**
     * Simple method to return a list of attendance data.
     *
     * @return A list of attendance events for a student.
     */
    //@Override
    public List<GenericEntity> getStudentAttendance(final String token, String studentId, String start, String end) {
        String url = STUDENTS_URL + studentId + ATTENDANCES;
        if (start != null && start.length() > 0) {
            url += "?eventDate>=" + start;
            url += "&eventDate<=" + end;
        }
        try {
            List<GenericEntity> ge = createEntitiesFromAPI(getApiUrl() + url, token);
            if (ge != null) {
                return ge;
            }
        } catch (JsonSyntaxException e) {
            LOGGER.error("Couldn't retrieve attendance for:" + studentId, e);
        }
        return Collections.emptyList();
    }

    private String getUsername() {
        return SecurityUtil.getPrincipal().getUsername().replace(" ", "");
    }

    /**
     * Creates a generic entity from an API call
     *
     * @param url
     * @param token
     * @return the entity
     */
    @ExecutionTimeLogger.LogExecutionTime
    public GenericEntity createEntityFromAPI(String url, String token) {
        // DE260 - Logging of possibly sensitive data
        // LOGGER.info("Querying API: {}", url);
        String response = restClient.makeJsonRequestWHeaders(url, token);
        if (response == null) {
            return null;
        }
        GenericEntity e = gson.fromJson(response, GenericEntity.class);
        return e;
    }

    /**
     * Retrieves an entity list from the specified API url
     * and instantiates from its JSON representation
     *
     * @param url
     *            - the API url to retrieve the entity list JSON string
     *            representation
     * @param token
     *            - the principle authentication token
     *
     * @return entityList
     *         - the generic entity list
     */
    @ExecutionTimeLogger.LogExecutionTime
    public List<GenericEntity> createEntitiesFromAPI(String url, String token) {
        List<GenericEntity> entityList = new ArrayList<GenericEntity>();

        // DE260 - Logging of possibly sensitive data
        // Parse JSON
        // LOGGER.info("Querying API for list: {}", url);
        String response = restClient.makeJsonRequestWHeaders(url, token);
        if (response == null) {
            return Collections.emptyList();
        }
        List<Map> maps = null;
        // this method expected to return JSON in array.
        // If JSON is not in array, convert to array.
        if (!response.startsWith("[")) {
            maps = gson.fromJson("[" + response + "]", ArrayList.class);
        } else {
            maps = gson.fromJson(response, ArrayList.class);
        }

        if (maps != null) {
            for (Map<String, Object> map : maps) {
                entityList.add(new GenericEntity(map));
            }
        } else {
            maps = Collections.emptyList();
        }

        return entityList;
    }

    /**
     * Returns a list of courses for a given student and query params
     * i.e students/{studentId}/studentCourseAssociations/courses?subejctArea=
     * "math"&includeFields=
     * courseId,courseTitle
     *
     * @param token
     *            Securiy token
     * @param sectionId
     *            The student Id
     * @param params
     *            Query params
     * @return
     */
    //@Override
    public List<GenericEntity> getCourses(final String token, final String sectionId, Map<String, String> params) {
        // get the entities
        return createEntitiesFromAPI(getApiUrl() + SECTIONS_URL + sectionId + STUDENT_SECTION_ASSOC + STUDENTS
                + "?views=transcript", token);
    }

    /**
     * Returns a list of student course associations for a
     * given student and query params
     * i.e students/{studentId}/studentCourseAssociations?courseId={courseId}&
     * includeFields=
     * finalLettergrade,studentId
     *
     * @param token
     *            Securiy token
     * @param studentId
     *            The student Id
     * @param params
     *            Query params
     * @return
     */
    //@Override
    public List<GenericEntity> getCourseTranscripts(final String token, final String studentId,
            Map<String, String> params) {
        // get the entities
        List<GenericEntity> entities = createEntitiesFromAPI(
                buildStudentURI(studentId, COURSE_TRANSCRIPTS, params), token);

        return entities;
    }

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
    //@Override
    public List<GenericEntity> getEntities(final String token, final String type, final String id,
            Map<String, String> params) {
        StringBuilder url = new StringBuilder();

        // build the url
        url.append(getApiUrl());
        url.append("/" + API_VERSION + "/" + type + "/" + id);

        // add the query string
        if (!params.isEmpty()) {
            url.append("?");
            url.append(buildQueryString(params));
            url.append("&");
        } else {
            url.append("?");
        }
        url.append(Constants.LIMIT + "=" + Constants.MAX_RESULTS);

        return createEntitiesFromAPI(url.toString(), token);
    }

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
    //@Override
    public GenericEntity getEntity(final String token, final String type, final String id, Map<String, String> params) {
        StringBuilder url = new StringBuilder();

        // build the url
        url.append(getApiUrl());
        url.append("/" + API_VERSION + "/");
        url.append(type);
        if (id != null) {
            url.append("/");
            url.append(id);
        }
        // add the query string
        if (params != null && !params.isEmpty()) {
            url.append("?");
            url.append(buildQueryString(params));
        }

        return createEntityFromAPI(url.toString(), token);
    }

    /**
     * Returns a list of sections for the given student and params
     *
     * @param token
     * @param studentId
     * @param params
     * @return
     */
    //@Override
    public List<GenericEntity> getSections(final String token, final String studentId, Map<String, String> params) {
        // get the entities
        List<GenericEntity> entities = createEntitiesFromAPI(
                buildStudentURI(studentId, STUDENT_SECTION_ASSOC + SECTIONS , params), token);

        return entities;
    }

    /*
     * Retrieves and returns student school associations for a given student.
     */
    //@Override
    public List<GenericEntity> getStudentEnrollment(final String token, GenericEntity student) {
        List<String> urls = extractLinksFromEntity(student, STUDENT_SCHOOL_ASSOCIATIONS_LINK);

        if (urls == null || urls.isEmpty()) {
            return new LinkedList<GenericEntity>();
        }

        // Retrieve the student school associations from the first link with
        // STUDENT_SCHOOL_ASSOCIATIONS_LINK
        // sorted by entryDate
        String url = this.sortBy(urls.get(0), "entryDate", "descending") + "&" + Constants.LIMIT + "=" + Constants.MAX_RESULTS;

        List<GenericEntity> studentSchoolAssociations = createEntitiesFromAPI(url, token);

        for (GenericEntity studentSchoolAssociation : studentSchoolAssociations) {
            GenericEntity enhancedStudentSchoolAssociation = GenericEntityEnhancer.enhanceStudentSchoolAssociation(studentSchoolAssociation);
            String schoolUrl = extractLinksFromEntity(enhancedStudentSchoolAssociation, SCHOOL_LINK).get(0);

            // Retrieve the school for the corresponding student school
            // association
            GenericEntity school = createEntityFromAPI(schoolUrl, token);
            enhancedStudentSchoolAssociation.put(Constants.ATTR_SCHOOL, school);
        }

        return studentSchoolAssociations;
    }

    /**
     * Returns a list of student grade book entries for a given student and
     * params
     *
     * @param token
     *            Security token
     * @param studentId
     *            The student Id
     * @param params
     *            param map
     * @return
     */
    //@Override
    public List<GenericEntity> getStudentGradebookEntries(final String token, final String studentId,
            Map<String, String> params) {
        StringBuilder url = new StringBuilder();

        // add the studentId to the param list
        params.put(Constants.ATTR_STUDENT_ID, studentId);

        // build the url
        url.append(getApiUrl());
        url.append(STUDENT_GRADEBOOK_ENTR_URL);
        // add the query string
        if (!params.isEmpty()) {
            url.append("?");
            url.append(buildQueryString(params));
//            url.append("&limit=" + Constants.MAX_RESULTS);
        }

        // get the entities
        List<GenericEntity> entities = createEntitiesFromAPI(url.toString(), token);

        return entities;
    }

    /**
     * Builds a student based URI using the given studentId,path and param map
     *
     * @param studentId
     *            The studentId
     * @param path
     *            The URI path
     * @param params
     *            The param map
     * @return
     */
    protected String buildStudentURI(final String studentId, String path, Map<String, String> params) {
        StringBuilder url = new StringBuilder();

        // build the url
        url.append(getApiUrl());
        url.append(STUDENTS_URL);
        url.append(studentId);
        url.append(path);

        // add the query string
        if (!params.isEmpty()) {
            url.append("?");
            url.append(buildQueryString(params));
//            url.append("&limit=" + Constants.MAX_RESULTS);
        }

        return url.toString();
    }

    /**
     * Builds a query string from the given param map
     *
     * @param params
     *            The param map
     * @return
     */
    protected String buildQueryString(Map<String, String> params) {
        StringBuilder query = new StringBuilder();
        String separator = "";

        for (Map.Entry<String, String> e : params.entrySet()) {
            query.append(separator);
            separator = "&";

            query.append(e.getKey());
            query.append("=");
            query.append(e.getValue());
        }

        return query.toString();
    }

    /**
     * Extract the link with the given relationship from an entity
     */
    private static List<String> extractLinksFromEntity(GenericEntity e, String rel) {
        List<String> retVal = new ArrayList<String>();
        if (e == null || !e.containsKey(Constants.ATTR_LINKS)) {
            return retVal;
        }
        for (Map link : (List<Map>) (e.get(Constants.ATTR_LINKS))) {
            if (link.get(Constants.ATTR_REL).equals(rel)) {
                String href = (String) link.get(Constants.ATTR_HREF);
                retVal.add(href);
            }
        }
        return retVal;
    }

    /**
     * Getter and Setter used by Spring to instantiate the live/test api class
     *
     * @return
     */
    public RESTClient getRestClient() {
        return restClient;
    }

    public void setRestClient(RESTClient restClient) {
        this.restClient = restClient;
    }

    public String getApiUrl() {
        return apiUrl + Constants.API_PREFIX;
    }

    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }

    //@Override
    public List<GenericEntity> getStudentsWithSearch(String token, String firstName, String lastName) {
        // TODO Auto-generated method stub
        String queryString = "?";
        boolean queryExists = false;
        if (firstName != null && !firstName.equals("")) {
            queryString += "name.firstName=" + firstName;
            queryExists = true;
            if (lastName != null && !lastName.equals("")) {
                queryString += "&name.lastSurname=" + lastName;
            }
        } else {
            if (lastName != null && !lastName.equals("")) {
                queryExists = true;
                queryString += "name.lastSurname=" + lastName;
            }
        }
        String url = getApiUrl() + STUDENTS_URL;

        if (queryExists) {
            // &limit=0 is the API syntax to return all results, not just the first 50 as per default
            url += queryString + "&" + Constants.LIMIT + "=" + Constants.MAX_RESULTS;
        }

        return createEntitiesFromAPI(url, token);
    }

    /**
     * Return a url with the sortBy parameter
     *
     * @param url
     * @param sortBy
     * @return
     */
    //@Override
    public String sortBy(String url, String sortBy) {
        return url + "?sortBy=" + sortBy;
    };

    /**
     * Return a url with the sortBy and sortOrder parameter
     *
     * @param url
     * @param sortBy
     * @param sortOrder
     *            "descending" or "ascending"
     * @return
     */
    //@Override
    public String sortBy(String url, String sortBy, String sortOrder) {
        return url + "?sortBy=" + sortBy + "&sortOrder=" + sortOrder;
    }

    //@Override
    public List<GenericEntity> getParentsForStudent(String token, String studentId) {
        return createEntitiesFromAPI(
                buildStudentURI(studentId, STUDENT_PARENT_ASSOC + PARENTS, Collections.<String, String>emptyMap()),
                token);
    }
}

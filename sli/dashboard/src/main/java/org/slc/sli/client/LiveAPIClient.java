package org.slc.sli.client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gson.Gson;

import org.apache.commons.lang3.StringUtils;
import org.slc.sli.entity.CustomConfig;
import org.slc.sli.entity.GenericEntity;
import org.slc.sli.entity.util.GenericEntityEnhancer;
import org.slc.sli.util.Constants;
import org.slc.sli.util.ExecutionTimeLogger.LogExecutionTime;
import org.slc.sli.util.SecurityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * API Client class used by the Dashboard to make calls to the API service.
 * TODO: Refactor public methods to private and mock with PowerMockito in unit
 * tests
 * 
 * @author svankina
 * 
 */
public class LiveAPIClient implements APIClient {
    
    private static final Logger LOGGER = LoggerFactory
            .getLogger(LiveAPIClient.class);
    
    // base urls
    private static final String STAFF_URL = "/v1/staff/";
    private static final String EDORGS_URL = "/v1/educationOrganizations/";
    private static final String SECTIONS_URL = "/v1/sections/";
    private static final String STUDENTS_URL = "/v1/students/";
    private static final String TEACHERS_URL = "/v1/teachers/";
    private static final String HOME_URL = "/v1/home/";
    private static final String ASSMT_URL = "/v1/assessments/";
    private static final String SESSION_URL = "/v1/sessions/";
    private static final String STUDENT_ASSMT_ASSOC_URL = "/v1/studentAssessmentAssociations/";
    private static final String STUDENT_SECTION_GRADEBOOK = "/v1/studentSectionGradebookEntries";
    private static final String STUDENTS_NO_SLASH_URL = "/v1/students";
    
    // resources to append to base urls
    private static final String STAFF_EDORG_ASSOC = "/staffEducationOrgAssignmentAssociations/educationOrganizations";
    private static final String ATTENDANCES = "/attendances";
    private static final String STUDENT_SECTION_ASSOC = "/studentSectionAssociations";
    private static final String TEACHER_SECTION_ASSOC = "/teacherSectionAssociations";
    private static final String STUDENT_ASSMT_ASSOC = "/studentAssessmentAssociations";
    private static final String SECTIONS = "/sections";
    private static final String STUDENTS = "/students";
    private static final String STUDENT_TRANSCRIPT_ASSOC = "/studentTranscriptAssociations";
    private static final String CUSTOM_DATA = "/custom";
    
    // link names
    private static final String ED_ORG_LINK = "getEducationOrganization";
    private static final String SCHOOL_LINK = "getSchool";
    private static final String STUDENT_SCHOOL_ASSOCIATIONS_LINK = "getStudentSchoolAssociations";
    
    // attributes
    private static final String EDORG_SLI_ID_ATTRIBUTE = "edOrgSliId";

    private String apiUrl;
    
    private String portalHeaderUrl;
    private String portalFooterUrl;
    
    public void setPortalHeaderUrl(String portalHeaderUrl) {
        this.portalHeaderUrl = portalHeaderUrl;
    }
    
    public void setPortalFooterUrl(String portalFooterUrl) {
        this.portalFooterUrl = portalFooterUrl;
    }
    
    private RESTClient restClient;
    private Gson gson;
    
    // For now, the live client will use the mock client for api calls not yet
    // implemented
    private MockAPIClient mockClient;
    
    public LiveAPIClient() {
        mockClient = new MockAPIClient();
        gson = new Gson();
    }
    
    /**
     * Get staff information (credentials, etc.)
     */
    @Override
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
            }
        }
        return staffEntity;
    }
    
    /**
     * Get educational organization custom data
     */
    @Override
    public CustomConfig getEdOrgCustomData(String token, String id) {
        return (CustomConfig) createEntityFromAPI(getApiUrl() + EDORGS_URL + id + CUSTOM_DATA, token,
                CustomConfig.class);
    }
    
    /**
     * Put or save educational organization custom data
     */
    @Override
    public void putEdOrgCustomData(String token, String id, String customJson) {
        restClient.putJsonRequestWHeaders(getApiUrl() + EDORGS_URL + id + CUSTOM_DATA, token, customJson);
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
        LOGGER.info("Querying API: {}", url);
        String response = restClient.makeJsonRequestWHeaders(url, token);
        if (response == null) {
            return null;
        }
        Object e = gson.fromJson(response, entityClass);
        return e;
    }

    /**
     * Get a list of schools for the user
     */
    @Override
    public List<GenericEntity> getSchools(String token, List<String> schoolIds) {
        
        String teacherId = getId(token);
        List<GenericEntity> sections = getSectionsForTeacher(teacherId, token);
        List<GenericEntity> schools = getSchoolsForSection(sections, token);
        return schools;
    }
    
    /**
     * Get a list of student objects, given the student ids
     */
    @Override
    public List<GenericEntity> getStudents(final String token,
            Collection<String> ids) {
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
    @Override
    public List<GenericEntity> getStudentAssessments(final String token,
            String studentId) {
        // make a call to student-assessments, with the student id
        List<GenericEntity> responses = createEntitiesFromAPI(getApiUrl()
                + STUDENTS_URL + studentId + STUDENT_ASSMT_ASSOC, token);
        
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
     * Get custom data
     */
    @Override
    public List<GenericEntity> getCustomData(final String token, String key) {
        return mockClient.getCustomData(getUsername(), key);
    }
    
    /**
     * Get assessment info, given a list of assessment ids
     */
    @Override
    public List<GenericEntity> getAssessments(final String token,
            List<String> assessmentIds) {
        
        List<GenericEntity> assmts = new ArrayList<GenericEntity>();
        for (String assmtId : assessmentIds) {
            assmts.add(getAssessment(assmtId, token));
        }
        return assmts;
    }
    
    /**
     * Get program participation, given a list of student ids
     */
    @Override
    public List<GenericEntity> getPrograms(final String token,
            List<String> studentIds) {
        return mockClient.getPrograms(getUsername(), studentIds);
    }
    
    @Override
    public List<GenericEntity> getParentEducationalOrganizations(
            final String token, List<GenericEntity> edOrgs) {
        
        StringBuilder parentEducationAgencyReferences = new StringBuilder();
        Set<String> edOrgParentIdLookup = new HashSet<String>();
        
        for (GenericEntity edOrg : edOrgs) {
            String parentEducationAgencyReferenceId = (String) edOrg
                    .get(Constants.ATTR_PARENT_EDORG);
            
            // if educationOrganizationParentId returns null, it means edOrg
            // reaches the top of the
            // hierarchy. Also check educationOrganizationParentId because we do
            // not want to send
            // identical educationOrganizationParentId multiple times.
            if (parentEducationAgencyReferenceId != null
                    && !parentEducationAgencyReferenceId.isEmpty()
                    && !edOrgParentIdLookup
                            .contains(parentEducationAgencyReferenceId)) {
                if (parentEducationAgencyReferences.length() != 0) {
                    parentEducationAgencyReferences.append(",");
                }
                parentEducationAgencyReferences
                        .append(parentEducationAgencyReferenceId);
                edOrgParentIdLookup.add(parentEducationAgencyReferenceId);
            }
        }
        
        // if there is any reference to query, access to API and return
        // entities.
        if (parentEducationAgencyReferences.length() != 0) {
            List<GenericEntity> returnedEdOrgsFromAPI = getEntities(token,
                    Constants.ATTR_ED_ORGS,
                    parentEducationAgencyReferences.toString(),
                    Collections.<String, String> emptyMap());
            if (returnedEdOrgsFromAPI != null) {
                return returnedEdOrgsFromAPI;
            }
        }
        return Collections.emptyList();
    }
    
    @Override
    public GenericEntity getParentEducationalOrganization(final String token,
            GenericEntity edOrg) {
        List<String> hrefs = extractLinksFromEntity(edOrg, ED_ORG_LINK);
        for (String href : hrefs) {
            
            // API provides *both* parent and children edOrgs in the
            // "educationOrganization" link.
            // So this hack needs be made because api doesn't distinguish
            // between
            // parent or children edOrgs. DE105 has been logged to resolve it.
            // TODO: Remove the if statement once DE105 is resolved.
            if (href.contains("?" + Constants.ATTR_PARENT_EDORG + "=")) {
                // Links to children edOrgs are queries; ignore them.
                continue;
            }
            
            GenericEntity responses = createEntityFromAPI(href, token);
            return responses; // there should only be one parent.
        }
        // if we reached here the edOrg or school doesn't have a parent
        return null;
    }
    
    /**
     * Get a list of student ids belonging to a section
     */
    private List<String> getStudentIdsForSection(String id, String token) {
        
        List<GenericEntity> responses = createEntitiesFromAPI(getApiUrl()
                + SECTIONS_URL + id + STUDENT_SECTION_ASSOC, token);
        List<String> studentIds = new ArrayList<String>();
        
        if (responses != null) {
            for (GenericEntity response : responses) {
                studentIds.add(response.getString(Constants.ATTR_STUDENT_ID));
            }
        }
        return studentIds;
    }
    
    /**
     * Get one student
     */
    @Override
    public GenericEntity getStudent(String token, String id) {
        return createEntityFromAPI(getApiUrl() + STUDENTS_URL + id, token);
    }
    
    @Override
    public List<GenericEntity> getStudents(String token, String sectionId,
            List<String> studentIds) {
        return createEntitiesFromAPI(getApiUrl() + SECTIONS_URL + sectionId
                + STUDENT_SECTION_ASSOC + STUDENTS
                + "?optionalFields=assessments,attendances.1,"
                + Constants.ATTR_TRANSCRIPT + ",gradebook", token);
    }
    
    @Override
    public List<GenericEntity> getStudentsWithGradebookEntries(
            final String token, final String sectionId) {
        return createEntitiesFromAPI(getApiUrl() + SECTIONS_URL + sectionId
                + STUDENT_SECTION_ASSOC + STUDENTS
                + "?optionalFields=gradebook", token);
    }
    
    @Override
    public GenericEntity getStudentWithOptionalFields(String token,
            String studentId, List<String> optionalFields) {
        String optFields = StringUtils.join(optionalFields, ',');
        
        String url = getApiUrl() + STUDENTS_URL + studentId
                + "?optionalFields=" + optFields;
        return createEntityFromAPI(url, token);
    }
    
    /**
     * Get one section
     */
    public GenericEntity getSection(String id, String token) {
        if (id == null) {
            return null;
        }
        GenericEntity section = createEntityFromAPI(getApiUrl() + SECTIONS_URL
                + id, token);
        if (section == null) {
            return null;
        }
        
        // if no section name, fill in with section code
        if (section.get(Constants.ATTR_SECTION_NAME) == null) {
            section.put(Constants.ATTR_SECTION_NAME,
                    section.get(Constants.ATTR_UNIQUE_SECTION_CODE));
        }
        
        return section;
    }
    
    @Override
    public GenericEntity getSession(String token, String id) {
        GenericEntity session = null;
        try {
            session = createEntityFromAPI(getApiUrl() + SESSION_URL + id, token);
            LOGGER.debug("Session: {}", session);
        } catch (Exception e) {
            LOGGER.warn("Error occured while getting session", e);
            session = new GenericEntity();
        }
        return session;
    }
    
    /**
     * Get one student-assessment association
     */
    private GenericEntity getStudentAssessment(String id, String token) {
        return createEntityFromAPI(getApiUrl() + STUDENT_ASSMT_ASSOC_URL + id,
                token);
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
        GenericEntity response = createEntityFromAPI(getApiUrl() + HOME_URL,
                token);
        
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
        returnValue = ((String) (link.get(Constants.ATTR_HREF)))
                .substring(index + 1);
        return returnValue;
    }
    
    /**
     * Get a list of sections, given a teacher id
     */
    public List<GenericEntity> getSectionsForTeacher(String id, String token) {
        
        List<GenericEntity> sections = createEntitiesFromAPI(getApiUrl()
                + TEACHERS_URL + id + TEACHER_SECTION_ASSOC + SECTIONS, token);
        
        if (sections != null) {
            for (GenericEntity section : sections) {
                
                // if no section name, fill in with section code
                if (section.get(Constants.ATTR_SECTION_NAME) == null) {
                    section.put(Constants.ATTR_SECTION_NAME,
                            section.get(Constants.ATTR_UNIQUE_SECTION_CODE));
                }
            }
        }
        
        return sections;
    }
    
    /**
     * Get a list of schools, given a list of sections
     * 
     * @param sections
     * @param token
     * @return
     */
    public List<GenericEntity> getSchoolsForSection(
            List<GenericEntity> sections, String token) {
        // collect associated course first.
        HashMap<String, GenericEntity> courseMap = new HashMap<String, GenericEntity>();
        HashMap<String, String> sectionIDToCourseIDMap = new HashMap<String, String>();
        getCourseSectionsMappings(sections, token, courseMap,
                sectionIDToCourseIDMap);
        
        // now collect associated schools.
        HashMap<String, GenericEntity> schoolMap = new HashMap<String, GenericEntity>();
        HashMap<String, String> sectionIDToSchoolIDMap = new HashMap<String, String>();
        getSchoolSectionsMappings(sections, token, schoolMap,
                sectionIDToSchoolIDMap);
        
        // Now associate course and school.
        // There is no direct course-school association in ed-fi, so in
        // dashboard
        // the "course-school" association is defined as follows:
        // course C is associated with school S if there exists a section X s.t.
        // C is associated
        // with X and S is associated with X.
        HashMap<String, HashSet<String>> schoolIDToCourseIDMap = new HashMap<String, HashSet<String>>();
        
        for (int i = 0; i < sections.size(); i++) {
            GenericEntity section = sections.get(i);
            
            if (sectionIDToSchoolIDMap.containsKey(section
                    .get(Constants.ATTR_ID))
                    && sectionIDToCourseIDMap.containsKey(section
                            .get(Constants.ATTR_ID))) {
                String schoolId = sectionIDToSchoolIDMap.get(section
                        .get(Constants.ATTR_ID));
                String courseId = sectionIDToCourseIDMap.get(section
                        .get(Constants.ATTR_ID));
                if (!schoolIDToCourseIDMap.containsKey(schoolId)) {
                    schoolIDToCourseIDMap.put(schoolId, new HashSet<String>());
                }
                schoolIDToCourseIDMap.get(schoolId).add(courseId);
            }
        }
        
        // now create the generic entity
        for (String schoolId : schoolIDToCourseIDMap.keySet()) {
            for (String courseId : schoolIDToCourseIDMap.get(schoolId)) {
                GenericEntity s = schoolMap.get(schoolId);
                GenericEntity c = courseMap.get(courseId);
                s.appendToList(Constants.ATTR_COURSES, c);
            }
        }
        return new ArrayList<GenericEntity>(schoolMap.values());
        
    }
    
    /**
     * Get the associations between courses and sections
     */
    private void getCourseSectionsMappings(List<GenericEntity> sections,
            String token, Map<String, GenericEntity> courseMap,
            Map<String, String> sectionIDToCourseIDMap) {
        
        // this variable is used to prevent sending duplicate courseId to API
        Set<String> courseIdTracker = new HashSet<String>();
        
        // this temporary sectionLookup will be used for cross reference between
        // courseId and
        // section.
        Map<String, Set<GenericEntity>> sectionLookup = new HashMap<String, Set<GenericEntity>>();
        
        StringBuilder courseIds = new StringBuilder();
        // iterate each section
        for (GenericEntity section : sections) {
            // Get course using courseId reference in section
            String courseId = (String) section.get(Constants.ATTR_COURSE_ID);
            
            // search course which doesn't exist already
            if (!courseMap.containsKey(courseId)) {
                if (!courseIdTracker.contains(courseId)) {
                    if (courseIds.length() != 0) {
                        courseIds.append(",");
                    }
                    courseIds.append(courseId);
                    courseIdTracker.add(courseId);
                }
                if (!sectionLookup.containsKey(courseId)) {
                    sectionLookup.put(courseId, new HashSet<GenericEntity>());
                }
                sectionLookup.get(courseId).add(section);
            }
        }
        
        // get Entites by given courseIds
        if (courseIds.length() != 0) {
            // get course Entity
            List<GenericEntity> courses = getEntities(token,
                    Constants.ATTR_COURSES, courseIds.toString(),
                    Collections.<String, String> emptyMap());
            
            // update courseMap with courseId. "id" for this entity
            for (GenericEntity course : courses) {
                // Add course to courseMap
                courseMap.put(course.getId(), course);
                Set<GenericEntity> matchedSections = sectionLookup.get(course
                        .getId());
                if (matchedSections != null) {
                    Iterator<GenericEntity> sectionEntities = matchedSections
                            .iterator();
                    while (sectionEntities.hasNext()) {
                        GenericEntity sectionEntity = sectionEntities.next();
                        course.appendToList(Constants.ATTR_SECTIONS,
                                sectionEntity);
                        // update sectionIdToCourseIdMap
                        sectionIDToCourseIDMap.put(sectionEntity.getId(),
                                course.getId());
                    }
                }
            }
        }
    }
    
    /**
     * Get the associations between schools and sections
     */
    private void getSchoolSectionsMappings(List<GenericEntity> sections,
            String token, Map<String, GenericEntity> schoolMap,
            Map<String, String> sectionIDToSchoolIDMap) {
        
        Set<String> schoolIdTracker = new HashSet<String>();
        // this temporary sectionLookup will be used for cross reference between
        // courseId and
        // section.
        Map<String, Set<GenericEntity>> sectionLookup = new HashMap<String, Set<GenericEntity>>();
        
        StringBuilder schoolIds = new StringBuilder();
        
        // iterate each section
        for (GenericEntity section : sections) {
            String schoolId = (String) section.get(Constants.ATTR_SCHOOL_ID);
            
            // search school which doesn't exist already
            if (!schoolMap.containsKey(schoolId)) {
                if (!schoolIdTracker.contains(schoolId)) {
                    if (schoolIds.length() != 0) {
                        schoolIds.append(",");
                    }
                    schoolIds.append(schoolId);
                    schoolIdTracker.add(schoolId);
                }
                if (!sectionLookup.containsKey(schoolId)) {
                    sectionLookup.put(schoolId, new HashSet<GenericEntity>());
                }
                sectionLookup.get(schoolId).add(section);
            }
        }
        
        // get Entites by given schoolIds
        if (schoolIds.length() != 0) {
            // get school Entity
            List<GenericEntity> schools = getEntities(token,
                    Constants.ATTR_SCHOOLS, schoolIds.toString(),
                    Collections.<String, String> emptyMap());
            
            // update courseMap with courseId. "id" for this entity
            for (GenericEntity school : schools) {
                String schoolId = school.getId();
                Set<GenericEntity> matchedSections = sectionLookup
                        .get(schoolId);
                if (matchedSections != null) {
                    Iterator<GenericEntity> sectionEntities = matchedSections
                            .iterator();
                    while (sectionEntities.hasNext()) {
                        GenericEntity sectionEntity = sectionEntities.next();
                        // Add school to schoolmapM
                        schoolMap.put(school.getId(), school);
                        // update sectionIdToCourseIdMap
                        sectionIDToSchoolIDMap.put(sectionEntity.getId(),
                                schoolId);
                    }
                }
            }
        }
    }
    
    @Override
    public List<GenericEntity> getSessionsByYear(String token, String schoolYear) {
        String url = getApiUrl() + SESSION_URL + "?schoolYear=" + schoolYear;
        try {
            return createEntitiesFromAPI(url, token);
        } catch (Exception e) {
            LOGGER.error(e.toString());
            return new ArrayList<GenericEntity>();
        }
    }
    
    /**
     * Returns the homeroom section for the student
     * 
     * @param studentId
     * @param token
     * @return
     */
    @Override
    public GenericEntity getHomeRoomForStudent(String studentId, String token) {
        String url = getApiUrl() + STUDENTS_URL + studentId
                + STUDENT_SECTION_ASSOC;
        List<GenericEntity> sectionStudentAssociations = createEntitiesFromAPI(
                url, token);
        
        // If only one section association exists for the student, return the
        // section as home room
        if (sectionStudentAssociations.size() == 1) {
            String sectionId = sectionStudentAssociations.get(0).getString(
                    Constants.ATTR_SECTION_ID);
            return getSection(sectionId, token);
        }
        
        // If multiple section associations exist for the student, return the
        // section with
        // homeroomIndicator set to true
        for (GenericEntity secStudentAssociation : sectionStudentAssociations) {
            if ((secStudentAssociation.get(Constants.ATTR_HOMEROOM_INDICATOR) != null)
                    && ((Boolean) secStudentAssociation
                            .get(Constants.ATTR_HOMEROOM_INDICATOR))) {
                String sectionId = secStudentAssociation
                        .getString(Constants.ATTR_SECTION_ID);
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
    @Override
    public GenericEntity getTeacherForSection(String sectionId, String token) {
        String url = getApiUrl() + SECTIONS_URL + sectionId
                + TEACHER_SECTION_ASSOC;
        List<GenericEntity> teacherSectionAssociations = createEntitiesFromAPI(
                url, token);
        if (teacherSectionAssociations != null) {
            
            for (GenericEntity teacherSectionAssociation : teacherSectionAssociations) {
                
                if (teacherSectionAssociation.getString(
                        Constants.ATTR_CLASSROOM_POSITION).equals(
                        Constants.TEACHER_OF_RECORD)) {
                    String teacherUrl = getApiUrl()
                            + TEACHERS_URL
                            + teacherSectionAssociation
                                    .getString(Constants.ATTR_TEACHER_ID);
                    GenericEntity teacher = createEntityFromAPI(teacherUrl,
                            token);
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
    @Override
    public List<GenericEntity> getStudentAttendance(final String token,
            String studentId, String start, String end) {
        String url = STUDENTS_URL + studentId + ATTENDANCES;
        if (start != null && start.length() > 0) {
            url += "?eventDate>=" + start;
            url += "&eventDate<=" + end;
        }
        try {
            List<GenericEntity> ge = createEntitiesFromAPI(getApiUrl() + url,
                    token);
            if (ge != null) {
                return ge;
            }
        } catch (Exception e) {
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
     * @param fullEntities
     *            TODO
     * @return the entity
     */
    @LogExecutionTime
    public GenericEntity createEntityFromAPI(String url, String token) {
        LOGGER.info("Querying API: {}", url);
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
    @LogExecutionTime
    public List<GenericEntity> createEntitiesFromAPI(String url, String token) {
        List<GenericEntity> entityList = new ArrayList<GenericEntity>();
        
        // Parse JSON
        LOGGER.info("Querying API for list: {}", url);
        String response = restClient.makeJsonRequestWHeaders(url, token);
        if (response == null) {
            return Collections.emptyList();
        }
        List<Map> maps = null;
        // this method expected to return JSON in array.
        // If JSON is not in array, convert to array.
        if (!response.startsWith("[")) {
            maps = gson.fromJson("[" + response + "]",
                    new ArrayList<Map>().getClass());
        } else {
            maps = gson.fromJson(response, new ArrayList<Map>().getClass());
        }
        
        if (maps != null) {
            for (Map<String, Object> map : maps) {
                entityList.add(new GenericEntity(map));
            }
        } else
            maps = Collections.emptyList();
        
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
    @Override
    public List<GenericEntity> getCourses(final String token,
            final String sectionId, Map<String, String> params) {
        // get the entities
        return createEntitiesFromAPI(getApiUrl() + SECTIONS_URL + sectionId
                + STUDENT_SECTION_ASSOC + STUDENTS
                + "?optionalFields=transcript", token);
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
    @Override
    public List<GenericEntity> getStudentTranscriptAssociations(
            final String token, final String studentId,
            Map<String, String> params) {
        // get the entities
        List<GenericEntity> entities = createEntitiesFromAPI(
                buildStudentURI(studentId, STUDENT_TRANSCRIPT_ASSOC, params),
                token);
        
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
    @Override
    public List<GenericEntity> getEntities(final String token,
            final String type, final String id, Map<String, String> params) {
        StringBuilder url = new StringBuilder();
        
        // build the url
        url.append(getApiUrl());
        url.append("/v1/");
        url.append(type);
        url.append("/");
        url.append(id);
        // add the query string
        if (!params.isEmpty()) {
            url.append("?");
            url.append(buildQueryString(params));
        }
        
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
    @Override
    public GenericEntity getEntity(final String token, final String type,
            final String id, Map<String, String> params) {
        StringBuilder url = new StringBuilder();
        
        // build the url
        url.append(getApiUrl());
        url.append("/v1/");
        url.append(type);
        url.append("/");
        url.append(id);
        // add the query string
        if (!params.isEmpty()) {
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
    @Override
    public List<GenericEntity> getSections(final String token,
            final String studentId, Map<String, String> params) {
        // get the entities
        List<GenericEntity> entities = createEntitiesFromAPI(
                buildStudentURI(studentId, STUDENT_SECTION_ASSOC + SECTIONS,
                        params), token);
        
        return entities;
    }
    
    /*
     * Retrieves and returns student school associations for a given student.
     */
    @Override
    public List<GenericEntity> getStudentEnrollment(final String token,
            GenericEntity student) {
        List<String> urls = extractLinksFromEntity(student,
                STUDENT_SCHOOL_ASSOCIATIONS_LINK);
        
        if (urls == null || urls.isEmpty()) {
            return new LinkedList<GenericEntity>();
        }
        
        // Retrieve the student school associations from the furst link with
        // STUDENT_SCHOOL_ASSOCIATIONS_LINK
        String url = urls.get(0);
        List<GenericEntity> studentSchoolAssociations = createEntitiesFromAPI(
                url, token);
        
        for (GenericEntity studentSchoolAssociation : studentSchoolAssociations) {
            studentSchoolAssociation = GenericEntityEnhancer
                    .enhanceStudentSchoolAssociation(studentSchoolAssociation);
            String schoolUrl = extractLinksFromEntity(studentSchoolAssociation,
                    SCHOOL_LINK).get(0);
            
            // Retrieve the school for the corresponding student school
            // association
            GenericEntity school = createEntityFromAPI(schoolUrl, token);
            studentSchoolAssociation.put(Constants.ATTR_SCHOOL, school);
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
    @Override
    public List<GenericEntity> getStudentSectionGradebookEntries(
            final String token, final String studentId,
            Map<String, String> params) {
        StringBuilder url = new StringBuilder();
        
        // add the studentId to the param list
        params.put(Constants.ATTR_STUDENT_ID, studentId);
        
        // build the url
        url.append(getApiUrl());
        url.append(STUDENT_SECTION_GRADEBOOK);
        // add the query string
        if (!params.isEmpty()) {
            url.append("?");
            url.append(buildQueryString(params));
        }
        
        // get the entities
        List<GenericEntity> entities = createEntitiesFromAPI(url.toString(),
                token);
        
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
    protected String buildStudentURI(final String studentId, String path,
            Map<String, String> params) {
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
    private static List<String> extractLinksFromEntity(GenericEntity e,
            String rel) {
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
    
    /*
     * 
     * TODO: HARDCODED Token, because lycans server is bad. Change to incoming
     * token
     */
    @Override
    public String getHeader(String token) {
        return restClient.getJsonRequest(portalHeaderUrl + "?isAdmin="
                + SecurityUtil.isAdmin());
    }
    
    /*
     * 
     * TODO: HARDCODED Token, because lycans server is bad. Change to incoming
     * token
     */
    @Override
    public String getFooter(String token) {
        return restClient.getJsonRequest(portalFooterUrl + "?isAdmin="
                + SecurityUtil.isAdmin());
    }
    
    @Override
    public List<GenericEntity> getStudentsWithSearch(String token,
            String firstName, String lastName) {
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
            url += queryString;
        }
        
        return createEntitiesFromAPI(url, token);
    }
}

package org.slc.sli.client;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slc.sli.api.client.SLIClient;
import org.slc.sli.entity.ConfigMap;
import org.slc.sli.entity.GenericEntity;
import org.slc.sli.entity.util.GenericEntityEnhancer;
import org.slc.sli.util.Constants;
import org.slc.sli.util.ExecutionTimeLogger;
import org.slc.sli.util.SecurityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This client will use the SDK client to communicate with the SLI API.
 * 
 * @author dwalker
 * @author rbloh
 * 
 */
public class SDKAPIClient implements APIClient {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(SDKAPIClient.class);
    
    private SLIClient sdkClient;
    private String gracePeriod;
    
    /*
     * *****************************************************
     * API Client Interface Methods
     * *****************************************************
     */
    
    /**
     * Set the SDK client
     * 
     * @param sdkClient
     */
    public void setSdkClient(SLIClient sdkClient) {
        this.sdkClient = sdkClient;
    }
    
    /**
     * Get the SDK client
     * 
     * @return
     */
    @Override
    public SLIClient getSdkClient() {
        return sdkClient;
    }
    
    /**
     * Set the SLI configured grace period for historical access
     * 
     * @param gracePeriod
     */
    public void setGracePeriod(String gracePeriod) {
        this.gracePeriod = gracePeriod;
    }
    
    /**
     * Get the SLI configured grace period for historical access
     * 
     * @return
     */
    @Override
    public String getGracePeriod() {
        return this.gracePeriod;
    }
    
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
    @Override
    public GenericEntity getEntity(String token, String type, String id, Map<String, String> params) {
        if ((id == null) || (id.length() <= 0)) {
            return null;
        } else {
            return this.readEntity(token, "/" + type + "/" + id + "?" + this.buildQueryString(params));
        }
    }
    
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
    @Override
    public List<GenericEntity> getEntities(String token, String type, String ids, Map<String, String> params) {
        if ((ids == null) || (ids.length() <= 0)) {
            return Collections.emptyList();
        } else {
            return this.readEntityList(token, "/" + type + "/" + ids + "?" + this.buildQueryString(params));
        }
    }
    
    /**
     * Get user's home entity
     * 
     * @param token
     * @return
     */
    @Override
    public GenericEntity getHome(String token) {
        return this.readEntity(token, SDKConstants.HOME_ENTITY);
    }
    
    /**
     * Get the user's unique identifier
     * 
     * @param token
     * @return
     */
    @Override
    public String getId(String token) {
        String id = null;
        
        GenericEntity homeEntity = this.getHome(token);
        
        if (homeEntity != null) {
            for (Map linkMap : (List<Map>) (homeEntity.get(Constants.ATTR_LINKS))) {
                if (linkMap.get(Constants.ATTR_REL).equals(Constants.ATTR_SELF)) {
                    id = parseId(linkMap);
                }
            }
        }
        
        return id;
    }
    
    /**
     * Get EdOrg custom data
     * 
     * @param token
     * @param id
     * @return
     */
    @Override
    public ConfigMap getEdOrgCustomData(String token, String id) {
        return (ConfigMap) this.readCustomEntity(token, SDKConstants.EDORGS_ENTITY + id + SDKConstants.CUSTOM_DATA,
                ConfigMap.class);
    }
    
    /**
     * Store EdOrg custom data
     * 
     * @param token
     * @param id
     * @param configMap
     */
    @Override
    public void putEdOrgCustomData(String token, String id, ConfigMap configMap) {
        Map<String, Object> entityMap = new HashMap<String, Object>();
        entityMap.put("config", configMap.getConfig());
        GenericEntity configMapEntity = new GenericEntity(entityMap);
        this.createEntity(token, SDKConstants.EDORGS_ENTITY + id + SDKConstants.CUSTOM_DATA, configMapEntity);
    }
    
    /**
     * Get a list of educational organizations using a list of ids
     * 
     * @param token
     * @param ids
     * @param params
     * @return
     */
    @Override
    public List<GenericEntity> getEducationalOrganizations(String token, List<String> ids, Map<String, String> params) {
        if ((ids == null) || (ids.size() <= 0)) {
            return Collections.emptyList();
        } else {
            return this.readEntityList(token,
                    SDKConstants.EDORGS_ENTITY + buildListString(ids) + "?" + this.buildQueryString(params));
        }
    }
    
    /**
     * Get education organizations for staff member identified by id
     * 
     * @param token
     * @param staffId
     * @return
     */
    @Override
    public List<GenericEntity> getEducationOrganizationsForStaff(String token, String staffId) {
        if ((staffId == null) || (staffId.length() <= 0)) {
            return Collections.emptyList();
        } else {
            return this.readEntityList(token, SDKConstants.STAFF_ENTITY + staffId
                    + SDKConstants.STAFF_EDORG_ASSIGNMENT_ASSOC + SDKConstants.EDORGS);
        }
    }
    
    /**
     * Get an educational organization identified by id
     * 
     * @param token
     * @param id
     * @return
     */
    @Override
    public GenericEntity getEducationalOrganization(String token, String id) {
        if ((id == null) || (id.length() <= 0)) {
            return null;
        } else {
            return this.readEntity(token, SDKConstants.EDORGS_ENTITY + id);
        }
    }
    
    /**
     * Get education organizations for staff member identified by id and matching organization
     * category or first if not specified
     * 
     * @param token
     * @param staffId
     * @param organizationCategory
     * @return
     */
    @Override
    public GenericEntity getEducationOrganizationForStaff(String token, String staffId, String organizationCategory) {
        if ((staffId == null) || (staffId.length() <= 0)) {
            return null;
        } else {
            GenericEntity staffEdOrg = null;
            List<GenericEntity> edOrgs = this.readEntityList(token, SDKConstants.STAFF_ENTITY + staffId
                    + SDKConstants.STAFF_EDORG_ASSIGNMENT_ASSOC + SDKConstants.EDORGS);
            if ((organizationCategory != null) && (organizationCategory.length() <= 0)) {
                for (GenericEntity edOrg : edOrgs) {
                    List<String> edOrgCategories = (List<String>) edOrg.get(Constants.ATTR_ORG_CATEGORIES);
                    if (edOrgCategories != null && edOrgCategories.size() > 0) {
                        for (String edOrgCategory : edOrgCategories) {
                            if (edOrgCategory.equals(organizationCategory)) {
                                staffEdOrg = edOrg;
                                break;
                            }
                        }
                    }
                }
            } else if (edOrgs.size() > 0) {
                staffEdOrg = edOrgs.get(0);
            }
            return staffEdOrg;
        }
    }
    
    /**
     * Get parent educational organizations for the supplied edOrgs
     * 
     * @param token
     * @param educationalOrganizations
     * @return
     */
    @Override
    public List<GenericEntity> getParentEducationalOrganizations(String token,
            List<GenericEntity> educationalOrganizations) {
        List<String> ids = this.extractAttributesFromEntities(educationalOrganizations, Constants.ATTR_PARENT_EDORG);
        return this.getEducationalOrganizations(token, ids, null);
    }
    
    /**
     * Get parent educational organization for the supplied edOrg
     * 
     * @param token
     * @param educationalOrganization
     * @return
     */
    @Override
    public GenericEntity getParentEducationalOrganization(String token, GenericEntity educationalOrganization) {
        GenericEntity parentEducationOrganization = null;
        List<GenericEntity> educationalOrganizations = new ArrayList<GenericEntity>();
        educationalOrganizations.add(educationalOrganization);
        List<String> ids = this.extractAttributesFromEntities(educationalOrganizations, Constants.ATTR_PARENT_EDORG);
        if (ids.size() > 0) {
            String parentId = ids.get(0);
            parentEducationOrganization = this.getEducationalOrganization(token, parentId);
        }
        return parentEducationOrganization;
    }
    
    /**
     * Get a list of all schools depending upon user role
     * 
     * @param token
     * @param ids
     * @return
     */
    @Override
    public List<GenericEntity> getSchools(String token, List<String> ids) {
        
        // get schools
        List<GenericEntity> schools = this.readEntityList(token, SDKConstants.SCHOOLS_ENTITY);
        
        // get sections
        List<GenericEntity> sections = null;
        if (SecurityUtil.isNotEducator()) {
            sections = getSectionsForNonEducator(token, null);
        } else {
            String teacherId = this.getId(token);
            sections = getSectionsForTeacher(teacherId, token, null);
        }
        
        // match schools and sections
        matchSchoolsAndSections(schools, sections, token);
        
        return schools;
    }
    
    /**
     * Get a list of schools using a list of ids
     * 
     * @param token
     * @param ids
     * @param params
     * @return
     */
    @Override
    public List<GenericEntity> getSchools(String token, List<String> ids, Map<String, String> params) {
        if ((ids == null) || (ids.size() <= 0)) {
            return Collections.emptyList();
        } else {
            return this.readEntityList(token,
                    SDKConstants.SCHOOLS_ENTITY + buildListString(ids) + "?" + this.buildQueryString(params));
        }
    }
    
    /**
     * Get a school identified by id
     * 
     * @param token
     * @param id
     * @return
     */
    @Override
    public GenericEntity getSchool(String token, String id) {
        if ((id == null) || (id.length() <= 0)) {
            return null;
        } else {
            return this.readEntity(token, SDKConstants.SCHOOLS_ENTITY + id);
        }
    }
    
    /**
     * Get a list of all sessions
     * 
     * @param token
     * @param params
     * @return
     */
    @Override
    public List<GenericEntity> getSessions(String token, Map<String, String> params) {
        return this.readEntityList(token, SDKConstants.SESSIONS_ENTITY + "?" + this.buildQueryString(params));
    }
    
    /**
     * Get a list of sessions using a list of ids
     * 
     * @param token
     * @param ids
     * @param params
     * @return
     */
    @Override
    public List<GenericEntity> getSessions(String token, List<String> ids, Map<String, String> params) {
        if ((ids == null) || (ids.size() <= 0)) {
            return Collections.emptyList();
        } else {
            return this.readEntityList(token,
                    SDKConstants.SESSIONS_ENTITY + buildListString(ids) + "?" + this.buildQueryString(params));
        }
    }
    
    /**
     * Get a list of sessions for the specified school year
     * 
     * @param token
     * @param schoolYear
     * @return
     */
    @Override
    public List<GenericEntity> getSessionsForYear(String token, String schoolYear) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("schoolYear", schoolYear);
        return this.readEntityList(token, SDKConstants.SESSIONS_ENTITY + "?" + this.buildQueryString(params));
    }
    
    /**
     * Get a session identified by id
     * 
     * @param token
     * @param id
     * @return
     */
    @Override
    public GenericEntity getSession(String token, String id) {
        if ((id == null) || (id.length() <= 0)) {
            return null;
        } else {
            return this.readEntity(token, SDKConstants.SESSIONS_ENTITY + id);
        }
    }
    
    /**
     * Get a list of all sections
     * 
     * @param token
     * @param params
     * @return
     */
    @Override
    public List<GenericEntity> getSections(String token, Map<String, String> params) {
        return this.readEntityList(token, SDKConstants.SECTIONS_ENTITY + "?" + this.buildQueryString(params));
    }
    
    /**
     * Get a list of sections using a list of ids
     * 
     * @param token
     * @param ids
     * @param params
     * @return
     */
    @Override
    public List<GenericEntity> getSections(String token, List<String> ids, Map<String, String> params) {
        if ((ids == null) || (ids.size() <= 0)) {
            return Collections.emptyList();
        } else {
            return this.readEntityList(token,
                    SDKConstants.SECTIONS_ENTITY + buildListString(ids) + "?" + this.buildQueryString(params));
        }
    }
    
    /**
     * Get all sections for a non-Educator
     * 
     * @param token
     * @param params
     * @return
     */
    @Override
    public List<GenericEntity> getSectionsForNonEducator(String token, Map<String, String> params) {
        List<GenericEntity> sections = this.getSections(token, params);
        
        // Enrich sections with session details
        enrichSectionsWithSessionDetails(token, sections);
        
        // Enable filtering
        sections = filterCurrentSections(sections, true);
        
        return sections;
    }
    
    /**
     * Get all sections for a Teacher
     * 
     * @param token
     * @param teacherId
     * @param params
     * @return
     */
    @Override
    public List<GenericEntity> getSectionsForTeacher(String teacherId, String token, Map<String, String> params) {
        if ((teacherId == null) || (teacherId.length() <= 0)) {
            return Collections.emptyList();
        } else {
            
            List<GenericEntity> sections = this.readEntityList(token,
                    SDKConstants.TEACHERS_ENTITY + teacherId + SDKConstants.TEACHER_SECTION_ASSOC
                            + SDKConstants.SECTIONS_ENTITY + "?" + this.buildQueryString(params));
            
            // Disable filtering, so just adding section codes to sections with no name
            sections = filterCurrentSections(sections, false);
            
            return sections;
        }
    }
    
    /**
     * Get a list of sections for the given student id
     * 
     * @param token
     * @param studentId
     * @param params
     * @return
     */
    @Override
    public List<GenericEntity> getSectionsForStudent(final String token, final String studentId,
            Map<String, String> params) {
        if ((studentId == null) || (studentId.length() <= 0)) {
            return Collections.emptyList();
        } else {
            
            List<GenericEntity> sections = this.readEntityList(token,
                    SDKConstants.STUDENTS_ENTITY + studentId + SDKConstants.STUDENT_SECTION_ASSOC
                            + SDKConstants.SECTIONS_ENTITY + "?" + this.buildQueryString(params));
            
            // Disable filtering, so just adding section codes to sections with no name
            sections = filterCurrentSections(sections, false);
            
            return sections;
        }
    }
    
    /**
     * Get a section identified by id
     * 
     * @param token
     * @param id
     * @return
     */
    @Override
    public GenericEntity getSection(String token, String id) {
        if ((id == null) || (id.length() <= 0)) {
            return null;
        } else {
            GenericEntity section = this.readEntity(token, SDKConstants.SECTIONS_ENTITY + id);
            ensureSectionName(section);
            return section;
        }
    }
    
    /**
     * Get student home room information
     * 
     * @param token
     * @param studentId
     * @return
     */
    @Override
    public GenericEntity getSectionHomeForStudent(String token, String studentId) {
        GenericEntity homeRoomEntity = null;
        
        List<GenericEntity> studentSections = this.getSectionsForStudent(token, studentId, null);
        
        // If only one section association exists for the student, return the
        // section as home room
        if (studentSections.size() == 1) {
            homeRoomEntity = studentSections.get(0);
        }
        
        // If multiple section associations exist for the student, return the
        // section with homeroomIndicator set to true
        for (GenericEntity studentSection : studentSections) {
            if ((studentSection.get(Constants.ATTR_HOMEROOM_INDICATOR) != null)
                    && ((Boolean) studentSection.get(Constants.ATTR_HOMEROOM_INDICATOR))) {
                homeRoomEntity = studentSection;
            }
        }
        
        return homeRoomEntity;
    }
    
    /**
     * Get a list of courses using a list of ids
     * 
     * @param token
     * @param ids
     * @param params
     * @return
     */
    @Override
    public List<GenericEntity> getCourses(String token, List<String> ids, Map<String, String> params) {
        if ((ids == null) || (ids.size() <= 0)) {
            return Collections.emptyList();
        } else {
            return this.readEntityList(token,
                    SDKConstants.COURSES_ENTITY + buildListString(ids) + "?" + this.buildQueryString(params));
        }
    }
    
    /**
     * Get a list of courses for the given student id
     * 
     * @param token
     * @param studentId
     * @param params
     * @return
     */
    @Override
    public List<GenericEntity> getCoursesForStudent(String token, String studentId, Map<String, String> params) {
        if ((studentId == null) || (studentId.length() <= 0)) {
            return Collections.emptyList();
        } else {
            params.put("optionalFields", "transcript");
            return this.readEntityList(token, SDKConstants.SECTIONS_ENTITY + studentId
                    + SDKConstants.STUDENT_SECTION_ASSOC + SDKConstants.STUDENTS + "?" + this.buildQueryString(params));
        }
    }
    
    /**
     * Get a list of transcripts for the given student id
     * 
     * @param token
     * @param studentId
     * @param params
     * @return
     */
    @Override
    public List<GenericEntity> getTranscriptsForStudent(String token, String studentId, Map<String, String> params) {
        if ((studentId == null) || (studentId.length() <= 0)) {
            return Collections.emptyList();
        } else {
            return this.readEntityList(token, SDKConstants.STUDENTS_ENTITY + studentId
                    + SDKConstants.STUDENT_TRANSCRIPT_ASSOC + "?" + this.buildQueryString(params));
        }
    }
    
    /**
     * Get a course identified by id
     * 
     * @param token
     * @param id
     * @return
     */
    @Override
    public GenericEntity getCourse(String token, String id) {
        if ((id == null) || (id.length() <= 0)) {
            return null;
        } else {
            return this.readEntity(token, SDKConstants.COURSES_ENTITY + id);
        }
    }
    
    /**
     * Get a list of staff members using a list of ids
     * 
     * @param token
     * @param ids
     * @param params
     * @return
     */
    @Override
    public List<GenericEntity> getStaff(String token, List<String> ids, Map<String, String> params) {
        if ((ids == null) || (ids.size() <= 0)) {
            return Collections.emptyList();
        } else {
            return this.readEntityList(token,
                    SDKConstants.STAFF_ENTITY + buildListString(ids) + "?" + this.buildQueryString(params));
        }
    }
    
    /**
     * Get staff member information identified by id
     * 
     * @param token
     * @param id
     * @return
     */
    @Override
    public GenericEntity getStaff(String token, String id) {
        if ((id == null) || (id.length() <= 0)) {
            return null;
        } else {
            return this.readEntity(token, SDKConstants.STAFF_ENTITY + id);
        }
    }
    
    /**
     * Get staff member information identified by id along with specified education organization of
     * category
     * 
     * @param token
     * @param id
     * @param organizationCategory
     * @return
     */
    @Override
    public GenericEntity getStaffWithEducationOrganization(String token, String id, String organizationCategory) {
        if ((id == null) || (id.length() <= 0)) {
            return null;
        } else {
            GenericEntity staffEntity = this.getStaff(token, id);
            GenericEntity edOrgEntity = this.getEducationOrganizationForStaff(token, id, organizationCategory);
            if (edOrgEntity != null) {
                String edOrgSliId = edOrgEntity.getId();
                staffEntity.put(SDKConstants.EDORG_SLI_ID_ATTRIBUTE, edOrgSliId);
                staffEntity.put(SDKConstants.EDORG_ATTRIBUTE, edOrgEntity);
            }
            return staffEntity;
        }
    }
    
    /**
     * Get a list of teachers specified by a list of ids
     * 
     * @param token
     * @param ids
     * @param params
     * @return
     */
    @Override
    public List<GenericEntity> getTeachers(String token, List<String> ids, Map<String, String> params) {
        if ((ids == null) || (ids.size() <= 0)) {
            return Collections.emptyList();
        } else {
            return this.readEntityList(token,
                    SDKConstants.TEACHERS_ENTITY + buildListString(ids) + "?" + this.buildQueryString(params));
        }
    }
    
    /**
     * Get a teacher identified by id
     * 
     * @param token
     * @param id
     * @return
     */
    @Override
    public GenericEntity getTeacher(String token, String id) {
        if ((id == null) || (id.length() <= 0)) {
            return null;
        } else {
            return this.readEntity(token, SDKConstants.TEACHERS_ENTITY + id);
        }
    }
    
    /**
     * Get the teacher for a specified section
     * 
     * @param token
     * @param sectionId
     * @return
     */
    @Override
    public GenericEntity getTeacherForSection(String token, String sectionId) {
        if ((sectionId == null) || (sectionId.length() <= 0)) {
            return null;
        } else {
            GenericEntity teacher = null;
            List<GenericEntity> teacherSectionAssociations = this.readEntityList(token, SDKConstants.SECTIONS_ENTITY
                    + sectionId + SDKConstants.TEACHER_SECTION_ASSOC + "?" + this.buildQueryString(null));
            if (teacherSectionAssociations != null) {
                for (GenericEntity teacherSectionAssociation : teacherSectionAssociations) {
                    if (teacherSectionAssociation.getString(Constants.ATTR_CLASSROOM_POSITION).equals(
                            Constants.TEACHER_OF_RECORD)) {
                        String teacherId = teacherSectionAssociation.getString(Constants.ATTR_TEACHER_ID);
                        teacher = this.getTeacher(token, teacherId);
                        return teacher;
                    }
                }
            }
            return teacher;
        }
    }
    
    /**
     * Get a list of parents for the given student id
     * 
     * @param token
     * @param studentId
     * @param params
     * @return
     */
    @Override
    public List<GenericEntity> getParentsForStudent(String token, String studentId, Map<String, String> params) {
        return this.readEntityList(token, SDKConstants.STUDENTS_ENTITY + studentId + SDKConstants.STUDENT_PARENT_ASSOC
                + SDKConstants.PARENTS + "?" + this.buildQueryString(params));
    }
    
    /**
     * Get a list of all students
     * 
     * @param token
     * @param ids
     * @param params
     * @return
     */
    @Override
    public List<GenericEntity> getStudents(String token, Map<String, String> params) {
        return this.readEntityList(token, SDKConstants.STUDENTS_ENTITY + "?" + this.buildQueryString(params));
    }
    
    /**
     * Get a list of students specified by a list of ids
     * 
     * @param token
     * @param ids
     * @param params
     * @return
     */
    @Override
    public List<GenericEntity> getStudents(String token, List<String> ids, Map<String, String> params) {
        if ((ids == null) || (ids.size() <= 0)) {
            return Collections.emptyList();
        } else {
            return this.readEntityList(token,
                    SDKConstants.STUDENTS_ENTITY + buildListString(ids) + "?" + this.buildQueryString(params));
        }
    }
    
    /**
     * Get a list of students assigned to the specified section
     * 
     * @param token
     * @param sectionId
     * @return
     */
    @Override
    public List<GenericEntity> getStudentsForSection(String token, String sectionId, List<String> studentIds) {
        if ((sectionId == null) || (sectionId.length() <= 0)) {
            return Collections.emptyList();
        } else {
            Map<String, String> params = new HashMap<String, String>();
            String optionalParams = Constants.ATTR_ASSESSMENTS + "," + Constants.ATTR_STUDENT_ATTENDANCES_1 + ","
                    + Constants.ATTR_TRANSCRIPT + "," + Constants.ATTR_GRADEBOOK;
            params.put(SDKConstants.PARAM_OPTIONAL_FIELDS, optionalParams);
            
            return this.readEntityList(token, SDKConstants.SECTIONS_ENTITY + sectionId
                    + SDKConstants.STUDENT_SECTION_ASSOC + SDKConstants.STUDENTS + "?" + this.buildQueryString(params));
        }
    }
    
    /**
     * Get a list of students using name search
     * 
     * @param token
     * @param firstName
     * @param lastName
     * @return
     */
    @Override
    public List<GenericEntity> getStudentsWithSearch(String token, String firstName, String lastName) {
        Map<String, String> params = new HashMap<String, String>();
        if ((firstName != null) && (firstName.length() > 0)) {
            params.put(SDKConstants.PARAM_FIRST_NAME, firstName);
        }
        if ((lastName != null) && (lastName.length() > 0)) {
            params.put(SDKConstants.PARAM_LAST_NAME, lastName);
        }
        return this.getStudents(token, params);
    }
    
    /**
     * Get a list of students in the specified section along with gradebook entries
     * 
     * @param token
     * @param sectionId
     * @return
     */
    @Override
    public List<GenericEntity> getStudentsForSectionWithGradebookEntries(String token, String sectionId) {
        if ((sectionId == null) || (sectionId.length() <= 0)) {
            return Collections.emptyList();
        } else {
            Map<String, String> params = new HashMap<String, String>();
            String optionalParams = Constants.ATTR_GRADEBOOK;
            params.put(SDKConstants.PARAM_OPTIONAL_FIELDS, optionalParams);
            
            return this.readEntityList(token, SDKConstants.SECTIONS_ENTITY + sectionId
                    + SDKConstants.STUDENT_SECTION_ASSOC + SDKConstants.STUDENTS + "?" + this.buildQueryString(params));
        }
    }
    
    /**
     * Get a student identified by id
     * 
     * @param token
     * @param id
     * @return
     */
    @Override
    public GenericEntity getStudent(String token, String id) {
        if ((id == null) || (id.length() <= 0)) {
            return null;
        } else {
            return this.readEntity(token, SDKConstants.STUDENTS_ENTITY + id);
        }
    }
    
    /**
     * Get a student identified by id including specified optional information
     * 
     * @param token
     * @param id
     * @param optionalFields
     * @return
     */
    @Override
    public GenericEntity getStudentWithOptionalFields(String token, String id, List<String> optionalFields) {
        if ((id == null) || (id.length() <= 0)) {
            return null;
        } else {
            Map<String, String> params = new HashMap<String, String>();
            String optionalParams = this.buildListString(optionalFields);
            params.put(SDKConstants.PARAM_OPTIONAL_FIELDS, optionalParams);
            
            return this.readEntity(token, SDKConstants.STUDENTS_ENTITY + id + "?" + this.buildQueryString(params));
        }
    }
    
    /**
     * Get a list of school enrollments for the given student id
     * 
     * @param token
     * @param student
     * @return
     */
    @Override
    public List<GenericEntity> getEnrollmentForStudent(String token, String studentId) {
        if ((studentId == null) || (studentId.length() <= 0)) {
            return Collections.emptyList();
        } else {
            Map<String, String> params = new HashMap<String, String>();
            params.put(SDKConstants.PARAM_SORT_BY, SDKConstants.PARAM_ENTRY_DATE);
            params.put(SDKConstants.PARAM_SORT_ORDER, SDKConstants.PARAM_SORT_ORDER_DESCENDING);
            List<GenericEntity> studentSchoolAssociations = this.readEntityList(token, SDKConstants.STUDENTS_ENTITY
                    + studentId + SDKConstants.STUDENT_SCHOOL_ASSOC + "?" + this.buildQueryString(params));
            for (GenericEntity studentSchoolAssociation : studentSchoolAssociations) {
                studentSchoolAssociation = GenericEntityEnhancer
                        .enhanceStudentSchoolAssociation(studentSchoolAssociation);
                String schoolId = (String) studentSchoolAssociation.get(Constants.ATTR_SCHOOL_ID);
                
                // Retrieve the school for the corresponding student school association
                GenericEntity school = this.getSchool(token, schoolId);
                studentSchoolAssociation.put(Constants.ATTR_SCHOOL, school);
            }
            
            return studentSchoolAssociations;
        }
    }
    
    /**
     * Get a list of attendances for the given student id
     * 
     * @param token
     * @param studentId
     * @param params
     * @return
     */
    @Override
    public List<GenericEntity> getAttendanceForStudent(String token, String studentId, Map<String, String> params) {
        if ((studentId == null) || (studentId.length() <= 0)) {
            return Collections.emptyList();
        } else {
            return this.readEntityList(token, SDKConstants.STUDENTS_ENTITY + studentId
                    + SDKConstants.ATTENDANCES_ENTITY + "?" + this.buildQueryString(params));
        }
    }
    
    /**
     * Get a list of academic records for the given student id
     * 
     * @param token
     * @param studentId
     * @param params
     * @return
     */
    @Override
    public List<GenericEntity> getAcademicRecordsForStudent(String token, String studentId, Map<String, String> params) {
        if ((studentId == null) || (studentId.length() <= 0)) {
            return Collections.emptyList();
        } else {
            if (params != null) {
                params.put(SDKConstants.PARAM_STUDENT_ID, studentId);
            }
            return this.readEntityList(token,
                    SDKConstants.ACADEMIC_RECORDS_ENTITY + "?" + this.buildQueryString(params));
        }
    }
    
    /**
     * Get a list of assessments using a list of ids
     * 
     * @param token
     * @param ids
     * @param params
     * @return
     */
    @Override
    public List<GenericEntity> getAssessments(String token, List<String> ids, Map<String, String> params) {
        if ((ids == null) || (ids.size() <= 0)) {
            return Collections.emptyList();
        } else {
            return this.readEntityList(token,
                    SDKConstants.ASSESSMENTS_ENTITY + buildListString(ids) + "?" + this.buildQueryString(params));
        }
    }
    
    /**
     * Get a list of assessments for the given student id
     * 
     * @param token
     * @param studentId
     * @param params
     * @return
     */
    @Override
    public List<GenericEntity> getAssessmentsForStudent(String token, String studentId) {
        if ((studentId == null) || (studentId.length() <= 0)) {
            return Collections.emptyList();
        } else {
            return this.readEntityList(token, SDKConstants.STUDENTS_ENTITY + studentId
                    + SDKConstants.STUDENT_ASSMT_ASSOC + "?" + this.buildQueryString(null));
        }
    }
    
    /**
     * Get an assessment identified by id
     * 
     * @param token
     * @param id
     * @return
     */
    @Override
    public GenericEntity getAssessment(String token, String id) {
        if ((id == null) || (id.length() <= 0)) {
            return null;
        } else {
            return this.readEntity(token, SDKConstants.ASSESSMENTS_ENTITY + id);
        }
    }
    
    /*
     * *****************************************************
     * Core API SDK Methods
     * *****************************************************
     */
    
    /**
     * Read a custom entity using the SDK
     * 
     * @param token
     * @param url
     * @param entityClass
     * @return
     */
    @ExecutionTimeLogger.LogExecutionTime
    protected Object readCustomEntity(String token, String url, Class entityClass) {
        Object entity = null;
        try {
            List<Object> entityList = new ArrayList<Object>();
            sdkClient.read(token, entityList, url, entityClass);
            if (entityList.size() > 0) {
                entity = entityList.get(0);
            }
        } catch (Exception e) {
            LOGGER.error("Exception occurred during API read", e);
        }
        return entity;
    }
    
    /**
     * Read a resource entity using the SDK
     * 
     * @param token
     * @param url
     * @return
     */
    @ExecutionTimeLogger.LogExecutionTime
    protected GenericEntity readEntity(String token, String url) {
        GenericEntity entity = null;
        
        // Testing
        LOGGER.warn("Accessing SDK URL: {}", url);
        
        try {
            List<GenericEntity> entityList = new ArrayList<GenericEntity>();
            sdkClient.read(token, entityList, url, GenericEntity.class);
            if (entityList.size() > 0) {
                entity = (GenericEntity) entityList.get(0);
            }
        } catch (Exception e) {
            LOGGER.error("Exception occurred during API read", e);
        }
        return entity;
    }
    
    /**
     * Read a list of resource entities using the SDK
     * 
     * @param token
     * @param url
     * @return
     */
    @ExecutionTimeLogger.LogExecutionTime
    protected List<GenericEntity> readEntityList(String token, String url) {
        List<GenericEntity> entityList = new ArrayList<GenericEntity>();
        
        // Testing
        LOGGER.warn("Accessing SDK URL: {}", url);
        
        try {
            sdkClient.read(token, entityList, url, GenericEntity.class);
        } catch (Exception e) {
            LOGGER.error("Exception occurred during API read", e);
        }
        return entityList;
    }
    
    /**
     * Create a resource entity using the SDK
     * 
     * @param token
     * @param url
     * @param entity
     * @return
     */
    @ExecutionTimeLogger.LogExecutionTime
    protected void createEntity(String token, String url, GenericEntity entity) {
        try {
            sdkClient.create(token, url, entity);
        } catch (Exception e) {
            LOGGER.error("Exception occurred during API create", e);
        }
    }
    
    /**
     * Update a resource entity using the SDK
     * 
     * @param token
     * @param url
     * @param entity
     * @return
     */
    @ExecutionTimeLogger.LogExecutionTime
    protected void updateEntity(String token, String url, GenericEntity entity) {
        try {
            sdkClient.update(token, url, entity);
        } catch (Exception e) {
            LOGGER.error("Exception occurred during API update", e);
        }
    }
    
    /**
     * Delete a resource entity using the SDK
     * 
     * @param token
     * @param url
     * @param entity
     * @return
     */
    @ExecutionTimeLogger.LogExecutionTime
    protected void deleteEntity(String token, String url) {
        try {
            sdkClient.delete(token, url);
        } catch (Exception e) {
            LOGGER.error("Exception occurred during API delete", e);
        }
    }
    
    /*
     * *****************************************************
     * API Helper Methods
     * *****************************************************
     */
    
    /**
     * Given a link in the API response, extract the entity's unique id
     * 
     * @param link
     * @return
     */
    private String parseId(Map linkMap) {
        String id;
        int index = ((String) (linkMap.get(Constants.ATTR_HREF))).lastIndexOf("/");
        id = ((String) (linkMap.get(Constants.ATTR_HREF))).substring(index + 1);
        return id;
    }
    
    /**
     * Extract the specified attribute's value from each entity in the given entity list
     * 
     * @param entities
     * @param attributeName
     * @return
     */
    private List<String> extractAttributesFromEntities(List<GenericEntity> entities, String attributeName) {
        List<String> attributeList = new ArrayList<String>();
        
        if (entities != null) {
            for (GenericEntity entity : entities) {
                String attributeValue = (String) entity.get(attributeName);
                if ((attributeValue != null) && (attributeValue.length() > 0)) {
                    attributeList.add(attributeValue);
                }
            }
        }
        
        return attributeList;
    }
    
    /**
     * Extract the link with the given relationship from an entity
     * 
     * @param entity
     * @param rel
     * @return
     */
    private List<String> extractLinksFromEntity(GenericEntity entity, String rel) {
        List<String> linkList = new ArrayList<String>();
        
        if (entity != null && entity.containsKey(Constants.ATTR_LINKS)) {
            for (Map link : (List<Map>) (entity.get(Constants.ATTR_LINKS))) {
                if (link.get(Constants.ATTR_REL).equals(rel)) {
                    String href = (String) link.get(Constants.ATTR_HREF);
                    linkList.add(href);
                }
            }
        }
        
        return linkList;
    }
    
    /**
     * Enrich section entities with session details to be leveraged during filtering
     * 
     * @param token
     * @param sections
     */
    private void enrichSectionsWithSessionDetails(String token, List<GenericEntity> sections) {
        
        List<GenericEntity> sessions = this.getSessions(token, null);
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
                
                // Ensure section name
                ensureSectionName(section);
                
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
    
    /**
     * Match schools and sections. Also retrieve course info.
     * 
     * @param sections
     * @param token
     * @return
     */
    private List<GenericEntity> matchSchoolsAndSections(List<GenericEntity> schools, List<GenericEntity> sections,
            String token) {
        
        // collect associated course first.
        HashMap<String, GenericEntity> courseMap = new HashMap<String, GenericEntity>();
        HashMap<String, String> sectionIDToCourseIDMap = new HashMap<String, String>();
        getCourseSectionsMappings(sections, token, courseMap, sectionIDToCourseIDMap);
        
        // now collect associated schools.
        HashMap<String, GenericEntity> schoolMap = new HashMap<String, GenericEntity>();
        HashMap<String, String> sectionIDToSchoolIDMap = new HashMap<String, String>();
        getSchoolSectionsMappings(sections, token, schools, schoolMap, sectionIDToSchoolIDMap);
        
        // Now associate course and school.
        // There is no direct course-school association in ed-fi. For any section associated to
        // a school, its course will also be associated.
        HashMap<String, HashSet<String>> schoolIDToCourseIDMap = new HashMap<String, HashSet<String>>();
        
        if (sections != null) {
            for (int i = 0; i < sections.size(); i++) {
                GenericEntity section = sections.get(i);
                if (sectionIDToSchoolIDMap.containsKey(section.get(Constants.ATTR_ID))
                        && sectionIDToCourseIDMap.containsKey(section.get(Constants.ATTR_ID))) {
                    String schoolId = sectionIDToSchoolIDMap.get(section.get(Constants.ATTR_ID));
                    String courseId = sectionIDToCourseIDMap.get(section.get(Constants.ATTR_ID));
                    if (!schoolIDToCourseIDMap.containsKey(schoolId)) {
                        schoolIDToCourseIDMap.put(schoolId, new HashSet<String>());
                    }
                    schoolIDToCourseIDMap.get(schoolId).add(courseId);
                }
            }
        }
        
        // now create the generic entity
        for (String schoolId : schoolIDToCourseIDMap.keySet()) {
            GenericEntity s = schoolMap.get(schoolId);
            for (String courseId : schoolIDToCourseIDMap.get(schoolId)) {
                GenericEntity c = courseMap.get(courseId);
                s.appendToList(Constants.ATTR_COURSES, c);
            }
        }
        
        return new ArrayList<GenericEntity>(schoolMap.values());
    }
    
    /**
     * Get the associations between courses and sections
     */
    private void getCourseSectionsMappings(List<GenericEntity> sections, String token,
            Map<String, GenericEntity> courseMap, Map<String, String> sectionIDToCourseIDMap) {
        
        // this variable is used to prevent sending duplicate courseId to API
        Set<String> courseIdTracker = new HashSet<String>();
        
        // this temporary sectionLookup will be used for cross reference between
        // courseId and
        // section.
        Map<String, Set<GenericEntity>> sectionLookup = new HashMap<String, Set<GenericEntity>>();
        
        List<String> courseIds = new ArrayList<String>();
        // iterate each section
        if (sections != null) {
            for (GenericEntity section : sections) {
                // Get course using courseId reference in section
                String courseId = (String) section.get(Constants.ATTR_COURSE_ID);
                // search course which doesn't exist already
                if (!courseMap.containsKey(courseId)) {
                    if (!courseIdTracker.contains(courseId)) {
                        courseIds.add(courseId);
                        courseIdTracker.add(courseId);
                    }
                    if (!sectionLookup.containsKey(courseId)) {
                        sectionLookup.put(courseId, new HashSet<GenericEntity>());
                    }
                    sectionLookup.get(courseId).add(section);
                }
            }
        }
        
        // get Entities by given courseIds
        if (courseIds.size() > 0) {
            
            // get course Entities
            List<GenericEntity> courses = getCourses(token, courseIds, null);
            
            // update courseMap with courseId. "id" for this entity
            for (GenericEntity course : courses) {
                // Add course to courseMap
                courseMap.put(course.getId(), course);
                Set<GenericEntity> matchedSections = sectionLookup.get(course.getId());
                if (matchedSections != null) {
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
    }
    
    /**
     * Get the associations between schools and sections
     */
    private void getSchoolSectionsMappings(List<GenericEntity> sections, String token, List<GenericEntity> schools,
            Map<String, GenericEntity> schoolMap, Map<String, String> sectionIDToSchoolIDMap) {
        
        // temporary cross reference between schoolId and sections
        Map<String, Set<GenericEntity>> sectionLookup = new HashMap<String, Set<GenericEntity>>();
        
        // iterate each section
        if (sections != null) {
            for (GenericEntity section : sections) {
                String schoolId = (String) section.get(Constants.ATTR_SCHOOL_ID);
                // search school which doesn't exist already
                if (!schoolMap.containsKey(schoolId)) {
                    
                    if (!sectionLookup.containsKey(schoolId)) {
                        sectionLookup.put(schoolId, new HashSet<GenericEntity>());
                    }
                    sectionLookup.get(schoolId).add(section);
                }
            }
        }
        
        if (schools != null) {
            
            // update schoolMap with schoolId. "id" for this entity
            for (GenericEntity school : schools) {
                String schoolId = school.getId();
                Set<GenericEntity> matchedSections = sectionLookup.get(schoolId);
                if (matchedSections != null) {
                    for (GenericEntity sectionEntity : matchedSections) {
                        // Add school to schoolmap
                        schoolMap.put(school.getId(), school);
                        // update sectionIdToSchoolIdMap
                        sectionIDToSchoolIDMap.put(sectionEntity.getId(), schoolId);
                    }
                }
            }
        }
    }
    
    private void ensureSectionName(GenericEntity section) {
        if ((section != null) && (section.get(Constants.ATTR_SECTION_NAME) == null)) {
            section.put(Constants.ATTR_SECTION_NAME, section.get(Constants.ATTR_UNIQUE_SECTION_CODE));
        }
    }
    
    /**
     * Builds a comma-separated string from the given string item list
     * 
     * @param items
     * @return
     */
    private String buildListString(List<String> items) {
        StringBuilder query = new StringBuilder();
        String separator = "";
        
        if (items != null) {
            for (String item : items) {
                query.append(separator);
                separator = ",";
                
                query.append(item);
            }
        }
        
        return query.toString();
    }
    
    /**
     * Builds a query string from the given parameter map
     * 
     * @param params
     * @return
     */
    private String buildQueryString(Map<String, String> params) {
        StringBuilder query = new StringBuilder();
        String separator = "";
        
        // Setup defaults including paging disabled
        if (params == null) {
            params = new HashMap<String, String>();
        }
        if (!params.containsKey("limit")) {
            params.put("limit", "0");
        }
        
        for (Map.Entry<String, String> e : params.entrySet()) {
            query.append(separator);
            separator = "&";
            
            query.append(e.getKey());
            query.append("=");
            query.append(e.getValue());
        }
        
        return query.toString();
    }
    
}

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

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.ws.rs.MessageProcessingException;

import org.apache.commons.lang.StringUtils;
import org.scribe.exceptions.OAuthException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.slc.sli.api.client.Entity;
import org.slc.sli.api.client.Link;
import org.slc.sli.api.client.SLIClient;
import org.slc.sli.api.client.SLIClientException;
import org.slc.sli.api.client.SLIClientFactory;
import org.slc.sli.api.constants.PathConstants;
import org.slc.sli.dashboard.entity.ConfigMap;
import org.slc.sli.dashboard.entity.GenericEntity;
import org.slc.sli.dashboard.entity.util.GenericEntityComparator;
import org.slc.sli.dashboard.entity.util.GenericEntityEnhancer;
import org.slc.sli.dashboard.util.CacheableUserData;
import org.slc.sli.dashboard.util.Constants;
import org.slc.sli.dashboard.util.ExecutionTimeLogger;
import org.slc.sli.dashboard.util.JsonConverter;
import org.slc.sli.dashboard.util.SecurityUtil;

/**
 * This client will use the SDK client to communicate with the SLI API.
 *
 * @author dwalker
 * @author rbloh
 * @author iivanisevic
 *
 */
public class SDKAPIClient implements APIClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(SDKAPIClient.class);

    private SLIClientFactory clientFactory;

    private String gracePeriod;

    /**
     * Wrapper for value for the custom store - value is expected json object vs
     * primitive
     *
     */
    public static class CustomEntityWrapper {
        String value;

        public CustomEntityWrapper(String value) {
            this.value = value;
        }
    }

    /*
     * *****************************************************
     * API Client Interface Methods
     * *****************************************************
     */

    public SLIClientFactory getClientFactory() {
        return clientFactory;
    }

    public void setClientFactory(SLIClientFactory clientFactory) {
        this.clientFactory = clientFactory;
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
    // @Override
    public String getGracePeriod() {
        return this.gracePeriod;
    }

    /**
     * Get a resource entity of a specified type which is identified by id and
     * enriched using optional parameters
     *
     * @param token
     * @param type
     * @param id
     * @param params
     * @return
     */
    @Override
    public GenericEntity getEntity(String token, String type, String id, Map<String, String> params) {
        return this.readEntity(token, "/" + type + "/" + id + "?" + this.buildQueryString(params), id);
    }

    /**
     * Get a list of resource entities of a specified type which are identified
     * by a list of ids and enriched using optional parameters
     *
     * @param token
     * @param type
     * @param ids
     * @param params
     * @return
     */
    @Override
    public List<GenericEntity> getEntities(String token, String type, String ids, Map<String, String> params) {
        return this.readEntityList(token, "/" + type + "/" + ids + "?" + this.buildQueryString(params), ids);
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

        GenericEntity homeEntity = this.getHome(token);

        if (homeEntity != null) {
            for (Link linkMap : homeEntity.getLinks()) {
                if (linkMap.getLinkName().equals(Constants.ATTR_SELF)) {
                    return parseId(linkMap.getResourceURL().getPath());
                }
            }
        }

        return null;
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
        GenericEntity ge = readCustomEntity(token, SDKConstants.EDORGS_ENTITY + id + SDKConstants.CUSTOM_DATA);

        if (ge == null) {
            return null;
        }

        Object config = ge.get("config");

        if (config == null) {
            return null;
        }

        return JsonConverter.fromJson((String) config, ConfigMap.class);
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
        GenericEntity configMapEntity = new GenericEntity();
        configMapEntity.put("config", JsonConverter.toJson(configMap));
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
        return this.readEntityList(token,
                SDKConstants.EDORGS_ENTITY + buildListString(ids) + "?" + this.buildQueryString(params), ids);
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
        return this.readEntityList(token, SDKConstants.STAFF_ENTITY + staffId
                + SDKConstants.STAFF_EDORG_ASSIGNMENT_ASSOC + SDKConstants.EDORGS, staffId);
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
        return this.readEntity(token, SDKConstants.EDORGS_ENTITY + id, id);
    }

    /**
     * Get education organizations for staff member identified by id and
     * matching organization category or first if not specified
     *
     * @param token
     * @param staffId
     * @param organizationCategory
     * @return
     */
    @Override
    public GenericEntity getEducationOrganizationForStaff(String token, String staffId, String organizationCategory) {
        GenericEntity staffEdOrg = null;
        List<GenericEntity> edOrgs = this.readEntityList(token, SDKConstants.STAFF_ENTITY + staffId
                + SDKConstants.STAFF_EDORG_ASSIGNMENT_ASSOC + SDKConstants.EDORGS, staffId);
        if ((organizationCategory != null) && (organizationCategory.length() > 0)) {
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
        List<GenericEntity> schools = this.readEntityList(token, ids, null, SDKConstants.SCHOOLS_ENTITY);

        return schools;
    }

    /**
     * Attempt to get a list of the users's schools
     *
     * Note: This is a naieve method, it assumes you're a teacher, and if that
     * fails it takes the route of going through pretending you're generic
     * staff.
     *
     * This adds 1 extra API call.
     *
     * @param token
     * @return a list of school entities that the user is associated with
     */
    @Override
    public List<GenericEntity> getMySchools(String token) {

        List<GenericEntity> schools = new ArrayList<GenericEntity>();
        // get schools
        schools = this.readEntityList(token, SDKConstants.TEACHERS_ENTITY + getId(token)
                + SDKConstants.TEACHER_SCHOOL_ASSOC + SDKConstants.SCHOOLS_ENTITY + "?" + this.buildQueryString(null));
        if (schools == null || schools.size() == 0) {
            // Ok there are 5 potential edOrg levels so we need to get the edOrg
            // for this staff then
            // dig down to the individual schools.
            Set<GenericEntity> schoolSet = new HashSet<GenericEntity>();
            List<GenericEntity> edOrgs = new ArrayList<GenericEntity>();
            schools = new ArrayList<GenericEntity>();
            edOrgs.addAll(this.readEntityList(token,
                    SDKConstants.STAFF_ENTITY + getId(token) + SDKConstants.STAFF_EDORG_ASSIGNMENT_ASSOC
                            + SDKConstants.EDORGS_ENTITY + "?" + this.buildQueryString(null)));

            for (int i = 0; i < edOrgs.size(); ++i) {
                GenericEntity edOrg = edOrgs.get(i);
                Map<String, String> query = new HashMap<String, String>();
                query.put("parentEducationAgencyReference", (String) edOrg.get("id"));

                List<String> categories = edOrg.getList("organizationCategories");
                if (categories.contains("School")) {
                    schoolSet.add(edOrg);
                } else {
                    List<GenericEntity> newEdorgs = this.readEntityList(token,
                            SDKConstants.EDORGS_ENTITY + "?" + this.buildQueryString(query));
                    if (newEdorgs != null) {
                        edOrgs.addAll(newEdorgs);
                    }
                }
            }
            schools.addAll(schoolSet);
        }

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
        return this.readEntityList(token,
                SDKConstants.SCHOOLS_ENTITY + buildListString(ids) + "?" + this.buildQueryString(params), ids);
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
        return this.readEntity(token, SDKConstants.SCHOOLS_ENTITY + id, id);
    }

    /**
     * Get a list of all sessions
     *
     * @param token
     * @param params
     * @return
     */
    @Override
    public List<GenericEntity> getSessions(String token, String schoolId, Map<String, String> params) {
        String url = "";
        if (schoolId != null) {
            url = SDKConstants.SCHOOLS_ENTITY + schoolId;
        }
        if (params != null && !params.isEmpty()) {
            url += SDKConstants.SESSIONS_ENTITY + "?" + this.buildQueryString(params);
        } else {
            url += SDKConstants.SESSIONS_ENTITY;
        }
        return this.readEntityList(token, url);
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
        return this.readEntityList(token,
                SDKConstants.SESSIONS_ENTITY + buildListString(ids) + "?" + this.buildQueryString(params), ids);
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
        return this.readEntity(token, SDKConstants.SESSIONS_ENTITY + id, id);
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
        return this.readEntityList(token,
                SDKConstants.SECTIONS_ENTITY + buildListString(ids) + "?" + this.buildQueryString(params), ids);
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
        enrichSectionsWithSessionDetails(token, null, sections);

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
        List<GenericEntity> sections = this.readEntityList(token,
                SDKConstants.TEACHERS_ENTITY + teacherId + SDKConstants.TEACHER_SECTION_ASSOC
                        + SDKConstants.SECTIONS_ENTITY + "?" + this.buildQueryString(params), teacherId);

        // Disable filtering, so just adding section codes to sections with no
        // name
        sections = filterCurrentSections(sections, false);

        return sections;
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
        // params.put(Constants.ATTR_SELECTOR_FIELD,
        // ":(.,studentSectionAssociations)");

        List<GenericEntity> studentSectionAssociations = getStudentSectionAssociations(token, studentId, params);

        List<GenericEntity> sections = this.readEntityList(token,
                SDKConstants.STUDENTS_ENTITY + studentId + SDKConstants.STUDENT_SECTION_ASSOC
                        + SDKConstants.SECTIONS_ENTITY + "?" + this.buildQueryString(params), studentId);

        sections = mergeLists(sections, studentSectionAssociations, "id", "sectionId", "studentSectionAssociations");

        // Disable filtering, so just adding section codes to sections with no
        // name
        sections = filterCurrentSections(sections, false);

        return sections;
    }

    private List<GenericEntity> getStudentSectionAssociations(final String token, final String studentId,
            Map<String, String> params) {
        List<GenericEntity> studentSectionAssociations = this.readEntityList(token, SDKConstants.STUDENTS_ENTITY
                + studentId + SDKConstants.STUDENT_SECTION_ASSOC + "?" + this.buildQueryString(params), studentId);

        return studentSectionAssociations;
    }

    private List<GenericEntity> mergeLists(List<GenericEntity> entities, List<GenericEntity> associations, String key,
            String associationKey, String attributeName) {
        List<GenericEntity> results = new ArrayList<GenericEntity>();

        for (GenericEntity entity : entities) {
            String id = (String) entity.get(key);
            List<GenericEntity> subList = getSubList(associations, associationKey, id);

            entity.put(attributeName, subList);
            results.add(entity);
        }

        return results;
    }

    private List<GenericEntity> getSubList(List<GenericEntity> entities, String key, String value) {
        List<GenericEntity> results = new ArrayList<GenericEntity>();

        for (GenericEntity entity : entities) {
            if (entity.containsKey(key)) {
                if (entity.get(key).equals(value)) {
                    results.add(entity);
                }
            }
        }

        return results;
    }

    /**
     * Get a section identified by id
     *
     * @param token
     * @param id
     * @return
     */
    @Override
    @CacheableUserData
    public GenericEntity getSection(String token, String id) {
        GenericEntity section = this.readEntity(token, SDKConstants.SECTIONS_ENTITY + id);
        ensureSectionName(section);
        return section;
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

        List<GenericEntity> studentSections = this.getSectionsForStudent(token, studentId,
                new HashMap<String, String>());

        // If only one section association exists for the student, return the
        // section as home room
        if (studentSections.size() == 1) {
            homeRoomEntity = studentSections.get(0);
            return homeRoomEntity;
        }

        // If multiple section associations exist for the student, return the
        // section with homeroomIndicator set to true
        for (GenericEntity studentSection : studentSections) {
            List<Map<String, Object>> studentSectionAssocs = (List<Map<String, Object>>) studentSection
                    .get("studentSectionAssociations");
            if (studentSectionAssocs != null) {
                for (Map<String, Object> sectionAssoc : studentSectionAssocs) {
                    if ((sectionAssoc.get(Constants.ATTR_HOMEROOM_INDICATOR) != null)
                            && ((Boolean) sectionAssoc.get(Constants.ATTR_HOMEROOM_INDICATOR))
                            && sectionAssoc.get(Constants.ATTR_STUDENT_ID).equals(studentId)) {
                        homeRoomEntity = studentSection;
                        return homeRoomEntity;
                    }
                }
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
        return this.readEntityList(token,
                SDKConstants.COURSES_ENTITY + buildListString(ids) + "?" + this.buildQueryString(params), ids);
    }

    public List<GenericEntity> getCourses(String token, List<String> ids) {
        return this.readEntityList(token, ids, null, SDKConstants.COURSES_ENTITY);
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
        params.put("optionalFields", "transcript");
        addGradeLevelParam(params);
        return this.readEntityList(token, SDKConstants.SECTIONS_ENTITY + studentId + SDKConstants.STUDENT_SECTION_ASSOC
                + SDKConstants.STUDENTS + "?" + this.buildQueryString(params), studentId);
    }

    // @Override
    @Override
    public List<GenericEntity> getCoursesSectionsForSchool(String token, String schoolId) {

        // get sections
        List<GenericEntity> sections = null;
        if (SecurityUtil.isNotEducator()) {

            sections = this.readEntityList(token, SDKConstants.SCHOOLS_ENTITY + schoolId + SDKConstants.SECTIONS + "?"
                    + Constants.LIMIT + "=" + Constants.MAX_RESULTS);

            enrichSectionsWithSessionDetails(token, schoolId, sections);

            sections = filterCurrentSections(sections, true);

        } else {
            String teacherId = getId(token);
            sections = getSectionsForTeacher(teacherId, token, null);

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
        }

        // get courses
        List<GenericEntity> courses = new ArrayList<GenericEntity>();
        if (sections != null && !sections.isEmpty()) {
            courses = getCourseSectionMappings(sections, schoolId, token);
        }

        return courses;
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
        return this.readEntityList(token, SDKConstants.STUDENTS_ENTITY + studentId + SDKConstants.COURSE_TRANSCRIPTS
                + "?" + this.buildQueryString(params), studentId);
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
        return this.readEntity(token, SDKConstants.COURSES_ENTITY + id, id);
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
        return this.readEntityList(token,
                SDKConstants.STAFF_ENTITY + buildListString(ids) + "?" + this.buildQueryString(params), ids);
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
        return this.readEntity(token, SDKConstants.STAFF_ENTITY + id, id);
    }

    /**
     * Get staff member information identified by id along with specified
     * education organization of category
     *
     * @param token
     * @param id
     * @param organizationCategory
     * @return
     */
    @Override
    public GenericEntity getStaffWithEducationOrganization(String token, String id, String organizationCategory) {
        GenericEntity staffEntity = this.getStaff(token, id);
        GenericEntity edOrgEntity = this.getEducationOrganizationForStaff(token, id, organizationCategory);
        if (edOrgEntity != null) {
            String edOrgSliId = edOrgEntity.getId();
            staffEntity.put(SDKConstants.EDORG_SLI_ID_ATTRIBUTE, edOrgSliId);
            staffEntity.put(SDKConstants.EDORG_ATTRIBUTE, edOrgEntity);
        }
        return staffEntity;
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
        return this.readEntityList(token, ids, params, SDKConstants.TEACHERS_ENTITY);
    }

    /**
     * Get a teacher identified by id
     *
     * @param token
     * @param id
     * @return
     */
    @Override
    @CacheableUserData
    public GenericEntity getTeacher(String token, String id) {
        // get Teacher information
        return this.readEntity(token, "/" + Constants.ATTR_TEACHERS + "/" + id, id);
    }

    @Override
    public GenericEntity getTeacherWithSections(String token, String id) {
        GenericEntity teacher = getTeacher(token, id);
        List<GenericEntity> sections = this.readEntityList(token, "/" + Constants.ATTR_TEACHERS + "/" + id + "/"
                + Constants.ATTR_TEACHER_SECTION_ASSOCIATIONS + "/" + Constants.ATTR_SECTIONS, id);
        if (sections != null && !sections.isEmpty()) {
            GenericEntityComparator sectionComparator = new GenericEntityComparator("uniqueSectionCode", String.class);
            Collections.sort(sections, sectionComparator);
            teacher.put("sections", sections);
        }
        return teacher;
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
        GenericEntity teacher = null;

        String teacherId = getTeacherIdForSection(token, sectionId);
        if (teacherId == null) {
            return null;
        }

        return this.getTeacher(token, teacherId);
    }

    /**
     * Get the teacher for a specified section
     *
     * @param token
     * @param sectionId
     * @return
     */
    @Override
    public String getTeacherIdForSection(String token, String sectionId) {
        List<GenericEntity> teacherSectionAssociations = this.readEntityList(token, SDKConstants.SECTIONS_ENTITY
                + sectionId + SDKConstants.TEACHER_SECTION_ASSOC + "?" + this.buildQueryString(null), sectionId);
        if (teacherSectionAssociations != null) {
            for (GenericEntity teacherSectionAssociation : teacherSectionAssociations) {
                if (teacherSectionAssociation.getString(Constants.ATTR_CLASSROOM_POSITION).equals(
                        Constants.TEACHER_OF_RECORD)) {
                    String teacherId = teacherSectionAssociation.getString(Constants.ATTR_TEACHER_ID);

                    //if (teacherId == null) {
                        // LOGGER.debug(arg0)
                    //}

                    return teacherId;

                }
            }
        }
        return null;
    }

    @Override
    public void getTeacherIdForSections(String token, List<String> sectionIds, Map<String, String> teacherIdCache) {

        List<GenericEntity> result = new ArrayList<GenericEntity>(sectionIds.size());
        for (int i = 0; i <= sectionIds.size() / Constants.MAX_IDS_PER_API_CALL; i++) {
            List<String> subList = sectionIds.subList(i * Constants.MAX_IDS_PER_API_CALL,
                    Math.min((i + 1) * Constants.MAX_IDS_PER_API_CALL, sectionIds.size()));
            List<GenericEntity> entityList = this.readEntityList(token,
                    SDKConstants.SECTIONS_ENTITY + buildListString(subList) + SDKConstants.TEACHER_SECTION_ASSOC + "?"
                            + this.buildQueryString(null), subList);
            result.addAll(entityList);
        }

        for (String sectionId : sectionIds) {
            teacherIdCache.put(sectionId, "empty");
        }

        if (result.size() > 0) {
            for (GenericEntity teacherSectionAssociation : result) {
                String teacherId = null;
                if (teacherSectionAssociation.getString(Constants.ATTR_CLASSROOM_POSITION).equals(
                        Constants.TEACHER_OF_RECORD)) {
                    teacherId = teacherSectionAssociation.getString(Constants.ATTR_TEACHER_ID);
                }

                String sectionId = teacherSectionAssociation.getString(Constants.ATTR_SECTION_ID);

                teacherIdCache.put(sectionId, teacherId);
            }
        }
    }

    /**
     * Get a list of teachers for a specific school
     *
     * @param token
     * @param schoolId
     * @return
     */
    @Override
    public List<GenericEntity> getTeachersForSchool(String token, String schoolId) {
        List<GenericEntity> teachers = this.readEntityList(token, Constants.ATTR_SCHOOLS + "/" + schoolId + "/"
                + Constants.ATTR_TEACHER_SCHOOL_ASSOCIATIONS + "/" + Constants.ATTR_TEACHERS, schoolId);

        return teachers;
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
        // params.put(Constants.ATTR_SELECTOR_FIELD,
        // ":(.,studentParentAssociations)");

        List<GenericEntity> studentParentAssociations = getStudentParentAssociations(token, studentId, params);

        List<GenericEntity> parents = this.readEntityList(token, SDKConstants.STUDENTS_ENTITY + studentId
                + SDKConstants.STUDENT_PARENT_ASSOC + SDKConstants.PARENTS + "?" + this.buildQueryString(params),
                studentId);

        parents = mergeLists(parents, studentParentAssociations, "id", "parentId", "studentParentAssociations");

        return parents;
    }

    private List<GenericEntity> getStudentParentAssociations(final String token, final String studentId,
            Map<String, String> params) {
        List<GenericEntity> studentParentAssociations = this.readEntityList(token, SDKConstants.STUDENTS_ENTITY
                + studentId + SDKConstants.STUDENT_PARENT_ASSOC, studentId);

        return studentParentAssociations;
    }

    /**
     * Get a list of students assigned to the specified school for a given
     * params
     *
     * @param token
     * @param schoolId
     * @param params
     * @return
     */
    @Override
    public List<GenericEntity> getStudentsForSchool(String token, String schoolId, Map<String, String> params) {
        addGradeLevelParam(params);
        return this.readEntityList(token,
                "/" + Constants.ATTR_SCHOOLS + "/" + schoolId + "/" + Constants.ATTR_STUDENT_SCHOOL_ASSOCIATIONS + "/"
                        + Constants.ATTR_STUDENTS + "?" + this.buildQueryString(params));
    }

    /**
     * Get a list of all students
     *
     * @param token
     * @param params
     * @return
     */
    @Override
    public List<GenericEntity> getStudents(String token, Map<String, String> params) {
        addGradeLevelParam(params);
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
        addGradeLevelParam(params);
        return this.readEntityList(token,
                SDKConstants.STUDENTS_ENTITY + buildListString(ids) + "?" + this.buildQueryString(params), ids);
    }

    private void addGradeLevelParam(Map<String, String> params) {
        String optionalParams;
        if (params.containsKey(SDKConstants.PARAM_OPTIONAL_FIELDS)) {
            optionalParams = params.get(SDKConstants.PARAM_OPTIONAL_FIELDS) + "," + Constants.ATTR_GRADE_LEVEL;
        } else {
            optionalParams = Constants.ATTR_GRADE_LEVEL;
        }
        params.put(SDKConstants.PARAM_OPTIONAL_FIELDS, optionalParams);
    }

    /**
     * Get a list of students assigned to the specified section
     *
     * @param token
     * @param sectionId
     * @return
     */
    @Override
    public List<GenericEntity> getStudentsForSection(String token, String sectionId) {
        Map<String, String> params = new HashMap<String, String>();
        String optionalParams = Constants.ATTR_ASSESSMENTS + "," + Constants.ATTR_STUDENT_ATTENDANCES_1 + ","
                + Constants.ATTR_TRANSCRIPT + "," + Constants.ATTR_GRADEBOOK + "," + Constants.ATTR_GRADE_LEVEL;
        params.put(SDKConstants.PARAM_OPTIONAL_FIELDS, optionalParams);

        return this.readEntityList(token, SDKConstants.SECTIONS_ENTITY + sectionId + SDKConstants.STUDENT_SECTION_ASSOC
                + SDKConstants.STUDENTS + "?" + this.buildQueryString(params), sectionId);
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
    public List<GenericEntity> getStudentsWithSearch(String token, String firstName, String lastName, String schoolId) {
        List<GenericEntity> students = null;
        Map<String, String> params = new HashMap<String, String>();
        addGradeLevelParam(params);
        if ((firstName != null) && (firstName.length() > 0)) {
            params.put(SDKConstants.PARAM_FIRST_NAME, firstName);
        }
        if ((lastName != null) && (lastName.length() > 0)) {
            params.put(SDKConstants.PARAM_LAST_NAME, lastName);
        }
        if ((schoolId != null) && (schoolId.length() > 0)) {
            students = this.getStudentsForSchool(token, schoolId, params);
        } else {
            students = this.getStudents(token, params);
        }
        return students;
    }

    @Override
    public List<GenericEntity> searchStudents(String token, String query, Map<String, String> params) {

        return this.readEntityList(token, "search/students?q=" + query + "&" + this.buildQueryString(params));
    }

    /**
     * Get a list of students in the specified section along with gradebook
     * entries
     *
     * @param token
     * @param sectionId
     * @return
     */
    @Override
    public List<GenericEntity> getStudentsForSectionWithGradebookEntries(String token, String sectionId) {
        Map<String, String> params = new HashMap<String, String>();
        String optionalParams = Constants.ATTR_GRADEBOOK;
        params.put(SDKConstants.PARAM_OPTIONAL_FIELDS, optionalParams);
        addGradeLevelParam(params);

        return this.readEntityList(token, SDKConstants.SECTIONS_ENTITY + sectionId + SDKConstants.STUDENT_SECTION_ASSOC
                + SDKConstants.STUDENTS + "?" + this.buildQueryString(params), sectionId);
    }

    /**
     * Get a student identified by id
     *
     * @param token
     * @param id
     * @return
     */
    @Override
    @CacheableUserData
    public GenericEntity getStudent(String token, String id) {
        Map<String, String> params = new HashMap<String, String>();
        String optionalParams = Constants.ATTR_GRADE_LEVEL;
        params.put(SDKConstants.PARAM_OPTIONAL_FIELDS, optionalParams);

        return this.readEntity(token, SDKConstants.STUDENTS_ENTITY + id + "?" + this.buildQueryString(params), id);
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
        Map<String, String> params = new HashMap<String, String>();
        String optionalParams = this.buildListString(optionalFields);
        params.put(SDKConstants.PARAM_OPTIONAL_FIELDS, optionalParams);
        addGradeLevelParam(params);

        return this.readEntity(token, SDKConstants.STUDENTS_ENTITY + id + "?" + this.buildQueryString(params), id);
    }

    /**
     * Get a list of school enrollments for the given student id
     *
     * @param token
     * @param studentId
     * @return
     */
    @Override
    public List<GenericEntity> getEnrollmentForStudent(String token, String studentId) {
        Map<String, String> params = new HashMap<String, String>();
        params.put(SDKConstants.PARAM_SORT_BY, SDKConstants.PARAM_ENTRY_DATE);
        params.put(SDKConstants.PARAM_SORT_ORDER, SDKConstants.PARAM_SORT_ORDER_DESCENDING);
        List<GenericEntity> studentSchoolAssociations = this.readEntityList(token, SDKConstants.STUDENTS_ENTITY
                + studentId + SDKConstants.STUDENT_SCHOOL_ASSOC + "?" + this.buildQueryString(params), studentId);

        List<String> schoolIds = new ArrayList<String>(studentSchoolAssociations.size());
        for (GenericEntity studentSchoolAssociation : studentSchoolAssociations) {
            GenericEntity enhancedStudentSchoolAssociation = GenericEntityEnhancer.enhanceStudentSchoolAssociation(studentSchoolAssociation);
            String schoolId = (String) enhancedStudentSchoolAssociation.get(Constants.ATTR_SCHOOL_ID);

            schoolIds.add(schoolId);
        }

        List<GenericEntity> schools = getSchools(token, schoolIds);

        Map<String, GenericEntity> cache = new HashMap<String, GenericEntity>();
        for (GenericEntity school : schools) {
            cache.put(school.getId(), school);
        }

        for (GenericEntity studentSchoolAssociation : studentSchoolAssociations) {
            GenericEntity enhancedStudentSchoolAssociation = GenericEntityEnhancer.enhanceStudentSchoolAssociation(studentSchoolAssociation);
            String schoolId = (String) enhancedStudentSchoolAssociation.get(Constants.ATTR_SCHOOL_ID);

            // Retrieve the school for the corresponding student school
            // association
            GenericEntity school = cache.get(schoolId);
            if (school == null) {
                LOGGER.warn("reading school from API, but should be reading from cache.");
                school = getSchool(token, schoolId);
            }
            enhancedStudentSchoolAssociation.put(Constants.ATTR_SCHOOL, school);
        }

        return studentSchoolAssociations;
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
        return this.readEntityList(token, SDKConstants.STUDENTS_ENTITY + studentId + SDKConstants.ATTENDANCES_ENTITY
                + "?" + this.buildQueryString(params), studentId);
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
        if (params != null) {
            params.put(SDKConstants.PARAM_STUDENT_ID, studentId);
        }
        List<GenericEntity> entityList = this.readEntityList(token,
                SDKConstants.ACADEMIC_RECORDS_ENTITY + "?" + this.buildQueryString(params), studentId);
        return entityList;
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
        return this.readEntityList(token,
                SDKConstants.ASSESSMENTS_ENTITY + buildListString(ids) + "?" + this.buildQueryString(params), ids);
    }

    /**
     * Get a list of assessments for the given student id
     *
     * @param token
     * @param studentId
     * @return
     */
    @Override
    public List<GenericEntity> getAssessmentsForStudent(String token, String studentId) {
        return this.readEntityList(token, SDKConstants.STUDENTS_ENTITY + studentId + SDKConstants.STUDENT_ASSESSMENTS
                + "?" + this.buildQueryString(null), studentId);
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
        return this.readEntity(token, SDKConstants.ASSESSMENTS_ENTITY + id, id);
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
     * @return
     */
    @ExecutionTimeLogger.LogExecutionTime
    protected GenericEntity readCustomEntity(String token, String url) {
        GenericEntity entity = null;
        try {
            List<Entity> entityList = getClient(token).read(url);
            if (entityList.size() > 0) {
                entity = new GenericEntity(entityList.get(0));
            }
        } catch (SLIClientException e) {
            return null;
        } catch (MessageProcessingException e) {
            LOGGER.error("Exception occurred during API read", e);
        } catch (OAuthException e) {
            LOGGER.error("Exception occurred during API read", e);
        } catch (MalformedURLException e) {
            LOGGER.error("Exception occurred during API read", e);
        } catch (URISyntaxException e) {
            LOGGER.error("Exception occurred during API read", e);
        } catch (IOException e) {
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
    @Override
    @ExecutionTimeLogger.LogExecutionTime
    public GenericEntity readEntity(String token, String url) {
        GenericEntity entity = null;
        try {
            List<Entity> entityList = getClient(token).read(url);
            if (entityList.size() > 0) {
                Entity theEntity = entityList.get(0);
                entity = new GenericEntity(theEntity);
            }
        } catch (MessageProcessingException e) {
            LOGGER.error("Exception occurred during API read", e);
        } catch (OAuthException e) {
            LOGGER.error("Exception occurred during API read", e);
        } catch (MalformedURLException e) {
            LOGGER.error("Exception occurred during API read", e);
        } catch (URISyntaxException e) {
            LOGGER.error("Exception occurred during API read", e);
        } catch (IOException e) {
            LOGGER.error("Exception occurred during API read", e);
        } catch (SLIClientException e) {
            LOGGER.error("Exception occurred during API read", e);
        } catch (ArrayIndexOutOfBoundsException e) {
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
    // @ExecutionTimeLogger.LogExecutionTime
    protected GenericEntity readEntity(String token, String url, String id) {
        if ((id == null) || (id.length() <= 0)) {
            return null;
        } else {
            return readEntity(token, url);
        }
    }

    /**
     * Read a list of resource entities using the SDK
     *
     * @param token
     * @param url
     * @return
     */
    @ExecutionTimeLogger.LogExecutionTime
    @Override
    @CacheableUserData
    public List<GenericEntity> readEntityList(String token, String url) {
        List<GenericEntity> genericEntities = new ArrayList<GenericEntity>();
        try {
            List<Entity> entityList = getClient(token).read(url);
            for (Entity entity : entityList) {
                GenericEntity genericEntity = new GenericEntity(entity);
                genericEntity.put("links", mappifyLinks(entity.getLinks()));
                genericEntities.add(genericEntity);
            }
        } catch (SLIClientException e) {
            return Collections.EMPTY_LIST;
        } catch (MessageProcessingException e) {
            LOGGER.error("Exception occurred during API read", e);
        } catch (OAuthException e) {
            LOGGER.error("Exception occurred during API read", e);
        } catch (MalformedURLException e) {
            LOGGER.error("Exception occurred during API read", e);
        } catch (URISyntaxException e) {
            LOGGER.error("Exception occurred during API read", e);
        } catch (IOException e) {
            LOGGER.error("Exception occurred during API read", e);
        }
        return genericEntities;
    }

    /**
     * Get a list of generic entities using a list of ids, the calls to the API are done in batches
     * of MAX_IDS_PER_API_CALL entities at a time.
     *
     * @param token
     * @param ids
     * @param params
     * @return
     */
    @Override
    public List<GenericEntity> readEntityList(String token, List<String> ids, Map<String, String> params, String type) {
        List<GenericEntity> result = new ArrayList<GenericEntity>(ids.size());
        for (int i = 0; i <= ids.size() / Constants.MAX_IDS_PER_API_CALL; i++) {
            List<String> subList = ids.subList(i * Constants.MAX_IDS_PER_API_CALL,
                    Math.min((i + 1) * Constants.MAX_IDS_PER_API_CALL, ids.size()));
            List<GenericEntity> entityList = this.readEntityList(token,
                    type + buildListString(subList) + "?" + this.buildQueryString(params), subList);
            result.addAll(entityList);
        }

        return result;
    }

    private List<Map<String, String>> mappifyLinks(List<Link> realLinks) {
        // somewhere some code is dying because the links in the SDK are being
        // returned as Link
        // objects and not maps. I don't feel like digging through acres of code
        // to find it, so I'm
        // putting in this hack
        List<Map<String, String>> mapLinks = new ArrayList<Map<String, String>>();
        if (realLinks != null) {
            for (Link link : realLinks) {
                Map<String, String> mapLink = new HashMap<String, String>();
                mapLink.put("rel", link.getLinkName());
                mapLink.put("href", link.getResourceURL().toString());
                mapLinks.add(mapLink);
            }
        }
        return mapLinks;
    }

    /**
     * Read a list of resource entities using the SDK. This method checks id for
     * null or size == 0 and returns Collections.emptyList iff true.
     *
     * @param token
     * @param url
     * @param id
     * @return
     */
    protected List<GenericEntity> readEntityList(String token, String url, List id) {
        if (id == null || id.size() == 0) {
            return Collections.emptyList();
        } else {
            return readEntityList(token, url);
        }
    }

    /**
     * Read a list of resource entities using the SDK. This method checks id for
     * null or length == 0 and returns defaultList iff true.
     *
     * @param token
     * @param url
     * @param id
     * @return
     */
    // @ExecutionTimeLogger.LogExecutionTime
    protected List<GenericEntity> readEntityList(String token, String url, String id) {
        if (id == null || id.length() <= 0) {
            return Collections.emptyList();
        } else {
            return readEntityList(token, url);
        }
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
            getClient(token).create(entity, url);
        } catch (OAuthException e) {
            LOGGER.error("Exception occurred during API create", e);
        } catch (MalformedURLException e) {
            LOGGER.error("Exception occurred during API create", e);
        } catch (IOException e) {
            LOGGER.error("Exception occurred during API create", e);
        } catch (URISyntaxException e) {
            LOGGER.error("Exception occurred during API create", e);
        } catch (SLIClientException e) {
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
            getClient(token).update(entity);
        } catch (MessageProcessingException e) {
            LOGGER.error("Exception occurred during API update", e);
        } catch (OAuthException e) {
            LOGGER.error("Exception occurred during API update", e);
        } catch (MalformedURLException e) {
            LOGGER.error("Exception occurred during API update", e);
        } catch (URISyntaxException e) {
            LOGGER.error("Exception occurred during API update", e);
        } catch (IOException e) {
            LOGGER.error("Exception occurred during API update", e);
        } catch (SLIClientException e) {
            LOGGER.error("Exception occurred during API update", e);
        }
    }

    /**
     * Delete a resource entity using the SDK
     *
     * @param token
     * @param entity
     * @return
     */
    @ExecutionTimeLogger.LogExecutionTime
    protected void deleteEntity(String token, Entity entity) {
        try {
            getClient(token).delete(entity);
        } catch (MalformedURLException e) {
            LOGGER.error("Exception occurred during API delete", e);
        } catch (OAuthException e) {
            LOGGER.error("Exception occurred during API delete", e);
        } catch (URISyntaxException e) {
            LOGGER.error("Exception occurred during API delete", e);
        } catch (SLIClientException e) {
            LOGGER.error("Exception occurred during API delete", e);
        }
    }

    /*
     * *****************************************************
     * API Helper Methods *****************************************************
     */

    /**
     * Given a link in the API response, extract the entity's unique id
     *
<<<<<<< HEAD
     * @param link
=======
     * @param path
>>>>>>> master
     * @return
     */
    private String parseId(String path) {
        String id;
        int index = path.lastIndexOf("/");
        id = path.substring(index + 1);
        return id;
    }

    /**
     * Extract the specified attribute's value from each entity in the given
     * entity list
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
                if (link.get(Constants.ATTR_REL).toString().contains(rel)) {
                    String href = (String) link.get(Constants.ATTR_HREF);
                    linkList.add(href);
                }
            }
        }

        return linkList;
    }

    /**
     * Enrich section entities with session details to be leveraged during
     * filtering
     *
     * @param token
     * @param sections
     */
    private void enrichSectionsWithSessionDetails(String token, String schoolId, List<GenericEntity> sections) {

        List<GenericEntity> sessions = this.getSessions(token, schoolId, null);
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
     * Process sections to ensure section name and filter historical data if
     * specified
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

        if (sections != null && sections.size() > 0) {

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

                            // Add filtered section if grace period adjusted
                            // date is before
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
        // There is no direct course-school association in ed-fi. For any
        // section associated to
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
    @Override
    public List<GenericEntity> getCourseSectionMappings(List<GenericEntity> sections, String schoolId, String token) {
        Map<String, GenericEntity> courseMap = new HashMap<String, GenericEntity>();
        Map<String, String> sectionIDToCourseIDMap = new HashMap<String, String>();

        // this temporary sectionLookup will be used for cross reference between
        // courseId and
        // section.
        Map<String, Set<GenericEntity>> sectionLookup = new HashMap<String, Set<GenericEntity>>();

        // iterate each section
        if (sections != null) {

            Map<String, String> courseOfferingToCourseIDMap = new HashMap<String, String>();
            String url = "";
            if (schoolId != null) {
                url = SDKConstants.SCHOOLS_ENTITY + schoolId;
            }
            // find the course for each course offering
            List<GenericEntity> courseOfferings = readEntityList(token, url + SDKConstants.COURSE_OFFERINGS + "?"
                    + this.buildQueryString(null));
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
                    sectionLookup.put(courseId, new TreeSet<GenericEntity>(new GenericEntityComparator(
                            Constants.ATTR_SECTION_NAME, String.class)));
                }
                sectionLookup.get(courseId).add(section);
            }

            // get all the 'ancestor' ed org ids
            List<String> edOrgIds = getEdorgHierarchy(schoolId, token);

            // get course Entity
            List<GenericEntity> courses = getCoursesForEdorgs(edOrgIds, token);

            // update courseMap with courseId. "id" for this entity
            for (GenericEntity course : courses) {
                // Add course to courseMap
                // courseMap.put(course.getId(), course);
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

        List<GenericEntity> courses = new ArrayList<GenericEntity>(courseMap.values());
        Collections.sort(courses, new GenericEntityComparator(Constants.ATTR_COURSE_TITLE, String.class));
        return courses;
    }

    /**
     * Gets the list of edorg id's in the edorg hierarchy
     *
     * @param schoolId
     * @param token
     * @return
     */
    private List<String> getEdorgHierarchy(String schoolId, String token) {
        List<String> ids = new ArrayList<String>();
        final String rootUrl = "/" + PathConstants.EDUCATION_ORGANIZATIONS + "/";

        String currentEdOrgId = schoolId;

        // iteratively visit each parent edorg and cache their ids
        while (currentEdOrgId != null) {
            ids.add(currentEdOrgId);

            String url = rootUrl + currentEdOrgId;
            GenericEntity edorg = readEntity(token, url);
            if (edorg == null) {
                currentEdOrgId = null;
            } else {
                currentEdOrgId = (String) edorg.get(Constants.ATTR_PARENT_EDORG);
            }
        }

        return ids;
    }


    private List<GenericEntity> getCoursesForEdorgs(List<String> edorgIds, String token) {

        // Collect all courses in hashmap in order to filter out possible duplicates
        Map<String,GenericEntity> allCourses = new HashMap<String,GenericEntity>();

        for (String edOrgId : edorgIds) {

            String url = "/" + PathConstants.EDUCATION_ORGANIZATIONS + "/" + edOrgId + "/" + PathConstants.COURSES ;
            // get course Entity
            List<GenericEntity> courses = readEntityList(token,
                    url + "?" + this.buildQueryString(null));

            for (GenericEntity course: courses){
                String id = (String)course.get("id");
                allCourses.put(id, course);
            }
        }
        return new ArrayList<GenericEntity>(allCourses.values());
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
            Collections.sort(courses, new Comparator<GenericEntity>() {

                @Override
                public int compare(GenericEntity o1, GenericEntity o2) {
                    return o1.getString("coursesName").compareTo(o2.getString("coursesName"));
                }

            });

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
        return (items == null) ? "" : StringUtils.join(items, ",");
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

        Map<String, String> localParams = params;

        // Setup defaults including paging disabled
        if (localParams == null) {
            localParams = new HashMap<String, String>();
        }
        if (!localParams.containsKey(Constants.LIMIT)) {
            localParams.put(Constants.LIMIT, String.valueOf(Constants.MAX_RESULTS));
        }

        for (Map.Entry<String, String> e : localParams.entrySet()) {
            query.append(separator);
            separator = "&";

            query.append(e.getKey());
            query.append("=");
            query.append(e.getValue());
        }

        return query.toString();
    }

    public SLIClient getClient(String sessionToken) throws OAuthException, MalformedURLException, URISyntaxException {
        return clientFactory.getClientWithSessionToken(sessionToken);
    }
}

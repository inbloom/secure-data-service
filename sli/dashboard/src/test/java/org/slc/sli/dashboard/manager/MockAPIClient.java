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

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.slc.sli.api.client.SLIClient;
import org.slc.sli.dashboard.client.APIClient;
import org.slc.sli.dashboard.client.SDKAPIClient;
import org.slc.sli.dashboard.entity.ConfigMap;
import org.slc.sli.dashboard.entity.GenericEntity;
import org.slc.sli.dashboard.util.Constants;

/**
 *
 * A mock API client. Reads json data from local files, instead of calling an API server.
 *
 */
public class MockAPIClient extends SDKAPIClient implements APIClient {

    private static final Logger LOG = LoggerFactory.getLogger(MockAPIClient.class);

    private ClassLoader classLoader;

    // Mock Data Files
    private static final String MOCK_DATA_DIRECTORY = "mock_data/";
    private static final String MOCK_ENROLLMENT_FILE = "school.json";
    private static final String MOCK_STUDENTS_FILE = "student.json";
    private static final String MOCK_PROGRAMS_FILE = "student_program_association.json";
    private static final String MOCK_ASSESSMENT_METADATA_FILE = "assessment_meta_data.json";
    private static final String MOCK_ASSESSMENTS_FILE = "assessment.json";
    private static final String MOCK_ATTENDANCE_FILE = "attendance.json";
    private static final String MOCK_ED_ORG_FILE = "educational_organization.json";
    private static final String MOCK_ED_ORG_ASSOC_FILE = "educational_organization_association.json";
    private static final String MOCK_SCHOOL_ED_ORG_ASSOC_FILE = "school_educational_organization_association.json";

    public MockAPIClient() {
        this.classLoader = Thread.currentThread().getContextClassLoader();
    }

    public SLIClient getSdkClient() {
        return null;
    }

    @Override
    public String getId(String token) {
        return null;
    }

    /**
     *
     * Mock API client does not support new Staff API call.
     *
     */
    @Override
    public GenericEntity getStaffWithEducationOrganization(String token, String id, String organizationCategory) {
        return this.getEntity(token, getFilename(MOCK_DATA_DIRECTORY + "common/staffInfo.json"), "IT Admin");
    }

    /**
     *
     * Mock API client does not support custom data API calls.
     *
     */
    @Override
    public ConfigMap getEdOrgCustomData(String token, String id) {
        return null;
    }

    /**
     *
     * Mock API client does not support custom data API calls.
     *
     */
    @Override
    public void putEdOrgCustomData(String token, String id, ConfigMap configMap) {
        //No Op
    }

    @Override
    public GenericEntity getStudent(final String token, String studentId) {
        return this.getEntity(token, getFilename(MOCK_DATA_DIRECTORY + token + "/" + MOCK_STUDENTS_FILE), studentId);
    }

    @Override
    public List<GenericEntity> getStudentsForSection(String token, String sectionId) {
        return getStudents(token, new LinkedList<String>());
    }

    @Override
    public List<GenericEntity> getStudentsForSectionWithGradebookEntries(final String token, final String sectionId) {
        return null;
    }


    @Override
    public GenericEntity getStudentWithOptionalFields(String token, String studentId, List<String> optionalFields) {
        return this.getEntity(token, getFilename(MOCK_DATA_DIRECTORY + "common/" + "student_transcript.json"), studentId);
    }

    //@Override
    public List<GenericEntity> getStudents(final String token, Collection<String> studentIds) {
        return this.getEntities(token, getFilename(MOCK_DATA_DIRECTORY + token + "/" + MOCK_STUDENTS_FILE), studentIds);
    }

    @Override
    public List<GenericEntity> getSchools(final String token, List<String> schoolIds) {
        return this
                .getEntities(token, getFilename(MOCK_DATA_DIRECTORY + token + "/" + MOCK_ENROLLMENT_FILE), schoolIds);
    }

    @Override
    public List<GenericEntity> getAssessmentsForStudent(final String token, String studentId) {

        // get all assessments in the file. this is very inefficient, since we're reading the whole
        // file each time, but only
        // grabbing assmts for one student. not sure of a good way around it at the moment.
        List<GenericEntity> studentAssmts = this.getEntities(token, getFilename(MOCK_DATA_DIRECTORY + token + "/"
                + MOCK_ASSESSMENTS_FILE), null);
        List<GenericEntity> filteredAssmts = new ArrayList<GenericEntity>();

        // filter by the student id
        for (GenericEntity studentAssmt : studentAssmts) {
            if (studentAssmt.getString(Constants.ATTR_STUDENT_ID).equals(studentId)) {
                filteredAssmts.add(studentAssmt);
            }
        }

        return filteredAssmts;
    }

    /*
     * We aren't going to bother with this for now.
     */
    @Override
    public List<GenericEntity> getAttendanceForStudent(String token, String studentId, Map<String, String> params) {
        return new ArrayList<GenericEntity>();
    }

    @Override
    public GenericEntity getSession(String token, String sessionId) {
        GenericEntity session = new GenericEntity();
        session.put("beginDate", "2010-01-01");
        session.put("endDate", "2011-12-31");
        return session;
    }

    @Override
    public List<GenericEntity> getSessions(String token, String schoolId, Map<String, String> params) {
        return new ArrayList<GenericEntity>();
    }

    @Override
    public List<GenericEntity> getSessionsForYear(String token, String schoolYear) {
        return new ArrayList<GenericEntity>();
    }


    @Override
    public List<GenericEntity> getAcademicRecordsForStudent(String token, String studentId, Map<String, String> params) {
        return null;
    }


    @Override
    public List<GenericEntity> getAssessments(final String token, List<String> assessmentIds, Map<String, String> params) {

        return this.getEntities(token, getFilename(MOCK_DATA_DIRECTORY + MOCK_ASSESSMENT_METADATA_FILE), null);
    }

    @Override
    public GenericEntity getParentEducationalOrganization(final String token, GenericEntity edOrgOrSchool) {
        // Find the parent ed-org's name.
        String parentEdOrgId = edOrgOrSchool.getString(Constants.ATTR_PARENT_EDORG);
        return getEducationOrganization(token, parentEdOrgId);
    }

    // helper, to find an ed-org entity.
    private GenericEntity getEducationOrganization(final String token, String id) {
        List<GenericEntity> allEdOrgs = this.getEntities(token, getFilename(MOCK_DATA_DIRECTORY + token + "/"
                + MOCK_ED_ORG_FILE), null);
        if (id == null) {
            return null;
        }
        for (int i = 0; i < allEdOrgs.size(); i++) {
            if (id.equals(allEdOrgs.get(i).get(Constants.ATTR_ID))) {
                return allEdOrgs.get(i);
            }
        }
        // an unknown ed-org
        return null;
    }

    /**
     * Helper function to translate a .json file into object.
     * TODO: remove this after assessment meta data is switched to use the generic entity
     */

    public static <T> T[] fromFile(String fileName, Class<T[]> c) {

        BufferedReader bin = null;

        try {
            FileReader filein;
            filein = new FileReader(fileName);
            bin = new BufferedReader(filein);
            String s, total;
            total = "";
            while ((s = bin.readLine()) != null) {
                total += s;
            }
            Gson gson = new Gson();
            T[] temp = gson.fromJson(total, c);
            return temp;

        } catch (IOException e) {
            return null;
        } finally {
            IOUtils.closeQuietly(bin);
        }
    }

    /**
     * Get the list of entities identified by the entity id list and authorized for the security
     * token
     *
     * @param token
     *            - the principle authentication token
     * @param filePath
     *            - the file containing the JSON entities representation
     * @param entityIds
     *            - the list of entity ids
     * @return entityList
     *         - the entity list
     */
    public List<GenericEntity> getEntities(final String token, String filePath, Collection<String> entityIds) {

        // Get all the entities for the user identified by token
        List<GenericEntity> entities = fromFile(filePath);

        // Filter entities according to the entity id list
        List<GenericEntity> filteredEntities = new ArrayList<GenericEntity>();
        if (entityIds != null) {
            for (GenericEntity entity : entities) {
                if (entityIds.contains(entity.get(Constants.ATTR_ID))) {
                    filteredEntities.add(entity);
                }
            }
        } else {
            filteredEntities.addAll(entities);
        }

        return filteredEntities;
    }

    /**
     * Get the entity identified by the entity id and authorized for the security token
     *
     * @param token
     *            - the principle authentication token
     * @param filePath
     *            - the file containing the JSON entities representation
     * @param id
     *            - the entity id
     * @return entity
     *         - the entity entity
     */
    public GenericEntity getEntity(final String token, String filePath, String id) {

        // Get all the entities for the user identified by token
        List<GenericEntity> entities = fromFile(filePath);

        // Select entity identified by id
        if (id != null) {
            for (GenericEntity entity : entities) {
                if (id.equals(entity.get(Constants.ATTR_ID))) {
                    return entity;
                }
            }
        }

        return null;
    }

    /**
     * In mock data, each student only exists in one section
     * Retrieves the population hierarchy and returns the section containing the student, populated
     * with minimal data
     */
    @Override
    public GenericEntity getSectionHomeForStudent(String token, String studentId) {
        List<GenericEntity> hierarchy = getSchools(token, null);

        for (GenericEntity school : hierarchy) {
            List<LinkedHashMap> courses = school.getList(Constants.ATTR_COURSES);

            for (LinkedHashMap course : courses) {
                List<LinkedHashMap> sections = (List<LinkedHashMap>) course.get(Constants.ATTR_SECTIONS);

                for (LinkedHashMap section : sections) {
                    List<String> studentUIDs = (List<String>) section.get(Constants.ATTR_STUDENT_UIDS);
                    if (studentUIDs.contains(studentId)) {
                        GenericEntity sectionEntity = new GenericEntity();
                        sectionEntity.put(Constants.ATTR_UNIQUE_SECTION_CODE, section.get(Constants.ATTR_SECTION_NAME));
                        sectionEntity.put(Constants.ATTR_ID, section.get(Constants.ATTR_SECTION_NAME));
                        return sectionEntity;
                    }
                }
            }

        }
        return null;
    }

    /**
     * Returns teacher with only name object, with first, last, middle names, and prefix populated
     * Token is the username of logged in user, we use it to populate the name
     */
    @Override
    public GenericEntity getTeacherForSection(String token, String sectionId) {
        GenericEntity name = new GenericEntity();
        name.put(Constants.ATTR_FIRST_NAME, token);
        name.put(Constants.ATTR_LAST_SURNAME, "");
        name.put(Constants.ATTR_MIDDLE_NAME, "");
        name.put(Constants.ATTR_PERSONAL_TITLE_PREFIX, "Dr");
        GenericEntity teacher = new GenericEntity();
        teacher.put(Constants.ATTR_NAME, name);
        return teacher;
    }

    /**
     * Retrieves an entity list from the specified file
     * and instantiates from its JSON representation
     *
     * @param filePath
     *            - the file path to persist the view component XML string representation
     * @return entityList
     *         - the generic entity list
     */
    public List<GenericEntity> fromFile(String filePath) {
        List<GenericEntity> entityList = new ArrayList<GenericEntity>();

        BufferedReader reader = null;

        try {

            // Read JSON file
            reader = new BufferedReader(new FileReader(filePath));
            StringBuffer jsonBuffer = new StringBuffer();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonBuffer.append(line);
            }

            // Parse JSON
            Gson gson = new Gson();
            List<Map> maps = gson.fromJson(jsonBuffer.toString(), ArrayList.class);

            for (Map<String, Object> map : maps) {
                entityList.add(new GenericEntity(map));
            }

        } catch (FileNotFoundException e) {
            LOG.error(e.getMessage());
        } catch (IOException e) {
            LOG.error(e.getMessage());
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                LOG.error(e.getMessage());
            }
        }

        return entityList;
    }

    public String getFilename(String filename) {
        URL url = classLoader.getResource(filename);
        return url == null ? null : url.getFile();
    }

    @Override
    public List<GenericEntity> getCoursesForStudent(String token, String studentId, Map<String, String> params) {
        return null;
    }

    @Override
    public List<GenericEntity> getTranscriptsForStudent(String token, String studentId,
            Map<String, String> params) {
        return null;
    }

    @Override
    public List<GenericEntity> getSectionsForStudent(String token, String studentId, Map<String, String> params) {
        return null;
    }

    @Override
    public GenericEntity getEntity(String token, String type, String id, Map<String, String> params) {
        return null;
    }

    @Override
    public List<GenericEntity> getEnrollmentForStudent(final String token, String studentId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<GenericEntity> getStudentsWithSearch(String token,
            String firstName, String lastName, String schoolId) {
        // TODO Auto-generated method stub
        return null;
    }


    @Override
    public List<GenericEntity> getEntities(String token, String type, String id, Map<String, String> params) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<GenericEntity> getParentEducationalOrganizations(String token,
            List<GenericEntity> educationalOrganizations) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * Return a url with the sortBy parameter
     * @param url
     * @param sortBy
     * @return
     */
    public String sortBy(String url, String sortBy) {
        return url + "?sortBy=" + sortBy;
    };

    /**
     * Return a url with the sortBy and sortOrder parameter
     * @param url
     * @param sortBy
     * @param sortOrder
     *          "descending" or "ascending"
     * @return
     */
    public String sortBy(String url, String sortBy, String sortOrder) {
        return url + "?sortBy=" + sortBy + "&sortOrder=" + sortOrder;
    }

    public List<GenericEntity> getParentsForStudent(String token, String studentId) {
        // TODO Auto-generated method stub
        return null;
    };

}

package org.slc.sli.client;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slc.sli.entity.GenericEntity;
import org.slc.sli.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

/**
 * 
 * A mock API client. Reads json data from local files, instead of calling an API server.
 * 
 */
public class MockAPIClient implements APIClient {

    private static Logger log = LoggerFactory.getLogger(MockAPIClient.class);
    
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
    
    @Override
    public GenericEntity getStudent(final String token, String studentId) {
        return this.getEntity(token, getFilename(MOCK_DATA_DIRECTORY + token + "/" + MOCK_STUDENTS_FILE), studentId);
    }
    
    @Override
    public List<GenericEntity> getStudents(final String token, List<String> studentIds) {
        return this.getEntities(token, getFilename(MOCK_DATA_DIRECTORY + token + "/" + MOCK_STUDENTS_FILE), studentIds);
    }

    @Override
    public List<GenericEntity> getSchools(final String token, List<String> schoolIds) {
        return this
                .getEntities(token, getFilename(MOCK_DATA_DIRECTORY + token + "/" + MOCK_ENROLLMENT_FILE), schoolIds);
    }

    @Override
    public List<GenericEntity> getStudentAssessments(final String token, String studentId) {
        
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
    public List<GenericEntity> getStudentAttendance(String token, String studentId, String start, String end) {
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
    public List<GenericEntity> getSessionsByYear(String token, String schoolYear) {
        return new ArrayList<GenericEntity>();
    }

    @Override
    public List<GenericEntity> getAssessments(final String token, List<String> assessmentIds) {
        
        return this.getEntities(token, getFilename(MOCK_DATA_DIRECTORY + MOCK_ASSESSMENT_METADATA_FILE), null);
    }
    
    @Override
    public List<GenericEntity> getCustomData(String token, String key) {
        return this.getEntities(token, getFilename(MOCK_DATA_DIRECTORY + token + "/custom_" + key + ".json"), null);
    }
    
    @Override
    public List<GenericEntity> getPrograms(final String token, List<String> studentIds) {
        // TODO: student id logic isn't working yet. for now, pass in null.
        return this.getEntities(token, getFilename(MOCK_DATA_DIRECTORY + token + "/" + MOCK_PROGRAMS_FILE), null);
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
            System.err.println(e);
            return null;

        } finally {

            try {
                if (bin != null) {
                    bin.close();
                }
            } catch (Exception e) {
                System.err.println(e);
            }
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
    public List<GenericEntity> getEntities(final String token, String filePath, List<String> entityIds) {
        
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
     * Retrieves the population hierarchy and returns the section containing the student, populated with minimal data
     */
    public GenericEntity getHomeRoomForStudent(String studentId, String token) {
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
    public GenericEntity getTeacherForSection(String sectionId, String token) {
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
            List<Map> maps = gson.fromJson(jsonBuffer.toString(), new ArrayList<Map>().getClass());
            
            for (Map<String, Object> map : maps) {
                entityList.add(new GenericEntity(map));
            }

        } catch (IOException e) {
            log.error(e.getMessage());
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
        
        return entityList;
    }
    
    public String getFilename(String filename) {
        URL url = classLoader.getResource(filename);
        return url.getFile();
    }

    @Override
    public List<GenericEntity> getCourses(String token, String studentId, Map<String, String> params) {
        return null;
    }

    @Override
    public List<GenericEntity> getStudentTranscriptAssociations(String token, String studentId,
            Map<String, String> params) {
        return null;
    }

    @Override
    public List<GenericEntity> getSections(String token, String studentId, Map<String, String> params) {
        return null;
    }

    @Override
    public GenericEntity getEntity(String token, String type, String id, Map<String, String> params) {
        return null;
    }
    
    @Override
    public List<GenericEntity> getStudentSectionGradebookEntries(final String token, final String studentId, Map<String, String> params) {
        return null;
    }

}

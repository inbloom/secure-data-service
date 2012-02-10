package org.slc.sli.client;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import com.google.gson.Gson;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.slc.sli.entity.GenericEntity;
import org.slc.sli.entity.assessmentmetadata.AssessmentMetaData;
import org.slc.sli.util.Constants;


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
    public List<GenericEntity> getStudents(final String token, List<String> studentIds) {
        return this.getEntities(token, getFilename(MOCK_DATA_DIRECTORY + token + "/" + MOCK_STUDENTS_FILE), studentIds);
    }

    @Override
    public List<GenericEntity> getSchools(final String token, List<String> schoolIds) {
        return this.getEntities(token, getFilename(MOCK_DATA_DIRECTORY + token + "/" + MOCK_ENROLLMENT_FILE), schoolIds);
    }

    @Override
    public List<GenericEntity> getAssessments(final String token, List<String> studentIds) {
        // TODO: the logic for filtering by student id isn't working right now, so just passing in null 
        return this.getEntities(token, getFilename(MOCK_DATA_DIRECTORY + token + "/" + MOCK_ASSESSMENTS_FILE), null);
    }

    @Override
    public List<GenericEntity> getCustomData(String token, String key) {
        return this.getEntities(token, getFilename(MOCK_DATA_DIRECTORY + token + "/custom_" + key + ".json"), null);
    }

    @Override
    public AssessmentMetaData[] getAssessmentMetaData(final String token) {
        return fromFile(getFilename(MOCK_DATA_DIRECTORY + MOCK_ASSESSMENT_METADATA_FILE), AssessmentMetaData[].class);
    }

    @Override
    public List<GenericEntity> getPrograms(final String token, List<String> studentIds) {
        // TODO: student id logic isn't working yet. for now, pass in null.
        return this.getEntities(token, getFilename(MOCK_DATA_DIRECTORY + token + "/" + MOCK_PROGRAMS_FILE), null);
    }

    @Override
    public List<GenericEntity> getAssociatedEducationalOrganizations(final String token, GenericEntity school) {
        
        List<GenericEntity> allEdOrgs = this.getEntities(token, getFilename(MOCK_DATA_DIRECTORY + token + "/" + MOCK_ED_ORG_FILE), null);
        List<GenericEntity> allAssociations = this.getEntities(token, getFilename(MOCK_DATA_DIRECTORY + token + "/" + MOCK_SCHOOL_ED_ORG_ASSOC_FILE), null);
        // create a set of associated ed org ids, and then filter the ed or entities based on it.
        Set<String> associatedEdOrgIds = new HashSet<String>();
        for (int i = 0; i < allAssociations.size(); i++) {
            if (school.get(Constants.ATTR_ID) != null && school.get(Constants.ATTR_ID).equals(allAssociations.get(i).get(Constants.ATTR_SCHOOL_ID))) {
                associatedEdOrgIds.add((String) (allAssociations.get(i).get(Constants.ATTR_ED_ORG_ID)));
            }
        }
        Vector<GenericEntity> filtered = new Vector<GenericEntity>();
        for (int i = 0; i < allEdOrgs.size(); i++) {
            if (associatedEdOrgIds.contains(allEdOrgs.get(i).get(Constants.ATTR_ID))) {
                filtered.add(allEdOrgs.get(i));
            }
        }

        return filtered;
    }

    @Override
    public List<GenericEntity> getParentEducationalOrganizations(final String token, GenericEntity edOrg) {
        List<GenericEntity> allEdOrgs = this.getEntities(token, getFilename(MOCK_DATA_DIRECTORY + token + "/" + MOCK_ED_ORG_FILE), null);
        List<GenericEntity> allAssociations = this.getEntities(token, getFilename(MOCK_DATA_DIRECTORY + token + "/" + MOCK_ED_ORG_ASSOC_FILE), null);
        // create a set of associated ed org ids, and then filter the ed or entities based on it.
        Set<String> parentEdOrgIds = new HashSet<String>();
        for (int i = 0; i < allAssociations.size(); i++) {
            if (edOrg.get(Constants.ATTR_ID) != null && edOrg.get(Constants.ATTR_ID).equals(allAssociations.get(i).get("educationOrganizationChildId"))) {
                parentEdOrgIds.add((String) (allAssociations.get(i).get(Constants.ATTR_ED_ORG_PARENT_ID)));
            }
        }
        Vector<GenericEntity> filtered = new Vector<GenericEntity>();
        for (int i = 0; i < allEdOrgs.size(); i++) {
            if (parentEdOrgIds.contains(allEdOrgs.get(i).get(Constants.ATTR_ID))) {
                filtered.add(allEdOrgs.get(i));
            }
        }
        
        return filtered;
    }

    /**
     *  Helper function to translate a .json file into object.
     *  TODO: remove this after assessment meta data is switched to use the generic entity
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
     * Get the list of entities identified by the entity id list and authorized for the security token
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

}

package org.slc.sli.client;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slc.sli.api.client.Entity;
import org.slc.sli.api.client.EntityCollection;
import org.slc.sli.api.client.Link;
import org.slc.sli.api.client.SLIClient;
import org.slc.sli.api.client.impl.BasicClient;
import org.slc.sli.api.client.impl.BasicQuery;
import org.slc.sli.entity.ConfigMap;
import org.slc.sli.entity.GenericEntity;
import org.slc.sli.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This client will use the SDK client to communicate with the SLI API.
 *
 * @author dwalker
 *
 */
public class SDKAPIClient implements APIClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(SDKAPIClient.class);

    /**
     * Dashboard client to API
     */
    private APIClient liveApiClient;

    /**
     * SDK Client to API
     */
    private SLIClient sdkClient;

    /**
     * The API URL
     */
    private String apiUrl;


    /**
     * The Grace Period
     */
    private String gracePeriod;

    public APIClient getLiveApiClient() {
        return liveApiClient;
    }

    public SLIClient getSdkClient() {
        return sdkClient;
    }

    public void setLiveApiClient(APIClient liveApiClient) {
        this.liveApiClient = liveApiClient;
    }

    public void setSdkClient(SLIClient sdkClient) {
        this.sdkClient = sdkClient;
    }

    public String getApiUrl() {
        return apiUrl + Constants.API_PREFIX;
    }

    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }

    public void setGracePeriod(String gracePeriod) {
        this.gracePeriod = gracePeriod;
    }

    public String getGracePeriod() {
        return this.gracePeriod;
    }

    @Override
    public GenericEntity getStaffInfo(String token) {
        // TODO Auto-generated method stub
        return liveApiClient.getStaffInfo(token);
    }

    @Override
    public ConfigMap getEdOrgCustomData(String token, String id) {
        ConfigMap configMap = null;
        try {
            List entityList = new ArrayList();
            sdkClient.read(entityList, ClientConstants.SDK_EDORGS_URL + id + ClientConstants.CUSTOM_DATA, ConfigMap.class);
            if (entityList.size() > 0) {
                configMap = (ConfigMap) entityList.get(0);
            }
        } catch (URISyntaxException e) {
            LOGGER.error("Exception occurred", e);
        } catch (MalformedURLException e) {
            LOGGER.error("Exception occurred", e);
        }
        return configMap;
    }

    @Override
    public void putEdOrgCustomData(String token, String id, ConfigMap configMap) {
        try {
            Map<String, Object> entityMap = new HashMap<String, Object>();
            entityMap.put("config", configMap.getConfig());
            Entity configMapEntity = new GenericEntity(entityMap);
            sdkClient.create(ClientConstants.SDK_EDORGS_URL + id + ClientConstants.CUSTOM_DATA, configMapEntity);
        } catch (URISyntaxException e) {
            LOGGER.error("Exception occurred", e);
        } catch (MalformedURLException e) {
            LOGGER.error("Exception occurred", e);
        }
    }

    @Override
    public List<GenericEntity> getSchools(String token, List<String> schoolIds) {
        // TODO Auto-generated method stub
        return liveApiClient.getSchools(token, schoolIds);
    }

    @Override
    public GenericEntity getStudent(String token, String id) {
        // TODO Auto-generated method stub
        ((BasicClient) sdkClient).setToken(token);
        EntityCollection collection = new EntityCollection();
        try {
            String url = getApiUrl() + ClientConstants.STUDENTS_URL + id;
            sdkClient.getResource(collection, new URL(url), BasicQuery.EMPTY_QUERY);
        } catch (URISyntaxException e) {
            LOGGER.error("Exception occurred", e);
        } catch (MalformedURLException e) {
            LOGGER.error("Exception occurred", e);
        }

        return convertEntity(collection).get(0);
//        return liveApiClient.getStudent(token, id);
    }

    @Override
    public List<GenericEntity> getStudentAssessments(String token, String studentId) {
        // TODO Auto-generated method stub
        return liveApiClient.getStudentAssessments(token, studentId);

    }

    @Override
    public List<GenericEntity> getAssessments(String token, List<String> assessmentIds) {
        // TODO Auto-generated method stub
        return liveApiClient.getAssessments(token, assessmentIds);
    }

    @Override
    public List<GenericEntity> getStudentAttendance(String token, String studentId, String start, String end) {
        // TODO Auto-generated method stub
        return liveApiClient.getStudentAttendance(token, studentId, start, end);
    }

    @Override
    public GenericEntity getParentEducationalOrganization(String token, GenericEntity educationalOrganization) {
        // TODO Auto-generated method stub
        return liveApiClient.getParentEducationalOrganization(token, educationalOrganization);
    }

    @Override
    public List<GenericEntity> getParentEducationalOrganizations(String token,
            List<GenericEntity> educationalOrganizations) {
        // TODO Auto-generated method stub
        return liveApiClient.getParentEducationalOrganizations(token, educationalOrganizations);
    }

    @Override
    public List<GenericEntity> getStudentEnrollment(String token, GenericEntity student) {
        // TODO Auto-generated method stub
        return liveApiClient.getStudentEnrollment(token, student);
    }

    @Override
    public List<GenericEntity> getStudentsWithGradebookEntries(String token, String sectionId) {
        // TODO Auto-generated method stub
        return liveApiClient.getStudentsWithGradebookEntries(token, sectionId);
    }

    @Override
    public List<GenericEntity> getStudentsWithSearch(String token, String firstName, String lastName) {
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
        String url = getApiUrl() + ClientConstants.STUDENTS_URL;

        if (queryExists) {
            url += queryString + "&limit=0";
        }

        ((BasicClient) sdkClient).setToken(token); // TODO - Remove, once Robert checks his code in.
        EntityCollection collection = new EntityCollection();
        try {
            sdkClient.getResource(collection, new URL(url), BasicQuery.EMPTY_QUERY);
        } catch (URISyntaxException e) {
            LOGGER.error("Exception occurred", e);
        } catch (MalformedURLException e) {
            LOGGER.error("Exception occurred", e);
        }


        return convertEntity(collection);
    }

    @Override
    public GenericEntity getStudentWithOptionalFields(String token, String studentId, List<String> optionalFields) {
        // TODO Auto-generated method stub
        return liveApiClient.getStudentWithOptionalFields(token, studentId, optionalFields);
    }

    @Override
    public List<GenericEntity> getCourses(String token, String studentId, Map<String, String> params) {
        // TODO Auto-generated method stub
        return liveApiClient.getCourses(token, studentId, params);
    }

    @Override
    public List<GenericEntity> getStudentTranscriptAssociations(String token, String studentId,
            Map<String, String> params) {
        // TODO Auto-generated method stub
        return liveApiClient.getStudentTranscriptAssociations(token, studentId, params);
    }

    @Override
    public List<GenericEntity> getSections(String token, String studentId, Map<String, String> params) {
        // TODO Auto-generated method stub
        return liveApiClient.getSections(token, studentId, params);
    }

    @Override
    public GenericEntity getEntity(String token, String type, String id, Map<String, String> params) {
        // TODO Auto-generated method stub
        return liveApiClient.getEntity(token, type, id, params);
    }

    @Override
    public List<GenericEntity> getEntities(String token, String type, String id, Map<String, String> params) {
        // TODO Auto-generated method stub
        return liveApiClient.getEntities(token, type, id, params);
    }

    @Override
    public List<GenericEntity> getStudentSectionGradebookEntries(String token, String studentId,
            Map<String, String> params) {
        // TODO Auto-generated method stub
        return liveApiClient.getStudentSectionGradebookEntries(token, studentId, params);
    }

    @Override
    public List<GenericEntity> getStudents(String token, String sectionId, List<String> studentIds) {
        ((BasicClient) sdkClient).setToken(token); // TODO - Remove, once Robert checks his code in.
        EntityCollection collection = new EntityCollection();
        try {
            String url = getApiUrl() + ClientConstants.SECTIONS_URL + sectionId + ClientConstants.STUDENT_SECTION_ASSOC
                    + ClientConstants.STUDENTS + "?optionalFields=assessments,attendances.1,"
                    + Constants.ATTR_TRANSCRIPT + ",gradebook";
            sdkClient.getResource(collection, new URL(url), BasicQuery.EMPTY_QUERY);
        } catch (URISyntaxException e) {
            LOGGER.error("Exception occurred", e);
        } catch (MalformedURLException e) {
            LOGGER.error("Exception occurred", e);
        }

        return convertEntity(collection);
    }

    /**
     * Converts a collection of SDK entities to a Collection of Dashboard entities.
     * @param collection
     * @return
     */
    public List<GenericEntity> convertEntity(EntityCollection collection) {
        List<GenericEntity> entities = new ArrayList<GenericEntity>();
        for (Entity col : collection) {
            entities.add(convertEntity(col));
        }
        return entities;
    }

    /**
     * Converts a single SDK Entity to a Dashboard Generic Entity.
     * @param entity
     * @return
     */
    public GenericEntity convertEntity(Entity entity) {
        Map<String, Object> data = entity.getData();
        GenericEntity ge = new GenericEntity(data);
        ge.put(Constants.ATTR_LINKS, convertLinks(entity));
        return ge;
    }

    /**
     * Extract the link with the given relationship from an entity
     */
    private static List<Map<String, Object>> convertLinks(Entity entity) {
        List<Link> links = entity.getLinks();
        List<Map<String, Object>> retVal = new ArrayList<Map<String, Object>>();
        for (Link link : links) {
            Map<String, Object> map = new LinkedHashMap<String, Object>();
            map.put(Constants.ATTR_REL, link.getLinkName());
            map.put(Constants.ATTR_HREF, link.getResourceURL().getPath());
        }
        return retVal;
    }

    @Override
    public GenericEntity getTeacherForSection(String sectionId, String token) {
        // TODO Auto-generated method stub
        return liveApiClient.getTeacherForSection(sectionId, token);
    }

    @Override
    public GenericEntity getHomeRoomForStudent(String studentId, String token) {
        // TODO Auto-generated method stub
        return liveApiClient.getHomeRoomForStudent(studentId, token);
    }

    @Override
    public GenericEntity getSession(String token, String sessionId) {
        // TODO Auto-generated method stub
        return liveApiClient.getSession(token, sessionId);
    }

    @Override
    public List<GenericEntity> getSessions(String token) {
        // TODO Auto-generated method stub
        return liveApiClient.getSessions(token);
    }

    @Override
    public List<GenericEntity> getSessionsByYear(String token, String schoolYear) {
        // TODO Auto-generated method stub
        return liveApiClient.getSessionsByYear(token, schoolYear);
    }

    @Override
    public String sortBy(String url, String sortBy) {
        // TODO Auto-generated method stub
        return liveApiClient.sortBy(url, sortBy);
    }

    @Override
    public String sortBy(String url, String sortBy, String sortOrder) {
        // TODO Auto-generated method stub
        return liveApiClient.sortBy(url, sortBy, sortOrder);
    }

    @Override
    public GenericEntity getAcademicRecord(String token, Map<String, String> params) {
        // TODO Auto-generated method stub
        return liveApiClient.getAcademicRecord(token, params);
    }

    @Override
    public List<GenericEntity> getParentsForStudent(String token, String studentId) {
        // TODO Auto-generated method stub
        return liveApiClient.getParentsForStudent(token, studentId);
    }
}

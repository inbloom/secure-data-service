package org.slc.sli.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import org.slc.sli.entity.EducationalOrganization;
import org.slc.sli.entity.GenericEntity;
import org.slc.sli.entity.assessmentmetadata.AssessmentMetaData;
import org.slc.sli.util.Constants;
import org.slc.sli.util.SecurityUtil;



/**
 *
 * API Client class used by the Dashboard to make calls to the API service.
 *
 * @author svankina
 *
 */
public class LiveAPIClient implements APIClient {

    private RESTClient restClient;
    private Gson gson;

    // For now, the live client will use the mock client for api calls not yet implemented
    private MockAPIClient mockClient;

    /**
     * Getter and Setter used by Spring to instantiate the live/test api class
     * @return
     */
    public RESTClient getRestClient() {
        return restClient;
    }
    public void setRestClient(RESTClient restClient) {
        this.restClient = restClient;
    }

    public LiveAPIClient() {
        mockClient = new MockAPIClient();
        gson = new Gson();
    }

    @Override
    public List<GenericEntity> getSchools(String token, List<String> schoolIds) {
        
        String teacherId = getId(token);
        GenericEntity[] sections = getSectionsForTeacher(teacherId, token);
        GenericEntity[] schools = getSchoolsForSection(sections, token);

        List<GenericEntity> schoolList = new ArrayList<GenericEntity>();
        
        for (GenericEntity school : schools) {
            schoolList.add(school);
        }
        return schoolList;
    }

    @Override
    public List<GenericEntity> getStudents(final String token, List<String> urls) {
        if (urls == null) {
            return null;
        }
        
        List<GenericEntity> students = new ArrayList<GenericEntity>();
        
        int i = 0;
        for (String url: urls) {
            students.add(createEntityFromJson(getStudent(url, token)));
        }

        return students;
    }

    
    private String getStudent(String id, String token) {
        String url = Constants.API_SERVER_URI + "/students/" + id;
        return restClient.makeJsonRequestWHeaders(url, token);
    }    
    
    private GenericEntity getSchool(String id, String token) {

        String url = Constants.API_SERVER_URI + "/schools/" + id;
        GenericEntity school = createEntityFromJson(restClient.makeJsonRequestWHeaders(url, token));
        return school;
    }

    private String[] getStudentIdsForSection(String id, String token) {
        String url = Constants.API_SERVER_URI + "/student-section-associations/" + id + "/targets";

        List<GenericEntity> responses = fromAPI(token, url);
        
        String[] studentIds = new String[responses.size()];

        int i = 0;
        for (GenericEntity response : responses) {
            studentIds[i++] = (String) (response.get(Constants.ATTR_ID));
        }
        return studentIds;
    }
    
    
    private GenericEntity getSection(String id, String token) {
        String url = Constants.API_SERVER_URI + "/sections/" + id;
        GenericEntity section = createEntityFromJson(restClient.makeJsonRequestWHeaders(url, token));
        section.put(Constants.ATTR_STUDENT_UIDS, getStudentIdsForSection(id, token));

        if (section.get("sectionName") == null) { section.put("sectionName", section.get("uniqueSectionCode")); }

        return section;
    }

    private GenericEntity getCourse(String id, String token) {
        String url = Constants.API_SERVER_URI + "/courses/" + id;
        GenericEntity course = createEntityFromJson(restClient.makeJsonRequestWHeaders(url, token));
        return course;
    }

    private EducationalOrganization getEducationalOrganization(String id, String token) {
        String url = Constants.API_SERVER_URI + "/educationOrganizations/" + id;
        EducationalOrganization edOrg = gson.fromJson(restClient.makeJsonRequestWHeaders(url, token), EducationalOrganization.class);
        edOrg.setId(id);
        return edOrg;
    }

    @Override
    public List<GenericEntity> getAssessments(final String token, List<String> studentIds) {
        return mockClient.getAssessments(getUsername(), studentIds);
    }
    
    @Override
    public List<GenericEntity> getCustomData(final String token, String key) {
        return mockClient.getCustomData(getUsername(), key);
    }

    @Override
    public AssessmentMetaData[] getAssessmentMetaData(final String token) {
        return mockClient.getAssessmentMetaData(getUsername());
    }
    @Override
    public List<GenericEntity> getPrograms(final String token, List<String> studentIds) {
        return mockClient.getPrograms(getUsername(), studentIds);
    }

    @Override
    public EducationalOrganization[] getAssociatedEducationalOrganizations(final String token, GenericEntity school) {
        String url = Constants.API_SERVER_URI + "/educationOrganization-school-associations/" + school.get("id") + "/targets";
        List<GenericEntity> responses = fromAPI(url, token);
        List<EducationalOrganization> edOrgs = new ArrayList<EducationalOrganization>();
        for (GenericEntity response : responses) {
            edOrgs.add(getEducationalOrganization(parseId((Map) (response.get("link"))), token));
        }
        EducationalOrganization[] retVal = new EducationalOrganization[edOrgs.size()];
        return edOrgs.toArray(retVal);
    }

    @Override
    public EducationalOrganization[] getParentEducationalOrganizations(final String token, EducationalOrganization edOrg) {
        String url = Constants.API_SERVER_URI + "/educationOrganization-associations/" + edOrg.getId();
        List<GenericEntity> responses = fromAPI(url, token);
        List<EducationalOrganization> edOrgs = new ArrayList<EducationalOrganization>();
        // For every association, and find the ones that this ed org is a child, and follow the parent 
        for (GenericEntity response : responses) {
            try {
                String assLink = (String) (((Map) (response.get("link"))).get("href"));
                JSONObject assResponse = new JSONObject(restClient.makeJsonRequestWHeaders(assLink, token));
                String childId = assResponse.optString("educationOrganizationChildId");
                if (childId != null && childId.equals(edOrg.getId())) {
                    String parentId = assResponse.optString("educationOrganizationParentId");
                    if (parentId != null) {
                        edOrgs.add(getEducationalOrganization(parentId, token));
                    }
                }
            } catch (JSONException e) {
                throw new RuntimeException("malformed json from educationOrganization-associations api call.");
            }
        }
        EducationalOrganization[] retVal = new EducationalOrganization[edOrgs.size()];
        return edOrgs.toArray(retVal);
    }

    private String getId(String token) {

        // Make a call to the /home uri and retrieve id from there
        String returnValue = "";
        String url = Constants.API_SERVER_URI + "/home";
        GenericEntity response = createEntityFromJson(restClient.makeJsonRequestWHeaders(url, token));
        
        for (Map link : (List<Map>) (response.get(Constants.ATTR_LINKS))) {
            if (link.get(Constants.ATTR_REL).equals(Constants.ATTR_SELF)) {
                returnValue = parseId(link);
            }
        }
        
        return returnValue;
    }

    private String parseId(Map link) {
        String returnValue;
        int index = ((String) (link.get(Constants.ATTR_HREF))).lastIndexOf("/");
        returnValue = ((String) (link.get(Constants.ATTR_HREF))).substring(index + 1);
        return returnValue;
    }
    
    private GenericEntity[] getSectionsForTeacher(String id, String token) {
        String url = Constants.API_SERVER_URI + "/teacher-section-associations/" + id + "/targets";
        List<GenericEntity> responses = fromAPI(token, url);
        List<GenericEntity> sections = new ArrayList<GenericEntity>();
        
        for (GenericEntity response : responses) {
            sections.add(getSection(parseId(((Map) (response.get(Constants.ATTR_LINK)))), token));
        }

        // FIXME: converting like this because we suck.
        GenericEntity[] sections2 = new GenericEntity[sections.size()];
        int index = 0;
        for (GenericEntity s : sections) {
            sections2[index++] = s;
        }

        return sections2;
    }

        
    private GenericEntity[] getSchoolsForSection(GenericEntity[] sections, String token) {
        // collect associated course first.
        HashMap<String, GenericEntity> courseMap = new HashMap<String, GenericEntity>();
        HashMap<String, String> sectionIDToCourseIDMap = new HashMap<String, String>();
        for (int i = 0; i < sections.length; i++) {
            GenericEntity section = sections[i];
            String url = Constants.API_SERVER_URI + "/course-section-associations/" + section.get("id") + "/targets";
            List<GenericEntity> responses = fromAPI(url, token);
            if (responses.size() > 0) {
                GenericEntity response = responses.get(0); // there should be only one.
                GenericEntity course = getCourse(parseId((Map) (response.get("link"))), token);
                if (!courseMap.containsKey(course.get("id"))) {
                    courseMap.put((String) (course.get("id")), course);
                }
                course = courseMap.get(course.get("id"));
                GenericEntity[] singleSectionArray = { section };
                course.put("sections", singleSectionArray);
                sectionIDToCourseIDMap.put((String) (section.get("id")), (String) (course.get("id")));

            }
        }

        // now collect associated schools.
        HashMap<String, GenericEntity> schoolMap = new HashMap<String, GenericEntity>();
        HashMap<String, String> sectionIDToSchoolIDMap = new HashMap<String, String>();
        for (int i = 0; i < sections.length; i++) {
            GenericEntity section = sections[i];
            String url = Constants.API_SERVER_URI + "/section-school-associations/" + section.get("id") + "/targets";
            List<GenericEntity> responses = fromAPI(url, token);
            if (responses.size() > 0) {
                GenericEntity response = responses.get(0); // there should be only one.
                GenericEntity school = getSchool(parseId((Map) (response.get("link"))), token);
                if (!schoolMap.containsKey(school.get("id"))) {
                    schoolMap.put((String) (school.get("id")), school);
                }
                sectionIDToSchoolIDMap.put((String) (section.get("id")), (String) (school.get("id")));
            }
        }

        // Now associate course and school.
        // There is no direct course-school association in ed-fi, so in dashboard
        // the "course-school" association is defined as follows:
        // course C is associated with school S if there exists a section X s.t. C is associated
        // with X and S is associated with X.
        for (int i = 0; i < sections.length; i++) {
            GenericEntity section = sections[i];
            if (sectionIDToSchoolIDMap.containsKey(section.get("id"))
                && sectionIDToCourseIDMap.containsKey(section.get("id"))) {
                String schoolId = sectionIDToSchoolIDMap.get(section.get("id"));
                String courseId = sectionIDToCourseIDMap.get(section.get("id"));
                GenericEntity s = schoolMap.get(schoolId);
                GenericEntity c = courseMap.get(courseId);
                GenericEntity [] singleCourseArray = { c };
                s.put("courses", singleCourseArray);
            }
        }

        GenericEntity[] retVal = new GenericEntity[schoolMap.values().size()];
        return schoolMap.values().toArray(retVal);
    }

    private String getUsername() {
        return SecurityUtil.getPrincipal().getUsername().replace(" ", "");
    }

    
    /**
     * 
     * @param json
     * @param type
     * @return
     */
    private GenericEntity createEntityFromJson(String json) {
        
        GenericEntity e = gson.fromJson(json, GenericEntity.class);

        return e;
    }
    

    /**
     * Retrieves an entity list from the specified API url
     * and instantiates from its JSON representation
     * 
     * @param token
     *            - the principle authentication token
     * @param url
     *            - the API url to retrieve the entity list JSON string representation
     * @return entityList
     *         - the generic entity list
     */
    public List<GenericEntity> fromAPI(String token, String url) {
        List<GenericEntity> entityList = new ArrayList<GenericEntity>();

        // Parse JSON
        List<Map> maps = gson.fromJson(restClient.makeJsonRequestWHeaders(url, token), new ArrayList<Map>().getClass());
            
        for (Map<String, Object> map : maps) {
            entityList.add(new GenericEntity(map));
        }

        return entityList;
    }

}

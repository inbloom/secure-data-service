package org.slc.sli.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;

import org.apache.commons.lang.ArrayUtils;

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
        GenericEntity[] courses = getCoursesForSections(sections, token);
        GenericEntity[] schools = getSchoolsForCourses(courses, token);
        
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
        return section;
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

    private GenericEntity[] getCoursesForSections(GenericEntity[] sections, String token) {
        
        HashMap<String, GenericEntity> courseMap = new HashMap<String, GenericEntity>();

        // TODO: Make an actual api call to the courses service, when it comes up.
        
        // loop through sections, figure out course->section mappings
        for (GenericEntity section: sections) {
            
            // get course name
            String courseName; 
            String sectionName = (String) (section.get(Constants.ATTR_SECTION_NAME));
            if (sectionName.indexOf('-') > 0) {
                courseName = sectionName.substring(0, sectionName.indexOf('-') - 1);
                section.put(Constants.ATTR_SECTION_NAME, sectionName.substring(sectionName.indexOf('-') + 2));
            } else {
                courseName = sectionName;
            }    
            
            // need to create new one?
            GenericEntity course;
            if (courseMap.containsKey(courseName)) {
                course = courseMap.get(courseName);
            } else {
                course = new GenericEntity();
                course.put(Constants.ATTR_COURSE, courseName);
                courseMap.put(courseName, course);
            }
            
            // add section to course
            GenericEntity[] sectionArray = {section};
            course.put(Constants.ATTR_SECTIONS, (GenericEntity[]) ArrayUtils.addAll((GenericEntity[]) (course.get(Constants.ATTR_SECTIONS)), sectionArray));
            
            //TODO: Make a mapping between courses and schools 
            if (SecurityUtil.getPrincipal().getUsername().contains("Kim")) {
                course.put(Constants.ATTR_SCHOOL_ID, "00000000-0000-0000-0000-000000000201");
            } else {
                course.put(Constants.ATTR_SCHOOL_ID, "00000000-0000-0000-0000-000000000202");
            }
            
        }

        return (GenericEntity[]) courseMap.values().toArray(new GenericEntity[courseMap.size()]);
    }
    
    private GenericEntity[] getSchoolsForCourses(GenericEntity[] courses, String token) {
        // TODO Auto-generated method stub
        HashMap<String, GenericEntity> schoolMap = new HashMap<String, GenericEntity>();
        
        for (GenericEntity course: courses) {
            GenericEntity[] singleCourseArray = {course};
            if (schoolMap.containsKey(course.get(Constants.ATTR_SCHOOL_ID))) {
                schoolMap.get(course.get(Constants.ATTR_SCHOOL_ID)).put(Constants.ATTR_COURSES, singleCourseArray);
            } else {
                GenericEntity school = getSchool((String) (course.get(Constants.ATTR_SCHOOL_ID)), token);
                school.put(Constants.ATTR_COURSES, singleCourseArray);
                schoolMap.put((String) (course.get(Constants.ATTR_SCHOOL_ID)), school);
            }
        }
            
        return (GenericEntity[]) schoolMap.values().toArray(new GenericEntity[1]);
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

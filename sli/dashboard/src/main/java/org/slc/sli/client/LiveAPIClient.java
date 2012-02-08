package org.slc.sli.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.gson.Gson;

import org.apache.commons.lang.ArrayUtils;

import org.slc.sli.entity.AssociationResponseObject;
import org.slc.sli.entity.GenericEntity;
import org.slc.sli.entity.InnerResponse;
import org.slc.sli.entity.ResponseObject;
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
            students.add(createEntityFromJson(getStudent(url, token), Constants.ENTITY_TYPE_STUDENT));
        }
        
        return students;
    }
    
    private String getStudent(String id, String token) {
        String url = Constants.API_SERVER_URI + "/students/" + id;
        return restClient.makeJsonRequestWHeaders(url, token);
    }    
    
    private GenericEntity getSchool(String id, String token) {
        String url = Constants.API_SERVER_URI + "/schools/" + id;
        GenericEntity school = createEntityFromJson(restClient.makeJsonRequestWHeaders(url, token), null);
        return school;
    }
    
    private String[] getStudentIdsForSection(String id, String token) {
        String url = Constants.API_SERVER_URI + "/student-section-associations/" + id + "/targets";
        ResponseObject[] responses = gson.fromJson(restClient.makeJsonRequestWHeaders(url, token), ResponseObject[].class);
        
        String[] studentIds = new String[responses.length];
        int i = 0;
        for (ResponseObject response : responses) {
            studentIds[i++] = response.getId();
        }
        return studentIds;
    }
    
    
    private GenericEntity getSection(String id, String token) {
        String url = Constants.API_SERVER_URI + "/sections/" + id;
        GenericEntity section = createEntityFromJson(restClient.makeJsonRequestWHeaders(url, token), null);
        section.put("studentUIDs", getStudentIdsForSection(id, token));
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
        ResponseObject response = gson.fromJson(restClient.makeJsonRequestWHeaders(url, token), ResponseObject.class);
        
        for (InnerResponse link : response.getLinks()) {
            if (link.getRel().equals("self")) {
                returnValue = parseId(link);
            }
        }
        
        return returnValue;
    }

    private String parseId(InnerResponse link) {
        String returnValue;
        int index = link.getHref().lastIndexOf("/");
        returnValue = link.getHref().substring(index + 1);
        return returnValue;
    }
    
    private GenericEntity[] getSectionsForTeacher(String id, String token) {
        String url = Constants.API_SERVER_URI + "/teacher-section-associations/" + id + "/targets";
        AssociationResponseObject[] responses = gson.fromJson(restClient.makeJsonRequestWHeaders(url, token), AssociationResponseObject[].class);
        List<GenericEntity> sections = new ArrayList<GenericEntity>();
        
        for (AssociationResponseObject response : responses) {
            sections.add(getSection(parseId(response.getLink()), token));
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
            String sectionName = (String) (section.get("sectionName"));
            if (sectionName.indexOf('-') > 0) {
                courseName = sectionName.substring(0, sectionName.indexOf('-') - 1);
                section.put("sectionName", sectionName.substring(sectionName.indexOf('-') + 2));
            } else {
                courseName = sectionName;
            }    
            
            // need to create new one?
            GenericEntity course;
            if (courseMap.containsKey(courseName)) {
                course = courseMap.get(courseName);
            } else {
                course = new GenericEntity();
                course.put("course", courseName);
                courseMap.put(courseName, course);
            }
            
            // add section to course
            GenericEntity[] sectionArray = {section};
            course.put("sections", (GenericEntity[]) ArrayUtils.addAll((GenericEntity[]) (course.get("sections")), sectionArray));
            
            //TODO: Make a mapping between courses and schools 
            if (SecurityUtil.getPrincipal().getUsername().contains("Kim")) {
                course.put("schoolId", "00000000-0000-0000-0000-000000000201");
            } else {
                course.put("schoolId", "00000000-0000-0000-0000-000000000202");
            }
            
        }

        return (GenericEntity[]) courseMap.values().toArray(new GenericEntity[courseMap.size()]);
    }
    
    private GenericEntity[] getSchoolsForCourses(GenericEntity[] courses, String token) {
        // TODO Auto-generated method stub
        HashMap<String, GenericEntity> schoolMap = new HashMap<String, GenericEntity>();
        
        for (GenericEntity course: courses) {
            GenericEntity[] singleCourseArray = {course};
            if (schoolMap.containsKey(course.get("schoolId"))) {
                schoolMap.get(course.get("schoolId")).put("courses", singleCourseArray);
            } else {
                GenericEntity school = getSchool((String) (course.get("schoolId")), token);
                school.put("courses", singleCourseArray);
                schoolMap.put((String) (course.get("schoolId")), school);
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
    private GenericEntity createEntityFromJson(String json, String type) {
        
        Gson gson = new Gson();
        
        GenericEntity e = gson.fromJson(json, GenericEntity.class);

        return e;
    }
    

    
}

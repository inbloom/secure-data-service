package org.slc.sli.client;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import com.google.gson.Gson;

import org.slc.sli.entity.Assessment;
import org.slc.sli.entity.Course;
import org.slc.sli.entity.CustomData;
import org.slc.sli.entity.ResponseObject;
import org.slc.sli.entity.School;
import org.slc.sli.entity.Section;
import org.slc.sli.entity.Student;
import org.slc.sli.entity.assessmentmetadata.AssessmentMetaData;
import org.slc.sli.security.RESTClient;


/**
 * 
 * API Client class used by the Dashboard to make calls to the API service.
 *
 */
public class LiveAPIClient implements APIClient {

    private RESTClient restClient;

    private String apiServerUri = "http://devapi1.slidev.org:8080/api/rest";
    
    public RESTClient getRestClient() {
        return restClient;
    }
    public void setRestClient(RESTClient restClient) {
        this.restClient = restClient;
    }

    // For now, the live client will use the mock client for api calls not yet implemented
    private MockAPIClient mockClient;
    
    public LiveAPIClient() {
        mockClient = new MockAPIClient();
    }
    

    @Override
    public School[] getSchools(String token) {
        return mockClient.getSchools(token);
    }
    
    @Override
    public Student[] getStudents(final String token, List<String> urls) {
        Gson gson = new Gson();
        Student[] students = new Student[urls.size()];
        
        int i = 0;
        for (String url: urls) {
            students[i++] = gson.fromJson(restClient.getStudent(url, token), Student.class);
        }
        
        return students;

    }
    
    
    private School getSchool(String id, String token) {
        String url = apiServerUri + "/schools/" + id;
        Gson gson = new Gson();
        School school = gson.fromJson(restClient.makeJsonRequestWHeaders(url, token), School.class);
        return school;
    }
    
    private String[] getStudentIdsForSection(String id, String token) {
        Gson gson = new Gson();
        String url = apiServerUri + "/student-section-associations/" + id + "/targets";
        ResponseObject[] responses = gson.fromJson(restClient.makeJsonRequestWHeaders(url, token), ResponseObject[].class);
        
        String[] studentIds = new String[responses.length];
        int i = 0;
        for (ResponseObject response : responses) {
            studentIds[i++] = response.getId();
        }
        return studentIds;
    }
    
    
    private Section getSection(String id, String token) {
        Gson gson = new Gson();
        String url = apiServerUri + "/sections/" + id;
        Section section = gson.fromJson(restClient.makeJsonRequestWHeaders(url, token), Section.class);
        section.setStudentUIDs(getStudentIdsForSection(id, token));
        return section;
        
    }

    @Override
    public Assessment[] getAssessments(final String token, List<String> studentIds) {
        return mockClient.getAssessments(token, studentIds);
    }
    @Override
    public CustomData[] getCustomData(final String token, String key) {
        return mockClient.getCustomData(token, key);
    }
    @Override
    public void saveCustomData(CustomData[] src, String token, String key) {
        mockClient.saveCustomData(src, token, key);
    }
    @Override
    public AssessmentMetaData[] getAssessmentMetaData(final String token) {
        return mockClient.getAssessmentMetaData(token);
    }
    
    @Override
    public String getTeacherId(String token) {
    //TODO: Make a call to the /home uri and retrieve id from there
        return "eb424dcc-6cff-a69b-c1b3-2b1fc86b2c94";
    }
    
    @Override
    public Section[] getSectionsForTeacher(String id, String token) {
        Gson gson = new Gson();
        String url = apiServerUri + "/teacher-section-associations/" + id + "/targets";
        ResponseObject[] responses = gson.fromJson(restClient.makeJsonRequestWHeaders(url, token), ResponseObject[].class);
        //String[] sectionIds = new String[responses.length];
        Section[] sections = new Section[responses.length];
        int i = 0;
        for (ResponseObject response: responses) {
            sections[i++] = getSection(response.getId(), token);
        }
        
        return sections;
    }

    @Override
    public Course[] getCoursesForSections(Section[] sections, String token) {
        
        Course[] courses = new Course[sections.length];
        int i = 0;
        // TODO: Make an actual api call to the courses service, when it comes up.
        for (Section section: sections) {
            Course course = new Course();
            Section[] sectionArray = {section};
            course.setSections(sectionArray);
            String sectionName = section.getUniqueSectionCode();
            course.setCourse(sectionName.substring(0, sectionName.length() - 3));
            
            //TODO: Make a mapping between courses and schools 
            course.setSchoolId("0f464187-30ff-4e61-a0dd-74f45e5c7a9d");
            courses[i++] = course;
        }

        return courses;
    }
    
    @Override
    public School[] getSchoolsForCourses(Course[] courses, String token) {
        // TODO Auto-generated method stub
        HashMap<String, School> schoolMap = new HashMap<String, School>();
        
        for (Course course: courses) {
            Course[] singleCourseArray = {course};
            if (schoolMap.containsKey(course.getSchoolId())) {
                schoolMap.get(course.getSchoolId()).addCourses(singleCourseArray);
            } else {
                School school = getSchool(course.getSchoolId(), token);
                school.setCourses(singleCourseArray);
                schoolMap.put(course.getSchoolId(), school);
            }
        }
            
        Collection<School> temp = schoolMap.values(); 
        return (School[]) schoolMap.values().toArray(new School[1]);
    }
}

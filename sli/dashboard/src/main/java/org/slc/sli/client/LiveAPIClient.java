package org.slc.sli.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.gson.Gson;

import org.apache.commons.lang.ArrayUtils;

import org.slc.sli.entity.Assessment;
import org.slc.sli.entity.AssociationResponseObject;
import org.slc.sli.entity.Course;
import org.slc.sli.entity.CustomData;
import org.slc.sli.entity.InnerResponse;
import org.slc.sli.entity.ResponseObject;
import org.slc.sli.entity.School;
import org.slc.sli.entity.Section;
import org.slc.sli.entity.Student;
import org.slc.sli.entity.StudentProgramAssociation;
import org.slc.sli.entity.assessmentmetadata.AssessmentMetaData;
import org.slc.sli.entity.EducationalOrganization;
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
    public School[] getSchools(String token) {
        String teacherId = getId(token);
        Section[] sections = getSectionsForTeacher(teacherId, token);
        Course[] courses = getCoursesForSections(sections, token);
        School[] schools = getSchoolsForCourses(courses, token);
        
        return schools;
    }
    
    @Override
    public Student[] getStudents(final String token, List<String> urls) {
        if (urls == null) {
            return null;
        }
        
        Student[] students = new Student[urls.size()];
        
        int i = 0;
        for (String url: urls) {
            students[i++] = gson.fromJson(getStudent(url, token), Student.class);
        }
        
        return students;
    }
    
    
    
    private String getStudent(String id, String token) {
        String url = Constants.API_SERVER_URI + "/students/" + id;
        return restClient.makeJsonRequestWHeaders(url, token);
    }    
    
    private School getSchool(String id, String token) {
        String url = Constants.API_SERVER_URI + "/schools/" + id;
        School school = gson.fromJson(restClient.makeJsonRequestWHeaders(url, token), School.class);
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
    
    
    private Section getSection(String id, String token) {
        String url = Constants.API_SERVER_URI + "/sections/" + id;
        Section section = gson.fromJson(restClient.makeJsonRequestWHeaders(url, token), Section.class);
        section.setStudentUIDs(getStudentIdsForSection(id, token));
        return section;
    }

    @Override
    public Assessment[] getAssessments(final String token, List<String> studentIds) {
        return mockClient.getAssessments(getUsername(), studentIds);
    }
    @Override
    public CustomData[] getCustomData(final String token, String key) {
        return mockClient.getCustomData(getUsername(), key);
    }
    @Override
    public void saveCustomData(CustomData[] src, String token, String key) {
        mockClient.saveCustomData(src, getUsername(), key);
    }
    @Override
    public AssessmentMetaData[] getAssessmentMetaData(final String token) {
        return mockClient.getAssessmentMetaData(getUsername());
    }
    @Override
    public StudentProgramAssociation[] getStudentProgramAssociation(final String token, List<String> studentIds) {
        return mockClient.getStudentProgramAssociation(getUsername(), studentIds);
    }
    
    @Override
    public EducationalOrganization[] getParentEducationalOrganizations(final String token, School s) {
        return mockClient.getParentEducationalOrganizations(getUsername(), s);
    }
    
    @Override
    public EducationalOrganization[] getParentEducationalOrganizations(final String token, EducationalOrganization edOrg) {
        return mockClient.getParentEducationalOrganizations(getUsername(), edOrg);
    }
    
    private String getId(String token) {
    //TODO: Make a call to the /home uri and retrieve id from there
        String returnValue = "";
        String url = Constants.API_SERVER_URI + "/home";
        ResponseObject response = gson.fromJson(restClient.makeJsonRequestWHeaders(url, token), ResponseObject.class);
        
        for (InnerResponse link : response.getLinks()) {
            if (link.getRel().equals("self")) {
                returnValue = parseId(link);
            }
        }
        
        //TODO: Actually return the id from the call, once teacher-section works
        // FIXME
        //return "eb424dcc-6cff-a69b-c1b3-2b1fc86b2c94";
        return returnValue;
    }

    private String parseId(InnerResponse link) {
        String returnValue;
        int index = link.getHref().lastIndexOf("/");
        returnValue = link.getHref().substring(index + 1);
        return returnValue;
    }
    
    private Section[] getSectionsForTeacher(String id, String token) {
        String url = Constants.API_SERVER_URI + "/teacher-section-associations/" + id + "/targets";
        AssociationResponseObject[] responses = gson.fromJson(restClient.makeJsonRequestWHeaders(url, token), AssociationResponseObject[].class);
        List<Section> sections = new ArrayList<Section>();
        
        for (AssociationResponseObject response : responses) {
            sections.add(getSection(parseId(response.getLink()), token));
        }
        
        // FIXME: converting like this because we suck.
        Section[] sections2 = new Section[sections.size()];
        int index = 0;
        for (Section s : sections) {
            sections2[index++] = s;
        }
        
        return sections2;
    }

    private Course[] getCoursesForSections(Section[] sections, String token) {
        
        //Course[] courses = new Course[sections.length];
        HashMap<String, Course> courseMap = new HashMap<String, Course>();

        // TODO: Make an actual api call to the courses service, when it comes up.
        
        // loop through sections, figure out course->section mappings
        for (Section section: sections) {
            
            // get course name
            String courseName; 
            String sectionName = section.getSectionName();
            if (sectionName.indexOf('-') > 0) {
                courseName = sectionName.substring(0, sectionName.indexOf('-') - 1);
                section.setSectionName(sectionName.substring(sectionName.indexOf('-') + 2));
            } else {
                courseName = sectionName;
            }    
            
            // need to create new one?
            Course course;
            if (courseMap.containsKey(courseName)) {
                course = courseMap.get(courseName);
            } else {
                course = new Course();
                course.setCourse(courseName);
                courseMap.put(courseName, course);
            }
            
            // add section to course
            Section[] sectionArray = {section};
            course.setSections((Section[]) ArrayUtils.addAll(course.getSections(), sectionArray));
            
            //TODO: Make a mapping between courses and schools 
            //course.setSchoolId("0f464187-30ff-4e61-a0dd-74f45e5c7a9d");
            if (SecurityUtil.getPrincipal().getUsername().contains("Kim")) {
                course.setSchoolId("00000000-0000-0000-0000-000000000201");
            } else {
                course.setSchoolId("00000000-0000-0000-0000-000000000202");
            }
            
        }

        return (Course[]) courseMap.values().toArray(new Course[courseMap.size()]);
    }
    
    private School[] getSchoolsForCourses(Course[] courses, String token) {
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
            
        return (School[]) schoolMap.values().toArray(new School[1]);
    }
    
    private String getUsername() {
        return SecurityUtil.getPrincipal().getUsername().replace(" ", "");
    }
    
}

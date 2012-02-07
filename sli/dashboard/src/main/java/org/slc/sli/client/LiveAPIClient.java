package org.slc.sli.client;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.gson.Gson;

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
        School[] schools = getSchoolsForSection(sections, token);
        
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
        section.setId(id);
        if (section.getSectionName() == null) { section.setSectionName(section.getUniqueSectionCode()); }
        return section;
    }

    private Course getCourse(String id, String token) {
        String url = Constants.API_SERVER_URI + "/courses/" + id;
        Course course = gson.fromJson(restClient.makeJsonRequestWHeaders(url, token), Course.class);
        course.setId(id);
        return course;
    }
    
    private EducationalOrganization getEducationalOrganization(String id, String token) {
        String url = Constants.API_SERVER_URI + "/educationOrganizations/" + id;
        EducationalOrganization edOrg = gson.fromJson(restClient.makeJsonRequestWHeaders(url, token), EducationalOrganization.class);
        edOrg.setId(id);
        return edOrg;
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
    public EducationalOrganization[] getAssociatedEducationalOrganizations(final String token, School s) {
        String url = Constants.API_SERVER_URI + "/educationOrganization-school-associations/" + s.getId() + "/targets";
        AssociationResponseObject[] responses = gson.fromJson(restClient.makeJsonRequestWHeaders(url, token), AssociationResponseObject[].class);
        List<EducationalOrganization> edOrgs = new ArrayList<EducationalOrganization>();
        for (AssociationResponseObject response : responses) {
            edOrgs.add(getEducationalOrganization(parseId(response.getLink()), token));
        }
        EducationalOrganization[] retVal = new EducationalOrganization[edOrgs.size()];
        return edOrgs.toArray(retVal);
    }
    
    @Override
    public EducationalOrganization[] getParentEducationalOrganizations(final String token, EducationalOrganization edOrg) {
        String url = Constants.API_SERVER_URI + "/educationOrganization-associations/" + edOrg.getId() + "/targets";
        AssociationResponseObject[] responses = gson.fromJson(restClient.makeJsonRequestWHeaders(url, token), AssociationResponseObject[].class);
        List<EducationalOrganization> edOrgs = new ArrayList<EducationalOrganization>();
        // @@@ TODO: This is untested. Pending on API team resolving educationOrganization-associations for reverse lookup
        for (AssociationResponseObject response : responses) { 
            if (response.getId() != null && response.getId().equals("getEducationOrganizationParents")) {
                edOrgs.add(getEducationalOrganization(parseId(response.getLink()), token));
            }
        }
        EducationalOrganization[] retVal = new EducationalOrganization[edOrgs.size()];
        return edOrgs.toArray(retVal);
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

    private School[] getSchoolsForSection(Section[] sections, String token) {
        // collect associated course first. 
        HashMap<String, Course> courseMap = new HashMap<String, Course>();
        HashMap<String, String> sectionIDToCourseIDMap = new HashMap<String, String>();
        for (int i = 0; i < sections.length; i++) {
            Section section = sections[i];
            String url = Constants.API_SERVER_URI + "/course-section-associations/" + section.getId() + "/targets";
            AssociationResponseObject[] responses = gson.fromJson(restClient.makeJsonRequestWHeaders(url, token), AssociationResponseObject[].class);
            if (responses.length > 0) { 
                AssociationResponseObject response = responses[0]; // there should be only one. 
                Course course = getCourse(parseId(response.getLink()), token);
                if (!courseMap.containsKey(course.getId())) {
                    courseMap.put(course.getId(), course);
                }
                course = courseMap.get(course.getId());
                Section [] singleSectionArray = { section };
                course.addSections(singleSectionArray);
                sectionIDToCourseIDMap.put(section.getId(), course.getId());
            }
        }
        
        // now collect associated schools. 
        HashMap<String, School> schoolMap = new HashMap<String, School>();
        HashMap<String, String> sectionIDToSchoolIDMap = new HashMap<String, String>();
        for (int i = 0; i < sections.length; i++) {
            Section section = sections[i];
            String url = Constants.API_SERVER_URI + "/section-school-associations/" + section.getId() + "/targets";
            AssociationResponseObject[] responses = gson.fromJson(restClient.makeJsonRequestWHeaders(url, token), AssociationResponseObject[].class);
            if (responses.length > 0) { 
                AssociationResponseObject response = responses[0]; // there should be only one. 
                School school = getSchool(parseId(response.getLink()), token);
                if (!schoolMap.containsKey(school.getId())) {
                    schoolMap.put(school.getId(), school);
                }
                sectionIDToSchoolIDMap.put(section.getId(), school.getId());
            }
        }

        // Now associate course and school. 
        // There is no direct course-school association in ed-fi, so in dashboard 
        // the "course-school" association is defined as follows: 
        // course C is associated with school S if there exists a section X s.t. C is associated
        // with X and S is associated with X.
        for (int i = 0; i < sections.length; i++) {
            Section section = sections[i];
            if (sectionIDToSchoolIDMap.containsKey(section.getId()) 
                && sectionIDToCourseIDMap.containsKey(section.getId())) {
                String schoolId = sectionIDToSchoolIDMap.get(section.getId());
                String courseId = sectionIDToCourseIDMap.get(section.getId());
                School s = schoolMap.get(schoolId);
                Course c = courseMap.get(courseId);
                Course [] singleCourseArray = { c };
                s.addCourses(singleCourseArray);
            }
        }

        School[] retVal = new School[schoolMap.values().size()];
        return schoolMap.values().toArray(retVal);
    }
    
    private String getUsername() {
        return SecurityUtil.getPrincipal().getUsername().replace(" ", "");
    }
    
}

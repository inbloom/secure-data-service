package org.slc.sli.client;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.slc.sli.entity.ConfigMap;
import org.slc.sli.entity.GenericEntity;
import org.slc.sli.api.client.impl.RESTClient;

public class SDKAPIClient implements APIClient{

    RESTClient client;
    
    @Override
    public GenericEntity getStaffInfo(String token) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ConfigMap getEdOrgCustomData(String token, String id) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void putEdOrgCustomData(String token, String id, ConfigMap configMap) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public List<GenericEntity> getSchools(String token, List<String> schoolIds) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<GenericEntity> getStudents(String token, Collection<String> studentIds) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public GenericEntity getStudent(String token, String id) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<GenericEntity> getStudentAssessments(String token, String studentId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<GenericEntity> getAssessments(String token, List<String> assessmentIds) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<GenericEntity> getStudentAttendance(String token, String studentId, String start, String end) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public GenericEntity getParentEducationalOrganization(String token, GenericEntity educationalOrganization) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<GenericEntity> getParentEducationalOrganizations(String token,
            List<GenericEntity> educationalOrganizations) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<GenericEntity> getStudentEnrollment(String token, GenericEntity student) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<GenericEntity> getStudentsWithGradebookEntries(String token, String sectionId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<GenericEntity> getStudentsWithSearch(String token, String firstName, String lastName) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public GenericEntity getStudentWithOptionalFields(String token, String studentId, List<String> optionalFields) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getHeader(String token) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getFooter(String token) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<GenericEntity> getCourses(String token, String studentId, Map<String, String> params) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<GenericEntity> getStudentTranscriptAssociations(String token, String studentId,
            Map<String, String> params) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<GenericEntity> getSections(String token, String studentId, Map<String, String> params) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public GenericEntity getEntity(String token, String type, String id, Map<String, String> params) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<GenericEntity> getEntities(String token, String type, String id, Map<String, String> params) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<GenericEntity> getStudentSectionGradebookEntries(String token, String studentId,
            Map<String, String> params) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<GenericEntity> getStudents(String token, String sectionId, List<String> studentIds) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public GenericEntity getTeacherForSection(String sectionId, String token) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public GenericEntity getHomeRoomForStudent(String studentId, String token) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public GenericEntity getSession(String token, String sessionId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<GenericEntity> getSessions(String token) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<GenericEntity> getSessionsByYear(String token, String schoolYear) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String sortBy(String url, String sortBy) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String sortBy(String url, String sortBy, String sortOrder) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public GenericEntity getAcademicRecord(String token, Map<String, String> params) {
        // TODO Auto-generated method stub
        return null;
    }
    
}

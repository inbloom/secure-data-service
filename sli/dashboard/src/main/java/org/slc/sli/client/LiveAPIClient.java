package org.slc.sli.client;

import java.util.List;

import com.google.gson.Gson;

import org.slc.sli.entity.Assessment;
import org.slc.sli.entity.CustomData;
import org.slc.sli.entity.School;
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
    public Student[] getStudents(final String token, List<String> studentIds) {
        Gson gson = new Gson();
        Student[] students = new Student[studentIds.size()];
        
        int i = 0;
        for (String id: studentIds) {
            students[i++] = gson.fromJson(restClient.getStudent(id, token), Student.class);
        }
        
        return students;

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
}

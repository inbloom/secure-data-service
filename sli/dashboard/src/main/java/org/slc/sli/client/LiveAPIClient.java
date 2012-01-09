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
 * TODO: Write Javadoc
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
    @Override
    public School[] getSchools(String token) {
        System.err.println("Not implemented");
        return null;
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
        System.err.println("Not implemented");
        return null;
    }
    @Override
    public CustomData[] getCustomData(final String token, String key) {
        System.err.println("Not implemented");
        return null;
    }
    @Override
    public void saveCustomData(CustomData[] src, String token, String key) {
        System.err.println("Not implemented");
    }
    @Override
    public AssessmentMetaData[] getAssessmentMetaData(final String token) {
        System.err.println("Not implemented");
        return null;
    }
}

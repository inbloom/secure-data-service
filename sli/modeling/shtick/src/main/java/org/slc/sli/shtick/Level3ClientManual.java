package org.slc.sli.shtick;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.slc.sli.shtick.pojo.Student;

public interface Level3ClientManual {
    
    List<Student> getStudents(String token, Map<String, Object> queryArgs) throws IOException, StatusCodeException;
    
    List<Student> getStudentsById(String token, List<String> studentIds, Map<String, Object> queryArgs)
            throws IOException, StatusCodeException;
    
    String postStudent(String token, Student student) throws IOException, StatusCodeException;
    
    void putStudent(String token, Student student) throws IOException, StatusCodeException;
    
    void deleteStudent(String token, Student student) throws IOException, StatusCodeException;
    
    Map<String, Object> getCustomForStudentsById(String token, List<String> studentIds, Map<String, Object> queryArgs)
            throws IOException, StatusCodeException;
    
    void postCustomForStudentsById(String token, String id, Entity entity) throws IOException, StatusCodeException;
}

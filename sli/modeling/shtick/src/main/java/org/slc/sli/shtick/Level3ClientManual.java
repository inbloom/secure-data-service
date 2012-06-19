package org.slc.sli.shtick;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.slc.sli.shtick.pojo.Student;
import org.slc.sli.shtick.pojomanual.StudentManual;

public interface Level3ClientManual {

    List<StudentManual> getStudents(String token, Map<String, Object> queryArgs) throws IOException, StatusCodeException;

    List<StudentManual> getStudentsById(String token, List<String> studentIds, Map<String, Object> queryArgs)
            throws IOException, StatusCodeException;

    String postStudent(String token, StudentManual student) throws IOException, StatusCodeException;

    void putStudent(String token, StudentManual student) throws IOException, StatusCodeException;

    void deleteStudent(String token, StudentManual student) throws IOException, StatusCodeException;
}

package org.slc.sli.shtick;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.slc.sli.shtick.pojo.Student;

public interface Level3ClientManual {

    public List<Student> getStudents(String token, Map<String, Object> queryArgs) throws IOException, RestException;

    public List<Student> getStudentsById(String token, List<String> studentIds, Map<String, Object> queryArgs)
            throws IOException, RestException;

}

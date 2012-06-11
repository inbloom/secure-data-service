package org.slc.sli.shtick;

import org.slc.sli.shtick.pojo.Student;

import java.io.IOException;
import java.util.List;

/**
 * @author jstokes
 */
public interface Level3ClientManual {
    public List<Student> getStudents() throws IOException, SLIDataStoreException;
    public List<Student> getStudentsById(List<String> studentIds) throws IOException, SLIDataStoreException;
}

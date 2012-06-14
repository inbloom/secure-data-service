package org.slc.sli.shtick;

import java.io.IOException;
import java.util.List;

/**
 * @author jstokes
 */
public interface Level2ClientManual {

    List<RestEntity> getStudentsByStudentId(String token, List<String> studentIds) throws IOException, RestException;
    List<RestEntity> getStudents(String token) throws IOException, RestException;
}

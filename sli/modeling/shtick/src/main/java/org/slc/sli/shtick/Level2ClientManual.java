package org.slc.sli.shtick;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

/**
 * @author jstokes
 */
public interface Level2ClientManual {

    List<RestEntity> getStudentsByStudentId(String token, List<String> studentIds, Map<String, Object> queryArgs)
            throws IOException, RestException;

    List<RestEntity> getStudents(String token, Map<String, Object> queryArgs) throws IOException, RestException;

    void deleteStudentById(final String token, final String studentId) throws URISyntaxException, IOException,
            RestException;

    String postStudent(final String token, final RestEntity body) throws URISyntaxException, IOException, RestException;

    void putStudent(final String token, final RestEntity data) throws URISyntaxException, IOException, RestException;
}

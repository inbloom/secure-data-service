package org.slc.sli.shtick;

import java.io.IOException;
import java.util.List;

import org.slc.sli.api.client.Entity;

/**
 * @author jstokes
 */
public interface Level2ClientManual {

    List<Entity> getStudentsByStudentId(String token, List<String> studentIds) throws IOException, SLIDataStoreException;
    List<Entity> getStudents(String token) throws IOException, SLIDataStoreException;
}

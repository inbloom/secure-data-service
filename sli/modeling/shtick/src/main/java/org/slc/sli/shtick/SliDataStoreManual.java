package org.slc.sli.shtick;

import java.io.IOException;
import java.util.List;

import org.slc.sli.api.client.Entity;

/**
 * @author jstokes
 */
public interface SliDataStoreManual {

    List<Entity> getStudents() throws IOException, SLIDataStoreException;

    void connect(String auth);

}

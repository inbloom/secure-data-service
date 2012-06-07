package org.slc.sli.shtick;

import org.slc.sli.api.client.Entity;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

/**
 * @author jstokes
 */
public interface SliDataStoreManual {

    List<Entity> getStudents() throws IOException, SLIDataStoreException;
    void connect(String auth);

}

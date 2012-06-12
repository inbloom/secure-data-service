package org.slc.sli.shtick;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import org.slc.sli.api.client.Entity;

/**
 * @author jstokes
 */
public interface Level1Client {
    // SliDataStore = http code exception
    List<Entity> getRequest(final String token, final URL url) throws URISyntaxException, IOException, SLIDataStoreException;

    void deleteRequest(final String token, final URL url) throws URISyntaxException, IOException, SLIDataStoreException;

    //TODO: should we return entity id here?
    void createRequest(final String token, final String data, final URL url) throws URISyntaxException, IOException, SLIDataStoreException;

    void updateRequest(final String token, final String data, final URL url) throws URISyntaxException, IOException, SLIDataStoreException;

}

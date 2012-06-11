package org.slc.sli.shtick.client.impl;

import org.slc.sli.api.client.Entity;
import org.slc.sli.shtick.SLIDataStoreException;
import org.slc.sli.shtick.client.Level0ClientManual;
import org.slc.sli.shtick.client.Level1ClientManual;

import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

/**
 * @author jstokes
 */
public class StandardLevel1ClientManual implements Level1ClientManual {

    private Level0ClientManual level0ClientManual;

    public StandardLevel1ClientManual(Level0ClientManual level0ClientManual) {
       this.level0ClientManual = level0ClientManual;
    }

    @Override
    public List<Entity> getRequest(String token, URL url) throws URISyntaxException, IOException, SLIDataStoreException {
        Response response = level0ClientManual.getRequest(token, url);
        return deserialize(response);
    }

    private List<Entity> deserialize(Response response) {
        throw new UnsupportedOperationException();
    }

}

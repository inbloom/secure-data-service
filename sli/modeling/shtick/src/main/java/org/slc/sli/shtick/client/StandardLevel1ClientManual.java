package org.slc.sli.shtick.client;

import org.slc.sli.api.client.Entity;
import org.slc.sli.shtick.Level2ClientManual;
import org.slc.sli.shtick.SLIDataStoreException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

/**
 * @author jstokes
 */
public class StandardLevel1ClientManual implements Level1ClientManual {
    @Override
    public List<Entity> getRequest(String token, URL url) throws URISyntaxException, IOException, SLIDataStoreException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}

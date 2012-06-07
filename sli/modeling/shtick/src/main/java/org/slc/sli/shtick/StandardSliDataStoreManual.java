package org.slc.sli.shtick;

import org.slc.sli.api.client.Entity;
import org.slc.sli.api.client.SLIClient;
import org.slc.sli.api.client.impl.BasicClient;
import org.slc.sli.api.client.impl.BasicQuery;

import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * @author jstokes
 */
public class StandardSliDataStoreManual implements SliDataStoreManual {

    private SLIClient sliClient;

    public StandardSliDataStoreManual(final URL apiServerUrl, final String clientId, final String clientSecret, final URL callbackURL) {
        sliClient = new BasicClient(apiServerUrl, clientId, clientSecret, callbackURL);
    }

    @Override
    public void connect(String auth) {
        sliClient.connect(auth);
    }

    @Override
    public List<Entity> getStudents() throws IOException, SLIDataStoreException {
        List<Entity> ret = new ArrayList<Entity>();

        URL url = new URL("http://local.slidev.org:8080/api/rest/v1/students");
        final Response response;
        try {
            response = sliClient.getResource(ret, url, BasicQuery.EMPTY_QUERY);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        if (response.getStatus() == Response.Status.OK.getStatusCode()) {
            return ret;
        } else {
            throw new SLIDataStoreException();
        }
    }
}

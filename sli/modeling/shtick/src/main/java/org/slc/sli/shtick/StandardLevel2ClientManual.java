package org.slc.sli.shtick;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.Response;

import org.slc.sli.api.client.Entity;
import org.slc.sli.api.client.SLIClient;
import org.slc.sli.api.client.impl.BasicClient;
import org.slc.sli.api.client.impl.BasicQuery;
import org.slc.sli.shtick.client.Level1ClientManual;

/**
 * @author jstokes
 */
public class StandardLevel2ClientManual implements Level2ClientManual {

//    private SLIClient sliClient;
//
//    public StandardLevel2ClientManual(final URL apiServerUrl, final String clientId, final String clientSecret, final URL callbackURL) {
//        sliClient = new BasicClient(apiServerUrl, clientId, clientSecret, callbackURL);
//    }
//
//    @Override
//    public void connect(String auth) {
//        sliClient.connect(auth);
//    }
//
//    @Override
//    public List<Entity> getStudents() throws IOException, SLIDataStoreException {
//        try {
//            final List<Entity> entities = new ArrayList<Entity>();
//            final URL url = new URL("http://local.slidev.org:8080/api/rest/v1/students");
//            final Response response = sliClient.getResource(entities, url, BasicQuery.EMPTY_QUERY);
//            if (response.getStatus() == Response.Status.OK.getStatusCode()) {
//                return entities;
//            } else {
//                throw new SLIDataStoreException();
//            }
//        } catch (final URISyntaxException e) {
//            throw new RuntimeException(e);
//        }
//
//    }

    private Level1ClientManual level1ClientManual;
    public StandardLevel2ClientManual(Level1ClientManual level1ClientManual) {
        this.level1ClientManual = level1ClientManual;
    }

    @Override
    public List<Entity> getStudentsByStudentId(String token, List<String> studentIds) throws IOException, SLIDataStoreException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<Entity> getStudents(String token) throws IOException, SLIDataStoreException {
        try {
            return level1ClientManual.getRequest(token, new URL("TODO"));
        } catch (URISyntaxException e) {
            throw new AssertionError(e);
        }
    }
}

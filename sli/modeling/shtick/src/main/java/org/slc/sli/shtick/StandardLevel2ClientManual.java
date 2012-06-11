package org.slc.sli.shtick;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import org.slc.sli.api.client.Entity;

/**
 * @author jstokes
 */
public final class StandardLevel2ClientManual implements Level2ClientManual {

    private final Level1ClientManual client;

    public StandardLevel2ClientManual(final Level1ClientManual client) {
        if (client == null) {
            throw new NullPointerException("client");
        }
        this.client = client;
    }

    @Override
    public List<Entity> getStudentsByStudentId(final String token, final List<String> studentIds) throws IOException,
            SLIDataStoreException {
        if (token == null) {
            throw new NullPointerException("token");
        }
        if (studentIds == null) {
            throw new NullPointerException("studentIds");
        }
        return null;  // To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<Entity> getStudents(final String token) throws IOException, SLIDataStoreException {
        if (token == null) {
            throw new NullPointerException("token");
        }
        try {
            return client.getRequest(token, new URL("TODO"));
        } catch (URISyntaxException e) {
            throw new AssertionError(e);
        }
    }
}

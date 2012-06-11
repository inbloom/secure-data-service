package org.slc.sli.shtick;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slc.sli.api.client.Entity;
import org.slc.sli.api.client.constants.v1.PathConstants;

/**
 * @author jstokes
 */
public final class StandardLevel2ClientManual implements Level2ClientManual {

    // TODO: pull in via properties
    private static final String BASE_URL = "http://local.slidev.org:8080/api/rest/v1/";

    private static final String SEP = "/";

    private final Level1Client client;

    public StandardLevel2ClientManual(final Level1Client client) {
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

        try {
            return client.getRequest(token, new URL(
                    BASE_URL + PathConstants.STUDENTS + SEP + StringUtils.join(studentIds, ',')));
        } catch (URISyntaxException e) {
            throw new AssertionError(e);
        }
    }

    @Override
    public List<Entity> getStudents(final String token) throws IOException, SLIDataStoreException {
        if (token == null) {
            throw new NullPointerException("token");
        }

        try {
            return client.getRequest(token, new URL(BASE_URL + PathConstants.STUDENTS));
        } catch (URISyntaxException e) {
            throw new AssertionError(e);
        }
    }
}

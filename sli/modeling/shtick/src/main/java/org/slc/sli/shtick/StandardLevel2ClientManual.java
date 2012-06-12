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

    private final String baseUrl;
    private static final String SEP = "/";
    private final Level1Client client;

    protected StandardLevel2ClientManual(final String baseUrl, final Level1Client client) {
        if (baseUrl == null) {
            throw new NullPointerException("baseUrl");
        }

        this.client = client;
        this.baseUrl = baseUrl;
    }

    public StandardLevel2ClientManual(final String baseUrl) {
        this(baseUrl, new StandardLevel1Client());
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
                    baseUrl + PathConstants.STUDENTS + SEP + StringUtils.join(studentIds, ',')));
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
            return client.getRequest(token, new URL(baseUrl + PathConstants.STUDENTS));
        } catch (URISyntaxException e) {
            throw new AssertionError(e);
        }
    }
}

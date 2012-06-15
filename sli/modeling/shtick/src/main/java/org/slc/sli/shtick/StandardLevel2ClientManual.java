package org.slc.sli.shtick;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

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
        this(baseUrl, new JsonLevel1Client());
    }

    @Override
    public List<RestEntity> getStudentsByStudentId(final String token, final List<String> studentIds,
            Map<String, Object> queryArgs) throws IOException, RestException {
        if (token == null) {
            throw new NullPointerException("token");
        }
        if (studentIds == null) {
            throw new NullPointerException("studentIds");
        }

        try {
            // FIXME: queryArgs, URLBuilder
            return client.getRequest(token,
                    new URL(baseUrl + PathConstants.STUDENTS + SEP + StringUtils.join(studentIds, ',')));
        } catch (URISyntaxException e) {
            throw new AssertionError(e);
        }
    }

    @Override
    public List<RestEntity> getStudents(final String token, Map<String, Object> queryArgs) throws IOException,
            RestException {
        if (token == null) {
            throw new NullPointerException("token");
        }

        try {
            // FIXME: ditto
            return client.getRequest(token, new URL(baseUrl + PathConstants.STUDENTS));
        } catch (URISyntaxException e) {
            throw new AssertionError(e);
        }
    }

    @Override
    public void deleteRequest(final String token, final String studentId) throws URISyntaxException, IOException,
            RestException {
        if (token == null) {
            throw new NullPointerException("token");
        }
        try {
            // FIXME: studentid
            client.deleteRequest(token, new URL(baseUrl + PathConstants.STUDENTS));
        } catch (final URISyntaxException e) {
            throw new AssertionError(e);
        }
    }

    @Override
    public String postRequest(String token, RestEntity entity) throws URISyntaxException, IOException, RestException {
        if (token == null) {
            throw new NullPointerException("token");
        }
        try {
            @SuppressWarnings("unused")
            final URL url = client.postRequest(token, entity, new URL(baseUrl + PathConstants.STUDENTS));
            // FIXME
            return "";
        } catch (final URISyntaxException e) {
            throw new AssertionError(e);
        }
    }

    @Override
    public void putRequest(String token, RestEntity entity) throws URISyntaxException, IOException, RestException {
        if (token == null) {
            throw new NullPointerException("token");
        }
        try {
            client.putRequest(token, entity, new URL(baseUrl + PathConstants.STUDENTS));
        } catch (final URISyntaxException e) {
            throw new AssertionError(e);
        }
    }
}

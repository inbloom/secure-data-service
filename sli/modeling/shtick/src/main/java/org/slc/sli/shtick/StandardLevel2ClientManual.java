package org.slc.sli.shtick;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

/**
 * @author jstokes
 */
public final class StandardLevel2ClientManual implements Level2ClientManual {

    private final String baseUrl;
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
    public List<Entity> getStudentsByStudentId(final String token, final List<String> studentIds,
            Map<String, Object> queryArgs) throws IOException, StatusCodeException {
        if (token == null) {
            throw new NullPointerException("token");
        }
        if (studentIds == null) {
            throw new NullPointerException("studentIds");
        }
        try {
            final String path = String.format("students/%s", StringUtils.join(studentIds, ','));
            final URLBuilder builder = URLBuilder.baseUrl(baseUrl).addPath(path).query(queryArgs);
            final URL url = builder.build();
            return client.getRequest(token, url);
        } catch (final URISyntaxException e) {
            throw new AssertionError(e);
        }
    }

    @Override
    public List<Entity> getStudents(final String token, Map<String, Object> queryArgs) throws IOException,
            StatusCodeException {
        if (token == null) {
            throw new NullPointerException("token");
        }
        try {
            final String path = String.format("students");
            final URLBuilder builder = URLBuilder.baseUrl(baseUrl).addPath(path).query(queryArgs);
            final URL url = builder.build();
            return client.getRequest(token, url);
        } catch (final URISyntaxException e) {
            throw new AssertionError(e);
        }
    }

    @Override
    public void deleteStudentById(final String token, final String entityId) throws IOException, StatusCodeException {
        if (token == null) {
            throw new NullPointerException("token");
        }
        try {
            final String path = String.format("students/%s", entityId);
            final URLBuilder builder = URLBuilder.baseUrl(baseUrl).addPath(path);
            final URL url = builder.build();
            client.deleteRequest(token, url);
        } catch (final URISyntaxException e) {
            throw new AssertionError(e);
        }
    }

    @Override
    public String postStudent(final String token, final Entity entity) throws IOException, StatusCodeException {
        if (token == null) {
            throw new NullPointerException("token");
        }
        if (entity == null) {
            throw new NullPointerException("entity");
        }
        try {
            final String path = "students";
            final URLBuilder builder = URLBuilder.baseUrl(baseUrl).addPath(path);
            final URL url = builder.build();
            final URL postedURL = client.postRequest(token, entity, url);
            return URLHelper.stripId(postedURL);
        } catch (final URISyntaxException e) {
            throw new AssertionError(e);
        }
    }

    @Override
    public void putStudent(final String token, final Entity entity) throws IOException, StatusCodeException {
        if (token == null) {
            throw new NullPointerException("token");
        }
        if (entity == null) {
            throw new NullPointerException("entity");
        }
        try {
            final String path = String.format("students/%s", entity.getId());
            final URLBuilder builder = URLBuilder.baseUrl(baseUrl).addPath(path);
            final URL url = builder.build();
            client.putRequest(token, entity, url);
        } catch (final URISyntaxException e) {
            throw new AssertionError(e);
        }
    }

}

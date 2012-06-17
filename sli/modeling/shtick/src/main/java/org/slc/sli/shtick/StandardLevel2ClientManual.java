package org.slc.sli.shtick;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
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
            final URIBuilder builder = URIBuilder.baseUri(baseUrl).addPath(path).query(queryArgs);
            final URI uri = builder.build();
            return client.get(token, uri);
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
            final URIBuilder builder = URIBuilder.baseUri(baseUrl).addPath(path).query(queryArgs);
            final URI uri = builder.build();
            return client.get(token, uri);
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
            final URIBuilder builder = URIBuilder.baseUri(baseUrl).addPath(path);
            final URI uri = builder.build();
            client.delete(token, uri);
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
            final URIBuilder builder = URIBuilder.baseUri(baseUrl).addPath(path);
            final URI uri = builder.build();
            final URI postedURL = client.post(token, entity, uri);
            return URIHelper.stripId(postedURL);
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
            final URIBuilder builder = URIBuilder.baseUri(baseUrl).addPath(path);
            final URI uri = builder.build();
            client.put(token, entity, uri);
        } catch (final URISyntaxException e) {
            throw new AssertionError(e);
        }
    }

}

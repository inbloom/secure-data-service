package org.slc.sli.shtick;

import java.net.URISyntaxException;
import java.net.URL;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientFactory;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.Response;

/**
 * @author jstokes
 */
public final class StandardLevel0Client implements Level0Client {
    /**
     * Header name used for specifying the bearer token.
     */
    private static final String HEADER_NAME_AUTHORIZATION = "Authorization";
    /**
     * Header value used for specifying the bearer token.
     */
    private static final String HEADER_VALUE_AUTHORIZATION_FORMAT = "Bearer %s";

    @SuppressWarnings("unused")
    private static final String HEADER_VALUE_CONTENT_TYPE = "content-type";

    private final Client client;

    public StandardLevel0Client() {
        this.client = ClientFactory.newClient();
    }

    @Override
    public Response getRequest(final String token, final URL url, final String mediaType) throws URISyntaxException,
            HttpRestException {
        if (token == null) {
            throw new NullPointerException("token");
        }
        if (url == null) {
            throw new NullPointerException("url");
        }
        if (mediaType == null) {
            throw new NullPointerException("mediaType");
        }

        final Invocation.Builder builder = createBuilder(token, url, mediaType);
        final Response response = builder.buildGet().invoke();

        return checkResponse(response, Response.Status.OK);
    }

    @Override
    public Response deleteRequest(final String token, final URL url, final String mediaType) throws URISyntaxException,
            HttpRestException {
        if (token == null) {
            throw new NullPointerException("token");
        }
        if (url == null) {
            throw new NullPointerException("url");
        }
        if (mediaType == null) {
            throw new NullPointerException("mediaType");
        }

        final Invocation.Builder builder = createBuilder(token, url, mediaType);
        final Response response = builder.buildDelete().invoke();

        return checkResponse(response, Response.Status.NO_CONTENT);
    }

    @Override
    public Response createRequest(final String token, final String data, final URL url, final String mediaType)
            throws URISyntaxException, HttpRestException {
        if (token == null) {
            throw new NullPointerException("token");
        }
        if (data == null) {
            throw new NullPointerException("data");
        }
        if (url == null) {
            throw new NullPointerException("url");
        }
        if (mediaType == null) {
            throw new NullPointerException("mediaType");
        }

        final Invocation.Builder builder = createBuilder(token, url, mediaType);
        final Response response = builder.buildPost(Entity.entity(data, mediaType)).invoke();

        return checkResponse(response, Response.Status.CREATED);
    }

    @Override
    public Response updateRequest(final String token, final String data, final URL url, final String mediaType)
            throws URISyntaxException, HttpRestException {
        if (token == null) {
            throw new NullPointerException("token");
        }
        if (url == null) {
            throw new NullPointerException("url");
        }
        if (data == null) {
            throw new NullPointerException("data");
        }
        if (mediaType == null) {
            throw new NullPointerException("mediaType");
        }

        final Invocation.Builder builder = createBuilder(token, url, mediaType);
        final Response response = builder.buildPut(Entity.entity(data, mediaType)).invoke();

        return checkResponse(response, Response.Status.CREATED);
    }

    private Invocation.Builder createBuilder(final String token, final URL url, final String mediaType)
            throws URISyntaxException {
        if (token == null) {
            throw new NullPointerException("token");
        }
        if (url == null) {
            throw new NullPointerException("url");
        }
        if (mediaType == null) {
            throw new NullPointerException("mediaType");
        }
        final Invocation.Builder builder = client.target(url.toURI()).request(mediaType);
        builder.header(HEADER_NAME_AUTHORIZATION, String.format(HEADER_VALUE_AUTHORIZATION_FORMAT, token));
        return builder;
    }

    private Response checkResponse(final Response response, final Response.Status expected) throws HttpRestException {
        if (response == null) {
            throw new NullPointerException("response");
        }
        if (expected == null) {
            throw new NullPointerException("expected");
        }
        if (response.getStatus() != expected.getStatusCode()) {
            throw new HttpRestException(response.getStatus());
        } else {
            return response;
        }
    }
}

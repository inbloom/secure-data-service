package org.slc.sli.shtick;

import java.net.URISyntaxException;
import java.net.URL;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientFactory;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.Response;

/**
 * @author jstokes
 */
public final class StandardLevel0ClientManual implements Level0ClientManual {
    /**
     * Header name used for specifying the bearer token.
     */
    private static final String HEADER_NAME_AUTHORIZATION = "Authorization";
    /**
     * Header value used for specifying the bearer token.
     */
    private static final String HEADER_VALUE_AUTHORIZATION_FORMAT = "Bearer %s";

    private final Client client;

    public StandardLevel0ClientManual() {
        this.client = ClientFactory.newClient();
    }

    @Override
    public Response getRequest(final String token, final URL url, final String mediaType) throws URISyntaxException {
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
        return builder.buildGet().invoke();
    }
}

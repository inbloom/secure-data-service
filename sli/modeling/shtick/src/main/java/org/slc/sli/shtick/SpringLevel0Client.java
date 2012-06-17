package org.slc.sli.shtick;

import java.net.URISyntaxException;
import java.net.URL;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

/**
 * @author jstokes
 */
public final class SpringLevel0Client implements Level0Client {

    /**
     * Header name used for specifying the bearer token.
     */
    private static final String HEADER_NAME_AUTHORIZATION = "Authorization";
    /**
     * Header value used for specifying the bearer token.
     */
    private static final String HEADER_VALUE_AUTHORIZATION_FORMAT = "Bearer %s";

    private final RestTemplate template;

    public SpringLevel0Client() {
        this.template = new RestTemplate();
    }

    @Override
    public String getRequest(final String token, final URL url, final String mediaType) throws URISyntaxException,
            StatusCodeException {
        if (token == null) {
            throw new NullPointerException("token");
        }
        if (url == null) {
            throw new NullPointerException("url");
        }
        if (mediaType == null) {
            throw new NullPointerException("mediaType");
        }

        final HttpHeaders headers = new HttpHeaders();
        headers.add(HEADER_NAME_AUTHORIZATION, String.format(HEADER_VALUE_AUTHORIZATION_FORMAT, token));

        final HttpEntity<String> entity = new HttpEntity<String>(headers);
        try {
            final ResponseEntity<String> response = template.exchange(url.toString(), HttpMethod.GET, entity,
                    String.class);
            return response.getBody();
        } catch (final HttpClientErrorException e) {
            throw new StatusCodeException(e.getStatusCode().value());
        }
    }

    @Override
    public void deleteRequest(final String token, final URL url, final String mediaType) throws URISyntaxException,
            StatusCodeException {
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public URL postRequest(final String token, final String data, final URL url, final String mediaType)
            throws URISyntaxException, StatusCodeException {
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public void putRequest(final String token, final String data, final URL url, final String mediaType)
            throws URISyntaxException, StatusCodeException {
        throw new UnsupportedOperationException("TODO");
    }
}

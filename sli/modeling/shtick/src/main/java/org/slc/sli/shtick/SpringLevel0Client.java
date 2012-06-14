package org.slc.sli.shtick;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.net.URISyntaxException;
import java.net.URL;

/**
 * @author jstokes
 */
public class SpringLevel0Client implements Level0Client {

    /**
     * Header name used for specifying the bearer token.
     */
    private static final String HEADER_NAME_AUTHORIZATION = "Authorization";
    /**
     * Header value used for specifying the bearer token.
     */
    private static final String HEADER_VALUE_AUTHORIZATION_FORMAT = "Bearer %s";

    private RestTemplate template;

    public SpringLevel0Client() {
        this.template = new RestTemplate();
    }

    @Override
    public RestResponse getRequest(String token, URL url, String mediaType) throws URISyntaxException, RestException {
        if (token == null)     { throw new NullPointerException("token"); }
        if (url == null)       { throw new NullPointerException("url"); }
        if (mediaType == null) { throw new NullPointerException("mediaType"); }

        final HttpHeaders headers = new HttpHeaders();
        headers.add(HEADER_NAME_AUTHORIZATION, String.format(HEADER_VALUE_AUTHORIZATION_FORMAT, token));

        final HttpEntity entity = new HttpEntity(headers);
        final ResponseEntity<String> response;

        try {
            response = template.exchange(url.toString(), HttpMethod.GET, entity, String.class);
        } catch (HttpClientErrorException e) {
            throw new RestException(e.getStatusCode().value());
        }

        return new RestResponse(response.getBody(), response.getStatusCode().value());
    }

    @Override
    public RestResponse deleteRequest(String token, URL url, String mediaType) throws URISyntaxException, RestException {
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public RestResponse postRequest(String token, String data, URL url, String mediaType) throws URISyntaxException, RestException {
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public RestResponse putRequest(String token, String data, URL url, String mediaType) throws URISyntaxException, RestException {
        throw new UnsupportedOperationException("TODO");
    }
}

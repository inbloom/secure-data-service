package org.slc.sli.shtick;

import java.net.URISyntaxException;
import java.net.URL;

/**
 * @author jstokes
 */
public interface Level0Client {
    RestResponse getRequest(final String token, final URL url, final String mediaType) throws URISyntaxException, RestException;

    RestResponse deleteRequest(final String token, final URL url, final String mediaType) throws URISyntaxException, RestException;

    RestResponse postRequest(final String token, final String data, final URL url, final String mediaType) throws URISyntaxException, RestException;

    RestResponse putRequest(final String token, final String data, final URL url, final String mediaType) throws URISyntaxException, RestException;
}

package org.slc.sli.shtick;

import java.net.URISyntaxException;
import java.net.URL;

/**
 * @author jstokes
 */
public interface Level0Client {
    String getRequest(final String token, final URL url, final String mediaType) throws URISyntaxException, RestException;

    void deleteRequest(final String token, final URL url, final String mediaType) throws URISyntaxException, RestException;

    URL postRequest(final String token, final String data, final URL url, final String mediaType) throws URISyntaxException, RestException;

    void putRequest(final String token, final String data, final URL url, final String mediaType) throws URISyntaxException, RestException;
}

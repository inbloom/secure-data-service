package org.slc.sli.shtick;

import java.net.URISyntaxException;
import java.net.URL;

import javax.ws.rs.core.Response;

/**
 * @author jstokes
 */
public interface Level0Client {
    Response getRequest(final String token, final URL url, final String mediaType) throws URISyntaxException, RestException;

    Response deleteRequest(final String token, final URL url, final String mediaType) throws URISyntaxException, RestException;

    Response createRequest(final String token, final String data, final URL url, final String mediaType) throws URISyntaxException, RestException;

    Response updateRequest(final String token, final String data, final URL url, final String mediaType) throws URISyntaxException, RestException;
}

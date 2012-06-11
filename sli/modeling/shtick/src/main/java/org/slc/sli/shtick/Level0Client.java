package org.slc.sli.shtick;

import java.net.URISyntaxException;
import java.net.URL;

import javax.ws.rs.core.Response;

/**
 * @author jstokes
 */
public interface Level0Client {
    Response getRequest(final String token, final URL url, final String mediaType) throws URISyntaxException;
}

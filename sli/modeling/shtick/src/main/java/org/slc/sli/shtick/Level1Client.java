package org.slc.sli.shtick;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

/**
 * @author jstokes
 */
public interface Level1Client {
    List<Entity> getRequest(final String token, final URL url) throws URISyntaxException, IOException, StatusCodeException;

    void deleteRequest(final String token, final URL url) throws URISyntaxException, IOException, StatusCodeException;

    URL postRequest(final String token, final Entity data, final URL url) throws URISyntaxException, IOException, StatusCodeException;

    void putRequest(final String token, final Entity data, final URL url) throws URISyntaxException, IOException, StatusCodeException;
}

package org.slc.sli.shtick.client;


import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * @author jstokes
 */
public interface Level0ClientManual {
    Response getRequest(final String token, final URL url) throws URISyntaxException, IOException;
}

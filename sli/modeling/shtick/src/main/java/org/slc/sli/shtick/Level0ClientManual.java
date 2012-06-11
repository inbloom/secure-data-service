package org.slc.sli.shtick;

import javax.ws.rs.core.Response;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * @author jstokes
 */
public interface Level0ClientManual {
    Response getRequest(final String token, final URL url) throws URISyntaxException;
}

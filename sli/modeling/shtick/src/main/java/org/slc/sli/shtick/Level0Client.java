package org.slc.sli.shtick;

import java.io.IOException;
import java.net.URI;

/**
 * @author jstokes
 */
public interface Level0Client {
    String get(final String token, final URI uri, final String mediaType) throws IOException, StatusCodeException;
    
    void delete(final String token, final URI uri, final String mediaType) throws IOException, StatusCodeException;
    
    URI post(final String token, final String data, final URI uri, final String mediaType) throws IOException,
            StatusCodeException;
    
    void put(final String token, final String data, final URI uri, final String mediaType) throws IOException,
            StatusCodeException;
}

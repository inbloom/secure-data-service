package org.slc.sli.shtick;

import java.io.IOException;
import java.net.URI;
import java.util.List;

/**
 * @author jstokes
 */
public interface Level1Client {
    List<Entity> get(final String token, final URI uri) throws IOException, StatusCodeException;

    void delete(final String token, final URI uri) throws IOException, StatusCodeException;

    URI post(final String token, final Entity data, final URI uri) throws IOException, StatusCodeException;

    void put(final String token, final Entity data, final URI uri) throws IOException, StatusCodeException;
}

package org.slc.sli.shtick;

import java.net.URI;

/**
 * @author jstokes
 */
final class URIHelper {
    static String stripId(final URI url) {
        return url.toString().substring(url.toString().lastIndexOf("/") + 1);
    }
}

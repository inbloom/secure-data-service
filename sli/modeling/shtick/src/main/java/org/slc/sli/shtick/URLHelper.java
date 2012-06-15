package org.slc.sli.shtick;

import java.net.URL;

/**
 * @author jstokes
 */
final class URLHelper {
    static String stripId(final URL url) {
        return url.toString().substring(url.toString().lastIndexOf("/") + 1);
    }
}

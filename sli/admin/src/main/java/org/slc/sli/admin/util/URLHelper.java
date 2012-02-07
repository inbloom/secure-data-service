package org.slc.sli.admin.util;

import javax.servlet.http.HttpServletRequest;

/**
 * URL Helper utility
 * @author scole
 *
 */
public class URLHelper {

    /**
     * Get the URL that generated this request
     * @param req
     * @return
     */
    public static String getUrl(HttpServletRequest req) {
        String reqUrl = req.getRequestURL().toString();
        String queryString = req.getQueryString();
        if (queryString != null) {
            reqUrl += "?" + queryString;
        }
        return reqUrl;
    }
}

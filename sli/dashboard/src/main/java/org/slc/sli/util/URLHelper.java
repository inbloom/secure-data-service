package org.slc.sli.util;

import javax.servlet.http.HttpServletRequest;

/**
 * 
 * @author svankina
 *TODO: Write javadoc
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


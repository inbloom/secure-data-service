package org.slc.sli.admin.util;

import java.io.UnsupportedEncodingException;

import org.springframework.web.util.UriUtils;

/**
 * Tool to build URLs
 * @author scole
 *
 */
public class UrlBuilder {
    private static final String ENCODING = "UTF-8";

    private StringBuffer url = new StringBuffer();

    public UrlBuilder(String base) {
        url.append(base);
    }

    public void addPath(String path) {
        addPathSeparaterIfNeeded();
        url.append(path);
    }

    private void addPathSeparaterIfNeeded() {
        if (url.charAt(url.length() - 1) != '/') {
            url.append("/");
        }
    }

    public void addQueryParam(String key, String value) {
        if (url.charAt(url.length() - 1) == '/') {
            url.deleteCharAt(url.length() - 1);
        }
        if (url.indexOf("?") < 0) {
            url.append("?");
        } else if (url.charAt(url.length() - 1) != '&') {
            url.append("&");
        }
        url.append(key).append("=");
        try {
            url.append(UriUtils.encodeQueryParam(value, ENCODING));
        } catch (UnsupportedEncodingException e) {
            url.append(value);
        }
    }

    /**
     * Returns the url as a String
     */
    @Override
    public String toString() {
        return url.toString();
    }


}

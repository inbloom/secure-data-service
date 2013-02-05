/*
 * Copyright 2012-2013 inBloom, Inc. and its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.slc.sli.dashboard.util;

import java.io.UnsupportedEncodingException;

import org.springframework.web.util.UriUtils;

/**
 * Tool to build URLs
 * @author scole
 *
 */
public class URLBuilder {
    private static final String ENCODING = "UTF-8";

    private StringBuffer url = new StringBuffer();

    public URLBuilder(String base) {
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

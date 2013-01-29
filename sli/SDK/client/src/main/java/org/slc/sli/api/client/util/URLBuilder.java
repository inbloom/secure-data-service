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

package org.slc.sli.api.client.util;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import org.slc.sli.api.client.constants.v1.PathConstants;

/**
 * Builder for creating a URL suitable for use with the SLI API ReSTful web service.
 */
public final class URLBuilder {
    private static final String ENCODING = "UTF-8";

    private final StringBuffer url = new StringBuffer();

    /**
     * Start building a new URL with the provided base location.
     *
     * @param baseUrl
     *            - base URL of the ReSTful API.
     * @return URLBuilder instance
     */
    public static URLBuilder create(final String baseUrl) {
        URLBuilder rval = new URLBuilder();
        rval.addPath(baseUrl);
        return rval;
    }

    /**
     * Append a path fragment to the current URL path.
     *
     * @param path
     *            URL fragment to add.
     * @return Updated URLBuilder instance.
     */
    public URLBuilder addPath(final String path) {
        if (!path.startsWith("/")) {
            addPathSeparaterIfNeeded();
        }
        url.append(path);
        return this;
    }

    /**
     * Append a path element for accessing the provided entity type.
     *
     * @param type
     *            Entity type of interest.
     * @return Updated URLBuilder instance.
     */
    public URLBuilder entityType(final String type) {
        String path = PathConstants.TEMP_MAP.get(type);
        if (path == null) {
        	path = PathConstants.TEMP_MAP.get(type + "s");
        }
        if (path == null && type.equals(PathConstants.SECURITY_SESSION_DEBUG)) {
            path = type;
        } else if (path == null && type.equals(PathConstants.HOME)) {
            path = type;
        } else if (path == null) {
            path = type + "s";
        }
        addPath(path);
        return this;
    }

    /**
     * Append an entity id to the path
     *
     * @param id
     *            Entity ID
     * @return Updated URLBuilder instance.
     */
    public URLBuilder id(final String id) {
        addPath(id);
        return this;
    }

    /**
     * Append a collection of entity ids to the path
     *
     * @param ids
     *            a collection of Entity IDs
     * @return Updated URLBuilder instance.
     */
    public URLBuilder ids(final List<String> ids) {
        StringBuffer idCollection = new StringBuffer();
        if (ids != null && ids.size() > 0) {
            for (String id : ids) {
                idCollection.append(id + ",");
            }
            idCollection.deleteCharAt(idCollection.lastIndexOf(","));
        }
        addPath(idCollection.toString());
        return this;
    }

    /**
     * Apply the given query to the URL.
     *
     * @param query
     * @return Updated URLBuilder instance.
     */
    public URLBuilder query(final Query query) {

        if (query != null) {
            Map<String, Object> params = query.getParameters();
            if (params != null) {
                for (Map.Entry<String, Object> entry : params.entrySet()) {
                    addQueryParameter(entry.getKey(), entry.getValue());
                }
            }
        }
        return this;
    }

    /**
     * Builds the URL.
     *
     * @return URL represented by the values set in this builder.
     * @throws MalformedURLException
     *             if the URL is not valid.
     */
    public URL build() throws MalformedURLException {

        return new URL(url.toString());
    }

    /**
     * Add a URL Query parameter to the URL.
     *
     * @param key
     *            query parameter name
     * @param value
     *            query parameter value
     * @return Updated URLBuilder instance.
     */
    private URLBuilder addQueryParameter(final String key, final Object value) {
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
            url.append(URLEncoder.encode(value.toString(), ENCODING).replace("+", "%20"));
        } catch (UnsupportedEncodingException e) {
            url.append(value);
        }
        return this;
    }

    private URLBuilder addPathSeparaterIfNeeded() {
        if (url.length() > 0 && url.charAt(url.length() - 1) != '/') {
            url.append("/");
        }
        return this;
    }
}

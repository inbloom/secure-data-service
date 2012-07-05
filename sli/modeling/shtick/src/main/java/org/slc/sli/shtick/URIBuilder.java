package org.slc.sli.shtick;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

/**
 * Builder for creating a URL suitable for use with the SLI API ReSTful web service.
 *
 * This class is intentionally package-protected.
 */
final class URIBuilder {
    private static final String ENCODING = "UTF-8";

    private final StringBuilder sb = new StringBuilder();

    /**
     * Start building a new URI with the provided base uri.
     *
     * @param baseUri
     *            - base URI of the ReSTful API.
     * @return URLBuilder instance
     */
    public static URIBuilder baseUri(final String baseUri) {
        final URIBuilder rval = new URIBuilder();
        rval.addPath(baseUri);
        return rval;
    }

    /**
     * Append a path fragment to the current URL path.
     *
     * @param path
     *            URL fragment to add.
     * @return Updated URLBuilder instance.
     */
    public URIBuilder addPath(final String path) {
        addPathSeparaterIfNeeded();
        sb.append(path);
        return this;
    }

    /**
     * Append an entity id to the path
     *
     * @param id
     *            Entity ID
     * @return Updated URLBuilder instance.
     */
    public URIBuilder id(final String id) {
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
    public URIBuilder ids(final List<String> ids) {
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
    public URIBuilder query(final Map<String, Object> params) {

        if (params != null) {
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                addQueryParameter(entry.getKey(), entry.getValue());
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
    public URI build() throws URISyntaxException {
        return new URI(sb.toString());
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
    private URIBuilder addQueryParameter(final String key, final Object value) {
        if (sb.charAt(sb.length() - 1) == '/') {
            sb.deleteCharAt(sb.length() - 1);
        }
        if (sb.indexOf("?") < 0) {
            sb.append("?");
        } else if (sb.charAt(sb.length() - 1) != '&') {
            sb.append("&");
        }
        sb.append(key).append("=");
        try {
            sb.append(URLEncoder.encode(value.toString(), ENCODING));
        } catch (UnsupportedEncodingException e) {
            sb.append(value);
        }
        return this;
    }

    private URIBuilder addPathSeparaterIfNeeded() {
        if (sb.length() > 0 && sb.charAt(sb.length() - 1) != '/') {
            sb.append("/");
        }
        return this;
    }
}

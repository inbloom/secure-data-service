package org.slc.sli.api.client;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

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
        addPathSeparaterIfNeeded();
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
    public URLBuilder entityType(final EntityType type) {
        addPath(type.getURL());
        return this;
    }
    
    /**
     * Append an entity id to the path
     * 
     * @Param id Entity ID
     * @return Updated URLBuilder instance.
     */
    public URLBuilder id(final String id) {
        addPath(id);
        return this;
    }
    
    /**
     * Apply the given query to the URL.
     * 
     * @param query
     * @return Updated URLBuilder instance.
     */
    public URLBuilder query(final Query query) {
        
        Map<String, Object> params = query.getParameters();
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            addQueryParameter(entry.getKey(), entry.getValue());
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
            url.append(URLEncoder.encode(value.toString(), ENCODING));
        } catch (UnsupportedEncodingException e) {
            url.append(value);
        }
        return this;
    }
    
    private URLBuilder addPathSeparaterIfNeeded() {
        if (url.charAt(url.length() - 1) != '/') {
            url.append("/");
        }
        return this;
    }
}

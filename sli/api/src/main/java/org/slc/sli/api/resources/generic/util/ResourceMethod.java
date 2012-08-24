package org.slc.sli.api.resources.generic.util;

/**
 * Created with IntelliJ IDEA.
 * User: srupasinghe
 * Date: 8/16/12
 * Time: 12:05 PM
 * To change this template use File | Settings | File Templates.
 */
public enum ResourceMethod {
    GET("GET"),
    POST("POST"),
    PUT("PUT"),
    DELETE("DELETE"),
    PATCH("PATCH");

    private final String method;

    private ResourceMethod(final String method) {
        this.method = method;
    }

    public String getMethod() {
        return method;
    }
}

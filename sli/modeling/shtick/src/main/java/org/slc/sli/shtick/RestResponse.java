package org.slc.sli.shtick;

/**
 * @author jstokes
 */
public final class RestResponse {
    private String body;
    private int statusCode;

    RestResponse(String body, int statusCode) {
        this.body = body;
        this.statusCode = statusCode;
    }

    public String getBody() {
        return body;
    }

    public int getStatusCode() {
        return statusCode;
    }
}

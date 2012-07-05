package org.slc.sli.shtick;

import java.util.List;
import java.util.Map;

/**
 * @author jstokes
 *
 *         Intentionally package-protected.
 */
final class RestResponse {
    private String body;
    private int statusCode;
    private Map<String, List<String>> headers;

    RestResponse(final String body, final int statusCode, final Map<String, List<String>> headers) {
        this.body = body;
        this.statusCode = statusCode;
        this.headers = headers;
    }

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

    public Map<String, List<String>> getHeaders() {
        return headers;
    }

    public String getHeader(String key) {
        if (this.headers.containsKey(key)) {
            // TODO : handle list case
            if (this.headers.get(key).size() >= 1) {
                return this.headers.get(key).get(0);
            }
        }

        throw new AssertionError();
    }
}

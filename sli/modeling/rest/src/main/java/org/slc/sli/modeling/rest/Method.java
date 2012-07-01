package org.slc.sli.modeling.rest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A <code>method</code> element describes the input to and output from an HTTP protocol method that
 * may be applied to a resource.
 */
public final class Method extends WadlElement {
    /**
     * GET
     */
    public static final String NAME_HTTP_GET = "GET";
    /**
     * POST
     */
    public static final String NAME_HTTP_POST = "POST";
    /**
     * PUT
     */
    public static final String NAME_HTTP_PUT = "PUT";
    /**
     * DELETE
     */
    public static final String NAME_HTTP_DELETE = "DELETE";
    /**
     * PATCH
     */
    public static final String NAME_HTTP_PATCH = "PATCH";

    private final String id;
    private final String verb;
    private final Request request;
    private final List<Response> responses;

    public Method(final String id, final String verb, final List<Documentation> doc, final Request request,
            final List<Response> responses) {
        super(doc);
        if (null == verb) {
            throw new NullPointerException("verb");
        }
        if (null == responses) {
            throw new NullPointerException("responses");
        }
        this.id = id;
        this.verb = checkVerb(verb);
        this.request = request;
        this.responses = Collections.unmodifiableList(new ArrayList<Response>(responses));
    }

    private static final String checkVerb(final String verb) {
        if (verb == null) {
            throw new NullPointerException("verb");
        }
        if (NAME_HTTP_POST.equals(verb)) {
            return verb;
        } else if (NAME_HTTP_GET.equals(verb)) {
            return verb;
        } else if (NAME_HTTP_PUT.equals(verb)) {
            return verb;
        } else if (NAME_HTTP_DELETE.equals(verb)) {
            return verb;
        } else {
            throw new IllegalArgumentException("verb : " + verb);
        }
    }

    public String getId() {
        return id;
    }

    public String getVerb() {
        return verb;
    }

    public Request getRequest() {
        return request;
    }

    public List<Response> getResponses() {
        return responses;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("id").append(" : ").append(id);
        sb.append(", ");
        sb.append("name").append(" : ").append(verb);
        sb.append(", ");
        sb.append("request").append(" : ").append(request);
        sb.append(", ");
        sb.append("responses").append(" : ").append(responses);
        sb.append(", ");
        sb.append("doc").append(" : ").append(getDocumentation());
        sb.append("}");
        return sb.toString();
    }
}
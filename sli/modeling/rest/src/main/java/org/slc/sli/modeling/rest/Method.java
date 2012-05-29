package org.slc.sli.modeling.rest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A <code>method</code> element describes the input to and output from an HTTP protocol method that
 * may be applied to a resource.
 */
public final class Method extends WadlElement {
    private final String id;
    private final String name;
    private final Request request;
    private final List<Response> responses;

    public Method(final String id, final String name, final List<Documentation> doc, final Request request,
            final List<Response> responses) {
        super(doc);
        if (null == name) {
            throw new NullPointerException("name");
        }
        if (null == responses) {
            throw new NullPointerException("responses");
        }
        this.id = id;
        this.name = name;
        this.request = request;
        this.responses = Collections.unmodifiableList(new ArrayList<Response>(responses));
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
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
        sb.append("name").append(" : ").append(name);
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
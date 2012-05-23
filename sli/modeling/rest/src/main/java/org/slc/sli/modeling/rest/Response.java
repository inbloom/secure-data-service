package org.slc.sli.modeling.rest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A <code>response</code> element describes the output that results from performing an HTTP method
 * on a resource.
 */
public final class Response extends WadlElement {
    private final List<String> statusCodes;
    private final List<Param> params;
    private final List<Representation> representations;

    public Response(final List<String> statusCodes, final List<Documentation> doc, final List<Param> params,
            final List<Representation> representations) {
        super(doc);
        if (null == statusCodes) {
            throw new NullPointerException("statusCodes");
        }
        if (null == params) {
            throw new NullPointerException("params");
        }
        if (null == representations) {
            throw new NullPointerException("representations");
        }
        this.statusCodes = Collections.unmodifiableList(new ArrayList<String>(statusCodes));
        this.params = Collections.unmodifiableList(new ArrayList<Param>(params));
        this.representations = Collections.unmodifiableList(new ArrayList<Representation>(representations));
    }

    public List<String> getStatusCodes() {
        return statusCodes;
    }

    public List<Param> getParams() {
        return params;
    }

    public List<Representation> getRepresentations() {
        return representations;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("statusCodes").append(" : ").append(statusCodes);
        sb.append(", ");
        sb.append("params").append(" : ").append(params);
        sb.append(", ");
        sb.append("representations").append(" : ").append(representations);
        sb.append(", ");
        sb.append("doc").append(" : ").append(getDocumentation());
        sb.append("}");
        return sb.toString();
    }
}
package org.slc.sli.modeling.rest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A <code>request</code> element describes the input to be included when applying an HTTP method to
 * a resource.
 */
public final class Request extends WadlElement {
    private final List<Param> params;
    private final List<Representation> representations;

    public Request(final List<Documentation> doc, final List<Param> params, final List<Representation> representations) {
        super(doc);
        if (null == params) {
            throw new NullPointerException("params");
        }
        if (null == representations) {
            throw new NullPointerException("representations");
        }
        this.params = Collections.unmodifiableList(new ArrayList<Param>(params));
        this.representations = Collections.unmodifiableList(new ArrayList<Representation>(representations));
    }

    public List<Param> getParams() {
        return params;
    }

    /**
     * Note that the use of
     * <code>representation/code> elements is confined to HTTP methods that accept an entity body in the request (e.g. PUT or POST). Sibling <code>representation</code>
     * elements represent logically equivalent alternatives.
     */
    public List<Representation> getRepresentations() {
        return representations;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("params").append(" : ").append(params);
        sb.append(", ");
        sb.append("representations").append(" : ").append(representations);
        sb.append(", ");
        sb.append("doc").append(" : ").append(getDocumentation());
        sb.append("}");
        return sb.toString();
    }
}
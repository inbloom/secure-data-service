package org.slc.sli.modeling.rest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The <code>resources</code> element acts as a container for the resources provided by the
 * application.
 */
public final class Resources extends WadlElement {
    private final String base;
    private final List<Resource> resources;

    public Resources(final String base, final List<Documentation> doc, final List<Resource> resources) {
        super(doc);
        if (null == base) {
            throw new NullPointerException("base");
        }
        if (null == resources) {
            throw new NullPointerException("resources");
        }
        this.base = base;
        this.resources = Collections.unmodifiableList(new ArrayList<Resource>(resources));
    }

    /**
     * Provides the base URI for each child resource identifier.
     */
    public String getBase() {
        return base;
    }

    /**
     * The resources provided by the application.
     */
    public List<Resource> getResources() {
        return resources;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("base").append(" : ").append(base);
        sb.append(", ");
        sb.append("resources").append(" : ").append(resources);
        sb.append(", ");
        sb.append("doc").append(" : ").append(getDocumentation());
        sb.append("}");
        return sb.toString();
    }
}
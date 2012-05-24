package org.slc.sli.modeling.rest;

import java.util.List;

/**
 * Used to identify links to resources within representations.
 */
public final class Link extends WadlElement {
    private final String resourceType;
    private final String rel;
    private final String rev;

    public Link(final String resourceType, final String rel, final String rev, final List<Documentation> doc) {
        super(doc);
        if (null == resourceType) {
            throw new NullPointerException("resourceType");
        }
        if (null == rel) {
            throw new NullPointerException("rel");
        }
        if (null == rev) {
            throw new NullPointerException("rev");
        }
        if (null == doc) {
            throw new NullPointerException("doc");
        }
        this.resourceType = resourceType;
        this.rel = rel;
        this.rev = rev;
    }

    public String getResourceType() {
        return resourceType;
    }

    public String getRel() {
        return rel;
    }

    public String getRev() {
        return rev;
    }
}
package org.slc.sli.modeling.rest;

import java.util.List;

public final class Include extends WadlElement {
    private final String href;

    public Include(final String href, final List<Documentation> doc) {
        super(doc);
        if (null == href) {
            throw new NullPointerException("href");
        }
        this.href = href;
    }

    public String getHref() {
        return href;
    }
}
package org.slc.sli.modeling.rest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class WadlElement {
    private final List<Documentation> doc;

    public WadlElement(final List<Documentation> doc) {
        if (null == doc) {
            throw new NullPointerException("doc");
        }
        this.doc = Collections.unmodifiableList(new ArrayList<Documentation>(doc));
    }

    public final List<Documentation> getDocumentation() {
        return doc;
    }
}
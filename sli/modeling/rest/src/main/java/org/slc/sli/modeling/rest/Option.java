package org.slc.sli.modeling.rest;

import java.util.List;

/**
 * An <code>option</code> element defines one of a set of possible values for the parameter
 * represented by its parent <code>param</code> element. An <code>option</code> element has a
 * required <code>value</code> attribute that defines the value.
 */
public final class Option extends WadlElement {
    private final String value;

    public Option(final String value, final List<Documentation> doc) {
        super(doc);
        if (null == value) {
            throw new NullPointerException("value");
        }
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
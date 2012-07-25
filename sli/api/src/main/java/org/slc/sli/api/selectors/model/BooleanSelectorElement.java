package org.slc.sli.api.selectors.model;

import org.slc.sli.modeling.uml.Type;

/**
 * @author jstokes
 */
public class BooleanSelectorElement implements SelectorElement {
    private final boolean qualifier;
    private final Type type;
    private final String attribute;

    public BooleanSelectorElement(final Type type, final boolean qualifier) {
        this.qualifier = qualifier;
        this.type = type;
        this.attribute = null;
    }

    public BooleanSelectorElement(final String attribute, final boolean qualifier) {
        this.qualifier = qualifier;
        this.attribute = attribute;
        this.type = null;
    }

    @Override
    public boolean isTyped() {
        return type != null;
    }

    @Override
    public boolean isAttribute() {
        return attribute != null;
    }

    @Override
    public Object getLHS() {
        return isTyped() ? type : attribute;
    }

    @Override
    public Object getRHS() {
        return qualifier;
    }

    public boolean getQualifier() {
        return qualifier;
    }
}

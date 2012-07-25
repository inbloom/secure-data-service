package org.slc.sli.api.selectors.model;

import org.slc.sli.modeling.uml.Type;

/**
 * @author jstokes
 */
public class IncludeAllSelectorElement implements SelectorElement {
    private final Type type;

    public IncludeAllSelectorElement(final Type type) {
        this.type = type;
    }

    @Override
    public boolean isTyped() {
        return true;
    }

    @Override
    public boolean isAttribute() {
        return false;
    }

    @Override
    public Object getLHS() {
        return type;
    }

    @Override
    public Object getRHS() {
        return SelectorElement.INCLUDE_ALL;
    }
}

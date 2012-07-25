package org.slc.sli.api.selectors.model;

import org.slc.sli.api.selectors.doc.SelectorQuery;
import org.slc.sli.api.selectors.doc.SelectorQueryVisitor;
import org.slc.sli.modeling.uml.Type;


/**
 * @author jstokes
 */
public class ComplexSelectorElement implements SelectorElement {
    private final SemanticSelector selector;
    private final Type type;

    public ComplexSelectorElement(final Type type, final SemanticSelector selector) {
        this.type = type;
        this.selector = selector;
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
        return selector;
    }

    public SemanticSelector getSelector() {
        return selector;
    }

    @Override
    public SelectorQuery accept(SelectorQueryVisitor selectorQueryVisitor) {
        return selectorQueryVisitor.visit(this);
    }
}

package org.slc.sli.api.selectors.model;

import org.slc.sli.api.selectors.doc.SelectorQuery;
import org.slc.sli.api.selectors.doc.SelectorQueryVisitor;
import org.slc.sli.modeling.uml.ClassType;
import org.slc.sli.modeling.uml.ModelElement;


/**
 * @author jstokes
 */
public class BooleanSelectorElement implements SelectorElement {
    private final boolean qualifier;
    private final ModelElement modelElement;
    private boolean typed;

    public BooleanSelectorElement(final ModelElement modelElement, final boolean qualifier) {
        this.qualifier = qualifier;
        this.modelElement = modelElement;
        this.typed = modelElement instanceof ClassType;
    }

    @Override
    public boolean isTyped() {
        return typed;
    }

    @Override
    public boolean isAttribute() {
        return !typed;
    }

    @Override
    public ModelElement getLHS() {
        return modelElement;
    }

    @Override
    public Object getRHS() {
        return qualifier;
    }

    public boolean getQualifier() {
        return qualifier;
    }

    @Override
    public SelectorQuery accept(final SelectorQueryVisitor selectorQueryVisitor) {
        return selectorQueryVisitor.visit(this);
    }
}

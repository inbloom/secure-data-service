package org.slc.sli.api.selectors.model;

import org.slc.sli.api.selectors.doc.SelectorQuery;
import org.slc.sli.api.selectors.doc.SelectorQueryVisitor;
import org.slc.sli.modeling.uml.Attribute;
import org.slc.sli.modeling.uml.ClassType;
import org.slc.sli.modeling.uml.ModelElement;


/**
 * Implementation of a selector element that has a ModelElement => boolean structure
 *
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

    @Override
    public String getElementName() {
        if (modelElement instanceof ClassType) {
            return ((ClassType) modelElement).getName();
        } else if (modelElement instanceof Attribute) {
            return ((Attribute) modelElement).getName();
        }
        return null;
    }

    public boolean getQualifier() {
        return qualifier;
    }

    @Override
    public SelectorQuery accept(final SelectorQueryVisitor selectorQueryVisitor) {
        return selectorQueryVisitor.visit(this);
    }

    @Override
    public String toString() {
        return "{" + getElementName() + " : " + getQualifier() + "}";
    }
}

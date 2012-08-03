package org.slc.sli.api.selectors.model.elem;

import org.slc.sli.api.selectors.doc.SelectorQuery;
import org.slc.sli.api.selectors.doc.SelectorQueryVisitor;
import org.slc.sli.modeling.uml.Attribute;
import org.slc.sli.modeling.uml.ClassType;
import org.slc.sli.modeling.uml.ModelElement;

/**
 * @author jstokes
 */
public class IncludeXSDSelectorElement implements SelectorElement {

    private final ModelElement modelElement;
    private final boolean typed;

    public IncludeXSDSelectorElement(final ModelElement modelElement) {
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
        return SelectorElement.INCLUDE_XSD;
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

    @Override
    public String toString() {
        return "$";
    }

    @Override
    public SelectorQuery accept(final SelectorQueryVisitor selectorQueryVisitor) {
        return selectorQueryVisitor.visit(this);
    }
}

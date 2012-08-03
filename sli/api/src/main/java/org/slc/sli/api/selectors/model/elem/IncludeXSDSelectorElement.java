package org.slc.sli.api.selectors.model.elem;

import org.slc.sli.api.selectors.doc.SelectorQuery;
import org.slc.sli.api.selectors.doc.SelectorQueryVisitor;
import org.slc.sli.modeling.uml.Attribute;
import org.slc.sli.modeling.uml.ClassType;
import org.slc.sli.modeling.uml.ModelElement;

/**
 * @author jstokes
 */
public class IncludeXSDSelectorElement extends AbstractSelectorElement implements SelectorElement {

    public IncludeXSDSelectorElement(final ModelElement modelElement) {
        super.setElement(modelElement);
        super.setTyped(modelElement instanceof  ClassType);
    }

    @Override
    public Object getRHS() {
        return SelectorElement.INCLUDE_XSD;
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

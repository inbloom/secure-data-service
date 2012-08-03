package org.slc.sli.api.selectors.model.elem;

import org.slc.sli.api.selectors.doc.SelectorQuery;
import org.slc.sli.api.selectors.doc.SelectorQueryVisitor;
import org.slc.sli.modeling.uml.ClassType;
import org.slc.sli.modeling.uml.ModelElement;

/**
 * @author jstokes
 */
public class IncludeDefaultSelectorElement extends AbstractSelectorElement implements SelectorElement {
    public IncludeDefaultSelectorElement(final ModelElement modelElement) {
        super.setElement(modelElement);
        super.setTyped(modelElement instanceof ClassType);
    }

    @Override
    public Object getRHS() {
        return SelectorElement.INCLUDE_DEFAULT;
    }

    @Override
    public SelectorQuery accept(SelectorQueryVisitor selectorQueryVisitor) {
        return selectorQueryVisitor.visit(this);
    }

    @Override
    public String toString() {
        return ".";
    }
}

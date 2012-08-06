package org.slc.sli.api.selectors.model.elem;

import org.slc.sli.api.selectors.doc.SelectorQueryPlan;
import org.slc.sli.api.selectors.doc.SelectorQueryVisitor;
import org.slc.sli.modeling.uml.ClassType;
import org.slc.sli.modeling.uml.ModelElement;

/**
 * @author jstokes
 */
public class EmptySelectorElement extends AbstractSelectorElement implements SelectorElement {

    public EmptySelectorElement(final ModelElement modelElement) {
        super.setElement(modelElement);
        super.setTyped(modelElement instanceof ClassType);
    }

    @Override
    public Object getRHS() {
        return SelectorElement.EMPTY;
    }

    @Override
    public SelectorQueryPlan accept(final SelectorQueryVisitor selectorQueryVisitor) {
        return selectorQueryVisitor.visit(this);
    }

    @Override
    public String toString() {
        return getElementName() + ":()";
    }
}

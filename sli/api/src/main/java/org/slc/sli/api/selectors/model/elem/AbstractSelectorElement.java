package org.slc.sli.api.selectors.model.elem;


import org.slc.sli.modeling.uml.Attribute;
import org.slc.sli.modeling.uml.ClassType;
import org.slc.sli.modeling.uml.ModelElement;

/**
 * @author jstokes
 */
public abstract class AbstractSelectorElement {

    private ModelElement modelElement;
    private boolean typed;

    public boolean isTyped() {
        return getTyped();
    }

    public boolean isAttribute() {
        return !getTyped();
    }

    public ModelElement getLHS() {
        return getElement();
    }

    public String getElementName() {
        if (modelElement instanceof ClassType) {
            return ((ClassType) modelElement).getName();
        } else if (modelElement instanceof Attribute) {
            return ((Attribute) modelElement).getName();
        }
        return null;
    }

    protected ModelElement getElement() {
        return modelElement;
    }

    protected void setElement(final ModelElement element) {
        this.modelElement = element;
    }

    protected boolean getTyped() {
        return typed;
    }

    protected void setTyped(final boolean typed) {
        this.typed = typed;
    }
}

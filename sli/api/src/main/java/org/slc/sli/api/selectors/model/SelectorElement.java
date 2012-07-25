package org.slc.sli.api.selectors.model;

import org.slc.sli.api.selectors.doc.SelectorQueryVisitable;
import org.slc.sli.modeling.uml.ModelElement;

/**
 * @author jstokes
 */
public interface SelectorElement extends SelectorQueryVisitable {
    /**
     * Sentinel value for including all attributes
     */
    public static final String INCLUDE_ALL = "*";

    /**
     *
     * @return true if <code>SelectorElement</code> is typed, false if otherwise
     */
    public boolean isTyped();

    /**
     * @return true is <code>SelectorElement</code> is an attribute, false if otherwise
     */
    public boolean isAttribute();

    /**
     * @return The model element identifier on the left-hand side of the selector element
     */
    public ModelElement getLHS();

    /**
     * @return The value on the right-hand side of the selector element.
     * This could be a <code>boolean</code>, include all element or a <code>SemanticSelector</code>
     */
    public Object getRHS();

    /**
     * @return The string representation of the left hand side of the <code>SelectorElement</code>
     */
    public String getElementName();
}

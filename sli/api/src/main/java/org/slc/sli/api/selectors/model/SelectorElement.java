package org.slc.sli.api.selectors.model;

import org.slc.sli.api.selectors.doc.SelectorQueryVisitable;
import org.slc.sli.modeling.uml.ModelElement;

/**
 * @author jstokes
 */
public interface SelectorElement extends SelectorQueryVisitable {
    public static String INCLUDE_ALL = "*";

    public boolean isTyped();
    public boolean isAttribute();
    public ModelElement getLHS();
    public Object getRHS();
}

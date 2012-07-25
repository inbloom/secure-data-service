package org.slc.sli.api.selectors.model;

import org.slc.sli.api.selectors.doc.SelectorQueryVisitable;

/**
 * @author jstokes
 */
public interface SelectorElement extends SelectorQueryVisitable {
    public static String INCLUDE_ALL = "*";

    public boolean isTyped();
    public boolean isAttribute();
    public Object getLHS();
    public Object getRHS();
}

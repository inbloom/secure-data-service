package org.slc.sli.api.selectors.model;

/**
 * @author jstokes
 */
public interface SelectorElement {
    public static String INCLUDE_ALL = "*";

    public boolean isTyped();
    public boolean isAttribute();
    public Object getLHS();
    public Object getRHS();
}

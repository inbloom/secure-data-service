package org.slc.sli.modeling.xmi;

/**
 * Symbolic constants used for attribute names in XMI.
 */
public enum XmiAttributeName {
    /**
     * The identifier used by XMI (xmi.id).
     */
    ID("xmi.id"),
    /**
     * The lower bound attribute used by XMI (lower).
     */
    LOWER("lower"),
    /**
     * The name attribute used by XMI (name).
     */
    NAME("name"),
    /**
     * The identifier reference used by XMI (xmi.idref).
     */
    IDREF("xmi.idref"),
    /**
     * The upper bound attribute used by XMI (upper).
     */
    UPPER("upper");
    
    private final String localName;
    
    XmiAttributeName(final String localName) {
        this.localName = localName;
    }
    
    public String getLocalName() {
        return localName;
    }
}

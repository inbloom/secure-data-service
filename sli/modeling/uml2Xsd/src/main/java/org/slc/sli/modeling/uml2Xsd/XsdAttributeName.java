package org.slc.sli.modeling.uml2Xsd;

/**
 * Symbolic constants used for attribute names in XMI.
 */
public enum XsdAttributeName {
    /**
     * The upper bound attribute used by XMI (upper).
     */
    BASE("base"),
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
     * The type attribute used by WXS (type).
     */
    TYPE("type"),
    /**
     * The upper bound attribute used by XMI (upper).
     */
    UPPER("upper"),
    /**
     * The value attribute used by WXS (value).
     */
    VALUE("value");
    
    private final String localName;
    
    XsdAttributeName(final String localName) {
        this.localName = localName;
    }
    
    public String getLocalName() {
        return localName;
    }
}

package org.slc.sli.modeling.xsd;

/**
 * Symbolic constants used for element names in W3C XML Schema.
 */
public enum XsdElementName {
    /**
     * annotation
     */
    ANNOTATION("annotation"),
    /**
     * complexType
     */
    COMPLEX_TYPE("complexType"),
    /**
     * documentation
     */
    DOCUMENTATION("documentation"),
    /**
     * element
     */
    ELEMENT("element"),
    /**
     * enumeration
     */
    ENUMERATION("enumeration"),
    /**
     * restriction
     */
    RESTRICTION("restriction"),
    /**
     * sequence
     */
    SEQUENCE("sequence"),
    /**
     * simpleType
     */
    SIMPLE_TYPE("simpleType");
    
    private final String localName;
    
    XsdElementName(final String localName) {
        if (localName == null) {
            throw new NullPointerException("localName");
        }
        this.localName = localName;
    }
    
    public String getLocalName() {
        return localName;
    }
}

package org.slc.sli.modeling.xsd;

/**
 * Symbolic constants used for attribute names in W3C XML Schema.
 */
public enum XsdAttributeName {
    /**
     * attributeFormDefault
     */
    ATTRIBUTE_FORM_DEFAULT("attributeFormDefault"),
    /**
     * base
     */
    BASE("base"),
    /**
     * elementFormDefault
     */
    ELEMENT_FORM_DEFAULT("elementFormDefault"),
    /**
     * maxOccurs
     */
    MAX_OCCURS("maxOccurs"),
    /**
     * minOccurs.
     */
    MIN_OCCURS("minOccurs"),
    /**
     * name
     */
    NAME("name"),
    /**
     * type
     */
    TYPE("type"),
    /**
     * value
     */
    VALUE("value");
    
    private final String localName;
    
    XsdAttributeName(final String localName) {
        if (localName == null) {
            throw new NullPointerException("localName");
        }
        this.localName = localName;
    }
    
    public String getLocalName() {
        return localName;
    }
}

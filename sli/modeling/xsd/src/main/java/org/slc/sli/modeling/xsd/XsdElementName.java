package org.slc.sli.modeling.xsd;

/**
 * Symbolic constants used for element names in W3C XML Schema.
 */
public enum XsdElementName {
    /**
     * all
     */
    ALL("all"),
    /**
     * annotation
     */
    ANNOTATION("annotation"),
    /**
     * appinfo
     */
    APPINFO("appinfo"),
    /**
     * choice
     */
    CHOICE("choice"),
    /**
     * complexContent
     */
    COMPLEX_CONTENT("complexContent"),
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
     * extension
     */
    EXTENSION("extension"),
    /**
     * fractionDigits
     */
    FRACTION_DIGITS("fractionDigits"),
    /**
     * length
     */
    LENGTH("length"),
    /**
     * maxExclusive
     */
    MAX_EXCLUSIVE("maxExclusive"),
    /**
     * maxInclusive
     */
    MAX_INCLUSIVE("maxInclusive"),
    /**
     * maxLength
     */
    MAX_LENGTH("maxLength"),
    /**
     * minExclusive
     */
    MIN_EXCLUSIVE("minExclusive"),
    /**
     * minInclusive
     */
    MIN_INCLUSIVE("minInclusive"),
    /**
     * minLength
     */
    MIN_LENGTH("minLength"),
    /**
     * pattern
     */
    PATTERN("pattern"),
    /**
     * restriction
     */
    RESTRICTION("restriction"),
    /**
     * schema
     */
    SCHEMA("schema"),
    /**
     * sequence
     */
    SEQUENCE("sequence"),
    /**
     * simpleType
     */
    SIMPLE_TYPE("simpleType"),
    /**
     * totalDigits
     */
    TOTAL_DIGITS("totalDigits");
    
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

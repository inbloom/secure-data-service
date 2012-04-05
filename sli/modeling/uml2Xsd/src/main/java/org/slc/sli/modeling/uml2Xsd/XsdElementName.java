package org.slc.sli.modeling.uml2Xsd;

/**
 * Symbolic constants used for element names in XMI.
 */
public enum XsdElementName {
    /**
     * 
     */
    ASSOCIATION("Association"),
    /**
     * 
     */
    ASSOCIATION_DOT_CONNECTION("Association.connection"),
    /**
     * 
     */
    ASSOCIATION_END("AssociationEnd"),
    /**
     * 
     */
    ASSOCIATION_END_DOT_MULTIPLICITY("AssociationEnd.multiplicity"),
    /**
     * 
     */
    ASSOCIATION_END_DOT_PARTICIPANT("AssociationEnd.participant"),
    /**
     * 
     */
    ATTRIBUTE("Attribute"),
    /**
     * 
     */
    OPERATION("Operation"),
    /**
     * 
     */
    CLASS("Class"),
    /**
     * 
     */
    CLASSIFIER_DOT_FEATURE("Classifier.feature"),
    /**
     * 
     */
    DATA_TYPE("DataType"),
    /**
     * 
     */
    SIMPLE_TYPE("simpleType"),
    /**
     * 
     */
    ENUMERATION("enumeration"),
    /**
     * 
     */
    RESTRICTION("restriction"),
    /**
     * 
     */
    MODEL_ELEMENT_DOT_TAGGED_VALUE("ModelElement.taggedValue"),
    /**
     * 
     */
    MULTIPLICITY("Multiplicity"),
    /**
     * 
     */
    MULTIPLICITY_DOT_RANGE("Multiplicity.range"),
    /**
     * 
     */
    MULTIPLICITY_RANGE("MultiplicityRange"),
    /**
     * 
     */
    STRUCTURAL_FEATURE_DOT_MULTIPLICITY("StructuralFeature.multiplicity"),
    /**
     * 
     */
    STRUCTURAL_FEATURE_DOT_TYPE("StructuralFeature.type"),
    /**
     * 
     */
    TAG_DEFINITION("TagDefinition"),
    /**
     * 
     */
    TAG_DEFINITION_DOT_MULTIPLICITY("TagDefinition.multiplicity"),
    /**
     * 
     */
    TAGGED_VALUE("TaggedValue"),
    /**
     * 
     */
    TAGGED_VALUE_DOT_DATA_VALUE("TaggedValue.dataValue"),
    /**
     * 
     */
    TAGGED_VALUE_DOT_TYPE("TaggedValue.type");
    
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
    
    public static final XsdElementName getElementName(final String localName) {
        for (final XsdElementName name : values()) {
            if (name.localName.equals(localName)) {
                return name;
            }
        }
        return null;
    }
}

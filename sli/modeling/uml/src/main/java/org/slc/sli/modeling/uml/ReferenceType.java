package org.slc.sli.modeling.uml;

/**
 * Describes the type of an identifier so that polymorphic lookups can be performed.
 */
public enum ReferenceType {
    /**
     * 
     */
    CLASS_TYPE,
    /**
     * 
     */
    DATA_TYPE,
    /**
     * 
     */
    ENUM_TYPE,
    /**
     * 
     */
    TAG_DEFINITION,
    /**
     * The type cannot be determined (usually due to lazy loading).
     */
    UNKNOWN_TYPE;
}

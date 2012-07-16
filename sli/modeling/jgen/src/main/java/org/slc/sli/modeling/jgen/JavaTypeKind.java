package org.slc.sli.modeling.jgen;

/**
 * A category for a {@link JavaType}.
 */
public enum JavaTypeKind {
    /**
     * May have references to other types.
     */
    COMPLEX,
    /**
     * Represents a finite domain.
     */
    ENUM,
    /**
     * May not have references to other types and not an enumeration.
     */
    SIMPLE;
}

package org.slc.sli.modeling.uml;

/**
 * Represents a restricted set of occurrence values (0,1,*).
 * 
 * This seems to be sufficient for "government work".
 */
public enum Occurs {
    /**
     * Zero.
     */
    ZERO,
    /**
     * One
     */
    ONE,
    /**
     * Unbounded
     */
    UNBOUNDED
}

package org.slc.sli.modeling.uml;

/**
 * A type in the system. This could be a class, a data-type or an enumeration.
 */
public interface Type extends HasName, HasModifiers {
    
    /**
     * Returns a reference to this type.
     */
    Reference getReference();
}

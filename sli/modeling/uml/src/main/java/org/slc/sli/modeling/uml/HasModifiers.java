package org.slc.sli.modeling.uml;

/**
 * Providers the UML modifiers.
 */
public interface HasModifiers {
    
    /**
     * Determines whether this entity is abstract (cannot be instantiated) or concrete (can be
     * instantiated).
     */
    boolean isAbstract();
}

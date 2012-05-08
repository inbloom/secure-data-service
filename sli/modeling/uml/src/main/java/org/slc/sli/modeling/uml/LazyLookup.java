package org.slc.sli.modeling.uml;

/**
 * Provides the ability to lookup (forward) references. The purpose is to create the illusion that
 * the objects form a closed graph.
 */
public interface LazyLookup {
    
    /**
     * Returns the type specified by the reference.
     * 
     * @param reference
     *            The reference to the type.
     * @return the type required.
     */
    Type getType(Reference reference);
    
    /**
     * Returns the tag definition for the specified reference.
     * 
     * @param reference
     *            The reference to the tag definition.
     * @return the tag definition required.
     */
    TagDefinition getTagDefinition(Reference reference);
    
}

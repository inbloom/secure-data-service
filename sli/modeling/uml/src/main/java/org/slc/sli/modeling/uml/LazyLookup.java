package org.slc.sli.modeling.uml;

import java.util.List;

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
    
    /**
     * Returns a list of generalizations for the derived reference.
     * 
     * @param derived
     *            The derived reference.
     * @return The base generalizations.
     */
    List<Generalization> getGeneralizationBase(Reference derived);
    
    /**
     * Returns a list of generalizations for the derived reference.
     * 
     * @param base
     *            The base type reference.
     * @return The base generalizations.
     */
    List<Generalization> getGeneralizationDerived(Reference base);
    
    /**
     * Returns a list of association ends for the type reference.
     * 
     * @param type
     *            The type reference.
     * @return The type associations.
     */
    List<AssociationEnd> getAssociationEnds(Reference type);
}

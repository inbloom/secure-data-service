package org.slc.sli.modeling.uml;

import java.util.List;

/**
 * A type in the system. This could be a class, a data-type or an enumeration.
 */
public interface Type extends HasName, HasModifiers, HasTaggedValues {
    
    /**
     * Returns a reference to this type.
     */
    Reference getReference();
    
    /**
     * Returns the generalizations that form the base for this type.
     */
    List<Generalization> getGeneralizationBase();
    
    /**
     * Returns the generalizations that are derived from this type.
     */
    List<Generalization> getGeneralizationDerived();
    
    /**
     * Returns the association ends that are associated with this type.
     */
    List<AssociationEnd> getAssociationEnds();
}

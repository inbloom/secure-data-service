package org.slc.sli.modeling.uml.index;

import java.util.List;
import java.util.Set;

import org.slc.sli.modeling.uml.AssociationEnd;
import org.slc.sli.modeling.uml.ClassType;
import org.slc.sli.modeling.uml.DataType;
import org.slc.sli.modeling.uml.EnumType;
import org.slc.sli.modeling.uml.Generalization;
import org.slc.sli.modeling.uml.Identifier;
import org.slc.sli.modeling.uml.TagDefinition;
import org.slc.sli.modeling.uml.Type;
import org.slc.sli.modeling.uml.UmlModelElement;
import org.slc.sli.modeling.uml.Visitor;

/**
 * Provides the ability to lookup references.
 *
 * The purpose is to enable the parse model to be used directly.
 */
public interface Mapper {

    /**
     * Returns a list of association ends for the type reference.
     *
     * @param type
     *            The type reference.
     * @return The type associations.
     */
    List<AssociationEnd> getAssociationEnds(Identifier type);

    Iterable<ClassType> getClassTypes();

    Iterable<DataType> getDataTypes();

    Iterable<EnumType> getEnumTypes();

    /**
     * Returns a list of generalizations for the derived reference.
     *
     * @param derived
     *            The derived reference.
     * @return The base generalizations.
     */
    List<Generalization> getGeneralizationBase(Identifier derived);

    /**
     * Returns a list of generalizations for the derived reference.
     *
     * @param base
     *            The base type reference.
     * @return The base generalizations.
     */
    List<Generalization> getGeneralizationDerived(Identifier base);

    /**
     * Returns the tag definition for the specified reference.
     *
     * @param reference
     *            The reference to the tag definition.
     * @return the tag definition required.
     */
    TagDefinition getTagDefinition(Identifier reference);

    /**
     * Returns the type specified by the reference.
     *
     * @param reference
     *            The reference to the type.
     * @return the type required.
     */
    Type getType(Identifier reference);

    void lookup(Identifier id, Visitor visitor);

    Set<UmlModelElement> whereUsed(Identifier id);

    Set<UmlModelElement> lookupByName(String name);
}

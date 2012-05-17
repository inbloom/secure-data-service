package org.slc.sli.modeling.tools.xsd2xmi.core;

import org.slc.sli.modeling.uml.Identifier;
import org.slc.sli.modeling.uml.TagDefinition;
import org.slc.sli.modeling.uml.Type;

/**
 * Provides information required by an {@link Xsd2UmlPlugin}.
 */
public interface Xsd2UmlPluginHost extends Xsd2UmlPlugin {

    /**
     * Ensures that there is a tag definition for the specified name and obtains the identifier.
     *
     * @param name
     *            The name of the tag definition.
     * @return The identifier of the tag definition.
     */
    // FIXME: This should be a QName.
    Identifier ensureTagDefinitionId(final String name);

    /**
     * Returns the tag definition for the specified identifier.
     *
     * @param id
     *            The identifier of the tag definition.
     * @return The tag definition corresponding to the identifier.
     */
    TagDefinition getTagDefinition(Identifier id);

    /**
     * Returns the {@link Type} for the specified {@link Identifier}.
     *
     * @param typeId
     *            The identifier of the type.
     * @return The type corresponding to the identifier.
     */
    Type getType(Identifier typeId);
}
